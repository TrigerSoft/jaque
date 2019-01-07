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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * Provides the base class from which the classes that represent expression tree nodes are derived. It also contains
 * static factory methods to create the various node types.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */
@EqualsAndHashCode
@Getter
public abstract class Expression {
	private final int expressionType;
	private final Class<?> resultType;

	static private final HashMap<Method, Class<?>> _boxers;
	static private final HashMap<Method, Class<?>> _unboxers;

	static {

		HashMap<Method, Class<?>> unboxers = new HashMap<Method, Class<?>>(8);
		try {
			unboxers.put(Boolean.class.getMethod("booleanValue"), Boolean.TYPE);
			unboxers.put(Byte.class.getMethod("byteValue"), Byte.TYPE);
			unboxers.put(Character.class.getMethod("charValue"), Character.TYPE);
			unboxers.put(Double.class.getMethod("doubleValue"), Double.TYPE);
			unboxers.put(Float.class.getMethod("floatValue"), Float.TYPE);
			unboxers.put(Integer.class.getMethod("intValue"), Integer.TYPE);
			unboxers.put(Long.class.getMethod("longValue"), Long.TYPE);
			unboxers.put(Short.class.getMethod("shortValue"), Short.TYPE);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		HashMap<Method, Class<?>> boxers = new HashMap<Method, Class<?>>(8);
		try {
			boxers.put(Boolean.class.getMethod("valueOf", Boolean.TYPE), Boolean.class);
			boxers.put(Byte.class.getMethod("valueOf", Byte.TYPE), Byte.class);
			boxers.put(Character.class.getMethod("valueOf", Character.TYPE), Character.class);
			boxers.put(Double.class.getMethod("valueOf", Double.TYPE), Double.class);
			boxers.put(Float.class.getMethod("valueOf", Float.TYPE), Float.class);
			boxers.put(Integer.class.getMethod("valueOf", Integer.TYPE), Integer.class);
			boxers.put(Long.class.getMethod("valueOf", Long.TYPE), Long.class);
			boxers.put(Short.class.getMethod("valueOf", Short.TYPE), Short.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		_unboxers = unboxers;
		_boxers = boxers;
	}

	private boolean isNumeric() {
		return isNumeric(getResultType());
	}

	private boolean isIntegral() {
		return isIntegral(getResultType());
	}

	private static boolean isNumeric(Class<?> type) {
		if (isIntegral(type))
			return true;

		if (type.isPrimitive())
			return type == Float.TYPE || type == Double.TYPE;

		return type == Float.class || type == Double.class || type == BigDecimal.class;
	}

	private static boolean isIntegral(Class<?> type) {
		if (!type.isPrimitive())
			return type == Byte.class || type == Integer.class || type == Long.class || type == Short.class || type == BigInteger.class;

		return type == Byte.TYPE || type == Integer.TYPE || type == Long.TYPE || type == Short.TYPE;
	}

	private boolean isBoolean() {
		return isBoolean(getResultType());
	}

	private static boolean isBoolean(Class<?> type) {
		return type == Boolean.TYPE || type == Boolean.class;
	}

	private static Expression stripQuotesAndConverts(Expression e) {
		while (e.getExpressionType() == ExpressionType.Convert)
			e = ((UnaryExpression) e).getFirst();

		return e;
	}

	/**
	 * Initializes a new instance of the {@link Expression} class.
	 * 
	 * @param expressionType
	 *            The {@link ExpressionType} to set as the node type.
	 * @param resultType
	 *            The {@link Class} to set as the type of the expression that this Expression represents.
	 */
	protected Expression(int expressionType, @NonNull Class<?> resultType) {

		this.expressionType = expressionType;
		this.resultType = resultType;
	}

	/**
	 * Creates a {@link BinaryExpression} that represents an arithmetic addition operation that does not have overflow
	 * checking.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to Add and the getFirst() and
	 *         getSecond() methods set to the specified values.
	 */
	public static BinaryExpression add(Expression first, Expression second) {
		return createNumeric(ExpressionType.Add, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents an arithmetic division operation.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to Divide and the getFirst()
	 *         and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression divide(Expression first, Expression second) {
		return createNumeric(ExpressionType.Divide, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents an arithmetic subtract operation.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to Subtract and the getFirst()
	 *         and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression subtract(Expression first, Expression second) {
		return createNumeric(ExpressionType.Subtract, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents an arithmetic multiply operation.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to Multiply and the getFirst()
	 *         and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression multiply(Expression first, Expression second) {
		return createNumeric(ExpressionType.Multiply, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents an arithmetic remainder operation.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to Modulo and the getFirst()
	 *         and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression modulo(Expression first, Expression second) {
		return createNumeric(ExpressionType.Modulo, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents a "greater than" numeric comparison.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to GreaterThan and the
	 *         getFirst() and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression greaterThan(Expression first, Expression second) {
		return createNumericComparison(ExpressionType.GreaterThan, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents a "greater than or equal" numeric comparison.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to GreaterThanOrEqual and the
	 *         getFirst() and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression greaterThanOrEqual(Expression first, Expression second) {
		return createNumericComparison(ExpressionType.GreaterThanOrEqual, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents a "less than" numeric comparison.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to LessThan and the getFirst()
	 *         and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression lessThan(Expression first, Expression second) {
		return createNumericComparison(ExpressionType.LessThan, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents a "less than or equal" numeric comparison.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to LessThanOrEqual and the
	 *         getFirst() and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression lessThanOrEqual(Expression first, Expression second) {
		return createNumericComparison(ExpressionType.LessThanOrEqual, first, second);
	}

	private static BinaryExpression createNumericComparison(int expressionType, Expression first, Expression second) {
		if (!first.isNumeric())
			throw new IllegalArgumentException(first.getResultType().toString());
		if (!second.isNumeric())
			throw new IllegalArgumentException(second.getResultType().toString());

		return new BinaryExpression(expressionType, Boolean.TYPE, null, first, second);
	}

	private static BinaryExpression createNumeric(int expressionType, Expression first, Expression second) {
		boolean fnumeric = first.isNumeric();
		boolean snumeric = second.isNumeric();
		if (!fnumeric || !snumeric) {
			if (!fnumeric && !snumeric)
				throw new IllegalArgumentException(
						"At least one argument must be numeric, got: " + first.getResultType().toString() + "," + second.getResultType().toString());
			if (!fnumeric)
				first = TypeConverter.convert(first, second.getResultType());
			else
				second = TypeConverter.convert(second, first.getResultType());
		}

		return new BinaryExpression(expressionType, first.getResultType(), null, first, second);
	}

	private static BinaryExpression createIntegral(int expressionType, Expression first, Expression second) {
		if (!first.isIntegral())
			throw new IllegalArgumentException(first.getResultType().toString());
		if (!second.isIntegral())
			throw new IllegalArgumentException(second.getResultType().toString());

		return new BinaryExpression(expressionType, first.getResultType(), null, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents an arithmetic left-shift operation.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to LeftShift and the getFirst()
	 *         and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression leftShift(Expression first, Expression second) {
		return createIntegral(ExpressionType.LeftShift, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents an arithmetic right-shift operation.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to RightShift and the
	 *         getFirst() and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression rightShift(Expression first, Expression second) {
		return createIntegral(ExpressionType.RightShift, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents a coalescing operation.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to Coalesce and the getFirst()
	 *         and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression coalesce(Expression first, Expression second) {
		if (first.getResultType().isPrimitive())
			throw new IllegalArgumentException(first.getResultType().toString());
		if (second.getResultType().isPrimitive())
			throw new IllegalArgumentException(second.getResultType().toString());
		return new BinaryExpression(ExpressionType.Coalesce, first.getResultType(), null, first, second);
	}

	/**
	 * Creates a {@link Expression} that represents an equality comparison. The expression will be simplified if one of
	 * parameters is constant {@link Boolean}.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} method equal to Equal and the getFirst() and
	 *         getSecond() methods set to the specified values, or one of the parameters if they one of them is constant
	 *         {@link Boolean}.
	 */
	public static Expression equal(Expression first, Expression second) {
		if (first.getResultType() != second.getResultType())
			throw new IllegalArgumentException(first.getResultType().toString() + " != " + second.getResultType().toString());
		return createBooleanExpression(ExpressionType.Equal, first, second);
	}

	/**
	 * Creates a {@link Expression} that represents an inequality comparison. The expression will be simplified if one of
	 * parameters is constant {@link Boolean}.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} method equal to Equal and the getFirst() and
	 *         getSecond() methods set to the specified values, or one of the parameters if they one of them is constant
	 *         {@link Boolean}.
	 */
	public static Expression notEqual(Expression first, Expression second) {
		if (first.getResultType() != second.getResultType())
			throw new IllegalArgumentException(first.getResultType().toString() + " != " + second.getResultType().toString());
		return createBooleanExpression(ExpressionType.NotEqual, first, second);
	}

	/**
	 * Creates a {@link Expression} that represents a conditional AND operation that evaluates the second operand only if it
	 * has to. The expression will be simplified if one of parameters is constant.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} method equal to LogicalAnd and the getFirst()
	 *         and getSecond() methods set to the specified values, or one of the parameters if they one of them is
	 *         constant.
	 */
	public static Expression logicalAnd(Expression first, Expression second) {
		if (!first.isBoolean())
			throw new IllegalArgumentException(first.getResultType().toString());
		if (!second.isBoolean())
			throw new IllegalArgumentException(second.getResultType().toString());
		return createBooleanExpression(ExpressionType.LogicalAnd, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents a bitwise AND operation.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} method equal to BitwiseAnd and the getFirst()
	 *         and getSecond() properties set to the specified values.
	 */
	public static BinaryExpression bitwiseAnd(Expression first, Expression second) {
		return createIntegral(ExpressionType.BitwiseAnd, first, second);
	}

	/**
	 * Creates a {@link Expression} that represents a conditional OR operation that evaluates the second operand only if it
	 * has to. The expression will be simplified if one of parameters is constant.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} method equal to LogicalOr and the getFirst()
	 *         and getSecond() methods set to the specified values, or one of the parameters if they one of them is
	 *         constant.
	 */
	public static Expression logicalOr(Expression first, Expression second) {
		if (!first.isBoolean())
			throw new IllegalArgumentException(first.getResultType().toString());
		if (!second.isBoolean())
			throw new IllegalArgumentException(second.getResultType().toString());
		return createBooleanExpression(ExpressionType.LogicalOr, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents a bitwise OR operation.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} method equal to BitwiseOr and the getFirst()
	 *         and getSecond() properties set to the specified values.
	 */
	public static BinaryExpression bitwiseOr(Expression first, Expression second) {
		return createIntegral(ExpressionType.BitwiseOr, first, second);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents a bitwise XOR operation, or {@link UnaryExpression} that
	 * represents a bitwise NOT in case the second parameter equals to -1.
	 * 
	 * @param first
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param second
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} method equal to ExclusiveOr and the getFirst()
	 *         and getSecond() properties set to the specified values, or {@link UnaryExpression} that represents a bitwise
	 *         NOT in case the second parameter equals to -1.
	 */
	public static Expression exclusiveOr(Expression first, Expression second) {
		if (second.getExpressionType() == ExpressionType.Constant) {
			ConstantExpression csecond = (ConstantExpression) second;
			if (isIntegral(csecond.getResultType())) {
				if (((Number) csecond.getValue()).intValue() == -1)
					return bitwiseNot(first);
			}
		}

		return createIntegral(ExpressionType.ExclusiveOr, first, second);
	}

	/**
	 * Creates a {@link UnaryExpression} that represents getting the length of an array.
	 * 
	 * @param array
	 *            An {@link Expression} to set the getFirst method equal to.
	 * @return A {@link UnaryExpression} that has the {@link ExpressionType} property equal to ArrayLength and the
	 *         getFirst() method set to array.
	 */
	public static UnaryExpression arrayLength(Expression array) {
		if (!array.getResultType().isArray())
			throw new IllegalArgumentException(array.getResultType().toString());

		return new UnaryExpression(ExpressionType.ArrayLength, Integer.TYPE, array);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents applying an array index operator to an array.
	 * 
	 * @param array
	 *            An Expression to set the getFirst method equal to.
	 * @param index
	 *            An Expression to set the getSecond method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} method equal to ArrayIndex and the getFirst()
	 *         and getSecond() methods set to the specified values.
	 */
	public static BinaryExpression arrayIndex(Expression array, Expression index) {
		Class<?> arrayType = array.getResultType();
		if (!arrayType.isArray())
			throw new IllegalArgumentException(arrayType.toString());

		if (index.getResultType() != Integer.TYPE)
			throw new IllegalArgumentException("index:" + index.getResultType().toString());

		return new BinaryExpression(ExpressionType.ArrayIndex, arrayType.getComponentType(), null, array, index);
	}

	/**
	 * Creates a {@link UnaryExpression} that represents a conversion operation, or 'e' if its ResultType equals to 'to'.
	 * 
	 * @param e
	 *            An Expression to set the getFirst() method equal to.
	 * @param to
	 *            The {@link Class} to set as the type of the expression that this Expression represents.
	 * @return A {@link UnaryExpression} that has the {@link ExpressionType} property equal to Convert, or 'e'.
	 */
	public static Expression convert(Expression e, Class<?> to) {
		if (e.getResultType() == to)
			return e;
		return new UnaryExpression(ExpressionType.Convert, to, e);
	}

	/**
	 * Creates a {@link ConstantExpression} that has the getValue() method set to the specified value and resultType is
	 * assignable from its type.
	 * 
	 * @param value
	 *            An Object to set the getValue() method equal to.
	 * @param resultType
	 *            The {@link Class} to set as the type of the expression that this Expression represents.
	 * @return A {@link ConstantExpression} that has the {@link ExpressionType} property equal to Constant and the
	 *         getValue() method set to the specified value.
	 */
	public static ConstantExpression constant(Object value, Class<?> resultType) {
		return new ConstantExpression(resultType, value);
	}

	/**
	 * Creates a {@link ConstantExpression} that has the getValue() method set to the specified value.
	 * 
	 * @param value
	 *            An Object to set the getValue() method equal to.
	 * @return A {@link ConstantExpression} that has the {@link ExpressionType} property equal to Constant and the
	 *         getValue() method set to the specified value.
	 */
	public static ConstantExpression constant(Object value) {
		Class<?> type = value == null ? Object.class : value.getClass();
		return constant(value, type);
	}

	/**
	 * Creates a {@link UnaryExpression} that represents an arithmetic negation operation.
	 * 
	 * @param e
	 *            An {@link Expression} to set the getValue() method equal to.
	 * @return A {@link UnaryExpression} that has the {@link ExpressionType} property equal to Negate and the getValue()
	 *         method set to the specified value.
	 */
	public static UnaryExpression negate(Expression e) {
		if (!e.isNumeric())
			throw new IllegalArgumentException(e.getResultType().toString());
		return new UnaryExpression(ExpressionType.Negate, e.getResultType(), e);
	}

	/**
	 * Creates a {@link ParameterExpression}.
	 * 
	 * @param resultType
	 *            The {@link Class} to set as the type of the expression that this Expression represents.
	 * @param index
	 *            Parameter index in the method signature.
	 * @return A {@link ParameterExpression} that has the getExpressionType() method equal to Parameter and the
	 *         getResultType() and getIndex() methods set to the specified values.
	 */
	public static ParameterExpression parameter(Class<?> resultType, int index) {
		return new ParameterExpression(resultType, index);
	}

	/**
	 * Creates a {@link BinaryExpression} that represents an instanceOf test.
	 * 
	 * @param e
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param type
	 *            The {@link Class} that assignability is tested with.
	 * @return A {@link BinaryExpression} that has the getExpressionType() equal to InstanceOf, the getFirst() set to 'e'
	 *         and getSecond() set to {@link ConstantExpression} with value equals to 'type'.
	 */
	public static BinaryExpression instanceOf(Expression e, Class<?> type) {
		return instanceOf(e, constant(type));
	}

	/**
	 * Creates a {@link BinaryExpression} that represents an instanceOf test.
	 * 
	 * @param e
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param type
	 *            The {@link Expression} that evaluates to Class assignability is tested with.
	 * @return A {@link BinaryExpression} that has the getExpressionType() equal to InstanceOf, the getFirst() set to 'e'
	 *         and getSecond() set to {@link ConstantExpression} with value equals to 'type'.
	 */
	public static BinaryExpression instanceOf(Expression e, Expression type) {
		return new BinaryExpression(ExpressionType.InstanceOf, Boolean.TYPE, null, e, type);
	}

	/**
	 * Creates a {@link Expression}, given an operand and unary operator, by calling the appropriate factory method.
	 * 
	 * @param expressionType
	 *            The {@link ExpressionType} that specifies the type of unary operation.
	 * @param resultType
	 *            The {@link Class} that specifies the type to be converted to (pass null if not applicable).
	 * @param operand
	 *            An {@link Expression} that represents the operand.
	 * @return The {@link Expression} that results from calling the appropriate factory method.
	 */
	public static Expression unary(int expressionType, Class<?> resultType, Expression operand) {
		switch (expressionType) {
		case ExpressionType.Convert:
			return convert(operand, resultType);
		case ExpressionType.ArrayLength:
			return arrayLength(operand);
		case ExpressionType.Negate:
			return negate(operand);
		case ExpressionType.BitwiseNot:
			return bitwiseNot(operand);
		case ExpressionType.LogicalNot:
			return logicalNot(operand);
		case ExpressionType.IsNull:
			return isNull(operand);
		case ExpressionType.IsNonNull:
			return isNonNull(operand);
		default:
			throw new IllegalArgumentException("expressionType");
		}
	}

	/**
	 * Creates a {@link Expression}, given an operand and binary operator, by calling the appropriate factory method.
	 * 
	 * @param expressionType
	 *            The {@link ExpressionType} that specifies the type of binary operation.
	 * @param first
	 *            An {@link Expression} that represents the left operand.
	 * @param second
	 *            An {@link Expression} that represents the right operand.
	 * @return The {@link Expression} that results from calling the appropriate factory method.
	 */
	public static Expression binary(int expressionType, Expression first, Expression second) {
		return binary(expressionType, null, first, second);
	}

	/**
	 * Creates a {@link Expression}, given an operand and binary operator, by calling the appropriate factory method.
	 * 
	 * @param expressionType
	 *            The {@link ExpressionType} that specifies the type of binary operation.
	 * @param operator
	 *            An {@link Expression} that represents the operator.
	 * @param first
	 *            An {@link Expression} that represents the left operand.
	 * @param second
	 *            An {@link Expression} that represents the right operand.
	 * @return The {@link Expression} that results from calling the appropriate factory method.
	 */
	public static Expression binary(int expressionType, Expression operator, Expression first, Expression second) {

		switch (expressionType) {
		case ExpressionType.Add:
			return add(first, second);
		case ExpressionType.BitwiseAnd:
			return bitwiseAnd(first, second);
		case ExpressionType.LogicalAnd:
			return logicalAnd(first, second);
		case ExpressionType.ArrayIndex:
			return arrayIndex(first, second);
		case ExpressionType.Coalesce:
			return coalesce(first, second);
		case ExpressionType.Conditional:
			return condition(operator, first, second);
		case ExpressionType.Divide:
			return divide(first, second);
		case ExpressionType.Equal:
			return equal(first, second);
		case ExpressionType.ExclusiveOr:
			return exclusiveOr(first, second);
		case ExpressionType.GreaterThan:
			return greaterThan(first, second);
		case ExpressionType.GreaterThanOrEqual:
			return greaterThanOrEqual(first, second);
		case ExpressionType.LeftShift:
			return leftShift(first, second);
		case ExpressionType.LessThan:
			return lessThan(first, second);
		case ExpressionType.LessThanOrEqual:
			return lessThanOrEqual(first, second);
		case ExpressionType.Modulo:
			return modulo(first, second);
		case ExpressionType.Multiply:
			return multiply(first, second);
		case ExpressionType.NotEqual:
			return notEqual(first, second);
		case ExpressionType.BitwiseOr:
			return bitwiseOr(first, second);
		case ExpressionType.LogicalOr:
			return logicalOr(first, second);
		case ExpressionType.RightShift:
			return rightShift(first, second);
		case ExpressionType.Subtract:
			return subtract(first, second);
		case ExpressionType.InstanceOf:
			return instanceOf(first, second);
		default:
			throw new IllegalArgumentException("expressionType");
		}
	}

	private static Expression createBooleanExpression(int expressionType, Expression first, Expression second) {

		Expression toReduce;
		Expression toLeave;

		if (first.getExpressionType() == ExpressionType.Constant) {
			toReduce = first;
			toLeave = second;
		} else if (second.getExpressionType() == ExpressionType.Constant) {
			toReduce = second;
			toLeave = first;
		} else {
			toReduce = null;
			toLeave = null;
		}

		if (toLeave != null && toLeave.isBoolean()) {
			toReduce = TypeConverter.convert(toReduce, Boolean.TYPE);
			switch (expressionType) {
			case ExpressionType.Equal:
				return (Boolean) ((ConstantExpression) toReduce).getValue() ? toLeave : logicalNot(toLeave);
			case ExpressionType.NotEqual:
				return (Boolean) ((ConstantExpression) toReduce).getValue() ? logicalNot(toLeave) : toLeave;
			case ExpressionType.LogicalAnd:
				return (Boolean) ((ConstantExpression) toReduce).getValue() ? toLeave : toReduce;
			case ExpressionType.LogicalOr:
				return (Boolean) ((ConstantExpression) toReduce).getValue() ? toReduce : toLeave;
			}
		}

		return new BinaryExpression(expressionType, Boolean.TYPE, null, first, second);
	}

	/**
	 * Creates a {@link LambdaExpression} as a method receiving the specified {@code arguments}, returning the
	 * {@code resultType} and having {@code body} for its implementation.
	 * 
	 * @param resultType
	 *            The method return value.
	 * @param body
	 *            The method implementation.
	 * @param parameters
	 *            The method parameters.
	 * @return A {@link LambdaExpression} as a method receiving the specified {@code arguments}, returning the
	 *         {@code resultType} and having {@code body} for its implementation.
	 */
	public static LambdaExpression<?> lambda(Class<?> resultType, Expression body, List<ParameterExpression> parameters) {
		return new LambdaExpression<Object>(resultType, body, parameters);
	}

	/**
	 * Creates a {@link DelegateExpression} as a method receiving the specified {@code arguments}, returning the
	 * {@code resultType} and having delegate to the implementation.
	 * 
	 * @param resultType
	 *            The method return value.
	 * @param delegate
	 *            The method implementation. The delegate resultType must be {@link InvocableExpression}.
	 * @param parameters
	 *            The method parameters.
	 * @return A {@link DelegateExpression} as a method receiving the specified {@code arguments}, returning the
	 *         {@code resultType} and having delegate to the implementation.
	 */
	public static DelegateExpression delegate(Class<?> resultType, Expression delegate, List<ParameterExpression> parameters) {
		return new DelegateExpression(resultType, delegate, parameters);
	}

	/**
	 * Creates a {@link MemberExpression} that represents accessing a static field given the name of the field.
	 * 
	 * @param type
	 *            The {@link Class} that specifies the type that contains the specified static field.
	 * @param name
	 *            The name of a field.
	 * @return A {@link InvocationExpression} that represents accessing a static field given the name of the field.
	 * @throws NoSuchFieldException
	 *             if a field with the specified name is not found.
	 */
	public static MemberExpression get(Class<?> type, String name) throws NoSuchFieldException {
		return get(null, type.getDeclaredField(name));
	}

	/**
	 * Creates a {@link MemberExpression} that represents accessing an instance field given the name of the field.
	 * 
	 * @param instance
	 *            An {@link Expression} whose {@code getResultType()} value will be searched for a specific field.
	 * @param name
	 *            The name of a field.
	 * @return A {@link InvocationExpression} that represents accessing an instance field given the name of the field.
	 * @throws NoSuchFieldException
	 *             if a field with the specified name is not found.
	 */
	public static MemberExpression get(Expression instance, String name) throws NoSuchFieldException {
		return get(instance, instance.getResultType().getDeclaredField(name));
	}

	/**
	 * Creates a {@link MemberExpression} that accessed the specified member.
	 * 
	 * @param expressionType
	 *            Type of access.
	 * @param instance
	 *            An {@link Expression} representing the instance.
	 * @param member
	 *            The {@code Member} to be accessed.
	 * @param resultType
	 *            The return value type.
	 * @param params
	 *            The parameters.
	 * @return A {@link MemberExpression} that accessed the specified member.
	 */
	public static MemberExpression member(int expressionType, Expression instance, Member member, Class<?> resultType, List<ParameterExpression> params) {
		return new MemberExpression(expressionType, instance, member, resultType, params);
	}

	/**
	 * Creates a {@link MemberExpression} that represents accessing an instance field.
	 * 
	 * @param instance
	 *            An {@link Expression} representing the instance.
	 * @param field
	 *            A field to be accessed.
	 * @return An {@link InvocationExpression} that represents accessing an instance field.
	 */
	public static MemberExpression get(Expression instance, Field field) {
		return member(ExpressionType.FieldAccess, instance, field, field.getType(), Collections.<ParameterExpression>emptyList());
	}

	/**
	 * Creates an {@link InvocationExpression} that represents a call to an instance method, or {@link UnaryExpression} in
	 * case of boxing.
	 * 
	 * @param instance
	 *            An {@link Expression} whose {@code getResultType()} value will be searched for a specific method.
	 * @param method
	 *            The {@link Method} to be called.
	 * @param arguments
	 *            An array of {@link Expression} objects that represent the arguments to the method.
	 * @return An {@link InvocationExpression} that has the {@link ExpressionType} method equal to Invoke or Convert in case
	 *         of boxing.
	 */
	public static Expression invoke(Expression instance, Method method, Expression... arguments) {
		return invoke(instance, method, Collections.unmodifiableList(Arrays.asList(arguments)));
	}

	/**
	 * Creates an {@link InvocationExpression} that represents a call to an instance method, or {@link UnaryExpression} in
	 * case of boxing.
	 * 
	 * @param instance
	 *            An {@link Expression} whose {@code getResultType()} value will be searched for a specific method.
	 * @param method
	 *            The {@link Method} to be called.
	 * @param arguments
	 *            An array of {@link Expression} objects that represent the arguments to the method.
	 * @return An {@link InvocationExpression} that has the {@link ExpressionType} method equal to Invoke or Convert in case
	 *         of boxing.
	 */
	public static Expression invoke(Expression instance, Method method, List<Expression> arguments) {

		if (instance != null) {
			Class<?> primitive;
			if (!instance.getResultType().isPrimitive() && ((primitive = _unboxers.get(method)) != null))
				return convert(instance, primitive);
		} else {
			Class<?> boxer;
			Expression e;
			if (arguments.size() == 1 && (e = arguments.get(0)).getResultType().isPrimitive() && ((boxer = _boxers.get(method)) != null))
				return convert(e, boxer);
		}

		if (method.isSynthetic()) {
			Object actualInstance = instance != null ? instance.accept(Interpreter.Instance).apply(null) : null;
			LambdaExpression<?> lambdaExpression = ExpressionClassCracker.get().lambdaFromFileSystem(actualInstance, method);
			return invoke(lambdaExpression, arguments);// arguments.get(0).accept(Interpreter.Instance).apply(null).getClass().isSynthetic()
		}

		return invoke(member(ExpressionType.MethodAccess, instance, method, method.getReturnType(), getParameters(method)), arguments);
	}

	/**
	 * Creates an {@link InvocationExpression} that represents a call to an instance method.
	 * 
	 * @param method
	 *            An {@link InvocableExpression} which encapsulates method to be called.
	 * @param arguments
	 *            An array of {@link Expression} objects that represent the arguments to the method.
	 * @return An {@link InvocationExpression} that has the {@link ExpressionType} method equal to Invoke.
	 */
	public static InvocationExpression invoke(InvocableExpression method, Expression... arguments) {
		return invoke(method, Arrays.asList(arguments));
	}

	/**
	 * Creates an {@link InvocationExpression} that represents a call to an instance method.
	 * 
	 * @param method
	 *            An {@link InvocableExpression} which encapsulates method to be called.
	 * @param arguments
	 *            An array of {@link Expression} objects that represent the arguments to the method.
	 * @return An {@link InvocationExpression} that has the {@link ExpressionType} method equal to Invoke.
	 */
	public static InvocationExpression invoke(InvocableExpression method, List<Expression> arguments) {
		arguments = new ArrayList<>(arguments);
		method = ExpressionClassCracker.get().parseSyntheticArguments(method, arguments);
		return new InvocationExpression(method, arguments);
	}

	private static List<ParameterExpression> getParameters(Member member) {

		Class<?>[] params;
		if (member instanceof Constructor<?>) {
			Constructor<?> ctor = (Constructor<?>) member;
			params = ctor.getParameterTypes();
		} else {
			Method m = (Method) member;
			params = m.getParameterTypes();
		}

		List<ParameterExpression> plist = new ArrayList<ParameterExpression>(params.length);
		for (int i = 0; i < params.length; i++)
			plist.add(Expression.parameter(params[i], i));

		return Collections.unmodifiableList(plist);
	}

	/**
	 * Creates a {@link InvocationExpression} that represents calling the specified constructor.
	 * 
	 * @param method
	 *            The constructor to invoke.
	 * @param arguments
	 *            The constructor arguments.
	 * @return A {@link InvocationExpression} that represents calling the specified constructor.
	 */
	public static InvocationExpression newInstance(Constructor<?> method, Expression... arguments) {
		return newInstance(method, Collections.unmodifiableList(Arrays.asList(arguments)));
	}

	/**
	 * Creates a {@link InvocationExpression} that represents calling the specified constructor.
	 * 
	 * @param method
	 *            The constructor to invoke.
	 * @param arguments
	 *            The constructor arguments.
	 * @return A {@link InvocationExpression} that represents calling the specified constructor.
	 */
	public static InvocationExpression newInstance(Constructor<?> method, List<Expression> arguments) {
		return invoke(member(ExpressionType.New, null, method, method.getDeclaringClass(), getParameters(method)), arguments);
	}

	/**
	 * Creates a {@link InvocationExpression} that represents calling the specified constructor.
	 * 
	 * @param type
	 *            {@link Class} to be instantiated.
	 * @param argumentTypes
	 *            The Constructor argument types.
	 * @param arguments
	 *            The constructor arguments.
	 * @return A {@link InvocationExpression} that represents calling the specified constructor.
	 * @throws NoSuchMethodException
	 *             if a matching method is not found.
	 */
	public static InvocationExpression newInstance(Class<?> type, Class<?>[] argumentTypes, Expression... arguments) throws NoSuchMethodException {

		return newInstance(type.getConstructor(argumentTypes), arguments);
	}

	/**
	 * Creates an {@link InvocationExpression} that represents a call to an instance method by calling the appropriate
	 * factory method, or {@link UnaryExpression} in case of boxing.
	 * 
	 * @param instance
	 *            An {@link Expression} whose {@code getResultType()} value will be searched for a specific method.
	 * @param name
	 *            The name of the method.
	 * @param parameterTypes
	 *            An array of {@link Class} objects that specify the type of parameters of the method.
	 * @param arguments
	 *            An array of {@link Expression} objects that represent the arguments to the method.
	 * @return An {@link InvocationExpression} that has the {@link ExpressionType} method equal to Invoke or Convert in case
	 *         of boxing.
	 * @throws NoSuchMethodException
	 *             if a matching method is not found.
	 */
	public static Expression invoke(Expression instance, String name, Class<?>[] parameterTypes, Expression... arguments) throws NoSuchMethodException {
		return invoke(instance, getDeclaredMethod(instance.getResultType(), name, parameterTypes), arguments);
	}

	/**
	 * Creates an {@link InvocationExpression} that represents a call to a static method by calling the appropriate factory
	 * method, or {@link UnaryExpression} in case of boxing.
	 * 
	 * @param type
	 *            The {@link Class} that specifies the type that contains the specified static method.
	 * @param name
	 *            The name of the method.
	 * @param parameterTypes
	 *            An array of {@link Class} objects that specify the type of parameters of the method.
	 * @param arguments
	 *            An array of {@link Expression} objects that represent the arguments to the method.
	 * @return An {@link InvocationExpression} that has the {@link ExpressionType} method equal to Invoke or Convert in case
	 *         of boxing.
	 * @throws NoSuchMethodException
	 *             if a matching method is not found.
	 */
	public static Expression invoke(Class<?> type, String name, Class<?>[] parameterTypes, Expression... arguments) throws NoSuchMethodException {
		return invoke(null, getDeclaredMethod(type, name, parameterTypes), arguments);
	}

	/**
	 * Get a method declaration recursively starting with the given class.
	 * 
	 * @param clazz
	 *            Class which is the start of the search
	 * @param name
	 *            The name of the searched method.
	 * @param parameterTypes
	 *            The parameter types of the searched method.
	 * @return The search method declaration.
	 * @throws NoSuchMethodException
	 *             if a matching method is not found.
	 */
	private static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>[] parameterTypes) throws NoSuchMethodException {
		Class<?> tmpClass = clazz;

		for (;;) {
			try {
				return tmpClass.getDeclaredMethod(name, parameterTypes);
			} catch (NoSuchMethodException e) {
				Class<?> thisClass = tmpClass;
				tmpClass = thisClass.getSuperclass();
				if (tmpClass == null)
					return thisClass.getMethod(name, parameterTypes);
			}
		}
	}

	/**
	 * Creates a {@link BinaryExpression} that represents a conditional operation, or one of operands in case test is a
	 * constant.
	 * 
	 * @param test
	 *            An Expression to set the getOperator() method equal to.
	 * @param ifTrue
	 *            An {@link Expression} to set the getFirst() method equal to.
	 * @param ifFalse
	 *            An {@link Expression} to set the getSecond() method equal to.
	 * @return A {@link BinaryExpression} that has the {@link ExpressionType} property equal to Conditional and the
	 *         getFirst() and getSecond() methods set to the specified values.
	 */
	public static Expression condition(Expression test, Expression ifTrue, Expression ifFalse) {
		if (!test.isBoolean())
			throw new IllegalArgumentException("test is " + test.getResultType());

		// reduce conditional
		if ((ifTrue.isBoolean())) {
			Expression ifTrueStripped = stripQuotesAndConverts(ifTrue);
			Expression ifFalseStripped = stripQuotesAndConverts(ifFalse);
			if (ifTrueStripped.getExpressionType() == ExpressionType.Constant && ifFalseStripped.getExpressionType() == ExpressionType.Constant) {
				ConstantExpression cfirst = (ConstantExpression) ifTrueStripped;
				ConstantExpression csecond = (ConstantExpression) ifFalseStripped;
				if (cfirst.getValue().equals(csecond.getValue()))
					return ifTrue;

				return convert((Boolean) cfirst.getValue() ? test : Expression.logicalNot(test), ifTrue.getResultType());
			}
		}

		return new BinaryExpression(ExpressionType.Conditional, ifTrue.getResultType(), test, ifTrue, ifFalse);
	}

	/**
	 * Creates a {@link UnaryExpression} that represents a test for null operation.
	 * 
	 * @param e
	 *            Operand
	 * @return A {@link UnaryExpression} that represents a test for null operation.
	 */
	public static UnaryExpression isNull(Expression e) {
		if (e.getResultType().isPrimitive())
			throw new IllegalArgumentException(e.getResultType().toString());

		return new UnaryExpression(ExpressionType.IsNull, Boolean.TYPE, e);
	}

	/**
	 * Creates a {@link UnaryExpression} that represents a test for null operation.
	 * 
	 * @param e
	 *            Operand
	 * @return A {@link UnaryExpression} that represents a test for null operation.
	 */
	public static UnaryExpression isNonNull(Expression e) {
		if (e.getResultType().isPrimitive())
			throw new IllegalArgumentException(e.getResultType().toString());

		return new UnaryExpression(ExpressionType.IsNonNull, Boolean.TYPE, e);
	}

	/**
	 * Creates a {@link UnaryExpression} that represents a bitwise complement operation.
	 * 
	 * @param e
	 *            Operand
	 * @return A {@link UnaryExpression} that represents a bitwise complement operation.
	 */
	public static UnaryExpression bitwiseNot(Expression e) {
		if (!e.isIntegral())
			throw new IllegalArgumentException(e.getResultType().toString());

		return new UnaryExpression(ExpressionType.BitwiseNot, e.getResultType(), e);
	}

	/**
	 * Creates a {@link Expression} that represents a logical negation operation.
	 * 
	 * @param e
	 *            Operand
	 * @return A {@link Expression} that represents a logical negation operation.
	 */
	public static Expression logicalNot(Expression e) {
		if (!e.isBoolean())
			throw new IllegalArgumentException(e.getResultType().toString());

		BinaryExpression be;
		UnaryExpression ue;

		int type;
		switch (e.getExpressionType()) {
		case ExpressionType.Conditional:
			be = (BinaryExpression) e;
			return condition(be.getOperator(), logicalNot(be.getFirst()), logicalNot(be.getSecond()));

		case ExpressionType.Constant:
			ConstantExpression ce = (ConstantExpression) e;
			return constant(!(Boolean) ce.getValue(), ce.getResultType());

		case ExpressionType.LogicalNot:
			ue = (UnaryExpression) e;
			return ue.getFirst();

		case ExpressionType.IsNull:
			ue = (UnaryExpression) e;
			return isNonNull(ue.getFirst());

		case ExpressionType.IsNonNull:
			ue = (UnaryExpression) e;
			return isNull(ue.getFirst());

		case ExpressionType.LogicalAnd:
			be = (BinaryExpression) e;
			return convert(logicalOr(logicalNot(be.getFirst()), logicalNot(be.getSecond())), be.getResultType());

		case ExpressionType.LogicalOr:
			be = (BinaryExpression) e;
			return convert(logicalAnd(logicalNot(be.getFirst()), logicalNot(be.getSecond())), be.getResultType());

		case ExpressionType.Equal:
			type = ExpressionType.NotEqual;
			break;
		case ExpressionType.GreaterThan:
			type = ExpressionType.LessThanOrEqual;
			break;
		case ExpressionType.GreaterThanOrEqual:
			type = ExpressionType.LessThan;
			break;
		case ExpressionType.LessThan:
			type = ExpressionType.GreaterThanOrEqual;
			break;
		case ExpressionType.LessThanOrEqual:
			type = ExpressionType.GreaterThan;
			break;
		case ExpressionType.NotEqual:
			type = ExpressionType.Equal;
			break;
		default:
			return new UnaryExpression(ExpressionType.LogicalNot, e.getResultType(), e);
		}

		be = (BinaryExpression) e;
		return binary(type, be.getFirst(), be.getSecond());
	}

	/**
	 * Dispatches to the specific visit method for this node type. For example, {@link BinaryExpression} calls the
	 * {@link ExpressionVisitor#visit(BinaryExpression)}.
	 * 
	 * @param <T>
	 *            type the visitor methods return after processing.
	 * 
	 * @param v
	 *            The visitor to visit this node with.
	 * @return T
	 */
	public final <T> T accept(ExpressionVisitor<T> v) {
		return visit(v);
	}

	/**
	 * Dispatches to the specific visit method for this node type. For example, {@link BinaryExpression} calls the
	 * {@link ExpressionVisitor#visit(BinaryExpression)}.
	 * 
	 * @param <T>
	 *            type the visitor methods return after processing.
	 * 
	 * @param v
	 *            The visitor to visit this node with.
	 * @return T
	 */
	protected abstract <T> T visit(ExpressionVisitor<T> v);
}
