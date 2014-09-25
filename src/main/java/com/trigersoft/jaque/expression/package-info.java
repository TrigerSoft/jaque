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

/**
 * Contains classes, interfaces and enumerations that enable language-level code expressions to be represented as objects in the form of <em>expression trees</em>.
 * 
 * <p>Use {@link com.trigersoft.jaque.expression.LambdaExpression#parse(Object)} to get a lambda expression tree.</p>
 * <p>The abstract class Expression provides the root of a class hierarchy used to model expression trees.</p>
 * <p>The classes in this package that derive from Expression, for example MemberExpression and ParameterExpression, are used to represent nodes in an expression tree.
 * The Expression class contains static factory methods to create expression tree nodes of the various types.</p>
 * <p>The enumeration type ExpressionType specifies the unique node types.</p>
 */
package com.trigersoft.jaque.expression;