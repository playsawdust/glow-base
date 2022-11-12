package com.playsawdust.glow.image.vector;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface Curve {
	/**
	 * Approximates this Curve using straight lines. No guarantee is made about the spacing of the points or how they
	 * are generated.
	 * @param maxSegments a limit for how many segments may be used to approximate this Curve. Must be at least 1.
	 * @return A List of LineSegments approximating this curve. The caller should not attempt to modify this List.
	 */
	public default List<LineSegment> approximate(int maxSegments) {
		return approximate(maxSegments, null);
	}
	
	/**
	 * Approximates this Curve using straight lines. No guarantee is made about the spacing of the points or how they
	 * are generated.
	 * @param maxSegments a limit for how many segments may be used to approximate this Curve. Must be at least 1.
	 * @param destination a List to append the LineSegments approximating this curve to.
	 * @return If destination is nonnull, destination will be returned with LineSegments appended to it. If destination
	 *         is null, a List of LineSegments will be returned. If the caller did not create the list, they should not
	 *         modify it.
	 */
	List<LineSegment> approximate(int maxSegments, @Nullable List<LineSegment> destination);
}
