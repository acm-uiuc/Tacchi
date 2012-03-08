package tacchi.particlegame;

import java.util.Collection;
import processing.core.*;

public abstract class ParticleModifier implements StageLoadable {
	
	private static final float MoveTolerance = 5.0f;
	private static final float MinPower = 0.8f;
	private static final float CenterPower = 1.0f;
	private static final float MaxPower = 1.2f;
	public enum ModifierState {
		None,
		Selected,
		Moving,
		Sizing
	}
	
	public PVector pos = new PVector();
	protected float radius = 50;
	protected float r2;
	
	public ModifierState state = ModifierState.None;
	protected float minRadius = 20;
	protected float maxRadius = 180;
	protected boolean moveable = true;
	protected boolean sizeable = true;
	protected ObjectRenderer renderer;
	
	public Cursor cursor1 = null;
	public Cursor cursor2 = null;
	
	public float getPower() {
		float half = (maxRadius - minRadius) / 2;
		if (radius >= half) 
			return ((MaxPower - CenterPower) / (maxRadius - half)) * (radius - half) + CenterPower;
		else 
			return ((CenterPower - MinPower) / (half - minRadius)) * (radius - half) + CenterPower;
	}
	
	public ParticleModifier(ObjectRenderer renderer, PVector pos) {
		this.renderer = renderer;
		this.pos = pos;
	}
	
	public ParticleModifier(ObjectRenderer renderer, PVector pos, float radius) {
		this(renderer, pos);
		setRadius(radius);
	}
	
	public ParticleModifier(PVector pos) {
		this.pos = pos;
		this.renderer = new ObjectRenderer(this);
	}
	
	public ParticleModifier(PVector pos, float radius)
	{
		this(pos);
		setRadius(radius);
	}
	
	
	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
		r2 = PApplet.sq(radius);
	}
	
	public void step(Collection<Particle> particles, float secondsElapsed) {
		renderer.step(secondsElapsed);
	}
	public void draw(PGraphics g) {
		renderer.draw(g);
	}
	
	/**
	 * Gets the distance squared to particle p
	 * @param p
	 * @return
	 */
	public float d2(Particle p)
	{
		return PApplet.sq(p.pos.x - pos.x) + PApplet.sq(p.pos.y - pos.y);
	}
	
	public void updateCursors(Game game) {
		switch (state) {
		case None: break;
		case Selected:
			if (cursor1.distFromStart() >= MoveTolerance)
				state = ModifierState.Moving;
			break;
		case Moving:
			if (!moveable)
				return;
			move(cursor1.getChange(), game);
			break;
		case Sizing:
			if (!sizeable)
				return;
			float lastDist = cursor1.lastDistTo(cursor2);
			float newDist = cursor1.distTo(cursor2);
			size(newDist - lastDist);
			break;
		}
	}
	
	public void move(PVector change, Game game) {
		pos.x += change.x;
		pos.y += change.y;
		if (pos.x - minRadius < 0)
			pos.x = (float) minRadius;
		if (pos.y - minRadius < 0)
			pos.y = (float) minRadius;
		if (pos.x + minRadius > game.width)
			pos.x = (float) (game.width - minRadius);
		if (pos.y + minRadius > game.height)
			pos.y = (float) (game.height - minRadius);
	}
	
	public void size(float change) {
		setRadius(getRadius() + change);
		if (radius < minRadius)
			setRadius(minRadius);
		if (radius > maxRadius)
			setRadius(maxRadius);
	}
	
	public void destroy() {
		renderer.startDestroy();
	}
	
	public boolean isDestroyed() {
		return renderer.state == ObjectRenderer.State.Destroyed;
	}
	
	@Override
	public void parseFileLine(Utils.ParseInfo info) {
		if (info.symbol.equals("radius")) {
			
			if (info.args.length == 3) {
				minRadius = Float.parseFloat(info.args[1]);
				maxRadius = Float.parseFloat(info.args[2]);
			}
			setRadius(Float.parseFloat(info.args[0]));
		}
		else if (info.symbol.equals("mode")) {
			for (String arg : info.args) {
				arg = arg.toLowerCase();
				if (arg.equals("move"))
					moveable = true;
				else if (arg.equals("nomove"))
					moveable = false;
				else if (arg.equals( "size"))
					sizeable = true;
				else if (arg.equals("nosize"))
					sizeable = false;
			}
		}
	}
}
