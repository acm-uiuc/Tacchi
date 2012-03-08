package advanced.lightSequencer;

import java.awt.Color;
import java.util.Vector;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class Light extends MTRoundRectangle implements IGestureEventListener {
	public static int HEIGHT = 30;
	
	//colors
	private int red;
	private int green;
	private int blue;
	
	private boolean docked;
	
	//light controlling
	private int light;
	
	private LightSequencerScene parent;
	
	PFont font;
	PApplet applet;
	
	public Light(PApplet pApplet,int x,int y,int light,LightSequencerScene parent){
		super(x,y,//upperleft
				1,//z??
				HEIGHT,//width
				HEIGHT,//height
				HEIGHT/2,//the arc width
				HEIGHT/2,//the arc height
				pApplet);
		
		this.parent = parent;
		
		this.setPositionGlobal(this.parent.getLightLocation(light));

		this.light = light;
		
		applet = pApplet;
		
		font = applet.loadFont("GillSans-Bold-48.vlw");
		applet.textFont(font, 20);
		applet.textAlign(applet.CENTER);
		
		docked = true;
	}
	
	public float distance(int x){
	    Vector3D c = this.getCenterPointGlobal();
	    float w = this.getWidthXY(TransformSpace.GLOBAL);
	    float r = 100/  w;
		if (x > c.x - w/2 && x < c.x + w/2)
			return 100 - Math.abs((int)c.x - x)*r;
		else return 0;
	}
	
	@Override
	public void drawComponent(PGraphics g) {
	    Vector3D c = this.getCenterPointGlobal();
	    Vector3D d = this.getCenterPointLocal();
		g.colorMode(g.HSB, applet.height);
		int color = g.color(c.y, applet.height, applet.height);
		red = (g.color(color) >> 16) & 0xFF;
		green = (g.color(color) >> 8) & 0xFF;
		blue = g.color(color) & 0xFF;
		g.colorMode(g.RGB, 255);
	    this.setFillColor(new MTColor(red,green,blue,100));
	    this.setStrokeWeight(3);
	    this.setStrokeColor(new MTColor(red,green,blue,175));
	    
	    super.drawComponent(g);
	    g.fill(255);
	    g.text(""+light, d.x, d.y+5);
	}
	
	
	public boolean inDock() {
	    Vector3D c = this.getCenterPointGlobal();
		if (c.y > 9*applet.height/10)
			return true;
		else return false;
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
	
	public int getNum(){
		return light;
	}
	
	public boolean processGestureEvent(MTGestureEvent ge) {
		if(ge.getClass() == DragEvent.class){
			DragEvent de = (DragEvent)ge;
			Vector3D diff = de.getTranslationVect();
			
			Vector3D p = this.getCenterPointGlobal();
			p.x += diff.x;
			p.y += diff.y;
			
			this.setPositionGlobal(p);
			
			if(docked){
				if(!inDock()){
					parent.addNewLight(light);
					docked = false;
				}else if(ge.getId() == MTGestureEvent.GESTURE_ENDED){
					this.setPositionGlobal(this.parent.getLightLocation(light));
				}
			}else{
				if(inDock()){
					this.parent.removeLight(this);
				}
			}
		}else if(ge.getClass() == ScaleEvent.class){
			ScaleEvent se = (ScaleEvent)ge;
			float factor = se.getScaleFactorX();
			float w = this.getWidthXY(TransformSpace.GLOBAL);
			float newW = w*factor;
			if(newW < HEIGHT){
				newW = HEIGHT;
			}
			this.setSizeLocal(newW, HEIGHT);
		}
		return false;
	}

}
