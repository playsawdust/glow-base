package com.playsawdust.glow.io.resource;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Identifier {
	private static final Predicate<String> IDENTIFIER = Pattern.compile("[a-z0-9._]+:[a-z0-9._]+(?:\\/[a-z0-9._]+)*").asMatchPredicate();
	//private static final Predicate<String> PATH = Pattern.compile("[a-z0-9._]+(?:\\/[a-z0-9._]+)*").asMatchPredicate();
	//private static final Predicate<String> PATH_ELEMENT = Pattern.compile("[a-z0-9._]+").asMatchPredicate();
	
	private final String value;
	
	private Identifier(String safeValue) {
		this.value = safeValue;
	}
	
	public String toString() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Identifier id) && id.value == this.value;
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	public String namespace() {
		int idx = value.indexOf(':');
		if (idx == -1) return value;
		return value.substring(0, idx);
	}
	
	public String path() {
		int idx = value.indexOf(':');
		if (idx == -1) return "";
		return value.substring(idx, value.length());
	}
	
	public String[] pathElements() {
		return path().split("\\/");
	}
	
	public static Identifier of(String value) {
		if (value == null) throw new NullPointerException("Identifier value cannot be null");
		if (!IDENTIFIER.test(value)) {
			// TODO: Do more specific tests to see how the Identifier value fails?
			throw new IllegalArgumentException("Invalid Identifier.");
		}
		
		return new Identifier(value);
	}
}
