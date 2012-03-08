package tacchi.particlegame;


import java.util.*;

import processing.core.*;
import tacchi.particlegame.animation.*;

public class Emitter implements StageLoadable {
	public enum State {
		Creating,
		Active,
		Destroying,
		Destroyed
	}
	
	protected static Animation baseCreate;
	protected static Animation baseDestroy;
	
	protected Animation create;
	protected Animation destroy;
	protected Animation currentAnim;
	
	/**
	 * maximum particles per frame.  Avoid giant bursts if game freezes
	 */
	private static final int MAX_PARTICLES = 30;
	
	
	private Random rand;
	
	public PVector pos = new PVector();
	public float radius = 15;
	public ParticleColor color = ParticleColor.White;
	public State state;

	/**
	 * direction particles are emitted
	 */
	float direction = 0;
	/**
	 * emit direction = direction +/- (0-variance) degrees
	 */
	float variance = 0.0f;
	
	/**
	 * particles to emit per second
	 */
	int rate = 30;
	/**
	 * velocity in pixels per second
	 */
	float velocity = 12.0f;
	
	float timeTillEmit = 0;
	
	
	protected float drawRadius = 0;
	protected float drawRadius2 = 0;
	protected float drawAlpha = 0;
	
	public Emitter(PVector pos, float direction) {
		this.pos = pos;
		this.direction = direction;
		rand = new Random();
		
		loadResources();
		startCreate();
	}

	private void loadResources() {
		//Load animations if not already done
		if (baseCreate == null) {
			HashMap<String, Animation> anims = Animation.loadNamed("data/anims/emitter.anim");
			baseCreate = anims.get("created");
			baseDestroy = anims.get("destroyed");
		}
		
		create = baseCreate.clone();
		destroy = baseDestroy.clone();
	}
	
	public void startCreate() {
		state = State.Creating;
		currentAnim = create;
		destroy.stop();
		create.play(true);
	}
	
	public void startDestroy() {
		state = State.Destroying;
		currentAnim = destroy;
		create.stop();
		destroy.play(true);
	}
	
	public void startActive() {
		state = State.Active;
		currentAnim = null;
		create.stop();
		destroy.stop();
	}
	
	public void draw(PGraphics g) {

		if (currentAnim != null) {
			drawRadius = currentAnim.getValue("Radius");
			drawRadius2 = currentAnim.getValue("Radius2");
			drawAlpha = currentAnim.getValue("Alpha");
		}
		
		float r = radius * drawRadius;
		float r2 = radius * drawRadius2;
		
		g.pushStyle();
		g.pushMatrix();
		g.translate(pos.x, pos.y);
		
		g.noStroke();
		g.fill(color.r, color.g, color.b, drawAlpha * 0.15f);
		
		g.ellipse(0, 0, 1.25f * r, 1.25f * r);
		g.ellipse(0, 0, 1f * r2, 1f * r2);
		
		
		g.popStyle();
		g.popMatrix();
		
	}

	public void step(ParticleSystem system, float secondsElapsed) {
		
		if (currentAnim != null)
			currentAnim.step(secondsElapsed);
		
		switch (state) {
		case Destroying:
			if (destroy.isFinished())
				state = State.Destroyed;
			
			if (destroy.currentTime > 2.0)
				break;
			
		case Active: 
			timeTillEmit -= secondsElapsed;
			
			int i = 0;
			while (timeTillEmit <= 0 && i < MAX_PARTICLES) {
				i++;
				timeTillEmit += 1f / (float)rate;
				//Get emit angle
				float angle = PApplet.radians(direction + variance * (float)rand.nextGaussian());
				//Get emit position relative to center of emitter
				PVector p = new PVector(radius * (float)rand.nextGaussian(), radius * (float)rand.nextGaussian());
				p.limit(radius);
				//Get particle velocity
				PVector v = new PVector(PApplet.cos(angle) * velocity, -PApplet.sin(angle) * velocity);
				//Make the particle
				system.add(new Particle(PVector.add(p, pos), v, color));
			}
			break;
		case Creating:
			if (create.isFinished())
				startActive();
			break;
		
		}
	}

	public void destroy() {
		startDestroy();
	}
	
	public boolean isDestroyed() {
		return state == State.Destroyed;
	}
	
	@Override
	public void parseFileLine(Utils.ParseInfo info) {
		if (info.symbol.equals("color")) {
			color = ParticleColor.getColor(info.args[0]);
		}
		else if (info.symbol.equals("direction")) {
			direction = Float.parseFloat(info.args[0]);
			if (info.args.length >= 2) {
				this.variance = Float.parseFloat(info.args[1]);
			}
		}
		else if (info.symbol.equals("rate")) {
			rate = Integer.parseInt(info.args[0]);
		}
		else if (info.symbol.equals("velocity")) {
			velocity = Float.parseFloat(info.args[0]);
		}
		else if (info.symbol.equals("radius")) {
			radius = Float.parseFloat(info.args[0]);
		}
	}
}
