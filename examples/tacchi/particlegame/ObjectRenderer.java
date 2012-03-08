package tacchi.particlegame;

import tacchi.particlegame.animation.*;
import processing.core.*;

import java.util.*;


/**
 * Thing that will eventually render pretty animated circles with icons
 * @author Joel
 *
 */
public class ObjectRenderer {
	public enum State {
		Creating,
		Active,
		Destroying,
		Destroyed
	}
	
	private static final int NUM_HANDLES = 4;
	
	protected static Animation baseCreate;
	protected static Animation baseDestroy;
	protected static Animation baseSelect;
	protected static PImage baseGlowImage;
	
	protected Animation create;
	protected Animation destroy;
	protected Animation select;
	protected Animation currentAnim;
	
	public ArrayList<RenderExtender> extenders = new ArrayList<RenderExtender>();
	public ParticleModifier object;
	public State state;
	
	public float sr = 1;
	public float sg = 1;
	public float sb = 1;
	public float sa = 0.8f;
	public float fr = 0.2f;
	public float fg = 0.2f;
	public float fb = 0.5f;
	public float fa = 0.1f;
	public float ha = 0.4f;
	
	protected float drawRadius = 0;
	protected float drawStrokeAlpha = 0;
	protected float drawFillAlpha = 0;
	protected float drawHandleAlpha = 0;
	protected float drawExtenderAlpha = 1;
	protected float drawGlow = 0;
	protected PImage glowImage;
	
	public ObjectRenderer(ParticleModifier object) {
		this.object = object;
		loadResources();
		startCreate();
	}
	
	protected void loadResources() {
		//Load animations if not already done
		if (baseCreate == null) {
			HashMap<String, Animation> anims = Animation.loadNamed("data/anims/object-base.anim");
			baseCreate = anims.get("created");
			baseDestroy = anims.get("destroyed");
			baseSelect = anims.get("selected");
		}
		
		if (baseGlowImage == null) {
			baseGlowImage = Game.getCurrent().loadImage("data/images/glow.png");
		}
		
		create = baseCreate.clone();
		destroy = baseDestroy.clone();
		select = baseSelect.clone();
		glowImage = baseGlowImage;
	}
	
	public void addExtender(RenderExtender ex) {
		extenders.add(ex);
	}
	
	public void startCreate() {
		state = State.Creating;
		currentAnim = create;
		destroy.stop();
		select.stop();
		create.play(true);
	}
	
	public void startDestroy() {
		state = State.Destroying;
		currentAnim = destroy;
		create.stop();
		select.stop();
		destroy.play(true);
	}
	
	public void startActive() {
		state = State.Active;
		currentAnim = null;
		create.stop();
		destroy.stop();
		select.play(true);
	}
	
	public void step(float timeElapsed) {
		if (currentAnim != null)
			currentAnim.step(timeElapsed);
		
		switch (state) {
		case Active: 
			select.speed = object.state != ParticleModifier.ModifierState.None ? 1 : -1;
			select.step(timeElapsed);
			break;
		case Creating:
			if (create.isFinished())
				startActive();
			break;
		case Destroying:
			if (destroy.isFinished())
				state = State.Destroyed;
			break;
		}
		
		for (RenderExtender e : extenders) {
			e.step(timeElapsed);
		}
	}
	
	public void draw(PGraphics g) {

		switch (state) {
		case Active:
			drawGlow = select.getValue("Glow");
			drawHandleAlpha = select.getValue("HandleAlpha");
			break;
		case Destroyed:
			return;
		}
		
		if (currentAnim != null) {
			drawRadius = currentAnim.getValue("Radius");
			drawStrokeAlpha = sa * currentAnim.getValue("StrokeAlpha");
			drawFillAlpha = fa * currentAnim.getValue("FillAlpha");
			drawHandleAlpha = currentAnim.getValue("HandleAlpha");
		}

		
		float r = object.getRadius() * drawRadius;
		g.pushStyle();
		g.pushMatrix();
		
		g.translate(object.pos.x, object.pos.y);
		drawBelow(g);
		
		g.fill(fr, fg, fb, drawFillAlpha);
		g.stroke(sr, sg, sb, drawStrokeAlpha);
		g.strokeWeight(2);
		g.ellipse(0, 0, r, r);
		
		drawAbove(g);
		
		g.popStyle();
		g.popMatrix();
	}
	
	protected void drawBelow(PGraphics g) {
		g.pushStyle();

		if (drawGlow > 0) {
			g.tint(sr, sg, sb, drawGlow);
			g.imageMode(PGraphics.CENTER);
			float size = 3.65f * drawRadius * object.getRadius();
			g.image(glowImage, 0, 0, size, size);
		}
		
		for (RenderExtender e : extenders) {
			e.alpha = drawExtenderAlpha;
			e.radius = drawRadius * object.radius;
			e.drawBelow(g);
		}
		
		if (object.moveable) {
			g.pushMatrix();
			g.strokeWeight(2);
			g.stroke(sr, sg, sb, ha * drawHandleAlpha * 0.5f);
			g.fill(sr, sg, sb, ha * drawHandleAlpha);
			for (int i = 0; i < 6; i++) {
				g.triangle(-4, object.radius * drawRadius + 4, 
						4, object.radius * drawRadius + 4, 
						0, object.radius * drawRadius + 9);
				g.rotate(PConstants.TWO_PI / NUM_HANDLES);
			}
			g.popMatrix();
		}
		else if (object.sizeable) {
			g.pushMatrix();
			g.rotate(PConstants.PI / NUM_HANDLES);
			g.strokeWeight(1);
			g.stroke(0, 0, 0, drawHandleAlpha);
			g.fill(sr, sg, sb, ha * drawHandleAlpha);
			for (int i = 0; i < 6; i++) {
				g.beginShape(PConstants.TRIANGLE_STRIP);
				g.vertex(0, object.radius * drawRadius + 3);
				g.vertex(-4, object.radius * drawRadius + 7);
				g.vertex(4, object.radius * drawRadius + 7);
				g.vertex(0, object.radius * drawRadius + 11);
				g.endShape();
				g.rotate(PConstants.TWO_PI / NUM_HANDLES);
			}
			g.popMatrix();
		}
		
		g.popStyle();
	}
	
	protected void drawAbove(PGraphics g) {
		for (RenderExtender e : extenders)
			e.drawAbove(g);
	}
}
