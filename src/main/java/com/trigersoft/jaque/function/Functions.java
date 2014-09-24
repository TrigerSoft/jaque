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

package com.trigersoft.jaque.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import com.trigersoft.jaque.function.math.*;

/**
 * @author <a href="mailto://object_streaming@googlegroups.com">Konstantin
 *         Triger</a>
 * 
 */
public final class Functions {
	private Functions() {
	}

	/**
	 * Returns the absolute value of an argument evaluation result. In other
	 * words, the result is semantically equivalent to this pseudo-code: <p/>
	 * {@code abs(selector.invoke(T))}
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param selector
	 *            a function to extract the numeric value from T.
	 * @return the absolute value of an argument evaluation result.
	 */
	public static <N extends Number> Function<N, Number> abs(
			Function<N, ? extends Number> selector) {
		return selector.andThen(UnaryOperator.Abs::eval);
	}

	/**
	 * Returns the absolute value of an argument.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @return the absolute value of an argument.
	 */
	public static <T extends Number> Function<T, Number> abs() {
		return abs(Function.<T> identity());
	}

	/**
	 * Returns the negative value of an argument evaluation result. In other
	 * words, the result is semantically equivalent to this pseudo-code: <p/>
	 * {@code -selector.invoke(T)}
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param selector
	 *            a function to extract the numeric value from T.
	 * @return the negative value of an argument evaluation result.
	 */
	public static Function<?, Number> negate(
			Function<?, ? extends Number> selector) {
		return selector.andThen(UnaryOperator.Negate::eval);
	}

	/**
	 * Returns the negative value of an argument.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @return the negative value of an argument.
	 */
	public static Function<?, Number> negate() {
		return negate(Function.identity());
	}

	/**
	 * Returns the bitwise not value of an argument evaluation result. In other
	 * words, the result is semantically equivalent to this pseudo-code: <p/>
	 * {@code ~selector.invoke(T)}
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param selector
	 *            a function to extract the numeric value from T.
	 * @return the bitwise not value of an argument evaluation result.
	 */
	public static <T> Function<T, Number> bitwiseNot(
			Function<T, ? extends Number> selector) {
		return selector.andThen(UnaryOperator.Not::eval);
	}

	/**
	 * Returns the bitwise not value of an argument.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @return the bitwise not value of an argument.
	 */
	public static <T extends Number> Function<T, Number> bitwiseNot() {
		return bitwiseNot(Function.<T> identity());
	}

