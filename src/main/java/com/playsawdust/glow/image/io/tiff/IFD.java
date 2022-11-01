package com.playsawdust.glow.image.io.tiff;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.playsawdust.glow.io.DataBuilder;
import com.playsawdust.glow.io.DataSlice;

public class IFD {
	
	public static final int TAG_IMAGE_WIDTH       = 0x100;
	public static final int TAG_IMAGE_HEIGHT      = 0x101;
	public static final int TAG_BITS_PER_SAMPLE   = 0x102;
	public static final int TAG_COMPRESSION       = 0x103;
	
	public static final int TAG_PHOTOMETRIC_INTERPRETATION = 0x106;
	
	public static final int TAG_STRIP_OFFSETS     = 0x111;
	
	public static final int TAG_SAMPLES_PER_PIXEL = 0x115;
	public static final int TAG_ROWS_PER_STRIP    = 0x116;
	public static final int TAG_STRIP_BYTE_COUNTS = 0x117;
	
	public static final int TAG_XRESOLUTION       = 0x11A;
	public static final int TAG_YRESOLUTION       = 0x11B;
	public static final int TAG_RESOLUTION_UNIT   = 0x128;
	
	public static final int TAG_COLOR_MAP         = 0x140;
	
	public static final int TAG_EXTRA_SAMPLES     = 0x152;
	public static final int TAG_SAMPLE_FORMAT     = 0x153;
	
	/*
	public static final int TYPE_I8U    = 1; //Each 'count' is an octet
	public static final int TYPE_STRING = 2; //Each 'count' is a 7-bit ascii byte. When null-terminated, 'count' includes the null.
	public static final int TYPE_I16U   = 3; //Each 'count' is a 2-byte unsigned integer
	public static final int TYPE_I32U   = 4; //Each 'count' is a 4-byte unsigned integer
	public static final int TYPE_RATIONAL = 5; //Each 'count' is a 4-byte unsigned integer numerator plus a 4-byte unsigned denominator
	public static final int TYPE_I64U   = 16;
	*/
	private boolean big = false;
	public ArrayList<Entry> entries = new ArrayList<>();
	public long nextIFD = 0L;
	
	public long getLong(DataSlice in, int tagType, long fallback) throws IOException {
		Entry entry = find(tagType);
		if (entry == null) return fallback;
		return entry.getAsLong(in, big);
	}
	
	public long[] getLongs(DataSlice in, int tagType) throws IOException {
		Entry entry = find(tagType);
		if (entry == null) return new long[0];
		return entry.getAsLongs(in, big);
	}
	
	public int[] getInts(DataSlice in, int tagType) throws IOException {
		Entry entry = find(tagType);
		if (entry == null) return new int[0];
		return entry.getAsInts(in, big);
	}
	
	public double getDouble(DataSlice in, int tagType, double fallback) throws IOException {
		Entry entry = find(tagType);
		if (entry == null) return fallback;
		return entry.getAsDouble(in, big);
	}
	
	public @Nullable Entry find(int tagType) {
		for(Entry entry : entries) {
			if (entry.tag == tagType) return entry;
		}
		
		return null;
	}
	
	public String explain(int tagType) {
		Entry entry = find(tagType);
		if (entry == null) return "null";
		return entry.toString();
	}
	
	public void write(DataBuilder out) throws IOException {
		out.writeI16u(entries.size());
		for(Entry entry : entries) {
			entry.write(out);
		}
		out.writeI32s((int) nextIFD);
	}
	
	public static IFD read(DataSlice in) throws IOException {
		IFD result = new IFD();
		int count = in.readI16u();
		for(int i=0; i<count; i++) {
			result.entries.add(Entry.read(in));
		}
		result.nextIFD = in.readI32u();
		result.big = false;
		
		return result;
	}
	
