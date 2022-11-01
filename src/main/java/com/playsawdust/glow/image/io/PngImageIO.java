/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2022 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterOutputStream;

import com.playsawdust.glow.image.SrgbImageData;
import com.playsawdust.glow.image.io.png.IDATChunk;
import com.playsawdust.glow.image.io.png.IHDRChunk;
import com.playsawdust.glow.image.io.png.PNGChunk;
import com.playsawdust.glow.image.io.png.PNGImageDataDecoder;
import com.playsawdust.glow.io.ArrayDataBuilder;
import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;

public class PngImageIO {
	/** The first two bytes of a file or stream can be used to uniquely identify it as a PNG file. Specifically, these two bytes. */
	public static final int PNG_SHORTMAGIC = 0x8950;
	
	/** {@code 0x89 'P' 'N' 'G' '\r' '\n' CTRL-Z '\n'}, carefully chosen to make your text editor think twice before opening, and do 7-bit integrity checks, right from the first dword */
	public static final long PNG_MAGIC = 0x89504e470d0a1a0aL;
	
	public static final int COLORTYPE_GRAY = 0;
	public static final int COLORTYPE_RGB  = 2;
	public static final int COLORTYPE_INDEXED = 3;
	public static final int COLORTYPE_GRAY_WITH_ALPHA = 4;
	public static final int COLORTYPE_RGBA = 6;
	
	public static SrgbImageData load(DataSlice in) throws IOException {
		List<PNGChunk> chunks = loadChunks(in);
		
		// Decode
		IHDRChunk header = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InflaterOutputStream inflater = new InflaterOutputStream(baos);
		
		for(PNGChunk chunk : chunks) {
			if (chunk instanceof IDATChunk data) {
				inflater.write(data.getRawData().toArray());
			} else if (chunk instanceof IHDRChunk foundHeader) {
				header = foundHeader;
			}
		}
		
		if (header == null) throw new IOException("Can't find the image header!");
		if (header.colorType != COLORTYPE_RGB && header.colorType != COLORTYPE_RGBA) throw new IOException("Can't decode color type "+header.colorType+".");
		
		inflater.flush();
		byte[] decompressedImageData = baos.toByteArray();
		
		SrgbImageData result = new SrgbImageData(header.width, header.height);
		PNGImageDataDecoder.decodeSrgb(DataSlice.of(decompressedImageData), result, header.colorType==COLORTYPE_RGBA);
		
		return result;
	}
	
	public static List<PNGChunk> loadChunks(DataSlice in) throws IOException {
		long fileMagic = in.readI64s();
		if (fileMagic != PNG_MAGIC) throw new IOException("Not a valid PNG file.");
		
		ArrayList<PNGChunk> result = new ArrayList<>();
		
		int errorCount = 0;
		while(in.position() < in.length()) {
			try {
				PNGChunk chunk = PNGChunk.readChunk(in);
				result.add(chunk);
				
			} catch (IOException ex) {
				//ex.printStackTrace();
				//Forgive this error 
				errorCount++;
				if (errorCount > 1000) throw ex;
			}
		}
		
		return result;
	}
	
	public DataSlice saveToDataSlice(SrgbImageData image) {
		ArrayDataBuilder out = DataBuilder.create();
		try {
			out.writeI64s(PNG_MAGIC);
			
			IHDRChunk header = new IHDRChunk(image.getWidth(), image.getHeight());
			header.writeChunk(out);
			
			//TODO: Write IDAT chunk with image data
			
			return out.toDataSlice();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}
