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

import java.lang.ref.WeakReference;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * Describes a lambda expression. This captures a block of code that is similar
 * to a method body. Please note that this is not an {@link Expression} itself. 
 * <p>
 * Use {@link #parse(Object)} method to get a lambda expression tree.
 * </p>
 * 
 * @param <F>
 *            type of the lambda represented by this LambdaExpression.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public final class LambdaExpression<F>  {

	private final Expression _body;
	private final List<Class<?>> _paramTypes;
	private final Class<?> resultType;

	private static final Map<Class<?>, WeakReference<LambdaExpression<?>>> _cache = Collections
			.synchronizedMap(new WeakHashMap<Class<?>, WeakReference<LambdaExpression<?>>>());

	LambdaExpression(Class<?> resultType, Expression body,
			List<Class<?>> paramTypes) {

		this.resultType = resultType;
		_paramTypes = paramTypes;
		_body = body;
	}

	/**
	 * Gets the body of the lambda expression.
	 * 
	 * @return {@link Expression}
	 */
	public Expression getBody() {
		return _body;
	}

	/**
	 * Creates {@link LambdaExpression} representing the lambda expression tree.
	 * 
	 * @param <T>
	 *            the type of lambda to parse
	 * 
	 * @param lambda
	 *            - the lambda
	 * 
	 * @return {@link LambdaExpression} representing the lambda expression tree.
	 */
	@SuppressWarnings("unchecked")
	public static <T> LambdaExpression<T> parse(T lambda) {

		LambdaExpression<T> lambdaE;
		WeakReference<LambdaExpression<?>> wlambda = _cache.get(lambda
				.getClass());
		if (wlambda != null) {
			lambdaE = (LambdaExpression<T>) wlambda.get();
			if (lambdaE != null)
				return (LambdaExpression<T>) Expression.lambda(lambdaE.getResultType(),
						lambdaE.getBody().accept(new InstanceReplacer(lambda)), lambdaE.getParamTypes());
		}

		ExpressionClassCracker cracker = new ExpressionClassCracker();
		lambdaE = (LambdaExpression<T>) cracker.lambda(lambda);

		_cache.put(lambda.getClass(), new WeakReference<LambdaExpression<?>>(
				lambdaE));

		return lambdaE;
	}

	/**
	 * Produces a {@link Function} that represents the lambda expression.
	 * 
	 * @return {@link Function} that represents the lambda expression.
	 */
	public Function<Object[], ?> compile() {
		final Function<Object[], ?> f = _body.accept(Interpreter.Instance);
		return f;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_body,resultType,_paramTypes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof LambdaExpression))
			return false;
		final LambdaExpression<?> other = (LambdaExpression<?>) obj;
		return 
				Objects.equals(_body,other._body)&&
				Objects.equals(resultType,other.resultType)&&
				Objects.equals(_paramTypes,other._paramTypes);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('{');
		b.append('(');
		if (_paramTypes.size() > 0) {
			for (int i = 0; i < _paramTypes.size(); i++) {
				if (i > 0) {
					b.append(',');
					b.append(' ');
				}
				b.append(_paramTypes.get(i).getName());
				b.append(" P");
				b.append(i);
			}
		}
		b.append(')');
		b.append(" -> ");
		b.append(getBody().toString());
		b.append('}');
		return b.toString();
	}

	public List<Class<?>> getParamTypes() {
		return _paramTypes;
	}

	public Class<?> getResultType() {
		return resultType;
	}

	private static class InstanceReplacer extends SimpleExpressionVisitor{

		private Object newThis;
		public InstanceReplacer( Object newThis) {
			this.newThis = newThis;
		}
		@Override
		public Expression visit(ThisExpression e) {
			return Expression.this_(newThis, e.getResultType());
		}
		
	}

}
