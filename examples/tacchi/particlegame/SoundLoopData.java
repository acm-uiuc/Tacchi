package tacchi.particlegame;

public class SoundLoopData implements StageLoadable  {
	public String filename;
	public int loopStart = 0;
	public int loopEnd = -1;
	public float minVolume = 0;
	public float maxVolume = 1;
	
	@Override
	public void parseFileLine(Utils.ParseInfo info) {
		if (info.symbol.equals("volume")) {
			minVolume = Float.parseFloat(info.args[0]);
			if (info.args.length == 2)
				maxVolume = Float.parseFloat(info.args[1]);
		}
		else if (info.symbol.equals("loop")) {
			if (info.args[0].toLowerCase().equals("start"))
				loopStart = 0;
			else
				loopStart = Integer.parseInt(info.args[0]);
			if (info.args.length == 2) {
				if (info.args[1].toLowerCase().equals("end"))
					loopEnd = -1;
				else
					loopEnd = Integer.parseInt(info.args[1]);
			}
		}
	}
	
	
	public SoundLoopData(String file) {
		filename = file;
	}
	
	public SoundLoop makeLoop(boolean load) {
		SoundLoop loop = new SoundLoop(filename, false);
		loop.setMinVolume(minVolume);
		loop.setMaxVolume(maxVolume);
		loop.loopStart = loopStart;
		loop.loopEnd = loopEnd;
		//if (load)
			//loop.load();
		return loop;
	}
}
