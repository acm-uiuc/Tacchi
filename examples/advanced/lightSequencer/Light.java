package advanced.lightSequencer;

import java.util.Vector;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class Light extends MTRectangle implements IGestureEventListener {
	public static int HEIGHT = 40;
	
	//colors
	private int red;
	private int green;
	private int blue;
	
	//light controlling
	private int light;
	
	PFont font;
	PApplet applet;
	
	public Light(PApplet pApplet,int x,int y,int light){
		super(x,y,//upperleft
				HEIGHT,//width
				HEIGHT,//height
				pApplet);
		this.setAnchor(PositionAnchor.UPPER_LEFT);

		this.light = light;
		
		applet = pApplet;
		
		font = applet.loadFont("GillSans-Bold-48.vlw");
		applet.textFont(font, 20);
		applet.textAlign(applet.CENTER);
	}
	
	public void resize(int width){
		this.setWidthLocal(width);
	}
	
	public boolean contained(int x){
		return true;//x>=a. && x<=(this.x+this.width);
	}
	
	@Override
	public void drawComponent(PGraphics g) {
	    Vector3D c = this.getCenterPointLocal();
		g.colorMode(g.HSB, applet.height);
		int color = g.color(c.y, applet.height, applet.height);
		red = (g.color(color) >> 16) & 0xFF;
		blue = (g.color(color) >> 8) & 0xFF;
		green = g.color(color) & 0xFF;
		g.colorMode(g.RGB, 255);
		g.fill(red, green, blue, 150);
	    g.stroke(0);
	    g.strokeWeight(3);
	    float w = this.getWidthXY(TransformSpace.GLOBAL);
		//g.ellipseMode(CENTER);
		g.ellipse(c.x,c.y,w,HEIGHT);
		
	    g.fill(255);
	    g.text(""+light, c.x, c.y);
	}
	
	
	public int red(){
		return red;
	}
	
	public int blue(){
		return blue;
	}
	
	public int green(){
		return green;
	}
	
	public boolean processGestureEvent(MTGestureEvent ge) {
		System.out.println("gesture");
		System.out.println(ge);
		/*switch (te.getId()) {
		case MTGestureEvent.GESTURE_DETECTED:
			System.out.println("Gesture detected");
			break;
		case MTGestureEvent.GESTURE_UPDATED:
			break;
		case MTGestureEvent.GESTURE_ENDED:
			break;
		}*/
		return false;
	}

}
