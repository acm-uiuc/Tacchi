package tacchi.particlegame;

import processing.core.*;

public final class DrawUtils {
	private static int RESOLUTION = 180;
	private static float[] cos;
	private static float[] sin;
	
	static {
		cos = new float[RESOLUTION + 1];
		sin = new float[RESOLUTION + 1];
		
		for (int i = 0; i < RESOLUTION + 1; i++) {
			float r = i * (PConstants.TWO_PI / RESOLUTION);
			cos[i] = PApplet.cos(r);
			sin[i] = PApplet.sin(r);
		}
	}
	
	
	public static void torus(PGraphics g, float x, float y, float inside, float outside) {
		if (g.fill) { 
			g.pushStyle();
			g.noStroke();
			g.beginShape(PConstants.TRIANGLE_STRIP);
			for (int i = 0; i < RESOLUTION + 1; i++) {
				g.vertex(x + inside * cos[i], y + inside * -sin[i]);
				g.vertex(x + outside * cos[i], y + outside * -sin[i]);
			}
			g.endShape();
			g.popStyle();
		}
		if (g.stroke) { 
			g.pushStyle();
			g.noFill();
			g.beginShape();
			for (int i = 0; i < RESOLUTION; i++) 
				g.vertex(x + inside * cos[i], y + inside * -sin[i]);
			g.endShape(PConstants.CLOSE);
			g.beginShape();
			for (int i = 0; i < RESOLUTION; i++) 
				g.vertex(x + outside * cos[i], y + outside * -sin[i]);
			g.endShape(PConstants.CLOSE);
			g.popStyle();
		}
	}
	
}
