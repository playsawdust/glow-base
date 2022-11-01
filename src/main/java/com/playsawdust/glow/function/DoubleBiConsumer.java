package com.playsawdust.glow.function;

import java.util.function.BiConsumer;

/**
 * This is the primitive-double type specialization of {@link java.function.BiConsumer}
 */
@FunctionalInterface
public interface DoubleBiConsumer extends BiConsumer<Double, Double> {
	default void accept(Double a, Double b) {
		acceptAsDoubles(a, b);
	}
	
	void acceptAsDoubles(double a, double b);
}
