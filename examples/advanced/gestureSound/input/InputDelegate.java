package advanced.gestureSound.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mt4j.components.MTComponent;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputSources.AbstractInputSource;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public class InputDelegate extends MTComponent {
	List<FadeOut> tickings;

	public InputDelegate(PApplet pApplet, final AbstractScene scene) {
		super(pApplet);
		tickings = new ArrayList<FadeOut>();
		// TODO Auto-generated constructor stub
		
		scene.getCanvas().addInputListener(new IMTInputEventListener() {

        	//@Override
        	public boolean processInputEvent(final MTInputEvent inEvt){
        		if(inEvt instanceof AbstractCursorInputEvt){
        			AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt)inEvt;
        			if (posEvt.hasTarget() && posEvt.getTargetComponent().equals(scene.getCanvas())){
        				final InputCursor m = posEvt.getCursor();
        				if (posEvt.getId() == AbstractCursorInputEvt.INPUT_ENDED) {
        					trailOff(inEvt, m);
        				}
        				else {
        					fireInputEvent(inEvt);
        				}
        				
        			}
        		}
        		return false;
        	}
		});
	}

	public void trailOff(final MTInputEvent inEvt, final InputCursor m) {
		final AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt)inEvt;
		m.getEvents().remove(posEvt);

		tickings.add(new FadeOut() {
			int i=0;
			float currentX = posEvt.getPosX();
			float currentY = posEvt.getPosY();
			Vector3D currentVec = m.getVelocityVector();

			public void tick() {
				synchronized(m.getEvents()) {

					if (currentVec.length() < 1) { 
						m.getEvents().add(posEvt);
						fireInputEvent(posEvt);
						done();
						return;
					}
					AbstractCursorInputEvt evt = m.getPreviousEvent();
					evt = predictNext(m);
					m.getEvents().add(evt);
					System.out.println("Sending evt: " + evt + " Stats: "
							+ evt.getId() + " " + evt.getPosX() + " "
							+ evt.getPosY() + " " + evt.getWhen());
					fireInputEvent(evt);
					
					i++;
				}
			}
			public AbstractCursorInputEvt predictNext(InputCursor m) {
				AbstractCursorInputEvt evt = m.getPreviousEvent();
				Vector3D out = currentVec.getAdded(new Vector3D(currentX, currentY));
				currentVec = currentVec.getScaled(0.9f);
				currentX = out.x;
				currentY = out.y;
				return new MTFingerInputEvt((AbstractInputSource) evt
						.getSource(), evt.getTargetComponent(), currentX
						, currentY , evt.getId() + 2, m);
			}
		});

	}
	
	

	public abstract class FadeOut {
		public boolean done = false;
		public abstract void tick();
		public void done() {
			done = true;
		}
	}

	public void tick() {
			Iterator i = tickings.iterator();
			FadeOut f;
			while (i.hasNext()) {
				f=(FadeOut) i.next();
				f.tick();
				if (f.done){
					i.remove();
				}
			}
			

	}
}
