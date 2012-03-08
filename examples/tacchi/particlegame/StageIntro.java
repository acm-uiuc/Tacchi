package tacchi.particlegame;

import java.util.HashMap;

import tacchi.particlegame.animation.*;
import processing.core.*;

public class StageIntro {
	private static final int HEIGHT = 128;
	private static final int ANGLE = 40;
	private static final int STAGE_OFF = 24;
	private static final int STAGE_OFF2 = 20;
	private static final int NAME_OFF = 50;
	private static final int NAME_OFF2 = 54;
	
	protected static Animation baseBars;
	protected static Animation baseText;
	protected static Animation baseText2;
	protected static Animation baseText3;
	protected static PImage baseGradient;
	
	protected Game game;
	
	protected Animation bars;
	protected Animation text;
	protected Animation text2;
	protected Animation text3;
	protected PImage gradient;
	protected PFont stageFont;
	protected PFont stageFont2;
	protected PFont nameFont;
	protected PFont nameFont2;
	
	protected float barX1 = 0;
	protected float barX2 = 0;
	protected float panelX1 = 0;
	protected float panelX2 = 0;
	
	protected float stageX = 0;
	protected float nameX = 0;
	protected float stageAlpha = 0;
	protected float nameAlpha = 0;
	protected String stageText = "Stage 0";
	protected String nameText = "Hello World!";
	
	protected float stageX2 = 0;
	protected float stageAlpha2 = 0;
	protected float nameX2 = 0;
	protected float nameAlpha2 = 0;
	
	public Color barColor = new Color(1, 1, 1, 1);
	public Color panelColor = new Color(0.4f, 0.5f, 0.8f, 0.5f);
	public Color stageColor = new Color(1, 1, 1, 1);
	public Color nameColor = new Color(0.4f, 0.5f, 0.8f, 1);
	public float stageBackAlpha = 0.05f;
	public float nameBackAlpha = 0.25f;
	
	public StageIntro(Game game) {
		this.game = game;
		loadResources();
	}
	
	public StageIntro() {
		this(Game.getCurrent());
	}
	
	protected void loadResources() {
		//Load animations if not already done
		if (baseBars == null) {
			HashMap<String, Animation> anims = Animation.loadNamed("data/anims/stage-intro.anim");
			baseBars = anims.get("bars");
			baseText = anims.get("text");
			baseText2 = anims.get("text2");
			baseText3 = anims.get("text3");
		}
		
		if (baseGradient == null) {
			baseGradient = game.loadImage("data/images/gradient.png");
		}
		
		bars = baseBars.clone();
		text = baseText.clone();
		text2 = baseText2.clone();
		text3 = baseText3.clone();
		gradient = baseGradient;
		stageFont = Game.fonts.get("stage");
		stageFont2 = Game.fonts.get("stage2");
		nameFont = Game.fonts.get("stageName");
		nameFont2 = Game.fonts.get("stageName2");
	}
	
	public void play() {
		bars.play(true);
		text.play(true);	
		text2.play(true);
		text3.play(true);
	}
	
	public void stop() {
		bars.stop();
		text.stop();
		text2.stop();
		text3.stop();
	}
	
	public boolean isFinished() {
		return bars.isFinished();
	}
	
	public void setNumber(int num) {
		stageText = "Stage " + num;
	}
	
	public void setName(String name) {
		nameText = name;
	}
	
	public void draw(PGraphics g, float timeElapsed) {
		bars.step(timeElapsed);
		text.step(timeElapsed);
		text2.step(timeElapsed);
		text3.step(timeElapsed);
		
		//Screen should be clear of all items when finished, so we don't need to draw
		if (isFinished())
			return;
		
		barX1 = bars.getValue("BarOut");
		barX2 = bars.getValue("BarIn");
		panelX1 = bars.getValue("PanelOut");
		panelX2 = bars.getValue("PanelIn");
		
		stageX = text.getValue("StageX");
		stageAlpha = text.getValue("StageAlpha");
		nameX = text.getValue("NameX");
		nameAlpha = text.getValue("NameAlpha");
		
		stageX2 = text2.getValue("StageX");
		stageAlpha2 = text2.getValue("StageAlpha");
		nameX2 = text3.getValue("NameX");
		nameAlpha2 = text3.getValue("NameAlpha");
		
		g.pushMatrix();
		g.pushStyle();
		drawHalf(g, timeElapsed);
		
		g.translate(game.width, game.height);
		g.rotate(PConstants.PI);
		drawHalf(g, timeElapsed);
		
		g.popMatrix();
		g.popStyle();
	}
	
	@SuppressWarnings("deprecation")
	private void drawHalf(PGraphics g, float timeElapsed) {
		
		g.textAlign(PConstants.CENTER);
		
		if (nameAlpha2 > 0) {
			//draw transparent behind name text
			g.textFont(nameFont2);
			g.fill(nameColor.r, nameColor.g, nameColor.b, nameBackAlpha * nameAlpha2);
			g.text(nameText, nameX2 * game.width, HEIGHT + NAME_OFF2);
		}
		
		if (nameAlpha > 0) {
			//draw stage name text
			g.textFont(nameFont);
			g.fill(nameColor.r, nameColor.g, nameColor.b, nameColor.a * nameAlpha);
			g.text(nameText, nameX * game.width, HEIGHT + NAME_OFF);
		}
		
		// draw top panel
		g.noStroke();
		g.fill(1, 1, 1, 1);
		g.textureMode(PConstants.NORMALIZED);
		g.beginShape();
		g.texture(gradient);
		g.tint(panelColor.r, panelColor.g, panelColor.b, panelColor.a);
		
		g.vertex(panelX1 * game.width - ANGLE, 0, 0, 0);
		g.vertex(panelX2 * game.width, 0, 1, 0);
		g.vertex(panelX2 * game.width + ANGLE, HEIGHT, 1, 1);
		g.vertex(panelX1 * game.width, HEIGHT, 0, 1);
		g.endShape();
	
		//draw bar
		g.noFill();
		g.stroke(barColor.r, barColor.g, barColor.b, barColor.a);
		g.strokeWeight(4);
		g.line(barX1 * game.width, HEIGHT, barX2 * game.width, HEIGHT);
		
		
		if (stageAlpha > 0) {
			//draw transparent behind stage text
			g.textFont(stageFont2);
			g.fill(stageColor.r, stageColor.g, stageColor.b, stageBackAlpha * stageAlpha2);
			g.text(stageText, stageX2 * game.width, HEIGHT - STAGE_OFF2);
		}
		
		if (stageAlpha > 0) {
			//draw stage text
			g.textFont(stageFont);
			g.fill(stageColor.r, stageColor.g, stageColor.b, stageColor.a * stageAlpha);
			g.text(stageText, stageX * game.width, HEIGHT - STAGE_OFF);
		}
		
		
	}
}
