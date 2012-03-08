package advanced.lightSequencer;

import org.mt4j.components.MTComponent;

import processing.core.PApplet;

public class Sequencer extends MTComponent{
	PlayHead p;
	LightManager lights;
	PApplet applet;
	
	public Sequencer(PApplet applet, int lights) {
		super(applet);
		this.applet = applet;
		this.lights = new LightManager(applet, p, lights);
	}
	

}