	/**
	 * Returns the value of {@code left.invoke(T) & right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) & right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> bitwiseAnd(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.And.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) + right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) + right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> add(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.Add.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) - right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) - right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> subtract(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.Subtract.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) * right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) * right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> multiply(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.Multiply.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) / right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) / right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> divide(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.Divide.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) % right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) % right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> modulo(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.Modulo.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) | right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) | right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> bitwiseOr(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.Or.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of <tt>left.invoke(T)<sup>right.invoke(T)</sup></tt>.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of <tt>left.invoke(T)<sup>right.invoke(T)</sup></tt>.
	 */
	public static <T, U> BiFunction<T, U, Number> power(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.Power.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) << right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) << right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> shiftLeft(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.ShiftLeft.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) >>> right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) >>> right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> shiftRight(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.ShiftRight.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) ^ right.invoke(T)}.
	 * 
	 * @param <E>
	 *            the type of the numeric type.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) ^ right.invoke(T)}
	 */
	public static <T, U> BiFunction<T, U, Number> xor(
			Function<T, ? extends Number> ft,
			Function<U, ? extends Number> fu) {
		return (T t, U u) -> BinaryOperator.Xor.eval(ft.apply(t), fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) && right.invoke(T)}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) && right.invoke(T)}.
	 */
	public static <T, U> BiPredicate<T, U> and(
			Predicate<T> ft,
			Predicate<U> fu) {
		return (T t, U u) -> ft.test(t) && fu.test(u);
	}
	
	public static <T, U> BiPredicate<T, U> and(
			Function<T, Boolean> ft,
			Function<U, Boolean> fu) {
		return and((Predicate<T>)(T t) -> ft.apply(t), (Predicate<U>)(U u) -> fu.apply(u));
	}

	/**
	 * Returns the value of {@code left.invoke(T) || right.invoke(T)}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param left
	 *            a function to extract the left hand value from T.
	 * @param right
	 *            a function to extract the right hand value from T.
	 * @return the value of {@code left.invoke(T) || right.invoke(T)}.
	 */
	public static <T, U> BiPredicate<T, U> or(
			Predicate<T> ft,
			Predicate<U> fu) {
		return (T t, U u) -> ft.test(t) || fu.test(u);
	}
	
	public static <T, U> BiPredicate<T, U> or(
			Function<T, Boolean> ft,
			Function<U, Boolean> fu) {
		return or((Predicate<T>)(T t) -> ft.apply(t), (Predicate<U>)(U u) -> fu.apply(u));
	}

	/**
	 * Returns the value of
	 * {@code left.invoke(T).compareTo(right.invoke(T)) < 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @return the value of
	 *         {@code left.invoke(T).compareTo(right.invoke(T)) < 0}.
	 */
	public static <T, U, Key extends Number> BiPredicate<T, U> lessThan(
			Function<T, Key> ft,
			Function<U, Key> fu) {
		return (T t, U u) -> Objects.compare(ft.apply(t),fu.apply(u), 
				(Key key1, Key key2) -> BinaryOperator.Subtract.eval(key1, key2).intValue()) < 0;
	}

	/**
	 * Returns the value of
	 * {@code comparator.compare(left.invoke(T), right.invoke(T)) < 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @param comparator
	 *            comparator of Keys
	 * @return the value of
	 *         {@code comparator.compare(left.invoke(T), right.invoke(T)) < 0}.
	 */
//	public static <T, Key> Comparator<T, Key> lessThan(
//			Function<? extends Key, ? super T> left,
//			Function<? extends Key, ? super T> right,
//			final java.util.Comparator<? super Key> comparator) {
//		return new Comparator<T, Key>(left, right,
//				Comparator.Operator.LessThan, comparator);
//	}

	// public static <Key extends Comparable<? super Key>> Predicate2<Key>
	// lessThanOrEqual(
	// Function<Key, Key> right) {
	// return lessThanOrEqual(Expression.<Key> self(), right);
	// }

	/**
	 * Returns the value of
	 * {@code left.invoke(T).compareTo(right.invoke(T)) <= 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @return the value of
	 *         {@code left.invoke(T).compareTo(right.invoke(T)) <= 0}.
	 */
	public static <T, U, Key extends Number> BiPredicate<T, U> lessThanOrEqual(
			Function<T, Key> ft,
			Function<U, Key> fu) {
		return (T t, U u) -> Objects.compare(ft.apply(t),fu.apply(u), 
				(Key key1, Key key2) -> BinaryOperator.Subtract.eval(key1, key2).intValue()) <= 0;
	}

	/**
	 * Returns the value of
	 * {@code comparator.compare(left.invoke(T), right.invoke(T)) <= 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @param comparator
	 *            comparator of Keys
	 * @return the value of
	 *         {@code comparator.compare(left.invoke(T), right.invoke(T)) <= 0}.
	 */
//	public static <T, Key> Comparator<T, Key> lessThanOrEqual(
//			Function<? extends Key, ? super T> left,
//			Function<? extends Key, ? super T> right,
//			final java.util.Comparator<? super Key> comparator) {
//		return new Comparator<T, Key>(left, right,
//				Comparator.Operator.LessThanOrEqual, comparator);
//	}

	/**
	 * Returns the value of
	 * {@code left.invoke(T).compareTo(right.invoke(T)) == 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @return the value of
	 *         {@code left.invoke(T).compareTo(right.invoke(T)) == 0}.
	 */
	public static <T, U> BiPredicate<T, U> equal(
			Function<T, ?> ft,
			Function<U, ?> fu) {
		return (T t, U u) -> Objects.equals(ft.apply(t),fu.apply(u));
	}

	/**
	 * Returns the value of
	 * {@code comparator.compare(left.invoke(T), right.invoke(T)) == 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @param comparator
	 *            comparator of Keys
	 * @return the value of
	 *         {@code comparator.compare(left.invoke(T), right.invoke(T)) == 0}.
	 */
