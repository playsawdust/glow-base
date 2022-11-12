package com.playsawdust.glow.vecmath;

/**
 * In one sense this is a number range over the [double-addressable] Real numbers. In another sense, it's the 1D
 * parameterization of the {@link Rect2d axis-aligned rectangle}.
 */
public record DoubleRange(double start, double length) {
	/**
	 * Creates a DoubleRange from a starting value and a length. The range will cover the start value and all following
	 * values up to but excluding start+length.
	 * @param start The initial, lowest value which the range shall cover.
	 * @param length The length of the range.
	 */
	public DoubleRange(double start, double length) {
		if (length<0) throw new IllegalArgumentException("Range length cannot be negative");
		this.start = start;
		this.length = length;
	}
	
	/**
	 * Creates a DoubleRange from a start and ending value. Both the start and end value will be included in the
	 * resulting range.
	 * @param start The first value to be contained by the range
	 * @param end The last value to be contained by the range
	 * @return A range containing these two values, and no values beyond them.
	 */
	public static DoubleRange inclusive(double start, double end) {
		if (start>end) return new DoubleRange(end, Math.nextUp(start-end));
		return new DoubleRange(start, Math.nextUp(end-start));
	}
	
	/**
	 * Creates a half-open range which includes the start value, and all values up to but excluding the end value.
	 * @param start The first value to be contained by the range
	 * @param end The smallest value above start which is not contained within the range.
	 * @return A range from start, inclusive, up to but not including end.
	 */
	public static DoubleRange exclusive(double start, double end) {
		if (start>end) return new DoubleRange(end, start-end);
		return new DoubleRange(start, end-start);
	}
	
	/**
	 * Gets the end of the range. In other words, the highest value which is within the range.
	 * @return
	 */
	public double end() {
		return Math.nextDown(start+length);
	}
	
	/**
	 * Returns true if value falls inside this Range.
	 * @param value The value to test.
	 * @return True if value is inside this Range, false if value is outside this Range.
	 */
	public boolean contains(double value) {
		return
				value >= start &&
				value < start + length;
	}
}
