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
 * Represents a three-dimensional vector.
 */
public record Vector3d(double x, double y, double z) {
	
	/**
	 * Converts this vector into a two-dimensional vector by discarding the z coordinate.
	 */
	public Vector2d xy() {
		return new Vector2d(x, y);
	}
	
	/**
	 * Gets the length of this vector.
	 */
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	/**
	 * Returns a normalized vector pointing the same direction as this vector, but with length 1.
	 */
	public Vector3d normalize() {
		double l = length();
		if (l == 0) return new Vector3d(0, 0, 0);
		
		return new Vector3d(x / l, y / l, z / l);
	}
	
	/**
	 * Returns a vector with each component multiplied by value. If this is a unit vector, its length will become value.
	 */
	public Vector3d multiply(double value) {
		return new Vector3d(x * value, y * value, z * value);
	}
	
	/**
	 * Returns a vector with each component divided by value. If value is this vector's length, the returned vector will
	 * be normalized.
	 */
	public Vector3d divide(double value) {
		return new Vector3d(x / value, y / value, z / value);
	}
	
	/**
	 * Returns the sum of this vector and the argument, (this + value)
	 */
	public Vector3d add(Vector3d value) {
		return new Vector3d(this.x + value.x, this.y + value.y, this.z + value.z);
	}
	
	/**
	 * Returns the difference of this vector and the argument, (this - value)
	 */
	public Vector3d subtract(Vector3d value) {
		return new Vector3d(this.x - value.x, this.y - value.y, this.z - value.z);
	}
	
	/**
	 * Returns a vector perpendicular to this vector and the vector argument. See {@link #cross(Vector3d, Vector3d)}
	 */
	public Vector3d cross(Vector3d value) {
		return Vector3d.cross(this, value);
	}
	
	/**
	 * Returns the scalar product of this vector and the vector argument. See {@link #dot(Vector3d, Vector3d)}
	 */
	public double dot(Vector3d value) {
		return Vector3d.dot(this, value);
	}
	
	/**
	 * Gets the distance between this vector and the passed-in vector.
	 */
	public double distance(Vector3d other) {
		return Vector3d.distance(this, other);
	}
	
	/**
	 * Gets the square of the distance between this vector and the passed-in vector. This is faster than getting the
	 * distance.
	 */
	public double distanceSquared(Vector3d other) {
		return Vector3d.distanceSquared(this, other);
	}
	
	
	
	// Static helpers
	
	/**
	 * Returns the sum of the two vectors, a + b.
	 */
	public static Vector3d add(Vector3d a, Vector3d b) {
		return new Vector3d(a.x + b.x, a.y + b.y, a.z + b.z);
	}
	
	/**
	 * Returns the difference of the two vectors, a - b.
	 */
	public static Vector3d subtract(Vector3d a, Vector3d b) {
		return new Vector3d(a.x - b.x, a.y - b.y, a.z - b.z);
	}
	
	/**
	 * Returns a vector perpendicular to the plane defined by the two vector arguments. The length of the result is
	 * related to their relative directions. If both arguments are normalized and perpendicular, the result will be a
	 * unit vector. If both arguments are pointing in the exact same direction, the result will be a zero vector.
	 */
	public static Vector3d cross(Vector3d a, Vector3d b) {
		return new Vector3d(
				a.y * b.z - a.z * b.y,
				a.z * b.x - a.x * b.z,
				a.x * b.y - a.y * b.x
				);
	}
	
	/*//The result of this is not a rotor. it's a dot b + a ^ b but you're better off using outerProduct.
	public static Rotor geometricProduct(Vector3d a, Vector3d b) {
		double scalarPart = a.x * b.x + a.y * b.y + a.z * b.z;
		double xyPart = a.x * b.y - a.y * b.x;
		double yzPart = a.y * b.z - a.z * b.y;
		double xzPart = a.x * b.z - a.z * b.x;
		
		return new Rotor(scalarPart, new Bivector(xyPart, xzPart, yzPart));
	}*/
	
	/**
	 * Produces the Bivector that both these vectors lie on.
	 */
	public static Bivector outerProduct(Vector3d a, Vector3d b) {
		double xyPart = a.x * b.y - a.y * b.x;
		double xzPart = a.x * b.z - a.z * b.x;
		double yzPart = a.y * b.z - a.z * b.y;
		
		return new Bivector(xyPart, xzPart, yzPart);
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
	public static double dot(Vector3d a, Vector3d b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}
	
	/**
	 * Gets the distance between the two vectors.
	 */
	public static double distance(Vector3d a, Vector3d b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		double dz = a.z - b.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	/**
	 * Gets the square of the distance between the two vectors. This is faster than getting the distance.
	 */
	public static double distanceSquared(Vector3d a, Vector3d b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		double dz = a.z - b.z;
		return dx * dx + dy * dy + dz * dz;
	}
}
