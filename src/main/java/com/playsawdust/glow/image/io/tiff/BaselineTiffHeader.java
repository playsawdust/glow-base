package com.playsawdust.glow.image.io.tiff;

import java.io.IOException;

import com.playsawdust.glow.io.DataSlice;

public class BaselineTiffHeader {
	
	public long width = 0L;  //i16u or i32u
	public long height = 0L; //i16u or i32u
	
	public TiffColorType colorType = TiffColorType.UNKNOWN;
	
	public int[] bitsPerSample; //i16u[]
	public int[] sampleFormat;  //i16u[]
	
	public long compression;
	
	public boolean premultiply;
	
	public long pixelStride; //i16u, synthetic
	public long rowStride; //i16u * i16u or i32u * i32u, synthetic
	
	public long rowsPerStrip; //i16u or i32u
	public long[] stripOffsets; //i16u[] or i32u[]
	public long[] stripByteCounts;
	
	
	public BaselineTiffHeader(IFD ifd, DataSlice in) throws IOException {
		width = ifd.getLong(in, IFD.TAG_IMAGE_WIDTH, 0L);
		height = ifd.getLong(in, IFD.TAG_IMAGE_HEIGHT, 0L);
		
		colorType = TiffColorType.of((int) ifd.getLong(in, IFD.TAG_PHOTOMETRIC_INTERPRETATION, -1L));
		
		bitsPerSample = ifd.getInts(in, IFD.TAG_BITS_PER_SAMPLE);
		sampleFormat = ifd.getInts(in, IFD.TAG_SAMPLE_FORMAT);
		
		compression = ifd.getLong(in, IFD.TAG_COMPRESSION, -1);
		//System.out.println("Compression: "+compression);
		
		int[] extraSamples = ifd.getInts(in, IFD.TAG_EXTRA_SAMPLES);
		if (extraSamples.length>=1) {
			if (extraSamples[0] == 1) premultiply = true;
		}
		
		int bitsPerPixel = 0;
		for(int i=0; i<bitsPerSample.length; i++) bitsPerPixel += bitsPerSample[i];
		pixelStride = (int) Math.ceil(bitsPerPixel / 8f); //Expect pixels to be padded out to whole bytes. This might be wrong but it's the best bet without knowing.
		rowStride = pixelStride * width;
		
		rowsPerStrip = ifd.getLong(in, IFD.TAG_ROWS_PER_STRIP, 0L);
		stripOffsets = ifd.getLongs(in, IFD.TAG_STRIP_OFFSETS);
		stripByteCounts = ifd.getLongs(in, IFD.TAG_STRIP_BYTE_COUNTS);
		//System.out.println(ifd.explain(IFD.TAG_STRIP_BYTE_COUNTS));
		//System.out.println(ifd.getLong(in, IFD.TAG_STRIP_BYTE_COUNTS, -1));
	}
}
