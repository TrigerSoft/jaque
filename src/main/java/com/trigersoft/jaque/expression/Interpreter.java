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

import static com.trigersoft.jaque.function.Functions.add;
import static com.trigersoft.jaque.function.Functions.and;
import static com.trigersoft.jaque.function.Functions.bitwiseAnd;
import static com.trigersoft.jaque.function.Functions.bitwiseNot;
import static com.trigersoft.jaque.function.Functions.bitwiseOr;
import static com.trigersoft.jaque.function.Functions.constant;
import static com.trigersoft.jaque.function.Functions.divide;
import static com.trigersoft.jaque.function.Functions.equal;
import static com.trigersoft.jaque.function.Functions.greaterThan;
import static com.trigersoft.jaque.function.Functions.greaterThanOrEqual;
import static com.trigersoft.jaque.function.Functions.iif;
import static com.trigersoft.jaque.function.Functions.instanceOf;
import static com.trigersoft.jaque.function.Functions.lessThan;
import static com.trigersoft.jaque.function.Functions.lessThanOrEqual;
import static com.trigersoft.jaque.function.Functions.modulo;
import static com.trigersoft.jaque.function.Functions.multiply;
import static com.trigersoft.jaque.function.Functions.negate;
import static com.trigersoft.jaque.function.Functions.not;
import static com.trigersoft.jaque.function.Functions.or;
import static com.trigersoft.jaque.function.Functions.shiftLeft;
import static com.trigersoft.jaque.function.Functions.shiftRight;
import static com.trigersoft.jaque.function.Functions.subtract;
import static com.trigersoft.jaque.function.Functions.xor;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

final class Interpreter implements ExpressionVisitor<Function<Object[], ?>> {

	static final Interpreter Instance = new Interpreter();

	private Interpreter() {
	}

	private Function<Object[], ?> normalize(BiFunction<Object[], Object[], ?> source) {
		return pp -> source.apply(pp, pp);
	}

	private Function<Object[], Boolean> normalize(BiPredicate<Object[], Object[]> source) {
		return pp -> source.test(pp, pp);
	}

