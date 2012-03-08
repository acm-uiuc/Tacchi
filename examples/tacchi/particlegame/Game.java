package tacchi.particlegame;
import java.awt.Rectangle;
import java.io.*;

import TUIO.*;
//import tacchi.test.jcollider.*;
//import tacchi.test.main.Preferences;

import java.util.*;

import org.mt4j.input.inputData.InputCursor;

import processing.core.*;

@SuppressWarnings("serial")
public class Game 
{
	public enum State {
		None,
		StageIntro,
		Game,
		End,
	}
	
	public int width;
	public int height;
	public boolean setup = false;
	private static final long serialVersionUID = -2376969893766909170L;
	// A cursor can be this many pixels from the outside of an object and still select it
	private static final float CursorTolerance = 10.0f;
	// An object must be inside another at least this times as big to be considered enclosed
	private static final float MinEncloseScale = 1.3f;
	// An object can be this much of its radius outside an enclosing object and still be considered enclosed
	private static final float EncloseTolerance = 0.35f;
	
	private static Game current;
	public static HashMap<String, PFont> fonts = new HashMap<String, PFont>();
	public static HashMap<String, String> pathVars = new HashMap<String, String>() {
		{
			String base = "/";
			try {
				File f = new File(".");
				base = f.getCanonicalPath() + "/";
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			put("audio", base + "data/audio");
			put("levels", base + "data/levels");
			put("fonts", base + "data/fonts");
			put("images", base + "data/images");
		}
	};
	
	public static SoundController sound;
	public static TuioClient tuio;
	public ArrayList<Cursor> cursors;
	public ArrayList<CollectorLink> links;
	
	public ParticleSystem particles;
	public WinController win;
	
	public StageIntro stageIntro;
	public LevelIntro levelIntro;
	
	public State state = State.None;
	private boolean endingLevel = false;
	
	private StageSet stages;
	private Stage currentStage;
	
	private static final int fpsUpdateInterval = 500;
	private int lastTime;
	private int lastFpsUpdate;
	private int fps;
	
	PApplet applet;
	
	public static Game getCurrent() {
		return current;
	}
	
	public static void main(String args[])
	{
		//PApplet.main(new String[] { /*"--present",*/ "tacchi.particlegame.Game", "" });
	}
	
	public Game(PApplet applet) 
	{
		Game.current = this;
		
		this.applet = applet;
		width = applet.width;
		height = applet.height;
		//Preferences.fillVariables();
		
		//set up sound
		sound = new SoundController();
		links = new ArrayList<CollectorLink>();
		win = new WinController();
		
		//set up TUIO
		cursors = new ArrayList<Cursor>(8);
		/*
		tuio = new TuioClient();
		tuio.connect();
		tuio.addTuioListener(this);*/
	}
	
	public void setup(PGraphics g)
	{
		/*size(Preferences.WIDTH_IN_PIXELS, 
			Preferences.HEIGHT_IN_PIXELS,
			OPENGL);
		
		hint(ENABLE_OPENGL_4X_SMOOTH);
		frameRate(Preferences.TARGET_FPS);
		colorMode(RGB, 1.0f);
		*/
		
		loadResources();
		g.textFont(fonts.get("fps"));
		
		g.ellipseMode(g.RADIUS);
		g.rectMode(g.RADIUS);
		
		particles = new ParticleSystem(new Rectangle(0, 0, applet.width, applet.height));
		
		stageIntro = new StageIntro(this);
		levelIntro = new LevelIntro(this);
		
		loadStageSet("data/levels/test-game.dat");
		
		//set up timers
		lastTime = lastFpsUpdate = applet.millis();
		setup = true;
	}

	public void loadResources() {
		fonts.put("stage", applet.createFont("fonts/MgOpenModernaBold.ttf", 64, true));
		fonts.put("stage2", applet.createFont("fonts/MgOpenModernaBold.ttf", 82, true));
		fonts.put("stageName", applet.createFont("fonts/MgOpenModernaRegular.ttf", 42, true));
		fonts.put("stageName2", applet.createFont("fonts/MgOpenModernaRegular.ttf", 60, true));
		fonts.put("level", applet.createFont("fonts/LiberationSans-BoldItalic.ttf", 36, true));
		fonts.put("fps", applet.createFont("fonts/Comfortaa Regular.ttf", 28, true));
	}
	
	public void loadStageSet(String filename) {
		stages = StageSet.load(filename);
		currentStage = stages.firstStage();
		currentStage.apply(this);
	}
	
	public void beginStage() {
		state = State.StageIntro;
		System.out.println("Starting");
		stageIntro.setNumber(stages.getCurrentNumber());
		stageIntro.setName(currentStage.name);
		stageIntro.play();
	}
	
	public void beginLevel() {
		//sound.play();
		System.out.println("Starting level");

		levelIntro.setText(currentStage.getCurrentName());
		levelIntro.play();
	}
	
	public void draw(PGraphics g) 
	{
		g.colorMode(g.RGB, 1.0f);
		int current = applet.millis();
		float elapsed = (current - lastTime) / 1000.0f;
		
		step(elapsed);
		
		
		g.background(0);
		//g.noStroke();
		//g.fill(0, 0, 0, 0.5f);
		//g.rect(0, 0, width, height);
		
		particles.draw(g);
		
		//System.out.println("v1: " + Game.soundLoops.get(0).getVolume() + 
		//		" v2: " + Game.soundLoops.get(1).getVolume() + 
		//		" v3: " + Game.soundLoops.get(2).getVolume() +
		//		" v4: " + Game.soundLoops.get(3).getVolume());
		
		
		// Draw cursors on top of almost everything else
		if (cursors != null) {
			for (Cursor c : cursors) 
				c.draw(g, elapsed);
		}
		
		switch (state) {
		case StageIntro:
			stageIntro.draw(g, elapsed);
			break;
		case Game:
			levelIntro.draw(g, elapsed);
			break;
		}
		
		
		//if (Preferences.SHOW_FPS) {
		if (false) {
			if (current > lastFpsUpdate + fpsUpdateInterval)
			{
				fps = (int)applet.frameRate;
				lastFpsUpdate = current;
			}
			g.text(Integer.toString(fps), 5, 22);
		}
		
		lastTime = current;
		
	}

	public void step(float elapsed) {
		if (particles != null)
			particles.step(elapsed);
		
		// set sound loop volumes based on collector fill levels
		for (CollectorLink link : links) 
			link.step();
		
		// Game logic
		switch (state) {
		case StageIntro:
			if (stageIntro.isFinished()) {
				state = State.Game;
				currentStage.firstLevel(this);
			}
			break;
		case Game:
			if (win.isWon() && !endingLevel) {
				endingLevel = true;
				particles.destroyAll();
			}
			
			if (endingLevel && particles.allDestroyed()) {
				if (!currentStage.nextLevel(this)) {
					currentStage = stages.nextStage();
					if (currentStage != null)
						currentStage.apply(this);
					else
						state = State.End;
				}
					
				endingLevel = false;
			}
			break;
		case End:
			applet.exit();
			break;
		}
	}
	
	private boolean isEnclosedBy(ParticleModifier in, ParticleModifier out) {
		return (out.radius > in.radius * MinEncloseScale &&
				in.pos.dist(out.pos) + (1 - EncloseTolerance) * in.radius <= out.radius);
	}
	
	private ParticleModifier findCursorTarget(Cursor c) {
		
		// Find objects the cursor is over
		ArrayList<ParticleModifier> cursorOver = new ArrayList<ParticleModifier>();
		for (ParticleModifier mod : particles.modifiers) {
			if (c.getPos().dist(mod.pos) <= (mod.radius + CursorTolerance)) {
				if (mod.moveable || mod.sizeable)
					cursorOver.add(mod);
			}
		}
		
		if (cursorOver.size() == 0)
			return null;
		
		// Find objects that are mostly enclosed by another and give them precedence
		ArrayList<ParticleModifier> enclosed = new ArrayList<ParticleModifier>();
		for (ParticleModifier mod1 : cursorOver) {
			for (ParticleModifier mod2 : cursorOver) {
				if (mod1 == mod2)
					continue;
				if (isEnclosedBy(mod1, mod2)) {
					enclosed.add(mod1);
					break;
				}
			}
		}
		
		// Remove objects enclosing other objects from the list
		ArrayList<ParticleModifier> temp = new ArrayList<ParticleModifier>(enclosed);
		for (ParticleModifier mod1 : temp) {
			if (!enclosed.contains(mod1))
				break;
			for (ParticleModifier mod2 : temp) {
				if (mod1 == mod2)
					continue;
				if (!enclosed.contains(mod2))
					break;
				if (isEnclosedBy(mod1, mod2)) {
					enclosed.remove(mod2);
				}
			}
		}
		
		// Get list of possible targets and find object with center closest to cursor
		ArrayList<ParticleModifier> candidates = (enclosed.size() > 0) ? enclosed : cursorOver;
		ParticleModifier closest = null;
		float minDist = Float.MAX_VALUE;
		for (ParticleModifier mod : candidates) {
			float dist = c.getPos().dist(mod.pos);
			if (dist < minDist) {
				minDist = dist;
				closest = mod;
			}
		}
		
		return closest;
	}
	
	
	public void addTuioCursor(InputCursor c) {
		Cursor cursor = new Cursor(this, c);
		cursors.add(cursor);
		
		ParticleModifier mod = findCursorTarget(cursor);
		if (mod != null)
			cursor.attach(mod);
		
		
		/*
		for (ParticleModifier mod : particles.modifiers) {
			if (cursor.getPos().dist(mod.pos) <= (mod.radius + CursorTolerance)) {
				if (cursor.attach(mod))
					break;
			}
		}*/
	}


	public void refresh() {
		for (Cursor c : cursors) 
			c.update();
		
		for (ParticleModifier mod : particles.modifiers)
			mod.updateCursors(this);
	}

	public void removeTuioCursor(InputCursor c) {
		ArrayList<Cursor> temp = new ArrayList<Cursor>(cursors);
		for (Cursor cursor : temp) {
			if (cursor.cursor == c) {
				cursor.remove();
				cursors.remove(cursor);
			}
		}
	}

	
	/**
	 * Methods to act like a PApplet so the wrapper works
	 * */
	public PImage loadImage(String s) {
		return applet.loadImage(s);
	}
	
	public boolean isSetup() {
		return setup;
	}
	 
	
}
