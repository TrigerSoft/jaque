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

/**
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin
 *         Triger</a>
 */

final class TypeConverter extends SimpleExpressionVisitor {
	private final Class<?> _to;

	private TypeConverter(Class<?> to) {
		_to = to;
	}

	static Expression convert(Expression e, Class<?> to) {
		Class<?> from = e.getResultType();
		if (from == to)
			return e;

		return e.accept(new TypeConverter(to));
	}

	private Object convert(Class<?> from, Object value) {

		if (from == Integer.TYPE)
			return convert((int) (Integer) value);

		return defaultConvert(value);
	}

	private Object convert(int value) {
		if (_to == Boolean.TYPE) {

			if (value == 0)
				return Boolean.FALSE;

			if (value == 1)
				return Boolean.TRUE;
		}

		return defaultConvert(value);
	}

	private Expression defaultConvert(Expression value) {
		return (Expression) defaultConvert((Object) value);
	}

	private Object defaultConvert(Object value) {
		return _to.cast(value);
	}

	@Override
	public Expression visit(BinaryExpression e) {
		Expression first = e.getFirst().accept(this);
		Expression second = e.getSecond().accept(this);
		Expression op = e.getOperator();

		return Expression.condition(op, first, second);
	}

	@Override
	public Expression visit(ConstantExpression e) {
		return Expression.constant(convert(e.getResultType(), e.getValue()),
				_to);
	}

	@Override
	public Expression visit(InvocationExpression e) {
		Expression expr = e.getTarget().accept(this);
		if (expr != e.getTarget())
			return Expression.invoke((InvocableExpression) expr, e
					.getArguments());

		return e;
	}

	@Override
	public Expression visit(LambdaExpression<?> e) {
		return defaultConvert(e);
	}

	@Override
	public Expression visit(MemberExpression e) {
		if (_to.isAssignableFrom(e.getResultType()))
			return e;

		if (e.getResultType().isAssignableFrom(_to))
			return Expression.member(e.getExpressionType(), e.getInstance(), e
					.getMember(), _to, e.getParameters());

		return defaultConvert(e);
	}

	@Override
	public Expression visit(ParameterExpression e) {
		if (e.getResultType().isAssignableFrom(_to))
			return Expression.parameter(_to, e.getIndex());
		return defaultConvert(e);
	}

	@Override
	public Expression visit(UnaryExpression e) {
		return defaultConvert(e);
	}
}
