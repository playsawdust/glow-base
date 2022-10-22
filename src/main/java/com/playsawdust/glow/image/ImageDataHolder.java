package com.playsawdust.glow.image;

/**
 * Represents an object containing pixel image data. The object may or may not have extended precision.
 */
public interface ImageDataHolder {
	/**
	 * Gets the value of the indicated pixel, packed as AARRGGBB (high-order to low-order bits), in gamma srgb color
	 * space. for instance, 0xFFFF00FF is opaque, fully-saturated magenta.
	 * 
	 * <p>If the pixel is outside the image, transparent black will be returned.
	 */
	int getPixel(int x, int y);
	
	/**
	 * Gets the value of the indicated pixel, packed as AAAA RRRR GGGG BBBB (high-order to low-order bits), in gamma
	 * srgb color space. For instance, 0xFFFF_FFFF_0000_FFFF is opaque, fully saturated magenta.
	 * 
	 * <p>If the pixel is outside the image, transparent black will be returned.
	 */
	long getDeepPixel(int x, int y);
	
	/**
	 * Sets the value of the pixel at the indicated coordinates. If the pixel is outside the image, nothing happens.
	 */
	void setPixel(int x, int y, int color);
	
	/**
	 * Sets the value of the pixel at the indicated coordinates. If the pixel is outside the image, nothing happens. If
	 * this image is unable to hold extended color depth, the precision of the color will be reduced.
	 */
	void setDeepPixel(int x, int y, long color);
}
