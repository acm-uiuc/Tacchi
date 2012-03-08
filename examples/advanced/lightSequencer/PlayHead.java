package advanced.lightSequencer;

import org.mt4j.components.MTComponent;

import processing.core.PApplet;
import processing.core.PGraphics;

public class PlayHead extends MTComponent{
	private int position = 0;
	private PApplet applet;
	
	public PlayHead(PApplet applet) {
		super(applet);
		this.applet = applet;
	}
	
	private void step() {
		position++;
		if (position > applet.width) position = 0;
	}
	
	public void draw(PGraphics g) {
	    g.fill(150,100,150,150);
	    g.stroke(0);
	    g.strokeWeight(3);
		g.line(position, 0, position, applet.height);
	}
	
	
	
}
