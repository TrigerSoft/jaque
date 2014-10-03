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

package com.trigersoft.jaque;


public class Customer implements Comparable<Customer> {
	private final int _x;

	@Override
	public int compareTo(Customer o) {
		return _x - o._x;
	}

	public Customer(int x) {
		_x = x;
	}

	public int getData() {
		// throw new IllegalArgumentException("h");
		return _x;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Integer.toString(_x);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _x;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Customer))
			return false;
		final Customer other = (Customer) obj;
		if (_x != other._x)
			return false;
		return true;
	}
}

class Customer1<T> extends Customer {
	T data;

	public Customer1(T d, int x) {
		super(x);
		data = d;
	}
}

final class Join<T, R> {
	private final T _outer;
	private final R _inner;

	public Join(T outer, R inner) {
		_outer = outer;
		_inner = inner;
	}

	public T getOuter() {
		return _outer;
	}

	public R getInner() {
		return _inner;
	}

	@Override
	public String toString() {
		return _outer.toString() + ":" + _inner.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_inner == null) ? 0 : _inner.hashCode());
		result = prime * result + ((_outer == null) ? 0 : _outer.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Join))
			return false;
		final Join other = (Join) obj;
		if (_inner == null) {
			if (other._inner != null)
				return false;
		} else if (!_inner.equals(other._inner))
			return false;

		if (_outer == null)
			return other._outer == null;

		return _outer.equals(other._outer);
	}
}
