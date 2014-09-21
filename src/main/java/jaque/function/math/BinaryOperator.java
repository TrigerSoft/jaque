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

public enum BinaryOperator {
	/**
	 * binary + operator.
	 */
	Add {
		@Override
		public Number eval(Number left, Number right) {
			if (left == null)
				return right;
			if (left instanceof Byte)
				return (Byte) left + right.byteValue();
			if (left instanceof Double)
				return (Double) left + right.doubleValue();
			if (left instanceof Float)
				return (Float) left + right.floatValue();
			if (left instanceof Integer)
				return (Integer) left + right.intValue();
			if (left instanceof Long)
				return (Long) left + right.longValue();
			if (left instanceof Short)
				return (Short) left + right.shortValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).add(getBigInteger(right));
			if (left instanceof BigDecimal)
				return ((BigDecimal) left).add(getBigDecimal(right));

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary & operator.
	 */
	And {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (Byte) left & right.byteValue();
			if (left instanceof Integer)
				return (Integer) left & right.intValue();
			if (left instanceof Long)
				return (Long) left & right.longValue();
			if (left instanceof Short)
				return (Short) left & right.shortValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).and(getBigInteger(right));

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary - operator.
	 */
	Subtract {
		@Override
		public Number eval(Number left, Number right) {
			if (left == null)
				return UnaryOperator.Negate.eval(right);
			if (left instanceof Byte)
				return (Byte) left - right.byteValue();
			if (left instanceof Double)
				return (Double) left - right.doubleValue();
			if (left instanceof Float)
				return (Float) left - right.floatValue();
			if (left instanceof Integer)
				return (Integer) left - right.intValue();
			if (left instanceof Long)
				return (Long) left - right.longValue();
			if (left instanceof Short)
				return (Short) left - right.shortValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).subtract(getBigInteger(right));
			if (left instanceof BigDecimal)
				return ((BigDecimal) left).subtract(getBigDecimal(right));

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary * operator.
	 */
	Multiply {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (Byte) left * right.byteValue();
			if (left instanceof Double)
				return (Double) left * right.doubleValue();
			if (left instanceof Float)
				return (Float) left * right.floatValue();
			if (left instanceof Integer)
				return (Integer) left * right.intValue();
			if (left instanceof Long)
				return (Long) left * right.longValue();
			if (left instanceof Short)
				return (Short) left * right.shortValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).multiply(getBigInteger(right));
			if (left instanceof BigDecimal)
				return ((BigDecimal) left).multiply(getBigDecimal(right));

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary / operator.
	 */
	Divide {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (double) (Byte) left / right.byteValue();
			if (left instanceof Double)
				return (Double) left / right.doubleValue();
			if (left instanceof Float)
				return (Float) left / right.floatValue();
			if (left instanceof Integer)
				return (double) (Integer) left / right.intValue();
			if (left instanceof Long)
				return (double) (Long) left / right.longValue();
			if (left instanceof Short)
				return (double) (Short) left / right.shortValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).divide(getBigInteger(right));
			if (left instanceof BigDecimal)
				return ((BigDecimal) left).divide(getBigDecimal(right));

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary % operator.
	 */
	Modulo {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (Byte) left % right.byteValue();
			if (left instanceof Double)
				return (Double) left % right.doubleValue();
			if (left instanceof Float)
				return (Float) left % right.floatValue();
			if (left instanceof Integer)
				return (Integer) left % right.intValue();
			if (left instanceof Long)
				return (Long) left % right.longValue();
			if (left instanceof Short)
				return (Short) left % right.shortValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).mod(getBigInteger(right));

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary | operator.
	 */
	Or {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (Byte) left | right.byteValue();
			if (left instanceof Integer)
				return (Integer) left | right.intValue();
			if (left instanceof Long)
				return (Long) left | right.longValue();
			if (left instanceof Short)
				return (Short) left | right.shortValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).or(getBigInteger(right));

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary exponent operator (<tt>left<sup>right</sup></tt>).
	 */
	Power {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (byte) Math.pow(left.doubleValue(), right
						.doubleValue());
			if (left instanceof Double)
				return Math.pow(left.doubleValue(), right.doubleValue());
			;
			if (left instanceof Float)
				return (float) Math.pow(left.doubleValue(), right
						.doubleValue());
			if (left instanceof Integer)
				return (int) Math.pow(left.doubleValue(), right
						.doubleValue());
			;
			if (left instanceof Long)
				return (long) Math.pow(left.doubleValue(), right
						.doubleValue());
			;
			if (left instanceof Short)
				return (short) Math.pow(left.doubleValue(), right
						.doubleValue());
			;
			if (left instanceof BigInteger)
				return ((BigInteger) left).pow(right.intValue());

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary << operator ({@code left << right}).
	 */
	ShiftLeft {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (Byte) left << right.intValue();
			if (left instanceof Integer)
				return (Integer) left << right.intValue();
			if (left instanceof Long)
				return (Long) left << right.intValue();
			if (left instanceof Short)
				return (Short) left << right.intValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).shiftLeft(right.intValue());

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary >>> operator ({@code left >>> right}).
	 */
	ShiftRight {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (Byte) left >>> right.intValue();
			if (left instanceof Integer)
				return (Integer) left >>> right.intValue();
			if (left instanceof Long)
				return (Long) left >>> right.intValue();
			if (left instanceof Short)
				return (Short) left >>> right.intValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).shiftRight(right.intValue());

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary ^ operator.
	 */
	Xor {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (Byte) left ^ right.byteValue();
			if (left instanceof Integer)
				return (Integer) left ^ right.intValue();
			if (left instanceof Long)
				return (Long) left ^ right.longValue();
			if (left instanceof Short)
				return (Short) left ^ right.shortValue();
			if (left instanceof BigInteger)
				return ((BigInteger) left).xor(getBigInteger(right));

			throw new ArithmeticException(left.getClass().toString());
		}
	};

	public abstract Number eval(Number left, Number right);

	private static BigInteger getBigInteger(Number n) {
		if (n instanceof BigInteger)
			return (BigInteger) n;

		return BigInteger.valueOf(n.longValue());
	}

	private static BigDecimal getBigDecimal(Number n) {
		if (n instanceof BigDecimal)
			return (BigDecimal) n;

		return BigDecimal.valueOf(n.doubleValue());
	}
}
