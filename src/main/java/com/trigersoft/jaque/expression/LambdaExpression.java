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

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;

public final class LambdaExpression<F> extends InvocableExpression {

	private final Expression _body;

	private static final Map<Class<?>, WeakReference<LambdaExpression<?>>> _cache = Collections
			.synchronizedMap(new WeakHashMap<Class<?>, WeakReference<LambdaExpression<?>>>());

	LambdaExpression(Class<?> resultType, Expression body,
			List<ParameterExpression> params) {
		super(ExpressionType.Lambda, resultType, params);

		if (body == null)
			throw new NullPointerException("body");

		_body = body;
	}

	public Expression getBody() {
		return _body;
	}

	@SuppressWarnings("unchecked")
	public static <T> LambdaExpression<T> parse(T lambda) {

		LambdaExpression<T> lambdaE;
		WeakReference<LambdaExpression<?>> wlambda = _cache.get(lambda
				.getClass());
		if (wlambda != null) {
			lambdaE = (LambdaExpression<T>) wlambda.get();
			if (lambdaE != null)
				return (LambdaExpression<T>) lambdaE
						.apply(new InstanceReplacer(lambda));
		}

		ExpressionClassVisitor visitor = new ExpressionClassVisitor();
		lambdaE = (LambdaExpression<T>) visitor.lambda(lambda);

		_cache.put(lambda.getClass(), new WeakReference<LambdaExpression<?>>(
				lambdaE));

		return lambdaE;
	}

	public Function<Object[], ?> compile() {
		final Function<Object[], ?> f = apply(Interpreter.Instance);
		return f;
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_body == null) ? 0 : _body.hashCode());
		return result;
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
		if (_body == null) {
			if (other._body != null)
				return false;
		} else if (!_body.equals(other._body))
			return false;
		return true;
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

	private static final class InstanceReplacer extends SimpleExpressionVisitor {
		private final Object _lambda;

		public InstanceReplacer(Object lambda) {
			_lambda = lambda;
		}

		@Override
		public Expression visit(ConstantExpression e) {
			if (e.getResultType() == _lambda.getClass())
				return Expression.constant(_lambda);

			return super.visit(e);
		}
	}
}
