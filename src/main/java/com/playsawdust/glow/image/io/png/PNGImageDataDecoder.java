/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2022 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.io.png;

import java.io.IOException;

import com.playsawdust.glow.image.SrgbImageData;
import com.playsawdust.glow.io.DataSlice;

public class PNGImageDataDecoder {
	public static final int FILTER_NONE = 0;
	public static final int FILTER_SUB = 1;
	public static final int FILTER_UP = 2;
	public static final int FILTER_AVERAGE = 3;
	public static final int FILTER_PAETH = 4;
	
	/**
	 * Accepts a DataSlice of uncompressed (inflated) image data, and decodes it into the target ImageData.
	 * @param in the image data, stitched into one piece and inflated.
	 * @param out the image to pour image data into. The dimensions of this image will be used in decoding.
	 */
	public static void decodeSrgb(DataSlice in, SrgbImageData out, boolean hasAlpha) throws IOException {
		for(int y=0; y<out.getHeight(); y++) {
			int filterByte = in.read();
			int priorR = 0x00;
			int priorG = 0x00;
			int priorB = 0x00;
			int priorA = 0x00;
			for(int x=0; x<out.getWidth(); x++) {
				int r = in.read();
				int g = in.read();
				int b = in.read();
				int a = (hasAlpha) ? in.read() : 0xFF;
				
				switch(filterByte) {
				case FILTER_NONE:
					break;
				case FILTER_SUB:
					r = ((byte)r + (byte)priorR) & 0xFF;
					g = ((byte)g + (byte)priorG) & 0xFF;
					b = ((byte)b + (byte)priorB) & 0xFF;
					a = ((byte)a + (byte)priorA) & 0xFF;
					
					priorR = r;
					priorG = g;
					priorB = b;
					priorA = a;
					break;
				case FILTER_UP: {
					int upColor = out.getSrgbPixel(x, y-1);
					byte upA = (byte)(upColor >> 24);
					byte upR = (byte)(upColor >> 16);
					byte upG = (byte)(upColor >>  8);
					byte upB = (byte)(upColor);
				
					r = ((byte)r + upR) & 0xFF;
					g = ((byte)g + upG) & 0xFF;
					b = ((byte)b + upB) & 0xFF;
					a = ((byte)a + upA) & 0xFF;
					break; //TODO: Does not set prior???
				}
				case FILTER_AVERAGE: {
					int upRGBA = out.getSrgbPixel(x, y-1);
					int upA = (upRGBA >> 24) & 0xFF;
					int upR = (upRGBA >> 16) & 0xFF;
					int upG = (upRGBA >>  8) & 0xFF;
					int upB = (upRGBA) & 0xFF;
					
					upA = (upA+priorA)/2;
					upR = (upR+priorR)/2;
					upG = (upG+priorG)/2;
					upB = (upB+priorB)/2;
					
					r = ((byte)r + (byte)upR) & 0xFF;
					g = ((byte)g + (byte)upG) & 0xFF;
					b = ((byte)b + (byte)upB) & 0xFF;
					a = ((byte)a + (byte)upA) & 0xFF;
					
					priorR = r;
					priorG = g;
					priorB = b;
					priorA = a;
					break;
				}
				case FILTER_PAETH: {
					int upRGBA = out.getSrgbPixel(x,y-1);
					int upA = (upRGBA >> 24) & 0xFF;
					int upR = (upRGBA >> 16) & 0xFF;
					int upG = (upRGBA >>  8) & 0xFF;
					int upB = (upRGBA) & 0xFF;
					
					int upLeftRGBA = out.getSrgbPixel(x-1, y-1);
					int upLeftA = (upLeftRGBA >> 24) & 0xFF;
					int upLeftR = (upLeftRGBA >> 16) & 0xFF;
					int upLeftG = (upLeftRGBA >>  8) & 0xFF;
					int upLeftB = (upLeftRGBA) & 0xFF;
					
					if (x>0) {
						r = ((byte)r + (byte)paeth(priorR, upR, upLeftR)) & 0xFF;
						g = ((byte)g + (byte)paeth(priorG, upG, upLeftG)) & 0xFF;
						b = ((byte)b + (byte)paeth(priorB, upB, upLeftB)) & 0xFF;
						a = ((byte)a + (byte)paeth(priorA, upA, upLeftA)) & 0xFF;
					} else {
						r = ((byte)r + upR) & 0xFF;
						g = ((byte)g + upG) & 0xFF;
						b = ((byte)b + upB) & 0xFF;
						a = ((byte)a + upA) & 0xFF;
					}
					
					priorR = r;
					priorG = g;
					priorB = b;
					priorA = a;
					break;
				}
				default:
					throw new IOException("Unknown filter type #"+filterByte);
				}
				
				out.setPixel(x, y, a << 24 | r << 16 | g << 8 | b);
			}
		}
	}
	
	private static int paeth(int left, int up, int upLeft) {
		//Distance to a/b/c
		int pLeft   = Math.abs(up - upLeft);
		int pUp     = Math.abs(left - upLeft);
		int pUpLeft = Math.abs(left + up - upLeft - upLeft);
		
		//Return the smallest of the distances. Order of tie-breaking is important!!!
		if (pLeft <= pUp && pLeft <= pUpLeft) {
			return left;
		}
		if (pUp <= pUpLeft) {
			return up;
		}
		return upLeft;
	}
}
