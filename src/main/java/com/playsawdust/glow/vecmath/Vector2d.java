/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2022 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.vecmath;

/**
 * Represents a two-dimensional vector.
 * 
 * <p>Note, there is no cross product here. Cross products work in 3d.
 */
public record Vector2d(double x, double y) {
	public static final Vector2d ZERO = new Vector2d(0,0);
	
	/**
	 * Gets the length of this vector.
	 */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}
	
	/**
	 * Returns a normalized vector pointing the same direction as this vector, but with length 1.
	 */
	public Vector2d normalize() {
		double l = length();
		if (l == 0) return new Vector2d(0, 0);
		
		return new Vector2d(x / l, y / l);
	}
	
	/**
	 * Returns a vector with each component multiplied by value. If this is a unit vector, its length will become value.
	 */
	public Vector2d multiply(double value) {
		return new Vector2d(x * value, y * value);
	}
	
	/**
	 * Returns a vector with each component divided by value. If value is this vector's length, the returned vector will
	 * be normalized.
	 */
	public Vector2d divide(double value) {
		return new Vector2d(x / value, y / value);
	}
	
	/**
	 * Returns the sum of this vector and the argument, (this + value)
	 */
	public Vector2d add(Vector2d value) {
		return new Vector2d(this.x + value.x, this.y + value.y);
	}
	
	/**
	 * Returns the difference of this vector and the argument, (this - value)
	 */
	public Vector2d subtract(Vector2d value) {
		return new Vector2d(this.x - value.x, this.y - value.y);
	}
	
	/**
	 * Returns this vector transformed with the perp operation, equivalent to a 90 degree counterclockwise rotation.
	 * This is a real rotation, equivalent to a 90-degree rotation matrix; calling this twice inverts the vector,
	 * calling it three times rotates it clockwise, and calling it four times yields this vector again.
	 */
	public Vector2d perp() {
		return new Vector2d(-y, x);
	}
	
	/**
	 * Performs an alternative version of {@link #perp()} yielding a vector rotated *clockwise* from this one by 90
	 * degrees.
	 */
	public Vector2d cwPerp() {
		return new Vector2d(y, -x);
	}
	
	/**
	 * Returns the scalar product of this vector and the vector argument. See {@link #dot(Vector2d, Vector2d)}
	 */
	public double dot(Vector2d value) {
		return Vector2d.dot(this, value);
	}
	
	/**
	 * Gets the distance between this vector and the passed-in vector.
	 */
	public double distance(Vector2d other) {
		double dx = other.x - this.x;
		double dy = other.y - this.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Gets the square of the distance between this vector and the passed-in vector. This is faster than getting the
	 * distance.
	 */
	public double distanceSquared(Vector2d other) {
		double dx = other.x - this.x;
		double dy = other.y - this.y;
		
		return dx * dx + dy * dy;
	}
	
	/**
	 * Returns the perpendicular dot product of two vectors. This represents the "change" in angle between the two. If
	 * you put the vectors end-to-end, and travel along the resulting path, if the path "bends" left, the result will
	 * be positive. If the path "bends" right, the result will be negative. Parallel vectors in both the same and
	 * opposite directions will return zero.
	 * 
	 * <p>This is useful to find out if a polygon is a convex hull - take the perpDot of each pair of successive points
	 * (including the last and the first), and if all signs are the same, the shape is convex. Which sign will tell you
	 * the winding direction - positive is ccw, negative is cw.
	 */
	public static double perpDot(Vector2d a, Vector2d b) {
		return a.x * b.y - a.y * b.x;
	}
	
	/**
	 * Returns the scalar product of two vectors. Technically this is the length of 'a' times the length of 'b' times
	 * the cosine of the angle between them.
	 * 
	 * <p>This is commonly used in shaders to determine diffuse lighting influence; when 'a' and 'b' are unit vectors,
	 * the dot product is 1 when they are pointed in identical directions, 0 when 90 degrees apart, and -1 when pointed
	 * in opposite directions. So you can clamp the dot to 0..1 and then multiply your diffuse term by it for a quick
	 * and easy lighting calculation.
	 * 
	 * <p>If only 'b' is a unit vector, this can also be seen as the projection of 'a' onto 'b'. If the result is
	 * negative, the vectors are pointing in opposite directions. Either way, it's as if you put the vectors tail to
	 * tail and observed the "shadow" of 'a' falling onto the line defined by 'b'.
	 */
	public static double dot(Vector2d a, Vector2d b) {
		return a.x * b.x + a.y * b.y;
	}
}
