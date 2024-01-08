/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2024 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
