package tacchi.particlegame;

import tacchi.particlegame.Utils.ParseInfo;

public class LevelSoundLoopData implements StageLoadable {

	public int loop = 0;
	public float minVolume = 0;
	public float maxVolume = 1;
	
	public LevelSoundLoopData(int loop) {
		this.loop = loop;
	}
	
	@Override
	public void parseFileLine(ParseInfo info) {
		if (info.symbol.equals("volume")) {
			minVolume = Float.parseFloat(info.args[0]);
			if (info.args.length == 2)
				maxVolume = Float.parseFloat(info.args[1]);
		}
	}
	
	public void apply(SoundLoop loop) {
		loop.setMinVolume(minVolume);
		loop.setMaxVolume(maxVolume);
	}

}
