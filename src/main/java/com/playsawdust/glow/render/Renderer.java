/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.render;

import java.util.Iterator;
import java.util.function.Function;

import com.playsawdust.glow.image.ImageData;
import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.model.Material;
import com.playsawdust.glow.model.Mesh;
import com.playsawdust.glow.model.Model;
import com.playsawdust.glow.model.ShaderAttribute;
import com.playsawdust.glow.model.ShaderAttributeHolder;
import com.playsawdust.glow.vecmath.Matrix4;
import com.playsawdust.glow.vecmath.Vector3d;

/**
 * Implementors of this class can display an object in the 3d world.
 */
public interface Renderer {
	default void renderModel(Model m, double x, double y, double z, Function<String, ImageData> textureGetter) {
		for(Mesh mesh : m) {
			Material material = mesh.getMaterial();
			String s = material.get(ShaderAttribute.DIFFUSE_TEXTURE);
			if (s!=null) {
				//Render this mesh textured
				ImageData texture = textureGetter.apply(s);
				RGBColor backupColor = material.get(ShaderAttribute.DIFFUSE_COLOR, new RGBColor(1,1,1,1));
				for(Mesh.Face face : mesh.createTriangleList()) {
					
					Iterator<Mesh.Vertex> iterator = face.iterator();
					Mesh.Vertex va = iterator.next();
					Mesh.Vertex vb = iterator.next();
					Mesh.Vertex vc = iterator.next();
					
					drawTriangle(va, vb, vc, material, null);
					/*
					Vector3d a = va.get(ShaderAttribute.POSITION);
					Vector3d b = vb.get(ShaderAttribute.POSITION);
					Vector3d c = vc.get(ShaderAttribute.POSITION);
					
					if (texture==null) {
						fillTriangle(a.x(), a.y(), a.z(), b.x(), b.y(), b.z(), c.x(), c.y(), c.z(), backupColor);
					} else {
						textureTriangle(a.x(), a.y(), a.z(), b.x(), b.y(), b.z(), c.x(), c.y(), c.z(), texture);
					}*/
				}
			} else {
				RGBColor color = material.get(ShaderAttribute.DIFFUSE_COLOR, new RGBColor(1,1,1,1));
				for(Mesh.Face face : mesh.createTriangleList()) {
					Iterator<Mesh.Vertex> iterator = face.iterator();
					Mesh.Vertex va = iterator.next();
					Mesh.Vertex vb = iterator.next();
					Mesh.Vertex vc = iterator.next();
					
					Vector3d a = va.get(ShaderAttribute.POSITION);
					Vector3d b = vb.get(ShaderAttribute.POSITION);
					Vector3d c = vc.get(ShaderAttribute.POSITION);
					
					fillTriangle(a.x(), a.y(), a.z(), b.x(), b.y(), b.z(), c.x(), c.y(), c.z(), color);
				}
			}
		}
	}
	default void renderModel(Model m, Vector3d location, Function<String, ImageData> textureGetter) {
		renderModel(m, location.x(), location.y(), location.z(), textureGetter);
	}
	
	void fillTriangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, RGBColor color);
	void drawTriangle(Mesh.Vertex a, Mesh.Vertex b, Mesh.Vertex c, Material material, ShaderAttributeHolder environment);
	
	Matrix4 getTransform();
	void setTransform(Matrix4 transform);
}
