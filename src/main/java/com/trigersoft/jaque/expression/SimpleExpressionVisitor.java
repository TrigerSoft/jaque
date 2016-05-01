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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Expression visitor visiting all nested expressions and creating the visited expression again. 
 */

public abstract class SimpleExpressionVisitor implements
		ExpressionVisitor<Expression> {

	protected List<Expression> visitExpressionList(List<Expression> original) {
		if (original != null) {
			List<Expression> list = null;
			for (int i = 0, n = original.size(); i < n; i++) {
				Expression p = original.get(i).accept(this);
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
	
	@Override
	public Expression visit(ThisExpression e) {
		return e;
	}

	@Override
	public Expression visit(BinaryExpression e) {
		Expression first = e.getFirst();
		Expression visitedFirst = first.accept(this);

		Expression second = e.getSecond();
		Expression visitedSecond = second.accept(this);

		Expression op = e.getOperator();
		Expression visitedOp = op != null ? op.accept(this) : op;

		if (first != visitedFirst || second != visitedSecond || op != visitedOp)
			return Expression.binary(e.getExpressionType(), visitedOp,
					visitedFirst, visitedSecond);

		return e;
	}

	@Override
	public Expression visit(ConstantExpression e) {
		return e;
	}


	@Override
	public Expression visit(MemberExpression e) {
		Expression instance = e.getInstance();
		if (instance != null) {
			instance = instance.accept(this);
		}
		List<Expression> arguments = visitExpressionList(e.getArguments());

		if (instance != e.getInstance() || arguments != e.getArguments())
			return Expression.member(e.getExpressionType(), instance, e.getMember(), e.getResultType(),
					e.getParameterTypes(), arguments);

		return e;
	}

	@Override
	public Expression visit(ParameterExpression e) {
		return e;
	}

	@Override
	public Expression visit(UnaryExpression e) {
		Expression operand = e.getFirst();
		Expression visitedOp = operand.accept(this);
		if (operand != visitedOp)
			return Expression.unary(e.getExpressionType(), e.getResultType(),
					visitedOp);

		return e;
	}

	@Override
	public Expression visit(LambdaInvocationExpression e) {
		Expression instance = e.getInstance().accept(this);
		List<Expression> arguments = visitExpressionList(e.getArguments());
		if (instance!=e.getInstance() || arguments!=e.getArguments()){
			return Expression.invokeLambda(e.getParameterTypes(), instance, arguments);
		}
		return e;
	}
}
