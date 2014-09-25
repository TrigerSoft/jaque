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

package com.trigersoft.jaque.function.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Provides mathematical binary operations implementations.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

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
	 * binary &amp; operator.
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
				return (byte) Math.pow(left.doubleValue(), right.doubleValue());
			if (left instanceof Double)
				return Math.pow(left.doubleValue(), right.doubleValue());
			;
			if (left instanceof Float)
				return (float) Math
						.pow(left.doubleValue(), right.doubleValue());
			if (left instanceof Integer)
				return (int) Math.pow(left.doubleValue(), right.doubleValue());
			;
			if (left instanceof Long)
				return (long) Math.pow(left.doubleValue(), right.doubleValue());
			;
			if (left instanceof Short)
				return (short) Math
						.pow(left.doubleValue(), right.doubleValue());
			;
			if (left instanceof BigInteger)
				return ((BigInteger) left).pow(right.intValue());

			throw new ArithmeticException(left.getClass().toString());
		}
	},
	/**
	 * binary &lt;&lt; operator ({@code left &lt;&lt; right}).
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
	 * binary &gt;&gt; operator ({@code left &gt;&gt; right}).
	 */
	ShiftRight {
		@Override
		public Number eval(Number left, Number right) {
			if (left instanceof Byte)
				return (Byte) left >> right.intValue();
			if (left instanceof Integer)
				return (Integer) left >> right.intValue();
			if (left instanceof Long)
				return (Long) left >> right.intValue();
			if (left instanceof Short)
				return (Short) left >> right.intValue();
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

	/**
	 * Evaluates the operator.
	 * 
	 * @param left
	 *            operand.
	 * @param right
	 *            operand.
	 * @return operation result.
	 */
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
