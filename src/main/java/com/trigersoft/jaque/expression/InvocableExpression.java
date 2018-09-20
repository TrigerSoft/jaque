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
 * Provides the base class from which the expression that represent invocable operations are derived.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

@EqualsAndHashCode(callSuper = true)
@Getter
public abstract class InvocableExpression extends Expression {

	private final List<ParameterExpression> parameters;

	protected InvocableExpression(int expressionType, Class<?> resultType, List<ParameterExpression> params) {
		super(expressionType, resultType);

		if (params == null)
			throw new NullPointerException("params");

		this.parameters = params;
	}
}
