package com.playsawdust.glow.image;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Colors {
	//Do not instantiate
	private Colors() {}
	
	/*
	 * A note about gamma:
	 * 
	 * It sucks.
	 * 
	 * Real gamma is a whole chain of events between the camera and the eye. We call this a "transfer function" and
	 * eventually, hopefully, we arrive at a net gamma effect of 1.0, but that can be tricky because every camera,
	 * file format, monitor, and even the room affect the final viewing.
	 * 
	 * But that's not really why we ramp color this way in file formats.
	 * 
	 * As animals that need to do useful things in this world, we are much more sensitive to subtle variations in dark
	 * colors than nitpicking 1000-nit details. Bright is just bright to us. So when we have limited color depth, it
	 * makes more sense to log-scale the information. As it happens, CRTs also had a log-ish power requirement to drive
	 * the electron guns, it takes a lot less energy to make the beam brighter than it does to power it on in the first
	 * place.
	 * 
	 * but okay, if sRGB is log-scaled, and CRT monitor output is log-scaled, and our eyes are log-scaled, what's the
	 * problem? You're looking at this on a flat-panel LCD, LED, or oLED with no electron guns! Typical flat-panel
	 * displays use a lookup table to map input colors to a final output value based on the monitor's factory-known
	 * output characteristics.
	 * 
	 * That's almost all fine. Under normal circumstances, this produces a carefully coreographed, airtight chain
	 * between the input images and the eyeball. BUT, color mixing and light transport needs to happen in linear color
	 * space to look right. So we have to pick an input gamma to unpack from, and an output gamma to repack into when
	 * we're done.
	 * 
	 * As an input gamma for PNG images, I strongly suggest the W3C gamma of 2.2, which in the piecewise W3C PNG color
	 * function behaves a little more like a "pure" 1/2.35. Sometimes the image will come packaged with a gamma, use
	 * that instead; in practice, this will almost always be the integer 45455 (/100,000), corresponding to 1/W3C_GAMMA.
	 * 
	 * As output gamma, I would recommend a pure 2.4/1 correction if possible, as the brightly-lit daylight office
	 * settings that 2.2 are tuned for are actually pretty uncommon.
	 * 
	 * Final side-note, alpha samples are always stored as linear alpha. I don't know of a single image format or
	 * display system that gamma-ramps them, because our visual discrimination isn't log-curved for opacity, and no
	 * optical or electrical phenomenon distorts opacity like that.
	 */
	
	/**
	 * "Ideal" gamma suitable for an office setting with bright overhead lights and indirect daylight. The W3C
	 * recommends this value but very few people use it in practice, favoring 2.35 or 2.4 to reflect more typical
	 * viewing conditions such as a living room at night.
	 */
	public static final double W3C_GAMMA = 2.2;
	
	/**
	 * The gamma of typical viewing conditions: gentle room lights with no indirect daylight.
	 */
	public static final double TYPICAL_GAMMA = 2.4;
	
	/**
	 * This gamma value is most suitable for blacked-out viewing environments such as a movie theatre.
	 */
	public static final double BLACKOUT_GAMMA = 2.6;
	
	/**
	 * Converts a 64-bit AAAA_RRRR_GGGG_BBBB color to a 32-bit AARRGGBB color.
	 */
	public static int toShallowColor(long color) {
		//This and the next paragraph could be combined into one step if we need more throughput.
		int a = (int) (color >> 48) & 0xFFFF;
		int r = (int) (color >> 32) & 0xFFFF;
		int g = (int) (color >> 16) & 0xFFFF;
		int b = (int) (color      ) & 0xFFFF;
		
		a = (a >> 8) & 0xFF;
		r = (r >> 8) & 0xFF;
		g = (g >> 8) & 0xFF;
		b = (b >> 8) & 0xFF;
		
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	/**
	 * Converts a 32-bit AARRGGBB color to a 64-bit AAAA_RRRR_GGGG_BBBB color.
	 */
	public static long toDeepColor(int color) {
		long a = (color >> 24) & 0xFF;
		long r = (color >> 16) & 0xFF;
		long g = (color >>  8) & 0xFF;
		long b = (color      ) & 0xFF;
		
		a = (a << 8) | a;
		r = (r << 8) | r;
		g = (g << 8) | g;
		b = (b << 8) | b;
		
		return (a << 48) | (r << 32) | (g << 16) | (b);
	}
	
	/**
	 * Converts a gamma-ramped color to a linear color. In the returned color elements, 0.0 represents pure black, and
	 * 1.0 represents the edge of the srgb color gamut for that color element. Alpha is untouched and merely converted
	 * into a floating point from 0..1.
	 * @param srgb a color in gamma space.
	 * @param dest an array to hold the linear color elements, or null to create a new array.
	 * @return the passed-in array if nonnull, or a new array, containing the linear color elements equivalent to the passed in color.
	 */
	public static double[] gammaToLinear(long srgb, @Nullable double[] dest) {
		return gammaToLinear(srgb, dest, W3C_GAMMA);
	}
	
	/**
	 * Converts a gamma-ramped color to a linear color. In the returned color elements, 0.0 represents pure black, and
	 * 1.0 represents the edge of the srgb color gamut for that color element. Alpha is untouched and merely converted
	 * into a floating point from 0..1.
	 * @param srgb a color in gamma space.
	 * @param dest an array to hold the linear color elements, or null to create a new array.
	 * @param gamma the gamma setting the source color is in.
	 * @return the passed-in array if nonnull, or a new array, containing the linear color elements equivalent to the passed in color.
	 */
	public static double[] gammaToLinear(long srgb, @Nullable double[] dest, double gamma) {
		if (dest == null) dest = new double[4];
		if (dest.length < 4) throw new IllegalArgumentException("dest length must be at least 4");
		
		int a = (int) (srgb >> 48) & 0xFFFF;
		int r = (int) (srgb >> 32) & 0xFFFF;
		int g = (int) (srgb >> 16) & 0xFFFF;
		int b = (int) (srgb      ) & 0xFFFF;
		
		double divisor = 0xFFFF;
		dest[0] = a / divisor;
		dest[1] = gammaElementToLinear(r / divisor, gamma);
		dest[2] = gammaElementToLinear(g / divisor, gamma);
		dest[3] = gammaElementToLinear(b / divisor, gamma);
		
		return dest;
	}
	
	/**
	 * Converts a gamma-ramped color to a linear color. In the returned color elements, 0.0 represents pure black, and
	 * 1.0 represents the edge of the srgb color gamut for that color element. Alpha is untouched and merely converted
	 * into a floating point from 0..1.
	 * @param srgb a color in gamma space.
	 * @param dest an array to hold the linear color elements, or null to create a new array.
	 * @return the passed-in array if nonnull, or a new array, containing the linear color elements equivalent to the passed in color.
	 */
	public static double[] gammaToLinear(int srgb, @Nullable double[] dest) {
		return gammaToLinear(srgb, dest, W3C_GAMMA);
	}
	
	/**
	 * Converts a gamma-ramped color to a linear color. In the returned color elements, 0.0 represents pure black, and
	 * 1.0 represents the edge of the srgb color gamut for that color element. Alpha is untouched and merely converted
	 * into a floating point from 0..1.
	 * @param srgb a color in gamma space.
	 * @param dest an array to hold the linear color elements, or null to create a new array.
	 * @param gamma the gamma setting the source color is in.
	 * @return the passed-in array if nonnull, or a new array, containing the linear color elements equivalent to the passed in color.
	 */
	public static double[] gammaToLinear(int srgb, @Nullable double[] dest, double gamma) {
		if (dest == null) dest = new double[4];
		if (dest.length < 4) throw new IllegalArgumentException("dest length must be at least 4");
		
		int a = (srgb >> 24) & 0xFF;
		int r = (srgb >> 16) & 0xFF;
		int g = (srgb >>  8) & 0xFF;
		int b = (srgb      ) & 0xFF;
		
		dest[0] = a / 255.0;
		dest[1] = gammaElementToLinear(r / 255.0, gamma);
		dest[2] = gammaElementToLinear(g / 255.0, gamma);
		dest[3] = gammaElementToLinear(b / 255.0, gamma);
		
		return dest;
	}
	
	/** Converts one color sample from gamma colorspace into linear colorspace. */
	public static double gammaElementToLinear(double srgbElement) {
		return gammaElementToLinear(srgbElement, W3C_GAMMA);
	}
	
	/** Converts one color sample from gamma colorspace into linear colorspace. */
	public static double gammaElementToLinear(double srgb, double gamma) {
		if (srgb<0) return 0.0;
		
		if (srgb <= 0.04045) {
			return srgb / 12.92;
		} else if (srgb <= 1.0) {
			return Math.pow((srgb + 0.055) / 1.055, gamma);
		} else {
			return 1.0;
		}
	}
	
	/** Converts one color sample from linear colorspace into gamma colorspace. */
	public static double linearElementToGamma(double linearElement) {
		return linearElementToGamma(linearElement, W3C_GAMMA);
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
			return 255.0;
		}
	}
}
