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

public final class ParameterExpression extends Expression {

	private final int _index;

	ParameterExpression(Class<?> resultType, int index) {
		super(ExpressionType.Parameter, resultType);
		
		if (index < 0)
			throw new IndexOutOfBoundsException("index");

		_index = index;
	}

	public int getIndex() {
		return _index;
	}
	
	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + _index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ParameterExpression))
			return false;
		final ParameterExpression other = (ParameterExpression) obj;
		if (_index != other._index)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "P" + getIndex();
	}
}
