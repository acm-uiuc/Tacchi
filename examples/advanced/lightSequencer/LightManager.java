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

	public LightManager(PApplet applet, PlayHead p, int lights) {
		this.applet = applet;
		this.p = p;

		try {
			osc = OSCClient.newUsing(OSCClient.UDP);
			osc.setTarget( new InetSocketAddress( "127.0.0.1", 11661 ));
			osc.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		
		while(true) {

		}
	}
	
}
