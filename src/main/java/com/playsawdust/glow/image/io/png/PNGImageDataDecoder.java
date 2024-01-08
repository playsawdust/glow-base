/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.io.png;

import java.io.IOException;
import java.util.Arrays;

import com.playsawdust.glow.image.ImageData;
import com.playsawdust.glow.image.SrgbImageData;
import com.playsawdust.glow.image.color.Colors;
import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.io.DataSlice;

public class PNGImageDataDecoder {
	public static final int FILTER_NONE = 0;
	public static final int FILTER_SUB = 1;
	public static final int FILTER_UP = 2;
	public static final int FILTER_AVERAGE = 3;
	public static final int FILTER_PAETH = 4;
	
	public static final int TYPE_GRAY = 0;
	public static final int TYPE_RGB  = 2;
	public static final int TYPE_INDEXED = 3;
	public static final int TYPE_GRAY_WITH_ALPHA = 4;
	public static final int TYPE_RGBA = 6;
	
	private static final RGBColor BLACK = new RGBColor(1, 0, 0, 0);
	private static final RGBColor WHITE = new RGBColor(1, 1, 1, 1);
	private static final RGBColor[] PALETTE_4GRAYS = { BLACK, new RGBColor(1, 0.33f, 0.33f, 0.33f), new RGBColor(1, 0.66f, 0.66f, 0.66f), WHITE };
	private static final RGBColor[] PALETTE_16GRAYS = {
		BLACK,
		gray(1,16), gray( 2,16), gray( 3,16), gray( 4,16), gray( 5,16), gray( 6,16), gray( 7,16),
		gray(8,16), gray(9,16), gray(10,16), gray(11,16), gray(12,16), gray(13,16), gray(14,16),
		WHITE
	};
	
	private static RGBColor gray(int index, int total) {
		if (index<=0) return BLACK;
		if (index>=total-1) return WHITE;
		float intensity = index / (float) (total-1);
		return new RGBColor(1, intensity, intensity, intensity);
	}
	
	public static void decode(DataSlice in, ImageData out, int colorType, int bitsPerSample, RGBColor[] palette) throws IOException {
		//boolean hasAlpha = (colorType==4) || (colorType==6);
		int sampleCount = switch(colorType) {
			case TYPE_GRAY -> 1; //Gray
			case TYPE_RGB -> 3; //RGB
			case TYPE_INDEXED -> 1; //Indexed
			case TYPE_GRAY_WITH_ALPHA -> 2; //Gray with Alpha
			case TYPE_RGBA -> 4; //RGBA
			default -> throw new IOException("Unknown colortype "+colorType);
		};
		
		if (palette==null) {
			boolean supplyPalette = switch(colorType) {
				case TYPE_GRAY -> true;
				case TYPE_INDEXED -> true;
				case TYPE_GRAY_WITH_ALPHA -> true;
				default -> false;
			};
				
			if (supplyPalette) {
				int colorCount = (int) Math.pow(2, bitsPerSample);
				palette = new RGBColor[colorCount];
				for(int i=0; i<colorCount; i++) {
					palette[i] = gray(i, colorCount);
				}
			}
		}
		
		int byteStride = (sampleCount * bitsPerSample) / 8;
		if (byteStride<1) byteStride = 1;
		int scanlineBits = sampleCount * bitsPerSample * out.getWidth();
		while(scanlineBits%8 != 0) scanlineBits++; //Pad scanline up to the next byte
		int scanlineBytes = scanlineBits / 8;
		
		//System.out.println("BitsPerSample: "+bitsPerSample+" Samples: "+sampleCount);
		//System.out.println("Scanline is "+scanlineBytes+" bytes");
		//System.out.println("DataSlice is "+in.length()+" bytes and we're at "+in.position());
		
		byte[] scanline = new byte[scanlineBytes];
		byte[] lastScanline = new byte[scanlineBytes];
		
		for(int y=0; y<out.getHeight(); y++) {
			//System.out.println("Processing line "+y);
			int filterByte = in.read();
			
			for(int i=0; i<scanlineBytes; i++) {
				int leftIndex = i-byteStride;
				scanline[i] = in.readI8s();
				int left = ((leftIndex<0) ? 0 : scanline[leftIndex] & 0xFF);
				int up = lastScanline[i] & 0xFF;
				int upLeft = (leftIndex<0) ? 0 : lastScanline[leftIndex] & 0xFF;
				
				scanline[i] = (byte) filter(scanline[i] & 0xFF, filterByte, left, up, upLeft);
			}
			
			DataSlice lineSlice = DataSlice.of(scanline);
			switch(colorType) {
				case 0 -> {
					switch(bitsPerSample) {
						case 1 -> decodeGray1Line(lineSlice, out, y, BLACK, WHITE);
						case 2 -> decodePalette2Line(lineSlice, out, y, PALETTE_4GRAYS);
						case 4 -> decodePalette4Line(lineSlice, out, y, PALETTE_16GRAYS);
						case 8 -> decodeGray8Line(lineSlice, out, y, false);
						case 16-> decodeGray16Line(lineSlice, out, y, false);
						default -> throw new IOException("Can't decode grays with "+bitsPerSample+" bits per sample.");
					}
				}
				case 2 -> {
					switch(bitsPerSample) {
						case 8 -> decodeSrgb8Line(lineSlice, out, y, false);
						case 16-> decodeSrgb16Line(lineSlice, out, y, false);
						default -> throw new IOException("Can't decode RGB with "+bitsPerSample+" bits per sample.");
					}
				}
				case 3 -> {
					switch (bitsPerSample) {
						case 1 -> decodePalette1Line(lineSlice, out, y, palette);
						case 2 -> decodePalette2Line(lineSlice, out, y, palette);
						case 4 -> decodePalette4Line(lineSlice, out, y, palette);
						case 8 -> decodePalette8Line(lineSlice, out, y, palette);
						default -> throw new IOException("Can't decode paletted images with "+bitsPerSample+" bits per sample.");
					}
				}
				case 4 -> {
					switch (bitsPerSample) {
						case 8 -> decodeGray8Line(lineSlice, out, y, true);
						case 16-> decodeGray16Line(lineSlice, out, y, true);
						default -> throw new IOException("Can't decode gray-alpha with "+bitsPerSample+" bits per sample.");
					}
				}
				case 6 -> {
					switch(bitsPerSample) {
						case 8 -> decodeSrgb8Line(lineSlice, out, y, true);
						case 16-> decodeSrgb16Line(lineSlice, out, y, true);
						default -> throw new IOException("Can't decode RGBA with "+bitsPerSample+" bits per sample.");
					}
				}
				default -> throw new IOException("Can't decode colortype "+colorType);
			}
			
			//Swap buffers
			byte[] tmp = lastScanline;
			lastScanline = scanline;
			scanline = tmp;
			Arrays.fill(scanline, (byte) 0);
		}
		
	}
	
