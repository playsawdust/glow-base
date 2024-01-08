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
 * Represents a linear (non-gamma-ramped) RGB color
 */
public record RGBColor(float alpha, float r, float g, float b) {
	public static RGBColor TRANSPARENT = new RGBColor(0,0,0,0);
	
	public RGBColor(int srgb) {
		this(
				((srgb >> 24) & 0xFF) / 255.0f,
				Colors.gammaElementToLinear(((srgb >> 16) & 0xFF) / 255.0f, Colors.SRGB_GAMMA),
				Colors.gammaElementToLinear(((srgb >>  8) & 0xFF) / 255.0f, Colors.SRGB_GAMMA),
				Colors.gammaElementToLinear(((srgb      ) & 0xFF) / 255.0f, Colors.SRGB_GAMMA)
				);
	}
	
	public RGBColor(float alpha, Vector3d rgb) {
		this(alpha, (float) rgb.x(), (float) rgb.y(), (float) rgb.z());
	}
	
	public XYZColor toXyz() {
		Vector3d result = Colors.RGB_TO_CIEXYZ.transform(new Vector3d(r, g, b));
		return new XYZColor(alpha, result);
	}

	public int toSrgb() {
		int r = (int) (Colors.linearElementToGamma(this.r, Colors.SRGB_GAMMA) * 255);
		int g = (int) (Colors.linearElementToGamma(this.g, Colors.SRGB_GAMMA) * 255);
		int b = (int) (Colors.linearElementToGamma(this.b, Colors.SRGB_GAMMA) * 255);
		int a = (int) (alpha * 255);
		
		// We can expect to encounter some out-of-gamut colors here; clamp everything rather than &'ing so that we hit
		// the closest in-gamut color to this object.
		if (r>0xFF) r=0xFF; if (r<0) r=0;
		if (g>0xFF) g=0xFF; if (g<0) g=0;
		if (b>0xFF) b=0xFF; if (b<0) b=0;
		a &= 0xFF; //Out of range numbers shouldn't happen even out-of-gamut, so we can just blast away any extra bits to be safe.
		
		return a << 24 | r << 16 | g << 8 | b;
	}
	
	/**
	 * Returns a version of this color with opacity scaled to the specified value. If this is an opaque color, the
	 * alpha of the returned color with be equal to the passed-in opacity. If this is a partially translucent color, the
	 * returned color with have alpha of opacity * this.getAlpha()
	 */
	public RGBColor withOpacity(float opacity) {
		return new RGBColor(alpha * opacity, r, g, b);
	}
	
	/**
	 * Returns a version of this color with a rescaled luminance. Values between 0 and 1 will darken the color, while
	 * values greater than 1 will lighten it.
	 */
	public RGBColor withLuminance(float value) {
		XYZColor xyz = toXyz();
		XYZColor modified = new XYZColor(xyz.alpha(), xyz.x(), xyz.y()*value, xyz.z());
		return modified.toRgb();
	}
	
	/**
	 * Returns a lighter version of this color.
	 */
	public RGBColor lighter(float amount) {
		XYZColor xyz = toXyz();
		XYZColor modified = new XYZColor(xyz.alpha(), xyz.x(), xyz.y()+amount, xyz.z());
		return modified.toRgb();
	}
	
	/**
	 * Returns a darker version of this color.
	 */
	public RGBColor darker(float amount) {
		XYZColor xyz = toXyz();
		XYZColor modified = new XYZColor(xyz.alpha(), xyz.x(), xyz.y()-amount, xyz.z());
		return modified.toRgb();
	}
	
	public static RGBColor fromGamma(float alpha, float r, float g, float b) {
		return new RGBColor(
				alpha,
				Colors.gammaElementToLinear(r, Colors.SRGB_GAMMA),
				Colors.gammaElementToLinear(g, Colors.SRGB_GAMMA),
				Colors.gammaElementToLinear(b, Colors.SRGB_GAMMA)
				);
	}
}
