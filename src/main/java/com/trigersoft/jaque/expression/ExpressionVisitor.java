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
