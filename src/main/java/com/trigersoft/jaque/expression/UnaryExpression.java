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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents an expression that has a unary operator.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

@EqualsAndHashCode(callSuper = true)
@Getter
public class UnaryExpression extends Expression {

	private final Expression first;

	UnaryExpression(int expressionType, Class<?> resultType, @NonNull Expression operand) {
		super(expressionType, resultType);

		this.first = operand;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
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
