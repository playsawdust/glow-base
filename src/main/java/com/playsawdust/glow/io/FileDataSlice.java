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
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

public class FileDataSlice implements DataSlice {
	protected final RandomAccessFile file;
	protected long pointer = 0L;
	protected ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
	
	public FileDataSlice(RandomAccessFile file) {
		this.file = file;
	}

	@Override
	public void seek(long offset) throws IOException {
		pointer = offset;
	}

	@Override
	public int read() throws IOException {
		if (pointer!=file.getFilePointer()) file.seek(pointer);
		int result = file.read();
		pointer++;
		return result;
	}
	
	@Override
	public int read(long offset) throws IOException {
		if (offset!=file.getFilePointer()) file.seek(pointer);
		return file.read();
	}

	@Override
	public long position() {
		return pointer;
	}

	@Override
	public long length() throws IOException {
		return file.length();
	}

	@Override
	public DataSlice slice(long offset, long length) {
		SubSlice result = new SubSlice(this, offset, length);
		this.pointer += length;
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
		file.close();
	}
}