//	public static <T, Key> Comparator<T, Key> equal(
//			Function<? extends Key, ? super T> left,
//			Function<? extends Key, ? super T> right,
//			final java.util.Comparator<? super Key> comparator) {
//		return new Comparator<T, Key>(left, right, Comparator.Operator.Equal,
//				comparator);
//	}

	// public static <Key extends Comparable<? super Key>> Predicate2<Key>
	// greaterThanOrEqual(
	// Function<? super Key, ? extends Key> right) {
	// return greaterThanOrEqual(Expression.<Key> self(), right);
	// }

	/**
	 * Returns the value of
	 * {@code left.invoke(T).compareTo(right.invoke(T)) >= 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @return the value of
	 *         {@code left.invoke(T).compareTo(right.invoke(T)) >= 0}.
	 */
	public static <T, U, Key extends Number> BiPredicate<T, U> greaterThanOrEqual(
			Function<T, Key> ft,
			Function<U, Key> fu) {
		return (T t, U u) -> Objects.compare(ft.apply(t),fu.apply(u), 
				(Key key1, Key key2) -> BinaryOperator.Subtract.eval(key1, key2).intValue()) >= 0;
	}

	/**
	 * Returns the value of
	 * {@code comparator.compare(left.invoke(T), right.invoke(T)) >= 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @param comparator
	 *            comparator of Keys
	 * @return the value of
	 *         {@code comparator.compare(left.invoke(T), right.invoke(T)) >= 0}.
	 */
//	public static <T, Key> Comparator<T, Key> greaterThanOrEqual(
//			Function<? extends Key, ? super T> left,
//			Function<? extends Key, ? super T> right,
//			final java.util.Comparator<? super Key> comparator) {
//		return new Comparator<T, Key>(left, right,
//				Comparator.Operator.GreaterThanOrEqual, comparator);
//	}

	/**
	 * Returns the value of
	 * {@code left.invoke(T).compareTo(right.invoke(T)) > 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @return the value of
	 *         {@code left.invoke(T).compareTo(right.invoke(T)) > 0}.
	 */
	public static <T, U, Key extends Number> BiPredicate<T, U> greaterThan(
			Function<T, Key> ft,
			Function<U, Key> fu) {
		return (T t, U u) -> Objects.compare(ft.apply(t),fu.apply(u), 
				(Key key1, Key key2) -> BinaryOperator.Subtract.eval(key1, key2).intValue()) > 0;
	}

	/**
	 * Returns the value of
	 * {@code comparator.compare(left.invoke(T), right.invoke(T)) > 0}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param <Key>
	 *            the type of key to compare.
	 * @param left
	 *            a function to extract the left hand Key from T.
	 * @param right
	 *            a function to extract the right hand Key from T.
	 * @param comparator
	 *            comparator of Keys
	 * @return the value of
	 *         {@code comparator.compare(left.invoke(T), right.invoke(T)) > 0}.
	 */
//	public static <T, Key> Comparator<T, Key> greaterThan(
//			Function<? extends Key, ? super T> left,
//			Function<? extends Key, ? super T> right,
//			final java.util.Comparator<? super Key> comparator) {
//		return new Comparator<T, Key>(left, right,
//				Comparator.Operator.GreaterThan, comparator);
//	}

	/**
	 * Represents a function returning a constant value.
	 * 
	 * @param <Result>
	 *            the type of returned value.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param result
	 *            value to return.
	 * @return result.
	 */
	public static <T, Result> Function<T, Result> constant(final Result result) {
		return (T t) -> result;
	}

	// public static <T> Function<T, T> constant(final T result) {
	// return new ConstantSelector<T, T>(result);
	// }

	/**
	 * Represents a function returning passed argument.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence and type of returned
	 *            value.
	 * @return passed argument.
	 */
