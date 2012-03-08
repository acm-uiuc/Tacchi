package tacchi.particlegame;

public enum ParticleColor {
	White (1, 1, 1, 1),
	Red (1, 0, 0, 1),
	Green (0, 1, 0, 1),
	Blue (0, 0, 1, 1),
	
	Yellow (1, 1, 0, 1),
	Cyan (0, 1, 1, 1),
	Purple (1, 0, 1, 1),
	
	Pink(1, 0.6f, 0.85f, 1),
	Orange (1, 0.8f, 0, 1);
	
	
	public final float r;
	public final float g;
	public final float b;
	public final float a;
	
	ParticleColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public static ParticleColor getColor(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		return valueOf(name);
	}
}
