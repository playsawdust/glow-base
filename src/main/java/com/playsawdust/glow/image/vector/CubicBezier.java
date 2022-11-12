package com.playsawdust.glow.image.vector;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.playsawdust.glow.vecmath.Vector2d;

public class CubicBezier implements Curve {
	private final Vector2d a;
	private final Vector2d b;
	private final Vector2d c;
	private final Vector2d d;
	
	public CubicBezier(Vector2d a, Vector2d b, Vector2d c, Vector2d d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public List<LineSegment> approximate(int maxSegments, @Nullable List<LineSegment> destination) {
		// TODO Auto-generated method stub
		return null;
	}
}