//	@SuppressWarnings("unchecked")
//	public static <T> Function<T, T> self() {
//		return (Function<T, T>) SelfSelector;
//	}

	//
	// @SuppressWarnings("unchecked")
	// public static <T> Function<T, T> self(final Class<T> type) {
	// return (Function<T, T>) SelfSelector;
	// }

	/**
	 * Represents a function returning value of a property using reflection.
	 * 
	 * @param <Result>
	 *            the type of returned value.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param outerType
	 *            Class of the objects in the sequence.
	 * @param propertyName
	 *            name of the property to retrieve. The implementation will look
	 *            for get&lt;propertyName&gt;() method.
	 * @return value of the property.
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public static <T, Result> Function<T, Result> property(
			Class<? super T> outerType, // Class<? super Result> resultType,
			String propertyName) throws NoSuchMethodException {
		Method d = outerType.getDeclaredMethod("get" + propertyName);
		return (T t) -> {
			try {
				return (Result)d.invoke(t);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		};
	}

	/**
	 * Represents a function returning value of a field using reflection.
	 * 
	 * @param <Result>
	 *            the type of returned value.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param outerType
	 *            Class of the objects in the sequence.
	 * @param fieldName
	 *            name of the field to retrieve.
	 * @return value of the field.
	 * @throws NoSuchFieldException
	 */
	@SuppressWarnings("unchecked")
	public static <T, Result> Function<T, Result> field(
			Class<? super T> outerType, // Class<? super Result> resultType,
			String fieldName) throws NoSuchFieldException {
		java.lang.reflect.Field d = outerType.getDeclaredField(fieldName);
		return (T t) -> {
			try {
				return (Result)d.get(t);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		};
	}

	/**
	 * Returns the value of {@code first.invoke(T) ?? second.invoke(T)}.
	 * <p/>e, that either ifTrue or ifFalse function is evaluated.
	 * 
	 * @param <Result>
	 *            the type of returned value.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param first
	 *            a function to evaluate if predicate returns true.
	 * @param second
	 *            a function to evaluate if predicate returns false.
	 * @return the value of {@code first.invoke(T) ?? second.invoke(T)}.
	 */
//	public static <T, U, Result> BiFunction<T, U, Result> coalesce(
//			final Function<? super T, ? extends Result> first,
//			final Function<? super T, ? extends Result> second) {
//		return (T t, U u) -> ;
//	}

	/**
	 * Returns the value of {@code operand.invoke(T) instanceof type}.
	 * 
	 * @param <Result>
	 *            the type of returned value.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param operand
	 *            a function to extract the operand from T
	 * @param type
	 *            the Class to test by.
	 * @return the value of {@code operand.invoke(T) instanceof type}.
	 */
	public static <Result, T> Predicate<T> instanceOf(
			final Function<? super T, ? extends Result> operand,
			final Class<?> type) {
		return t -> type.isInstance(operand.apply(t));
	}

	/**
	 * Returns the value of
	 * {@code predicate.invoke(first, second) ? first : second}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param predicate
	 *            predicate to test.
	 * @return the value of
	 *         {@code predicate.invoke(first, second) ? first : second}.
	 */
//	public static <T> Function2<T, T, T> conditional(
//			final Function2<Boolean, T, T> predicate) {
//		return new Function2<T, T, T>() {
//
//			@Override
//			public T invoke(T t, T r) throws Throwable {
//				if (t == null)
//					return r;
//				return predicate.invoke(t, r) ? t : r;
//			}
//
//		};
//	}

	/**
	 * Returns the value of
	 * {@code predicate.invoke(T) ? ifTrue.invoke(T) : ifFalse.invoke(T)}.
	 * <p/>Note, that either ifTrue or ifFalse function is evaluated.
	 * 
	 * @param <Result>
	 *            the type of returned value.
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param predicate
	 *            predicate to test.
	 * @param ifTrue
	 *            a function to evaluate if predicate returns true.
	 * @param ifFalse
	 *            a function to evaluate if predicate returns false.
	 * @return the value of
	 *         {@code predicate.invoke(T) ? ifTrue.invoke(T) : ifFalse.invoke(T)}.
	 */
	public static <Result, T> Function<T, Result> iif(
			final Function<? super T, Boolean> predicate,
			final Function<? super T, ? extends Result> ifTrue,
			final Function<? super T, ? extends Result> ifFalse) {
		return t -> predicate.apply(t) ? ifTrue.apply(t) : ifFalse.apply(t); 
	}

	/**
	 * Negates the return value of a predicate. In other words:
	 * {@code !predicate.invoke(T)}.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param predicate
	 *            predicate to negate its evaluation result.
	 * @return value of {@code !predicate.invoke(T)}.
	 */
	public static <T> Predicate<T> not(Function<T, Boolean> predicate) {
		Predicate<T> p = t -> predicate.apply(t);
		return p.negate();
	}

	/**
	 * Counts invocations and returns true while the counter is less than
	 * specified.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param value
	 *            breaking number of invocations.
	 * @return true while the counter is less than specified, otherwise false.
	 */
//	public static <T> Predicate<T> indexLessThan(final int value) {
//		if (value < 0)
//			throw new IndexOutOfBoundsException("value");
//		return new CountingPredicate<T>() {
//			@Override
//			public Boolean invoke(T t, int index) throws Throwable {
//				return index < value;
//			}
//		};
//	}

	/**
	 * Counts invocations and returns true while the counter is less or equal
	 * than specified.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param value
	 *            breaking number of invocations.
	 * @return true while the counter is less or equal than specified, otherwise
	 *         false.
	 */
//	public static <T> Predicate<T> indexLessThanOrEqual(final int value) {
//		if (value < 0)
//			throw new IndexOutOfBoundsException("value");
//		return new CountingPredicate<T>() {
//			@Override
//			public Boolean invoke(T t, int index) throws Throwable {
//				return index <= value;
//			}
//		};
//	}

	/**
	 * Counts invocations and returns true while the counter is greater than
	 * specified.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param value
	 *            breaking number of invocations.
	 * @return true while the counter is greater than specified, otherwise
	 *         false.
	 */
//	public static <T> Predicate<T> indexGreaterThan(final int value) {
//		if (value < 0)
//			throw new IndexOutOfBoundsException("value");
//		return new CountingPredicate<T>() {
//			@Override
//			public Boolean invoke(T t, int index) throws Throwable {
//				return index > value;
//			}
//		};
//	}

	/**
	 * Counts invocations and returns true while the counter is greater or equal
	 * than specified.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param value
	 *            breaking number of invocations.
	 * @return true while the counter is greater or equal than specified,
	 *         otherwise false.
	 */
//	public static <T> Predicate<T> indexGreaterThanOrEqual(final int value) {
//		if (value < 0)
//			throw new IndexOutOfBoundsException("value");
//		return new CountingPredicate<T>() {
//			@Override
//			public Boolean invoke(T t, int index) throws Throwable {
//				return index >= value;
//			}
//		};
//	}

	/**
	 * Counts invocations and returns true while the counter is inside the
	 * specified range.
	 * 
	 * @param <T>
	 *            the type of the objects in the sequence.
	 * @param start
	 *            lower limit of invocations.
	 * @param count
	 *            number of invocations to break.
	 * @return true while the counter is inside the specified range.
	 */
//	public static <T> Predicate<T> indexRange(final int start, final int count) {
//		if (start < 0)
//			throw new IndexOutOfBoundsException("start");
//		return and(indexGreaterThanOrEqual(start), indexLessThan(start + count));
//	}
//
//	private static abstract class LogicalUnaryOperation<T> extends
//			UnaryFunction<Boolean, T> implements Predicate<T> {
//		protected LogicalUnaryOperation(Function<Boolean, ? super T> left) {
//			super(left);
//		}
//	}
//
//	private static final class Not<T> extends LogicalUnaryOperation<T> {
//		protected Not(Function<Boolean, ? super T> left) {
//			super(left);
//		}
//
//		@Override
//		public Boolean invoke(T source) throws Throwable {
//			return !super.invoke(source);
//		}
//	}
//
//	private static final class Cast<T, R, Result extends R> implements
//			AggregateFunction<Result, T> {
//		private final AggregateFunction<R, T> _value;
//
//		public Cast(AggregateFunction<R, T> value) {
//			_value = value;
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public Result invoke(T t) throws Throwable {
//			return (Result) _value.invoke(t);
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public Result invoke(Result t, T r) throws Throwable {
//			return (Result) _value.invoke(t, r);
//		}
//	}
//
//	private static final class ConstantSelector<Result, T> implements
//			Function<Result, T> {
//		private final Result _value;
//
//		public ConstantSelector(Result value) {
//			_value = value;
//		}
//
//		@Override
//		public Result invoke(T t) throws Throwable {
//			return _value;
//		}
//	}
//
//	private static final Function<Object, Object> SelfSelector = new Function<Object, Object>() {
//
//		@Override
//		public Object invoke(Object t) throws Throwable {
//			return t;
//		}
//	};
//
//	private static final class Property<Result, T> implements
//			Function<Result, T> {
//
//		private final Method _method;
//
//		// private final String _propertyName;
//
//		public Property(Class<? super T> type, String propertyName)
//				throws NoSuchMethodException {
//
//			if (type == null)
//				throw new NullPointerException("type");
//			if (propertyName == null)
//				throw new NullPointerException("propetyName");
//
//			// _propertyName = propertyName;
//			_method = type.getDeclaredMethod("get" + propertyName);
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public Result invoke(T t) throws Throwable {
//			return (Result) _method.invoke(t);
//		}
//	}
//
//	private static final class Field<Result, T> implements Function<Result, T> {
//		private final java.lang.reflect.Field _field;
//
//		// private final String _fieldName;
//
//		public Field(Class<? super T> type, String fieldName)
//				throws NoSuchFieldException {
//
//			if (type == null)
//				throw new NullPointerException("type");
//			if (fieldName == null)
//				throw new NullPointerException("fieldName");
//
//			// _fieldName = fieldName;
//			_field = type.getDeclaredField(fieldName);
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public Result invoke(T t) throws Throwable {
//			return (Result) _field.get(t);
//		}
//	}
//
//	private static abstract class LogicalBinaryOperation<T> extends
//			BinaryFunction<Boolean, T> implements Predicate<T> {
//		protected LogicalBinaryOperation(Function<Boolean, ? super T> first,
//				Function<Boolean, ? super T> second) {
//			super(first, second);
//		}
//	}
//
//	private static final class And<T> extends LogicalBinaryOperation<T> {
//		public And(Function<Boolean, ? super T> first, Function<Boolean, ? super T> second) {
//			super(first, second);
//		}
//
//		@Override
//		public Boolean invoke(Boolean t, T source) throws Throwable {
//			return t && getSecond().invoke(source);
//		}
//	}
//
//	private static final class Or<T> extends LogicalBinaryOperation<T> {
//		public Or(Function<Boolean, ? super T> first, Function<Boolean, ? super T> second) {
//			super(first, second);
//		}
//
//		@Override
//		public Boolean invoke(Boolean t, T source) throws Throwable {
//			return t || getSecond().invoke(source);
//		}
//	}
//
//	private static final class ArithmeticUnaryFunction<Result extends Number, T>
//			extends UnaryFunction<Result, T> {
//
//		private final UnaryOperator _op;
//
//		public ArithmeticUnaryFunction(
//				Function<? extends Result, ? super T> left, UnaryOperator op) {
//			super(left);
//			_op = op;
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public Result invoke(T source) throws Throwable {
//			return (Result) _op.eval(super.invoke(source));
//		}
//	}
//
//	private static final class ArithmeticBinaryFunction<E extends Number, T>
//			extends BinaryFunction<Number, T> {
//
//		private final BinaryOperator _op;
//
//		public ArithmeticBinaryFunction(Function<E, ? super T> first,
//				Function<? extends Number, ? super T> second, BinaryOperator op) {
//			super(first, second);
//			_op = op;
//		}
//
//		@Override
//		@SuppressWarnings("unchecked")
//		public E invoke(Number l, T source) throws Throwable {
//			Number r = getSecond().invoke(source);
//
//			return (E) _op.eval(l, r);
//		}
//	}
}
