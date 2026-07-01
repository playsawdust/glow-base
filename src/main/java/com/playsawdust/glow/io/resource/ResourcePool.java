package com.playsawdust.glow.io.resource;

import java.util.Optional;

public interface ResourcePool {
	public String origin();
	public int priority();
	public Optional<Resource> find(Identifier resource);
}
