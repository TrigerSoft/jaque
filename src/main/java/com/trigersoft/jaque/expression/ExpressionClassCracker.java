package com.trigersoft.jaque.expression;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.ClassReader;

final class ExpressionClassCracker {

	private static final URLClassLoader lambdaLoader;

	static {
		String folder = System.getProperty("jdk.internal.lambda.dumpProxyClasses");

		if (folder == null) {
			lambdaLoader = null;
		}
		else {
			try {
				URL[] urls = { new File(folder).toURI().toURL() };
				lambdaLoader = new URLClassLoader(urls);
			}
			catch (MalformedURLException mue) {
				throw new RuntimeException(mue);
			}
		}
	}

	LambdaExpression<?> lambda(Object functional) {

		if (lambdaLoader == null) throw new IllegalStateException("Cannot load Byte Code for lambda. Ensure that 'jdk.internal.lambda.dumpProxyClasses' system setting is properly set.");

		Class<?> functionalClass = functional.getClass();

		if (!functionalClass.isSynthetic()) throw new UnsupportedOperationException("The requested object is not a Java Lambda");

		Method method = null;
		for (Method m : functionalClass.getMethods()) {
			if (!m.isDefault()) {
				method = m;
				break;
			}
		}

		String name = functionalClass.getName();

		InputStream s = lambdaLoader.getResourceAsStream(name.substring(0, name.lastIndexOf('/')).replace('.', '/') + ".class");

		ExpressionClassVisitor visitor;
		try {
			visitor = parse(s, functional, method);
		}
		finally {
			try {
				s.close();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		Expression res = visitor.getResult();
		while (res.getExpressionType() == ExpressionType.Convert)
			res = ((UnaryExpression) res).getFirst();
		InvocationExpression target = (InvocationExpression) res;

		Method actual = (Method) ((MemberExpression) target.getTarget()).getMember();

		ParameterExpression[] outerParams = visitor.getParams();
		Class<?> outerType = visitor.getType();

		if (!actual.isSynthetic()) {

			return Expression.lambda(outerType, target, Collections.unmodifiableList(Arrays.asList(outerParams)));
		}

		// TODO: in fact must recursively parse all the synthetic methods,
		// so must have a relevant visitor. and then another visitor to reduce
		// forwarded calls

		//		method = actual.getName();
		//		methodDesc = Type.getMethodDescriptor(actual);

		Class<?> actualClass = actual.getDeclaringClass();
		String classPath = actualClass.getName().replace('.', '/') + ".class";

		s = actualClass.getClassLoader().getResourceAsStream(classPath);

		try {
			visitor = parse(s, functional, actual);
		}
		finally {
			try {
				s.close();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		Expression result = TypeConverter.convert(visitor.getResult(), visitor.getType());
		ParameterExpression[] params = visitor.getParams();

		// try reduce
		List<Expression> ntArgs = target.getArguments();
		// 1. there must be enough params
		if (ntArgs.size() <= params.length) {
			boolean canReduce = true;

			// 2. newTarget must have all args as PE
			for (Expression e : ntArgs)
				if (e.getExpressionType() != ExpressionType.Parameter) {
					canReduce = false;
					break;
				}

			if (canReduce) {
				ParameterExpression[] newInnerParams = new ParameterExpression[params.length];
				for (int i = 0; i < params.length; i++)
					newInnerParams[i] = (ParameterExpression) ntArgs.get(params[i].getIndex());

				result = TypeConverter.convert(result, outerType);

				LambdaExpression<?> lambda = Expression.lambda(outerType, result, Collections.unmodifiableList(Arrays.asList(newInnerParams)));
				return lambda;
			}
		}

		LambdaExpression<?> inner = Expression.lambda(visitor.getType(), result, Collections.unmodifiableList(Arrays.asList(params)));

		InvocationExpression newTarget = Expression.invoke(inner, target.getArguments());

		LambdaExpression<?> lambda = Expression.lambda(outerType, newTarget, Collections.unmodifiableList(Arrays.asList(outerParams)));

		return lambda;
	}

	private ExpressionClassVisitor parse(InputStream s, Object lambda, Method method) {
		ExpressionClassVisitor visitor = new ExpressionClassVisitor(lambda, method);
		try {
			try {
				ClassReader reader = new ClassReader(s);
				reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
				return visitor;
			}
			finally {
				s.close();
			}
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

}
