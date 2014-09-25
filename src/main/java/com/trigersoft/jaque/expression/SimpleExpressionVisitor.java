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

import java.util.*;

/**
 * Default expression visitor implementation.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin
 *         Triger</a>
 */

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
