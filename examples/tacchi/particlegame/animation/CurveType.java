package tacchi.particlegame.animation;

/**
 * Enum with methods to transform a transition curve.
 * @author Joel
 *
 */
public enum CurveType {
	
	/**
	 * No change
	 */
	Normal { 
		float x(float x) { return x; }
		float y(float y) { return y; }
	},
	/**
	 * Reverses the curve (mirrors it over the line (0,0) to (1,1))
	 */
	Reversed {
		float x(float x) { return 1 - x; }
		float y(float y) { return 1 - y; }
	};
	
	/**
	 * Transformation that should be applied to the input to the curve function
	 * @param x the x parameter of the getValue() method
	 * @return the x value that should be used as input to the curve function
	 */
	abstract float x(float x);
	/**
	 * Transformation that should be applied to the output of the curve function
	 * @param y the output of the curve function
	 * @return the y value that should be returned by getValue() 
	 */
	abstract float y(float y);
}
