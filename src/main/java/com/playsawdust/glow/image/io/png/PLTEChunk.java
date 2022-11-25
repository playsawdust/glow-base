package com.playsawdust.glow.image.io.png;

import java.io.IOException;

import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;

public class PLTEChunk extends PNGChunk {
	public static final int TYPE_TAG = (byte)'P' << 24 | (byte)'L' << 16 | (byte)'T' << 8 | (byte)'E';
	
	public RGBColor[] palette = new RGBColor[0];
	
	public PLTEChunk(int chunkType, DataSlice data) throws IOException {
		if (chunkType != TYPE_TAG) throw new IllegalArgumentException("Invalid chunk type tag.");
		
		int length = (int) ((data.length()-data.position()) / 3);
		palette = new RGBColor[length];
		for(int i=0; i<length; i++) {
			int r = data.read() & 0xFF;
			int g = data.read() & 0xFF;
			int b = data.read() & 0xFF;
			
			palette[i] = new RGBColor(1, r / 255f, g / 255f, b / 255f);
		}
	}
	
	@Override
	public int getChunkType() {
		return TYPE_TAG;
	}

	@Override
	public DataSlice getRawData() throws IOException {
		DataBuilder out = DataBuilder.create();
		for(RGBColor cur : palette) {
			out.write((int) (cur.r() * 255) & 0xFF);
			out.write((int) (cur.g() * 255) & 0xFF);
			out.write((int) (cur.b() * 255) & 0xFF);
		}
		return out.toDataSlice();
	}

}
