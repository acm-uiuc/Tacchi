package tacchi.particlegame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;

public class StageSet {
	protected static int lineNumber = 0;
	
	public ArrayList<String> stages = new ArrayList<String>();
	public String name;
	
	private File file = null;
	private String stagePath = "data";
	private int currentStage = -1;
	
	public StageSet(String filename) {
		file = new File(filename);
	}
	
	private Stage getStage(int index) {
		return Stage.load(stages.get(index));
	}
	
	public int getCurrentNumber() {
		return currentStage + 1;
	}
	
	public Stage nextStage() {
		if (currentStage < stages.size() - 1) {
			return getStage(++currentStage);
		}
		return null;
	}
	
	public Stage firstStage() {
		currentStage = -1;
		return nextStage();
	}
	
	private static void errorLine() {
		System.err.format("Line %1$2d: ", lineNumber);
	}
	
	public static StageSet load(String filename) {
		StageSet set = new StageSet(filename);
		
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				lineNumber = reader.getLineNumber();
				//Strip comments
				Utils.ParseInfo info = Utils.parseStageLine(line);
				if (info == null)
					continue;
				

				// check for level commands
				if (info.symbol.equals("name")) 
					set.name = info.arglist;
				else if (info.symbol.equals("path")) {
					set.stagePath = Utils.Path.combine(
						Utils.Path.getDirectory(set.file), 
						Utils.Path.replace(info.arglist, Game.pathVars));
				}
				else if (info.symbol.equals("stage")) {
					String path = Utils.Path.combine(
						set.stagePath, Utils.Path.replace(info.arglist, Game.pathVars));
					set.stages.add(path);
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("Could not find \"" + filename + "\"");
		}
		catch (IOException e) {
			errorLine();
			System.err.println(e);
		}
		
		return set;
	}
	
}
