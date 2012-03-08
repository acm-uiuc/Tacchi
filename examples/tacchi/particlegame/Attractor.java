package tacchi.particlegame;

import processing.core.*;
import java.util.*;

public class Attractor extends ParticleModifier {
	
	/**
	 * pixels per second^2
	 */
	public float accel = 1.0f;
	
	public Attractor(PVector pos) {
		super(pos);
		attachIcon();
	}
	
	public Attractor(PVector pos, float rad, float accel)
	{
		super(pos, rad);
		this.accel = accel;
		attachIcon();
	}
	
	private void attachIcon() {
		AttractorIcon icon = new AttractorIcon(this);
		renderer.addExtender(icon);
	}
	
	@Override
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
					PVector v = PVector.sub(pos, p.pos);
					v.normalize();
					v.mult(s * accel * getPower() * secondsElapsed);
					p.vel.add(v);
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
		if (info.symbol.equals("accel")) {
			accel = Float.parseFloat(info.args[0]);
		}
		else
			super.parseFileLine(info);
	}
	
}
