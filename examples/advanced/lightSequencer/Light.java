package advanced.lightSequencer;

import java.util.Vector;

import org.mt4j.components.MTComponent;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
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
	
	public Light(PApplet pApplet) {
		super(pApplet);
		x = 0;
		y = 0;
		width = HEIGHT;
	}
	
	public Light(PApplet pApplet,int x,int y){
		super(pApplet);
		this.x = x;
		this.y = y;
		width = HEIGHT;
	}
	
	public void move(int x,int y,PGraphics g){
		this.x = x;
		this.y = y;
		calcColors(g);
	}
	
	public void resize(int width){
		this.width = width;
	}
	
	public boolean on(Vector3D v){
		return this.contained((int)v.x) && v.y >= this.y-width/2 && v.y <=this.y+width/2;
		
	}
	
	public boolean contained(int x){
		return x>=this.x && x<=(this.x+this.width);
	}
	
	@Override
	public void drawComponent(PGraphics g) {
		g.fill(150,100,150,150);
	    g.stroke(0);
	    g.strokeWeight(3);
	    
		//g.ellipseMode(CENTER);
		g.ellipse(x+HEIGHT/2+width/2,y+HEIGHT/2,width,HEIGHT);
		
	}
	
	
	private void calcColors(PGraphics g){
		float v = 1;
		float Shsv = 1;
		
		
		int height = g.height;
		float hue = (float)x/height*360;
		float hueP = hue/60;
		
		float C = v*Shsv;
		float X = C*(1-Math.abs(hueP%2-1));
		
		int m = (int)(v - C);
		
		if(hueP >= 0 && hueP >1){
			red = (int) (C + m);
			green = (int) (X + m);
			blue = m;
		}else if(hueP < 2){
			red = (int) (X + m);
			green = (int) (C + m);
			blue = m;
		}else if(hueP < 3){
			red = m;
			green = (int) (C + m);
			blue = (int) (X + m);
		}else if(hueP < 4){
			red = m;
			green = (int) (X + m);
			blue = (int) (C + m);
		}else if(hueP < 5){
			red = (int) (X + m);
			green = 0;
			blue = (int) (C + m);
		}else if(hueP < 6){
			red = (int) (C + m);
			green = 0;
			blue = (int) (X + m);
		}else{
			red = m;
			green = m;
			blue = m;
		}
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
