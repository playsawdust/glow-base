package com.playsawdust.glow.io.resource;

import java.net.URL;
import java.util.Optional;

public class EmbeddedResourcePool implements ResourcePool {
	private final String origin;
	private final int priority;
	private final ClassLoader classLoader;
	private final String basePath;
	
	public EmbeddedResourcePool(String origin, int priority, ClassLoader classLoader, String basePath) {
		this.origin = origin;
		this.priority = priority;
		this.classLoader = classLoader;
		this.basePath = basePath;
	}
	
	private static String constructResourceName(Identifier id, String basePath) {
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(basePath);
		if (!basePath.endsWith("/")) pathBuilder.append('/');
		pathBuilder.append(id.namespace());
		pathBuilder.append('/');
		pathBuilder.append(id.path());
		return pathBuilder.toString();
	}
	
	@Override
	public String origin() { return origin; }
	@Override
	public int priority() { return priority; }

	@Override
	public Optional<Resource> find(Identifier resource) {
		String resName = constructResourceName(resource, basePath);
		URL loc = classLoader.getResource(resName);
		if (loc == null) return Optional.empty();
		return Optional.of(new Resource.EmbeddedResource(resource, origin, classLoader, resName));
	}
	
}
