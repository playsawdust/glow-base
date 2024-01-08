/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.render;

import java.util.Arrays;

import static com.playsawdust.glow.image.RasterizerUtils.*;

import com.playsawdust.glow.image.ImageData;
import com.playsawdust.glow.image.Sized;
import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.vecmath.Matrix2;
import com.playsawdust.glow.vecmath.Vector2d;

public interface Painter extends Sized {
	default void drawImage(ImageData image, int x, int y) {
		drawImage(image, x, y, 0, 0, image.getWidth(), image.getHeight(), 1.0f);
	}
	
	default void drawImage(ImageData image, int x, int y, float opacity) {
		drawImage(image, x, y, 0, 0, image.getWidth(), image.getHeight(), 1.0f);
	}
	
	void drawImage(ImageData image, int destX, int destY, int srcX, int srcY, int width, int height, float opacity);
	
	void drawTintImage(ImageData image, int destX, int destY, int srcX, int srcY, int width, int height, RGBColor tintColor);
	
	void drawPixel(int x, int y, RGBColor color);
	
	default void clear(RGBColor color) {
		for(int y=0; y<getHeight(); y++) {
			for(int x=0; x<getWidth(); x++) {
				drawPixel(x, y, color);
			}
		}
	}
	
	default void drawLine(double x1, double y1, double x2, double y2, RGBColor color) {
		double dx = x2-x1;
		double dy = y2-y1;
		double scale = Math.max(Math.abs(dx), Math.abs(dy));
		if(scale == 0) {
			//Since we divide by scale later, we can't proceed, but this condition represents a zero-length line. The
			//first and last pixels are the same, and we draw that pixel here to preserve fine edge details the best we
			//can.
			drawPixel((int) x1, (int) y1, color);
			return;
		}
		dx /= scale;
		dy /= scale;
		
		double xi = x1;
		double yi = y1;
		for(int i=0; i<(int)scale; i++) {
			drawPixel((int)xi, (int)yi, color);
			xi += dx;
			yi += dy;
		}
		drawPixel((int)xi, (int)yi, color);
	}
	
	public default void drawLine(double x1, double y1, double x2, double y2, RGBColor color, double lineWeight) {
		if (Math.abs(x2-x1)==0 && Math.abs(y2-y1)==0) return; //Degenerate line
		if (lineWeight==1.0) {
			drawLine(x1, y1, x2, y2, color);
			return;
		}
		
		Vector2d ab = new Vector2d(x2-x1, y2-y1);
		ab.normalize();
		
		Vector2d normal = new Vector2d(ab.y(), -ab.x()).normalize();
		normal = normal.multiply(lineWeight/2.0);
		//System.out.println(normal);
		
		//int debugColor3 = 0x66_FF00FF;
		
		fillQuad(
				(int) (x1-normal.x()), (int) (y1-normal.y()),
				(int) (x1+normal.x()), (int) (y1+normal.y()),
				(int) (x2+normal.x()), (int) (y2+normal.y()),
				(int) (x2-normal.x()), (int) (y2-normal.y()),
				color
				);
	}
	
	public default void drawArrow(double x1, double y1, double x2, double y2, RGBColor color, double lineWeight, double headLength, double headWidth, boolean fill) {
		
		Vector2d p1 = new Vector2d(x1, y1);
		Vector2d p2 = new Vector2d(x2, y2);
		
		
		Vector2d tangent = new Vector2d(x2-x1, y2-y1); //p2-p1
		double lineLength = tangent.length();
		if (lineLength==0) return; //We can't even meaningfully point at anything like this
		tangent = tangent.divide(lineLength); //Same as normalize but reusing the lineLength
		Vector2d normal = Matrix2.rotate(Math.PI/2).transform(tangent); //rotate to the right 90 degrees to get our second basis vector
		
		//Get the line minus the head
		Vector2d tailLine = tangent.multiply(lineLength-headLength);
		Vector2d headBase = p1.add(tailLine);
		drawLine(p1.x(), p1.y(), headBase.x(), headBase.y(), color, lineWeight);
		
		//Get the tail points
		Vector2d headBaseLeft = headBase.subtract(normal.multiply(headWidth));
		Vector2d headBaseRight = headBase.add(normal.multiply(headWidth));
		
		if (fill) {
			drawLine(p1.x(), p1.y(), headBase.x(), headBase.y(), color, lineWeight);
			fillTriangle((int) headBaseLeft.x(), (int) headBaseLeft.y(), (int) headBaseRight.x(), (int) headBaseRight.y(), (int) x2, (int) y2, color);
		} else {
			drawLine(p1.x(), p1.y(), p2.x(), p2.y(), color, lineWeight);
			drawLine(p2.x(), p2.y(), headBaseLeft.x(), headBaseLeft.y(), color, lineWeight);
			drawLine(p2.x(), p2.y(), headBaseRight.x(), headBaseRight.y(), color, lineWeight);
		}
	}
	
