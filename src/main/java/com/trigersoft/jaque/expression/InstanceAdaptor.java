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

import java.util.List;

/**
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

final class InstanceAdaptor extends SimpleExpressionVisitor {
	private final List<Expression> args;

	private InstanceAdaptor(List<Expression> args) {
		this.args = args;
	}

	static InvocableExpression normalize(InvocableExpression e,
			List<Expression> args) {

		InstanceAdaptor parameterNormalizer = new InstanceAdaptor(args);
		return (InvocableExpression) e.accept(parameterNormalizer);
	}

	@Override
	public Expression visit(ParameterExpression e) {
		int index = e.getIndex();
		if (index >= args.size())
			return e;
		Expression x = args.get(index);
		if (x instanceof ParameterExpression
				&& ((ParameterExpression) x).getIndex() == e.getIndex())
			return e;
		return x;
	}

	@Override
	public Expression visit(InvocationExpression e) {
		return e;
	}
}
