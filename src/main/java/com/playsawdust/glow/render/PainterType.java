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
