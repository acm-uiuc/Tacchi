package tacchi.particlegame;

import java.util.HashMap;

import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import tacchi.particlegame.animation.*;

public class LevelIntro {
	private static final int LEFT = 40;
	private static final int TOP = 30;
	private static final int SEP = -2;
	private static final int BAR = 3;
	private static final int LEFT_OVERHANG = 1;
	private static final int RIGHT_OVERHANG = 4;
	
	protected static Animation baseAnim;
	protected Animation anim;
	
	protected Game game;
	protected PFont font;
	
	protected float alpha = 0;
	protected String text = "<Insert Title>";
	
	public Color textColor = new Color(1, 1, 1, 0.5f);
	public Color barColor = new Color(1, 1, 1, 0.5f);
	
	
	public LevelIntro(Game game) {
		this.game = game;
		loadResources();
	}
	
	public LevelIntro() {
		this(Game.getCurrent());
	}
	
	protected void loadResources() {
		//Load animations if not already done
		if (baseAnim == null) {
			HashMap<String, Animation> anims = Animation.loadNamed("data/anims/level-intro.anim");
			baseAnim = anims.get("intro");
		}

		anim = baseAnim.clone();
		font = Game.fonts.get("level");
	}
	
	public void play() {
		anim.play(true);	
	}
	
	public void stop() {
		anim.stop();
	}
	
	public boolean isFinished() {
		return anim.isFinished();
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void draw(PGraphics g, float timeElapsed) {
		anim.step(timeElapsed);
		
		//Screen should be clear of all items when finished, so we don't need to draw
		if (isFinished())
			return;
		
		alpha = anim.getValue("Alpha");
		
		g.pushMatrix();
		g.pushStyle();
		drawHalf(g, timeElapsed);
		
		g.translate(game.width, game.height);
		g.rotate(PConstants.PI);
		drawHalf(g, timeElapsed);
		
		g.popMatrix();
		g.popStyle();
	}
	
	private void drawHalf(PGraphics g, float timeElapsed) {
		// draw top panel
		g.noStroke();
		g.fill(textColor.r, textColor.g, textColor.b, textColor.a * alpha);
		g.textFont(font);
		float w = g.textWidth(text);
		float top = g.textAscent();
		float lineY = TOP + top + g.textDescent() + SEP;
		
		
		g.text(text, LEFT, TOP + top);
		g.strokeWeight(2);
		g.noFill();
		for (int i = 0; i < BAR; i++) {
			g.stroke(barColor.r, barColor.g, barColor.b, barColor.a * alpha * ((BAR - i) / (float)BAR));
			g.line(LEFT - LEFT_OVERHANG, lineY + i, LEFT + RIGHT_OVERHANG + w, lineY + i);
		}
	}
}
