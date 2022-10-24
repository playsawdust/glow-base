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
package com.playsawdust.glow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.playsawdust.glow.vecmath.Matrix4;
import com.playsawdust.glow.vecmath.Vector3d;

public class TestMatrix4 {
	@Test
	public void testSpecificInverse() {
		Matrix4 subject = new Matrix4(
				1, 0, 0, 5,
				0, 2, 0, 4,
				0, 0, 3, 3,
				0, 0, 0, 1
				);
		
		Matrix4 expected = new Matrix4(
				1, 0,   0,      -5,
				0, 0.5, 0,      -2,
				0, 0,   0.3333, -1,
				0, 0,   0,       1
				);
		
		Matrix4 actual = subject.invert();
		
		Assertions.assertArrayEquals(expected.toArray(null), actual.toArray(null), 0.0001);
		
		Matrix4 hopefullyIdentity = subject.multiply(actual);
		Assertions.assertArrayEquals(Matrix4.IDENTITY.toArray(null), hopefullyIdentity.toArray(null), 0.0001);
	}
	
	
	@Test
	public void testInverse() {
		Matrix4 subject = Matrix4.rotate(new Vector3d(12,5,7).normalize(), Math.PI/3);
		
		Matrix4 inverseSubject = subject.invert();
		Matrix4 actual = inverseSubject.multiply(subject);
		
		Assertions.assertArrayEquals(Matrix4.IDENTITY.toArray(null), actual.toArray(null), 0.0001);
	}
}
