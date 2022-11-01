package com.playsawdust.glow.image.io.tiff;

/**
 * Represents values from the PhotometricInterpretation tag of a Tiff file
 */
public enum TiffColorType {
	/** Indicates a bilevel (1-bit black and white) image with one sample per pixel: 0=white, 1=black. */
	WHITE_IS_ZERO(0),
	
	/** Indicates a bilevel (1-bit black and white) image with one sample per pixel: 0=black, 1=white. */
	BLACK_IS_ZERO(1),
	
	/** Indicates three individual samples per pixel: red, green, and blue, in that order. */
	RGB(2),
	
	/** Indicates one sample per pixel: indices into a color lookup table. */
	PALETTE(3),
	
	/** Indicates one sample per pixel defining a mask for a different IFD in this file: 1=opaque, 0=transparent */
	MASK(4),
	
	/** Indicates separated color, usually 4 samples: Cyan, Magenta, Yellow, Black, in that order. */
	SEPARATION(5),
	
	/** Indicates YCbCr perceptual color, usually 3 samples, but 1 sample indicates luma (Y) only */
	YCBCR(6),
	
	/** Indicates CIE L*a*b* perceptual color, usually 3 samples, but 1 sample indicates Luminance (L*) only */
	CIELAB(8),
	
	/** Indicates the alternate CIELAB encoding from ICC, using unsigned values for all elements. (standard L*a*b* has unsigned L* but signed a* and b*) */
	ICCLAB(9),
	
	/** Indicates the alternate CIELAB encoding from ITU, including a color gamut */
	ITULAB(10),
	
	/** Indicates SGI High Dynamic Range data, but only luminance (grayscale) interpreted like L-only from CIELuv. */
	LOGL(32844),
	
	/** Indicates SGI High Dynamic Range data, as compressed, log-scaled CIELuv. */
	LOGLUV(32845),
	
	COLOR_FILTER_ARRAY(32803),
	LINEAR_RAW(34892),
	DEPTH(51177),
	UNKNOWN(-1);
	
	private final int value;
	
	private TiffColorType(int value) {
		this.value = value;
	}
	
	public static TiffColorType of(int value) {
		for(TiffColorType t : values()) {
			if (t.value==value) return t;
		}
		
		return UNKNOWN;
	}
}
