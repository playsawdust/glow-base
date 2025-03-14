/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.model.mutable;

import java.util.ArrayList;
import java.util.List;

import com.playsawdust.glow.vecmath.Vector2d;
import com.playsawdust.glow.vecmath.Vector3d;

public class EditorPoint {
	private Vector3d position = new Vector3d(0, 0, 0);
	private Vector2d uv = new Vector2d(0, 0);
	private List<EditorFace> connectedFaces = new ArrayList<>();
}
