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

package com.trigersoft.jaque.expression;

import java.util.*;

public abstract class SimpleExpressionVisitor implements
		ExpressionVisitor<Expression> {

	protected static Expression stripQuotes(Expression e) {
		while (e.getExpressionType() == ExpressionType.Quote)
			e = ((UnaryExpression) e).getFirst();

		return e;
	}

	protected List<Expression> visitExpressionList(List<Expression> original) {
		if (original != null) {
			List<Expression> list = null;
			for (int i = 0, n = original.size(); i < n; i++) {
				Expression p = original.get(i).apply(this);
				if (list != null) {
					list.add(p);
				} else if (p != original.get(i)) {
					list = new ArrayList<Expression>(n);
					for (int j = 0; j < i; j++) {
						list.add(original.get(j));
					}
					list.add(p);
				}
			}
			if (list != null) {
				return Collections.unmodifiableList(list);
			}
		}
		return original;
	}

	public Expression visit(BinaryExpression e) {
		Expression first = e.getFirst();
		Expression visitedFirst = first.apply(this);

		Expression second = e.getSecond();
		Expression visitedSecond = second.apply(this);

		Expression op = e.getOperator();
		Expression visitedOp = op != null ? op.apply(this) : op;

		if (first != visitedFirst || second != visitedSecond || op != visitedOp)
			return Expression.binary(e.getExpressionType(), visitedOp,
					visitedFirst, visitedSecond);

		return e;
	}

	public Expression visit(ConstantExpression e) {
		return e;
	}

	public Expression visit(InvocationExpression e) {
		Expression expr = e.getMethod().apply(this);
		List<Expression> args = visitExpressionList(e.getArguments());
		if (args != e.getArguments() || expr != e.getMethod()) {
			return Expression.invoke((InvocableExpression) expr, args);
		}
		return e;
	}

	public Expression visit(LambdaExpression<?> e) {
		Expression body = e.getBody().apply(this);
		if (body != e.getBody())
			return Expression
					.lambda(e.getResultType(), body, e.getParameters());

		return e;
	}

	public Expression visit(MemberExpression e) {
		Expression instance = e.getInstance();
		if (instance != null) {
			instance = instance.apply(this);
			if (instance != e.getInstance())
				return Expression.member(e.getExpressionType(), instance, e
						.getMember(), e.getResultType(), e.getParameters());
		}

		return e;
	}

	public Expression visit(ParameterExpression e) {
		return e;
	}

	public Expression visit(UnaryExpression e) {
		Expression operand = e.getFirst();
		Expression visitedOp = operand.apply(this);
		if (operand != visitedOp)
			return Expression.unary(e.getExpressionType(), e.getResultType(),
					visitedOp);

		return e;
	}

}
