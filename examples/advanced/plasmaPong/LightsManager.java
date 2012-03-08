package advanced.plasmaPong;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import de.sciss.net.OSCClient;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCPacket;
import msafluid.MSAFluidSolver2D;

public class LightsManager implements Runnable {
	private MSAFluidSolver2D fluidSolver;
	private OSCClient osc;
	private float[] lightArray = new float[24*3];


	public LightsManager(MSAFluidSolver2D fluidSolver)
	{
		this.fluidSolver = fluidSolver;
		// create OSC client
		try {
			osc = OSCClient.newUsing(OSCClient.UDP);
			osc.setTarget( new InetSocketAddress( "127.0.0.1", 11661 ));
			osc.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(true)
		{
			int h = fluidSolver.getHeight();
			int w = fluidSolver.getWidth();
			int step = w / (lightArray.length/9);
			for(int i = 0; i < lightArray.length - 1; i+=9)
			{
				int x = i*step;
				if (fluidSolver.r != null && fluidSolver.g != null && fluidSolver.b != null)
				{
				lightArray[i]   = fluidSolver.r[(x+step/2)+(h/4)*w];
				lightArray[i+1] = fluidSolver.g[(x+step/2)+(h/4)*w];
				lightArray[i+2] = fluidSolver.b[(x+step/2)+(h/4)*w];
					
				lightArray[i+3] = fluidSolver.r[(x+step/2)+(2*h/4)*w];
				lightArray[i+4] = fluidSolver.g[(x+step/2)+(2*h/4)*w];
				lightArray[i+5] = fluidSolver.b[(x+step/2)+(2*h/4)*w];

				lightArray[i+6] = fluidSolver.r[(x+step/2)+(3*h/4)*w];
				lightArray[i+7] = fluidSolver.g[(x+step/2)+(3*h/4)*w];
				lightArray[i+8] = fluidSolver.b[(x+step/2)+(3*h/4)*w];
				}
			}

			Object[] data = new Object[lightArray.length];
			for(int i = 0; i < lightArray.length; i++)
			{
				data[i] = new Integer((int) (10000*lightArray[i]));
			}
			OSCPacket p = new OSCMessage("/setcolors", data);
			try {
				osc.send(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

