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

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.*;

import static jaque.function.Functions.*;

final class Interpreter implements ExpressionVisitor<Function<Object[], ?>> {

	static final Interpreter Instance = new Interpreter();

	private Interpreter() {
	}

	private Function<Object[], ?> normalize(
			BiFunction<Object[], Object[], ?> source) {
		return pp -> source.apply(pp, pp);
	}

	private Function<Object[], Boolean> normalize(
			BiPredicate<Object[], Object[]> source) {
		return pp -> source.test(pp, pp);
	}

	private Function<Object[], Boolean> normalize(Predicate<Object[]> source) {
		return pp -> source.test(pp);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Function<Object[], ?> visit(BinaryExpression e) {
		final Function<Object[], ?> first = e.getFirst().apply(this);
		final Function<Object[], ?> second = e.getSecond().apply(this);
		switch (e.getExpressionType()) {
		case ExpressionType.Add:
			return normalize(add((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.BitwiseAnd:
			return normalize(bitwiseAnd((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.LogicalAnd:
			return normalize(and((Function<Object[], Boolean>) first,
					(Function<Object[], Boolean>) second));
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
			return iif((Function<Object[], Boolean>) e.getOperator()
					.apply(this), first, second);
		case ExpressionType.Divide:
			return normalize(divide((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.Equal:
			return normalize(equal(first, second));
		case ExpressionType.ExclusiveOr:
			return normalize(xor((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.GreaterThan:
			return normalize(greaterThan((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.GreaterThanOrEqual:
			return normalize(greaterThanOrEqual(
					(Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.LeftShift:
			return normalize(shiftLeft((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.LessThan:
			return normalize(lessThan((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.LessThanOrEqual:
			return normalize(lessThanOrEqual(
					(Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.Modulo:
			return normalize(modulo((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.Multiply:
			return normalize(multiply((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.NotEqual:
			return normalize(equal((Function<Object[], Number>) first,
					(Function<Object[], Number>) second).negate());
		case ExpressionType.BitwiseOr:
			return normalize(bitwiseOr((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.LogicalOr:
			return normalize(or((Function<Object[], Boolean>) first,
					(Function<Object[], Boolean>) second));
			// case ExpressionType.Power:
			// return power((Function<Number, Object[]>) first,
			// (Function<Number, Object[]>) second);
		case ExpressionType.RightShift:
			return normalize(shiftRight((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.Subtract:
			return normalize(subtract((Function<Object[], Number>) first,
					(Function<Object[], Number>) second));
		case ExpressionType.InstanceOf:
			return normalize(instanceOf(first, (Class<?>) second.apply(null)));
		default:
			throw new IllegalArgumentException(ExpressionType.toString(e
					.getExpressionType()));
		}
	}

	@Override
	public Function<Object[], ?> visit(ConstantExpression e) {
		return constant(e.getValue());
	}

	@Override
	public Function<Object[], ?> visit(InvocationExpression e) {

		final Function<Object[], ?> m = e.getMethod().apply(this);

		int size = e.getArguments().size();
		List<Function<Object[], ?>> ppe = new ArrayList<>(size);
		for (Expression p : e.getArguments())
			ppe.add(p.apply(this));

		Function<Object[], Object[]> params = pp -> {
			Object[] r = new Object[ppe.size()];
			int index = 0;
			for (Function<Object[], ?> pe : ppe) {
				r[index++] = pe.apply(pp);
			}

			return r;
		};

		return m.compose(params);
	}

	@Override
	public Function<Object[], ?> visit(LambdaExpression<?> e) {

		final Function<Object[], ?> f = e.getBody().apply(this);

		int size = e.getParameters().size();
		List<Function<Object[], ?>> ppe = new ArrayList<>(size);
		for (ParameterExpression p : e.getParameters())
			ppe.add(p.apply(this));

		Function<Object[], Object[]> params = pp -> {
			Object[] r = new Object[ppe.size()];
			int index = 0;
			for (Function<Object[], ?> pe : ppe) {
				r[index++] = pe.apply(pp);
			}
			return r;
		};

		return f.compose(params);
	}

	@Override
	public Function<Object[], ?> visit(MemberExpression e) {
		final Member m = e.getMember();
		Expression ei = e.getInstance();
		final Function<Object[], ?> instance = ei != null ? ei.apply(this)
				: null;

		int size = e.getParameters().size();
		List<Function<Object[], ?>> ppe = new ArrayList<>(size);
		for (ParameterExpression p : e.getParameters())
			ppe.add(p.apply(this));

		Function<Object[], Object[]> params = pp -> {
			Object[] r = new Object[ppe.size()];
			int index = 0;
			for (Function<Object[], ?> pe : ppe) {
				r[index++] = pe.apply(pp);
			}

			return r;
		};

		Function<Object[], ?> field = t -> {
			try {
				return ((Field) m).get(instance == null ? null : instance
						.apply(t));
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
				Object[] pp = params.apply(t);
				return ((Method) m).invoke(inst, pp);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		};

		Function<Object[], ?> ctor = t -> {
			try {
				return ((Constructor<?>) m).newInstance(params.apply(t));
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException ex) {
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

		return member;// .compose(params);
	}

	@Override
	public Function<Object[], ?> visit(ParameterExpression e) {
		final int index = e.getIndex();
		return t -> t[index];
	}

	@SuppressWarnings("unchecked")
	@Override
	public Function<Object[], ?> visit(UnaryExpression e) {
		final Function<Object[], ?> first = e.getFirst().apply(this);
		switch (e.getExpressionType()) {
		case ExpressionType.ArrayLength:
			return t -> Array.getLength(first.apply(t));
		case ExpressionType.BitwiseNot:
			return bitwiseNot((Function<Object[], Number>) first);
		case ExpressionType.Convert:
			final Class<?> to = e.getResultType();
			if (to.isPrimitive() || Number.class.isAssignableFrom(to))
				return t -> {
					Object source = first.apply(t);
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
					return to.cast(result);
				};

			return first;
		case ExpressionType.IsNull:
			return first.andThen(r -> r == null);
		case ExpressionType.LogicalNot:
			return normalize(not((Function<Object[], Boolean>) first));
		case ExpressionType.Negate:
			return (Function<Object[], ?>) negate((Function<Object[], Number>) first);
		case ExpressionType.Quote:
			return constant(first);
			// case ExpressionType.UnaryPlus:
			// return abs((Function<? extends Number, Object[]>) first);
		default:
			throw new IllegalArgumentException(ExpressionType.toString(e
					.getExpressionType()));
		}
	}
}
