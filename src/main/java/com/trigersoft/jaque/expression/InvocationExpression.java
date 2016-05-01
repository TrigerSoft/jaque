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
 * Provides the base class from which the expression that represent invocable
 * operations are derived.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public abstract class InvocationExpression extends Expression {

	private final List<Class<?>> _paramTypes;
	private final List<Expression> _arguments;
	private final Expression _instance;

	protected InvocationExpression(int expressionType, Expression _instance, Class<?> resultType, 
			List<Class<?>> paramTypes, List<Expression> arguments) {
		super(expressionType, resultType);
		this._instance = _instance;
		_paramTypes = paramTypes;
		_arguments = arguments;
		

		if (paramTypes.size()!=arguments.size()){
			throw new IllegalArgumentException("Number of parameter does not match the number of arguments");
		}

		for (int i = 0; i < paramTypes.size(); i++)
			if (!TypeConverter.isAssignable(paramTypes.get(i),
					arguments.get(i).getResultType()))
				throw new IllegalArgumentException(String.valueOf(i));
	}

	/**
	 * Gets the parameters of this invocable expression.
	 * 
	 * @return parameters of the this invocable expression.
	 */
	public final List<Class<?>> getParameterTypes() {
		return _paramTypes;
	}

	public List<Expression> getArguments() {
		return _arguments;
	}
	/**
	 * Gets the containing object of the {@link #getMember()}.
	 * 
	 * @return containing object of the {@link #getMember()}.
	 */
	public final Expression getInstance() {
		return _instance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_arguments == null) ? 0 : _arguments.hashCode());
		result = prime * result + ((_instance == null) ? 0 : _instance.hashCode());
		result = prime * result + ((_paramTypes == null) ? 0 : _paramTypes.hashCode());
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
		InvocationExpression other = (InvocationExpression) obj;
		if (_arguments == null) {
			if (other._arguments != null)
				return false;
		} else if (!_arguments.equals(other._arguments))
			return false;
		if (_instance == null) {
			if (other._instance != null)
				return false;
		} else if (!_instance.equals(other._instance))
			return false;
		if (_paramTypes == null) {
			if (other._paramTypes != null)
				return false;
		} else if (!_paramTypes.equals(other._paramTypes))
			return false;
		return true;
	}

	
}
