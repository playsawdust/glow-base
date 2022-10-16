/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.glow.model;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ShaderAttributeHolder {
	public @Nullable <T> T get(ShaderAttribute<T> attribute);
	
	public default <T> T get(ShaderAttribute<T> attribute, T fallback) {
		T t = get(attribute);
		return (t==null) ? fallback : t;
	}
	
	public Map<ShaderAttribute<?>, Object> getAll();
}
