/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
