package tacchi.particlegame;

import java.util.*;

import javax.sound.sampled.*;

public class SoundController {
	public Mixer mixer;
	public ArrayList<SoundLoop> loops;
	
	public SoundController() {
		loops = new ArrayList<SoundLoop>();
		Mixer.Info info = null; //TODO figure out how to get a mixer
		
		//info = AudioSystem.getMixerInfo()[0];
		System.out.println(info);
		//mixer = AudioSystem.getMixer(info);
	}
	
	public void synchronize() {
		//Line[] lines = new Line[loops.size()];
		//for (int i = 0; i < loops.size(); i++)
		//	lines[i] = loops.get(i).getClip();
		//if (mixer.isSynchronizationSupported(lines, false)) {
		//	mixer.synchronize(lines, false);
		//}
		//else
		//	System.err.println("Synchronization not supported.");
		

	}
	
	public void play() {
		for (SoundLoop loop : loops) {
			loop.play();
		}
	}
	
	public void stop() {
		
	}
	
	public void dispose() {
		
	}
}
