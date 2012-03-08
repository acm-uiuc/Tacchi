package tacchi.particlegame;

import processing.core.PVector;


public class CollectorData extends Collector {

	public int loop = 0;
	
	public CollectorData(PVector pos) {
		super(pos);
	}

	public CollectorData(PVector pos, ParticleColor color) {
		super(pos, color);
	}

	@Override
	public void parseFileLine(Utils.ParseInfo info) {
		if (info.symbol.equals("color")) {
			setColor(ParticleColor.getColor(info.args[0]));
		}
		else if (info.symbol.equals("loop")) {
			loop = Integer.parseInt(info.args[0]);
		}
		else if (info.symbol.equals("maxfill")) {
			maxFill = Float.parseFloat(info.args[0]);
		}
		else
			super.parseFileLine(info);
	}
	
	public Collector makeCollector() {
		Collector c = new Collector(pos);
		c.setColor(getColor());
		c.fill = fill;
		c.fillRate = fillRate;
		c.maxFill = maxFill;
		c.moveable = moveable;
		c.sizeable = sizeable;
		c.stayFullTime = stayFullTime;
		c.unfillRate = unfillRate;
		c.setRadius(getRadius());
		
		return c;
	}
}