	public static void decodePalette8Line(DataSlice in, ImageData out, int y, RGBColor[] palette) throws IOException {
		for(int x=0; x<out.getWidth(); x++) {
			int value = in.read() & 0xFF;
			out.setPixel(x, y, palette[value]);
		}
	}
	
	public static void decodePalette4Line(DataSlice in, ImageData out, int y, RGBColor[] palette) throws IOException {
		for(int ix = 0; ix < in.length(); ix++) {
			int packed = in.read();
			
			int a = (packed >> 4) & 0xF;
			int b = packed & 0xf;
			out.setPixel(ix * 2, y, palette[a]);
			out.setPixel(ix * 2 + 1, y, palette[b]);
		}
	}
	
	public static void decodePalette2Line(DataSlice in, ImageData out, int y, RGBColor[] palette) throws IOException {
		for(int ix = 0; ix < in.length(); ix++) {
			int packed = in.read();
			
			for(int i = 0; i < 4; i++) {
				int raw = ((packed >> (6-(i*2))) & 0x3);
				RGBColor cur = palette[raw];
				out.setPixel(ix * 4 + i, y, cur);
			}
		}
	}
	
	public static void decodePalette1Line(DataSlice in, ImageData out, int y, RGBColor[] palette) throws IOException {
		for(int ix = 0; ix < in.length(); ix++) {
			int packed = in.read();
			
			for(int i = 0; i < 8; i++) {
				RGBColor cur = (((packed >> (7-i)) & 0x1) == 1) ? palette[0] : palette[1];
				out.setPixel(ix * 8 + i, y, cur);
			}
		}
	}
	
	public static void decodeSrgb8Line(DataSlice in, ImageData out, int y, boolean hasAlpha) throws IOException {
		for(int x=0; x<out.getWidth(); x++) {
			int r = in.read() & 0xFF;
			int g = in.read() & 0xFF;
			int b = in.read() & 0xFF;
			int a = (hasAlpha) ? in.read() & 0xFF : 0xFF;
			
			out.setPixel(x, y, RGBColor.fromGamma(a/255f, r/255f, g/255f, b/255f));
		}
	}
	
