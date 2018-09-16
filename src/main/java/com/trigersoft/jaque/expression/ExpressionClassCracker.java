/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.trigersoft.jaque.expression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

class ExpressionClassCracker {

	private static final String DUMP_FOLDER_SYSTEM_PROPERTY = "jdk.internal.lambda.dumpProxyClasses";
	private static final URLClassLoader lambdaClassLoader;
	private static final String lambdaClassLoaderCreationError;

	private static ExpressionClassCracker instance = new ExpressionClassCracker();

	public static ExpressionClassCracker get() {
		return instance;
	}

	static {
		String folderPath = System.getProperty(DUMP_FOLDER_SYSTEM_PROPERTY);
		if (folderPath == null) {
			lambdaClassLoaderCreationError = "Ensure that the '" + DUMP_FOLDER_SYSTEM_PROPERTY + "' system property is properly set.";
			lambdaClassLoader = null;
		} else {
			File folder = new File(folderPath);
			if (!folder.isDirectory()) {
				lambdaClassLoaderCreationError = "Ensure that the '" + DUMP_FOLDER_SYSTEM_PROPERTY + "' system property is properly set (" + folderPath
						+ " does not exist).";
				lambdaClassLoader = null;
			} else {
				URL folderURL;
				try {
					folderURL = folder.toURI().toURL();
				} catch (MalformedURLException mue) {
					throw new RuntimeException(mue);
				}

				lambdaClassLoaderCreationError = null;
				lambdaClassLoader = new URLClassLoader(new URL[] { folderURL });
			}
		}
	}

	private ExpressionClassCracker() {
	}

	private static final class ParameterReplacer extends SimpleExpressionVisitor {
		private int paramIndex;
		private final LambdaExpression<?> target;

		public ParameterReplacer(LambdaExpression<?> target, int paramIndex) {
			this.target = target;
			this.paramIndex = paramIndex;
		}

		@Override
		public Expression visit(InvocationExpression e) {
			Expression expr = e.getTarget();
			List<Expression> originals = e.getArguments();
			List<Expression> args = visitExpressionList(originals);
			if (args != originals) {

				for (int i = 0; i < args.size(); i++) {
					if (args.get(i) != originals.get(i)) {
						int currIndex = paramIndex;
						try {
							paramIndex = i;
							// TODO: in theory there might be many duplicate params...
							expr = expr.accept(this);
							break;
						} finally {
							paramIndex = currIndex;
						}
					}
				}

				return Expression.invoke((InvocableExpression) expr, args);
			}
			return e;
		}

		@Override
		public Expression visit(ParameterExpression e) {
			return e.getIndex() == paramIndex ? Expression.parameter(LambdaExpression.class, paramIndex) : super.visit(e);
		}

		@Override
		public Expression visit(MemberExpression e) {
			Expression instance = e.getInstance();
			if (instance.getExpressionType() == ExpressionType.Parameter && ((ParameterExpression) instance).getIndex() == paramIndex)
				return target;
			return super.visit(e);
		}

	}

	LambdaExpression<?> lambda(Object lambda) {
		Class<?> lambdaClass = lambda.getClass();
		if (!lambdaClass.isSynthetic())
			throw new IllegalArgumentException("The requested object is not a Java lambda");

		if (lambda instanceof Serializable) {
			SerializedLambda extracted = SerializedLambda.extractLambda((Serializable) lambda);

			ClassLoader lambdaClassLoader = lambdaClass.getClassLoader();
			return lambda(extracted, lambdaClassLoader);

		}

		ExpressionClassVisitor lambdaVisitor = parseFromFileSystem(lambda, lambdaClass);

		Expression lambdaExpression = lambdaVisitor.getResult();
		Class<?> lambdaType = lambdaVisitor.getType();
		ParameterExpression[] lambdaParams = lambdaVisitor.getParams();

		InvocationExpression target = (InvocationExpression) stripConvertExpressions(lambdaExpression);
		Method actualMethod = (Method) ((MemberExpression) target.getTarget()).getMember();

		// short-circuit method references
		if (!actualMethod.isSynthetic()) {
			return Expression.lambda(lambdaType, target, Collections.unmodifiableList(Arrays.asList(lambdaParams)));
		}

		// TODO: in fact must recursively parse all the synthetic methods,
		// so must have a relevant visitor. and then another visitor to
		// reduce forwarded calls

		Class<?> actualClass = actualMethod.getDeclaringClass();
		ClassLoader actualClassLoader = actualClass.getClassLoader();
		String actualClassPath = classFilePath(actualClass.getName());
		ExpressionClassVisitor actualVisitor = parseClass(actualClassLoader, actualClassPath, () -> Expression.constant(lambda), actualMethod);

		Expression actualExpression = TypeConverter.convert(actualVisitor.getResult(), actualVisitor.getType());
		ParameterExpression[] actualParams = actualVisitor.getParams();

		return buildExpression(lambdaType, lambdaParams, target, actualVisitor, actualExpression, actualParams);
	}

