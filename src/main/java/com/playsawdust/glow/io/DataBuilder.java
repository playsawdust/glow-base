/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.io;

import java.io.IOException;
import java.nio.ByteOrder;

public interface DataBuilder {
	
	/**
	 * Sets the write pointer to zero and resets the endianness of this builder to BIG_ENDIAN.
	 */
	void reset();
	
	/**
	 * Sets the write pointer to the given byte offset, growing the data to include it if necessary. Any additional
	 * padding bytes created to grow the data by will be initialized to zeroes.
	 */
	void seek(long offset) throws IOException;
	
	/**
	 * Advances the write pointer by the specified number of bytes, growing the data to include it if necessary. The
	 * skipped bytes will be initialized to zeroes.
	 */
	void skip(long bytes) throws IOException;
	
	/**
	 * Writes a single byte to the data buffer and advances the write pointer by one. The buffer will grow if necessary
	 * to accommodate the new data.
	 */
	void write(int value) throws IOException;
	
	/**
	 * Writes a single byte into the data buffer at the specified offset. Does not affect the write pointer.
	 */
	void write(long offset, int value) throws IOException;
	
	/**
	 * Gets the length of data written so far into the data buffer.
	 */
	long length();
	
	/**
	 * Gets the ByteOrder used for multi-byte writes. Defaults to {@link ByteOrder#BIG_ENDIAN}.
	 */
	ByteOrder getByteOrder();
	
	/**
	 * Sets the ByteOrder for subsequent multi-byte writes. Defaults to {@link ByteOrder#BIG_ENDIAN}.
	 */
	void setByteOrder(ByteOrder order);
	
	/**
	 * Writes one byte, which will be 1 if value is true, and 0 if value is false.
	 */
	default void writeBoolean(boolean value) throws IOException {
		write( value ? 1 : 0 );
	}
	
	/**
	 * Writes a single signed byte into the data buffer and advances the write pointer by one byte.
	 */
	default void writeI8s(byte value) throws IOException {
		write(value & 0xFF);
	}
	
	/**
	 * Writes a single unsigned byte into the data buffer and advances the write pointer by one byte.
	 */
	default void writeI8u(int value) throws IOException {
		write(value & 0xFF);
	}
	
	/**
	 * Writes a signed short (16-bit) value into the data buffer and advances the write pointer by two bytes.
	 */
	default void writeI16s(short value) throws IOException {
		write16((int) value & 0xFFFF);
	}
	
	/**
	 * Writes an unsigned short (16-bit) value into the data buffer and advances the write pointer by two bytes.
	 */
	default void writeI16u(int value) throws IOException {
		write16((int) value & 0xFFFF);
	}
	
	/**
	 * Writes a signed integer (32-bit) value into the data buffer and advances the write pointer by four bytes.
	 */
	default void writeI32s(int value) throws IOException {
		write32(value);
	}
	
	/**
	 * Writes a signed long (64-bit) value into the data buffer and advances the write pointer by eight bytes.
	 */
	default void writeI64s(long value) throws IOException {
		write64(value);
	}
	
	/**
	 * Writes a float (32-bit IEEE float) value into the data buffer and advances the write pointer by four bytes.
	 */
	default void writeF32s(float value) throws IOException {
		write32(Float.floatToIntBits(value));
	}
	
	/**
	 * Writes a double (64-bit IEEE float) value into the data buffer and advances the write pointer by eight bytes.
	 */
	default void writeF64s(double value) throws IOException {
		write64(Double.doubleToLongBits(value));
	}
	
	default void writeDataSlice(DataSlice value) throws IOException {
		value.seek(0L);
		for(long i=0; i<value.length(); i++) {
			write(value.read());
		}
	}
	
	/**
	 * Defensively copies all data written to the buffer so far into a new byte array and returns it.
	 * 
	 * @throws ArrayOutOfBoundsException if the data is too large to fit in an array.
	 */
	byte[] toByteArray();
	
	/**
	 * Converts this DataBuilder into a DataSlice. This Builder should not be used after this method is called,
	 * as the backing store MAY be reused without a defensive copy, but the write-through behavior is undefined.
	 */
	DataSlice toDataSlice();
	
	public static ArrayDataBuilder create() {
		return new ArrayDataBuilder();
	}
	
	private void write16(int value) throws IOException {
		if (getByteOrder()==ByteOrder.BIG_ENDIAN) {
			write((value >> 8) & 0xFF);
			write(value & 0xFF);
		} else {
			write(value & 0xFF);
			write((value >> 8) & 0xFF);
		}
	}
	
	private void write32(int value) throws IOException {
		if (getByteOrder()==ByteOrder.BIG_ENDIAN) {
			write((value >> 24) & 0xFF);
			write((value >> 16) & 0xFF);
			write((value >>  8) & 0xFF);
			write(value & 0xFF);
		} else {
			write(value & 0xFF);
			write((value >>  8) & 0xFF);
			write((value >> 16) & 0xFF);
			write((value >> 24) & 0xFF);
		}
	}
	
	private void write64(long value) throws IOException {
		if (getByteOrder()==ByteOrder.BIG_ENDIAN) {
			write((int) (value >> 56) & 0xFF);
			write((int) (value >> 48) & 0xFF);
			write((int) (value >> 40) & 0xFF);
			write((int) (value >> 32) & 0xFF);
			write((int) (value >> 24) & 0xFF);
			write((int) (value >> 16) & 0xFF);
			write((int) (value >>  8) & 0xFF);
			write((int) value & 0xFF);
		} else {
			write((int) value & 0xFF);
			write((int) (value >>  8) & 0xFF);
			write((int) (value >> 16) & 0xFF);
			write((int) (value >> 24) & 0xFF);
			write((int) (value >> 32) & 0xFF);
			write((int) (value >> 40) & 0xFF);
			write((int) (value >> 48) & 0xFF);
			write((int) (value >> 56) & 0xFF);
		}
	}
}
