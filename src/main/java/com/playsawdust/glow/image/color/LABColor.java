/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.color;

import com.playsawdust.glow.vecmath.Vector3d;

/**
 * Represents a CIE L*a*b* color. These colors live in a "perceptual colorspace" that mimicks how we percieve colors.
 * 
 * <p>L* is luminancce. Zero indicates black, and 100 indicates fully white (at the white point indicated by the
 * illuminant used with this color; use {@link XYZColor#WHITEPOINT_D65} if unsure). 
 * 
 * <p>a* is a chromaticity component. Negative values are green and positive values are red.
 * 
 * <p>b* is a chromaticity component. Negative values are blue and positive values are yellow.
 */
public record LABColor(float alpha, float l, float a, float b) {
	
	/**
	 * Converts this color into CIE XYZ format, from which RGB is reachable.
	 * @param whitePoint the white point reference for this color; use {@link XYZColor#WHITEPOINT_D65} if unsure.
	 *        Note: Illuminants / white points must be specified as tristimulus values (Y=100)
	 */
	public XYZColor toXyz(Vector3d whitePoint) {
		float delta = 6/29f;
		
		float p = (l + 16) / 116;
		
		float y = (float) (whitePoint.y() / 100) * p * p * p;
		//float yn = y / (float) (whitePoint.y() / 100);
		
		float x = (float) Math.pow(p + (a / 500), 3);
		float z = (float) Math.pow(p - (b / 200), 3);
		
		if (x < delta) {
			float xtemp = p + (a / 500);
			x = delta * delta * 3f * (xtemp - (4/29f));
		}
		
		if (l < delta) {
			y = p / 903.3f;
		}
		
		if (z < delta) {
			float ztemp = p - (b / 200);
			z = delta * delta * 3f * (ztemp - (4/29f));
		}
		
		return new XYZColor(alpha, x * (float) (whitePoint.x()/100), y, z * (float) (whitePoint.z()/100));
	}
}
