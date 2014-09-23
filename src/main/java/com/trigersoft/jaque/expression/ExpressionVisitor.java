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

/**
 * Represents an expression visitor interface.
 * 
 * @author <a href="mailto://object_streaming@googlegroups.com">Konstantin
 *         Triger</a>
 */
public interface ExpressionVisitor<T> {
	T visit(BinaryExpression e);
	T visit(ConstantExpression e);
	T visit(InvocationExpression e);
	T visit(LambdaExpression<?> e);
	T visit(MemberExpression e);
	T visit(ParameterExpression e);
	T visit(UnaryExpression e);
}
