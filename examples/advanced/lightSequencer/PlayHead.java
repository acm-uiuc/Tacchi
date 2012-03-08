package advanced.lightSequencer;

import org.mt4j.components.MTComponent;

import processing.core.PApplet;
import processing.core.PGraphics;

public class PlayHead extends MTComponent{
	public int position = 0;
	private PApplet applet;
	
	public PlayHead(PApplet applet) {
		super(applet);
		this.applet = applet;
	}
	
	public void step() {
		position++;
		if (position > applet.width) position = 0;
	}
	
	public void drawComponent(PGraphics g) {
		g.pushStyle();
	    g.fill(150,100,150,150);
	    g.stroke(255);
	    g.strokeWeight(3);
		g.line(position, 0, position, applet.height);
	}
	
	
}
