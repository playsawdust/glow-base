/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.vecmath;

/**
 * Represents a bivector (a plane that passes through the origin) in three dimensions.
 * 
 * <p>To get a sense of what the components mean, think about the parallelogram that two vectors makes when their tails
 * are at the origin. The bivector elements are the area of the "shadow" this parallelogram makes when projected
 * onto each respective basis plane.
 */
public record Bivector(double xy, double xz, double yz) {
	public static final Bivector XY = fromAxisVector(0, 0, 1);
	public static final Bivector XZ = fromAxisVector(0, 1, 0);
	public static final Bivector YZ = fromAxisVector(1, 0, 0);
	
	
	
	public static Bivector fromAxisVector(double x, double y, double z) {
		return new Bivector(z, -y, x);
	}
}
