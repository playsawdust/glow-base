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

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.checkerframework.checker.nullness.qual.Nullable;

public record Matrix4(
		double a, double b, double c, double d,
		double e, double f, double g, double h,
		double i, double j, double k, double l,
		double m, double n, double o, double p) {

	public static final Matrix4 IDENTITY = new Matrix4(
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
			);
	
	public double determinant() {
		return
			  a * f * k * p + a * g * l * n + a * h * j * o
			- a * h * k * n - a * g * j * p - a * f * l * o
			- b * e * k * p - c * e * l * n - d * e * j * o
			+ d * e * k * n + c * e * j * p + b * e * l * o
			+ b * g * i * p + c * h * i * n + d * f * i * o
			- d * g * i * n - c * f * i * p - b * h * i * o
			- b * g * l * m - c * h * j * m - d * f * k * m
			+ d * g * j * m + c * f * l * m + b * h * k * m;
	}
	
	public Matrix4 adjugate() {
		// So we need the determinants of sub-matrices created by dropping one row and one column from each matrix.
		// Each location in the adjugate matrix corresponds to dropping the row and column it's in from the original
		// matrix, and taking the determinant of the resulting 3x3 matrix.
		// For instance: m42 removes row 4 and column 2
		double m11 = new Matrix3(
				
				   f, g, h,
				   j, k, l,
				   n, o, p).determinant();
		double m12 = new Matrix3(
				
				e,    g, h,
				i,    k, l,
				m,    o, p).determinant();
		double m13 = new Matrix3(
				
				e, f,    h,
				i, j,    l,
				m, n,    p).determinant();
		double m14 = new Matrix3(
				
				e, f, g,  
				i, j, k,  
				m, n, o   ).determinant();
		
		
		double m21 = new Matrix3(
				   b, c, d,
				   
				   j, k, l,
				   n, o, p).determinant();
		double m22 = new Matrix3(
				a,    c, d,
				
				i,    k, l,
				m,    o, p).determinant();
		double m23 = new Matrix3(
				a, b,    d,
				
				i, j,    l,
				m, n,    p).determinant();
		double m24 = new Matrix3(
				a, b, c,  
				
				i, j, k,  
				m, n, o   ).determinant();
		
		
		double m31 = new Matrix3(
				   b, c, d,
				   f, g, h,
				   
				   n, o, p).determinant();
		double m32 = new Matrix3(
				a,    c, d,
				e,    g, h,
				
				m,    o, p).determinant();
		double m33 = new Matrix3(
				a, b,    d,
				e, f,    h,
				
				m, n,    p).determinant();
		double m34 = new Matrix3(
				a, b, c,  
				e, f, g,  
				
				m, n, o   ).determinant();
		
		
		double m41 = new Matrix3(
				   b, c, d,
				   f, g, h,
				   j, k, l
				          ).determinant();
		double m42 = new Matrix3(
				a,    c, d,
				e,    g, h,
				i,    k, l
				          ).determinant();
		double m43 = new Matrix3(
				a, b,    d,
				e, f,    h,
				i, j,    l
				          ).determinant();
		double m44 = new Matrix3(
				a, b, c,  
				e, f, g,  
				i, j, k   
				          ).determinant();
		
		// Multiply each "mij" by (-1)^i+j
		m11 *= Math.pow(-1, 1+1);
		m12 *= Math.pow(-1, 1+2);
		m13 *= Math.pow(-1, 1+3);
		m14 *= Math.pow(-1, 1+4);
		
		m21 *= Math.pow(-1, 2+1);
		m22 *= Math.pow(-1, 2+2);
		m23 *= Math.pow(-1, 2+3);
		m24 *= Math.pow(-1, 2+4);
		
		m31 *= Math.pow(-1, 3+1);
		m32 *= Math.pow(-1, 3+2);
		m33 *= Math.pow(-1, 3+3);
		m34 *= Math.pow(-1, 3+4);
		
		m41 *= Math.pow(-1, 4+1);
		m42 *= Math.pow(-1, 4+2);
		m43 *= Math.pow(-1, 4+3);
		m44 *= Math.pow(-1, 4+4);
		
		/*
		 * Now we can finally construct a matrix such that each element i,j (row, col) =
		 * -1^(i+j) * det(ji)
		 * 
		 * Note that the final matrix has the rows and columns transposed!
		 */
		
		return new Matrix4(
				m11, m21, m31, m41,
				m12, m22, m32, m42,
				m13, m23, m33, m43,
				m14, m24, m34, m44
				);
	}
	
	public Matrix4 invert() {
		double det = determinant();
		if (det==0) throw new IllegalStateException("Cannot get the inverse of a singular matrix (determinant()==0)");
		
		double invDet = 1/det;
		return Matrix4.multiply(adjugate(), invDet);
	}
	
	/**
	 * Produces the same result as `this.transform(new Vector4d(value.x(), value.y(), value.z(), 1)).xyz()`
	 * 
	 * <p>The last (bottom) 4 cells of this matrix are ignored because the destination w value is being discarded,
	 * and cells 4, 8, and 12 are added directly because their coefficients are 1. This allows translation of 3d vectors,
	 * and more generally, any affine transformation.
	 * 
	 * <p>See also {@link #transform(Vector4d)}
	 */
	public Vector3d transform(Vector3d value) {
		return new Vector3d(
				value.x() * a + value.y() * b + value.z() * c + d,
				value.x() * e + value.y() * f + value.z() * g + h,
				value.x() * i + value.y() * j + value.z() * k + l
				);
	}
	
	/**
	 * Returns the result of multiplying the passed-in vector by this matrix, transforming it.
	 */
	public Vector4d transform(Vector4d value) {
		return new Vector4d(
				value.x() * a + value.y() * b + value.z() * c + value.w() * d,
				value.x() * e + value.y() * f + value.z() * g + value.w() * h,
				value.x() * i + value.y() * j + value.z() * k + value.w() * l,
				value.x() * m + value.y() * n + value.z() * o + value.w() * p
				);
	}
	
	public Matrix4 multiply(double scalar) {
		return Matrix4.multiply(this, scalar);
	}
	
	public Matrix4 multiply(Matrix4 other) {
		return Matrix4.multiply(this, other);
	}
	
	/**
	 * Returns an array containing this matrix. If the provided array is non-null, it will be used and returned.
	 */
	public double[] toArray(@Nullable double[] arr) {
		if (arr==null) {
			return new double[] { a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p };
		} else {
			if (arr.length<16) throw new IllegalArgumentException("Argument must have length of at least 16");
			arr[0] = a; arr[1] = b; arr[2] = c; arr[3] = d;
			arr[4] = e; arr[5] = f; arr[6] = g; arr[7] = h;
			arr[8] = i; arr[9] = j; arr[10]= k; arr[11]= l;
			arr[12]= m; arr[13]= n; arr[14]= o; arr[15]= p;
			return arr;
		}
	}
	
	/**
	 * Writes this matrix into the provided buffer at the current write position. This will advance the buffer position
	 * by 128 bytes (16 elements).
	 */
	public void write(DoubleBuffer buf) {
		buf.put(a); buf.put(b); buf.put(c); buf.put(d);
		buf.put(e); buf.put(f); buf.put(g); buf.put(h);
		buf.put(i); buf.put(j); buf.put(k); buf.put(l);
		buf.put(m); buf.put(n); buf.put(o); buf.put(p);
	}
	
	/**
	 * Writes this matrix at reduced precision into the provided buffer at the current write position. This will advance
	 * the buffer position by 64 bytes (16 elements).
	 */
	public void write(FloatBuffer buf) {
		buf.put((float) a); buf.put((float) b); buf.put((float) c); buf.put((float) d);
		buf.put((float) e); buf.put((float) f); buf.put((float) g); buf.put((float) h);
		buf.put((float) i); buf.put((float) j); buf.put((float) k); buf.put((float) l);
		buf.put((float) m); buf.put((float) n); buf.put((float) o); buf.put((float) p);
	}
	
	
	
	// Static helpers
	
	public static Matrix4 multiply(Matrix4 m, double scalar) {
		return new Matrix4(
				m.a * scalar, m.b * scalar, m.c * scalar, m.d * scalar,
				m.e * scalar, m.f * scalar, m.g * scalar, m.h * scalar,
				m.i * scalar, m.j * scalar, m.k * scalar, m.l * scalar,
				m.m * scalar, m.n * scalar, m.o * scalar, m.p * scalar
				);
	}
	
	public static Matrix4 multiply(Matrix4 m, Matrix4 n) {
		// TODO: Unroll / simplify
		
		double m11 = new Vector4d(m.a, m.b, m.c, m.d).dot(new Vector4d(n.a, n.e, n.i, n.m)); // m row 1 dot n col 1
		double m12 = new Vector4d(m.a, m.b, m.c, m.d).dot(new Vector4d(n.b, n.f, n.j, n.n)); // m row 1 dot n col 2
		double m13 = new Vector4d(m.a, m.b, m.c, m.d).dot(new Vector4d(n.c, n.g, n.k, n.o)); // m row 1 dot n col 3
		double m14 = new Vector4d(m.a, m.b, m.c, m.d).dot(new Vector4d(n.d, n.h, n.l, n.p)); // m row 1 dot n col 4
		
		double m21 = new Vector4d(m.e, m.f, m.g, m.h).dot(new Vector4d(n.a, n.e, n.i, n.m)); // m row 2 dot n col 1
		double m22 = new Vector4d(m.e, m.f, m.g, m.h).dot(new Vector4d(n.b, n.f, n.j, n.n)); // m row 2 dot n col 2
		double m23 = new Vector4d(m.e, m.f, m.g, m.h).dot(new Vector4d(n.c, n.g, n.k, n.o)); // m row 2 dot n col 3
		double m24 = new Vector4d(m.e, m.f, m.g, m.h).dot(new Vector4d(n.d, n.h, n.l, n.p)); // m row 2 dot n col 4
		
		double m31 = new Vector4d(m.i, m.j, m.k, m.l).dot(new Vector4d(n.a, n.e, n.i, n.m)); // m row 3 dot n col 1
		double m32 = new Vector4d(m.i, m.j, m.k, m.l).dot(new Vector4d(n.b, n.f, n.j, n.n)); // m row 3 dot n col 2
		double m33 = new Vector4d(m.i, m.j, m.k, m.l).dot(new Vector4d(n.c, n.g, n.k, n.o)); // m row 3 dot n col 3
		double m34 = new Vector4d(m.i, m.j, m.k, m.l).dot(new Vector4d(n.d, n.h, n.l, n.p)); // m row 3 dot n col 4
		
		double m41 = new Vector4d(m.m, m.n, m.o, m.p).dot(new Vector4d(n.a, n.e, n.i, n.m)); // m row 4 dot n col 1
		double m42 = new Vector4d(m.m, m.n, m.o, m.p).dot(new Vector4d(n.b, n.f, n.j, n.n)); // m row 4 dot n col 2
		double m43 = new Vector4d(m.m, m.n, m.o, m.p).dot(new Vector4d(n.c, n.g, n.k, n.o)); // m row 4 dot n col 3
		double m44 = new Vector4d(m.m, m.n, m.o, m.p).dot(new Vector4d(n.d, n.h, n.l, n.p)); // m row 4 dot n col 4
		
		return new Matrix4(
				m11, m12, m13, m14,
				m21, m22, m23, m24,
				m31, m32, m33, m34,
				m41, m42, m43, m44
				);
	}
	
	/**
	 * Rotate around the X axis by the specified amount of radians.
	 * 
	 * <p>In a Y-up system this is the same as pitch.
	 */
	public static Matrix4 rotateX(double theta) {
		return new Matrix4(
				1, 0, 0, 0,
				0, Math.cos(theta), -Math.sin(theta), 0,
				0, Math.sin(theta),  Math.cos(theta), 0,
				0, 0, 0, 1
				);
	}
	
	/**
	 * Rotate around the Y axis by the specified amount of radians.
	 * 
	 * <p>In a Y-up system this is the same as yaw.
	 */
	public static Matrix4 rotateY(double theta) {
		return new Matrix4(
				Math.cos(theta), 0, Math.sin(theta), 0,
				0, 1, 0, 0,
				-Math.sin(theta), 0, Math.cos(theta), 0,
				0, 0, 0, 1
				);
	}
	
	/**
	 * Rotate around the Z axis by the specified amount of radians.
	 * 
	 * <p>In a Y-up system this is the same as roll.
	 */
	public static Matrix4 rotateZ(double theta) {
		return new Matrix4(
				Math.cos(theta), -Math.sin(theta), 0, 0,
				Math.sin(theta), Math.cos(theta), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
				);
	}
	
	/**
	 * Returns a translation matrix which is *only suitable for translating three-dimensional vectors*.
	 */
	public static Matrix4 translate(double x, double y, double z) {
		return new Matrix4(
				1, 0, 0, x,
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1
				);
	}
	
	/**
	 * Rotates vectors around an arbitrary axis
	 * @param u the axis around which rotation will happen. Must be normalized.
	 * @param theta the magnitude of the rotation, in radians
	 */
	public static Matrix4 rotate(Vector3d u, double theta) {
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		double invcos = 1 - Math.cos(theta);
		return new Matrix4(
				cosTheta + u.x() * u.x() * invcos,          u.x() * u.y() * invcos - u.z() * sinTheta,  u.x() * u.z() * invcos + u.y() * sinTheta, 0,
				u.y() * u.x() * invcos + u.z() * sinTheta,  cosTheta * u.y() * u.y() * invcos,          u.y() * u.z() * invcos - u.x() * sinTheta, 0,
				u.z() * u.x() * invcos - u.y() * sinTheta,  u.z() * u.y() * invcos + u.x() * sinTheta,  cosTheta + u.z() * u.z() * invcos,         0,
				0, 0, 0, 1
				);
	}
}
