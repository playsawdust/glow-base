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

public record Rotor(double angle, Bivector plane) {
	
	public Vector3d transform(Vector3d vec) {
		//Transforms are a "sandwich" of geometric products
		
		
		//geometricProduct(this, vec)
		Vector4d f = new Vector4d(
				angle * vec.x() + plane.xy() * vec.y() + plane.xz() * vec.z(),
				angle * vec.y() - plane.xy() * vec.x() + plane.yz() * vec.z(),
				angle * vec.z() - plane.xz() * vec.x() - plane.yz() * vec.y(),
				plane.xy() * vec.z() - plane.xz() * vec.y() + plane.yz() * vec.x()
				);
		
		//geometricProduct(f, this)
		return new Vector3d(
				angle * f.x() + plane.xy() * f.y() + plane.xz() * f.z() + plane.yz() * f.w(),
				angle * f.y() - plane.xy() * f.x() - plane.xz() * f.w() + plane.yz() * f.z(),
				angle * f.z() + plane.xy() * f.w() - plane.xz() * f.x() - plane.yz() * f.y()
				);
	}
}
