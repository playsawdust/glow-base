package com.playsawdust.glow.function;

public abstract class AbstractSentinel {
private final String debugName;
	
	public AbstractSentinel(String debugName) {
		this.debugName = debugName;
	}
	
	public String getDebugName() {
		return debugName;
	}
	
	public String toString() {
		return debugName;
	}
}
