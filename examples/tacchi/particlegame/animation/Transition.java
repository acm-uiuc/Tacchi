package tacchi.particlegame.animation;

/**
 * Defines the way values in an animation goes from one keyframe to the next.<br>
 * getValue(x) should be a function that starts at (0,0) and goes to (1,1) any way it wants.
 * @author Joel
 *
 */
public interface Transition {
	/*
	 * if the object has a CurveType, the getValue functions should look like this:
	 * ITransition.getValue(x) {
	 * 		x = type.x(x);
	 * 		float y = (some function that starts at (0,0) and ends at (1,1))
	 * 		return type.y(y);
	 * }
	 */
	
	/**
	 * returns a value from 0 to 1 indicating the amount of change
	 * from the previous keyframe value to the next
	 * @param x a value from 0 to 1 indicating the position between the previous keyframe and the next
	 * @return a value from 0 to 1 where 0 corresponds to the previous keyframe value and 1
	 * 		corresponds to the next keyframe value.
	 */
	public float getValue(float x);
}
