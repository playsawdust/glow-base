/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.io.tiff;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import com.playsawdust.glow.image.ImageData;
import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.io.DataSlice;

public class TiffImageDataDecoder {
	public static void getLineWindow(DataSlice source, BaselineTiffHeader header, long srcX, long srcY, long length, ImageData dest, int destX, int destY) throws IOException {
		
		//long compression = ifd.getLong(source, IFD.TAG_COMPRESSION, -1L);
		//if (compression != 1L) throw new IOException("Don't know how to deal with compression type "+compression);
		
		//long photometricInterpretation = ifd.getLong(source, IFD.TAG_PHOTOMETRIC_INTERPRETATION, -1L);
		//long[] bitsPerSample = ifd.getLongs(source, IFD.TAG_BITS_PER_SAMPLE);
		//long samplesPerPixel = ifd.getLong(source, IFD.TAG_SAMPLES_PER_PIXEL, -1L);
		//long[] extraSamples = ifd.getLongs(source, IFD.TAG_EXTRA_SAMPLES);
		//long[] sampleFormat = ifd.getLongs(source, IFD.TAG_SAMPLE_FORMAT);
		
		//System.out.println("Interpretation: "+photometricInterpretation+", BitsPerSample: "+Arrays.toString(bitsPerSample)+", SamplesPerPixel: "+samplesPerPixel+", ExtraSamples: "+Arrays.toString(extraSamples)+", SampleFormat: "+Arrays.toString(sampleFormat));
		
		Function<DataSlice, RGBColor> pixelDecoder = null;
		if (header.colorType == TiffColorType.RGB) {
			if (header.bitsPerSample.length == 3) {
				// Plain RGB
				if (header.bitsPerSample[0] == 8 && header.bitsPerSample[1] == 8 && header.bitsPerSample[2] == 8) {
					pixelDecoder = TiffImageDataDecoder::rgb8uPixel;
				}
				
			} else if (header.bitsPerSample.length == 4) {
				if (header.premultiply) {
					//RGBA premul
					
					if (header.bitsPerSample[0] == 8 && header.bitsPerSample[1] == 8 && header.bitsPerSample[2] == 8 && header.bitsPerSample[3] == 8) {
						//System.out.println("Selected rgba8uPremul");
						pixelDecoder = TiffImageDataDecoder::rgba8uPremulPixel;
					}
					
				} else {
					//RGBA non-premul
					
					if (header.bitsPerSample[0] == 8 && header.bitsPerSample[1] == 8 && header.bitsPerSample[2] == 8 && header.bitsPerSample[3] == 8) {
						pixelDecoder = TiffImageDataDecoder::rgba8uPixel;
					}
				}
			}
		} else if (header.colorType == TiffColorType.BLACK_IS_ZERO) {
			//We have some tricks for this!
			//if (header.sampleFormat.length == 1 && header.sampleFormat[0] == 2) { //single i16s sample per pixel
			if (header.sampleFormat.length == 1) {
				if (header.sampleFormat[0] == 2) { //signed
					if (header.bitsPerSample[0] == 16) {
						pixelDecoder = TiffImageDataDecoder::gray16sPixel;
					}
				} else if (header.sampleFormat[0] == 1) { //unsigned
					if (header.bitsPerSample[0] == 16) {
						pixelDecoder = TiffImageDataDecoder::gray16uPixel;
					}
				}
			}
		}
		
		//System.out.println("PhotometricInterpretation: "+header.colorType
		//		+ ", SampleFormat: "+Arrays.toString(header.sampleFormat)
		//		+ ", BitsPerSample: "+Arrays.toString(header.bitsPerSample)
		//		+ ", Premultiply: "+header.premultiply);
		//		+ ", ByteCounts: "+Arrays.toString(header.stripByteCounts)
		//);
		
		if (pixelDecoder == null) throw new IOException("Don't know how to unpack these pixels!"
				+ " PhotometricInterpretation: "+header.colorType
				+ ", SampleFormat: "+Arrays.toString(header.sampleFormat)
				+ ", BitsPerSample: "+Arrays.toString(header.bitsPerSample)
				+ ", Premultiply: "+header.premultiply);
		
		long targetStrip = srcY / header.rowsPerStrip;
		long linesToSkip = (srcY - (targetStrip * header.rowsPerStrip));
		long availablePixels = header.width - srcX;
		
		if (srcY < 0 || targetStrip < 0 || targetStrip >= header.stripOffsets.length) {
			//System.out.println("Zeroing out this row.");
			//Zero out this row of the image
			for(int i=0; i<dest.getWidth(); i++) {
				dest.setPixel(i, destY, RGBColor.TRANSPARENT);
			}
		} else {
			long seekTarget = header.stripOffsets[(int) targetStrip] + (linesToSkip * header.rowStride) + (srcX * header.pixelStride);
			source.seek(seekTarget);
			
			//source.seek(header.stripOffsets[(int) targetStrip]);
			//int linesToSkip = (int) (srcY - (targetStrip * header.rowsPerStrip));
			//for(int y=0; y<linesToSkip; y++) {
			//	for(long x=0; x<header.width; x++) {
			//		pixelDecoder.apply(source);
			//	}
			//}
			
			//System.out.println("Discarding "+srcX+" pixels...");
			//if (srcX>0) {
			//	//TODO: Read in and discard pixels
			//	for(int i=0; i<srcX; i++) pixelDecoder.apply(source);
			//}
			
			long pixelsToCopy = Math.min(length, availablePixels);
			
			//System.out.println("Reading in "+length+" pixels...");
			for(int i=0; i<pixelsToCopy; i++) {
				RGBColor col = pixelDecoder.apply(source);
				dest.setPixel(destX + i, destY, col);
			}
			//System.out.println("Done.");
		}
	}
	
