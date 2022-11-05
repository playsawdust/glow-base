package com.playsawdust.glow.image;

import com.playsawdust.glow.function.DoubleBiConsumer;
import com.playsawdust.glow.vecmath.Rect2d;
import com.playsawdust.glow.vecmath.Vector2d;
import com.playsawdust.glow.vecmath.Vector3d;

public class RasterizerUtils {
	/**
	 * Plots a bresenham line between two points in 2D, supplying these points to the specified consumer.
	 * @param x1 The X coordinate of the first point.
	 * @param y1 The Y coordinate of the first point.
	 * @param x2 The X coordinate of the last point.
	 * @param y2 The Y coordinate of the last point.
	 * @param consumer A function that will accept each unique point on the line.
	 */
	public static void bresenham(double x1, double y1, double x2, double y2, DoubleBiConsumer consumer) {
		double dx = x2-x1;
		double dy = y2-y1;
		double scale = Math.max(Math.abs(dx), Math.abs(dy));
		if(scale == 0) {
			consumer.acceptAsDoubles(x1, y1);
			return; //Degenerate line will only plot the first/last pixel so we don't DivideByZero
		}
		dx /= scale;
		dy /= scale;
		
		double xi = x1;
		double yi = y1;
		for(int i=0; i<(int)scale; i++) {
			consumer.acceptAsDoubles(xi, yi);
			xi += dx;
			yi += dy;
		}
		consumer.acceptAsDoubles(xi, yi);
	}
	
	/**
	 * Returns a value at point {@code t} on a 1D quadratic bezier curve.
	 * @param a The first point on the curve.
	 * @param b The control point that governs the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0-1) across the curve, with 0 yielding a and 1 yielding c.
	 * @return A point on the quadratic curve for t values 0..1 inclusive.
	 */
	public static double quadratic(double a, double b, double c, double t) {
		double aTerm = c * Math.pow(t, 2);
		double bTerm = b * 2 * t * (1 - t);
		double cTerm = a * Math.pow((1 - t), 2);
		
		return aTerm + bTerm + cTerm;
	}
	
	/**
	 * Returns a value at point {@code t} on a 2D quadratic bezier curve.
	 * @param a The first point on the curve.
	 * @param b The control point that governs the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0-1) across the curve, with 0 yielding a and 1 yielding c.
	 * @return A point on the quadratic curve for t values 0..1 inclusive.
	 */
	public static Vector2d quadratic(Vector2d a, Vector2d b, Vector2d c, double t) {
		return new Vector2d(
				quadratic(a.x(), b.x(), c.x(), t),
				quadratic(a.y(), b.y(), c.y(), t)
				);
	}
	
	/**
	 * Returns a value at point {@code t} on a 3D quadratic bezier curve.
	 * @param a The first point on the curve.
	 * @param b The control point that governs the middle of the curve.
	 * @param c The last point on the curve.
	 * @param t The interpolation value (0-1) across the curve, with 0 yielding a and 1 yielding c.
	 * @return A point on the quadratic curve for t values 0..1 inclusive.
	 */
	public static Vector3d quadratic(Vector3d a, Vector3d b, Vector3d c, double t) {
		return new Vector3d(
				quadratic(a.x(), b.x(), c.x(), t),
				quadratic(a.y(), b.y(), c.y(), t),
				quadratic(a.z(), b.z(), c.z(), t)
				);
	}
	
	
	
	/**
	 * Plot a bezier curve through a series of points.
	 * @param points
	 * @param consumer
	 */
	public static void quadratic(Vector2d[] points, DoubleBiConsumer consumer) {
		if (points.length == 0) return;
		if (points.length == 1) {
			consumer.acceptAsDoubles(points[0].x(), points[0].y());
			return;
		}
		if (points.length == 2) {
			//There's a start and end, but no control points. A straight line is the best we can do.
			bresenham(points[0].x(), points[0].y(), points[1].x(), points[1].y(), consumer);
			return;
		}
		
		Vector2d a = null;
		Vector2d b = null;
		for(int i=0; i<points.length; i++) {
			Vector2d c = points[i];
			if (a!=null && b!=null) {
				
				
				
			}
			
			a = b;
			b = c;
		}
	}
	
	/**
	 * The derivative of a bezier curve is related to two things:
	 * 
	 * <p>If you put the vector's tail at the point at t, the vector will be tangent to the curve at that location, and
	 * point "forwards" along the curve towards c.
	 * 
	 * <p>The vector describes the change that the curve is undergoing in slope - you can think of this as
	 * an acceleration that the point must experience in order to follow the curve and arrive at c.
	 * 
	 * <p>In the case of a quadratic bezier curve, which has order 2, its derivative must be of order 1. In other words,
	 * this function will produce a straight line from quadraticDerivative(a,b,c,0) to quadraticDerivative(a,b,c,1).
	 * 
	 * @param a The first point in the curve.
	 * @param b The middle (control) point.
	 * @param c The last point in the curve.
	 * @param t The interpolation value (0-1) across the curve, with 0 representing the exact location of a and 1
	 *          representing the exact loacation of c.
	 * @return The derivative of the quadratic bezier curve at the corresponding t value.
	 */
	public static double quadraticDerivative(double a, double b, double c, double t) {
		return (2 - 2*t) * (b - a) + (2*t) * (c - b);
	}
	