	public default void drawQuadraticCurve(double x1, double y1, double x2, double y2, double x3, double y3, int precision, RGBColor color) {
		double stepSize = 1.0 / precision;
		double t = 0;
		double lastX = x1;
		double lastY = y1;
		while(t<1) {
			double x = quadratic(x1, x2, x3, t);
			double y = quadratic(y1, y2, y3, t);
			drawLine(lastX, lastY, x, y, color);
			
			t+= stepSize;
			lastX = x;
			lastY = y;
		}
		drawLine(lastX, lastY, x3, y3, color);
	}
	
	public default void fillRect(int x, int y, int width, int height, RGBColor color) {
		for(int yi = 0; yi<height; yi++) {
			for(int xi = 0; xi<width; xi++) {
				drawPixel(x + xi, y + yi, color);
			}
		}
	}
	
	public default void outlineRect(int x, int y, int width, int height, RGBColor color, double lineWeight) {
		int full = (int)lineWeight;
		if (full==0) full=1;
		int above = (int) (lineWeight / 2.0);
		int below = full - above;
		
		
		//top
		fillRect(x-above, y-above, width+full, full, color);
		//bottom
		fillRect(x-above, y+height-above, width+full, full, color);
		//left - withdraw the top and bottom edge to not collide with top or bottom
		fillRect(x-above, y+below, full, height-full, color);
		//right - " "
		fillRect(x+width-above, y+below, full, height-full, color);
	}
	
