package basic.circularMenuTest;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.junit.Test;
import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTOverlayContainer;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.menus.MTCircularButton;
import org.mt4j.components.visibleComponents.widgets.menus.MTCircularItem;
import org.mt4j.components.visibleComponents.widgets.menus.MTCircularMenu;
import org.mt4j.components.visibleComponents.widgets.menus.MTCircularSVGItem;
import org.mt4j.components.visibleComponents.widgets.menus.MTGLButton;
import org.mt4j.components.visibleComponents.widgets.menus.MTCircularItem.States;
import org.mt4j.input.gestureAction.DefaultArcballAction;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultLassoAction;
import org.mt4j.input.gestureAction.DefaultPanAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor.ArcballProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.lassoProcessor.LassoProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.panProcessor.PanProcessorTwoFingers;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import basic.scenes.Scene2;

public class CircularMenuTest extends AbstractScene {
	private MTApplication app;
	
	MTOverlayContainer overlaymenu;
	MTCircularSVGItem circularitem;
	
	MTCircularButton[] circularGLitems;
	MTCircularItem circularGLitem;
	MTCircularButton[] circularGLChildren;
	
	public CircularMenuTest(MTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		this.setClearColor(new MTColor(126, 130, 168, 255));
		this.registerGlobalInputProcessor(new CursorTracer(app, this));

		
		
		
		createOverlay(mtApplication);
		createCircularGLItem(mtApplication);
		//createCircularGLMenu(mtApplication);
		
		tests();
	}
	
	public void tests() {
		testInitialState();
		testCircularButtonClick();
		testVisibleChildren();
		testChildrenShape();
	}


	private void createOverlay(MTApplication mtApplication) {
		//Tap gesture
		overlaymenu = new MTOverlayContainer(mtApplication, "asdf");
		this.getCanvas().addChild(overlaymenu);
		overlaymenu.translate(new Vector3D(130,130));
		overlaymenu.scale(2, 2, 2, Vector3D.Z_AXIS);
	}
	
	private void createCircularGLItem(MTApplication mtApplication) {

		circularGLChildren = generateButtons(mtApplication, 7);
		MTCircularButton[] circularGLChildren2 = generateButtons(mtApplication, 7);
		MTCircularButton[] circularGLChildrenChildren = generateButtons(mtApplication, 7);
		circularGLitems = generateButtons(mtApplication, 7);
		circularGLitems[3].setChildren(circularGLChildren);
		circularGLitems[1].setChildren(circularGLChildren2);
		circularGLitems[4].setText("Tap me! ---");
		circularGLitems[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Something!! "+ae.getID());

				switch (ae.getID()) {
					case TapEvent.BUTTON_DOWN:
						System.out.println("Tapped!!");
						if (circularGLitems[4].getText().endsWith("--"))
							circularGLitems[4].setText("Tap me! -|-");
						else
							circularGLitems[4].setText("Tap me! ---");	
						break;
				}
			}
		});
		

		circularGLChildren2[3].setChildren(circularGLChildrenChildren);
		//Tap gesture
		circularGLitem = new MTCircularItem(mtApplication, null, circularGLitems, 20f, 110f, 30f);
		//this.clearAllGestures(tapOnly);
		//circularGLitem.setSizeXYRelativeToParent(100, 30);
		overlaymenu.addChild(circularGLitem);
		//circularGLitem.setState(States.PARTIAL);
		//circularGLitem.setPositionRelativeToParent(new Vector3D(50,50));
	}
	
	private MTCircularButton[] generateButtons(MTApplication m, int num) {
		MTCircularButton[] buttons = new MTCircularButton[num];
		for (int i=0; i<num; i++) {
			buttons[i] = new MTCircularButton(m);
			buttons[i].setText("Testing.."+i);
		}
		return buttons;
	}
	
	private void createCircularGLMenu(MTApplication mtApplication) {
		
		MTCircularItem circularGLChildrenChildren  [] = new MTCircularItem[] {new MTCircularItem(mtApplication), new MTCircularItem(mtApplication), new MTCircularItem(mtApplication),
				new MTCircularItem(mtApplication), new MTCircularItem(mtApplication), new MTCircularItem(mtApplication),
				new MTCircularItem(mtApplication)};

		
		MTCircularItem circularGLChildren  [] = new MTCircularItem[] {new MTCircularItem(mtApplication), new MTCircularItem(mtApplication), new MTCircularItem(mtApplication),
				new MTCircularItem(mtApplication), new MTCircularItem(mtApplication, circularGLChildrenChildren), new MTCircularItem(mtApplication),
				new MTCircularItem(mtApplication)};

		//circularGLitems = new MTCircularItem[] {new MTCircularItem(mtApplication,circularGLChildren), new MTCircularItem(mtApplication), new MTCircularItem(mtApplication),
		//		new MTCircularItem(mtApplication), new MTCircularItem(mtApplication), new MTCircularItem(mtApplication),
		//		new MTCircularItem(mtApplication), new MTCircularItem(mtApplication), new MTCircularItem(mtApplication)};
		//Tap gesture
		MTCircularMenu circularGLmenu = new MTCircularMenu(mtApplication,"", circularGLitems);
		//this.clearAllGestures(tapOnly);
		//circularGLmenu.setSizeXYRelativeToParent(200, 200);
		overlaymenu.addChild(circularGLmenu);
		circularGLitem.setPositionRelativeToParent(new Vector3D(200,0));
	}
	
	
	
	@Test
	public void testCircularButtonClick() {
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));
		assertTrue(circularGLitem.isSelected() == true);
		for (MTGLButton m : circularGLitems) {
			assertTrue(m.isVisible() == true);
		}
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));
		assertTrue(circularGLitem.isSelected() == false);
		for (MTGLButton m : circularGLitems) {
			assertTrue(m.isVisible() == false);
		}

	}
	
	@Test
	public void testVisibleChildren() {
		
		assertEquals(circularGLitem.countVisibleItems(), 1);
		
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));

		assertEquals(circularGLitem.countVisibleItems(), 8);
		
		circularGLitems[3].processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitems[3].processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));
		
		assertEquals(circularGLitem.countVisibleItems(), 15);
		
		circularGLitems[3].processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitems[3].processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));

	
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));

		assertEquals(circularGLitem.countVisibleItems(), 1);
	}
	
	@Test
	public void testChildrenShape() {
		
		
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));

		assertEquals(circularGLitems[1].arcWidth, 360f/7f);
		
		circularGLitems[3].processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitems[3].processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));
		
		assertEquals(circularGLitems[1].arcWidth, 360f/14f);
		
		circularGLitems[3].processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitems[3].processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));

	
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_DETECTED, null, null, null, 0));
		circularGLitem.processGestureEvent(new TapEvent(null, MTGestureEvent.GESTURE_ENDED, null, null, null, 0));

	}
	
	
	
	@Test
	public void testInitialState() {
		assertTrue(circularGLitem.getState() == MTCircularItem.States.FULL);

	}
	
	
	
	
	@Override
	public void init() {

	}

	@Override
	public void shutDown() {

	}

}
