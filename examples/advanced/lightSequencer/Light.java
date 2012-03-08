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
import processing.core.PGraphics;

public class Light extends MTRectangle implements IGestureEventListener {
	public static int HEIGHT = 40;
	
	//colors
	private int red;
	private int green;
	private int blue;
	
	//light controlling
	private int light;

	
	public Light(PApplet pApplet,int x,int y,int light){
		super(x,y,//upperleft
				HEIGHT,//width
				HEIGHT,//height
				pApplet);
		this.setAnchor(PositionAnchor.UPPER_LEFT);

		this.light = light;
	}
	
	
	
	public void resize(int width){
		this.setWidthLocal(width);
	}
	
	public boolean contained(int x){
		return true;//x>=a. && x<=(this.x+this.width);
	}
	
	@Override
	public void drawComponent(PGraphics g) {
		g.fill(((float)light/24*255),100,150,150);
	    g.stroke(0);
	    g.strokeWeight(3);
	    
	    float w = this.getWidthXY(TransformSpace.GLOBAL);
	    Vector3D c = this.getCenterPointLocal();
		//g.ellipseMode(CENTER);
		g.ellipse(c.x,c.y,w,HEIGHT);
		
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
