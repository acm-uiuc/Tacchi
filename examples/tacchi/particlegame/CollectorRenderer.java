package tacchi.particlegame;

import java.util.HashMap;

import processing.core.PGraphics;
import tacchi.particlegame.animation.*;

public class CollectorRenderer extends ObjectRenderer {

	protected static Animation baseFill;
	
	protected Animation fill;
	protected float drawFillGlow = 0;
	
	Collector collect;
	public CollectorRenderer(Collector object) {
		super(object);
		collect = (Collector)object;
	}

	@Override
	protected void loadResources() {
		if (baseFill == null) {
			HashMap<String, Animation> anims = Animation.loadNamed("data/anims/collector.anim");
			baseFill = anims.get("fill");
		}
		
		fill = baseFill.clone();
		super.loadResources();
	}
	
	@Override
	public void startCreate() {
		fill.stop();
		super.startCreate();
	}
	
	@Override
	public void startActive() {
		fill.play(true);
		super.startActive();
	}
	
	
	
	@Override
	public void step(float timeElapsed) {
		super.step(timeElapsed);
		
		boolean glow = collect.isFull && (state == State.Active || (state == State.Destroying && destroy.currentTime < 2));
		fill.speed = glow ? 1 : -1;
		fill.step(timeElapsed);
	}
	
	@Override
	protected void drawAbove(PGraphics g) {
		super.drawAbove(g);
		
		g.pushStyle();
		g.noStroke();
		g.fill(collect.getColor().r, collect.getColor().g, collect.getColor().b, this.drawStrokeAlpha);
		float r = object.radius * drawRadius * (collect.fill / collect.maxFill);
		g.ellipse(0, 0, r, r);
		g.popStyle();
	}
	
	protected void drawBelow(PGraphics g) {
		g.pushStyle();

		drawFillGlow = fill.getValue("Glow");
		if (drawFillGlow > 0) {
			g.tint(sr, sg, sb, drawFillGlow);
			g.imageMode(PGraphics.CENTER);
			float size = 3.65f * drawRadius * object.getRadius();
			g.image(glowImage, 0, 0, size, size);
		}
		g.popStyle();
	}
}
