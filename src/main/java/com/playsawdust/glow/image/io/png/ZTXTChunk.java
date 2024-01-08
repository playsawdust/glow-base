/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.io.png;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;

public class ZTXTChunk extends PNGChunk {
	public static final int TYPE_TAG = (byte)'z' << 24 | (byte)'T' << 16 | (byte)'X' << 8 | (byte)'t';
	
	public String keyword;
	public String value;
	
	public ZTXTChunk(String keyword, String value) {
		this.keyword = keyword;
		this.value = value;
	}
	
	public ZTXTChunk(int chunkType, DataSlice data) throws IOException {
		data.seek(0L);
		
		keyword = readNullDelimitedString(data);
		/*
		StringBuilder keywordBuilder = new StringBuilder();
		char ch = (char) data.read();
		while (ch != 0) {
			keywordBuilder.append(ch);
			ch = (char) data.read();
		}
		keyword = keywordBuilder.toString();*/
		//we've already read the null terminator
		
		int compressionType = data.read();
		switch(compressionType) {
		case 0:
			byte[] compressedData = readToEndOfChunk(data);
			
			InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(compressedData));
			byte[] uncompressedData = in.readAllBytes();
			value = new String(uncompressedData, StandardCharsets.ISO_8859_1);
			break;
			
		default:
			value = "ERROR - unknown compression type";
		}
	}
	
	@Override
	public int getChunkType() {
		return TYPE_TAG;
	}

	@Override
	public DataSlice getRawData() throws IOException {
		DataBuilder out = DataBuilder.create();
		
		for(byte b : keyword.getBytes(StandardCharsets.ISO_8859_1)) out.write(b);
		out.write(0); //Null separator
		out.write(0); //Compression Method = 0: zLib / Deflate
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DeflaterOutputStream deflater = new DeflaterOutputStream(baos);
		deflater.write(value.getBytes(StandardCharsets.ISO_8859_1));
		deflater.flush();
		byte[] valueBytes = baos.toByteArray();
		for(byte b : valueBytes) out.write(b);
		
		return out.toDataSlice();
	}

}
