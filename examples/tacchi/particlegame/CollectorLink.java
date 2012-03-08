package tacchi.particlegame;

import java.util.*;

/**
 * Manages the links between particle collectors and sound loop volumes
 * @author Joel
 *
 */
public class CollectorLink {
	public ArrayList<Collector> collectors;
	public SoundLoop loop;
	
	public CollectorLink(SoundLoop loop) {
		this.loop = loop;
		collectors = new ArrayList<Collector>();
	}
	
	public CollectorLink(SoundLoop loop, Collector c) {
		this(loop);
		addCollector(c);
	}
	
	public CollectorLink(SoundLoop loop, Collection<Collector> c) {
		this(loop);
		for (Collector e : c)
			addCollector(e);
	}
	
	public void addCollector(Collector c) {
		collectors.add(c);
	}
	
	
	public void step() {
		if (loop == null)
			return;
		float volume = 0;
		for (Collector c : collectors) {
			volume += c.fill / c.maxFill;
		}
		volume /= collectors.size();
		
		if (volume < 0)
			volume = 0;
		if (volume > 1)
			volume = 1;
		
		loop.setVolume(volume);
	}
}
