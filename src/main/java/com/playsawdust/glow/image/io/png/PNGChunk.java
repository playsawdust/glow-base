/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.io.png;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;

public abstract class PNGChunk {
	
	public static PNGChunk readChunk(DataSlice in) throws IOException {
		int length = in.readI32s();
		int chunkType = in.readI32s();
		byte[] chunkData = new byte[length];
		in.copy(chunkData);
		System.out.println("ChunkData: "+Arrays.toString(chunkData));
		
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
				return new IHDRChunk(chunkType, slice);
			
			default:
				return new RawPNGChunk(chunkType, slice);
			}
			
		}
		//case IDATChunk.TYPE_TAG:
		//	byte[] idatData = new byte[length];
		//	in.readFully(idatData);
		//	in.readInt(); //throw away the CRC
		//	IDATChunk idat = new IDATChunk();
		//	idat.data = idatData;
		//	return idat;
		//case IENDChunk.TYPE_TAG:
		//	if (length>0) in.skip(length); //Specified to never happen but let's check anyway
		//	in.readInt(); //throw away the CRC
		//	return new IENDChunk();*/
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
}
