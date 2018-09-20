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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents accessing a field or method.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

@EqualsAndHashCode(callSuper = true)
@Getter
public final class MemberExpression extends InvocableExpression {

	private final Expression instance;
	private final Member member;

	MemberExpression(int expressionType, Expression instance, Member member, Class<?> resultType, List<ParameterExpression> params) {
		super(expressionType, resultType, params);

		if (member instanceof AccessibleObject) {
			AccessibleObject ao = (AccessibleObject) member;
			try {
				if (!ao.isAccessible())
					ao.setAccessible(true);
			} catch (Exception e) {
				// suppress
			}
		}

		this.instance = instance;
		this.member = member;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public String toString() {
		Member m = getMember();
		String me = getInstance() != null ? getInstance().toString() : m.getDeclaringClass().getSimpleName();
		return me + "." + (m instanceof Constructor<?> ? "<new>" : m.getName());
	}
}
