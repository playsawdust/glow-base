/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image;

import com.playsawdust.glow.image.color.BlendMode;
import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.render.Painter;

public class ImagePainter implements Painter {
	private ImageData target;
	private BlendMode mode;
	
	public ImagePainter(ImageData target, BlendMode mode) {
		this.target = target;
		this.mode = mode;
	}
	
	@Override
	public void drawImage(ImageData image, int destX, int destY, int srcX, int srcY, int width, int height, float opacity) {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				RGBColor src = image.getLinearPixel(srcX + x, srcY + y);
				RGBColor dest = target.getLinearPixel(destX + x, destY + y);
				RGBColor result = mode.blend(src, dest, opacity);
				target.setPixel(destX + x, destY + y, result);
			}
		}
	}

	@Override
	public void drawTintImage(ImageData image, int destX, int destY, int srcX, int srcY, int width, int height, RGBColor tintColor) {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				RGBColor src = image.getLinearPixel(srcX + x, srcY + y);
				RGBColor dest = target.getLinearPixel(destX + x, destY + y);
				
				RGBColor tintedSrc = BlendMode.NORMAL.blend(tintColor, src);
				RGBColor result = mode.blend(tintedSrc, dest);
				target.setPixel(destX + x, destY + y, result);
			}
		}
	}

	@Override
	public void drawPixel(int x, int y, RGBColor color) {
		RGBColor dest = target.getLinearPixel(x, y);
		RGBColor result = mode.blend(color, dest);
		target.setPixel(x, y, result);
	}

	@Override
	public int getWidth() {
		return target.getWidth();
	}

	@Override
	public int getHeight() {
		return target.getHeight();
	}
	
}
