package com.playsawdust.glow.io.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Resource {
	public Identifier id();
	public String origin();
	public InputStream asInputStream() throws IOException;
	
	public static class BufferedResource implements Resource {
		private final Identifier id;
		private final String origin;
		private final byte[] data;
		
		public BufferedResource(Identifier id, String origin, byte[] data) {
			this.id = id;
			this.origin = origin;
			this.data = data;
		}
		
		@Override
		public Identifier id() { return id; }
		@Override
		public String origin() { return origin; }
		@Override
		public InputStream asInputStream() { return new ByteArrayInputStream(data); }
	}
	
	public static class PathResource implements Resource {
		private final Identifier id;
		private final String origin;
		private final Path path;
		
		public PathResource(Identifier id, String origin, Path data) {
			this.id = id;
			this.origin = origin;
			this.path = data;
		}
		
		@Override
		public Identifier id() { return id; }
		@Override
		public String origin() { return origin; }
		@Override
		public InputStream asInputStream() throws IOException {
			return Files.newInputStream(path);
		}
	}
	
	public static class EmbeddedResource implements Resource {
		private final Identifier id;
		private final String origin;
		private final ClassLoader classLoader;
		private final String resourceLocation;
		
		public EmbeddedResource(Identifier id, String origin, ClassLoader classLoader, String resourceLocation) {
			this.id = id;
			this.origin = origin;
			this.classLoader = classLoader;
			this.resourceLocation = resourceLocation;
		}
		
		
		
		@Override
		public Identifier id() { return id; }
		@Override
		public String origin() { return origin; }

		@Override
		public InputStream asInputStream() throws IOException {
			return classLoader.getResourceAsStream(resourceLocation);
		}
		
	}
}
