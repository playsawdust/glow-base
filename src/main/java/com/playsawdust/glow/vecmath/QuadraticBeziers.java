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
 * A quadratic bezier curve is one where the first and third points are "real" points which the curve passes through,
 * but the middle point is a "control" point that describes the shape of the curve between the two points. We will refer
 * to these points 'a', 'b', and 'c', and imaginary lines from one to the next as 'ab' and 'bc'.
 * 
 * <p>At point 'a', the curve must be tangent to the imaginary line 'ab'. At point 'c', the curve must be tangent to
 * 'bc'. The curve is of order 2, meaning it can be represented by a sum of polynomials of order 2 (ax^2 + bx + c). It
 * gets a bit weird though, because these polynomials are phrased in terms of 't', an interpolant that ranges from 0 to
 * 1 inclusive. Think of 't' as "progress from a to b". If you call {@code valueAt(a,b,c,0)}, you will get the value of
 * 'a'. If you call {@code valueAt(a,b,c,1)}, you will get the value of 'c'. If you're familiar with linear
 * interpolation, you'll see a very similar thing happening: points along the line are described as a weighted average
 * of its endpoints. Same thing here: when you see sums of weights, those weights will add up to 1 along every point in
 * the curve.
 */
public class QuadraticBeziers {
	
	/**
	 * Gets a point on the curve.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The specified point on the curve.
	 */
	public static double valueAt(double a, double b, double c, double t) {
		return
				a * (   t*t - 2*t + 1) +
				b * (-2*t*t + 2*t    ) +
				c * (   t*t);
	}
	
