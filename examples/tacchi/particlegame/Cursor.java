package tacchi.particlegame;

import java.util.HashMap;

import org.mt4j.input.inputData.InputCursor;

import TUIO.*;
import processing.core.*;
import tacchi.particlegame.ParticleModifier.ModifierState;
import tacchi.particlegame.animation.*;

public class Cursor {
	protected static Animation baseAnim;
	protected Animation anim;
	public float sr = 0.9f;
	public float sg = 0.9f;
	public float sb = 1;
	public float fr = 0.2f;
	public float fg = 0.2f;
	public float fb = 0.5f;
	
	private Game game;
	
	public ParticleModifier target;
	public InputCursor cursor;
	
	private PVector startPos;
	private PVector pos;
	private PVector lastPos;
	
	public PVector getPos() {
		return pos;
	}
	public void setPos(PVector pos) {
		this.pos = pos;
	}

	public PVector getLastPos() {
		return lastPos;
	}
	public void setLastPos(PVector lastPos) {
		this.lastPos = lastPos;
	}

	public PVector getChange() {
		return PVector.sub(pos, lastPos);
	}
	
	public float distFromStart() {
		return startPos.dist(pos);
	}
	
	public float lastDistTo(Cursor c) {
		return lastPos.dist(c.lastPos);
	}
	
	public float distTo(Cursor c) {
		return pos.dist(c.pos);
	}
	
	public Cursor(Game g, InputCursor c) {
		this(g, c, null);
	}
	
	public Cursor(Game g, InputCursor c, ParticleModifier t) {
		game = g;
		cursor = c;
		
		pos = new PVector(cursor.getCurrentEvtPosX(), cursor.getCurrentEvtPosY());
		startPos = new PVector(pos.x, pos.y);
		lastPos = new PVector(pos.x, pos.y);
		
		attach(t);
		
		loadResources();
		anim.play();
	}
	
	protected void loadResources() {
		//Load animations if not already done
		if (baseAnim == null) {
			HashMap<String, Animation> anims = Animation.loadNamed("data/anims/cursor.anim");
			baseAnim = anims.get("cursor");
		}
		
		anim = baseAnim.clone();
	}
	
	public boolean attach(ParticleModifier t) {
		target = t;
		if (target != null) {
			if (!target.moveable && !target.sizeable)
				return false;
			
			if (target.cursor1 == null)
			{
				target.cursor1 = this;
				target.state = ModifierState.Selected;
				return true;
			}
			else if (target.cursor2 == null && 
					(target.state == ModifierState.Selected || target.state == ModifierState.Moving)) {
				target.cursor2 = this;
				target.state = ModifierState.Sizing;
				return true;
			}
			else {
				target = null;
				return false;
			}
		}
		return false;
	}
	
	public void remove()
	{
		if (target == null)
			return;
		if (target.state == ModifierState.Selected || target.state == ModifierState.Moving) {
			// remove cursor if moving or selected
			if (target.cursor1 == this) {
				target.state = ModifierState.None;
				target.cursor1 = null;
			}
		}
		else if (target.state == ModifierState.Sizing) {
			// remove cursor if sizing
			if (target.cursor1 == this) {
				if (target.cursor2 == null) {
					target.state = ModifierState.None;
					target.cursor1 = null;
				}
				else {
					target.state = ModifierState.Selected;
					target.cursor1 = target.cursor2;
					target.cursor2 = null;
					target.cursor1.reset();
				}
			}
			else if (target.cursor2 == this) {
				if (target.cursor1 == null) {
					target.state = ModifierState.None;
					target.cursor2 = null;
				}
				else {
					target.state = ModifierState.Selected;
					target.cursor2 = null;
					target.cursor1.reset();
				}
			}
		}
		else if (target.cursor1 == this) {
			target.cursor1 = null;
		}
		else if (target.cursor2 == this) {
			target.cursor2 = null;
		}
	}
	
	public void update() {
		lastPos.x = pos.x;
		lastPos.y = pos.y;
		pos.x = cursor.getCurrentEvtPosX();
		pos.y = cursor.getCurrentEvtPosY();
		//System.out.println("Updating cursor to "+pos.x + " , "+pos.y);
	}
	
	public void reset() {
		lastPos.x = pos.x;
		lastPos.y = pos.y;
		startPos.x = pos.x;
		startPos.y = pos.y;
	}

	public void draw(PGraphics g, float timeElapsed) {
		anim.step(timeElapsed);
		float r = anim.getValue("Radius");
		float r2 = anim.getValue("Radius2");
		float a = anim.getValue("Alpha");
		float a2 = anim.getValue("Alpha2");
		
		g.pushStyle();
		
		g.noStroke();
		g.fill(fr, fg, fb, a);
		g.ellipse(startPos.x, startPos.y, r, r);
		
		g.noFill();
		g.strokeWeight(2.0f);
		g.stroke(sr, sg, sb, a2 * 0.5f);
		g.ellipse(pos.x, pos.y, r2 - 2, r2 - 2);
		g.ellipse(pos.x, pos.y, r2 + 2, r2 + 2);
		
		g.stroke(sr, sg, sb, a2);
		g.ellipse(pos.x, pos.y, r2, r2);
		
		g.popStyle();
	}
}
