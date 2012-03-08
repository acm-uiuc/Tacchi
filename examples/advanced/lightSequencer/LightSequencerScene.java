/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package advanced.lightSequencer;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;

import msafluid.MSAFluidSolver2D;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;

import com.sun.opengl.util.BufferUtil;

/**
 * The Class FluidSimulationScene.
 * 
 * The original fluid simulation code was taken from
 * memo akten (www.memo.tv)
 * 
 */
public class LightSequencerScene extends AbstractScene{
	
	/////////
	
	private MTApplication app;
	
	private ArrayList<Light> lightArray;
	private Sequencer seq;
	
	public static int NUM_LIGHTS = 22;
	
	private PFont font;

	public LightSequencerScene(final MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		this.font = app.loadFont("GillSans-Bold-48.vlw");

		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		lightArray = new ArrayList<Light>();
		this.seq = new Sequencer(app, 22, lightArray);
		this.getCanvas().addChild(seq.p);
		this.getCanvas().addChild(seq);		
		for(int i = 0; i < NUM_LIGHTS; i++)
		{
			this.addNewLight(i);
		}
		
		if (!MT4jSettings.getInstance().isOpenGlMode()){
			System.err.println("Scene only usable when using the OpenGL renderer! - See settings.txt");
        	return;
        }

		
		IFont fontReset = FontManager.getInstance().createFont(app, "arial.ttf", 18,  new MTColor(255,255,255),  new MTColor(255,255,255));
		MTTextArea reset = new MTTextArea(0, 0, 60, 20, fontReset, mtApplication);
		reset.setText("Reset");
		
		this.getCanvas().addChild(reset);
		
		reset.setFillColor(new MTColor(50,50,50,255));
		
		reset.unregisterAllInputProcessors();
		reset.removeAllGestureEventListeners();
		
		reset.registerInputProcessor(new TapAndHoldProcessor(app,2000));
		reset.addGestureListener(TapAndHoldProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapAndHoldEvent th = (TapAndHoldEvent)ge;
				switch (th.getId()) {
				case TapAndHoldEvent.GESTURE_DETECTED:
					break;
				case TapAndHoldEvent.GESTURE_UPDATED:
					break;
				case TapAndHoldEvent.GESTURE_ENDED:
					if (th.isHoldComplete()){
						for(int i = lightArray.size()-1;i>=0;i--){
							Light l = lightArray.get(i);
							if(!l.inDock()){
								removeLight(l);
							}
						}	
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		
        this.getCanvas().setDepthBufferDisabled(true);
	}
	
	
	
	@Override
	public void drawAndUpdate(PGraphics g, long timeDelta) {
//		this.drawFluidImage();
		super.drawAndUpdate(g, timeDelta);
		//graphics.fill(30,160,30);
		//graphics.rect(10,10,100,100);
		//System.out.println("asdf");
		//b.draw(graphics, fluidSolver);

//		app.noSmooth();
//		app.fill(255,255,255,255);
//		app.tint(255,255,255,255);
//		
//		//FIXME TEST
//		PGraphicsOpenGL pgl = (PGraphicsOpenGL)app.g; 
//		GL gl = pgl.gl;
//		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
//		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
//		gl.glDisable(GL.GL_LINE_SMOOTH);
//		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
//		mtApp.colorMode(PApplet.RGB, 255); 
	
	}
	
	
	
	
	//@Override
	public void init() {
		app.registerKeyEvent(this);
	}

	//@Override
	public void shutDown() {
		app.unregisterKeyEvent(this);
		/*
		mtApp.noSmooth();
		mtApp.fill(255,255,255,255);
		mtApp.tint(255,255,255,255);
		PGraphicsOpenGL pgl = (PGraphicsOpenGL)mtApp.g; 
		GL gl = pgl.gl;
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		gl.glDisable(GL.GL_LINE_SMOOTH);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mtApp.colorMode(PApplet.RGB, 255);  
		 */
	}
	
	public void addNewLight(int i){
		Vector3D p = this.getLightLocation(i);
		Light l = new Light(this.app,(int)p.x,(int)p.y,i,this,this.font);
		
		lightArray.add(l);
		l.unregisterAllInputProcessors();
		l.removeAllGestureEventListeners();
		l.registerInputProcessor(new ScaleProcessor(app));
		l.addGestureListener(ScaleProcessor.class, l);
		l.registerInputProcessor(new DragProcessor(app));
		l.addGestureListener(DragProcessor.class, l);
		
		this.getCanvas().addChild(l);
	}
	
	public Vector3D getLightLocation(int i){
		return new Vector3D(5+(Light.HEIGHT+5)*i+Light.HEIGHT/2+10,app.height-(Light.HEIGHT)+5);
	}
	
	public void removeLight(Light l){
		lightArray.remove(l);
		this.getCanvas().removeChild(l);
	}
	
	
	/**
	 * 
	 * @param e
	 */
	public void keyEvent(KeyEvent e){
		int evtID = e.getID();
		if (evtID != KeyEvent.KEY_PRESSED)
			return;
		switch (e.getKeyCode()){
		case KeyEvent.VK_BACK_SPACE:
			app.popScene();
			break;
			default:
				break;
		}
	}


}
