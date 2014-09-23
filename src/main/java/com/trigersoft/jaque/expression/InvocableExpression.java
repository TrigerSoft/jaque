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

public abstract class InvocableExpression extends Expression {

	private final List<ParameterExpression> _params;

	protected InvocableExpression(int expressionType,
			Class<?> resultType, List<ParameterExpression> params) {
		super(expressionType, resultType);
		
		if (params == null)
			throw new NullPointerException("params");

		_params = params;
	}

	public final List<ParameterExpression> getParameters() {
		return _params;
	}
}
