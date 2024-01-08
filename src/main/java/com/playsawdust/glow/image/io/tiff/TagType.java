/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.io.tiff;

import java.io.IOException;

import com.playsawdust.glow.io.DataSlice;

public enum TagType {
	UNKNOWN  (-1, 1),
	I8U      ( 1, 1), //docs: BYTE
	ASCII    ( 2, 1),
	I16U     ( 3, 2), //docs: SHORT
	I32U     ( 4, 4), //docs: LONG
	RATIONAL ( 5, 8), //I32U numerator followed by I32U denominator
	I8S      ( 6, 1),
	UNDEFINED( 7, 1), //Used for unofficial extension data
	I16S     ( 8, 2),
	I32S     ( 9, 4),
	SRATIONAL(10, 8), //I32S numerator followed by I32S denominator
	FLOAT    (11, 4),
	DOUBLE   (12, 8),
	
	I64U     (16, 8), //docs: LONG8
	I64S     (17, 8), //docs: SLONG8
	IFD      (18, 8), //docs: IFD8, an offset to a new IFD
	;
	
	private int value;
	private int byteCount;
	
	TagType(int value, int byteCount) {
		this.value = value;
		this.byteCount = byteCount;
	}
	
	public int getValue() {
		return value;
	}
	
	public static TagType of(int value) {
		for(TagType cur : values()) if (cur.getValue() == value) return cur;
		return UNKNOWN;
	}
	
	public int getFoldable(boolean big) {
		if (big) {
			return 8 / byteCount;
		} else {
			return 4 / byteCount;
		}
	}
	
	public long readLong(DataSlice in) throws IOException {
		switch(this) {
		case I8U: return in.readI8u();
		case ASCII: return in.readI8u();
		case I16U: return in.readI16u();
		case I32U: return in.readI32u();
		case RATIONAL: {
			long num = in.readI32u();
			long denom = in.readI32u();
			return num / denom;
		}
		case I8S: return in.readI8s();
		case UNDEFINED: return in.readI8u();
		case I16S: return in.readI16s();
		case I32S: return in.readI32s();
		case SRATIONAL: {
			int num = in.readI32s();
			int denom = in.readI32s();
			return num / denom;
		}
		case FLOAT: return (long) in.readF32s();
		case DOUBLE: return (long) in.readF64s();
		case I64U: return in.readI64s(); //We fib a little bit here
		case I64S: return in.readI64s();
		case IFD: return in.readI64s(); //Also here.
		
		default:
			throw new IllegalStateException("Cannot read data of type "+this);
		}
	}
	
	public int readInt(DataSlice in) throws IOException {
		return (int) readLong(in);
	}
	
	public double readDouble(DataSlice in) throws IOException {
		switch(this) {
		case I8U: return in.readI8u();
		case ASCII: return in.readI8u();
		case I16U: return in.readI16u();
		case I32U: return in.readI32u();
		case RATIONAL: {
			long num = in.readI32u();
			long denom = in.readI32u();
			return num / (double) denom;
		}
		case I8S: return in.readI8s();
		case UNDEFINED: return in.readI8u();
		case I16S: return in.readI16s();
		case I32S: return in.readI32s();
		case SRATIONAL: {
			int num = in.readI32s();
			int denom = in.readI32s();
			return num / (double) denom;
		}
		case FLOAT: return in.readF32s();
		case DOUBLE: return in.readF64s();
		case I64U: return in.readI64s(); //We fib a little bit here
		case I64S: return in.readI64s();
		case IFD: return in.readI64s(); //Also here.
		
		default:
			throw new IllegalStateException("Cannot read data of type "+this);
		}
	}
}
