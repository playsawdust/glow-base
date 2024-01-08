/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.vector;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.playsawdust.glow.vecmath.Vector2d;

public class CubicBezier implements Curve {
	private final Vector2d a;
	private final Vector2d b;
	private final Vector2d c;
	private final Vector2d d;
	
	public CubicBezier(Vector2d a, Vector2d b, Vector2d c, Vector2d d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public List<LineSegment> approximate(int maxSegments, @Nullable List<LineSegment> destination) {
		// TODO Auto-generated method stub
		return null;
	}
}
