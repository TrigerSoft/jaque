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
 * Represents an expression that has a binary operator.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */
public final class BinaryExpression extends UnaryExpression {

	private final Expression _operator;
	private final Expression _second;

	BinaryExpression(int expressionType, Class<?> resultType,
			Expression operator, Expression first, Expression second) {
		super(expressionType, resultType, first);

		if (expressionType == ExpressionType.Conditional)
			if (operator == null)
				throw new IllegalArgumentException(new NullPointerException(
						"operator"));

		if (second == null)
			throw new NullPointerException("second");
		_operator = operator;
		_second = second;
	}

	/**
	 * Gets the operator of the binary operation.
	 * 
	 * @return An Expression that represents the operator of the binary
	 *         operation.
	 */
	public Expression getOperator() {
		return _operator;
	}

	/**
	 * Gets the second operand of the binary operation.
	 * 
	 * @return An Expression that represents the second operand of the binary
	 *         operation.
	 */
	public Expression getSecond() {
		return _second;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_second == null) ? 0 : _second.hashCode());
		result = prime * result
				+ ((_operator == null) ? 0 : _operator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof BinaryExpression))
			return false;
		final BinaryExpression other = (BinaryExpression) obj;
		if (_second == null) {
			if (other._second != null)
				return false;
		} else if (!_second.equals(other._second))
			return false;
		if (_operator == null) {
			if (other._operator != null)
				return false;
		} else if (!_operator.equals(other._operator))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('(');
		if (getOperator() != null) {
			b.append(getOperator().toString());
			b.append(' ');
			b.append(ExpressionType.toString(getExpressionType()));
			b.append(' ');

			b.append(getFirst().toString());
			b.append(' ');

			b.append(':');
		} else {
			b.append(getFirst().toString());
			b.append(' ');
			b.append(ExpressionType.toString(getExpressionType()));
		}
		b.append(' ');
		b.append(getSecond().toString());
		b.append(')');
		return b.toString();
	}
}
