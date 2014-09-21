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

import java.util.Iterator;
import jaque.*;

public abstract class SimpleQueryable<E> implements Queryable<E>,
		QueryableFactory {

	private final Class<E> _elementType;
	private final Expression _e;

	protected SimpleQueryable(Class<E> elementType, Expression e) {
		_elementType = elementType;
		_e = e != null ? e : Expression.constant(this);
	}

	public Class<E> getElementType() {
		return _elementType;
	}

	public Expression getExpression() {
		return _e;
	}

	public QueryableFactory getFactory() {
		return this;
	}

	public Iterator<E> iterator() {
		return iterable().iterator();
	}
}