	public static void decodeSrgb16Line(DataSlice in, ImageData out, int y, boolean hasAlpha) throws IOException {
		for(int x=0; x<out.getWidth(); x++) {
			int r = in.readI16u() & 0xFFFF;
			int g = in.readI16u() & 0xFFFF;
			int b = in.readI16u() & 0xFFFF;
			int a = (hasAlpha) ? in.readI16u() & 0xFFFF : 0xFFFF;
			
			out.setPixel(x, y, RGBColor.fromGamma(a / (float) 0xFFFF, r / (float) 0xFFFF, g / (float) 0xFFFF, b / (float) 0xFFFF));
		}
	}
	
	public static void decodeGray16Line(DataSlice in, ImageData out, int y, boolean hasAlpha) throws IOException {
		for(int x=0; x<out.getWidth(); x++) {
			int value = in.readI16u() & 0xFFFF;
			int a = (hasAlpha) ? in.readI16u() & 0xFFFF : 0xFFFF;
			
			out.setPixel(x, y, RGBColor.fromGamma(a / (float) 0xFFFF, value / (float) 0xFFFF, value / (float) 0xFFFF, value / (float) 0xFFFF));
		}
	}
	
	public static void decodeGray8Line(DataSlice in, ImageData out, int y, boolean hasAlpha) throws IOException {
		for(int x=0; x<out.getWidth(); x++) {
			int value = in.read() & 0xFF;
			int a = (hasAlpha) ? in.read() & 0xFF : 0xFF;
			
			out.setPixel(x, y, RGBColor.fromGamma(a / (float) 0xFF, value / (float) 0xFF, value / (float) 0xFF, value / (float) 0xFF));
		}
	}
	
	public static void decodeGray4Line(DataSlice in, ImageData out, int y, RGBColor[] palette) throws IOException {
		for(int ix = 0; ix < in.length(); ix++) {
			int packed = in.read();
			
			int a = (packed >> 4) & 0xF;
			int b = packed & 0xf;
			out.setPixel(ix * 2, y, palette[a]);
			out.setPixel(ix * 2 + 1, y, palette[b]);
		}
	}
	
	/*
	public static void decodeGray2Line(DataSlice in, ImageData out, int y, RGBColor[] palette) throws IOException {
		for(int ix = 0; ix < in.length(); ix++) {
			int packed = in.read();
			
			for(int i = 0; i < 4; i++) {
				int raw = ((packed >> (6-(i*2))) & 0x3);
				RGBColor cur = palette[raw];
				out.setPixel(ix * 4 + i, y, cur);
			}
		}
	}*/
	
	public static void decodeGray1Line(DataSlice in, ImageData out, int y, RGBColor black, RGBColor white) throws IOException {
		for(int ix = 0; ix < in.length(); ix++) {
			int packed = in.read();
			
			for(int i = 0; i < 8; i++) {
				RGBColor cur = (((packed >> (7-i)) & 0x1) == 1) ? white : black;
				out.setPixel(ix * 8 + i, y, cur);
			}
		}
	}
	
	/**
	 * Accepts a DataSlice of uncompressed (inflated) image data, and decodes it into the target ImageData.
	 * @param in the image data, stitched into one piece and inflated.
	 * @param out the image to pour image data into. The dimensions of this image will be used in decoding.
	 */
	/*
	public static void decodeSrgb(DataSlice in, ImageData out, boolean hasAlpha) throws IOException {
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
	}*/
	
