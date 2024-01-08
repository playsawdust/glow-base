/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image;

import java.util.Arrays;

import com.playsawdust.glow.image.color.RGBColor;

public class SrgbImageData implements ImageData {
	private int width = 0;
	private int height = 0;
	private int[] data = new int[0];
	
	public SrgbImageData() {}
	
	public SrgbImageData(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new int[width*height];
	}
	
	public SrgbImageData(int width, int height, int[] data) {
		this.width = width;
		this.height = height;
		this.data = data;
	}
	
	public SrgbImageData(LinearImageData image) {
		this(image.getWidth(), image.getHeight());
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				setPixel(x, y, image.getSrgbPixel(x, y));
			}
		}
	}
	
	@Override
	public int getWidth() { return this.width; }
	
	@Override
	public int getHeight() { return this.height; }
	
	public int[] getData() { return this.data; }
	
	@Override
	public void setPixel(int x, int y, int srgb) {
		if (x<0 || x>=width || y<0 || y>=height) return;
		data[y*width + x] = srgb;
	}
	
	@Override
	public int getSrgbPixel(int x, int y) {
		if (x<0 || x>=width || y<0 || y>=height) return 0;
		return data[y*width + x];
	}
	
	@Override
	public RGBColor getLinearPixel(int x, int y) {
		return new RGBColor(getSrgbPixel(x,y));
	}
	
	@Override
	public void setPixel(int x, int y, RGBColor color) {
		setPixel(x, y, color.toSrgb());
	}
	
	public void clear() {
		Arrays.fill(data, 0);
	}
	
	public void clear(int clearColor) {
		Arrays.fill(data, clearColor);
	}
	
	public void resize(int width, int height) {
		int[] newData = new int[width*height];
		
		int copyWidth = Math.min(this.width, width);
		int copyHeight = Math.min(this.height, height);
		
		for(int y=0; y<copyHeight; y++) {
			if (y>=height) break;
			
			System.arraycopy(data, y*this.width, newData, y*width, copyWidth);
		}
		
		this.width = width;
		this.height = height;
		this.data = newData;
		
	}
	
	public LinearImageData toLinear() {
		return new LinearImageData(this);
	}
}