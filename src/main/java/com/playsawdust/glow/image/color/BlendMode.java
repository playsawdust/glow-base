package com.playsawdust.glow.image.color;

import com.playsawdust.glow.function.FloatBinaryOperator;

public interface BlendMode {
	RGBColor blend(RGBColor src, RGBColor dest, float alpha);
	
	default RGBColor blend(RGBColor src, RGBColor dest) {
		return blend(src, dest, 1.0f);
	}
	
	public static FloatBinaryOperator OP_NORMAL   = (src, dest) -> src;
	public static FloatBinaryOperator OP_MULTIPLY = (src, dest) -> src * dest;
	public static FloatBinaryOperator OP_DIVIDE   = (src, dest) -> src / dest;
	public static FloatBinaryOperator OP_ADD      = (src, dest) -> src + dest;
	public static FloatBinaryOperator OP_SUBTRACT = (src, dest) -> src - dest;
	
	public static FloatBinaryOperator OP_DODGE        = (src, dest) -> dest / (1-src);
	public static FloatBinaryOperator OP_LINEAR_DODGE = OP_ADD;
	public static FloatBinaryOperator OP_BURN         = (src, dest) -> 1 - ((1 - dest) / src);
	public static FloatBinaryOperator OP_LINEAR_BURN  = (src, dest) -> src + dest - 1;
	
	public static FloatBinaryOperator OP_DARKEN       = Math::min;
	public static FloatBinaryOperator OP_LIGHTEN      = Math::max;
	public static FloatBinaryOperator OP_SCREEN       = (src, dest) -> 1 - ((1 - src) * (1 - dest));
	public static FloatBinaryOperator OP_OVERLAY      = (src, dest) -> (src < 0.5) ? 2 * src * dest : OP_SCREEN.applyAsFloat(src, dest);
	
	public static Piecewise NORMAL       = new Piecewise(OP_NORMAL);
	public static Piecewise MULTIPLY     = new Piecewise(OP_MULTIPLY);
	public static Piecewise DIVIDE       = new Piecewise(OP_DIVIDE);
	public static Piecewise ADD          = new Piecewise(OP_ADD);
	public static Piecewise SUBTRACT     = new Piecewise(OP_SUBTRACT);
	
	public static Piecewise DODGE        = new Piecewise(OP_DODGE);
	public static Piecewise LINEAR_DODGE = ADD;
	public static Piecewise BURN         = new Piecewise(OP_BURN);
	public static Piecewise LINEAR_BURN  = new Piecewise(OP_LINEAR_BURN);
	
	public static Piecewise DARKEN       = new Piecewise(OP_DARKEN);
	public static Piecewise LIGHTEN      = new Piecewise(OP_LIGHTEN);
	public static Piecewise SCREEN       = new Piecewise(OP_SCREEN);
	public static Piecewise OVERLAY      = new Piecewise(OP_OVERLAY);
	
	public static Lab MULTIPLY_LAB     = new Lab(OP_MULTIPLY);
	public static Lab DIVIDE_LAB       = new Lab(OP_DIVIDE);
	public static Lab ADD_LAB          = new Lab(OP_ADD);
	public static Lab SUBTRACT_LAB     = new Lab(OP_SUBTRACT);
	
	public static Lab DODGE_LAB        = new Lab(OP_DODGE);
	public static Lab LINEAR_DODGE_LAB = new Lab(OP_LINEAR_DODGE);
	public static Lab BURN_LAB         = new Lab(OP_BURN);
	public static Lab LINEAR_BURN_LAB  = new Lab(OP_LINEAR_BURN);
	
	//TODO: Tweak darken and lighten to max/min out only the L* value
	public static Lab SCREEN_LAB    = new Lab(OP_SCREEN);
	public static Lab OVERLAY_LAB   = new Lab(OP_OVERLAY);
	
	public static class Piecewise implements BlendMode {
		private final FloatBinaryOperator function;
		
		public Piecewise(FloatBinaryOperator function) {
			this.function = function;
		}
		
		@Override
		public RGBColor blend(RGBColor src, RGBColor dest, float alpha) {
			float srcAlpha = src.alpha() * alpha;
			
			float r = function.applyAsFloat(src.r(), dest.r());
			float g = function.applyAsFloat(src.g(), dest.g());
			float b = function.applyAsFloat(src.b(), dest.b());
			float outAlpha = clamp(srcAlpha + (dest.alpha()*(1-srcAlpha)));
			
			return new RGBColor(
					outAlpha,
					lerp(r, dest.r(), srcAlpha),
					lerp(g, dest.g(), srcAlpha),
					lerp(b, dest.b(), srcAlpha)
					);
		}
	}
	
	public static class Lab implements BlendMode {
		private final FloatBinaryOperator function;
		
		public Lab(FloatBinaryOperator function) {
			this.function = function;
		}
		
		@Override
		public RGBColor blend(RGBColor src, RGBColor dest, float alpha) {
			LABColor srcLab = src.toXyz().toLab(Colors.WHITEPOINT_D65);
			LABColor destLab = dest.toXyz().toLab(Colors.WHITEPOINT_D65);
			
			float srcAlpha = src.alpha() * alpha;
			
			float l = function.applyAsFloat(srcLab.l(), destLab.l());
			float a = function.applyAsFloat(srcLab.a(), destLab.a());
			float b = function.applyAsFloat(srcLab.b(), destLab.b());
			float outAlpha = clamp(srcAlpha + (dest.alpha()*(1-srcAlpha)));
			
			return new LABColor(
					outAlpha,
					lerp(l, destLab.l(), srcAlpha),
					lerp(a, destLab.a(), srcAlpha),
					lerp(b, destLab.b(), srcAlpha)
					).toXyz(Colors.WHITEPOINT_D65).toRgb();
		}
	}
	
	private static float clamp(float value) {
		if (value<0f) return 0f;
		if (value>1f) return 1f;
		return value;
	}
	
	private static float lerp(float a, float b, float t) {
		return a*(1-t) + b*t;
	}
}
