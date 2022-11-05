package com.playsawdust.glow.image;

import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.render.Renderer;
import com.playsawdust.glow.vecmath.Matrix4;
import com.playsawdust.glow.vecmath.Vector3d;

public class ImageRenderer implements Renderer {
	private Matrix4 transform = Matrix4.IDENTITY; // Identity puts 0,0,0 in the middle, GL style
	private float[] zbuffer;
	private ImageData target;
	private int[] leftPos;
	private int[] rightPos;
	private Vector3d[] left3D;
	private Vector3d[] right3D;
	
	public ImageRenderer(ImageData target) {
		this.target = target;
		this.zbuffer = new float[target.getWidth() * target.getHeight()];
	}

	@Override
	public Matrix4 getTransform() {
		return transform;
	}

	@Override
	public void setTransform(Matrix4 transform) {
		this.transform = transform;
	}

	@Override
	public void fillTriangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, RGBColor color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void textureTriangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, ImageData texture) {
		// TODO Auto-generated method stub
		
	}
	
}
