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

package jaque.expression;

/**
 * Represents an expression that has a constant value.
 * 
 * @author <a href="mailto://object_streaming@googlegroups.com">Konstantin
 *         Triger</a>
 */
public final class ConstantExpression extends Expression {

	private final Object _value;

	ConstantExpression(Class<?> resultType, Object value) {
		super(ExpressionType.Constant, resultType);

		_value = value;
	}

	/**
	 * Gets the value of the constant expression.
	 * 
	 * @return An Object equal to the value of the represented expression.
	 */
	public Object getValue() {
		return _value;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_value == null) ? 0 : _value.hashCode());
		return (_value == null) ? 0 : _value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ConstantExpression))
			return false;
		final ConstantExpression other = (ConstantExpression) obj;
		if (_value == null) {
			if (other._value != null)
				return false;
		} else if (!_value.equals(other._value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		Object value = getValue();
		if (value != null)
			return value.toString();

		return '[' + getResultType().getName() + ']';
	}
}