	LambdaExpression<?> lambda(SerializedLambda extracted, ClassLoader lambdaClassLoader) {
		boolean hasCapturedArgs = extracted.capturedArgs != null && extracted.capturedArgs.length > 0;
		boolean hasThis[] = hasCapturedArgs ? new boolean[1] : null;
		ExpressionClassVisitor actualVisitor = parseClass(lambdaClassLoader, classFilePath(extracted.implClass), hasCapturedArgs ? () -> {
			hasThis[0] = true;
			Object instance = extracted.capturedArgs[0];
			return Expression.constant(instance);
		} : null, extracted.implMethodName, extracted.implMethodSignature);

		Expression reducedExpression = TypeConverter.convert(actualVisitor.getResult(), actualVisitor.getType());

		ParameterExpression[] params = actualVisitor.getParams();

		LambdaExpression<?> extractedLambda = Expression.lambda(actualVisitor.getType(), reducedExpression,
				Collections.unmodifiableList(Arrays.asList(params)));

		if (!hasCapturedArgs)
			return extractedLambda;

		List<Expression> args = new ArrayList<>(params.length);

		int capturedLength = extracted.capturedArgs.length;
		for (int i = hasThis != null && hasThis[0] ? 1 : 0; i < capturedLength; i++) {
			Object arg = extracted.capturedArgs[i];
			if (arg instanceof SerializedLambda) {
				SerializedLambda argLambda = (SerializedLambda) arg;

				LambdaExpression<?> argExtractedLambda = lambda(argLambda, lambdaClassLoader);

				extractedLambda = (LambdaExpression<?>) extractedLambda.accept(new ParameterReplacer(argExtractedLambda, args.size()));

				arg = argExtractedLambda;
			}
			args.add(Expression.constant(arg));
		}

		List<ParameterExpression> finalParams = new ArrayList<>(params.length - capturedLength);
		int boundArgs = args.size();
		for (int y = boundArgs; y < params.length; y++) {
			ParameterExpression param = params[y];
			ParameterExpression arg = Expression.parameter(param.getResultType(), y - boundArgs);
			args.add(arg);
			finalParams.add(arg);
		}

		InvocationExpression newTarget = Expression.invoke(extractedLambda, args);

		return Expression.lambda(actualVisitor.getType(), newTarget, Collections.unmodifiableList(finalParams));
	}

	private ExpressionClassVisitor parseFromFileSystem(Object lambda, Class<?> lambdaClass) {
		if (lambdaClassLoader == null)
			throw new RuntimeException(lambdaClassLoaderCreationError);

		String lambdaClassPath = lambdaClassFilePath(lambdaClass);
		Method lambdaMethod = findFunctionalMethod(lambdaClass);
		return parseClass(lambdaClassLoader, lambdaClassPath, () -> Expression.constant(lambda), lambdaMethod);
	}

	private String lambdaClassFilePath(Class<?> lambdaClass) {
		String lambdaClassName = lambdaClass.getName();
		String className = lambdaClassName.substring(0, lambdaClassName.lastIndexOf('/'));
		return classFilePath(className);
	}

	private String classFilePath(String className) {
		return className.replace('.', '/') + ".class";
	}

	private Method findFunctionalMethod(Class<?> functionalClass) {
		for (Method m : functionalClass.getMethods()) {
			if (!m.isDefault()) {
				return m;
			}
		}
		throw new IllegalArgumentException("Not a lambda expression. No non-default method.");
	}

	private ExpressionClassVisitor parseClass(ClassLoader classLoader, String classFilePath, Supplier<ConstantExpression> instance, Method method) {
		return parseClass(classLoader, classFilePath, instance, method.getName(), Type.getMethodDescriptor(method));
	}

	private ExpressionClassVisitor parseClass(ClassLoader classLoader, String classFilePath, Supplier<ConstantExpression> instance, String method,
			String methodDescriptor) {
		ExpressionClassVisitor visitor = new ExpressionClassVisitor(classLoader, instance, method, methodDescriptor);
		try {
			try (InputStream classStream = getResourceAsStream(classLoader, classFilePath)) {
				ClassReader reader = new ClassReader(classStream);
				reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
				return visitor;
			}
		} catch (IOException e) {
			throw new RuntimeException("error parsing class file " + classFilePath, e);
		}
	}

	private InputStream getResourceAsStream(ClassLoader classLoader, String path) throws FileNotFoundException {
		InputStream stream = classLoader.getResourceAsStream(path);
		if (stream == null)
			throw new FileNotFoundException(path);
		return stream;
	}

	private Expression stripConvertExpressions(Expression expression) {
		while (expression.getExpressionType() == ExpressionType.Convert) {
			expression = ((UnaryExpression) expression).getFirst();
		}
		return expression;
	}

	private LambdaExpression<?> buildExpression(Class<?> lambdaType, ParameterExpression[] lambdaParams, InvocationExpression target,
			ExpressionClassVisitor actualVisitor, Expression actualExpression, ParameterExpression[] actualParams) {
		// try reduce
		List<Expression> ntArgs = target.getArguments();
		// 1. there must be enough params
		if (ntArgs.size() <= actualParams.length) {
			// 2. newTarget must have all args as PE
			if (allArgumentsAreParameters(ntArgs)) {
				List<ParameterExpression> newInnerParams = new ArrayList<>();

				for (ParameterExpression actualParam : actualParams) {
					ParameterExpression newInnerParam = (ParameterExpression) ntArgs.get(actualParam.getIndex());
					newInnerParams.add(newInnerParam);
				}

				Expression reducedExpression = TypeConverter.convert(actualExpression, lambdaType);

				return Expression.lambda(lambdaType, reducedExpression, Collections.unmodifiableList(newInnerParams));
			}
		}

		LambdaExpression<?> inner = Expression.lambda(actualVisitor.getType(), actualExpression, Collections.unmodifiableList(Arrays.asList(actualParams)));

		InvocationExpression newTarget = Expression.invoke(inner, target.getArguments());

		return Expression.lambda(lambdaType, newTarget, Collections.unmodifiableList(Arrays.asList(lambdaParams)));
	}

	private boolean allArgumentsAreParameters(List<Expression> ntArgs) {
		for (Expression e : ntArgs) {
			if (e.getExpressionType() != ExpressionType.Parameter)
				return false;
		}
		return true;
	}

}
