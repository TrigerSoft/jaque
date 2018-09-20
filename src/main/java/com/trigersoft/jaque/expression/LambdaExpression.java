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

import java.util.List;
import java.util.function.Function;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * Describes a lambda expression. This captures a block of code that is similar to a method body.
 * <p>
 * Use {@link #parse(Object)} method to get a lambda expression tree.
 * </p>
 * 
 * @param <F>
 *            type of the lambda represented by this LambdaExpression.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

@EqualsAndHashCode(callSuper = true)
@Getter
public final class LambdaExpression<F> extends InvocableExpression {

	private final Expression body;

	// private static final Map<Class<?>, WeakReference<LambdaExpression<?>>> _cache = Collections
	// .synchronizedMap(new WeakHashMap<Class<?>, WeakReference<LambdaExpression<?>>>());

	LambdaExpression(Class<?> resultType, @NonNull Expression body, List<ParameterExpression> params) {
		super(ExpressionType.Lambda, resultType, params);

		this.body = body;
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
		// WeakReference<LambdaExpression<?>> wlambda = _cache.get(lambda.getClass());
		// if (wlambda != null) {
		// lambdaE = (LambdaExpression<T>) wlambda.get();
		// if (lambdaE != null)
		// return (LambdaExpression<T>) lambdaE.accept(new InstanceReplacer(lambda));
		// }

		lambdaE = (LambdaExpression<T>) ExpressionClassCracker.get().lambda(lambda);

		// _cache.put(lambda.getClass(), new WeakReference<LambdaExpression<?>>(lambdaE));

		return lambdaE;
	}

	/**
	 * Produces a {@link Function} that represents the lambda expression.
	 * 
	 * @return {@link Function} that represents the lambda expression.
	 */
	public Function<Object[], ?> compile() {
		final Function<Object[], ?> f = accept(Interpreter.Instance);
		return f;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('{');
		List<ParameterExpression> arguments = getParameters();
		if (arguments.size() > 0) {
			b.append('(');
			for (int i = 0; i < arguments.size(); i++) {
				if (i > 0) {
					b.append(',');
					b.append(' ');
				}
				ParameterExpression pe = arguments.get(i);
				b.append(pe.getResultType().getName());
				b.append(' ');
				b.append(pe.toString());
			}
			b.append(')');
		}
		b.append(" -> ");
		b.append(getBody().toString());
		b.append('}');
		return b.toString();
	}

	// private static final class InstanceReplacer extends SimpleExpressionVisitor {
	// private final Object _lambda;
	//
	// public InstanceReplacer(Object lambda) {
	// _lambda = lambda;
	// }
	//
	// @Override
	// public Expression visit(ConstantExpression e) {
	// if (e.getResultType() == _lambda.getClass())
	// return Expression.constant(_lambda);
	//
	// return super.visit(e);
	// }
	// }
}
