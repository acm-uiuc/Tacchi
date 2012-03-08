package basic.svgExample;

import java.io.File;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;


public class SVGScene extends AbstractScene {

	public SVGScene(MTApplication mtApplication, String name) {
		super(mtApplication, name);
		
		this.setClearColor(new MTColor(255, 255, 255, 255));
		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		MTSvg svg = new MTSvg(mtApplication,
				System.getProperty("user.dir")+File.separator + "examples"+File.separator +"basic"+ File.separator + "svgExample"+ File.separator + "data" + File.separator + 
				"windmill.svg");
		svg.setPositionGlobal(new Vector3D(mtApplication.width/2, mtApplication.height/2,0));
		this.getCanvas().addChild(svg);
		
		MTSvg butterFly = new MTSvg(mtApplication, System.getProperty("user.dir")+File.separator + "examples"+ File.separator +"basic"+ File.separator +"svgExample"+ File.separator + "data" + File.separator + 
				"butterfly.svg");
		butterFly.setPositionGlobal(new Vector3D(300, 100,0));
		this.getCanvas().addChild(butterFly);
		
		this.getCanvas().addChild(new MTSvg(mtApplication,System.getProperty("user.dir")+File.separator + "examples"+ File.separator +"basic"+ File.separator +"svgExample"+ File.separator + "data" + File.separator + 
				"primitives.svg"));
	}

	
	@Override
	public void init() {

	}

	@Override
	public void shutDown() {

	}

}
