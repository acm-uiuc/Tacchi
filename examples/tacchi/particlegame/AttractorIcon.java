package tacchi.particlegame;

import java.util.Arrays;
import java.util.HashMap;

import processing.core.*;
import tacchi.particlegame.animation.*;

public class AttractorIcon extends RenderExtender {
	protected static final int RINGS = 4;
	protected static Animation baseAttract;
	protected static Animation baseRepulse;
	protected Animation attract;
	protected Animation repulse;
	protected Attractor attractor;
	
	public Color fill = new Color(0.1f, 0.1f, 0.20f, 0.55f);
	
	protected float[] ringAlpha = new float[RINGS];
	
	
	public AttractorIcon(Attractor a) {
		attractor = a;
		loadResources();
		attract.play(true);
		repulse.play(true);
	}
	
	protected void loadResources() {
		//Load animations if not already done
		if (baseAttract == null) {
			HashMap<String, Animation> anims = Animation.loadNamed("data/anims/attractor.anim");
			baseAttract = anims.get("attract");
			baseRepulse = anims.get("repulse");
		}
		
		attract = baseAttract.clone();
		repulse = baseRepulse.clone();
	}
	
	@Override
	public void drawAbove(PGraphics g) {
		
		g.pushMatrix();
		g.pushStyle();
		
		for (int i = 0; i < RINGS; i++) {
			float r = (1.0f * radius / RINGS) * (RINGS - i - 1);
			float w = radius / RINGS;
			
			g.noStroke();
			g.fill(fill.r, fill.g, fill.b, fill.a * alpha * ringAlpha[i]);
			DrawUtils.torus(g, 0, 0, r, r + w);
		}
		
		g.popMatrix();
		g.popStyle();
	}

	@Override
	public void step(float timeElapsed) {
		attract.step(timeElapsed);
		repulse.step(timeElapsed);
		if (attractor.accel == 0)
			Arrays.fill(ringAlpha, 0);
		else {
			Animation current = attractor.accel > 0 ? attract : repulse;
			for (int i = 0; i < RINGS; i++) 
				ringAlpha[i] = current.getValue(i);
		}
	}
}
