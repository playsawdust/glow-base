/**
 * Glow - GL Object Wrapper
 * Copyright (C) 2020-2022 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.playsawdust.glow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.playsawdust.glow.image.color.Colors;
import com.playsawdust.glow.image.color.LABColor;
import com.playsawdust.glow.image.color.RGBColor;
import com.playsawdust.glow.image.color.XYZColor;

public class TestColorMath {
	
	/* Reversibility tests. Make sure that the math is at least correct enough that the transforms can be undone */
	
	@Test
	public void testRgbXyz() {
		RGBColor subject = new RGBColor(0xFF_a532e1); //A nice purple
		
		XYZColor xyz = subject.toXyz();
		
		RGBColor result = xyz.toRgb();
		
		Assertions.assertEquals(result.r(), subject.r(), 0.000001);
		Assertions.assertEquals(result.g(), subject.g(), 0.000001);
		Assertions.assertEquals(result.b(), subject.b(), 0.000001);
	}
	
	@Test
	public void testXyzLab() {
		XYZColor subject = new RGBColor(0xFF_a532e1).toXyz();
		
		LABColor lab = subject.toLab(Colors.WHITEPOINT_D65);
		
		XYZColor result = lab.toXyz(Colors.WHITEPOINT_D65);
		
		Assertions.assertEquals(result.x(), subject.x(), 0.000001);
		Assertions.assertEquals(result.y(), subject.y(), 0.000001);
		Assertions.assertEquals(result.z(), subject.z(), 0.000001);
	}
}
