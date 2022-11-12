package com.playsawdust.glow.vecmath;

/**
 * Also known as an Axis-Aligned Bounding Box, this class is the 3d equivalent of {@link Rect2d}
 */
public record Rect3d(double x, double y, double z, double xSize, double ySize, double zSize) {
	public Rect3d(double x, double y, double z, double xSize, double ySize, double zSize) {
		if (xSize<0 || ySize<0 || zSize<0) throw new IllegalArgumentException("Cannot have negative dimensions.");
		this.x = x;
		this.y = y;
		this.z = z;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
	}
}
