package advanced.lightSequencer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Timer;

import processing.core.PApplet;

import de.sciss.net.OSCClient;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCPacket;
class LightManager extends Thread{
	OSCClient osc;
	PApplet applet;
	PlayHead p;
	ArrayList<Light> lightArray;
	int lights;

	public LightManager(PApplet applet, PlayHead p, ArrayList<Light> lightArray, int lights) {
		this.applet = applet;
		this.p = p;
		this.lightArray = lightArray;
		this.lights = lights;

		try {
			osc = OSCClient.newUsing(OSCClient.UDP);
			osc.setTarget( new InetSocketAddress( "127.0.0.1", 11661 ));
			osc.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		long last = 0;
		while(true) {
			if(System.currentTimeMillis() - last > 10) {
				Object[] data = new Object[lights*3]; 
				p.step();
				for(int i = 0; i < lightArray.size(); i++) {
					Light temp = lightArray.get(i);
					if(!temp.inDock() && temp.distance(p.position) > 0)
					{
					data[3*temp.getNum()] = new Integer((int) (temp.red()*(temp.distance(p.position)/100)));
					data[3*temp.getNum()+1] = new Integer((int) (temp.green()*(temp.distance(p.position)/100)));
					data[3*temp.getNum()+2] = new Integer((int)(temp.blue()*(temp.distance(p.position)/100))) ;
					}
				}
				for(int i = 0; i < data.length; i++)
				{
					if(data[i] == null) data[i] = new Integer(0);
				}
				OSCPacket p = new OSCMessage("/setcolors", data);
				try {
					osc.send(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
				last = System.currentTimeMillis();
			}
		}
	}
	
}
