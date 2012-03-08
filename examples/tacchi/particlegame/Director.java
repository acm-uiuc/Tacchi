package tacchi.particlegame;

import processing.core.*;

import java.util.*;

public class Director extends ParticleModifier {
	/**
	 * pixels per second^2
	 */
	public float accel = 1.0f;
	protected PVector vector = new PVector();
	
	public PVector getVector() {
		return vector;
	}

	public void setVector(PVector vector) {
		this.vector = vector;
		vector.normalize();
	}
	
	/**
	 * 
	 * @return
	 */
	public float getDirection() {
		return vector.heading2D();
	}
	
	public void setDirection(float direction) {
		float rad = PApplet.radians(direction);
		vector.set(PApplet.cos(rad), -PApplet.sin(rad), 0);
	}

	public Director(PVector pos, float direction) {
		super(pos);
		setDirection(direction);
		attachIcon();
	}
	
	public Director(PVector pos, float rad, float accel, PVector vector)
	{
		super(pos, rad);
		this.accel = accel;
		setVector(vector);
		attachIcon();
	}
	
	public Director(PVector pos, float rad, float accel, float heading)
	{
		super(pos, rad);
		this.accel = accel;
		setDirection(heading);
		attachIcon();
	}
	
	private void attachIcon() {
		DirectorIcon icon = new DirectorIcon(this);
		renderer.addExtender(icon);
	}
	
	public void step(Collection<Particle> particles, float secondsElapsed)
	{
		super.step(particles, secondsElapsed);
		
		for (Particle p : particles)
		{
			float d2 = d2(p);
			if (d2 <= r2)	//only affect if inside radius
			{
				float s = (r2 - d2) / r2;
				if (s > 0.01)	//don't divide by zero
				{
					p.vel.add(PVector.mult(vector, s * accel * getPower() * secondsElapsed));
				}
			}
		}
	}

	@Override
	public void draw(PGraphics g) {
		super.draw(g);
	}
	
	@Override
	public void parseFileLine(Utils.ParseInfo info) {
		if (info.symbol.equals("direction")) {
			setDirection(Float.parseFloat(info.args[0]));
		}
		else if (info.symbol.equals("accel")) {
			accel = Float.parseFloat(info.args[0]);
		}
		else
			super.parseFileLine(info);
	}
}
