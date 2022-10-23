/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.color;

import com.playsawdust.glow.vecmath.Matrix3;
import com.playsawdust.glow.vecmath.Vector3d;

public class Colors {
	//Do not instantiate
	private Colors() {}
	
	/**
	 * The CIE illuminant "A". This represents the white spectrum of an incandescent bulb.
	 */
	public static Vector3d WHITEPOINT_A = new Vector3d(109.85, 100.00, 35.58);
	
	/**
	 * The D series of CIE standard illuminants are intended to represent daylight. Particularly useful is this D65
	 * white light spectrum, which is intented to represent an "average" daylight setting at a color temperature of
	 * 6504K. Unless there are specific reasons not to, this illuminant should be used for color transformations.
	 * 
	 * <p>Note that this is using the standard 2-degree observer, not the supplementary 10-degree observer.
	 */
	public static Vector3d WHITEPOINT_D65 = new Vector3d(95.047, 100.00, 108.883);
	
	/**
	 * CIE illuminant E is a perfectly flat spectrum. Because it's not a black-body-style radiator, it has no color
	 * temperature. This is the spectrum of the XYZ colorspace.
	 */
	public static Vector3d WHITEPOINT_E = new Vector3d(100.0, 100.0, 100.0);
	
	/**
	 * "Ideal" gamma suitable for an office setting with bright overhead lights and indirect daylight. The W3C
	 * recommends this value but very few people use it in practice, favoring 2.35 or 2.4 to reflect more typical
	 * viewing conditions such as a living room at night.
	 */
	public static final float IDEAL_GAMMA = 2.2f;
	
	/**
	 * The gamma of typical viewing conditions: gentle room lights with no indirect daylight.
	 */
	public static final float SRGB_GAMMA = 2.4f;
	
	/**
	 * This gamma value is most suitable for blacked-out viewing environments such as a movie theatre.
	 */
	public static final float BLACKOUT_GAMMA = 2.6f;
	
	public static Matrix3 RGB_TO_CIEXYZ = new Matrix3(
			0.4124, 0.3576, 0.1805,
			0.2126, 0.7152, 0.0722,
			0.0193, 0.1192, 0.9505);
	
	/**
	 * This matrix represents a more accurate inversion of the {@link #RGB_TO_CIEXYZ} matrix than the original CIE
	 * standard, and was introduced in a later amendment.
	 */
	public static Matrix3 CIEXYZ_TO_RGB = new Matrix3(
			 3.2406255, -1.5372080, -0.4986286,
			-0.9689307,  1.8757561,  0.0415175,
			 0.0557101, -0.2040211,  1.0569959);
	
	/** Converts one color sample from gamma colorspace into linear colorspace. */
	public static float gammaElementToLinear(float srgbElement) {
		return gammaElementToLinear(srgbElement, SRGB_GAMMA);
	}
	
	/** Converts one color sample from gamma colorspace into linear colorspace. */
	public static float gammaElementToLinear(float srgb, float gamma) {
		if (srgb<0) return 0.0f;
		
		if (srgb <= 0.04045) {
			return srgb / 12.92f;
		} else if (srgb <= 1.0) {
			return (float) Math.pow((srgb + 0.055) / 1.055, gamma);
		} else {
			return 1.0f;
		}
	}
	
	/** Converts one color sample from linear colorspace into gamma colorspace. */
	public static double linearElementToGamma(double linearElement) {
		return linearElementToGamma(linearElement, SRGB_GAMMA);
	}
	
	/** Converts one color sample from linear colorspace into gamma colorspace. */
	public static double linearElementToGamma(double linearElement, double gamma) {
		if (linearElement<0) {
			return 0;
		} else if (linearElement <= 0.0031308) {
			return linearElement * 12.92;
		} else if (linearElement <= 1.0) {
			return 1.055 * Math.pow(linearElement, 1.0 / gamma) - 0.055;
		} else {
			return 1.0;
		}
	}
	
	/*
	public static XYZColor linearToXyz(RGBColor color) {
		Vector3d result = RGB_TO_CIEXYZ.transform(new Vector3d(color.r(), color.g(), color.b()));
		return new XYZColor(color.alpha(), result);
	}*/
}
