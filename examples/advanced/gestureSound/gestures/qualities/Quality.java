package advanced.gestureSound.gestures.qualities;

import org.mt4j.input.inputData.InputCursor;

import advanced.gestureSound.gestures.GestureEngine;

public abstract class Quality {
	GestureEngine engine;
	public static String name="";
	
	public static Quality cursorDetected(GestureEngine engine) {
		return null;
	}
	
	
	public Quality(GestureEngine engine) {
		this.engine=engine;
	}
	
	public abstract float update(InputCursor in);
	
	public abstract float getCurrentValue();
}
