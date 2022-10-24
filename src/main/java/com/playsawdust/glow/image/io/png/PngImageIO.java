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
