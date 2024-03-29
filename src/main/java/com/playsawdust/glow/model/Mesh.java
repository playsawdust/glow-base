/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.playsawdust.glow.vecmath.Matrix3;
import com.playsawdust.glow.vecmath.Vector2d;
import com.playsawdust.glow.vecmath.Vector3d;

public class Mesh {
	protected Material material;
	
	protected Vector3d[] vertexBuffer;
	protected Vector2d[] uvBuffer;
	protected Vector3d[] normalBuffer;
	protected Vertex[] vertexData;
	
	protected int[] indices;
	
	public Mesh(Material material, Vector3d[] vertexBuffer, Vector2d[] uvBuffer, Vector3d[] normalBuffer, int[] indices) {
		this.material = material;
		this.vertexBuffer = vertexBuffer;
		this.uvBuffer = uvBuffer;
		this.normalBuffer = normalBuffer;
		this.indices = indices;
		this.vertexData = new Vertex[vertexBuffer.length];
	}
	
	public void transform(Matrix3 matrix) {
		for(int i=0; i<vertexBuffer.length; i++) {
			vertexBuffer[i] = matrix.transform(vertexBuffer[i]);
		}
	}
	/*
	public void transform(Matrix4 matrix) {
		for(int i=0; i<vertexBuffer.length; i++) {
			vertexBuffer[i] = matrix.transform(vertexBuffer[i]);
		}
	}*/
	
	public List<Face> createTriangleList() {
		int faceCount = indices.length / 3;
		List<Face> result = new ArrayList<>();
		for(int i=0; i<faceCount; i++) {
			int baseIndex = i*3;
			int index1 = indices[baseIndex+0];
			int index2 = indices[baseIndex+1];
			int index3 = indices[baseIndex+2];
			Face curFace = new Face();
			curFace.vertices.add(getVertex(index1));
			curFace.vertices.add(getVertex(index2));
			curFace.vertices.add(getVertex(index3));
			result.add(curFace);
		}
		return result;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * Returns the vertex for the specified index-buffer index
	 * @param index the index of the vertex in the global list
	 * @return a Vertex containing the data for that index
	 */
	public Vertex getVertex(int index) {
		Vertex v = new Vertex();
		v.put(ShaderAttribute.POSITION, vertexBuffer[index]);
		v.put(ShaderAttribute.TEXCOORD, uvBuffer[index]);
		v.put(ShaderAttribute.NORMAL, normalBuffer[index]);
		if (vertexData[index]!=null) {
			v.putAll(vertexData[index]);
		}
		return v;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("{\n");
		result.append("  Material: ");
		result.append(material.toString());
		result.append("\n");
		
		result.append("  Faces: [\n");
		
		List<Mesh.Face> faces = createTriangleList();
		for(Mesh.Face face : faces) {
			StringBuilder faceRep = new StringBuilder("    { ");
			for(Mesh.Vertex v : face) {
				faceRep.append(v);
				faceRep.append(", ");
			}
			
			faceRep.deleteCharAt(faceRep.length()-1); //Delete trailing space
			faceRep.deleteCharAt(faceRep.length()-1); //Delete trailing comma
			
			faceRep.append(" }");
			
			
			result.append(faceRep);
			result.append(",\n");
		}
		result.deleteCharAt(result.length()-1); //Delete trailing newline
		result.deleteCharAt(result.length()-1); //Delete trailing comma
		
		result.append("\n  ]\n}");
		
		return result.toString();
	}
	
	public class Face implements Iterable<Vertex> {
		protected List<Vertex> vertices = new ArrayList<>();
		
		@Override
		public Iterator<Vertex> iterator() {
			return vertices.iterator();
		}
		
		public Stream<Vertex> stream() {
			return vertices.stream();
		}
	}
	
	public class Vertex extends AbstractShaderAttributeHolder {
	}
}
