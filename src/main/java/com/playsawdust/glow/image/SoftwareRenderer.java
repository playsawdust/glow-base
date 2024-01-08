/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.model.Material;
import com.playsawdust.glow.model.Mesh;
import com.playsawdust.glow.model.ShaderAttributeHolder;
import com.playsawdust.glow.model.Mesh.Vertex;
import com.playsawdust.glow.model.ShaderAttribute;
import com.playsawdust.glow.render.Painter;
import com.playsawdust.glow.render.Renderer;
import com.playsawdust.glow.vecmath.Matrix4;
import com.playsawdust.glow.vecmath.Vector3d;

public class SoftwareRenderer implements Renderer {
	private Matrix4 transform = Matrix4.IDENTITY; // Identity puts 0,0,0 in the middle, GL style
	private float[] zbuffer;
	private Painter target;
	private Vector3d[] leftPoints;
	private Vector3d[] rightPoints;
	
	private PixelShader shader = SoftwareRenderer::defaultShader;
	
	public SoftwareRenderer(Painter target) {
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
		int xmin = (int) Math.min(x1, Math.min(x2, x3));
		int ymin = (int) Math.min(y1, Math.min(y2, y3));
		int zmin = (int) Math.min(z1, Math.min(z2, z3));
		int xmax = (int) Math.ceil(Math.max(x1, Math.max(x2, x3)));
		int ymax = (int) Math.ceil(Math.max(y1, Math.max(y2, y3)));
		int zmax = (int) Math.ceil(Math.max(z1, Math.max(z2, z3)));
		
		int xsize = xmax-xmin; if (xsize<1) xsize=1;
		int ysize = ymax-ymin; if (ysize<1) ysize=1;
		int zsize = zmax-zmin; if (zsize<1) zsize=1;
		
		
		if (leftPoints==null || rightPoints==null) {
			leftPoints = new Vector3d[ysize];
			rightPoints = new Vector3d[ysize];
		} else if (leftPoints.length<ysize || rightPoints.length<ysize) {
			leftPoints = Arrays.copyOf(leftPoints, ysize);
			rightPoints = Arrays.copyOf(rightPoints, ysize);
		}
		
		Arrays.fill(leftPoints, new Vector3d(Double.MAX_VALUE, 0, 0));
		Arrays.fill(rightPoints, new Vector3d(Double.MIN_VALUE, 0, 0));
		
		//Okay, now that we have point lists for each side, do a modified bresenham for each triangle edge
		Consumer<Vector3d> plotter = (vec) -> {
			int y = (int) vec.y();
			int index = y-ymin;
			if (index<0 || index>=leftPoints.length) return;
			//if (y<0 || index<=leftPoints.length || y>=target.getHeight()) return;
			if (vec.x() < leftPoints[index].x()) leftPoints[index] = vec;
			if (vec.x() > rightPoints[index].x()) rightPoints[index] = vec;
		};
		
		bresenham3d(x1,y1,z1, x2,y2,z2, plotter);
		bresenham3d(x2,y2,z2, x3,y3,z3, plotter);
		bresenham3d(x3,y3,z3, x1,y1,z1, plotter);
		
		for(int i=0; i<ysize; i++) {
			
			if (i>=leftPoints.length) continue;
			if (leftPoints[i]==null || leftPoints[i].x() >= target.getWidth()) return; //this scanline is undefined or off the right edge of the screen
			for(int x=(int)leftPoints[i].x(); x<=(int)rightPoints[i].x(); x++) {
				target.drawPixel(x, i+ymin, color);
			}
		}
	}