	public static IFD readBig(DataSlice in) throws IOException {
		IFD result = new IFD();
		long count = in.readI64s();
		for(int i=0; i<count; i++) {
			result.entries.add(Entry.readBig(in));
		}
		result.nextIFD = in.readI64s();
		result.big = true;
		
		return result;
	}
	
	public static class Entry {
		public int tag;
		public TagType fieldType;
		public long count;
		public long offset;
		
		public void write(DataBuilder out) throws IOException {
			out.writeI16u(tag);
			out.writeI16u(fieldType.getValue());
			out.writeI32s((int) count);
			out.writeI32s((int) offset);
		}
		
		public static Entry read(DataSlice in) throws IOException {
			Entry result = new Entry();
			
			result.tag = in.readI16u();
			result.fieldType = TagType.of(in.readI16u());
			result.count = in.readI32u();
			result.offset = in.readI32u();
			
			//System.out.println("Tag: "+Integer.toHexString(result.tag)+", Type: "+Integer.toHexString(result.fieldType)+", Count: "+result.count+", Offset: "+Long.toHexString(result.offset));
			
			return result;
		}
		
		public static Entry readBig(DataSlice in) throws IOException {
			Entry result = new Entry();
			result.tag = in.readI16u();
			result.fieldType = TagType.of(in.readI16u());
			result.count = in.readI64s();
			result.offset = in.readI64s();
			
			//System.out.println("Tag: "+Integer.toHexString(result.tag)+", Type: "+Integer.toHexString(result.fieldType)+", Count: "+result.count+", Offset: "+Long.toHexString(result.offset));
			
			return result;
		}
		
		public long getAsLong(DataSlice source, boolean big) throws IOException {
			if (count>1) throw new IllegalStateException("Expected single value but found an array");
			
			long[] result = getAsLongs(source, big);
			return result[0];
		}
		
		public long[] getAsLongs(DataSlice source, boolean big) throws IOException {
			long[] result = new long[(int) count];
			
			int foldableCount = fieldType.getFoldable(big);
			if (count > foldableCount) {
				source.seek(offset);
				for(int i=0; i<count; i++) {
					result[i] = fieldType.readLong(source);
				}
			} else {
				DataSlice tmp = unslice(offset, source.getByteOrder(), big);
				for(int i=0; i<count; i++) {
					result[i] = fieldType.readLong(tmp);
				}
			}
			
			return result;
		}
		
		public int[] getAsInts(DataSlice source, boolean big) throws IOException {
			int[] result = new int[(int) count];
			
			int foldableCount = fieldType.getFoldable(big);
			if (count > foldableCount) {
				source.seek(offset);
				for(int i=0; i<count; i++) {
					result[i] = fieldType.readInt(source);
				}
			} else {
				DataSlice tmp = unslice(offset, source.getByteOrder(), big);
				for(int i=0; i<count; i++) {
					result[i] = fieldType.readInt(tmp);
				}
			}
			
			return result;
		}
		
		public int getAsInt(DataSlice source, boolean big) throws IOException {
			if (count>1) throw new IllegalStateException("Expected single value but found an array");
			
			return getAsInts(source, big)[0];
		}
		
			/*
			switch(fieldType) {
			case TYPE_I16U: {
				int foldableCount = (big) ? 4 : 2;
				if (count > foldableCount) {
					source.seek(offset);
					for(int i=0; i<count; i++) {
						result[i] = source.readI16u();
					}
				} else {
					DataSlice tmp = unslice(offset, source.getByteOrder(), big);
					for(int i=0; i<count; i++) {
						result[i] = tmp.readI16u();
					}
				}
				break;
			}
			case TYPE_I32U: {
				int foldableCount = (big) ? 1 : 2;
				if (count > foldableCount) {
					source.seek(offset);
					for(int i=0; i<count; i++) {
						result[i] = source.readI32u();
					}
				} else {
					DataSlice tmp = unslice(offset, source.getByteOrder(), big);
					for(int i=0; i<count; i++) {
						result[i] = tmp.readI32u();
					}
				}
				break;
			}
			
			case TYPE_I64U:
				int foldableCount = (big) ? 1 : 0;
				if (count > foldableCount) {
					source.seek(offset);
					for(int i=0; i<count; i++) {
						result[i] = source.readI64s();
					}
				} else {
					DataSlice tmp = unslice(offset, source.getByteOrder(), big);
					for(int i=0; i<count; i++) {
						result[i] = tmp.readI64s();
					}
				}
				break;
			default:
				throw new IllegalStateException("Can't convert data of type "+fieldType);
			}
			return result;
		}*/
		
