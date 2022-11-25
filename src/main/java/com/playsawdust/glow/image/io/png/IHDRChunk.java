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

import com.playsawdust.glow.image.io.PngImageIO;
import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;

public class IHDRChunk extends PNGChunk {
	public static final int TYPE_TAG = (byte)'I' << 24 | (byte)'H' << 16 | (byte)'D' << 8 | (byte)'R';
	
	public static final int COMPRESSION_PNG = 0;
	public static final int FILTER_METHOD_NONE = 0;
	public static final int INTERLACE_NONE = 0;
	
	public IHDRChunk(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public IHDRChunk(int chunkType, DataSlice data) throws IOException {
		if (chunkType != TYPE_TAG) throw new IllegalArgumentException("Invalid chunk type tag.");
		
		width = data.readI32s();
		height = data.readI32s();
		bitDepth = data.read();
		colorType = data.read();
		compression = data.read();
		filterMethod = data.read();
		interlaceMethod = data.read();
	}
	
	public int width;
	public int height;
	/* Size of each sample in bits */
	public int bitDepth = 8;
	public int colorType = PngImageIO.COLORTYPE_RGBA;
	public int compression = COMPRESSION_PNG;
	public int filterMethod = FILTER_METHOD_NONE;
	public int interlaceMethod = INTERLACE_NONE;
	
	@Override
	public int getChunkType() {
		return TYPE_TAG;
	}

	@Override
	public DataSlice getRawData() throws IOException {
		DataBuilder out = DataBuilder.create();
		
		out.writeI32s(width);
		out.writeI32s(height);
		out.write(bitDepth);
		out.write(colorType);
		out.write(compression);
		out.write(filterMethod);
		out.write(interlaceMethod);
		
		return out.toDataSlice();
	}
}
