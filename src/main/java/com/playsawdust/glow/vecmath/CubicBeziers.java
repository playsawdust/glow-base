/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.vecmath;

/**
 * <p>A cubic bezier curve is one where the first and fourth points are "real" points which the curve passes through,
 * but the middle two points are "control" points that describe the shape of the curve between the two endpoints. We
 * will refer to these points 'a', 'b', 'c', and 'd', and imaginary lines from one to the next as 'ab', 'bc', and 'cd'.
 * 
 * <p>At point 'a', the curve must be tangent to the imaginary line 'ab'. At point 'd', the curve must be tangent to
 * 'cd'. The curve is of order 3, meaning it can be represented by a sum of polynomials of order 3
 * (ax^3 + bx^2 + cx + d). It gets a bit weird though, because these polynomials are phrased in terms of 't', an
 * interpolant that ranges from 0 to 1 inclusive. Think of 't' as "progress from a to b". If you call
 * {@code valueAt(a,b,c,d,0)}, you will get the value of 'a'. If you call {@code valueAt(a,b,c,d,1)}, you will get the
 * value of 'd'. If you're familiar with linear interpolation, you'll see a very similar thing happening: points along
 * the line are described as a weighted average of its endpoints. Same thing here: when you see sums of weights, those
 * weights will add up to 1 along every point in the curve.
 * 
 * <p>Special thanks to Freya Holmer for https://www.youtube.com/watch?v=aVwxzDHniEw - her work was absolutely essential
 * to the creation of this class.
 */
public class CubicBeziers {
	
	/**
	 * Gets a point on the curve.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The specified point on the curve.
	 */
	public static double valueAt(double a, double b, double c, double d, double t) {
		return
				a * (  -t*t*t + 3*t*t - 3*t + 1) +
				b * ( 3*t*t*t - 6*t*t + 3*t    ) +
				c * (-3*t*t*t + 3*t*t          ) +
				d * (   t*t*t                  );
	}
	
