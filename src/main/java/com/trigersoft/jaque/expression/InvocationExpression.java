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

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents an expression that applies a delegate or lambda expression to a list of argument expressions.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

@EqualsAndHashCode(callSuper = true)
@Getter
public final class InvocationExpression extends Expression {

	private final InvocableExpression target;
	private final List<Expression> arguments;

	InvocationExpression(InvocableExpression method, List<Expression> arguments) {
		super(ExpressionType.Invoke, method.getResultType());

		List<ParameterExpression> pp = method.getParameters();

		for (int i = 0; i < pp.size(); i++) {
			Class<?> resultType = arguments.get(i).getResultType();
			if (resultType == Object.class)
				continue; // if there is accessor method, the cast might be there
			Class<?> paramType = pp.get(i).getResultType();
			if (!TypeConverter.isAssignable(paramType, resultType)) {
				if (paramType.isInterface() && resultType == LambdaExpression.class)
					continue; // special case
				throw new IllegalArgumentException(String.valueOf(i));
			}
		}

		this.target = method;
		this.arguments = arguments;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		InvocableExpression normalized = getTarget();
		b.append(normalized.toString());
		if (normalized.getExpressionType() != ExpressionType.FieldAccess) {
			b.append('(');
			List<ParameterExpression> parameters = normalized.getParameters();
			for (int i = 0; i < parameters.size(); i++) {
				if (i > 0) {
					b.append(',');
					b.append(' ');
				}
				b.append(arguments.get(parameters.get(i).getIndex()).toString());
			}
			b.append(')');
		}
		return b.toString();
	}
}
