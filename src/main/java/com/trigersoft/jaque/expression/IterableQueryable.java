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
/*
import jaque.Queryable;

public final class IterableQueryable<E> extends SimpleQueryable<E> {
	public IterableQueryable(Class<E> elementType, Iterable<E> source) {
		this(elementType, Expression.constant(source));
	}

	private IterableQueryable(Class<E> elementType, Expression e) {
		super(elementType, e);
	}

	@SuppressWarnings("unchecked")
	public Iterable<E> iterable() {
		return (Iterable<E>) execute();
	}

	@SuppressWarnings("unchecked")
	public E single() {
		return (E) execute();
	}

	private Object execute() {
		try {
			return getExpression().apply(Interpreter.Instance).invoke(null);
		} catch (RuntimeException t) {
			throw t;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public <T> Queryable<T> createQueryable(Class<T> type, Expression e) {
		return new IterableQueryable<T>(type, e);
	}
}
*/