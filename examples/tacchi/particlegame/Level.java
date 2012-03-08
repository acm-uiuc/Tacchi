package tacchi.particlegame;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import processing.core.PVector;

public class Level implements StageLoadable {
	
	private static Level current = null;
	protected static  int lineNumber = 0;
	protected static Level getCurrent() {
		return current;
	}
	
	private static float scale;
	private static float xshift;
	private static float yshift;
	
	public String name = "";
	public Dimension size = new Dimension();

	ArrayList<ParticleModifier> objects = new ArrayList<ParticleModifier>();
	ArrayList<Emitter> emitters = new ArrayList<Emitter>();
	ArrayList<CollectorData> collectors = new ArrayList<CollectorData>();
	ArrayList<LevelSoundLoopData> loops = new ArrayList<LevelSoundLoopData>();
	
	
	
	private static void errorLine() {
		System.err.format("Line %1$2d: ", lineNumber);
	}
	
	public static Level load(String filename) {
		Level level = new Level();
		Level.current = level;
		
		//Current object being loaded
		StageLoadable current = null;
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				lineNumber = reader.getLineNumber();

				Utils.ParseInfo info = Utils.parseStageLine(line);
				if (info == null)
					continue;
				
				if (info.tabbed) {
					// line belongs to current object
					if (current != null)
						current.parseFileLine(info);
				}
				else {
					// finalize previous object
					if (current != null) {
						level.finalizeObject(current);
						current = null;
					}
					// check for level commands
					if (info.symbol.equals("name")) 
						level.name = info.arglist;
					else if (info.symbol.equals("size"))
						level.size = new Dimension(Integer.parseInt(info.args[0]), Integer.parseInt(info.args[1]));
					else {
						// make new object
						current = level.createFromFile(info);
					}
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("Could not find \"" + filename + "\"");
		}
		catch (IOException e) {
			errorLine();
			e.printStackTrace();
		}
		
		if (current != null)
			level.finalizeObject(current);
		
		Level.current = null;
		return level;
	}
	
	private StageLoadable createFromFile(Utils.ParseInfo info) {
		if (info.symbol.equals("loop"))
			return new LevelSoundLoopData(Integer.parseInt(info.args[0]));
		else if (info.symbol.equals("emitter")) 
			return new Emitter(parsePosition(info.args[0], info.args[1]), 0);
		else if (info.symbol.equals("collector"))
			return new CollectorData(parsePosition(info.args[0], info.args[1]));
		else if (info.symbol.equals("colorer"))
			return new Colorer(parsePosition(info.args[0], info.args[1]));
		else if (info.symbol.equals("director"))
			return new Director(parsePosition(info.args[0], info.args[1]), 0);
		else if (info.symbol.equals("attractor"))
			return new Attractor(parsePosition(info.args[0], info.args[1]));
		else {
			errorLine();
			System.err.println("Unknown object type \"" + info.symbol + "\"");
		}
		return null;
	}
	
	private PVector parsePosition(String x, String y) {
		float xx = Float.parseFloat(x);
		float yy = Float.parseFloat(y);
		
		return new PVector(xx, yy);
	}
	
	private void finalizeObject(StageLoadable object) {
		if (object instanceof CollectorData) {
			collectors.add((CollectorData)object);
		}
		else if (object instanceof ParticleModifier) {
			objects.add((ParticleModifier)object);
		}
		else if (object instanceof Emitter) {
			emitters.add((Emitter)object);
		}
		else if (object instanceof LevelSoundLoopData) {
			loops.add((LevelSoundLoopData)object);
		}
		else {
			errorLine();
			System.err.println("Failed to load object.  Unknown type.");
		}
			
	}
	
	private static PVector fixPosition(PVector pos) {
		return new PVector(
			Math.round((pos.x * scale) + xshift),
			Math.round((pos.y * scale) + yshift));
	}
	
	public void apply(Game game) {
		game.links.clear();
		game.particles.modifiers.clear();
		game.particles.emitters.clear();
		
		Level.scale = 1;
		Level.xshift = 0;
		Level.yshift = 0;
		float xscale = (float)game.width / size.width;
		float yscale = (float)game.height / size.height;
		
		/* keep aspect ratio and center to screen */
		if (xscale > yscale) {
			Level.scale = yscale;
			Level.xshift = size.width * (xscale - scale) / 2;
		}
		else if (yscale > xscale) {
			Level.scale = xscale;
			Level.yshift = size.height * (yscale - scale) / 2;
		}
		else
			Level.scale = xscale;
		
		//Apply sound loop volumes
		for (LevelSoundLoopData loop : loops) {
			loop.apply(Game.sound.loops.get(loop.loop));
		}
		
		//Add objects
		for (ParticleModifier mod : objects) {
			mod.pos = fixPosition(mod.pos);
			game.particles.modifiers.add(mod);
		}
		
		for (Emitter emit : emitters) {
			emit.pos = fixPosition(emit.pos);
			game.particles.emitters.add(emit);
		}
		
		game.win.collectors.clear();
		
		for (CollectorData data : collectors) {
			Collector c = data.makeCollector();
			c.pos = fixPosition(c.pos);
			game.particles.modifiers.add(c);
			game.win.collectors.add(c);
			
			boolean linked = false;
			/* If link already exists, add collector to it, else create link */
			for (CollectorLink link : game.links) {
				if (link.loop == Game.sound.loops.get(data.loop)) {
					link.collectors.add(c);
					linked = true;
				}
			}
			if (!linked) {
				CollectorLink link = new CollectorLink(Game.sound.loops.get(data.loop));
				link.collectors.add(c);
				game.links.add(link);
			}
		}
		
		game.beginLevel();
	}

	@Override
	public void parseFileLine(Utils.ParseInfo info) {
		//Nothing special in level files yet
		
	}
}
