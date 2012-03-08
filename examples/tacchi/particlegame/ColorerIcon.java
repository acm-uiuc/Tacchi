package tacchi.particlegame;

import processing.core.*;

public class ColorerIcon extends RenderExtender {
	
	protected Colorer colorer;
	
	protected float barAlpha = 0.6f;
	protected float coloredAlpha = 1.0f;
	protected float whiteAlpha = 0.8f;
	
	public ColorerIcon(Colorer c) {
		colorer = c;
	}
	
	
	@Override
	public void drawAbove(PGraphics g) {
		
		g.pushStyle();
		
		float r = 0.10f * radius;
		float sep = 0.085f * radius;
		float sep2 = 0.3f * radius;
		float h = 0.5f * radius;
		
		ParticleColor c = colorer.getColor();
		
		// Draw a white sphere smeared out to the left
		g.noStroke();
		if (colorer.getColor() == ParticleColor.White)
			g.fill(1, 0, 0, whiteAlpha * 0.1f);
		else
			g.fill(1, 1, 1, whiteAlpha * 0.1f);
		for (float f = 0.1f; f <= 1f; f += 0.1f) {
			float r2 = PApplet.sqrt(f);
			float s = 0.6f * r * f;
			g.ellipse(-sep - s, 0, r * r2 + s, r * r2);
		}
		
		// Draw a colored divider line with some perspective
		g.strokeWeight(3);
		g.stroke(c.r, c.g, c.b, c.a * alpha * barAlpha / 4);
		for (int i = -2; i < 2; i++) {
			g.line(i, -h - 2 * i, i, h + 2 * i);
		}

		// Draw a colored sphere smeared out to the left
		g.noStroke();
		g.fill(c.r, c.g, c.b, c.a * alpha * coloredAlpha * 0.12f);
		for (float f = 0; f <= 1f; f += 0.1f) {
			float r2 = PApplet.sqrt(f);
			float s = 0.7f * r * f;
			g.ellipse(sep2 - s, 0, r * r2 + s, r * r2);
		}
		
		g.popStyle();
		
	}
}
