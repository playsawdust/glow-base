/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

/**
 * Represents a segment of randomly-seekable raw byte data, agnostic of its origin.
 * 
 * <p>InputStream and RandomAccessFile each have beneficial properties: InputStream anonymizes data sources so that the
 * same code can deal with heterogeneous sources but can't really seek beyond an artificial read buffer.
 * RandomAccessFile allows seeking but is necessarily tied to a "real" file and can't be used for embedded or network
 * resource decoding. ByteBuffer does a little of both, and allows access to offheap memory and memory-mapped files, but
 * has an awkward interface for decoding struct data of different types.
 * 
 * <p>DataSlice attempts to facilitate reading and writing complex heterogeneous data to and from heterogeneous
 * data-sources, while requiring as little as possible from an implementor. It's used in various places throughout glow
 * to encode and decode file formats.
 * 
 * <p><B>Threading:</B> DataSlices are not thread-safe. Slices and sub-slices generally should not be passed
 * off of the thread that created them; in order to do so, use the {@link #copy(long, int)} command to produce a new
 * Slice that the target thread can own.
 */
public interface DataSlice {
	/**
	 * Resets this DataSlice to the state it had at creation time; sets the read
	 * pointer to zero and the ByteOrder to BIG_ENDIAN.
	 */
	default void reset() throws IOException {
		seek(0L);
		setByteOrder(ByteOrder.BIG_ENDIAN);
	}
	
	/**
	 * Sets the read pointer to the specified offset from the beginning of this DataSlice
	 * @param offset a number of bytes into this DataSlice
	 */
	void seek(long offset) throws IOException;
	
	/**
	 * Skips over a specific number of bytes in the DataSlice, advancing the read pointer by that many bytes.
	 * @param bytes the number of bytes to skip forward by
	 */
	default void skip(long bytes) throws IOException {
		seek(position()+bytes);
	}
	
	/**
	 * Reads one byte at the read pointer and returns its unsigned value as an int
	 */
	int read() throws IOException;
	
	/**
	 * Reads one byte at the specific offset from the beginning of the DataSlice and returns its unsigned value as an
	 * int.
	 * @param offset An offset in bytes from the beginning of this DataSlice
	 * @return the value at the specified offset
	 */
	int read(long offset) throws IOException;
	
	/**
	 * Gets the ByteOrder used for multi-byte reads. Defaults to ByteOrder.BIG_ENDIAN.
	 */
	ByteOrder getByteOrder();
	
	/**
	 * Sets the ByteOrder for subsequent multi-byte reads. Defaults to ByteOrder.BIG_ENDIAN.
	 */
	void setByteOrder(ByteOrder order);
	
	/**
	 * Reads one byte, and returns true if it is nonzero, and false if it's zero.
	 * @throws IOException to indicate that there was a problem reading the byte.
	 */
	public default boolean readBoolean() throws IOException {
		return (read()!=0);
	}
	
	/**
	 * Reads one byte and returns its value as a signed byte
	 */
	public default byte readI8s() throws IOException {
		return (byte) read();
	}
	
	/**
	 * Reads one byte and returns its value as an unsigned int
	 */
	public default int readI8u() throws IOException {
		return read() & 0xFF;
	}
	
	/**
	 * Reads two bytes and returns their value as a signed short
	 * @see #setByteOrder(ByteOrder)
	 */
	public default short readI16s() throws IOException {
		return (short) order(read(), read(), getByteOrder());
	}
	
	/**
	 * Reads two bytes and returns their value as an unsigned int
	 * @see #setByteOrder(ByteOrder)
	 */
	public default int readI16u() throws IOException {
		return order(read(), read(), getByteOrder());
	}
	
	/**
	 * Reads four bytes and returns their value as a signed int
	 * @see #setByteOrder(ByteOrder)
	 */
	public default int readI32s() throws IOException {
		return order(read(), read(), read(), read(), getByteOrder());
	}
	
	/**
	 * Reads eight bytes and returns their value as a signed long
	 * @see #setByteOrder(ByteOrder)
	 */
	public default long readI64s() throws IOException {
		return order(read(), read(), read(), read(), read(), read(), read(), read(), getByteOrder());
	}
	
	/**
	 * Reads four bytes and returns their value as a signed float
	 * @see #setByteOrder(ByteOrder)
	 */
	public default float readF32s() throws IOException {
		return Float.intBitsToFloat(readI32s());
	}
	
	/**
	 * Reads eight bytes and returns their value as a signed double
	 * @see #setByteOrder(ByteOrder)
	 */
	public default double readF64s() throws IOException {
		return Double.longBitsToDouble(readI64s());
	}
	
	/**
	 * Copies data from the current read pointer of this DataSlice into a byte[]. Either all bytes will be copied
	 * successfully for the full length of the array, or an IOException will be thrown. The read pointer will be
	 * advanced by the number of bytes read.
	 * 
	 * @param  destination the array to fill with data from this DataSlice
	 * @throws IOException if there was not enough data to fill the entire array, or if there was a problem reading the
	 *                     data.
	 */
	public default void copy(byte[] destination) throws IOException {
		copy(destination, 0, destination.length);
	}
	
