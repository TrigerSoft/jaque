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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Represents a visitor or rewriter for expression trees.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

final class ExpressionClassVisitor extends ClassVisitor {

	private final ConstantExpression _me;
	private final String _method;
	private final String _methodDesc;

	private Expression _result;
	private Class<?> _type;
	private Class<?>[] _argTypes;
	private Type _objectType;

	Expression getResult() {
		return _result;
	}

	void setResult(Expression result) {
		_result = result;
	}

	Class<?> getType() {
		return _type;
	}

	ParameterExpression[] getParams() {
		ParameterExpression[] params = new ParameterExpression[_argTypes.length];
		for (int i = 0; i < params.length; i++)
			params[i] = Expression.parameter(_argTypes[i], i);
		return params;
	}

	public ExpressionClassVisitor(Object lambda, String method,
			String methodDescriptor) {
		super(Opcodes.ASM5);
		_me = Expression.constant(lambda, lambda.getClass());
		_method = method;
		_methodDesc = methodDescriptor;
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

		if (_objectType != null && (access & Opcodes.ACC_STATIC) == 0) {
			try {
				Class<?> implClass = getClass(_objectType);
				_result = Expression.invoke(Expression.parameter(implClass, 0),
						name, argTypes);

				_argTypes = new Class<?>[argTypes.length + 1];
				_argTypes[0] = implClass;
				System.arraycopy(argTypes, 0, _argTypes, 1, argTypes.length);

				return null;
			} catch (Throwable e) {
				// fallback;
			}
		}

		_argTypes = argTypes;

		return new ExpressionMethodVisitor(this,
				(access & Opcodes.ACC_STATIC) == 0 ? _me : null, argTypes);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {

		// potentially a method reference - store object type
		if ((access & Opcodes.ACC_SYNTHETIC) == 0)
			_objectType = Type.getObjectType(name);
		super.visit(version, access, name, signature, superName, interfaces);
	}
}
