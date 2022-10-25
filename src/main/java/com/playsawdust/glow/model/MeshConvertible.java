/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2022 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.model;

/**
 * Represents a Mesh, or a Modifier connected to a Mesh, or a mesh generator, or other "mesh source".
 */
public interface MeshConvertible {
	public Mesh getMesh();
}
