package com.trigersoft.jaque.expression;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InvocationExpressionTest {

    @Test
    public void testParameterReplacementInToString() throws NoSuchMethodException, SecurityException {

        Expression firstInvocation = Expression.invoke(Expression.constant("Hello World"),
                String.class.getMethod("replace", CharSequence.class, CharSequence.class),
                Expression.parameter(String.class, 0), Expression.constant("Eddie"));

        assertEquals("Hello World.replace(P0, Eddie)", firstInvocation.toString());

        Expression secondInvocation = Expression.invoke(firstInvocation,
                String.class.getMethod("replace", CharSequence.class, CharSequence.class), Expression.constant("foo"),
                Expression.constant("bar"));

        assertEquals("Hello World.replace(P0, Eddie).replace(foo, bar)", secondInvocation.toString());
    }
}
