package tacchi.particlegame;

import java.util.Collection;

import processing.core.*;


public class Collector extends ParticleModifier {
	
	/**
	 * fill rate in units per particle per second
	 */
	public float fillRate = 0.15f;
	/**
	 * unfill rate in units per second
	 */
	public float unfillRate = 1.0f;
	public float fill = 0f;
	public float maxFill = 8f;
	/**
	 * need this number of particles to fill
	 */
	public int minParticles = 4;
	
	
	public boolean isFull = false;
	/**
	 * If full, stays full for this amount of time
	 */
	public float stayFullTime = 0.5f;
	public float fullTime = 0;
	
	private ParticleColor color = ParticleColor.White;
	
	public ParticleColor getColor() {
		return color;
	}
	
	public void setColor(ParticleColor color) {
		this.color = color;
		renderer.fr = renderer.sr = color.r;
		renderer.fg = renderer.sg = color.g;
		renderer.fb = renderer.sb = color.b;
	}
	
	public Collector(PVector pos) {
		super(null, pos);
		moveable = false;
		sizeable = false;
		renderer = new CollectorRenderer(this);
	}
	
	public Collector(PVector pos, ParticleColor color) {
		this(pos);
		this.color = color;
	}
	
	public Collector(PVector pos, float radius) {
		super(null, pos, radius);
		moveable = false;
		sizeable = false;
		renderer = new CollectorRenderer(this);
	}
	
	public Collector(PVector pos, float radius, ParticleColor color) {
		this(pos, radius);
		this.color = color;
	}

	@Override
	public void draw(PGraphics g) {
		renderer.draw(g);
		/*
		g.pushStyle();
		g.pushMatrix();
		g.translate(pos.x, pos.y);
		
		g.noFill();
		g.strokeWeight(2);
		float a = isFull ? 1.0f : 0.6f;
		g.stroke(color.r, color.g, color.b, a);
		
		g.ellipse(0, 0, radius, radius);
		
		g.noStroke();
		g.fill(color.r, color.g, color.b, 0.8f * a);
		float r = radius * (fill / maxFill);
		g.ellipse(0, 0, r, r);
		
		g.popMatrix();
		g.popStyle();*/
	}

	@Override
	public void step(Collection<Particle> particles, float secondsElapsed) {
		super.step(particles, secondsElapsed);
		int inside = 0;
		for (Particle p : particles) {
			float d = p.pos.dist(pos);
			if (d <= radius && (color == ParticleColor.White || p.color == color)) {
				inside++;
			}
		}
		
		if (isFull) {
			// collector is full
			if (inside >= minParticles) {
				// collector has particles
				fullTime = stayFullTime;
			}
			else {
				// collector has no particles
				fullTime -= secondsElapsed;
				if (fullTime <= 0) {
					fullTime = 0;
					isFull = false;
				}
			}
		}
		else {
			// collector is not full
			if (inside > 0) {
				// collector has particles
				fill += fillRate * inside * secondsElapsed;
				if (inside >= minParticles)
				fullTime = stayFullTime;
			}
			else
				fullTime -= secondsElapsed;
			
			if (fullTime <= 0) {
				fill -= unfillRate * secondsElapsed;
			}
			else {
				fill -= unfillRate * secondsElapsed / 2;
			}
			
			
			// bound fill level
			if (fill < 0)
				fill = 0;
			if (fill >= maxFill) {
				fill = maxFill;
				isFull = true;
			}
		}
	}

}
