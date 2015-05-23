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

package com.trigersoft.jaque.expressions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.Test;

import com.trigersoft.jaque.Customer;
import com.trigersoft.jaque.Fluent;
import com.trigersoft.jaque.Person;
import com.trigersoft.jaque.expression.Expression;
import com.trigersoft.jaque.expression.LambdaExpression;

public class LambdaExpressionTest {

	public interface SerializablePredicate<T> extends Predicate<T>,
			Serializable {

	}

	public interface SerializableFunction<T, R> extends Function<T, R>,
			Serializable {
	}

	private static <T> Predicate<T> ensureSerializable(
			SerializablePredicate<T> x) {
		return x;
	}

	// @Test
	// public void testGetBody() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetParameters() {
	// fail("Not yet implemented");
	// }

	@Test
	public void testParseNew() throws Throwable {
		Predicate<java.util.Date> pp1 = new Predicate<Date>() {

			@Override
			public boolean test(Date t) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		Class<? extends Predicate> class1 = pp1.getClass();
		class1.getName();
		Predicate<java.util.Date> pp = d -> d.after(new java.sql.Time(System
				.currentTimeMillis()));
		LambdaExpression<Predicate<java.util.Date>> le = LambdaExpression
				.parse(pp);
		Function<Object[], ?> fr = le.compile();

		le.toString();

		Date anotherDate = new Date(System.currentTimeMillis() + 1000);
		assertEquals(pp.test(anotherDate),
				fr.apply(new Object[] { anotherDate }));

		pp = d -> d.compareTo(anotherDate) < 10;
		le = LambdaExpression.parse(pp);

		fr = le.compile();

		Date date = new Date();
		assertEquals(pp.test(date), fr.apply(new Object[] { date }));
		// Predicate<java.util.Date> le = LambdaExpression.parse(pp);
		// le = LambdaExpression.parse(pp).compile();
		//
		// assertTrue(le.invoke(new java.sql.Date(System.currentTimeMillis()
		// + (5 * 1000))));
		// assertFalse(le.invoke(new java.sql.Date(System.currentTimeMillis()
		// - (5 * 1000))));
	}

	@Test
	public void testParseP() throws Throwable {
		Predicate<Float> pp = t -> t > 6 ? t < 12 : t > 2;
		LambdaExpression<Predicate<Float>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(4f), le.apply(new Object[] { 4f }));
		assertEquals(pp.test(7f), le.apply(new Object[] { 7f }));
		assertEquals(pp.test(14f), le.apply(new Object[] { 14f }));
		assertEquals(pp.test(12f), le.apply(new Object[] { 12f }));
		assertEquals(pp.test(6f), le.apply(new Object[] { 6f }));
		assertEquals(pp.test(Float.NaN), le.apply(new Object[] { Float.NaN }));
	}

