/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2022 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.glow.vecmath;

/**
 * Represents a four-dimensional vector. Can be used to do affine transformations in three-dimensional space if w=1.
 */
public record Vector4d(double x, double y, double z, double w) {
	
	/**
	 * Converts this vector into a two-dimensional vector by discarding its z and w components.
	 */
	public Vector2d xy() {
		return new Vector2d(x, y);
	}
	
	/**
	 * Converts this vector into a three-dimensional vector by discarding its w component.
	 */
	public Vector3d xyz() {
		return new Vector3d(x, y, z);
	}
	
	/**
	 * Gets the length of this vector.
	 */
	public double length() {
		return Math.sqrt(x * x + y * y + z * z + w * w);
	}
	
	/**
	 * Returns a normalized vector pointing the same direction as this vector, but with length 1. Note that w will also
	 * be foreshortened.
	 */
	public Vector4d normalize() {
		double l = length();
		if (l == 0) return new Vector4d(0, 0, 0, 0);
		
		return new Vector4d(x / l, y / l, z / l, w / l);
	}
	
	/**
	 * Returns a vector with each component multiplied by value. If this is a unit vector, its length will become value.
	 */
	public Vector4d multiply(double value) {
		return new Vector4d(x * value, y * value, z * value, w * value);
	}
	
	/**
	 * Returns a vector with each component divided by value. If value is this vector's length, the returned vector will
	 * be normalized.
	 */
	public Vector4d divide(double value) {
		return new Vector4d(x / value, y / value, z / value, w / value);
	}
	
	/**
	 * Returns the sum of this vector and the argument, (this + value)
	 */
	public Vector4d add(Vector4d value) {
		return new Vector4d(this.x + value.x, this.y + value.y, this.z + value.z, this.w + value.w);
	}
	
	/**
	 * Returns the difference of this vector and the argument, (this - value)
	 */
	public Vector4d subtract(Vector4d value) {
		return new Vector4d(this.x - value.x, this.y - value.y, this.z - value.z, this.w - value.w);
	}
	
	/**
	 * Returns the inner (dot) product of this vector and the vector argument. See {@link #dot(Vector4d, Vector4d)}
	 */
	public double dot(Vector4d value) {
		return Vector4d.dot(this, value);
	}
	
	/**
	 * Gets the distance between this vector and the passed-in vector.
	 */
	public double distance(Vector4d value) {
		return Vector4d.distance(this, value);
	}
	
	/**
	 * Gets the square of the distance between this vector and the passed-in vector. This is faster than getting the
	 * distance.
	 */
	public double distanceSquared(Vector4d value) {
		return Vector4d.distanceSquared(this, value);
	}
	
	
	
	//Static helpers
	
	/**
	 * Returns the inner (dot) product of a and b, which is zero if the two vectors are perpendicular, 1 if they are
	 * aligned, and -1 if they are opposite. Additionally, if b is a unit vector, this will get the (possibly negative)
	 * magnitude of a's "shadow" projected onto b.
	 * 
	 * <p>This result (for 4d vectors) is Lorentz-invariant, meaning it will be the same answer for any space or
	 * reference frame.
	 */
	public static double dot(Vector4d a, Vector4d b) {
		return  a.x * b.x +
				a.y * b.y +
				a.z * b.z +
				a.w * b.w;
	}
	
	/**
	 * Gets the distance between the two vectors.
	 */
	public static double distance(Vector4d a, Vector4d b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		double dz = a.z - b.z;
		double dw = a.w - b.w;
		return Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
	}
	
	/**
	 * Gets the square of the distance between the two vectors. This is faster than getting the distance.
	 */
	public static double distanceSquared(Vector4d a, Vector4d b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		double dz = a.z - b.z;
		double dw = a.w - b.w;
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}
}
