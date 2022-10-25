/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2022 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image.color;

import com.playsawdust.glow.vecmath.Vector3d;

public record XYZColor(float alpha, float x, float y, float z) {
	
	
	public XYZColor(float alpha, Vector3d xyz) {
		this(alpha, (float) xyz.x(), (float) xyz.y(), (float) xyz.z());
	}
	
	public RGBColor toRgb() {
		float xc = x; if (xc<0) xc=0; if (xc>1) xc=1;
		float yc = y; if (yc<0) yc=0; if (yc>1) yc=1;
		float zc = z; if (zc<0) zc=0; if (zc>1) zc=1;
		
		Vector3d result = Colors.CIEXYZ_TO_RGB.transform(new Vector3d(xc, yc, zc));
		return new RGBColor(alpha, result);
	}
	
	public LABColor toLab(Vector3d whitePoint) {
		float xn = x/(float) (whitePoint.x()/100);
		float yn = y/(float) (whitePoint.y()/100);
		float zn = z/(float) (whitePoint.z()/100);
		
		float fxn = 0;
		float fyn = 0;
		float fzn = 0;
		
		
		if (xn > 0.008856f) {
			fxn = (float) Math.pow(xn, 1/3f);
		} else {
			fxn = xn * 7.787f + (16/116f);
		}
		
		if (yn > 0.008856f) {
			fyn = (float) Math.pow(yn, 1/3f);
		} else {
			fyn = yn * 7.787f + (16/116f);
		}
		
		if (zn > 0.008856f) {
			fzn = (float) Math.pow(zn, 1/3f);
		} else {
			fzn = zn * 7.787f + (16/116f);
		}
		
		
		float l = 0;
		float a = 0;
		float b = 0;
		
		if (yn > 0.008856f) {
			l = (float) Math.pow(yn, 1/3f) * 116 - 16;
		} else {
			l = 903.3f * yn;
		}
		
		a = 500 * (fxn - fyn);
		b = 200 * (fyn - fzn);
		
		return new LABColor(alpha, l, a, b);
	}
}
