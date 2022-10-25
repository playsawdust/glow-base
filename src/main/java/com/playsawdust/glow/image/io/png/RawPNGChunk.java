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

import com.playsawdust.glow.io.DataSlice;

public class RawPNGChunk extends PNGChunk {
	private int chunkType;
	private DataSlice data;
	
	public RawPNGChunk(int chunkType, DataSlice data) {
		this.chunkType = chunkType;
		this.data = data;
	}
	
	@Override
	public int getChunkType() {
		return chunkType;
	}

	@Override
	public DataSlice getRawData() throws IOException {
		return data;
	}
	
	
}
