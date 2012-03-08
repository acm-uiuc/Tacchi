package tacchi.particlegame.animation;

import java.util.*;
import java.util.regex.*;

import tacchi.particlegame.Utils;

/**
 * Holds a list of values at a certain time in an animation.<br>
 * All keyframes in an animation are assumed to have the same number of values
 * when calling <code>getInterpolations()</code>
 * @author Joel
 *
 */
public class KeyFrame implements Comparable<KeyFrame>, Cloneable {
	private static final String TRANSITION_PATTERN = "([a-z]+?)((?:-?\\d+(?:\\.\\d+)?\\|?)+)?(r)?(\\*)?";
	
	/**
	 * The list of values at this keyframe
	 */
	public float[] values;
	
	/**
	 * The list of transitions to get from this keyframe to the next.
	 * Use an array of length 1 to use the same transition for all values,
	 * or an array with the same size as <code>value</code> to set a different 
	 * transition for each keyframe. <br>
	 * If <code>(trans.length != 1 && trans.length != value.length)</code>, you will get errors.
	 */
	public Transition[] trans;
	
	/**
	 * The time this keyframe appears in the animation in seconds.<br>
	 * If you change this after adding it to an animation, you must call <code>Animation.sort()</code>
	 */
	public float time = 0;
	
	
	@Override
	public KeyFrame clone() {
		KeyFrame f = new KeyFrame();
		f.values = values.clone();
		f.trans = trans.clone();
		f.time = time;
		return f;
	}
	
	/**
	 * Constructs a keyframe at time 0 with one value of 0 and a linear transition
	 */
	public KeyFrame() {
		values = new float[] {0};
		trans = new Transition[] { Transitions.get(Linear.class) };
	}
	
	/**
	 * Constructs a keyframe with one value and a linear transition
	 * @param time
	 * @param value
	 */
	public KeyFrame(float time, float value) {
		this(time, value, Transitions.get(Linear.class));
	}
	
	/**
	 * Constructs a keyframe with a list of values and linear transitions
	 * @param time
	 * @param values
	 */
	public KeyFrame(float time, float[] values) {
		this(time, values, Transitions.get(Linear.class));
	}
	
	/**
	 * Constructs a keyframe with a blank list of values of a particular size and linear transitions
	 * @param time
	 * @param numValues
	 */
	public KeyFrame(float time, int numValues) {
		this(time, new float[numValues]);
	}
	
	/**
	 * Constructs a keyframe with a blank list of values of a particular size and a specific transition
	 * @param time
	 * @param numValues
	 * @param trans
	 */
	public KeyFrame(float time, int numValues, Transition trans) {
		this(time, new float[numValues], trans);
	}
	
	/**
	 * Constructs a keyframe with one value and a specific transition
	 * @param time
	 * @param value
	 * @param trans
	 */
	public KeyFrame(float time, float value, Transition trans) {
		this.values = new float[] {value};
		this.time = time;
		this.trans = new Transition[] { trans };
	}
	
	/**
	 * Constructs a keyframe with a list of values and the same transition for each
	 * @param time
	 * @param values
	 * @param trans
	 */
	public KeyFrame(float time, float[] values, Transition trans) {
		this.values = values;
		this.time = time;
		this.trans = new Transition[] { trans };
	}
	
	/**
	 * Constructs a keyframe with a list of values and a specific transition for each
	 * @param time
	 * @param values
	 * @param trans
	 */
	public KeyFrame(float time, float[] values, Transition[] trans) {
		this.values = values;
		this.time = time;
		this.trans = trans;
		
		if (this.values.length != this.trans.length)
			throw new IllegalArgumentException("The list of values and transitions must be of equal length.");
	}
	
	/**
	 * Sets the transition for the specified value.
	 * @param index the index of the value to set a transition for
	 * @param trans
	 */
	public void setTransition(int index, Transition trans) {
		//If needed, expand trans[] to be same size as values[]
		if (this.trans.length == 1) {
			Transition temp = this.trans[0];
			this.trans = new Transition[values.length];
			Arrays.fill(this.trans, temp);
		}
		
		this.trans[index] = trans;
	}
	
	/**
	 * Sets the transition for all values to the same transition.
	 * @param trans
	 */
	public void setAllTransitions(Transition trans) {
		this.trans = new Transition[] {trans};
	}
	
	/**
	 * Gets the transition for a specified value
	 * @param index
	 * @return
	 */
	public Transition getTransition(int index) {
		if (trans.length == 1)
			return trans[0]; 
		return trans[index];
	}
	
	/**
	 * Interpolates between the values of this keyframe and another keyframe and returns the full list
	 * @param next the keyframe to interpolate with
	 * @param x the position between the two keyframes. 0 = this frame, 1 = other frame
	 * @return
	 */
	public float[] getInterpolations(KeyFrame next, float x) {
		float[] retVal = new float[values.length];
		if (trans.length == 1)
		{
			float change = trans[0].getValue(x);
			for (int i = 0; i < values.length; i++) 
				retVal[i] = (next.values[i] - values[i]) * change + values[i];
		}
		else
		{
			for (int i = 0; i < values.length; i++) 
				retVal[i] = (next.values[i] - values[i]) * trans[i].getValue(x) + values[i];
		}
		
		return retVal;
	}
	
	/**
	 * Interpolates between the first value of this keyframe and another keyframe
	 * @param next the keyframe to interpolate with
	 * @param x the position between the two keyframes. 0 = this frame, 1 = other frame
	 * @return
	 */
	public float getInterpolation(KeyFrame next, float x) {
		return getInterpolation(next, 0, x);
	}
	
	/**
	 * Interpolates between a specified value of this keyframe and another keyframe
	 * @param next the keyframe to interpolate with
	 * @param index the index of the value to interpolate
	 * @param x the position between the two keyframes. 0 = this frame, 1 = other frame
	 * @return
	 */
	public float getInterpolation(KeyFrame next, int index, float x) {
		return (next.values[index] - values[index]) * 
			(trans.length == 1 ? trans[0].getValue(x) : trans[index].getValue(x)) + values[index];
	}

	/**
	 * Compares two keyframes by their times
	 */
	@Override
	public int compareTo(KeyFrame arg0) {
		return (int)Math.signum(time - arg0.time);
	}
	
	protected static KeyFrame loadFromFile(Utils.ParseInfo info) {
		KeyFrame f = new KeyFrame(Float.parseFloat(info.symbol), info.args.length);
		Pattern p = Pattern.compile(TRANSITION_PATTERN, Pattern.CASE_INSENSITIVE);
		for (int i = 0; i < info.args.length; i++) {
			String[] parts = info.args[i].split(" ");
			if (parts.length == 0 || parts.length > 2)
				throw new IllegalArgumentException("Invalid value format in value " + i);
			
			f.values[i] = Float.parseFloat(parts[0]);
			
			if (parts.length == 2) {
				Matcher m = p.matcher(parts[1]);
				if (!m.matches())
					throw new IllegalArgumentException("Invalid transition format in value " + i);
				String identifier = m.group(1);
				String[] args = m.group(2) == null ? new String[0] : m.group(2).split("\\|");
				boolean reverse = m.group(3) != null;
				boolean all = m.group(4) != null;
				Transition trans = Transitions.getFromFile(identifier, args, reverse);
				if (trans != null) {
					if (all)
						f.setAllTransitions(trans);
					else
						f.setTransition(i, trans);
				}
			}
		}
		return f;
	}
}