	public static RGBColor rgb8uPixel(DataSlice source) {
		try {
			int r = source.read();
			int g = source.read();
			int b = source.read();
			return new RGBColor(1.0f, r / 255f, g / 255f, b / 255f);
		} catch (Throwable t) {
			return RGBColor.TRANSPARENT;
		}
	}
	
	public static RGBColor rgba8uPremulPixel(DataSlice source) {
		try {
			int ir = source.read();
			int ig = source.read();
			int ib = source.read();
			int ia = source.read();
			
			float a = ia / 255f;
			float r = (ir / 255f) / a;
			float g = (ig / 255f) / a;
			float b = (ib / 255f) / a;
			
			return new RGBColor(a, r, g, b);
		} catch (Throwable t) {
			return RGBColor.TRANSPARENT;
		}
	}
	
	public static RGBColor rgba8uPixel(DataSlice source) {
		try {
			int ir = source.read();
			int ig = source.read();
			int ib = source.read();
			int ia = source.read();
			
			float a = ia / 255f;
			float r = ir / 255f;
			float g = ig / 255f;
			float b = ib / 255f;
			
			return new RGBColor(a, r, g, b);
		} catch (Throwable t) {
			return RGBColor.TRANSPARENT;
		}
	}
	
	//TODO: I'm pretty sure -Short.MIN_VALUE (0x8000) is the correct value and the result is getting washed out by gamma.
	private static final int SHORT_SIGNED_OFFSET = 0x2000; //-Short.MIN_VALUE;
	public static RGBColor gray16sPixel(DataSlice source) {
		try {
			int value = (short) source.readI16s();
			//value = (short)(value + 0x8000);// & 0xFFFF);
			
			//if (value < 0) {
				float level = (value + SHORT_SIGNED_OFFSET) / (float) 0xFFFF;
				return new RGBColor(1.0f, level, level, level);
				//value = -value - 1; //0..32767
				//value = ((value + 0x8000) & 0xFFFF) / 2 + (0xFFFF / 4);
			//} else {
				//if (value == 0) return new RGBColor(1.0f, 0, 0, 0);
				
				//value = (value / 2);
				//value = (value + 0x8000) & 0xFFFF;
				
				//value = value + -Short.MIN_VALUE;
			//	float level = (value + SHORT_SIGNED_OFFSET) / (float) 0xFFFF;
			//	return new RGBColor(1.0f, level, level, level);
			//}
			//float level = (value / (float) 0xFFFF);
			
			//return new RGBColor(1.0f, level, level, level);
		} catch (Throwable t) {
			return RGBColor.TRANSPARENT;
		}
	}
	
	public static RGBColor gray16uPixel(DataSlice source) {
		try {
			int value = source.readI16u();
			float level = (value / (float) 0xFFFF);
			
			return new RGBColor(1.0f, level, level, level);
		} catch (Throwable t) {
			return RGBColor.TRANSPARENT;
		}
	}
	//public static RGBColor rgb16uPixel(DataSlice source) throws IOException {
	//	
	//}
}
