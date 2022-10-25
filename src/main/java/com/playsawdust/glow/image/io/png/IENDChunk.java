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

public class IENDChunk extends PNGChunk {
	public static final int TYPE_TAG = (byte)'I' << 24 | (byte)'E' << 16 | (byte)'N' << 8 | (byte)'D';
	
	public IENDChunk() {
		
	}
	
	@Override
	public int getChunkType() {
		return TYPE_TAG;
	}

	@Override
	public DataSlice getRawData() throws IOException {
		return DataSlice.EMPTY;
	}

}
