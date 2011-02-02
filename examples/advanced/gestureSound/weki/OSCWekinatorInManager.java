package advanced.gestureSound.weki;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import de.sciss.net.OSCClient;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCServer;

public class OSCWekinatorInManager implements OSCListener {

	private OSCClient c;
	private WekiInListener callback;
	
	
	public interface WekiInListener {
		public void outParamUpdate(Float[] vals);
		public void startSound();
		public void stopSound();
	}
	
	public OSCWekinatorInManager(WekiInListener callback) {
		try {
			c = OSCClient.newUsing(OSCClient.UDP, 12000, true);
			c.addOSCListener(this);
			c.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.callback = callback;
	}
	
	@Override
	public void messageReceived(OSCMessage arg0, SocketAddress arg1, long arg2) {
		System.out.println("New message!"+arg0.getName()+" urm: size:"+arg0.getArgCount());
		if (arg0.getArgCount() > 0) {
			
			if (arg0.getName().equalsIgnoreCase("/OSCSynth/sound")) {
					callback.startSound();
			}
			else if (arg0.getName().equalsIgnoreCase("/OSCSynth/silent")) {
					callback.stopSound();
			}
			else if (arg0.getName().equalsIgnoreCase("/OSCSynth/params")) {
				Float[] vals = new Float[arg0.getArgCount()];
				for (int i=0; i< vals.length; i++) {
					vals[i] = (Float)arg0.getArg(i);
				}
				callback.outParamUpdate(vals);
			}
		}
	}

}

