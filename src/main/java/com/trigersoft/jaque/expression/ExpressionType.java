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

/**
 * Describes the node types for the nodes of an expression tree.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */
public final class ExpressionType {
	private ExpressionType() {
	}

	/**
	 * A node that represents arithmetic addition without overflow checking.
	 */
	public static final int Add = 0;
	// AddChecked,
	/**
	 * A node that represents a bitwise AND operation.
	 */
	public static final int BitwiseAnd = Add + 1;
	/**
	 * A node that represents a short-circuiting conditional AND operation.
	 */
	public static final int LogicalAnd = BitwiseAnd + 1;
	/**
	 * A node that represents indexing into an array.
	 */
	public static final int ArrayIndex = LogicalAnd + 1;
	/**
	 * A node that represents getting the length of an array.
	 */
	public static final int ArrayLength = ArrayIndex + 1;
	// Call,
	/**
	 * A node that represents a null coalescing operation.
	 */
	public static final int Coalesce = ArrayLength + 1;
	/**
	 * A node that represents a conditional operation.
	 */
	public static final int Conditional = Coalesce + 1;
	/**
	 * A node that represents an expression that has a constant value.
	 */
	public static final int Constant = Conditional + 1;
	/**
	 * A node that represents a cast or conversion operation. If the operation
	 * is a numeric conversion, it overflows silently if the converted value
	 * does not fit the target type.
	 */
	public static final int Convert = Constant + 1;
	// ConvertChecked,
	/**
	 * A node that represents arithmetic division.
	 */
	public static final int Divide = Convert + 1;
	/**
	 * A node that represents an equality comparison.
	 */
	public static final int Equal = Divide + 1;
	/**
	 * A node that represents a bitwise XOR operation.
	 */
	public static final int ExclusiveOr = Equal + 1;
	/**
	 * A node that represents a "greater than" numeric comparison.
	 */
	public static final int GreaterThan = ExclusiveOr + 1;
	/**
	 * A node that represents a "greater than or equal" numeric comparison.
	 */
	public static final int GreaterThanOrEqual = GreaterThan + 1;
	/**
	 * A node that represents applying a delegate or lambda expression to a list
	 * of argument expressions.
	 */
	public static final int Invoke = GreaterThanOrEqual + 1;
	/**
	 * A node that represents a null test.
	 */
	public static final int IsNull = Invoke + 1;
	/**
	 * A node that represents a lambda expression.
	 */
	public static final int Lambda = IsNull + 1;
	/**
	 * A node that represents a bitwise left-shift operation.
	 */
	public static final int LeftShift = Lambda + 1;
	/**
	 * A node that represents a "less than" numeric comparison.
	 */
	public static final int LessThan = LeftShift + 1;
	/**
	 * A node that represents a "less than or equal" numeric comparison.
	 */
	public static final int LessThanOrEqual = LessThan + 1;
	// ListInit,
	/**
	 * A node that represents reading from a field.
	 */
	public static final int FieldAccess = LessThanOrEqual + 1;
	// MemberInit,
	/**
	 * A node that represents a method call.
	 */
	public static final int MethodAccess = FieldAccess + 1;
	/**
	 * A node that represents an arithmetic remainder operation.
	 */
	public static final int Modulo = MethodAccess + 1;
	/**
	 * A node that represents arithmetic multiplication without overflow
	 * checking.
	 */
	public static final int Multiply = Modulo + 1;
	// MultiplyChecked,
	/**
	 * A node that represents an arithmetic negation operation.
	 */
	public static final int Negate = Multiply + 1;
	// NegateChecked,
	/**
	 * A node that represents calling a constructor to create a new object.
	 */
	public static final int New = Negate + 1;
	// NewArrayInit,
	// NewArrayBounds,
	/**
	 * A node that represents a bitwise complement operation.
	 */
	public static final int BitwiseNot = New + 1;
	/**
	 * A node that represents a logical NOT operation.
	 */
	public static final int LogicalNot = BitwiseNot + 1;
	/**
	 * A node that represents an inequality comparison.
	 */
	public static final int NotEqual = LogicalNot + 1;
	/**
	 * A node that represents a bitwise OR operation.
	 */
	public static final int BitwiseOr = NotEqual + 1;
	/**
	 * A node that represents a short-circuiting conditional OR operation.
	 */
	public static final int LogicalOr = BitwiseOr + 1;
	/**
	 * A node that represents a parameter index defined in the context of the
	 * expression.
	 */
	public static final int Parameter = LogicalOr + 1;
	// /**
	// * A node that represents raising a number to a power.
	// */
	// public static final int Power = Parameter + 1;
	/**
	 * A node that represents an expression that has a constant value of type
	 * Expression. A Quote node can contain references to parameters defined in
	 * the context of the expression it represents.
	 */
	public static final int Quote = Parameter + 1;
	/**
	 * A node that represents a bitwise right-shift operation.
	 */
	public static final int RightShift = Quote + 1;
	/**
	 * A node that represents arithmetic subtraction without overflow checking.
	 */
	public static final int Subtract = RightShift + 1;
	// SubtractChecked,
	/**
	 * A node that represents a type test.
	 */
	public static final int InstanceOf = Subtract + 1;
	// /**
	// * A node that represents an arithmetic absolute value operation.
	// */
	// public static final int UnaryPlus = InstanceOf + 1;
	/**
	 * Holds the maximum expression type value.
	 */
	public static final int MaxExpressionTypeValue = InstanceOf;

	/**
	 * @param expressionType
	 *            Expression type.
	 * @return Expression type's string representation.
	 */
	public static String toString(int expressionType) {
		switch (expressionType) {
		case Add:
			return "+";
		case BitwiseAnd:
			return "&";
		case LogicalAnd:
			return "&&";
		case ArrayIndex:
			return "[]";
		case ArrayLength:
			return "#";
		case Coalesce:
			return "??";
		case Conditional:
			return "?";
			// case Constant:
			// case Convert:
		case Divide:
			return "/";
		case Equal:
			return "==";
		case ExclusiveOr:
			return "^";
		case GreaterThan:
			return ">";
		case GreaterThanOrEqual:
			return ">=";
			// case Invoke:
		case IsNull:
			return "(Is Null)";
			// case Lambda:
		case LeftShift:
			return "<<";
		case LessThan:
			return "<";
		case LessThanOrEqual:
			return "<=";
			// case FieldAccess:
			// case MethodAccess:
		case Modulo:
			return "%";
		case Multiply:
			return "*";
		case Negate:
			return "-";
		case BitwiseNot:
			return "~";
		case LogicalNot:
			return "!";
		case NotEqual:
			return "!=";
		case BitwiseOr:
			return "|";
		case LogicalOr:
			return "||";
			// case Parameter:
		case Quote:
			return "";
			// case Power:
			// return "^^";
		case RightShift:
			return ">>";
		case Subtract:
			return "-";
		case InstanceOf:
			return "instanceof";
			// case UnaryPlus:
			// return "+";
		default:
			return Integer.toString(expressionType);
		}
	}
}
