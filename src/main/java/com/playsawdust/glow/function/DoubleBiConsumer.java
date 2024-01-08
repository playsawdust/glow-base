/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
