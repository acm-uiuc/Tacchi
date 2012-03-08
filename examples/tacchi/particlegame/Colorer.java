package tacchi.particlegame;

import processing.core.*;
import java.util.*;

public class Colorer extends ParticleModifier {
	private ParticleColor color = ParticleColor.White;
	
	public ParticleColor getColor() {
		return color;
	}
	
	public void setColor(ParticleColor color) {
		this.color = color;
		renderer.fr = renderer.sr = color.r;
		renderer.fg = renderer.sg = color.g;
		renderer.fb = renderer.sb = color.b;
		renderer.fa = 0.2f;
	}
	
	
	public Colorer(PVector pos) {
		super(pos);
		attachIcon();
	}
	
	public Colorer(PVector pos, float radius, ParticleColor color) {
		super(pos, radius);
		setColor(color);
		attachIcon();
	}
	
	private void attachIcon() {
		ColorerIcon icon = new ColorerIcon(this);
		renderer.addExtender(icon);
	}
	
	public void step(Collection<Particle> particles, float secondsElapsed) {
		super.step(particles, secondsElapsed);
		for (Particle p : particles) {
			if (p.pos.dist(pos) <= radius) {
				p.color = color;
			}
		}
	}
	
	@Override
	public void parseFileLine(Utils.ParseInfo info) {
		if (info.symbol.equals("color")) {
			setColor(ParticleColor.getColor(info.args[0]));
		}
		else
			super.parseFileLine(info);
	}

}
