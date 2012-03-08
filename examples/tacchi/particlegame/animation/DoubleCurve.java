package tacchi.particlegame.animation;

/**
 * Smooth curve formed by placing two power curves end to end
 * @author Joel
 *
 */
public class DoubleCurve extends PowerCurve {
	
	public static void initialize() {
		Transitions.identifiers.put("D", new Transitions.TransitionArgs(
					DoubleCurve.class, new Class<?>[] {float.class, CurveType.class}));
	}
	
	public static Transition get() {
		return Transitions.get(DoubleCurve.class);
	}
	
	public static Transition get(float power) {
		return Transitions.get(DoubleCurve.class, power);
	}
	
	public static Transition get(CurveType type) {
		return Transitions.get(DoubleCurve.class, type);
	}
	
	public static Transition get(float power, CurveType type) {
		return Transitions.get(DoubleCurve.class, power, type);
	}
	
	
	public DoubleCurve() {

	}

	public DoubleCurve(float power) {
		super(power);
	}

	public DoubleCurve(CurveType type) {
		super(type);
	}

	public DoubleCurve(float power, CurveType type) {
		super(power, type);
	}

	public DoubleCurve(int power) {
		super(power);
	}

	public DoubleCurve(int power, CurveType type) {
		super(power, type);
	}

	@Override
	public float getValue(float x) {
		if (x < 0.5) 
			//return 1;
			return 0.5f * super.getValue(2 * x);
		else
			//return 0;
			return 1 - (0.5f * super.getValue(2 * (1 - x)));
	}
}
