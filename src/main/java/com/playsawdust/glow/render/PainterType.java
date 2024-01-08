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
import com.playsawdust.glow.image.BlendModePainterType;
import com.playsawdust.glow.image.color.BlendMode;

public class PainterType extends AbstractSentinel {
	public static final PainterType NORMAL = new BlendModePainterType("normal_painter", BlendMode.NORMAL);
	public static final PainterType TEXT = new PainterType("sdf_painter");
	
	public PainterType(String debugName) {
		super(debugName);
	}
}
