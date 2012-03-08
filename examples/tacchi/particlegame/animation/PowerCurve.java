package tacchi.particlegame.animation;

public class PowerCurve implements Transition {
	
	public static void initialize() {
		Transitions.identifiers.put("P", new Transitions.TransitionArgs(
				PowerCurve.class, new Class<?>[] {float.class, CurveType.class}));
	}
	
	public float power = 2;
	public CurveType type = CurveType.Normal;
	
	
	public static Transition get() {
		return Transitions.get(PowerCurve.class);
	}
	
	public static Transition get(float power) {
		return Transitions.get(PowerCurve.class, power);
	}
	
	public static Transition get(CurveType type) {
		return Transitions.get(PowerCurve.class, type);
	}
	
	public static Transition get(float power, CurveType type) {
		return Transitions.get(PowerCurve.class, power, type);
	}
	
	public PowerCurve() {
		
	}
	
	public PowerCurve(float power) {
		this.power = power;
	}
	
	public PowerCurve(CurveType type) {
		this.type = type;
	}
	
	public PowerCurve(float power, CurveType type) {
		this.power = power;
		this.type = type;
	}
	
	public PowerCurve(int power) {
		this((float)power);
	}
	
	public PowerCurve(int power, CurveType type) {
		this((float)power, type);
	}
	
	@Override
	public float getValue(float x) {
		x = type.x(x);
		return type.y((float)Math.pow(x, power));
	}

}
