/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.function;

/**
 * Represents an operation upon two float-valued operands and producing a float-valued result. This is a primitive type
 * specialization of {@link java.util.function.BinaryOperator BinaryOperator} for float.
 * 
 * <p>Java avoided making this for bloat reasons (see
 * <a href="https://mail.openjdk.org/pipermail/lambda-dev/2012-November/006750.html">the mailing list</a>),
 * and normally it's better to use {@link java.util.function.DoubleBinaryOperator DoubleBinaryOperator} instead, but for
 * color math we happened to have a strong use-case for it.
 */

@FunctionalInterface
public interface FloatBinaryOperator {
	float applyAsFloat(float left, float right);
	
	/**
	 * A FloatBinaryOperator which always returns the first (left) operand.
	 */
	public static FloatBinaryOperator LEFT_OPERAND = (left, right) -> left;
	
	/**
	 * A FloatBinaryOperator which always returns the second (right) operand.
	 */
	public static FloatBinaryOperator RIGHT_OPERAND = (left, right) -> right;
}
