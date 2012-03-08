package advanced.lightSequencer;

import java.util.Vector;

import org.mt4j.components.MTComponent;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class Light extends MTComponent {
	public static int HEIGHT = 40;
	
	//location on screen
	private int x;
	private int y;
	
	//colors
	private int red;
	private int green;
	private int blue;
	
	//length of light
	private int width;
	
	//light controlling
	private int light;
	
	PFont font;
	PApplet applet;
	
	public Light(PApplet pApplet) {
		super(pApplet);
		x = 0;
		y = 0;
		width = HEIGHT;
		applet = pApplet;
		
		font = applet.loadFont("GillSans-Bold-10.vlw");
		applet.textFont(font, 10);
		applet.textAlign(applet.CENTER);
	}
	
	public Light(PApplet pApplet,int x,int y,int light){
		super(pApplet);
		this.x = x;
		this.y = y;
		width = HEIGHT;
		this.light = light;
		
		applet = pApplet;
		
		font = applet.loadFont("GillSans-Bold-48.vlw");
		applet.textFont(font, 20);
		applet.textAlign(applet.CENTER);
	}
	
	public void move(int x,int y,PGraphics g){
		this.x += x;
		this.y += y;
	}
	
	public void resize(int width){
		this.width = width;
	}
	
	public boolean on(Vector3D v){
		return this.contained((int)v.x) && v.y >= this.y && v.y <=this.y+HEIGHT;
		
	}
	
	public boolean contained(int x){
		return x>=this.x && x<=(this.x+this.width);
	}
	
	@Override
	public void drawComponent(PGraphics g) {
		g.colorMode(g.HSB, applet.height);
		int color = g.color(y, applet.height, applet.height);
		red = (g.color(color) >> 16) & 0xFF;
		blue = (g.color(color) >> 8) & 0xFF;
		green = g.color(color) & 0xFF;
		g.colorMode(g.RGB, 255);
		g.fill(red, green, blue, 150);
	    g.stroke(0);
	    g.strokeWeight(3);
		g.ellipse(x+width/2,y+HEIGHT/2,width,HEIGHT);
	    g.fill(255);
	    g.text(""+light, x+width/2, y+2*width/3);
		//g.ellipseMode(CENTER);
		
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

}
