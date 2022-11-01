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
