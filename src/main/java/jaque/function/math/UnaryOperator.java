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

package jaque.function.math;

import java.math.BigDecimal;
import java.math.BigInteger;

public enum UnaryOperator {
	/**
	 * |value| operator. 
	 */
	Abs {
		@Override
		public Number eval(Number value) {
			if (value instanceof Byte)
				return (byte) Math.abs(value.byteValue());
			if (value instanceof Double)
				return (double) Math.abs(value.doubleValue());
			if (value instanceof Float)
				return (float) Math.abs(value.floatValue());
			if (value instanceof Integer)
				return (int) Math.abs(value.intValue());
			if (value instanceof Long)
				return (long) Math.abs(value.longValue());
			if (value instanceof Short)
				return (short) Math.abs(value.shortValue());
			if (value instanceof BigInteger)
				return ((BigInteger) value).abs();
			if (value instanceof BigDecimal)
				return ((BigDecimal) value).abs();

			throw new ArithmeticException(value.getClass().toString());
		}
	},
	/**
	 * -value operator. 
	 */
	Negate {
		@Override
		public Number eval(Number value) {
			if (value instanceof Byte)
				return -value.byteValue();
			if (value instanceof Double)
				return -value.doubleValue();
			if (value instanceof Float)
				return -value.floatValue();
			if (value instanceof Integer)
				return -value.intValue();
			if (value instanceof Long)
				return -value.longValue();
			if (value instanceof Short)
				return -value.shortValue();
			if (value instanceof BigInteger)
				return ((BigInteger) value).negate();
			if (value instanceof BigDecimal)
				return ((BigDecimal) value).negate();

			throw new ArithmeticException(value.getClass().toString());
		}
	},
	/**
	 * ~value operator. 
	 */
	Not {
		@Override
		public Number eval(Number value) {
			if (value instanceof Byte)
				return (byte) (~value.byteValue());
			if (value instanceof Integer)
				return (int) (~value.intValue());
			if (value instanceof Long)
				return (long) (~value.longValue());
			if (value instanceof Short)
				return (short) (~value.shortValue());
			if (value instanceof BigInteger)
				return ((BigInteger) value).not();

			throw new ArithmeticException(value.getClass().toString());
		}
	};
	public abstract Number eval(Number value);
}