	/**
	 * The derivative of a bezier curve is related to two things:
	 * 
	 * <p>If you put the vector's tail at the point at t, the vector will be tangent to the curve at that location, and
	 * point "forwards" along the curve towards c.
	 * 
	 * <p>The vector describes the change that the curve is undergoing in slope - you can think of this as
	 * an acceleration that the point must experience in order to follow the curve and arrive at c.
	 * 
	 * <p>In the case of a quadratic bezier curve, which has order 2, its derivative must be of order 1. In other words,
	 * this function will produce a straight line from quadraticDerivative(a,b,c,0) to quadraticDerivative(a,b,c,1).
	 * 
	 * @param a The first point in the curve.
	 * @param b The middle (control) point.
	 * @param c The last point in the curve.
	 * @param t The interpolation value (0-1) across the curve, with 0 representing the exact location of a and 1
	 *          representing the exact loacation of c.
	 * @return The derivative of the quadratic bezier curve at the corresponding t value.
	 */
	public static Vector2d quadraticDerivative(Vector2d a, Vector2d b, Vector2d c, double t) {
		return new Vector2d(
				quadraticDerivative(a.x(), b.x(), c.x(), t),
				quadraticDerivative(a.y(), b.y(), c.y(), t)
				);
	}
	
	/**
	 * The derivative of a bezier curve is related to two things:
	 * 
	 * <p>If you put the vector's tail at the point at t, the vector will be tangent to the curve at that location, and
	 * point "forwards" along the curve towards c.
	 * 
	 * <p>The vector describes the change that the curve is undergoing in slope - you can think of this as
	 * an acceleration that the point must experience in order to follow the curve and arrive at c.
	 * 
	 * <p>In the case of a quadratic bezier curve, which has order 2, its derivative must be of order 1. In other words,
	 * this function will produce a straight line from quadraticDerivative(a,b,c,0) to quadraticDerivative(a,b,c,1).
	 * 
	 * @param a The first point in the curve.
	 * @param b The middle (control) point.
	 * @param c The last point in the curve.
	 * @param t The interpolation value (0-1) across the curve, with 0 representing the exact location of a and 1
	 *          representing the exact loacation of c.
	 * @return The derivative of the quadratic bezier curve at the corresponding t value.
	 */
	public static Vector3d quadraticDerivative(Vector3d a, Vector3d b, Vector3d c, double t) {
		return new Vector3d(
				quadraticDerivative(a.x(), b.x(), c.x(), t),
				quadraticDerivative(a.y(), b.y(), c.y(), t),
				quadraticDerivative(a.z(), b.z(), c.z(), t)
				);
	}
	
	/**
	 * Returns a tight bounding box around a quadratic bezier curve.
	 * @param a The first point in the curve.
	 * @param b The middle (control) point.
	 * @param c The last point in the curve.
	 * @return A bounding box that precisely contains the curve.
	 */
	public static Rect2d quadraticBoundingBox(Vector2d a, Vector2d b, Vector2d c) {
		double xmin = Math.min(a.x(), c.x());
		double xmax = Math.max(a.x(), c.x());
		double ymin = Math.min(a.y(), c.y());
		double ymax = Math.max(a.y(), c.y());
		
		Vector2d d0 = quadraticDerivative(a, b, c, 0);
		Vector2d d1 = quadraticDerivative(a, b, c, 1);
		
		/*
		 * Imagine, if you will, a graph: horizontal axis is T, the vertical axis is Y.
		 * 
		 * The T-intercept is the T value of the local Y-minimum or Y-maximum
		 * 
		 * Think about what the slope is on this graph, it's y2-y1 / t2-t1. Since t2=1 and t1=0, the slope is just y2-y1.
		 */
		
		double xSlope = d1.x() - d0.x(); if (xSlope==0) xSlope = 0.00000000001; //guard against perfectly flat lines
		double ySlope = d1.y() - d0.y(); if (ySlope==0) ySlope = 0.00000000001;
		
		/*
		 * Our target is the t-intercept, where Y == 0
		 * Now, if y = 0 ; 0 = mx + b ; -b = mx ; -b / m = x
		 * 
		 * Unfortunately, as you can see we need the Y-intercept first, so:
		 * y = m*0 + b
		 * y = b
		 * it stands to reason whatever the y-value is at t=0, is the y-intercept.
		 */
		double xInterceptT = -d0.x() / xSlope;
		double yInterceptT = -d0.y() / ySlope;
		
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
		
		return new Rect2d(xmin, ymin, xmax-xmin, ymax-ymin);
	}
}
