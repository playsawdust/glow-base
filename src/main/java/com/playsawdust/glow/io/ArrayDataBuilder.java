/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.io;

import java.nio.ByteOrder;
import java.util.Arrays;

public class ArrayDataBuilder implements DataBuilder {
	private static final int DEFAULT_CAPACITY = 128;
	private static final double LOAD_FACTOR = 2.0;
	
	private byte[] data;
	private int dataLength;
	private int writePointer = 0;
	private ByteOrder order = ByteOrder.BIG_ENDIAN;
	
	public ArrayDataBuilder() {
		data = new byte[DEFAULT_CAPACITY];
		dataLength = 0;
	}
	
	@Override
	public void reset() {
		writePointer = 0;
		order = ByteOrder.BIG_ENDIAN;
	}
	
	@Override
	public void seek(long offset) {
		if (offset > 0x7FFFFFFF) throw new IndexOutOfBoundsException("Offset value '"+offset+"' out of range for arrays.");
		ensureCapacity((int) offset - 1);
		ensureDataLength((int) offset - 1);
	}
	
	@Override
	public void skip(long bytes) {
		seek(writePointer + bytes);
	}
	
	@Override
	public void write(int value) {
		ensureCapacity(writePointer + 1);
		data[writePointer] = (byte) value;
		writePointer++;
		ensureDataLength(writePointer);
	}
	
	@Override
	public void write(long offset, int value) {
		if (offset > 0x7FFFFFFF) throw new IndexOutOfBoundsException("Offset value '"+offset+"' out of range for arrays.");
		
		ensureCapacity((int) offset + 1);
		data[(int) offset] = (byte) value;
		ensureDataLength((int) offset + 1);
	}
	
	@Override
	public ByteOrder getByteOrder() {
		return order;
	}
	
	@Override
	public void setByteOrder(ByteOrder order) {
		this.order = order;
	}
	
	@Override
	public long length() {
		return dataLength;
	}
	
	/**
	 * Defensively copies all data written to the buffer so far into a new byte array and returns it.
	 */
	@Override
	public byte[] toByteArray() {
		byte[] result = new byte[dataLength];
		if (dataLength > 0) System.arraycopy(data, 0, result, 0, dataLength);
		return result;
	}
	
	/**
	 * Converts this ArayDataBuilder into a DataSlice. This Builder should not be used after this method is called,
	 * as the backing array MAY be reused without a defensive copy, but the write-through behavior is undefined.
	 */
	@Override
	public DataSlice toDataSlice() {
		return new ArrayDataSlice(data, 0, dataLength);
	}
	
	private void ensureCapacity(int capacity) {
		if (capacity < 0) throw new OutOfMemoryError("Overflow in memory reservation request.");
		if (data.length >= capacity) return;
		
		int newCapacity = (int) (data.length * LOAD_FACTOR);
		if (newCapacity < 0 || newCapacity < capacity) {
			newCapacity = capacity;
		}
		
		data = Arrays.copyOf(data, newCapacity);
	}
	
	private void ensureDataLength(int length) {
		if (dataLength < length) dataLength = length;
	}
}