	@Override
	public void drawTriangle(Vertex a, Vertex b, Vertex c, Material material, ShaderAttributeHolder environment) {
		Vector3d aPosition = a.get(ShaderAttribute.POSITION);
		Vector3d bPosition = b.get(ShaderAttribute.POSITION);
		Vector3d cPosition = c.get(ShaderAttribute.POSITION);
		
		
		int xmin = (int) Math.min(aPosition.x(), Math.min(bPosition.x(), cPosition.x()));
		int ymin = (int) Math.min(aPosition.y(), Math.min(bPosition.y(), cPosition.y()));
		int zmin = (int) Math.min(aPosition.z(), Math.min(bPosition.z(), cPosition.z()));
		int xmax = (int) Math.ceil(Math.max(aPosition.x(), Math.max(bPosition.x(), cPosition.x())));
		int ymax = (int) Math.ceil(Math.max(aPosition.y(), Math.max(bPosition.y(), cPosition.y())));
		int zmax = (int) Math.ceil(Math.max(aPosition.z(), Math.max(bPosition.z(), cPosition.z())));
		
		int xsize = xmax-xmin; if (xsize<1) xsize=1;
		int ysize = ymax-ymin; if (ysize<1) ysize=1;
		int zsize = zmax-zmin; if (zsize<1) zsize=1;
		
		
		if (leftPoints==null || rightPoints==null) {
			leftPoints = new Vector3d[ysize];
			rightPoints = new Vector3d[ysize];
		} else if (leftPoints.length<ysize || rightPoints.length<ysize) {
			leftPoints = Arrays.copyOf(leftPoints, ysize);
			rightPoints = Arrays.copyOf(rightPoints, ysize);
		}
		
		Arrays.fill(leftPoints, new Vector3d(Double.MAX_VALUE, 0, 0));
		Arrays.fill(rightPoints, new Vector3d(Double.MIN_VALUE, 0, 0));
		
		//Okay, now that we have point lists for each side, do a modified bresenham for each triangle edge
		Consumer<Vector3d> plotter = (vec) -> {
			int y = (int) vec.y();
			int index = y-ymin;
			if (index<0 || index>=leftPoints.length) return;
			//if (y<0 || index<=leftPoints.length || y>=target.getHeight()) return;
			if (vec.x() < leftPoints[index].x()) leftPoints[index] = vec;
			if (vec.x() > rightPoints[index].x()) rightPoints[index] = vec;
		};
		
		bresenham3d(aPosition, bPosition, plotter);
		bresenham3d(bPosition, cPosition, plotter);
		bresenham3d(cPosition, aPosition, plotter);
		
		for(int i=0; i<ysize; i++) {
			
			if (i>=leftPoints.length) continue;
			if (leftPoints[i]==null || leftPoints[i].x() >= target.getWidth()) return; //this scanline is undefined or off the right edge of the screen
			for(int x=(int)leftPoints[i].x(); x<=(int)rightPoints[i].x(); x++) {
				//Shade the pixel
				
				//target.drawPixel(x, i+ymin, color);
			}
		}
	}
	
	private static void bresenham3d(Vector3d a, Vector3d b, Consumer<Vector3d> consumer) {
		bresenham3d(a.x(), a.y(), a.z(), b.x(), b.y(), b.z(), consumer);
	}
	
	private static void bresenham3d(double x1, double y1, double z1, double x2, double y2, double z2, Consumer<Vector3d> consumer) {
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		double scale = Math.max(Math.abs(dx), Math.abs(dy)); //Do not factor in Z as it does not change the apparent dimensions after converting to normalized device coordinates.
		if(scale == 0) {
			consumer.accept(new Vector3d(x1, y1, z1));
			return; //Degenerate line will only plot the first/last pixel so we don't DivideByZero
		}
		dx /= scale;
		dy /= scale;
		dz /= scale;
		
		double xi = x1;
		double yi = y1;
		double zi = z1;
		for(int i=0; i<(int)scale; i++) {
			consumer.accept(new Vector3d(xi, yi, zi));
			xi += dx;
			yi += dy;
			zi += dz;
		}
		consumer.accept(new Vector3d(xi, yi, zi));
	}
	
	private class RasterizerLinearData implements ShaderAttributeHolder {
		private final Mesh.Vertex a;
		private final Mesh.Vertex b;
		private final Mesh.Vertex c;
		private final ShaderAttributeHolder delegate;
		
		public RasterizerLinearData(Mesh.Vertex a, Mesh.Vertex b, Mesh.Vertex c, Vector3d position) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.delegate = null;
		}
		
		
		@Override
		public <T> @Nullable T get(ShaderAttribute<T> attribute) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<ShaderAttribute<?>, Object> getAll() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static interface PixelShader {
		public void shade(Painter p, int x, int y, ShaderAttributeHolder fragment);
	}
	
	public static void defaultShader(Painter p, int x, int y, ShaderAttributeHolder fragment) {
		fragment.get(ShaderAttribute.DIFFUSE_COLOR, new RGBColor(0xFFFF00FF));
		p.drawPixel(x, y, null);
	}
}
