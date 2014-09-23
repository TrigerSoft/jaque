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

import java.util.List;

public final class InvocationExpression extends Expression {

	private final InvocableExpression _method;
	private final List<Expression> _arguments;

	InvocationExpression(InvocableExpression method, List<Expression> arguments) {
		super(ExpressionType.Invoke, method.getResultType());
		
		List<ParameterExpression> pp = method.getParameters();
		
		for (int i = 0; i < pp.size(); i++)
			if (!pp.get(i).getResultType().isAssignableFrom(arguments.get(i).getResultType()))
				throw new IllegalArgumentException(String.valueOf(i));

		_method = method;
		_arguments = arguments;
	}
	
	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	public InvocableExpression getMethod() {
		return _method;
	}

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
		b.append(_method.toString());
		b.append('(');
		List<ParameterExpression> parameters = _method.getParameters();
		for	(int i = 0; i < parameters.size(); i++) {
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
