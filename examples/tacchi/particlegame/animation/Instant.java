package tacchi.particlegame.animation;

/**
 * Implements instant keyframe transitions.<br>
 * Use <code>Instant(Linear.class)</code> to get a singleton instance
 * @author Joel
 *
 */
public class Instant implements Transition {
	
	public static void initialize() {
		Transitions.identifiers.put("I", new Transitions.TransitionArgs(
				Instant.class, new Class<?>[] { CurveType.class }));
	}
	
	public static Transition get() {
		return Transitions.get(Instant.class);
	}
	
	
	public CurveType type = CurveType.Normal;
	
	public Instant() {
		
	}
	
	public Instant(CurveType type) {
		this.type = type;
	}
	
	@Override
	public float getValue(float x) {
		return type.y(1);
	}

}
