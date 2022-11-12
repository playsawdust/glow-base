package com.playsawdust.glow.vecmath;

/**
 * Represents an origin and a set of vectors which form a basis for local space. Our basis vectors are tangent (forward)
 * and bitangent (right). Tangent and bitangent MUST be linearly independant, and SHOULD be orthogonal.
 * 
 */
public record ReferenceFrame2d(Vector2d origin, Vector2d tangent, Vector2d bitangent) {

}
