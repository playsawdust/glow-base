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
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;

public abstract class PNGChunk {
	
	public static PNGChunk readChunk(DataSlice in) throws IOException {
		int length = in.readI32s();
		int chunkType = in.readI32s();
		byte[] chunkData = new byte[length];
		in.copy(chunkData);
		
		DataSlice slice = DataSlice.of(chunkData);
		long crc = in.readI32s() & 0xFFFFFFFFL;
		
		CRC32 crcCheck = new CRC32();
		//CRC covers the chunkType and data but not the length or the crc itself
		crcCheck.update((chunkType >> 24) & 0xFF);
		crcCheck.update((chunkType >> 16) & 0xFF);
		crcCheck.update((chunkType >>  8) & 0xFF);
		crcCheck.update((chunkType >>  0) & 0xFF);
		
		crcCheck.update(chunkData);
		long checkedCrc = crcCheck.getValue();
		if (checkedCrc != crc) {
			return new RawPNGChunk(chunkType, slice);
		} else {
			switch(chunkType) {
			
			case IHDRChunk.TYPE_TAG:
				try {
					return new IHDRChunk(chunkType, slice);
				} catch (Throwable t) {
					return new RawPNGChunk(chunkType, slice);
				}
			
			case IDATChunk.TYPE_TAG:
				try {
					return new IDATChunk(chunkType, slice);
				} catch (Throwable t) {
					return new RawPNGChunk(chunkType, slice);
				}
			
			case TEXTChunk.TYPE_TAG:
				try {
					return new TEXTChunk(chunkType, slice);
				} catch (Throwable t) {
					return new RawPNGChunk(chunkType, slice);
				}
				
			case ZTXTChunk.TYPE_TAG:
				try {
					return new ZTXTChunk(chunkType, slice);
				} catch (Throwable t) {
					return new RawPNGChunk(chunkType, slice);
				}
				
			case GammaChunk.TYPE_TAG:
				try {
					return new GammaChunk(chunkType, slice);
				} catch (Throwable t) {
					return new RawPNGChunk(chunkType, slice);
				}
				
			case IENDChunk.TYPE_TAG:
				return new IENDChunk();
				
			default:
				System.out.println("" + (char) ((chunkType >> 24) & 0xFF) + (char) ((chunkType >> 16) & 0xFF) + (char) ((chunkType >> 8) & 0xFF) + (char) (chunkType & 0xFF));
				return new RawPNGChunk(chunkType, slice);
			}
			
		}
	}
	
	public abstract int getChunkType();
	
	public void writeChunk(DataBuilder out) throws IOException {
		DataSlice data = getRawData();
		
		out.writeI32s(getChunkType());
		out.writeI32s((int) data.length());
		
		data.seek(0L);
		CRC32 crc = new CRC32();
		crc.update((getChunkType() >> 24) & 0xFF);
		crc.update((getChunkType() >> 16) & 0xFF);
		crc.update((getChunkType() >>  8) & 0xFF);
		crc.update((getChunkType() >>  0) & 0xFF);
		
		for(int i=0; i<data.length(); i++) {
			int cur = data.read();
			crc.update(cur);
			out.write(cur);
		}
		out.writeI32s((int) crc.getValue());
	}
	
	public abstract DataSlice getRawData() throws IOException;
	
	/**
	 * Reads in a String from the current location of the DataSlice, stopping at the first null (zero) byte encountered.
	 * Interprets the data as ISO 8859-1 character data as mandated by the W3C PNG standard. Consumes the null delimiter.
	 */
	protected String readNullDelimitedString(DataSlice in) throws IOException {
		DataBuilder builder = DataBuilder.create();
		int i = in.read();
		while (i != 0) {
			builder.write(i);
			i = in.read();
		}
		return new String(builder.toByteArray(), StandardCharsets.ISO_8859_1);
	}
	
	/**
	 * Reads all remaining data in the chunk, and returns it as a byte[].
	 */
	protected byte[] readToEndOfChunk(DataSlice in) throws IOException {
		return in.arraycopy(in.position(), (int) (in.length()-in.position()));
	}
}
