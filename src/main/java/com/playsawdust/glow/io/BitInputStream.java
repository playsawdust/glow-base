package com.playsawdust.glow.io;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream {
	protected final InputStream in;
	protected int value = 0;
	protected int bitIndex = 0;
	
	public BitInputStream(InputStream in) {
		this.in = in;
	}
	
	/** Reads in a single bit from the stream, starting from the highest bit of the first byte */
	public boolean readBit() throws IOException {
		if (bitIndex>=8) {
			value = in.read();
			bitIndex = 0;
		}
			
		boolean result = (value & 0x80) != 0;
		value <<= 1;
		bitIndex++;
		
		return result;
	}
	
	/** Reads a number of bits in, in big-endian order */
	public int readBits(int count) throws IOException {
		int result = 0;
		for(int i=0; i<count; i++) {
			result <<= 1;
			if (readBit()) result |= 1;
		}
		
		return result;
	}
	
	/** Aligns the stream back onto a byte boundary, discarding any remaining bits in the current byte */
	public void align() {
		bitIndex = 8;
	}
	
	public void close() throws IOException {
		in.close();
	}
}
