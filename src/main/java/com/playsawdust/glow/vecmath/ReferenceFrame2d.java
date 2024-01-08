/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.vecmath;

/**
 * Represents an origin and a set of vectors which form a basis for local space. Our basis vectors are tangent (forward)
 * and bitangent (right). Tangent and bitangent MUST be linearly independant, and SHOULD be orthogonal.
 * 
 */
public record ReferenceFrame2d(Vector2d origin, Vector2d tangent, Vector2d bitangent) {

}
