package tacchi.particlegame.animation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;

import tacchi.particlegame.Utils;

/**
 * Enables animation of a list of values using keyframes and keyframe transitions.<br>
 * Also automates playback of one-time and looping animations.
 * @author Joel
 *
 */
public class Animation implements Cloneable{
	private static int lineNumber = 0;
	
	public String name = "";
	
	/**
	 * length of the animation in seconds
	 */
	public float length = 1;
	/**
	 * current playback position of the animation in seconds
	 */
	public float currentTime = 0;
	/**
	 * whether the animation should loop
	 */
	public boolean loop = false;
	/**
	 * whether the animation is playing
	 */
	public boolean playing = false;
	/**
	 * speed scale of animation playback
	 */
	public float speed = 1;
	
	public HashMap<String, Integer> namedValues = new HashMap<String, Integer>();
	
	private ArrayList<KeyFrame> frames = new ArrayList<KeyFrame>();
	
	private float[] cache = null;
	private boolean cacheOld = true;
	
	@Override
	public Animation clone() {
		Animation a = new Animation();
		a.name = name;
		a.length = length;
		a.currentTime = currentTime;
		a.loop = loop;
		a.playing = playing;
		a.speed = speed;
		a.namedValues = new HashMap<String, Integer>(namedValues);
		a.frames = new ArrayList<KeyFrame>(frames.size());
		for (KeyFrame f : frames)
			a.addFrame(f.clone());
		return a;
	}
	
	public Animation() {
		// default values already set
	}
	
	public Animation(float length) {
		this.length = length;
	}
	
	public Animation(float length, Collection<KeyFrame> frames) {
		this(length);
		for (KeyFrame f : frames)
			addFrame(f);
	}
	
	/**
	 * Inserts the frame in the animation so that all frames are in order
	 * @param f the frame to add
	 */
	public void addFrame(KeyFrame f) {
		for (int i = 0; i < frames.size(); i++) {
			if (f.time < frames.get(i).time) {
				frames.add(i, f);
				return;
			}
		}
		frames.add(f);
	}
	
	/**
	 * Removes a keyframe from the animation
	 * @param f
	 */
	public void removeFrame(KeyFrame f) {
		frames.remove(f);
	}
	
	/**
	 * Removes the keyframe at a specified position the keyframe list
	 * @param index
	 */
	public void removeFrame(int index) {
		removeFrame(index);
	}
	
	/**
	 * Gets the keyframe at the specified position in the keyframe list
	 * @param index
	 * @return
	 */
	public KeyFrame getFrame(int index) {
		return frames.get(index);
	}

	public int getFrameNumber() {
		return frames.size();
	}
	
	public int getValueNumber() {
		if (frames.size() == 0)
			return 0;
		return frames.get(0).values.length;
	}
	
	public void setValueNumber(int num) {
		for (KeyFrame frame : frames) {
			if (frame.values.length == num)
				continue;
			frame.values = Arrays.copyOf(frame.values, num);
		}
	}
	
	public int getIndex(String name) {
		Integer index = namedValues.get(name);
		if (index == null)
			return -1;
		return index;
	}
	
	
	/**
	 * Sorts the keyframes in ascending order by time.  Must be called after modifying keyframe times
	 */
	public void sort() {
		Collections.sort(frames);
	}
	
	/**
	 * Starts playing the animation from its previous position
	 */
	public void play() {
		playing = true;
	}
	
	/**
	 * Starts playing the animation
	 * @param rewind set to true to play from the beginning
	 */
	public void play(boolean rewind) {
		if (rewind)
			rewind();
		playing = true;
	}
	
	/**
	 * Pauses the animation
	 */
	public void stop() {
		playing = false;
	}
	
	/**
	 * Resets the animation to the beginning
	 */
	public void rewind() {
		currentTime = 0;
	}
	
	/**
	 * Updates the animation
	 * @param timeElapsed the time in seconds since the last call to step()
	 */
	public void step(float timeElapsed) {
		if (!playing)
			return;
		
		currentTime += timeElapsed * speed;
		//loop or bound currentTime
		if (loop) {
			if (currentTime > length || currentTime < 0)
				currentTime = ((currentTime % length) + length) % length;
		}
		else if (currentTime > length)
			currentTime = length;
		else if (currentTime < 0)
			currentTime = 0;
		
		cacheOld = true;
	}
	
	
	private void updateCache() {
		if (cacheOld) {
			cache = getValues(currentTime);
			cacheOld = false;
		}
	}
	
	/**
	 * gets the first interpolated value at the current time
	 * @return
	 */
	public float getValue() {
		return getValue(currentTime, 0);
	}
	
	/**
	 * Gets a specific interpolated value for the current time
	 * @param index the index of the value to get
	 * @return
	 */
	public float getValue(int index) {
		updateCache();
		return cache[index];
	}
	
