package tacchi.particlegame.animation;

import processing.core.*;
import tacchi.particlegame.ParticleColor;

import java.awt.*;
import java.util.ArrayList;

/**
 * Draws a graph of the values in an animation
 * @author Joel
 *
 */
public class AnimationGraph {
	public Rectangle bounds;
	public float min = 0;
	public float max = 1;
	public int samples = 100;
	public float nodeSize = 2;
	public float tickSpacing = 1;
	
	public ArrayList<Float> lines = new ArrayList<Float>();
	
	private Animation anim;
	private float[][] values;
	private float[][] keyValues;
	
	private static final ParticleColor[] lineColors = {
		ParticleColor.White,
		ParticleColor.Red,
		ParticleColor.Orange,
		ParticleColor.Yellow,
		ParticleColor.Green,
		ParticleColor.Cyan,
		ParticleColor.Blue,
		ParticleColor.Purple,
		ParticleColor.Pink,
	};
	
	public Animation getAnim() {
		return anim;
	}
	public void setAnim(Animation anim) {
		this.anim = anim;
		recalculate();
	}
	
	public AnimationGraph(Animation anim, Rectangle bounds) {
		lines.add(0f);
		setAnim(anim);
		this.bounds = bounds;
	}

	public void recalculate() {
		values = new float[samples][anim.getValueNumber()];
		
		for (int i = 0; i < samples; i++) {
			float time = anim.length / samples * i;
			values[i] = anim.getValues(time);
		}
		
		int frames = anim.getFrameNumber();
		keyValues = new float[frames][anim.getValueNumber()];
		
		for (int i = 0; i < frames; i++) {
			keyValues[i] = anim.getValues(anim.getFrame(i).time);
		}
	}


	public void draw(PGraphics g) {
		g.pushMatrix();
		g.pushStyle();
		
		g.stroke(1,1,1,1);
		g.noFill();
		g.strokeWeight(1);
		
		//Draw outer bounds
		
		g.rectMode(PConstants.CORNER);
		g.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		
		
		//Draw tick marks
		for (float x = tickSpacing; x < anim.length; x += tickSpacing) {
			float xx = bounds.x + (x * bounds.width / anim.length);
			g.line(xx, bounds.y + bounds.height - 2, xx, bounds.y + bounds.height + 2);
		}
		
		//Transform (0,min anim.length,max) to (bounds.x,bounds.y, bounds.x+width, bounds.y+height)
		float xscale = bounds.width / anim.length;
		float yscale = bounds.height / (min - max);
		g.translate(bounds.x, bounds.y + bounds.height);
		g.scale(xscale, yscale);
		
		g.stroke(1,1,1,0.5f);
		//draw horizontal lines
		for (Float f : lines) {
			if (f > min && f < max)
				g.line(0, f, anim.length, f);
		}
		
		//g.noStroke();
		//g.fill(1,0,0,0.5f);
		//g.rect(0, min, anim.length, max);
		
		g.stroke(1,1,1,1);
		g.noFill();
		g.rectMode(PConstants.RADIUS);
		
		if (values.length == 0)
			return;
		int valNum = values[0].length;
		int frames = anim.getFrameNumber();
		
		for (int v = 0; v < valNum; v++) {
			ParticleColor c = AnimationGraph.lineColors[v % AnimationGraph.lineColors.length];
			g.stroke(c.r, c.g, c.b, c.a);
			
			for (int s = 0; s < samples - 1; s++) {
				float x = anim.length / samples * s;
				float y = values[s][v];
				float nx = anim.length / samples * (s+1);
				float ny = values[s+1][v];
				
				// draw line connecting lastx,lasty to x,y
				g.line(x, y, nx, ny);

			}
			
			for (int k = 0; k < frames; k++) {
				float x = anim.getFrame(k).time;
				float y = keyValues[k][v];
				
				// draw node at x,y
				g.rect(x, y, nodeSize / xscale, nodeSize / yscale);
			}
		}
		
		g.popMatrix();
		g.popStyle();
		
	}
}
