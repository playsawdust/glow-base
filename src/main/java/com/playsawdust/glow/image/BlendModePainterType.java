/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow.image;

import com.playsawdust.glow.image.color.BlendMode;
import com.playsawdust.glow.render.PainterType;

public class BlendModePainterType extends PainterType {
	private final BlendMode blendMode;
	
	public BlendModePainterType(String debugName, BlendMode blendMode) {
		super(debugName);
		this.blendMode = blendMode;
	}
	
	public BlendMode getBlendMode() {
		return blendMode;
	}
}
