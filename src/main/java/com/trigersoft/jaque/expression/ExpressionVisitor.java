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

/**
 * Represents an expression visitor interface.
 * 
 * @param <T>
 *            type the visitor methods return after processing.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */
public interface ExpressionVisitor<T> {
	/**
	 * Visits the {@link BinaryExpression}.
	 * 
	 * @param e
	 *            {@link BinaryExpression} to visit.
	 * @return T
	 */
	T visit(BinaryExpression e);

	/**
	 * Visits the {@link ConstantExpression}.
	 * 
	 * @param e
	 *            {@link ConstantExpression} to visit.
	 * @return T
	 */
	T visit(ConstantExpression e);

	/**
	 * Visits the {@link InvocationExpression}.
	 * 
	 * @param e
	 *            {@link InvocationExpression} to visit.
	 * @return T
	 */
	T visit(InvocationExpression e);

	/**
	 * Visits the {@link LambdaExpression}.
	 * 
	 * @param e
	 *            {@link LambdaExpression} to visit.
	 * @return T
	 */
	T visit(LambdaExpression<?> e);

	/**
	 * Visits the {@link MemberExpression}.
	 * 
	 * @param e
	 *            {@link MemberExpression} to visit.
	 * @return T
	 */
	T visit(MemberExpression e);

	/**
	 * Visits the {@link ParameterExpression}.
	 * 
	 * @param e
	 *            {@link ParameterExpression} to visit.
	 * @return T
	 */
	T visit(ParameterExpression e);

	/**
	 * Visits the {@link UnaryExpression}.
	 * 
	 * @param e
	 *            {@link UnaryExpression} to visit.
	 * @return T
	 */
	T visit(UnaryExpression e);
}
