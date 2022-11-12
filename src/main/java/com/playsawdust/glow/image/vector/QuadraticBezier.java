package com.playsawdust.glow.image.vector;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.playsawdust.glow.vecmath.QuadraticBeziers;
import com.playsawdust.glow.vecmath.Vector2d;

public class QuadraticBezier implements Curve {

	private final Vector2d a;
	private final Vector2d b;
	private final Vector2d c;
	
	public QuadraticBezier(double ax, double ay, double bx, double by, double cx, double cy) {
		this.a = new Vector2d(ax, ay);
		this.b = new Vector2d(bx, by);
		this.c = new Vector2d(cx, cy);
	}
	
	public QuadraticBezier(Vector2d a, Vector2d b, Vector2d c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * Returns the first endpoint in this curve.
	 */
	public Vector2d a() {
		return a;
	}
	
	/**
	 * Returns the control point of this curve.
	 */
	public Vector2d b() {
		return b;
	}
	
	/**
	 * Returns the last endpoint in this curve.
	 */
	public Vector2d c() {
		return c;
	}
	
	@Override
	public List<LineSegment> approximate(int maxSegments, @Nullable List<LineSegment> destination) {
		//Think about it this way:Our first iteration goes from t=0 to t=0+tDivision. So at maxSegments=1, tStep MUST
		//be 1. At maxSegments = 2, tStep MUST be 0.5.
		double tStep = 1.0 / maxSegments;
		
		LineSegment[] result = new LineSegment[maxSegments];
		double t1 = 0;
		Vector2d p1 = a;
		for(int i=0; i<result.length; i++) {
			double t2 = t1 + tStep;
			Vector2d p2 = QuadraticBeziers.valueAt(a, b, c, t2);
			result[i] = new LineSegment(p1, p2);
			
			t1 = t2;
			p1 = p2;
		}
		
		if (destination == null) {
			return List.of(result);
		} else {
			for(int i=0; i<result.length; i++) destination.add(result[i]);
			return destination;
		}
	}

	
	
	
	
	
}
