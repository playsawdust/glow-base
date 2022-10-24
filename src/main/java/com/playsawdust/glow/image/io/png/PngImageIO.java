/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.io.png;

import com.playsawdust.glow.image.SrgbImageData;
import com.playsawdust.glow.io.ArrayDataSlice;
import com.playsawdust.glow.io.DataSlice;

public class PngImageIO {
	public static SrgbImageData load(DataSlice in) {
		//TODO: Implement
		
		return new SrgbImageData(0, 0);
	}
	
	public DataSlice saveToDataSlice(SrgbImageData image) {
		//TODO: Implement
		
		return new ArrayDataSlice(new byte[]{});
	}
}
