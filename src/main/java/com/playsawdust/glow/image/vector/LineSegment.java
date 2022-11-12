package com.playsawdust.glow.image.vector;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.playsawdust.glow.vecmath.Vector2d;

public class LineSegment implements Curve {
	private final Vector2d p1;
	private final Vector2d p2;
	
	public LineSegment(double x1, double y1, double x2, double y2) {
		this.p1 = new Vector2d(x1, y1);
		this.p2 = new Vector2d(x2, y2);
	}
	
	public LineSegment(Vector2d p1, Vector2d p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	@Override
	public List<LineSegment> approximate(int maxSegments, @Nullable List<LineSegment> destination) {
		if (destination != null) {
			destination.add(this);
			return destination;
		} else {
			return List.of(this); //immutable
		}
	}
	
}