		/*
		public int[] getAsInts(DataSlice source, boolean big) throws IOException {
			int[] result = new int[(int) count];
			
			switch(fieldType) {
			case TYPE_I16U: {
				int foldableCount = (big) ? 4 : 2;
				if (count > foldableCount) {
					source.seek(offset);
					for(int i=0; i<count; i++) {
						result[i] = source.readI16u();
					}
				} else {
					DataSlice tmp = unslice(offset, source.getByteOrder(), big);
					for(int i=0; i<count; i++) {
						result[i] = tmp.readI16u();
					}
				}
				break;
			}
			case TYPE_I32U: {
				int foldableCount = (big) ? 1 : 2;
				if (count > foldableCount) {
					source.seek(offset);
					for(int i=0; i<count; i++) {
						result[i] = (int) source.readI32u();
					}
				} else {
					DataSlice tmp = unslice(offset, source.getByteOrder(), big);
					for(int i=0; i<count; i++) {
						result[i] = (int) tmp.readI32u();
					}
				}
				break;
			}
			
			//case TYPE_I8U:
			//	if (count<=)
			}
			return result;
		}*/
		
		public double[] getAsDoubles(DataSlice source, boolean big) throws IOException {
			double[] result = new double[(int) count];
			
			int foldableCount = fieldType.getFoldable(big);
			if (count > foldableCount) {
				source.seek(offset);
				for(int i=0; i<count; i++) {
					result[i] = fieldType.readDouble(source);
				}
			} else {
				DataSlice tmp = unslice(offset, source.getByteOrder(), big);
				for(int i=0; i<count; i++) {
					result[i] = fieldType.readDouble(tmp);
				}
			}
			
			return result;
		}
		
		public double getAsDouble(DataSlice source, boolean big) throws IOException {
			if (count>1) throw new IllegalStateException("Expected single value but found an array");
			
			return getAsDoubles(source, big)[0];
			/*
			switch(fieldType) {
			case TYPE_I8U:
			case TYPE_I16U:
			case TYPE_I32U:
				return offset;
			case TYPE_RATIONAL:
				if (big) {
					//Rational will fit in offset
					long numerator = (offset >> 32) & 0xFFFFFFFF;
					long denominator = offset & 0xFFFFFFFF;
					double value = numerator / (double) denominator;
					System.out.println("Numerator: "+numerator+" / Denominator: "+denominator+" = "+value);
					return value;
				} else {
					//Need to seek to offset
					source.seek(offset);
					int numerator = source.readI32s();
					int denominator = source.readI32s();
					
					return numerator / (double) denominator;
				}
				
			default:
				throw new IllegalStateException("Can't convert this data.");
			}*/
		}
		
		private static DataSlice unslice(long value, ByteOrder order, boolean big) {
			try {
				DataBuilder builder = DataBuilder.create();
				//System.out.println(order);
				//Flip byte order
				builder.setByteOrder(order);
				builder.writeI64s(value);
				
				DataSlice result = builder.toDataSlice();
				result.setByteOrder(order);
				return result;
				
			} catch (IOException ex) {
				return DataSlice.EMPTY;
			}
		}
		
		@Override
		public String toString() {
			return "{ Tag: "+tag+", FieldType: "+fieldType+", Count: "+count+", Offset: "+offset+" }";
		}
	}
}