	/*
	public static void decodeGrayscale8(DataSlice in, SrgbImageData out, boolean hasAlpha) throws IOException {
		for(int y=0; y<out.getHeight(); y++) {
			int filterByte = in.read();
			int left = 0x00;
			int leftAlpha = 0x00;
			
			for(int x=0; x<out.getWidth(); x++) {
				int value = in.read();
				int a = (hasAlpha) ? in.read() : 0xFF;
				
				int upSrgb = out.getSrgbPixel(x, y-1);
				int up = upSrgb & 0xFF;
				int upAlpha = (upSrgb >> 24) & 0xFF;
				
				int upLeftSrgb = out.getSrgbPixel(x-1, y-1);
				int upLeft = upLeftSrgb & 0xFF;
				int upLeftAlpha = (upLeftSrgb >> 24) & 0xFF;
				
				value = filter(value, filterByte, left, up, upLeft) & 0xFF;
				a = filter(a, filterByte, leftAlpha, upAlpha, upLeftAlpha) & 0xFF;
				int col = (a << 24) | (value << 16) | (value << 8) | value;
				out.setPixel(x, y, col);
				
				left = value;
				leftAlpha = a;
			}
		}
	}
	
	public static void decodeGrayscale8(DataSlice in, ImageData out, boolean hasAlpha) throws IOException {
		for(int y=0; y<out.getHeight(); y++) {
			int filterByte = in.read();
			int left = 0x00;
			int leftAlpha = 0x00;
			
			for(int x=0; x<out.getWidth(); x++) {
				int value = in.read();
				int a = (hasAlpha) ? in.read() : 0xFF;
				
				int upSrgb = out.getSrgbPixel(x, y-1);
				int up = upSrgb & 0xFF;
				int upAlpha = (upSrgb >> 24) & 0xFF;
				
				int upLeftSrgb = out.getSrgbPixel(x-1, y-1);
				int upLeft = upLeftSrgb & 0xFF;
				int upLeftAlpha = (upLeftSrgb >> 24) & 0xFF;
				
				value = filter(value, filterByte, left, up, upLeft) & 0xFF;
				a = filter(a, filterByte, leftAlpha, upAlpha, upLeftAlpha) & 0xFF;
				int col = (a << 24) | (value << 16) | (value << 8) | value;
				out.setPixel(x, y, col);
				
				left = value;
				leftAlpha = a;
			}
		}
	}*/
	
	/*
	public static void decodeGrayscale16(DataSlice in, ImageData out, boolean hasAlpha) throws IOException {
		for(int y=0; y<out.getHeight(); y++) {
			int filterByte = in.read();
			//if (filterByte!=FILTER_NONE) System.out.println(filterByte);
			//System.out.println("Y: "+y+", Filter: "+filterByte);
			int left = 0x0000;
			int leftAlpha = 0x0000;
			
			for(int x=0; x<out.getWidth(); x++) {
				int value = in.readI16u() & 0xFFFF;
				int a = ((hasAlpha) ? in.readI16u() : 0xFFFF) & 0xFFFF;
				
				
				RGBColor upColor = out.getLinearPixel(x, y-1);
				int up = (int) (Colors.linearElementToGamma(upColor.r()) * 0xFFFF);
				int upAlpha = (int) (upColor.alpha() * 0xFFFF);
				
				RGBColor upLeftColor = out.getLinearPixel(x-1, y-1);
				int upLeft = (int) (Colors.linearElementToGamma(upLeftColor.r()) * 0xFFFF);
				int upLeftAlpha = (int) (upLeftColor.alpha() * 0xFFFF);
				
				value = value; //filter16(value, filterByte, left, up, upLeft) & 0xFFFF;
				a = a; //filter16(a, filterByte, leftAlpha, upAlpha, upLeftAlpha) & 0xFFFF;
				
				float floatValue = value / (float) 0xFFFF;
				RGBColor fromGamma = RGBColor.fromGamma(a / (float) 0xFFFF, floatValue, floatValue, floatValue);
				
				out.setPixel(x, y, fromGamma);
				
				left = value;
				leftAlpha = a;
			}
		}
	}*/
	
	//public static void decodeGrayscale
	
	//TODO: TEST THE FUCK OUT OF THIS
	private static int filter(int value, int filterType, int left, int up, int upLeft) throws IOException {
		return switch(filterType) {
			case FILTER_NONE -> value;
			case FILTER_SUB -> ((byte) value + (byte) left) & 0xFF;
			case FILTER_UP -> ((byte) value + (byte) up) & 0xFF;
			case FILTER_AVERAGE -> ((byte) value + (byte) ((up + left) / 2)) & 0xFF;
			case FILTER_PAETH -> ((byte) value + (byte) paeth(left, up, upLeft)) & 0xFF;
			default -> throw new IOException("Unknown filter type #"+filterType);
		};
	}
	
	
	private static int paeth(int left, int up, int upLeft) {
		/*
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
		return upLeft;*/
		
		int a = left;
		int b = up;
		int c = upLeft;
		
		int p = a + b - c; // initial estimate
		int pa = Math.abs(p - a); // distances to a, b, c
		int pb = Math.abs(p - b);
		int pc = Math.abs(p - c);
		
		//return nearest of a,b,c,
		// breaking ties in order a,b,c.
		if (pa <= pb && pa <= pc) return a;
		if (pb <= pc) return b;
		return c;
	}
}
