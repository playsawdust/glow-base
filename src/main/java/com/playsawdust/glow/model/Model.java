/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.glow.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Model implements Iterable<MeshConvertible> {
	protected List<MeshConvertible> meshes = new ArrayList<>();
	/*
	public void transform(Matrix3d matrix) {
		for(Mesh mesh : meshes) mesh.transform(matrix);
	}
	
	public void transform(Matrix4d matrix) {
		for(Mesh mesh : meshes) mesh.transform(matrix);
	}*/
	
	public List<MeshConvertible> getMeshes() {
		return meshes;
	}
	
	@Override
	public Iterator<MeshConvertible> iterator() {
		return meshes.iterator();
	}
}