	/**
	 * Gets the value with a specific name for the current time
	 * @param name
	 * @return
	 */
	public float getValue(String name) {
		Integer index = namedValues.get(name);
		if (index == null)
			return 0;
		return getValue(index);
	}
	
	/**
	 * gets the full list of interpolated values for the current time<br>
	 * Results are cached, so multiple calls will not recalculate
	 * @return
	 */
	public float[] getValues() {
		updateCache();
		return cache;
	}
	
	/**
	 * gets the first interpolated value at the given time
	 * @param time
	 * @return
	 */
	public float getValue(float time) {
		return getValue(time, 0);
	}
	
	/**
	 * gets a specific interpolated value for the given time
	 * @param time
	 * @param index the index of the value to get
	 * @return
	 */
	public float getValue(float time, int index) {
		if (frames.size() == 0)
			return 0;
			
		// Loop the time if looping enabled
		if (time < 0 || time > length) {
			if (loop) 
				time = ((time % length) + length) % length;
			else
				time = (time > length) ? length : ((time < 0) ? 0 : time);
		}
		
		// Find the keyframes directly before and after the time
		ListIterator<KeyFrame> iter = frames.listIterator();
		KeyFrame before = null;
		KeyFrame after = iter.next();
	
		while (time >= after.time) {
			if (time == after.time)
				return after.values[index];		/* exact time match */
			
			if (iter.hasNext()) 
				after = iter.next();
			else 
				return after.values[index];		/* time > last keyframe */
		}
		
		if (after != null) {
			iter.previous();
			if (iter.hasPrevious())
				before = iter.previous();
			else 
				return after.values[index];		/* time < first keyframe */
		}
		
		return before.getInterpolation(after, index, (time - before.time)/(after.time - before.time));
	}

	public float getValue(float time, String name) {
		Integer index = namedValues.get(name);
		if (index == null)
			return 0;
		return getValue(time, index);
	}
	
	/**
	 * gets the full list of interpolated values for the given time
	 * @param time
	 * @return
	 */
	public float[] getValues(float time) {
		if (frames.size() == 0)
			return new float[0];
			
		if (time < 0 || time > length) {
			if (loop) 
				time = ((time % length) + length) % length;
			else
				time = (time > length) ? length : ((time < 0) ? 0 : time);
		}
		
		ListIterator<KeyFrame> iter = frames.listIterator();
		KeyFrame before = null;
		KeyFrame after = iter.next();
	
		while (time >= after.time) {
			if (time == after.time)
				return after.values;		/* exact time match */
			
			if (iter.hasNext()) 
				after = iter.next();
			else 
				return after.values;		/* time > last keyframe */
		}
		
		if (after != null) {
			iter.previous();
			if (iter.hasPrevious())
				before = iter.previous();
			else 
				return after.values;		/* time < first keyframe */
		}
		
		float[] retVal = before.getInterpolations(after, (time - before.time)/(after.time - before.time));
		
		return retVal;
	}

	
	/**
	 * Gets whether the animation has completed (looping animations never finish)<br>
	 * If the animation speed is negative, an animation finishes when it hits the beginning
	 * @return true if animation is finished, false otherwise
	 */
	public boolean isFinished() {
		if (loop)
			return false;
		if (speed >= 0)
			return currentTime == length;
		else 
			return currentTime == 0;
	}

	
	
	private static void errorLine() {
		System.err.format("Line %1$2d: ", lineNumber);
	}
	
	public static ArrayList<Animation> load(String filename) {
		ArrayList<Animation> anims = new ArrayList<Animation>();
		Animation current = null;
		
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				lineNumber = reader.getLineNumber();

				Utils.ParseInfo info = Utils.parseStageLine(line);
				if (info == null)
					continue;
				
				if (info.symbol.equals("anim")) {
					// new anim
					current = new Animation();
					current.name = info.arglist;
					anims.add(current);
				}
				else if (current == null) {
					continue;
				}
				else if (info.symbol.equals("length")) {
					// anim length
					current.length = Float.parseFloat(info.args[0]);
				}
				else if (info.symbol.equals("loop")) {
					// loop
					current.loop = true;
				}
				else if (info.symbol.equals("")) {
					// value names
					for (int i = 0; i < info.args.length; i++) {
						current.namedValues.put(info.args[i], i);
					}
				}
				else {
					try {
						//If symbol is a number, new keyframe
						Float.parseFloat(info.symbol);
						KeyFrame frame = KeyFrame.loadFromFile(info);
						current.addFrame(frame);
					}
					catch (NumberFormatException e)	{ }
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("Could not find \"" + filename + "\"");
		}
		catch (IOException e) {
			errorLine();
			e.printStackTrace();
		}
		return anims;
	}

	public static HashMap<String, Animation> loadNamed(String filename) {
		ArrayList<Animation> anims = Animation.load(filename);
		HashMap<String, Animation> named = new HashMap<String, Animation>();
		for (Animation a : anims) {
			named.put(a.name, a);
		}
		return named;
	}
	
	
}
