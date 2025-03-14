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

public class EditorMesh {
	private ArrayList<EditorPoint> points = new ArrayList<>();
	private ArrayList<EditorEdge> edges = new ArrayList<>();
	private ArrayList<EditorFace> faces = new ArrayList<>();
}
