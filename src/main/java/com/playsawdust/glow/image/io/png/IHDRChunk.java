/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.io.png;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.playsawdust.glow.io.DataSlice;

public class IHDRChunk extends PNGChunk {
	public static final int TYPE_TAG = (byte)'I' << 24 | (byte)'H' << 16 | (byte)'D' << 8 | (byte)'R';
	
	public IHDRChunk(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public IHDRChunk(int chunkType, DataSlice data) throws IOException {
		if (chunkType != TYPE_TAG) throw new IllegalArgumentException("IHDR data can only be read from a valid IHDR chunk.");
		
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
	public int compression = 0;
	public int filterMethod = 0;
	public int interlaceMethod = 0;
	
	@Override
	public int getChunkType() {
		return TYPE_TAG;
	}

	@Override
	public DataSlice getRawData() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(baos);
		
		dout.writeInt(width);
		dout.writeInt(height);
		dout.write(bitDepth);
		dout.write(colorType);
		dout.write(compression);
		dout.write(filterMethod);
		dout.write(interlaceMethod);
		
		return DataSlice.of(baos.toByteArray());
	}
}
