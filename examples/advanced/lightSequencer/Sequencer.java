package advanced.lightSequencer;

import java.util.ArrayList;

import org.mt4j.components.MTComponent;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Sequencer extends MTComponent{
	PlayHead p;
	LightManager lights;
	PApplet applet;
	
	public Sequencer(PApplet applet, int lights, ArrayList<Light> lightsArray) {
		super(applet);
		this.applet = applet;
		this.p = new PlayHead(applet);
		this.lights = new LightManager(applet, p, lightsArray, lights);
		this.lights.start();
	}
	
	public void drawComponent(PGraphics g) {
		g.fill(100);
		g.rect(0, applet.height-(Light.HEIGHT+20), applet.width, Light.HEIGHT+20);
	}
	

}
