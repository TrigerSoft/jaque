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

/**
 * Represents an expression that has a constant value.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public final class ConstantExpression extends Expression {

	private final Object value;

	ConstantExpression(Class<?> resultType, Object value) {
		super(ExpressionType.Constant, resultType);

		this.value = value;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public String toString() {
		Object value = getValue();
		if (value != null)
			return value.toString();

		return '[' + getResultType().getSimpleName() + ']';
	}
}
