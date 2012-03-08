package tacchi.particlegame;

import java.util.HashMap;

import processing.core.*;
import tacchi.particlegame.animation.*;

public class DirectorIcon extends RenderExtender {
	protected static final int ARROWS = 4;
	protected static Animation baseAnim;
	protected Animation anim;
	protected Director director;
	
	public Color fill = new Color(0.1f, 0.1f, 0.20f, 0.55f);
	
	protected float[] arrowAlpha = new float[ARROWS];
	
	
	public DirectorIcon(Director d) {
		director = d;
		loadResources();
		anim.play(true);
	}
	
	protected void loadResources() {
		//Load animations if not already done
		if (baseAnim == null) {
			HashMap<String, Animation> anims = Animation.loadNamed("data/anims/director.anim");
			baseAnim = anims.get("anim");
		}
		
		anim = baseAnim.clone();
	}
	
	
	@Override
	public void drawAbove(PGraphics g) {
		
		g.pushMatrix();
		g.pushStyle();
		
		g.rotate(director.getDirection());
		float w = 0.5f * radius;
		float h = 0.6f * radius;
		float indent = 0.18f * radius;
		
		
		
		for (int i = 0; i < ARROWS; i++) {
			float x = (1.0f * radius / ARROWS) * (i - ((ARROWS - 1) / 2f));
			drawArrow(g, x, 0, w, h, indent, arrowAlpha[i]);
		}
		
		g.popMatrix();
		g.popStyle();
	}

	@Override
	public void step(float timeElapsed) {
		anim.step(timeElapsed);
		for (int i = 0; i < ARROWS; i++) {
			arrowAlpha[i] = anim.getValue(i);
		}
	}

	private void drawArrow(PGraphics g, float x, float y, float width, float height, float indent, float alpha) {
		//g.stroke(stroke.r, stroke.g, stroke.b, stroke.a * this.alpha * alpha);
		g.fill(fill.r, fill.g, fill.b, fill.a * this.alpha * alpha);
		//g.strokeWeight(2);
		g.noStroke();
		
		float h2 = height / 2;
		float w2 = width / 2;
		float front = x + w2;
		float back = x - w2;
		
		g.beginShape();
		g.vertex(front, y);
		g.vertex(front - indent, y - h2);
		g.vertex(back, y - h2);
		g.vertex(back + indent, y);
		g.vertex(back, y + h2);
		g.vertex(front - indent, y + h2);
		g.endShape(PConstants.CLOSE);
	}
}
