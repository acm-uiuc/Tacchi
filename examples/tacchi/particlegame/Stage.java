package tacchi.particlegame;
import java.util.*;
import java.io.*;

public class Stage {
	private static Stage current = null;
	protected static int lineNumber = 0;
	protected static Stage getCurrent() {
		return current;
	}
	
	public String name = "";
	private File file = null;
	private String stagePath = "data";
	private String audioPath = "data/music";
	ArrayList<Level> levels = new ArrayList<Level>();
	ArrayList<SoundLoopData> loops = new ArrayList<SoundLoopData>();

	private int currentLevel = -1;
	
	public int getCurrentNumber() {
		return currentLevel + 1;
	}
	
	public String getCurrentName() {
		return levels.get(currentLevel).name;
	}
	
	public Level getCurrentLevel() {
		return levels.get(currentLevel);
	}
	
	public Stage(String filename) {
		file = new File(filename);
	}
	
	
	
	
	public void apply() {
		apply(Game.getCurrent());
	}
	
	public void apply(Game game) {
		Game.sound.stop();
		Game.sound.dispose();
		
		for (SoundLoopData data : loops) {
			SoundLoop loop = data.makeLoop(true);
			Game.sound.loops.add(loop);
		}
		
		Game.sound.synchronize();
		
		game.beginStage();
	}
	
	public boolean nextLevel() {
		return nextLevel(Game.getCurrent());
	}
	
	public boolean nextLevel(Game game) {
		if (currentLevel < levels.size() - 1) {
			levels.get(++currentLevel).apply(game);
			return true;
		}
		return false;
	}
	
	public void firstLevel() {
		firstLevel(Game.getCurrent());
	}
	
	public void firstLevel(Game game) {
		currentLevel = -1;
		nextLevel(game);
	}
	
	
	
	
	private static void errorLine() {
		System.err.format("Line %1$2d: ", lineNumber);
	}
	
	public static Stage load(String filename) {
		Stage stage = new Stage(filename);
		Stage.current = stage;
		
		//Current object being loaded
		StageLoadable current = null;
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				lineNumber = reader.getLineNumber();
				//Strip comments
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
						stage.finalizeObject(current);
						current = null;
					}
					// check for level commands
					if (info.symbol.equals("name")) 
						stage.name = info.arglist;
					else {
						// make new object
						current = stage.createFromFile(info);
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
			stage.finalizeObject(current);
		
		Stage.current = null;
		return stage;
	}
	
	private StageLoadable createFromFile(Utils.ParseInfo info) {
		if (info.symbol.equals("path")) {
			stagePath = Utils.Path.combine(
					Utils.Path.getDirectory(file), 
					Utils.Path.replace(info.arglist, Game.pathVars));
		}
		else if (info.symbol.equals("audio")) {
			audioPath = Utils.Path.combine(
					Utils.Path.getDirectory(file), 
					Utils.Path.replace(info.arglist, Game.pathVars));
		}
		else if (info.symbol.equals("loop")) {
			String path = Utils.Path.combine(
					audioPath, Utils.Path.replace(info.arglist, Game.pathVars));
			return new SoundLoopData(path);
		}
		else if (info.symbol.equals("level")) {
			String path = Utils.Path.combine(
					stagePath, Utils.Path.replace(info.arglist, Game.pathVars));
			return Level.load(path);
		}
		else {
			errorLine();
			System.err.println("Unknown object type \"" + info.symbol + "\"");
		}
			
		return null;
	}
	
	private void finalizeObject(StageLoadable object) {
		if (object instanceof SoundLoopData) {
			loops.add((SoundLoopData)object);
		}
		else if (object instanceof Level) {
			levels.add((Level)object);
		}
	}
}
