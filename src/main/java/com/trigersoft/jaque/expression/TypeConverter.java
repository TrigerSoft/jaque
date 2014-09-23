/*
 * Copyright Konstantin Triger <kostat@gmail.com> 
 * 
 * This file is part of Jaque - JAva QUEry library <http://code.google.com/p/jaque/>.
 * 
 * Jaque is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaque is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.trigersoft.jaque.expression;

final class TypeConverter extends SimpleExpressionVisitor {
	private final Class<?> _to;

	private TypeConverter(Class<?> to) {
		_to = to;
	}

	static Expression convert(Expression e, Class<?> to) {
		Class<?> from = e.getResultType();
		if (from == to)
			return e;

		return e.apply(new TypeConverter(to));
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
		Expression first = e.getFirst().apply(this);
		Expression second = e.getSecond().apply(this);
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
		Expression expr = e.getMethod().apply(this);
		if (expr != e.getMethod())
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
