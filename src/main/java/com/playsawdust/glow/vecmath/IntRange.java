package com.playsawdust.glow.vecmath;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * A range of integer values.
 */
public record IntRange(int start, int length) {
	/**
	 * Creates an IntRange from a starting value and a length. The range will cover the start value and all following
	 * values up to but excluding start+length.
	 * @param start The initial, lowest value which the range shall cover.
	 * @param length The length of the range.
	 */
	public IntRange(int start, int length) {
		if (length<0) throw new IllegalArgumentException("Range length cannot be negative");
		this.start = start;
		this.length = length;
	}
	
	/**
	 * Creates an IntRange from a start and ending value. Both the start and end value will be included in the
	 * resulting range.
	 * @param start The first value to be contained by the range
	 * @param end The last value to be contained by the range
	 * @return A range containing these two values, and no values beyond them.
	 */
	public static IntRange inclusive(int start, int end) {
		if (start>end) return new IntRange(end, start-end + 1);
		return new IntRange(start, end-start + 1);
	}
	
	/**
	 * Creates a half-open range which includes the start value, and all values up to but excluding the end value.
	 * @param start The first value to be contained by the range
	 * @param end The smallest value above start which is not contained within the range.
	 * @return A range from start, inclusive, up to but not including end.
	 */
	public static IntRange exclusive(int start, int end) {
		if (start>end) return new IntRange(end, start-end);
		return new IntRange(start, end-start);
	}
	
	/**
	 * Returns true if value falls inside this Range.
	 * @param value The value to test.
	 * @return True if value is inside this Range, false if value is outside this Range.
	 */
	public boolean contains(int value) {
		return
				value >= start &&
				value < start + length;
	}
	
	/**
	 * Produces a stream of all integer values in this Range, in order from lowest to highest.
	 */
	public IntStream stream() {
		return IntStream.range(start, start+length);
	}
	
	/**
	 * Submits each value in this Range to consumer, in order from lowest to highest.
	 */
	public void forEach(IntConsumer consumer) {
		for(int i=start; i<start+length; i++) consumer.accept(i);
	}
}
