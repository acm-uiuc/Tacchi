package tacchi.particlegame;

import java.io.File;

import javax.sound.sampled.*;

public class SoundLoop {
	public boolean hasError = false;
	public String filename;
	public int loopStart = 0;
	public int loopEnd = -1;
	private float volume = 0;
	private float minVolume = 0;
	private float maxVolume = 1;
	
	private Clip clip;
	private AudioInputStream stream;
	//private DataLine.Info lineInfo;
	
	public Clip getClip() {
		return clip;
	}
	
	public float getMinVolume() {
		return minVolume;
	}
	public void setMinVolume(float minVolume) {
		this.minVolume = minVolume;
		setVolume(volume);
	}
	
	public float getMaxVolume() {
		return maxVolume;
	}
	public void setMaxVolume(float maxVolume) {
		this.maxVolume = maxVolume;
		setVolume(volume);
	}

	/**
	 * Gets the volume of the sound loop
	 * @return
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * Sets the volume of the sound loop
	 * @param volume the volume [0-1]
	 */
	public void setVolume(float volume) {
		this.volume = volume;
		
		//set volume in SC to getScaledVolume()
	}
	
	public float getScaledVolume() {
		return (maxVolume - minVolume)*volume + minVolume;
	}

	public SoundLoop(String file) {
		this(file, true);
	}
	
	public SoundLoop(String file, boolean load) {
		filename = file;
		
		if (load)
			hasError = !load();
	}
	
	/**
	 * Loads the sound file into SuperCollider
	 * @return true on success
	 */
	public boolean load() {
		if (clip != null && clip.isOpen())
			clip.close();
		try {
			File file = new File(filename);
			stream = AudioSystem.getAudioInputStream(file);
			DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
			clip = (Clip)Game.sound.mixer.getLine(info);
			clip.open(stream);
			
			clip.setLoopPoints(loopStart, loopEnd);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Starts the loop
	 */
	public void play() {
		//TODO Make stuff happen
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		//set initial volume
		setVolume(volume);
	}
	
	/**
	 * Stops the loop
	 */
	public void stop() {
		
	}

	@Override
	protected void finalize() throws Throwable {
		if (clip != null && clip.isOpen())
			clip.close();
		super.finalize();
	}
}
