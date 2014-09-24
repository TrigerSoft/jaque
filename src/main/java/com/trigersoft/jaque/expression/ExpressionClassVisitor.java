/*
 * Copyright TrigerSoft <kostat@trigersoft.com> 
 * 
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

import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.objectweb.asm.*;

final class ExpressionClassVisitor extends ClassVisitor {

	private static final URLClassLoader lambdaLoader;
	private ConstantExpression _me;
	private String _method;
	private String _methodDesc;
	private Expression _result;
	private Class<?> _type;
	private Class<?>[] _argTypes;

	static {
		String folder = System
				.getProperty("jdk.internal.lambda.dumpProxyClasses");

		if (folder == null) {
			lambdaLoader = null;
		} else {
			try {
				URL[] urls = { new File(folder).toURI().toURL() };
				lambdaLoader = new URLClassLoader(urls);
			} catch (MalformedURLException mue) {
				throw new RuntimeException(mue);
			}
		}
	}

	Expression getResult() {
		return _result;
	}

	void setResult(Expression result) {
		_result = result;
	}

	LambdaExpression<?> lambda(Object functional) {

		if (lambdaLoader == null)
			throw new IllegalStateException(
					"Cannot load Byte Code for lambda. Ensure that 'jdk.internal.lambda.dumpProxyClasses' system setting is properly set.");

		Class<?> functionalClass = functional.getClass();
		
		if (!functionalClass.isSynthetic())
			throw new UnsupportedOperationException("The requested object is not a Java Lambda");

		for (Method m : functionalClass.getMethods()) {
			if (!m.isDefault()) {
				_method = m.getName();
				_methodDesc = Type.getMethodDescriptor(m);
				break;
			}
		}

		_me = Expression.constant(functional, functionalClass);

		String name = functionalClass.getName();

		InputStream s = lambdaLoader.getResourceAsStream(name.substring(0,
				name.lastIndexOf('/')).replace('.', '/')
				+ ".class");

		parse(s);

		InvocationExpression target = (InvocationExpression) getResult();
		ParameterExpression[] outerParams = getParams();
		Class<?> outerType = _type;

		Method actual = (Method) ((MemberExpression) target.getMethod())
				.getMember();
		_method = actual.getName();
		_methodDesc = Type.getMethodDescriptor(actual);

		Class<?> actualClass = actual.getDeclaringClass();
		String classPath = actualClass.getName().replace('.', '/') + ".class";

		s = actualClass.getClassLoader().getResourceAsStream(classPath);

		parse(s);

		Expression result = TypeConverter.convert(getResult(), _type);
		ParameterExpression[] params = getParams();

		LambdaExpression<?> inner = Expression.lambda(_type, result,
				Collections.unmodifiableList(Arrays.asList(params)));

		InvocationExpression newTarget = Expression.invoke(inner,
				target.getArguments());

		LambdaExpression<?> lambda = Expression.lambda(outerType, newTarget,
				Collections.unmodifiableList(Arrays.asList(outerParams)));
		return lambda;
	}

	private ParameterExpression[] getParams() {
		ParameterExpression[] params = new ParameterExpression[_argTypes.length];
		for (int i = 0; i < params.length; i++)
			params[i] = Expression.parameter(_argTypes[i], i);
		return params;
	}

	private void parse(InputStream s) {
		try {
			try {
				ClassReader reader = new ClassReader(s);
				reader.accept(this, ClassReader.SKIP_DEBUG
						| ClassReader.SKIP_FRAMES);
			} finally {
				s.close();
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public ExpressionClassVisitor() {
		super(Opcodes.ASM5);
	}

	Class<?> getClass(Type t) {
		try {
			switch (t.getSort()) {
			case Type.BOOLEAN:
				return Boolean.TYPE;
			case Type.CHAR:
				return Character.TYPE;
			case Type.BYTE:
				return Byte.TYPE;
			case Type.SHORT:
				return Short.TYPE;
			case Type.INT:
				return Integer.TYPE;
			case Type.FLOAT:
				return Float.TYPE;
			case Type.LONG:
				return Long.TYPE;
			case Type.DOUBLE:
				return Double.TYPE;
			case Type.VOID:
				return Void.TYPE;
			}
			String cn = t.getInternalName();
			cn = cn != null ? cn.replace('/', '.') : t.getClassName();

			return Class.forName(cn, false, _me.getResultType()
					.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		// if ((access & Opcodes.ACC_SYNTHETIC) != 0)
		// return null;

		if (!_method.equals(name) || !_methodDesc.equals(desc))
			return null;

		Type ret = Type.getReturnType(desc);
		if (ret.getSort() == Type.VOID)
			throw ExpressionMethodVisitor.notLambda(Opcodes.RETURN);

		_type = getClass(ret);

		Type[] args = Type.getArgumentTypes(desc);
		Class<?>[] argTypes = new Class<?>[args.length];

		for (int i = 0; i < args.length; i++)
			argTypes[i] = getClass(args[i]);

		_argTypes = argTypes;

		return new ExpressionMethodVisitor(this,
				(access & Opcodes.ACC_STATIC) == 0 ? _me : null, argTypes);
	}

	@Override
	public void visit(int arg0, int arg1, String arg2, String arg3,
			String arg4, String[] arg5) {
	}

	@Override
	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		return null;
	}

	@Override
	public void visitAttribute(Attribute arg0) {
	}

	@Override
	public void visitEnd() {
	}

	@Override
	public FieldVisitor visitField(int arg0, String arg1, String arg2,
			String arg3, Object arg4) {
		return null;
	}

	@Override
	public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
	}

	@Override
	public void visitOuterClass(String arg0, String arg1, String arg2) {
	}

	@Override
	public void visitSource(String arg0, String arg1) {
	}

}