	private Function<Object[], Boolean> normalize(Predicate<Object[]> source) {
		return pp -> source.test(pp);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Function<Object[], ?> visit(BinaryExpression e) {
		final Function<Object[], ?> first = e.getFirst().accept(this);
		final Function<Object[], ?> second = e.getSecond().accept(this);
		switch (e.getExpressionType()) {
		case ExpressionType.Add:
			return normalize(add((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.BitwiseAnd:
			return normalize(bitwiseAnd((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.LogicalAnd:
			return normalize(and((Function<Object[], Boolean>) first, (Function<Object[], Boolean>) second));
		case ExpressionType.ArrayIndex:
			return t -> Array.get(first.apply(t), (Integer) second.apply(t));
		// return new Function<Object, Object[]>() {
		// // @Override
		// public Object invoke(Object[] t) throws Throwable {
		// return Array.get(first.invoke(t), (Integer) second
		// .invoke(t));
		// }
		// };
		// case ExpressionType.Coalesce:
		// return coalesce((Function<?, Object[]>) first,
		// (Function<?, Object[]>) second);
		case ExpressionType.Conditional:
			return iif((Function<Object[], Boolean>) e.getOperator().accept(this), first, second);
		case ExpressionType.Divide:
			return normalize(divide((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.Equal:
			return normalize(equal(first, second));
		case ExpressionType.ExclusiveOr:
			return normalize(xor((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.GreaterThan:
			return normalize(greaterThan((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.GreaterThanOrEqual:
			return normalize(
					greaterThanOrEqual((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.LeftShift:
			return normalize(shiftLeft((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.LessThan:
			return normalize(lessThan((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.LessThanOrEqual:
			return normalize(lessThanOrEqual((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.Modulo:
			return normalize(modulo((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.Multiply:
			return normalize(multiply((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.NotEqual:
			return normalize(equal(first, second).negate());
		case ExpressionType.BitwiseOr:
			return normalize(bitwiseOr((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.LogicalOr:
			return normalize(or((Function<Object[], Boolean>) first, (Function<Object[], Boolean>) second));
		// case ExpressionType.Power:
		// return power((Function<Number, Object[]>) first,
		// (Function<Number, Object[]>) second);
		case ExpressionType.RightShift:
			return normalize(shiftRight((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.Subtract:
			return normalize(subtract((Function<Object[], Number>) first, (Function<Object[], Number>) second));
		case ExpressionType.InstanceOf:
			return normalize(instanceOf(first, (Class<?>) second.apply(null)));
		default:
			throw new IllegalArgumentException(ExpressionType.toString(e.getExpressionType()));
		}
	}

	@Override
	public Function<Object[], ?> visit(ConstantExpression e) {
		return constant(e.getValue());
	}

	@Override
	public Function<Object[], ?> visit(MemberExpression e) {
		final Member m = e.getMember();
		Expression ei = e.getInstance();
		final Function<Object[], ?> instance = ei != null ? ei.accept(this) : null;

		List<Function<Object[], ?>> argExps = e.getArguments().stream()
				.map(exp -> ((Function<Object[], ?>) exp.accept(this))).collect(toList());
		Function<Object[], Object[]> args = t -> argExps.stream().map(x -> x.apply(t)).collect(toList()).toArray();

		Function<Object[], Object> field = t -> {
			try {
				return ((Field) m).get(instance == null ? null : instance.apply(t));
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				throw new RuntimeException(ex);
			}
		};

		Function<Object[], ?> method = t -> {
			Object inst;
			if (instance != null) {
				inst = instance.apply(t);
			} else
				inst = null;
			try {
				return ((Method) m).invoke(inst, args.apply(t));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		};

		Function<Object[], ?> ctor = t -> {
			try {
				return ((Constructor<?>) m).newInstance(args.apply(t));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		};

		Function<Object[], ?> member;

		if (m instanceof Field)
			member = field;
		else if (m instanceof Method)
			member = method;
		else
			member = ctor;

		return member;
	}

	@Override
	public Function<Object[], ?> visit(ParameterExpression e) {
		final int index = e.getIndex();
		return t -> t[index];
	}

	@SuppressWarnings("unchecked")
	@Override
	public Function<Object[], ?> visit(UnaryExpression e) {
		final Function<Object[], ?> first = e.getFirst().accept(this);
		switch (e.getExpressionType()) {
		case ExpressionType.ArrayLength:
			return t -> Array.getLength(first.apply(t));
		case ExpressionType.BitwiseNot:
			return (Function<Object[], ?>) bitwiseNot((Function<Object[], Number>) first);
		case ExpressionType.Convert:
			final Class<?> to = e.getResultType();
			if (to.equals(Boolean.TYPE) && e.getFirst().getResultType().equals(Integer.TYPE)) {
				return t -> (Integer.valueOf(1).equals(first.apply(t)));
			}
			if (to.isPrimitive() || Number.class.isAssignableFrom(to))
				return t -> {
					Object source = first.apply(t);
					if (source instanceof Number) {
						Number result = (Number) source;
						if (to.isPrimitive()) {
							if (to == Integer.TYPE)
								return result.intValue();
							if (to == Long.TYPE)
								return result.longValue();
							if (to == Float.TYPE)
								return result.floatValue();
							if (to == Double.TYPE)
								return result.doubleValue();
							if (to == Byte.TYPE)
								return result.byteValue();
							if (to == Character.TYPE)
								return (char) result.intValue();
							if (to == Short.TYPE)
								return result.shortValue();
						} else if (result != null) {
							if (to == BigInteger.class)
								return BigInteger.valueOf(result.longValue());
							if (to == BigDecimal.class)
								return BigDecimal.valueOf(result.doubleValue());
						}
					}
					if (source instanceof Character) {
						if (to == Integer.TYPE)
							return (int) (char) source;
						if (to == Long.TYPE)
							return (long) (char) source;
						if (to == Float.TYPE)
							return (float) (char) source;
						if (to == Double.TYPE)
							return (double) (char) source;
					}
					return to.cast(source);
				};

			return first;
		case ExpressionType.IsNull:
			return first.andThen(r -> r == null);
		case ExpressionType.LogicalNot:
			return normalize(not((Function<Object[], Boolean>) first));
		case ExpressionType.Negate:
			return (Function<Object[], ?>) negate((Function<Object[], Number>) first);
		default:
			throw new IllegalArgumentException(ExpressionType.toString(e.getExpressionType()));
		}
	}

	@Override
	public Function<Object[], ?> visit(ThisExpression e) {
		return t -> e.getValue();
	}

	@Override
	public Function<Object[], ?> visit(LambdaInvocationExpression lambdaInvocationExpression) {
		Function<Object[], ?> instance = lambdaInvocationExpression.getInstance().accept(this);
		List<Function<Object[], ?>> argExps = lambdaInvocationExpression.getArguments().stream()
				.map(arg -> ((Function<Object[], ?>)arg.accept(this))).collect(toList());
		Function<Object[], Object[]> params = t -> argExps.stream().map(arg -> arg.apply(t)).collect(toList())
				.toArray();
		return instance.compose(params);
	}
}
