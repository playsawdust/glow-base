/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2022 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.io;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * Represents a {@link DataSlice} backed by an array.
 */
public class ArrayDataSlice implements DataSlice {
	protected byte[] data;
	protected int baseOffset;
	protected int length;
	protected int pointer = 0;
	protected ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
	
	public ArrayDataSlice(byte[] data, int offset, int length) {
		this.data = data;
		this.baseOffset = offset;
		this.length = length;
	}
	
	public ArrayDataSlice(byte[] data) {
		this(data, 0, data.length);
	}
	
	@Override
	public void seek(long offset) {
		if (offset<0 || offset>length) throw new ArrayIndexOutOfBoundsException();
		pointer = (int) offset;
	}
	
	@Override
	public int read() {
		if (pointer>=length) throw new ArrayIndexOutOfBoundsException();
		int value = data[baseOffset+pointer];
		pointer++;
		return value;
	}
	
	@Override
	public int read(long offset) {
		if (offset<0) throw new ArrayIndexOutOfBoundsException();
		if (baseOffset+offset>=length) throw new ArrayIndexOutOfBoundsException();
		return data[baseOffset + (int) offset];
	}

	@Override
	public long position() {
		return pointer;
	}
	
	@Override
	public long length() {
		return length;
	}

	@Override
	public DataSlice slice(long offset, long length) {
		if (offset<0 || offset > this.baseOffset+this.length) throw new ArrayIndexOutOfBoundsException();
		if (length<0 || offset+length > this.length) throw new ArrayIndexOutOfBoundsException();
		
		ArrayDataSlice result = new ArrayDataSlice(data, (int) (this.baseOffset+offset), (int) length);
		result.setByteOrder(byteOrder);
		this.pointer = (int) (offset + length);
		return result;
	}
	
	@Override
	public byte[] arraycopy(long offset, int length) throws IOException {
		byte[] result = new byte[length];
		System.arraycopy(data, (int) offset, result, 0, length);
		return result;
	}

	@Override
	public ByteOrder getByteOrder() {
		return byteOrder;
	}
	
	@Override
	public void setByteOrder(ByteOrder order) {
		this.byteOrder = order;
	}
	
	@Override
	public void close() throws IOException {
		// Do Nothing
	}
	
}
