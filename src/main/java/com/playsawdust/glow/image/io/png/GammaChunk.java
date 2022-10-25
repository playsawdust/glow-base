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

import com.playsawdust.glow.image.color.Colors;
import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;

public class GammaChunk extends PNGChunk {
	public static final int TYPE_TAG = (byte)'g' << 24 | (byte)'A' << 16 | (byte)'M' << 8 | (byte)'A';
	
	public static final int IDEAL_GAMMA = 45455; //Equal to 1/2.2 * 100_000
	
	public float gamma = Colors.IDEAL_GAMMA;
	
	public GammaChunk() {}
	
	public GammaChunk(int chunkType, DataSlice data) throws IOException {
		if (chunkType != TYPE_TAG) throw new IllegalArgumentException("Invalid chunk type tag.");
		
		int gammaValue = data.readI32s();
		/*
		 * In glow, we represent gamma as values greater than one, like "2.2". In PNG, they start from the inverse
		 * gamma, "1/2.2", then multiply it by 100,000 to get an integer value, which is stored.
		 * 
		 * As an example, 45455 / 100000 = 0.45455 repeating , and 1/0.045455 = 2.24466891133 (2.2). Because this is
		 * both the most common number we'll encounter, and kind of messy, we hardcode the W3C's example 2.2 value.
		 */
		
		if (gammaValue==IDEAL_GAMMA) {
			gamma = Colors.IDEAL_GAMMA;
		} else {
			float invGamma = gammaValue / 100_000f;
			gamma = 1f/invGamma;
		}
	}
	
	@Override
	public int getChunkType() {
		return TYPE_TAG;
	}

	@Override
	public DataSlice getRawData() throws IOException {
		DataBuilder out = DataBuilder.create();
		
		if (gamma == Colors.IDEAL_GAMMA) {
			out.writeI32s(IDEAL_GAMMA);
		} else {
			float invGamma = 1f/gamma;
			int fileGamma = (int) invGamma * 100_000;
			out.writeI32s(fileGamma);
		}
		
		return out.toDataSlice();
	}

}
