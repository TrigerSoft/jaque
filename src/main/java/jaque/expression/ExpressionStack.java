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

import java.util.*;

import org.objectweb.asm.Label;

@SuppressWarnings("serial")
final class ExpressionStack extends ArrayList<Expression> {

	private BranchExpression _parent;
	private boolean _reduced;

	ExpressionStack() {
		this(null);
	}

	ExpressionStack(BranchExpression parent) {
		_parent = parent;
	}

	BranchExpression getParent() {
		return _parent;
	}

	private void setParent(BranchExpression value) {
		_parent = value;
	}

	boolean isReduced() {
		return _reduced;
	}

	void reduce() {
		_reduced = true;
	}

	void push(Expression item) {
		add(item);
	}

	int getDepth() {
		return getParent() != null ? getParent().getDepth() : 0;
	}

	Expression pop() {
		Expression obj = peek();
		remove(size() - 1);

		return obj;
	}

	Expression peek() {
		Expression obj = get(size() - 1);

		return obj;
	}

	static final class BranchExpression extends Expression {

		private final Expression _test;
		private final ExpressionStack _true;
		private final ExpressionStack _false;
		private final ExpressionStack _parent;

		BranchExpression(ExpressionStack parent, Expression test, Label label) {
			this(parent, test, null, null);
		}

		BranchExpression(ExpressionStack parent, Expression test,
				ExpressionStack trueE, ExpressionStack falseE) {
			super(ExpressionType.Conditional, Void.TYPE);
			_parent = parent;
			_test = test;

			if (trueE != null) {
				_true = trueE;
				_true.setParent(this);
			} else
				_true = new ExpressionStack(this);

			if (falseE != null) {
				_false = falseE;
				_false.setParent(this);
			} else
				_false = new ExpressionStack(this);
		}

		ExpressionStack getTrue() {
			return _true;
		}

		ExpressionStack getFalse() {
			return _false;
		}

		ExpressionStack get(boolean side) {
			return side ? getTrue() : getFalse();
		}

		Expression getTest() {
			return _test;
		}

		ExpressionStack getParent() {
			return _parent;
		}

		int getDepth() {
			return _parent.getDepth() + 1;
		}

		@Override
		protected <T> T visit(ExpressionVisitor<T> v) {
			throw new IllegalStateException();
		}

		@Override
		public String toString() {
			return "(" + getTest().toString() + " ? " + getTrue().toString()
					+ " : " + getFalse().toString() + ")";
		}
	}
}
