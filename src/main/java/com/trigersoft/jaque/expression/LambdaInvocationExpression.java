package com.trigersoft.jaque.expression;

import static java.util.stream.Collectors.joining;

import java.util.List;

/**
 * Invocation of an expression as lambda expression.
 */
public class LambdaInvocationExpression extends InvocationExpression{

	protected LambdaInvocationExpression(Expression target,  List<Class<?>> paramTypes, List<Expression> arguments) {
		super(ExpressionType.Invoke, target, target.getResultType(), paramTypes, arguments);
	}

	@Override
	protected <T> T visit(ExpressionVisitor<T> v) {
		return v.visit(this);
	}
	
	@Override
	public String toString() {
		return getInstance().toString() + "(" + getArguments().stream().map(Object::toString).collect(joining(","))
				+ ")";
	}

}
