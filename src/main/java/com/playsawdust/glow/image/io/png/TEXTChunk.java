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
import java.nio.charset.StandardCharsets;

import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;

public class TEXTChunk extends PNGChunk {
	public static final int TYPE_TAG = (byte)'t' << 24 | (byte)'E' << 16 | (byte)'X' << 8 | (byte)'t';
	
	public String keyword;
	public String value;
	
	public TEXTChunk(String keyword, String value) {
		this.keyword = keyword;
		this.value = value;
	}
	
	public TEXTChunk(int chunkType, DataSlice data) throws IOException {
		if (chunkType != TYPE_TAG) throw new IllegalArgumentException("Invalid chunk type tag.");
		
		keyword = readNullDelimitedString(data);
		
		byte[] valueData = readToEndOfChunk(data);
		value = new String(valueData, StandardCharsets.ISO_8859_1);
	}
	
	@Override
	public int getChunkType() {
		return TYPE_TAG;
	}

	@Override
	public DataSlice getRawData() throws IOException {
		DataBuilder out = DataBuilder.create();
		
		//ISO 8859-1 is specifically indicated by the W3C spec
		byte[] keywordBytes = keyword.getBytes(StandardCharsets.ISO_8859_1);
		byte[] valueBytes = value.getBytes(StandardCharsets.ISO_8859_1);
		
		for(byte b : keywordBytes) out.write(b);
		out.write(0); //null *delimiter*
		for(byte b : valueBytes) out.write(b);
		//not null-terminated!
		
		return out.toDataSlice();
	}

}
