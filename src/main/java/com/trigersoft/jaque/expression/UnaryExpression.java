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
 * Represents an expression that has a unary operator.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public class UnaryExpression extends Expression {

	private final Expression _operand;

	UnaryExpression(int expressionType, Class<?> resultType, Expression operand) {
		super(expressionType, resultType);

		if (operand == null)
			throw new NullPointerException("operand");

		_operand = operand;
	}

	/**
	 * Gets the (first) operand of the unary operation.
	 * 
	 * @return An {@link Expression} that represents the (first) operand of the
	 *         unary operation.
	 */
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
