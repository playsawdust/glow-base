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
 * A reference frame in 3D. This consists of an origin and basis vectors (see note below on bases).
 * 
 * <p>How to interpret these vectors really depends on what this reference frame is a basis for.
 * 
 * <p>If this reference frame relates to a spline, "tangent" is your "forward" vector, pointing along the spline in the
 * +t direction towards its next or last point. "bitangent" is a "right" vector, pointing out from the plane defined by
 * this section of the curve. "normal" can be a bit weird. In a Frenet frame this will typically point away from the
 * "top" of the curve, always choosing the "outer" side of a loop. In a rotation-minimizing or other stepwise frame,
 * the inner loop may be chosen based on the goals of the algorithm. Regardless, the normal vector is normal to the
 * plane defined by tangent and bitangent.
 * 
 * <p>If this reference frame relates to a triangle or a surface, tangent and bitangent define a coordinate space over
 * the plane of the surface, e.g. for texture mapping. Normal points away from the surface as expected, in a direction
 * determined by the winding of the points. (the "front face", which the normal points out of, is the one with counter-
 * clockwise winding when you look straight at it)
 * 
 * <p>If this reference frame is of a camera, "tangent" is the look vector, "bitangent" is right, and "normal" is up.
 * 
 * <p>Note: "Basis vectors" represented here are NEVER the OpenGL basis vectors, or a rotation of the OpenGL basis vectors.
 * The basis vectors in OpenGL go up, right, and backwards, making them right-handed. The vectors in a ReferenceFrame go
 * up, right, and forwards, making it left-handed. If you really need OpenGL basis vectors, e.g. for rotations and
 * consistent cross products, multiply the tangent vector by -1. This is acceptable because all assets are presented in
 * right-handed coordinates; if you wanted to import a left-handed asset that would be a change-of-basis operation which
 * is two matrix multiplies on every point, normal, pseudovector, etc.
 */
public record ReferenceFrame3d(Vector3d origin, Vector3d tangent, Vector3d bitangent, Vector3d normal) {
}
