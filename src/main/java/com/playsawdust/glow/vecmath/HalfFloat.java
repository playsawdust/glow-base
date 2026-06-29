package com.playsawdust.glow.vecmath;

/**
 * Represents static operations that work on half-precision floats. It is not recommended to do math on half-precision
 * floats; this class is intended to enable shipping low-precision vectors between hardware, such as vec2s for texture
 * UVs.
 * 
 * <p>Thanks to x4u's stackoverflow answer at https://stackoverflow.com/a/6162687 for the halfToFloat and floatToHalf
 * methods.
 */
public class HalfFloat {
	/**
	 * Turns the half-precision float bits into a full-precision float
	 */
	public static float halfToFloat( int hbits ) {
		// ignores the higher 16 bits
		int mant = hbits & 0x03ff;            // 10 bits mantissa
		int exp =  hbits & 0x7c00;            // 5 bits exponent
		if( exp == 0x7c00 )                   // NaN/Inf
			exp = 0x3fc00;                    // -> NaN/Inf
		else if( exp != 0 ) {                 // normalized value
			exp += 0x1c000;                   // exp - 15 + 127
			if( mant == 0 && exp > 0x1c400 )  // smooth transition
				return Float.intBitsToFloat( ( hbits & 0x8000 ) << 16 | exp << 13 | 0x3ff );
		}
		else if( mant != 0 ) {                // && exp==0 -> subnormal
			exp = 0x1c400;                    // make it normal
			do {
				mant <<= 1;                   // mantissa * 2
				exp -= 0x400;                 // decrease exp by 1
			} while( ( mant & 0x400 ) == 0 ); // while not normal
			mant &= 0x3ff;                    // discard subnormal bit
		}                                     // else +/-0 -> +/-0
		return Float.intBitsToFloat(          // combine all parts
				( hbits & 0x8000 ) << 16      // sign  << ( 31 - 15 )
				| ( exp | mant ) << 13 );     // value << ( 23 - 10 )
	}
	
	/**
	 * Turns the full-precision float into half-precision float bits
	 */
	public static int floatToHalf( float fval ) {
		// returns all higher 16 bits as 0 for all results
		int fbits = Float.floatToIntBits( fval );
		int sign = fbits >>> 16 & 0x8000;          // sign only
		int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value
		
		if( val >= 0x47800000 ) {             // might be or become NaN/Inf
			// avoid Inf due to rounding
			if( ( fbits & 0x7fffffff ) >= 0x47800000 ) {
				// is or must become NaN/Inf
				if( val < 0x7f800000 )        // was value but too large
					return sign | 0x7c00;     // make it +/-Inf
				return sign | 0x7c00 |        // remains +/-Inf or NaN
						( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
			}
			return sign | 0x7bff;             // unrounded not quite Inf
		}
		if( val >= 0x38800000 )               // remains normalized value
			return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
		if( val < 0x33000000 )                // too small for subnormal
			return sign;                      // becomes +/-0
		val = ( fbits & 0x7fffffff ) >>> 23;  // tmp exp for subnormal calc
		return sign | ( ( fbits & 0x7fffff | 0x800000 ) // add subnormal bit
				+ ( 0x800000 >>> val - 102 )     // round depending on cut off
				>>> 126 - val );   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
	}

	/**
	 * Creates a packed int in LSB format
	 * @param vec The vector to convert to a packed, half-precision int
	 * @return The vector in half-precision
	 */
	public static int vecToHalfVec(Vector2d vec) {
		int x = floatToHalf((float) vec.x()) & 0xFFFF;
		int y = floatToHalf((float) vec.y()) & 0xFFFF;
		int xLo = x & 0xFF;
		int xHi = (x >> 8) & 0xFF;
		int yLo = y & 0xFF;
		int yHi = (y >> 8) & 0xFF;
		
		return
			(xLo << 24) |
			(xHi << 16) |
			(yLo << 8) |
			yHi;
	}
	
	/**
	 * Takes a packed half-precision vec2 and decodes it into a full-fat Vector2d
	 * @param vec The packed half-precision vec2 to convert
	 * @return A Vector2d representation of the data
	 */
	public static Vector2d vecfromHalfVec(int vec) {
		int xLo = (vec >> 24) & 0xFF;
		int xHi = (vec >> 16) & 0xFF;
		int yLo = (vec >> 8) & 0xFF;
		int yHi = (vec) & 0xFF;
		
		int x = (xHi << 8) | xLo;
		int y = (yHi << 8) | yLo;
		
		return new Vector2d(halfToFloat(x), halfToFloat(y));
	}

}
