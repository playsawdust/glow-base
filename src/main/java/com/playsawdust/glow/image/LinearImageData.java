/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image;

import com.playsawdust.glow.image.color.RGBColor;

public class LinearImageData implements ImageData {
	private int width;
	private int height;
	
	private float[] r;
	private float[] g;
	private float[] b;
	private float[] a;
	
	public LinearImageData() {
		width = 0;
		height = 0;
		r = new float[0];
		g = new float[0];
		b = new float[0];
		a = new float[0];
	}
	
	public LinearImageData(int width, int height) {
		this.width = width;
		this.height = height;
		int sz = width*height;
		r = new float[sz];
		g = new float[sz];
		b = new float[sz];
		a = new float[sz];
	}
	
	public LinearImageData(SrgbImageData image) {
		this.width = image.getWidth();
		this.height = image.getHeight();
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				setPixel(x, y, image.getLinearPixel(x, y));
			}
		}
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	public void resize(int width, int height) {
		float[] newA = new float[width*height];
		float[] newR = new float[width*height];
		float[] newG = new float[width*height];
		float[] newB = new float[width*height];
		
		int copyWidth = Math.min(this.width, width);
		int copyHeight = Math.min(this.height, height);
		
		for(int y=0; y<copyHeight; y++) {
			if (y>=height) break;
			
			int srcOfs = y*this.width;
			int destOfs = y*width;
			
			System.arraycopy(a, srcOfs, newA, destOfs, copyWidth);
			System.arraycopy(r, srcOfs, newR, destOfs, copyWidth);
			System.arraycopy(g, srcOfs, newG, destOfs, copyWidth);
			System.arraycopy(b, srcOfs, newB, destOfs, copyWidth);
		}
		
		this.width = width;
		this.height = height;
		this.a = newA;
		this.r = newR;
		this.g = newG;
		this.b = newB;
	}
	
	public SrgbImageData toSrgb() {
		return new SrgbImageData(this);
	}

	@Override
	public int getSrgbPixel(int x, int y) {
		if (x<0 || x>=width || y<0 || y>=height) return 0x00_000000;
		
		int index = y * width + x;
		
		return new RGBColor(a[index], r[index], g[index], b[index]).toSrgb();
	}

	@Override
	public void setPixel(int x, int y, int srgb) {
		if (x<0 || x>=width || y<0 || y>=height) return;
		
		RGBColor rgb = new RGBColor(srgb);
		int index = y * width + x;
		a[index] = rgb.alpha();
		r[index] = rgb.r();
		g[index] = rgb.g();
		b[index] = rgb.b();
	}

	@Override
	public RGBColor getLinearPixel(int x, int y) {
		if (x<0 || x>=width || y<0 || y>=height) return RGBColor.TRANSPARENT;
		
		int index = y * width + x;
		return new RGBColor(a[index], r[index], g[index], b[index]);
	}

	@Override
	public void setPixel(int x, int y, RGBColor color) {
		if (x<0 || x>=width || y<0 || y>=height) return;
		
		int index = y * width + x;
		a[index] = color.alpha();
		r[index] = color.r();
		g[index] = color.g();
		b[index] = color.b();
	}

}
