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
