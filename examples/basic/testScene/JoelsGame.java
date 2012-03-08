package basic.testScene;
import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import TUIO.TuioCursor;

import processing.core.PGraphics;

import tacchi.particlegame.Game;



public class JoelsGame extends AbstractScene {
	Game g;
	MTApplication app;
	public JoelsGame(MTApplication mtApplication, String name) {
		super(mtApplication, name);
		
		MTColor white = new MTColor(255,255,255);
		//this.setClearColor(new MTColor(146, 150, 188, 255));
		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		
		
		
		
		if (!MT4jSettings.getInstance().isOpenGlMode()){
			System.err.println("Scene only usable when using the OpenGL renderer! - See settings.txt");
        	return;
        }

		
		g = new Game(mtApplication);
		
		
		
        this.getCanvas().addInputListener(new IMTInputEventListener() {
        	//@Override
        	public boolean processInputEvent(MTInputEvent inEvt){
        		if(inEvt instanceof AbstractCursorInputEvt){
        			AbstractCursorInputEvt posEvt = (AbstractCursorInputEvt)inEvt;
        			if (posEvt.hasTarget() && posEvt.getTargetComponent().equals(getCanvas())){
        				int id = posEvt.getId();
        				if (id == posEvt.INPUT_DETECTED) {
        					g.addTuioCursor(posEvt.getCursor());
        				}
        				else if (id == posEvt.INPUT_UPDATED){
        					g.refresh();
        				}
        				else {
        					g.removeTuioCursor(posEvt.getCursor());
        				}
        				//System.out.println("Uhm..."+ posEvt.getCursor().getId() + "   ");
        			}
        		}
        		return false;
        	}
		});

		
		//this.getCanvas().addChild(g);
		
		app = mtApplication;
        this.getCanvas().setDepthBufferDisabled(true);
		g.setup(app.g);
		System.out.println("Size of screen: "+app.width + "," + app.height);

		
	}
	@Override
	public void init() {
	}
	
	@Override
	public void drawAndUpdate(PGraphics graphics, long timeDelta) {
		super.drawAndUpdate(graphics, timeDelta);
		if (g.isSetup())
			g.draw(graphics);
		g.width = app.width;
		g.height = app.height;
	}
	
	@Override
	public void shutDown() {}
}
