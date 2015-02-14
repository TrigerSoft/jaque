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

/**
 * Represents an expression that applies a delegate or lambda expression to a
 * list of argument expressions.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public final class InvocationExpression extends Expression {

	private final InvocableExpression _method;
	private final List<Expression> _arguments;

	InvocationExpression(InvocableExpression method, List<Expression> arguments) {
		super(ExpressionType.Invoke, method.getResultType());

		List<ParameterExpression> pp = method.getParameters();

		for (int i = 0; i < pp.size(); i++)
			if (!TypeConverter.isAssignable(pp.get(i).getResultType(),
					arguments.get(i).getResultType()))
				throw new IllegalArgumentException(String.valueOf(i));

		_method = method;
		_arguments = arguments;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	/**
	 * Get the {@link InvocableExpression} to be called.
	 * 
	 * @return {@link InvocableExpression} to be called.
	 */
	public InvocableExpression getTarget() {
		return _method;
	}

	/**
	 * Gets a collection of expressions that represent arguments of the called
	 * expression.
	 * 
	 * @return Arguments of the called expression.
	 */
	public List<Expression> getArguments() {
		return _arguments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((_arguments == null) ? 0 : _arguments.hashCode());
		result = prime * result + ((_method == null) ? 0 : _method.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof InvocationExpression))
			return false;
		final InvocationExpression other = (InvocationExpression) obj;
		if (_arguments == null) {
			if (other._arguments != null)
				return false;
		} else if (!_arguments.equals(other._arguments))
			return false;
		if (_method == null) {
			if (other._method != null)
				return false;
		} else if (!_method.equals(other._method))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		InvocableExpression normalized = InstanceAdaptor.normalize(_method,
				_arguments);
		b.append(normalized.toString());
		b.append('(');
		List<ParameterExpression> parameters = normalized.getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			if (i > 0) {
				b.append(',');
				b.append(' ');
			}
			b.append(_arguments.get(parameters.get(i).getIndex()).toString());
		}
		b.append(')');
		return b.toString();
	}
}
