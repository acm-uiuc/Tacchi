package tacchi.particlegame;

import java.awt.Rectangle;
import java.util.*;

import processing.core.*;

public class ParticleSystem {
	private static final int PARTICLE_INIT_SIZE = 180;
	
	ArrayList<Particle> particles;
	ArrayList<ParticleModifier> modifiers;
	ArrayList<Emitter> emitters;
	ParticleSettings settings;
	
	PVector origin;
	
	private Stack<Particle> removing;
	private Stack<Particle> adding;
	
	Rectangle bounds;
	int boundsMargin = 100;
	
	public ParticleSystem(Rectangle bounds)
	{
		this.bounds = bounds;
		origin = new PVector(0, 0);
		particles = new ArrayList<Particle>(PARTICLE_INIT_SIZE);
		modifiers = new ArrayList<ParticleModifier>();
		emitters = new ArrayList<Emitter>();
		settings = new ParticleSettings();
		
		removing = new Stack<Particle>();
		adding = new Stack<Particle>();
	}
	
	public void step(float secondsElapsed)
	{
		// Emit particles
		for (Emitter em : emitters)
			em.step(this, secondsElapsed);
		// Add new particles
		while (!adding.isEmpty())
			particles.add(adding.pop());
		
		// update particle positions
		for (Particle p : particles)
			p.step(this, secondsElapsed);
		// apply modifiers
		for (ParticleModifier mod : modifiers)
			mod.step(particles, secondsElapsed);
		
		// Remove dead particles
		while (!removing.isEmpty())
			particles.remove(removing.pop());
		
		
	}
	
	public void draw(PGraphics g)
	{
		g.pushMatrix();
		g.pushStyle();
		g.translate(origin.x, origin.y);
		
		g.noStroke();
		
		// draw emitters
		for (Emitter em : emitters)
			em.draw(g);
		// draw modifiers
		for (ParticleModifier mod : modifiers)
			mod.draw(g);
		// draw particles
		for (Particle p : particles)
			p.draw(g, settings);
		
		g.popMatrix();
		g.popStyle();
	}
	
	public void add(Particle p)
	{
		adding.push(p);
	}
	
	public void addAll(Collection<Particle> p)
	{
		adding.addAll(p);
	}
	
	public void remove(Particle p)
	{
		removing.push(p);
	}
	
	public void removeAll(Collection<Particle> p)
	{
		removing.addAll(p);
	}


	public void destroyAll() {
		for (Emitter em : emitters) {
			em.destroy();
		}
		for (ParticleModifier mod : modifiers) {
			mod.destroy();
		}
	}
	
	public boolean allDestroyed() {
		for (Emitter em : emitters) {
			if (!em.isDestroyed())
				return false;
		}
		for (ParticleModifier mod : modifiers) {
			if (!mod.isDestroyed())
				return false;
		}
		return true;
	}
}
