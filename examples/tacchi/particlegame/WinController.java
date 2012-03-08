package tacchi.particlegame;

import java.util.ArrayList;

public class WinController {
	public ArrayList<Collector> collectors = new ArrayList<Collector>();
	
	public boolean isWon() {
		if (collectors.size() == 0)
			return false;
		for (Collector c : collectors) {
			if (c.fill < c.maxFill)
				return false;
		}
		return true;
	}
}
