/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.vecmath;

public record Vector2i(int x, int y) {
	/** Gets the length of this vector */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}
	
	/**
	 * Returns a vector with each component multiplied by value. If this is a unit vector, its length will become value.
	 */
	public Vector2i multiply(int value) {
		return new Vector2i(x * value, y * value);
	}
	
	/**
	 * Returns a vector with each component divided by value. If value is this vector's length, the returned vector will
	 * be normalized.
	 */
	public Vector2i divide(int value) {
		return new Vector2i(x / value, y / value);
	}
	
	/**
	 * Returns the sum of this vector and the argument, (this + value)
	 */
	public Vector2i add(Vector2i value) {
		return new Vector2i(this.x + value.x, this.y + value.y);
	}
	
	/**
	 * Returns the difference of this vector and the argument, (this - value)
	 */
	public Vector2i subtract(Vector2i value) {
		return new Vector2i(this.x - value.x, this.y - value.y);
	}
	
	/**
	 * Returns this vector transformed with the perp operation, equivalent to a 90 degree counterclockwise rotation.
	 * This is a real rotation, equivalent to a 90-degree rotation matrix; calling this twice inverts the vector,
	 * calling it three times rotates it clockwise, and calling it four times yields this vector again.
	 */
	public Vector2i perp() {
		return new Vector2i(-y, x);
	}
	
	/**
	 * Performs an alternative version of {@link #perp()} yielding a vector rotated *clockwise* from this one by 90
	 * degrees.
	 */
	public Vector2i cwPerp() {
		return new Vector2i(y, -x);
	}
	
	/**
	 * Gets the distance between this vector and the passed-in vector.
	 */
	public double distance(Vector2i other) {
		int dx = other.x - this.x;
		int dy = other.y - this.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Gets the square of the distance between this vector and the passed-in vector. This is faster than getting the
	 * distance.
	 */
	public int distanceSquared(Vector2i other) {
		int dx = other.x - this.x;
		int dy = other.y - this.y;
		
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
	public static int perpDot(Vector2i a, Vector2i b) {
		return a.x * b.y - a.y * b.x;
	}
}
