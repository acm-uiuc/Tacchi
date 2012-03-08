package tacchi.particlegame.animation;

/**
 * Implements linear keyframe transitions.<br>
 * Use <code>Linear.get(Linear.class)</code> to get a singleton instance
 * @author Joel
 *
 */
public class Linear implements Transition {
	
	public static void initialize() {
		Transitions.identifiers.put("L", new Transitions.TransitionArgs(
				Linear.class, new Class<?>[0]));
	}
	
	public static Transition get() {
		return Transitions.get(Linear.class);
	}
	
	public Linear() {
		
	}
	
	@Override
	public float getValue(float x) {
		return x;
	}

}
