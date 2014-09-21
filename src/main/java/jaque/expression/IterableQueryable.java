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