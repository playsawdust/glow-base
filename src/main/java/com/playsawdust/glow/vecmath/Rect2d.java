package com.playsawdust.glow.vecmath;

public record Rect2d(double x, double y, double width, double height) {
	public Rect2d(double x, double y, double width, double height) {
		if (width<0 || height<0) throw new IllegalArgumentException("Cannot have negative dimensions.");
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
