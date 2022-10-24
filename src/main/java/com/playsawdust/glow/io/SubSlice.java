package com.playsawdust.glow.io;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * Represents a DataSlice that is a sub-slice of another DataSlice
 */
public class SubSlice implements DataSlice {
	protected final DataSlice underlying;
	protected final long baseOffset;
	protected final long length;
	protected long pointer = 0L;
	protected ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
	
	public SubSlice(DataSlice underlying, long offset, long length) {
		this.underlying = underlying;
		this.baseOffset = offset;
		this.length = length;
		this.byteOrder = underlying.getByteOrder();
	}

	@Override
	public void seek(long offset) {
		if (offset<0 || offset>length) throw new ArrayIndexOutOfBoundsException();
		pointer = offset;
	}

	@Override
	public int read() throws IOException {
		int result = underlying.read(baseOffset + pointer);
		seek(pointer+1);
		return result;
	}
	
	@Override
	public int read(long offset) throws IOException {
		return underlying.read(baseOffset + offset);
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
		SubSlice result = new SubSlice(this, offset, length);
		pointer = offset+length;
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
		underlying.close();
	}
	
}