	/**
	 * Gets the curve's first derivative with respect to t, at t. This represents the "velocity" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'd'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The first derivative at the specified point on the curve.
	 */
	public static double firstDerivative(double a, double b, double c, double d, double t) {
		return
				a * (-3*t*t +  6*t - 3) +
				b * ( 9*t*t - 12*t + 3) +
				c * (-9*t*t +  6*t    ) +
				d * ( 3*t*t           );
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "acceleration" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'd'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The second derivative at the specified point on the curve.
	 */
	public static double secondDerivative(double a, double b, double c, double d, double t) {
		return
				a * ( -6*t +  6) +
				b * ( 18*t - 12) +
				c * (-18*t +  6) +
				d * (  6*t     );
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "jolt" vector, or the change in
	 * acceleration an object would need to adopt in order to follow the curve's contour going from 'a' to 'd'. Note
	 * that cubic beziers can only have constant jolt, so this value does not depend on 't'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @return The third derivative of the curve.
	 */
	public static double thirdDerivative(double a, double b, double c, double d) {
		return
				a *  -6 +
				b *  18 +
				c * -18 +
				d *   6;
	}
	
	/**
	 * Gets a point on the curve.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The specified point on the curve.
	 */
	public static Vector2d valueAt(Vector2d a, Vector2d b, Vector2d c, Vector2d d, double t) {
		return new Vector2d(
				valueAt(a.x(), b.x(), c.x(), d.x(), t),
				valueAt(a.y(), b.y(), c.y(), d.y(), t)
				);
	}
	
	/**
	 * Gets the curve's first derivative with respect to t, at t. This represents the "velocity" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'd'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The first derivative at the specified point on the curve.
	 */
	public static Vector2d firstDerivative(Vector2d a, Vector2d b, Vector2d c, Vector2d d, double t) {
		return new Vector2d(
				firstDerivative(a.x(), b.x(), c.x(), d.x(), t),
				firstDerivative(a.y(), b.y(), c.y(), d.y(), t)
				);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "acceleration" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'd'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The second derivative at the specified point on the curve.
	 */
	public static Vector2d secondDerivative(Vector2d a, Vector2d b, Vector2d c, Vector2d d, double t) {
		return new Vector2d(
				secondDerivative(a.x(), b.x(), c.x(), d.x(), t),
				secondDerivative(a.y(), b.y(), c.y(), d.y(), t)
				);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "jolt" vector, or the change in
	 * acceleration an object would need to adopt in order to follow the curve's contour going from 'a' to 'd'. Note
	 * that cubic beziers can only have constant jolt, so this value does not depend on 't'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @return The third derivative of the curve.
	 */
	public static Vector2d thirdDerivative(Vector2d a, Vector2d b, Vector2d c, Vector2d d) {
		return new Vector2d(
				thirdDerivative(a.x(), b.x(), c.x(), d.x()),
				thirdDerivative(a.y(), b.y(), c.y(), d.y())
				);
	}
	
	/**
	 * Gets a point on the curve.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The specified point on the curve.
	 */
	public static Vector3d valueAt(Vector3d a, Vector3d b, Vector3d c, Vector3d d, double t) {
		return new Vector3d(
				valueAt(a.x(), b.x(), c.x(), d.x(), t),
				valueAt(a.y(), b.y(), c.y(), d.y(), t),
				valueAt(a.z(), b.z(), c.z(), d.z(), t)
				);
	}
	
	/**
	 * Gets the curve's first derivative with respect to t, at t. This represents the "velocity" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'd'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The first derivative at the specified point on the curve.
	 */
	public static Vector3d firstDerivative(Vector3d a, Vector3d b, Vector3d c, Vector3d d, double t) {
		return new Vector3d(
				firstDerivative(a.x(), b.x(), c.x(), d.x(), t),
				firstDerivative(a.y(), b.y(), c.y(), d.y(), t),
				firstDerivative(a.z(), b.z(), c.z(), d.z(), t)
				);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "acceleration" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'd'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The second derivative at the specified point on the curve.
	 */
	public static Vector3d secondDerivative(Vector3d a, Vector3d b, Vector3d c, Vector3d d, double t) {
		return new Vector3d(
				secondDerivative(a.x(), b.x(), c.x(), d.x(), t),
				secondDerivative(a.y(), b.y(), c.y(), d.y(), t),
				secondDerivative(a.z(), b.z(), c.z(), d.z(), t)
				);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "jolt" vector, or the change in
	 * acceleration an object would need to adopt in order to follow the curve's contour going from 'a' to 'd'. Note
	 * that cubic beziers can only have constant jolt, so this value does not depend on 't'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @return The third derivative of the curve.
	 */
	public static Vector3d thirdDerivative(Vector3d a, Vector3d b, Vector3d c, Vector3d d) {
		return new Vector3d(
				thirdDerivative(a.x(), b.x(), c.x(), d.x()),
				thirdDerivative(a.y(), b.y(), c.y(), d.y()),
				thirdDerivative(a.z(), b.z(), c.z(), d.z())
				);
	}
	
	/**
	 * Gets a point on the curve.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The specified point on the curve.
	 */
	public static Vector4d valueAt(Vector4d a, Vector4d b, Vector4d c, Vector4d d, double t) {
		return new Vector4d(
				valueAt(a.x(), b.x(), c.x(), d.x(), t),
				valueAt(a.y(), b.y(), c.y(), d.y(), t),
				valueAt(a.z(), b.z(), c.z(), d.z(), t),
				valueAt(a.w(), b.w(), c.w(), d.w(), t)
				);
	}
	
	/**
	 * Gets the curve's first derivative with respect to t, at t. This represents the "velocity" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'd'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The first derivative at the specified point on the curve.
	 */
	public static Vector4d firstDerivative(Vector4d a, Vector4d b, Vector4d c, Vector4d d, double t) {
		return new Vector4d(
				firstDerivative(a.x(), b.x(), c.x(), d.x(), t),
				firstDerivative(a.y(), b.y(), c.y(), d.y(), t),
				firstDerivative(a.z(), b.z(), c.z(), d.z(), t),
				firstDerivative(a.w(), b.w(), c.w(), d.w(), t)
				);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "acceleration" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'd'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The second derivative at the specified point on the curve.
	 */
	public static Vector4d secondDerivative(Vector4d a, Vector4d b, Vector4d c, Vector4d d, double t) {
		return new Vector4d(
				secondDerivative(a.x(), b.x(), c.x(), d.x(), t),
				secondDerivative(a.y(), b.y(), c.y(), d.y(), t),
				secondDerivative(a.z(), b.z(), c.z(), d.z(), t),
				secondDerivative(a.w(), b.w(), c.w(), d.w(), t)
				);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "jolt" vector, or the change in
	 * acceleration an object would need to adopt in order to follow the curve's contour going from 'a' to 'd'. Note
	 * that cubic beziers can only have constant jolt, so this value does not depend on 't'.
	 * @param a The first point on the curve.
	 * @param b The first control point in the curve.
	 * @param c The second control point in the curve.
	 * @param d The last point on the curve.
	 * @return The third derivative of the curve.
	 */
	public static Vector4d thirdDerivative(Vector4d a, Vector4d b, Vector4d c, Vector4d d) {
		return new Vector4d(
				thirdDerivative(a.x(), b.x(), c.x(), d.x()),
				thirdDerivative(a.y(), b.y(), c.y(), d.y()),
				thirdDerivative(a.z(), b.z(), c.z(), d.z()),
				thirdDerivative(a.w(), b.w(), c.w(), d.w())
				);
	}
}