	@Test
	public void testParseP1() throws Throwable {
		Predicate<String> pp = ensureSerializable(t -> t.equals("abc"));
		LambdaExpression<Predicate<String>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test("abc"), le.apply(new Object[] { "abc" }));
		assertEquals(pp.test("abC"), le.apply(new Object[] { "abC" }));
	}

	@Test
	public void testParseP2() throws Throwable {
		final Object[] ar = new Object[] { 5 };

		Predicate<Integer> pp = t -> (ar.length << t) == (1 << 5)
				&& ar[0] instanceof Number;

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression
				.parse(pp);
		parsed.toString();
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.test(4), le.apply(new Object[] { 4 }));
	}

	@Test
	public void testParseThis() throws Throwable {

		Predicate<Integer> pp = t -> this != null;

		LambdaExpression<Predicate<Integer>> lambda = LambdaExpression
				.parse(pp);

		Function<Object[], ?> le = lambda.compile();

		assertEquals(pp.test(5), le.apply(new Object[] { 5 }));
	}

	@Test
	public void testParseP3() throws Throwable {
		final Object[] ar = new Object[] { 5f };

		Predicate<Integer> pp = ensureSerializable(t -> ar[0] instanceof Float
				|| (ar.length << t) == (1 << 5));

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.test(4), le.apply(new Object[] { 4 }));
	}

	@Test
	public void testParseField() throws Throwable {
		Predicate<Object[]> pp = t -> t.length == 3;

		LambdaExpression<Predicate<Object[]>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		Integer[] ar1 = { 2, 3, 4 };
		Integer[] ar2 = { 2, 4 };

		assertEquals(pp.test(ar1), le.apply(new Object[] { ar1 }));
		assertEquals(pp.test(ar2), le.apply(new Object[] { ar2 }));
	}

	@Test
	public void testParse0() throws Throwable {
		Supplier<Float> pp = () -> 23f;

		LambdaExpression<Supplier<Float>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertTrue(23f == (Float) le.apply(null));
		assertFalse(24f == (Float) le.apply(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseIllegal() throws Throwable {

		try {
			final Object[] x = new Object[1];
			Supplier<Float> pp = () -> {
				x[0] = null;
				return 23f;
			};
			LambdaExpression<Supplier<Float>> parsed = LambdaExpression
					.parse(pp);
			Function<Object[], ?> le = parsed.compile();

			le.apply(null);
		} catch (Throwable e) {
			assertTrue(e.getMessage().indexOf("AASTORE") >= 0);
			throw e;
		}
	}

	@Test
	public void testParse2() throws Throwable {
		BiFunction<Float, Float, Boolean> pp = (Float t, Float r) -> t > 6 ? r < 12
				: t > 2;

		LambdaExpression<BiFunction<Float, Float, Boolean>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.apply(7f, 10f), le.apply(new Object[] { 7f, 10f }));
		assertEquals(pp.apply(7f, 14f), le.apply(new Object[] { 7f, 14f }));
	}

	@Test
	public void testParse4() throws Throwable {
		Predicate<Integer> pp = ensureSerializable(r -> (r < 6 ? r > 1 : r < 4)
				|| (r instanceof Number));

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.test(11), le.apply(new Object[] { 11 }));
	}

	@Test
	public void testParse5() throws Throwable {
		Predicate<Integer> pp = r -> (r < 6 ? r > 1 : r < 4)
				|| (r > 25 ? r > 28 : r < 32) || (r < 23 ? r > 15 : r < 17);

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.test(11), le.apply(new Object[] { 11 }));
		assertEquals(pp.test(29), le.apply(new Object[] { 29 }));
		assertEquals(pp.test(26), le.apply(new Object[] { 26 }));
		assertEquals(pp.test(18), le.apply(new Object[] { 18 }));
		assertEquals(pp.test(14), le.apply(new Object[] { 14 }));
	}

	@Test
	public void testParse6() throws Throwable {
		Predicate<Integer> pp = ensureSerializable(r -> (r < 6 ? r > 1 : r < 4)
				&& (r > 25 ? r > 28 : r < 32) || (r < 23 ? r > 15 : r < 17));

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.test(11), le.apply(new Object[] { 11 }));
		assertEquals(pp.test(29), le.apply(new Object[] { 29 }));
		assertEquals(pp.test(26), le.apply(new Object[] { 26 }));
		assertEquals(pp.test(18), le.apply(new Object[] { 18 }));
		assertEquals(pp.test(14), le.apply(new Object[] { 14 }));
	}

	@Test
	public void testParse7() throws Throwable {
		Predicate<Integer> pp = r -> (r < 6 && r > 25) || r < 23;

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.test(11), le.apply(new Object[] { 11 }));
		assertEquals(pp.test(29), le.apply(new Object[] { 29 }));
		assertEquals(pp.test(26), le.apply(new Object[] { 26 }));
		assertEquals(pp.test(18), le.apply(new Object[] { 18 }));
		assertEquals(pp.test(14), le.apply(new Object[] { 14 }));
	}

	@Test
	public void testParse8() throws Throwable {
		Predicate<Integer> pp = r -> (r < 6 || r > 25) && r < 23;

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.test(11), le.apply(new Object[] { 11 }));
		assertEquals(pp.test(29), le.apply(new Object[] { 29 }));
		assertEquals(pp.test(26), le.apply(new Object[] { 26 }));
		assertEquals(pp.test(18), le.apply(new Object[] { 18 }));
		assertEquals(pp.test(14), le.apply(new Object[] { 14 }));
	}

	@Test
	public void testParse9() throws Throwable {
		SerializablePredicate<Integer> pp = r -> (r < 6 || r > 25) && r < 23
				|| r > 25;

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.test(11), le.apply(new Object[] { 11 }));
		assertEquals(pp.test(29), le.apply(new Object[] { 29 }));
		assertEquals(pp.test(26), le.apply(new Object[] { 26 }));
		assertEquals(pp.test(18), le.apply(new Object[] { 18 }));
		assertEquals(pp.test(14), le.apply(new Object[] { 14 }));
	}

	@Test
	public void testParse10() throws Throwable {
		Function<Integer, Integer> pp = r -> ~r;

		LambdaExpression<Function<Integer, Integer>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.apply(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.apply(-10), le.apply(new Object[] { -10 }));
		assertEquals(pp.apply(29), le.apply(new Object[] { 29 }));
		assertEquals(pp.apply(26), le.apply(new Object[] { 26 }));
		assertEquals(pp.apply(-18), le.apply(new Object[] { -18 }));
		assertEquals(pp.apply(14), le.apply(new Object[] { 14 }));
	}

	@Test
	public void testParse11() throws Throwable {
		Function<Integer, Byte> pp = r -> (byte) (int) r;

		LambdaExpression<Function<Integer, Byte>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.apply(5), le.apply(new Object[] { 5 }));
		assertEquals(pp.apply(-10), le.apply(new Object[] { -10 }));
		assertEquals(pp.apply(29), le.apply(new Object[] { 29 }));
		assertEquals(pp.apply(26), le.apply(new Object[] { 26 }));
		assertEquals(pp.apply(-18), le.apply(new Object[] { -18 }));
		assertEquals(pp.apply(144567), le.apply(new Object[] { 144567 }));
		assertEquals(pp.apply(-144567), le.apply(new Object[] { -144567 }));
	}

	@Test
	public void testMethodRef() throws Throwable {
		SerializableFunction<Customer, Integer> pp = Customer::getData;

		LambdaExpression<Function<Customer, Integer>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		Customer c = new Customer(5);

		assertEquals(pp.apply(c), le.apply(new Object[] { c }));

		pp = (Customer c1) -> c1.getData();

		parsed = LambdaExpression.parse(pp);
		le = parsed.compile();

		assertEquals(pp.apply(c), le.apply(new Object[] { c }));

		Fluent<Customer> f = new Fluent<Customer>();
		f.property(Customer::getData);

		assertEquals("public int com.trigersoft.jaque.Customer.getData()",
				f.getMember());

		le = f.getParsed().compile();

		assertEquals(pp.apply(c), le.apply(new Object[] { c }));
	}

	@Test(expected = NullPointerException.class)
	public void testParse12() throws Throwable {
		Function<Integer, Byte> pp = r -> (byte) (int) r;

		LambdaExpression<Function<Integer, Byte>> parsed = LambdaExpression
				.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		le.apply(null);
	}

	@Test
	public void canToStringACompoundExpression() throws Exception {
		SerializableFunction<String, String> e = s -> s.substring(0, 1)
				.toUpperCase();
		Expression body = LambdaExpression.parse(e).getBody();
		assertEquals("P0.substring(0, 1).toUpperCase()", body.toString());
	}

	@Test
	public void canParseAnExpressionWhereCharIsPromotedToIntAsAMethodParameter()
			throws Exception {
		SerializableFunction<String, Integer> e = s -> Math.abs(s.charAt(0));
		LambdaExpression<Function<String, Integer>> parsed = LambdaExpression
				.parse(e);

		Function<Object[], ?> le = parsed.compile();

		assertEquals(e.apply("A"), le.apply(new Object[] { "A" }));
	}

	@Test
	public void canParseAnExpressionWhereCharIsPromotedToLongAsAMethodParameter()
			throws Exception {
		SerializableFunction<String, Long> e = s -> Math
				.abs((long) s.charAt(0));
		LambdaExpression<Function<String, Long>> parsed = LambdaExpression
				.parse(e);

		Function<Object[], ?> le = parsed.compile();

		assertEquals(e.apply("A"), le.apply(new Object[] { "A" }));
	}

	@Test
	public void canParseAnExpressionWhereCharIsPromotedToFloatAsAMethodParameter()
			throws Exception {
		SerializableFunction<String, Float> e = s -> Math.abs((float) s
				.charAt(0));
		LambdaExpression<Function<String, Float>> parsed = LambdaExpression
				.parse(e);

		Function<Object[], ?> le = parsed.compile();

		assertEquals(e.apply("A"), le.apply(new Object[] { "A" }));
	}

	@Test
	public void canParseAnExpressionWhereCharIsPromotedToIntAsAnOperand()
			throws Exception {
		SerializableFunction<String, Integer> e = s -> s.charAt(0) + 1;
		LambdaExpression<Function<String, Integer>> parsed = LambdaExpression
				.parse(e);

		Function<Object[], ?> le = parsed.compile();

		assertEquals(e.apply("A"), le.apply(new Object[] { "A" }));
	}

	@Test
	public void testExpression1() {
		Predicate<Person> p = t -> t.getName() == "Maria Bonita";
		final LambdaExpression<Predicate<Person>> ex = LambdaExpression
				.parse(p);
		assertNotNull(ex);

		Function<Object[], ?> le = ex.compile();

		Person t = new Person();
		t.setName("Maria Bonita");
		assertEquals(p.test(t), le.apply(new Object[] { t }));
	}

	@Test
	public void testExpression2() {
		this.testExpression(t -> t.getName() == "Maria Bonita", "Maria Bonita");
	}

	@Test
	public void testExpression3() {
		final String name = "Maria Bonita";
		this.testExpression(name);
	}

	@Test
	public void testExpression4() {
		this.testExpression("Maria Bonita");
	}

	protected void testExpression(final String name) {
		this.testExpression(t -> t.getName() == name, name);
	}

	protected void testExpression(SerializablePredicate<Person> p, String name) {
		final LambdaExpression<Predicate<Person>> ex = LambdaExpression
				.parse(p);
		assertNotNull(ex);

		Function<Object[], ?> le = ex.compile();

		Person t = new Person();
		t.setName(name);
		assertEquals(p.test(t), le.apply(new Object[] { t }));
	}

	// @Test
	// public void testGetExpressionType() {
	// fail("Not yet implemented");
	// }

	// @Test
	// public void testGetResultType() {
	// fail("Not yet implemented");
	// }

}