	/**
	 * Gets the curve's first derivative with respect to t, at t. This represents the "velocity" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'c'.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The first derivative at the specified point on the curve.
	 */
	public static double firstDerivative(double a, double b, double c, double t) {
		return
				a * ( 2*t - 2) +
				b * (-4*t + 2) +
				c * ( 2*t);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "acceleration" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'c'. Note that quadratic beziers can only
	 * have constant acceleration, so this value does not depend on 't'.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @return The second derivative of the curve.
	 */
	public static double secondDerivative(double a, double b, double c) {
		return
				a *  2 +
				b * -4 +
				c *  2;
	}
	
	/*
	public static Vector2d boundingBox(Vector3d a, Vector3d b, Vector3d c) {
		double xmin = Math.min(a.x(), c.x());
		double xmax = Math.max(a.x(), c.x());
		double ymin = Math.min(a.y(), c.y());
		double ymax = Math.max(a.y(), c.y());
		double zmin = Math.min(a.z(), c.z());
		double zmax = Math.max(a.z(), c.z());
		
		Vector3d d0 = quadraticDerivative(a, b, c, 0);
		Vector3d d1 = quadraticDerivative(a, b, c, 1);
		
		double xSlope = d1.x() - d0.x(); if (xSlope==0) xSlope = 0.00000000001;
		double ySlope = d1.y() - d0.y(); if (ySlope==0) ySlope = 0.00000000001;
		double zSlope = d1.z() - d0.z(); if (zSlope==0) zSlope = 0.00000000001;
		
		double xInterceptT = -d0.x() / xSlope;
		double yInterceptT = -d0.y() / ySlope;
		double zInterceptT = -d0.z() / zSlope;
		
		if (xInterceptT >= 0 && xInterceptT <= 1) {
			double xSpot = quadratic(a.x(), b.x(), c.x(), xInterceptT);
			if (xSpot<xmin) xmin = xSpot;
			if (xSpot>xmax) xmax = xSpot;
		}
		
		if (yInterceptT >= 0 && yInterceptT <= 1) {
			double ySpot = quadratic(a.y(), b.y(), c.y(), yInterceptT);
			if (ySpot<ymin) ymin = ySpot;
			if (ySpot>ymax) ymax = ySpot;
		}
		
		if (zInterceptT >= 0 && zInterceptT <= 1) {
			double zSpot = quadratic(a.z(), b.z(), c.z(), zInterceptT);
			if (zSpot<zmin) zmin = zSpot;
			if (zSpot>zmax) zmax = zSpot;
		}
		
		return new Rect3d(xmin, ymin, zmin, xmax-xmin, ymax-ymin, zmax-zmin);
	}*/
	
	/**
	 * Gets a point on the curve.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The specified point on the curve.
	 */
	public static Vector2d valueAt(Vector2d a, Vector2d b, Vector2d c, double t) {
		return new Vector2d(
				valueAt(a.x(), b.x(), c.x(), t),
				valueAt(a.y(), b.y(), c.y(), t)
				);
	}
	
	/**
	 * Gets the curve's first derivative with respect to t, at t. This represents the "velocity" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'c'.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The first derivative at the specified point on the curve.
	 */
	public static Vector2d firstDerivative(Vector2d a, Vector2d b, Vector2d c, double t) {
		return new Vector2d(
				firstDerivative(a.x(), b.x(), c.x(), t),
				firstDerivative(a.y(), b.y(), c.y(), t)
				);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "acceleration" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'c'. Note that quadratic beziers can only
	 * have constant acceleration, so this value does not depend on 't'.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @return The second derivative of the curve.
	 */
	public static Vector2d secondDerivative(Vector2d a, Vector2d b, Vector2d c) {
		return new Vector2d(
				secondDerivative(a.x(), b.x(), c.x()),
				secondDerivative(a.y(), b.y(), c.y())
				);
	}
	
	/**
	 * Gets a point on the curve.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The specified point on the curve.
	 */
	public static Vector3d valueAt(Vector3d a, Vector3d b, Vector3d c, double t) {
		return new Vector3d(
				valueAt(a.x(), b.x(), c.x(), t),
				valueAt(a.y(), b.y(), c.y(), t),
				valueAt(a.z(), b.z(), c.z(), t)
				);
	}
	
	/**
	 * Gets the curve's first derivative with respect to t, at t. This represents the "velocity" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'c'.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The first derivative at the specified point on the curve.
	 */
	public static Vector3d firstDerivative(Vector3d a, Vector3d b, Vector3d c, double t) {
		return new Vector3d(
				firstDerivative(a.x(), b.x(), c.x(), t),
				firstDerivative(a.y(), b.y(), c.y(), t),
				firstDerivative(a.z(), b.z(), c.z(), t)
				);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "acceleration" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'c'. Note that quadratic beziers can only
	 * have constant acceleration, so this value does not depend on 't'.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @return The second derivative of the curve.
	 */
	public static Vector3d secondDerivative(Vector3d a, Vector3d b, Vector3d c) {
		return new Vector3d(
				secondDerivative(a.x(), b.x(), c.x()),
				secondDerivative(a.y(), b.y(), c.y()),
				secondDerivative(a.z(), b.z(), c.z())
				);
	}
	
	/**
	 * Gets a point on the curve.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The specified point on the curve.
	 */
	public static Vector4d valueAt(Vector4d a, Vector4d b, Vector4d c, double t) {
		return new Vector4d(
				valueAt(a.x(), b.x(), c.x(), t),
				valueAt(a.y(), b.y(), c.y(), t),
				valueAt(a.z(), b.z(), c.z(), t),
				valueAt(a.w(), b.w(), c.w(), t)
				);
	}
	
	/**
	 * Gets the curve's first derivative with respect to t, at t. This represents the "velocity" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'c'.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0..1 inclusive) specifying which point on the curve to return.
	 * @return The first derivative at the specified point on the curve.
	 */
	public static Vector4d firstDerivative(Vector4d a, Vector4d b, Vector4d c, double t) {
		return new Vector4d(
				firstDerivative(a.x(), b.x(), c.x(), t),
				firstDerivative(a.y(), b.y(), c.y(), t),
				firstDerivative(a.z(), b.z(), c.z(), t),
				firstDerivative(a.w(), b.w(), c.w(), t)
				);
	}
	
	/**
	 * Gets the curve's second derivative with respect to t. This represents the "acceleration" vector an object would
	 * need to adopt in order to follow the curve's contour going from 'a' to 'c'. Note that quadratic beziers can only
	 * have constant acceleration, so this value does not depend on 't'.
	 * @param a The first point on the curve.
	 * @param b The control point in the middle of the curve.
	 * @param c The last point on the curve.
	 * @return The second derivative of the curve.
	 */
	public static Vector4d secondDerivative(Vector4d a, Vector4d b, Vector4d c) {
		return new Vector4d(
				secondDerivative(a.x(), b.x(), c.x()),
				secondDerivative(a.y(), b.y(), c.y()),
				secondDerivative(a.z(), b.z(), c.z()),
				secondDerivative(a.w(), b.w(), c.w())
				);
	}
}
