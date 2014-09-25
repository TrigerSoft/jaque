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
 * Represents an indexed parameter expression.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public final class ParameterExpression extends Expression {

	private final int _index;

	ParameterExpression(Class<?> resultType, int index) {
		super(ExpressionType.Parameter, resultType);

		if (index < 0)
			throw new IndexOutOfBoundsException("index");

		_index = index;
	}

	/**
	 * Gets the index of the parameter or variable.
	 * 
	 * @return index of the parameter or variable.
	 */
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
