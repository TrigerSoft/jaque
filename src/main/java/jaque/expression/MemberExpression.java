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

package jaque.expression;

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
