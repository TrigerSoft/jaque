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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Default expression visitor implementation.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public abstract class SimpleExpressionVisitor implements ExpressionVisitor<Expression> {

	private Deque<List<Expression>> argumentsStack = new ArrayDeque<>();

	protected List<Expression> getContextArguments() {
		return argumentsStack.peek();
	}

	protected <T extends Expression> List<T> visitExpressionList(List<T> original) {
		if (original != null) {
			List<T> list = null;
			for (int i = 0, n = original.size(); i < n; i++) {
				@SuppressWarnings("unchecked")
				T p = (T) original.get(i).accept(this);
				if (list != null) {
					list.add(p);
				} else if (p != original.get(i)) {
					list = new ArrayList<>(n);
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

	protected List<Expression> visitArguments(List<Expression> original) {
		return visitExpressionList(original);
	}

	protected List<ParameterExpression> visitParameters(List<ParameterExpression> original) {
		return visitExpressionList(original);
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
			return Expression.binary(e.getExpressionType(), visitedOp, visitedFirst, visitedSecond);

		return e;
	}

	@Override
	public Expression visit(ConstantExpression e) {
		return e;
	}

	@Override
	public Expression visit(InvocationExpression e) {
		List<Expression> arguments = e.getArguments();
		Expression target = e.getTarget();
		boolean visitTargetWithOldArgs = target.getExpressionType() == ExpressionType.MethodAccess || target.getExpressionType() == ExpressionType.Delegate;
		boolean cleanArgsStack = false;
		if (!visitTargetWithOldArgs) {
			argumentsStack.push(arguments);
			cleanArgsStack = true;
		}
		try {
			target.accept(this);
			if (visitTargetWithOldArgs) {
				argumentsStack.push(arguments);
				cleanArgsStack = true;
			}
			List<Expression> args = visitArguments(arguments);
			if (args != e.getArguments() || target != e.getTarget()) {
				return Expression.invoke((InvocableExpression) target, args);
			}
			return e;
		} finally {
			if (cleanArgsStack)
				argumentsStack.pop();
		}
	}

	@Override
	public Expression visit(LambdaExpression<?> e) {
		Expression body = e.getBody().accept(this);
		List<ParameterExpression> parameters = visitParameters(e.getParameters());
		if (body != e.getBody())
			return Expression.lambda(e.getResultType(), body, parameters);

		return e;
	}

	@Override
	public Expression visit(DelegateExpression e) {
		Expression delegate = e.getDelegate().accept(this);
		Object result = delegate.accept(Interpreter.Instance).apply(argumentsStack.peek().toArray());
		if (result instanceof ConstantExpression) {
			Object value = ((ConstantExpression) result).getValue();
			if (value instanceof Expression)
				((Expression) value).accept(this);
		}

		List<ParameterExpression> parameters = visitParameters(e.getParameters());
		if (delegate != e.getDelegate())
			return Expression.delegate(e.getResultType(), delegate, parameters);

		return e;
	}

	@Override
	public Expression visit(MemberExpression e) {
		Expression instance = e.getInstance();
		if (instance != null) {
			instance = instance.accept(this);
			if (instance != e.getInstance())
				return Expression.member(e.getExpressionType(), instance, e.getMember(), e.getResultType(), visitParameters(e.getParameters()));
		}

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
			return Expression.unary(e.getExpressionType(), e.getResultType(), visitedOp);

		return e;
	}

}
