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
 * Represents the value of the containing instance (this)
 */
public final class ThisExpression extends Expression {

	private final Object _value;

	ThisExpression(Class<?> resultType, Object value) {
		super(ExpressionType.This, resultType);

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
		return (_value == null) ? 0 : _value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ThisExpression))
			return false;
		final ThisExpression other = (ThisExpression) obj;
		if (_value == null) {
			if (other._value != null)
				return false;
		} else if (!_value.equals(other._value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "this("+_value+")";
	}
}