	/**
	 * Reads data from the current read pointer into the specified area in the destination array. Either all bytes
	 * requested will be read successfully or an IOException will be thrown. The read pointer will be advanced by the
	 * number of bytes read.
	 * @param destination  the byte array to read data into
	 * @param start        the first index in the byte array to write data into
	 * @param len          the number of bytes to copy out of this DataSlice
	 * @throws IOException if there was not enough data to fulfill the request, or if there was a problem reading the
	 *                     data.
	 */
	public default void copy(byte[] destination, int start, int len) throws IOException {
		for(int i=0; i<len; i++) {
			destination[start+i] = (byte) read();
		}
	}
	
	/**
	 * Gets the location of the read pointer relative to the start of this DataSlice.
	 */
	long position();
	
	/**
	 * Gets the length of this DataSlice
	 */
	long length() throws IOException;
	
	/**
	 * Slices this DataSlice into a sub-slice. No data is copied, and reads always "read-through". If there is a change
	 * to the backing file or array, and it falls within this slice and the returned slice, both slices will be able to
	 * see the change. This DataSlice's offset will be moved to the first byte after the newly-created slice.
	 * 
	 * <p>The ByteOrder of the return value will be equal to the ByteOrder of this DataSlice, but subsequent changes
	 * to the ByteOrder in either slice will not affect the other.
	 * @param offset The offset into this DataSlice where the returned slice should begin
	 * @param length The length of the returned slice
	 * @return A DataSlice representing a subsection of this DataSlice.
	 */
	DataSlice slice(long offset, long length);
	
	/**
	 * Slices this DataSlice into a sub-slice starting from the current read pointer. No data is copied, and reads
	 * always "read-through". If there is a change to the backing file or array, and it falls within this slice and the
	 * returned slice, both slices will be able to see the change. This DataSlice's offset will be moved to the first
	 * byte after the newly-created slice.
	 * 
	 * <p>The ByteOrder of the return value will be equal to the ByteOrder of this DataSlice, but subsequent changes
	 * to the ByteOrder in either slice will not affect the other.
	 * @param length The length of the returned slice
	 * @return A DataSlice representing a subsection of this DataSlice.
	 */
	default DataSlice slice(long length) {
		return this.slice(position(), length);
	}
	
	/**
	 * Copies a portion of this DataSlice out into a new DataSlice.
	 * @param offset the start location of the copy
	 * @param length the number of bytes to copy out
	 * @return A DataSlice representing the copy.
	 * @throws IOException if there was an error reading the data from the underlying medium.
	 */
	default DataSlice copy(long offset, int length) throws IOException {
		if (length<0) throw new ArrayIndexOutOfBoundsException();
		byte[] data = new byte[length];
		for(int i=0; i<length; i++) {
			data[i] = (byte) read(offset+i);
		}
		return new ArrayDataSlice(data);
	}
	
	/**
	 * Closes the underlying slice, file, or stream
	 */
	void close() throws IOException;
	
	
	/**
	 * Creates a DataSlice backed by the specified array.
	 */
	public static DataSlice of(byte[] array) {
		return new ArrayDataSlice(array, 0, array.length);
	}
	
	/**
	 * Creates a DataSlice backed by the specified RandomAccessFile. Operations on the RandomAccessFile will require
	 * the file to be open, but it is the responsibility of the caller to close it. We recommend using a
	 * try-with-resources block to manage the file closure.
	 */
	public static DataSlice of(RandomAccessFile f) throws IOException {
		return new FileDataSlice(f);
	}
	
	
	
	
	//Utility methods to account for byte order
	
	public static int order(int a, int b, ByteOrder order) {
		if (order==ByteOrder.BIG_ENDIAN) {
			return ((a & 0xFF) << 8) | (b & 0xFF);
		} else {
			return ((b & 0xFF) << 8) | (a & 0xFF);
		}
	}
	
	public static int order(int a, int b, int c, int d, ByteOrder order) {
		if (order==ByteOrder.BIG_ENDIAN) {
			return  ((a & 0xFF) << 24) |
					((b & 0xFF) << 16) |
					((c & 0xFF) <<  8) |
					 (d & 0xFF);
		} else {
			return  ((d & 0xFF) << 24) |
					((c & 0xFF) << 16) |
					((b & 0xFF) <<  8) |
					 (a & 0xFF);
		}
	}
	
	public static long order(long a, long b, long c, long d, long e, long f, long g, long h, ByteOrder order) {
		if (order==ByteOrder.BIG_ENDIAN) {
			return  ((a & 0xFF) << 56) |
					((b & 0xFF) << 48) |
					((c & 0xFF) << 40) |
					((d & 0xFF) << 32) |
					((e & 0xFF) << 24) |
					((f & 0xFF) << 16) |
					((g & 0xFF) <<  8) |
					 (h & 0xFF);
		} else {
			return
					((h & 0xFF) << 56) |
					((g & 0xFF) << 48) |
					((f & 0xFF) << 40) |
					((e & 0xFF) << 32) |
					((d & 0xFF) << 24) |
					((c & 0xFF) << 16) |
					((b & 0xFF) <<  8) |
					 (a & 0xFF);
		}
	}
}
