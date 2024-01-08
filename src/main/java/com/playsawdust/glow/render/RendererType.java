/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.render;

import com.playsawdust.glow.function.AbstractSentinel;

public class RendererType extends AbstractSentinel {
	public static final RendererType SOLID = new RendererType("solid_renderer");
	public static final RendererType TRANSLUCENT = new RendererType("translucent_renderer");
	public static final RendererType TEXT = new RendererType("text_renderer");
	
	public RendererType(String debugName) {
		super(debugName);
	}
}