	public default void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, RGBColor color) {
		int minY = min(y1, y2, y3);
		int maxY = max(y1, y2, y3);
		
		int dy = maxY-minY;
		
		int[] xStart = new int[dy];
		int[] xEnd = new int[dy];
		Arrays.fill(xStart, Integer.MAX_VALUE);
		Arrays.fill(xEnd, -1);
		
		bresenham(x1,y1,x2,y2, (x,y)->{
			int yi = ((int) y) - minY;
			if (yi<0 || yi>=dy) return;
			
			xStart[yi] = Math.min(xStart[yi], (int) x);
			xEnd[yi] = Math.max(xEnd[yi], (int) x);
		});
		
		bresenham(x2,y2,x3,y3, (x,y)->{
			int yi = ((int) y) - minY;
			if (yi<0 || yi>=dy) return;
			
			xStart[yi] = Math.min(xStart[yi], (int) x);
			xEnd[yi] = Math.max(xEnd[yi], (int) x);
		});
		
		bresenham(x3,y3,x1,y1, (x,y)->{
			int yi = ((int) y) - minY;
			if (yi<0 || yi>=dy) return;
			
			xStart[yi] = Math.min(xStart[yi], (int) x);
			xEnd[yi] = Math.max(xEnd[yi], (int) x);
		});
		
		for(int y=0; y<dy; y++) {
			if (xEnd[y]>xStart[y]) {
				for(int x=xStart[y]; x<=xEnd[y]; x++) {
					drawPixel(x, y+minY, color);
				}
			}
		}
	}
	
	public default void fillQuad(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, RGBColor color) {
		int minY = min(y1, y2, y3, y4);
		int maxY = max(y1, y2, y3, y4);
		
		//int dx = maxX-minX;
		int dy = maxY-minY;
		
		int[] xStart = new int[dy];
		int[] xEnd = new int[dy];
		Arrays.fill(xStart, Integer.MAX_VALUE);
		Arrays.fill(xEnd, -1);
		
		bresenham(x1,y1,x2,y2, (x,y)->{
			int yi = ((int) y) - minY;
			if (yi<0 || yi>=dy) return;
			
			xStart[yi] = Math.min(xStart[yi], (int) x);
			xEnd[yi] = Math.max(xEnd[yi], (int) x);
		});
		
		bresenham(x2,y2,x3,y3, (x,y)->{
			int yi = ((int) y) - minY;
			if (yi<0 || yi>=dy) return;
			
			xStart[yi] = Math.min(xStart[yi], (int) x);
			xEnd[yi] = Math.max(xEnd[yi], (int) x);
		});
		
		bresenham(x3,y3,x4,y4, (x,y)->{
			int yi = ((int) y) - minY;
			if (yi<0 || yi>=dy) return;
			
			xStart[yi] = Math.min(xStart[yi], (int) x);
			xEnd[yi] = Math.max(xEnd[yi], (int) x);
		});
		
		bresenham(x4,y4,x1,y1, (x,y)->{
			int yi = ((int) y) - minY;
			if (yi<0 || yi>=dy) return;
			
			xStart[yi] = Math.min(xStart[yi], (int) x);
			xEnd[yi] = Math.max(xEnd[yi], (int) x);
		});
		
		for(int y=0; y<dy; y++) {
			if (xEnd[y]>xStart[y]) {
				for(int x=xStart[y]; x<=xEnd[y]; x++) {
					drawPixel(x, y+minY, color);
				}
			}
		}
	}
	
	//TODO: This could probably be more efficiently done using horizontal strips
	public default void fillCircle(int x, int y, double radius, RGBColor color) {
		int ir = (int) Math.ceil(radius);
		double r2 = radius * radius;
		
		for(int iy=y-ir; iy<=y+ir; iy++) {
			for(int ix=x-ir; ix<=x+ir; ix++) {
				double dx = ix-x;
				double dy = iy-y;
				double d = dx*dx + dy*dy;
				if (d<r2) drawPixel(ix, iy, color);
			}
		}
	}
	
	public default void outlineCircle(int x, int y, double radius, RGBColor color, double weight) {
		weight = weight / 2.0;
		int ir = (int) Math.ceil(radius);
		//double r2 = radius * radius;
		double innerRadius = (radius-weight) * (radius-weight);
		double outerRadius = (radius+weight) * (radius+weight);
		
		for(int iy=y - ir - (int) radius; iy<=y + ir + (int) radius; iy++) {
			for(int ix=x - ir - (int) radius; ix<=x + ir + (int) radius; ix++) {
				double dx = ix-x;
				double dy = iy-y;
				double d = dx*dx + dy*dy;
				if (d>=innerRadius && d<=outerRadius) drawPixel(ix, iy, color);
			}
		}
	}
	
	//TODO: These can be enabled when we bring shapes back online
	/*
	public default void fillShape(VectorShape shape, int x, int y, RGBColor color) {
		RectangleI bounds = shape.getBoundingBox();
		for(int yi=bounds.y(); yi<bounds.y()+bounds.height(); yi++) {
			for(int xi=bounds.x(); xi<bounds.x()+bounds.width(); xi++) {
				if (shape.contains(xi+0.1, yi+0.1)) {
					drawPixel(xi+x, yi+y, color);
				}
			}
		}
	}
	
	public default void outlineShape(VectorShape shape, int x, int y, RGBColor color, double weight) {
		weight /= 2.0;
		
		RectangleI bounds = shape.getBoundingBox();
		for(int yi=bounds.y()-(int)Math.ceil(weight); yi<bounds.y()+bounds.height()+(int)Math.ceil(weight); yi++) {
			for(int xi=bounds.x()-(int)Math.ceil(weight); xi<bounds.x()+bounds.width()+(int)Math.ceil(weight); xi++) {
				double dist = shape.distanceFromBorder(xi+0.1, yi+0.1);
				if (dist<weight) drawPixel(xi+x, yi+y, color);
			}
		}
	}*/
	
	private static int min(int i1, int i2, int i3) {
		return Math.min(i1, Math.min(i2, i3));
	}
	
	private static int min(int i1, int i2, int i3, int i4) {
		return Math.min(Math.min(i1, i2), Math.min(i3, i4));
	}
	
	private static int max(int i1, int i2, int i3) {
		return Math.max(i1, Math.max(i2, i3));
	}
	
	private static int max(int i1, int i2, int i3, int i4) {
		return Math.max(Math.max(i1, i2), Math.max(i3, i4));
	}
	
	
}
