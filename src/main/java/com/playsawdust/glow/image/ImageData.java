/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image;

import com.playsawdust.glow.image.color.Colors;
import com.playsawdust.glow.image.color.LABColor;
import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.image.color.XYZColor;

/**
 * Represents an object containing pixel image data. The object may or may not have extended precision.
 */
public interface ImageData extends Sized {
	/**
	 * Gets the value of the indicated pixel, packed as AARRGGBB (high-order to low-order bits), in gamma srgb color
	 * space. for instance, 0xFFFF00FF is opaque, fully-saturated magenta.
	 * 
	 * <p>If the pixel is outside the image, transparent black will be returned.
	 */
	int getSrgbPixel(int x, int y);
	
	/**
	 * Sets the value of the pixel at the indicated coordinates. If the pixel is outside the image, nothing happens.
	 */
	void setPixel(int x, int y, int srgb);
	
	/**
	 * Gets the value of the indicated pixel, as a linear (non-gamma, non-sRGB) RGB value with alpha.
	 */
	RGBColor getLinearPixel(int x, int y);
	
	void setPixel(int x, int y, RGBColor color);
	
	default void setPixel(int x, int y, XYZColor color) { setPixel(x,y, color.toRgb()); }
	default void setPixel(int x, int y, LABColor color) {setPixel(x,y, color.toXyz(Colors.WHITEPOINT_D65).toRgb()); }
}
