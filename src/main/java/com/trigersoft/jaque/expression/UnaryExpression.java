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

public class UnaryExpression extends Expression {

	private final Expression _operand;

	public UnaryExpression(int expressionType, Class<?> resultType,
			Expression operand) {
		super(expressionType, resultType);

		if (operand == null)
			throw new NullPointerException("operand");

		_operand = operand;
	}

	public final Expression getFirst() {
		return _operand;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((_operand == null) ? 0 : _operand.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final UnaryExpression other = (UnaryExpression) obj;
		if (_operand == null) {
			if (other._operand != null)
				return false;
		} else if (!_operand.equals(other._operand))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		if (getExpressionType() == ExpressionType.Convert) {
			b.append('(');
			b.append(getResultType().getName());
			b.append(')');
		} else
			b.append(ExpressionType.toString(getExpressionType()));
		b.append(getFirst().toString());

		return b.toString();
	}
}
