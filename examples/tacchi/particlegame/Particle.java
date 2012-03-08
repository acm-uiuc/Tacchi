package tacchi.particlegame;
import java.util.Arrays;

import processing.core.*;

public class Particle {
	private static final int TRAIL_SIZE = 3;
	private static final float TRAIL_FADE = 0.5f;
	
	public PVector pos;
	public PVector vel;
	public ParticleColor color;
	
	private float[] prevx = new float[TRAIL_SIZE];
	private float[] prevy = new float[TRAIL_SIZE];
	private int steps = 0;
	
	public Particle(PVector pos, PVector vel)
	{
		this.pos = new PVector(pos.x, pos.y);
		this.vel = new PVector(vel.x, vel.y);
		this.color = ParticleColor.White;
	}
	
	public Particle(PVector pos, PVector vel, ParticleColor color) {
		this(pos, vel);
		this.color = color;
	}
	
	public void step(ParticleSystem sys, float secondsElapsed)
	{
		for (int i = TRAIL_SIZE - 1; i > 0; i--) {
			prevx[i] = prevx[i - 1];
			prevy[i] = prevy[i - 1];
		}
		prevx[0] = pos.x;
		prevy[0] = pos.y;
		steps++;
		
		pos.add(vel);
		vel.mult(sys.settings.dampening);
		
		if (vel.mag() <= 0.01f)
			sys.remove(this);
		
		if (pos.x < sys.bounds.getMinX() - sys.boundsMargin || pos.x > sys.bounds.getMaxX() + sys.boundsMargin ||
			pos.y < sys.bounds.getMinY() - sys.boundsMargin || pos.y > sys.bounds.getMaxY() + sys.boundsMargin)
			sys.remove(this);
	}
	
	public void draw(PGraphics g, ParticleSettings settings)
	{
		g.strokeWeight(3);
		float alpha = color.a * getAlpha();
		
		g.stroke(color.r, color.g, color.b, alpha / 2);
		g.fill(color.r, color.g, color.b, alpha);
		g.rect(pos.x, pos.y, settings.radius / 2, settings.radius / 2);
		
		/*for (int i = 0; i < TRAIL_SIZE && i < steps; i++) {
			alpha = TRAIL_FADE * alpha;
			g.stroke(color.r, color.g, color.b, alpha / 2);
			g.fill(color.r, color.g, color.b, alpha);
			g.rect(prevx[i], prevy[i], settings.radius / 2, settings.radius / 2);
		}*/

	}
	
	public float getAlpha()
	{
		return Math.min(1, vel.mag() / 10);
	}
	
	public float getDistance(PVector pt)
	{
		return pos.dist(pt);
	}
}
