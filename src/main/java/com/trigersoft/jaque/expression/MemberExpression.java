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

import java.lang.reflect.*;
import java.util.List;

public final class MemberExpression extends InvocableExpression {

	private final Expression _instance;
	private final Member _member;

	MemberExpression(int expressionType, Expression instance, Member member,
			Class<?> resultType, List<ParameterExpression> params) {
		super(expressionType, resultType, params);
		
		if (member instanceof AccessibleObject) {
			AccessibleObject ao = (AccessibleObject)member;
			if (!ao.isAccessible())
				ao.setAccessible(true);
		}

		_instance = instance;
		_member = member;
	}

	public final Member getMember() {
		return _member;
	}

	public final Expression getInstance() {
		return _instance;
	}
	
	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public String toString() {
		Member m = getMember();
		String me = getInstance() != null ? getInstance().toString() : m
				.getDeclaringClass().getName();
		return me + "." + m.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((_instance == null) ? 0 : _instance.hashCode());
		result = prime * result + ((_member == null) ? 0 : _member.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof MemberExpression))
			return false;
		final MemberExpression other = (MemberExpression) obj;
		if (_instance == null) {
			if (other._instance != null)
				return false;
		} else if (!_instance.equals(other._instance))
			return false;
		if (_member == null) {
			if (other._member != null)
				return false;
		} else if (!_member.equals(other._member))
			return false;
		return true;
	}
}
