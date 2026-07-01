package com.playsawdust.glow.io.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.playsawdust.glow.image.ImageData;
import com.playsawdust.glow.image.io.PngImageIO;
import com.playsawdust.glow.io.DataSlice;

public interface Resource {
	public Identifier id();
	public String origin();
	public InputStream asInputStream() throws IOException;
	
	
	public default Optional<ImageData> asImage() {
		try {
			byte[] bytes = asInputStream().readAllBytes();
			return Optional.of(PngImageIO.load(DataSlice.of(bytes)));
		} catch (IOException ex) {
			return Optional.empty();
		}
	}
	
	public default Optional<String> asString() {
		try {
			return Optional.of(new InputStreamReader(asInputStream()).readAllAsString());
		} catch (IOException ex) {
			return Optional.empty();
		}
	}
	
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
