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
 * Provides mathematical unary operations implementations.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

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

	/**
	 * Evaluates the operator.
	 * 
	 * @param value
	 *            operand.
	 * @return operation result.
	 */
	public abstract Number eval(Number value);
}
