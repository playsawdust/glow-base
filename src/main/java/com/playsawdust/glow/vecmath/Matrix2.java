package com.playsawdust.glow.vecmath;

public record Matrix2(double a, double b, double c, double d) {
	public static final Matrix2 IDENTITY = new Matrix2(
			1, 0,
			0, 1);
	
	public double determinant() {
		return a * d - b * c;
	}
	
	public Vector2d transform(Vector2d value) {
		return new Vector2d(
				value.x() * a + value.y() * b,
				value.x() * c + value.y() * d
				);
	}
	
	public static Matrix2 scale(double x, double y) {
		return new Matrix2(
				x, 0,
				0, y
				);
	}
	
	public static Matrix2 rotate(double theta) {
		return new Matrix2(
				Math.cos(theta), -Math.sin(theta),
				Math.sin(theta), Math.cos(theta)
				);
	}
}
