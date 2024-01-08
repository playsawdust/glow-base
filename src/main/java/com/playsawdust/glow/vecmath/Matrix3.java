/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.vecmath;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.checkerframework.checker.nullness.qual.Nullable;

public record Matrix3(double a, double b, double c, double d, double e, double f, double g, double h, double i) {
	public static final Matrix3 IDENTITY = new Matrix3(
			1, 0, 0,
			0, 1, 0,
			0, 0, 1);
	
	/**
	 * Returns the determinant of this matrix. This is a series of sums and differences of diagonal products used to
	 * determine certain subtle characteristics of a matrix. The main one is that nonzero determinants indicate that
	 * a matrix can be inverted. A zero determinant means that we lost some information in this transformation, so we
	 * can't reverse it.
	 */
	public double determinant() {
		return
				a * e * i
				+ b * f * g
				+ c * d * h
				- c * e * g
				- b * d * i
				- a * f * h
				;
	}
	
	/**
	 * Produces the same result as `this.transform(new Vector3d(value.x(), value.y(), 1)).xy()`
	 * 
	 * <p>The last (bottom) 3 cells of this matrix are ignored because the destination z value is being discarded,
	 * and cells 3 and 6 are added directly because their coefficients are 1. This allows translation of 2d vectors,
	 * and more generally, any affine transformation.
	 * 
	 * <p>See also {@link #transform(Vector3d)}
	 */
	public Vector2d transform(Vector2d value) {
		return new Vector2d(
				value.x() * a + value.y() * b + c,
				value.x() * d + value.y() * e + f
				);
	}
	
	/**
	 * Returns the result of multiplying the passed-in vector by this matrix, transforming it.
	 */
	public Vector3d transform(Vector3d value) {
		return new Vector3d(
				value.x() * a + value.y() * b + value.z() * c,
				value.x() * d + value.y() * e + value.z() * f,
				value.x() * g + value.y() * h + value.z() * i
				);
	}
	
	/**
	 * Returns an array containing this matrix. If the provided array is non-null, it will be used and returned.
	 */
	public double[] toArray(@Nullable double[] arr) {
		if (arr==null) {
			return new double[] { a, b, c, d, e, f, g, h, i };
		} else {
			if (arr.length<9) throw new IllegalArgumentException("Argument must have length of at least 9");
			arr[0] = a; arr[1] = b; arr[2] = c;
			arr[3] = d; arr[4] = e; arr[5] = f;
			arr[6] = g; arr[7] = h; arr[8] = i;
			return arr;
		}
	}
	
	/**
	 * Writes this matrix into the provided buffer at the current write position. This will advance the buffer position
	 * by 72 bytes (9 elements).
	 */
	public void write(DoubleBuffer buf) {
		buf.put(a); buf.put(b); buf.put(c);
		buf.put(d); buf.put(e); buf.put(f);
		buf.put(g); buf.put(h); buf.put(i);
	}
	
	/**
	 * Writes this matrix at reduced precision into the provided buffer at the current write position. This will advance
	 * the buffer position by 36 bytes (9 elements).
	 */
	public void write(FloatBuffer buf) {
		buf.put((float) a); buf.put((float) b); buf.put((float) c);
		buf.put((float) d); buf.put((float) e); buf.put((float) f);
		buf.put((float) g); buf.put((float) h); buf.put((float) i);
	}
	
	
	
	// Static helpers
	
	/**
	 * Rotate around the X axis by the specified amount of radians.
	 * 
	 * <p>In a Y-up system this is the same as pitch.
	 */
	public static Matrix3 rotateX(double theta) {
		return new Matrix3(
				1, 0, 0,
				0, Math.cos(theta), -Math.sin(theta),
				0, Math.sin(theta),  Math.cos(theta)
				);
	}
	
	/**
	 * Rotate around the Y axis by the specified amount of radians.
	 * 
	 * <p>In a Y-up system this is the same as yaw.
	 */
	public static Matrix3 rotateY(double theta) {
		return new Matrix3(
				Math.cos(theta), 0, Math.sin(theta),
				0, 1, 0,
				-Math.sin(theta), 0, Math.cos(theta)
				);
	}
	
	/**
	 * Rotate around the Z axis by the specified amount of radians.
	 * 
	 * <p>In a Y-up system this is the same as roll.
	 */
	public static Matrix3 rotateZ(double theta) {
		return new Matrix3(
				Math.cos(theta), -Math.sin(theta), 0,
				Math.sin(theta), Math.cos(theta), 0,
				0, 0, 1
				);
	}
	
	/**
	 * Returns a translation matrix which is *only suitable for translating two-dimensional vectors*.
	 * 
	 * <p>See {@link Matrix4#translate(double, double}
	 */
	public static Matrix3 translate(double x, double y) {
		return new Matrix3(
				1, 0, x,
				0, 1, y,
				0, 0, 1
				);
	}
	
	/**
	 * Rotates vectors around an arbitrary axis
	 * @param u the axis around which rotation will happen. Must be normalized.
	 * @param theta the magnitude of the rotation, in radians
	 */
	public static Matrix3 rotate(Vector3d u, double theta) {
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		double invcos = 1 - Math.cos(theta);
		return new Matrix3(
				cosTheta + u.x() * u.x() * invcos,          u.x() * u.y() * invcos - u.z() * sinTheta,  u.x() * u.z() * invcos + u.y() * sinTheta,
				u.y() * u.x() * invcos + u.z() * sinTheta,  cosTheta * u.y() * u.y() * invcos,          u.y() * u.z() * invcos - u.x() * sinTheta,
				u.z() * u.x() * invcos - u.y() * sinTheta,  u.z() * u.y() * invcos + u.x() * sinTheta,  cosTheta + u.z() * u.z() * invcos
				);
	}
}
