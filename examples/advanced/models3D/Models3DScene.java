package advanced.models3D;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.media.opengl.GL;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.MTLight;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor.ArcBallGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor.ArcballProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Tools3D;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.modelImporter.ModelImporterFactory;
import org.mt4j.util.opengl.GLMaterial;

public class Models3DScene extends AbstractScene {
	private MTApplication mtApp;
	
	//TODO switch button/wireframe

	public Models3DScene(MTApplication mtApplication, String name) {
		super(mtApplication, name);
		mtApp = mtApplication;
		
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
		
		//Make canvas zoomable
		this.getCanvas().registerInputProcessor(new ZoomProcessor(mtApp));
		this.getCanvas().addGestureListener(ZoomProcessor.class, new DefaultZoomAction());
		
		//Init light settings
		MTLight.enableLightningAndAmbient(mtApplication, 150, 150, 150, 255);
		//Create a light source //I think GL_LIGHT0 is used by processing!
		MTLight light = new MTLight(mtApplication, GL.GL_LIGHT3, new Vector3D(0,0,0));
		
		//Set up a material to react to the light
		GLMaterial material = new GLMaterial(Tools3D.getGL(mtApplication));
		material.setAmbient(new float[]{ .3f, .3f, .3f, 1f });
		material.setDiffuse(new float[]{ .9f, .9f, .9f, 1f } );
		material.setEmission(new float[]{ .0f, .0f, .0f, 1f });
		material.setSpecular(new float[]{ 1.0f, 1.0f, 1.0f, 1f });  // almost white: very reflective
		material.setShininess(110);// 0=no shine,  127=max shine
		
		//Group used to move to the screen center and to put the mesh group in
		MTComponent group1 = new MTComponent(mtApplication);
		
		//Create a group and set the light for the whole mesh group ->better for performance than setting light to more comps
		final MTComponent meshGroup = new MTComponent(mtApplication, "Mesh group");
		meshGroup.setLight(light);
		
		//Desired position for the meshes to appear at
		Vector3D destinationPosition = new Vector3D(mtApplication.width/2, mtApplication.height/2, 50);
		//Desired scale for the meshes
		float destinationScale = mtApplication.width*0.94f;

		//Load the meshes with the ModelImporterFactory (A file can contain more than 1 mesh)
		MTTriangleMesh[] meshes = ModelImporterFactory.loadModel(mtApplication, System.getProperty("user.dir") + File.separator + "examples" +  File.separator +"advanced"+ File.separator+ File.separator + "models3D"  + File.separator + "data" +  File.separator +
				"kentosaurus" + File.separator + "kentrosaurus.3ds", 180, true, false );
//				"trashbin.obj", 180, false, false );
		
		
		//Get the biggest mesh in the group to use as a reference for setting the position/scale
		final MTTriangleMesh biggestMesh = this.getBiggestMesh(meshes);
		
		Vector3D translationToScreenCenter = new Vector3D(destinationPosition);
		translationToScreenCenter.subtractLocal(biggestMesh.getCenterPointGlobal());
		
		Vector3D scalingPoint = new Vector3D(biggestMesh.getCenterPointGlobal());
		float biggestWidth = biggestMesh.getWidthXY(TransformSpace.GLOBAL);	
		float scale = destinationScale/biggestWidth;
		
		//Move the group the the desired position
		group1.scaleGlobal(scale, scale, scale, scalingPoint);
		group1.translateGlobal(translationToScreenCenter);
		this.getCanvas().addChild(group1);
		group1.addChild(meshGroup);
		
		for (int i = 0; i < meshes.length; i++) {
			MTTriangleMesh mesh = meshes[i];
			meshGroup.addChild(mesh);
			mesh.unregisterAllInputProcessors(); //Clear previously registered input processors
			mesh.setPickable(true);
			//If the mesh has more than 20 vertices, use a display list for faster rendering
			if (mesh.getVertexCount() > 20)
				mesh.generateAndUseDisplayLists();
			//Set the material to the mesh  (determines the reaction to the lightning)
			if (mesh.getMaterial() == null)
				mesh.setMaterial(material);
			mesh.setDrawNormals(false);
		}
		
		//Register arcball gesture manipulation to the whole mesh-group
		meshGroup.setComposite(true); //-> Group gets picked instead of its children
		meshGroup.registerInputProcessor(new ArcballProcessor(mtApplication, biggestMesh));
		meshGroup.addGestureListener(ArcballProcessor.class, new IGestureEventListener(){
			//@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				ArcBallGestureEvent aEvt =  (ArcBallGestureEvent)ge;
				meshGroup.transform(aEvt.getTransformationMatrix());
				return false;
			}
		});
		
		meshGroup.registerInputProcessor(new ScaleProcessor(mtApplication));
		meshGroup.addGestureListener(ScaleProcessor.class, new IGestureEventListener(){
			//@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				ScaleEvent se = (ScaleEvent)ge;
				meshGroup.scaleGlobal(se.getScaleFactorX(), se.getScaleFactorY(), se.getScaleFactorX(), biggestMesh.getCenterPointGlobal());
				return false;
			}
		});
		
		meshGroup.registerInputProcessor(new RotateProcessor(mtApplication));
		meshGroup.addGestureListener(RotateProcessor.class, new DefaultRotateAction());
	}

	
	public MTTriangleMesh getBiggestMesh(MTTriangleMesh[] meshes){
		MTTriangleMesh currentBiggestMesh = null;
		//Get the biggest mesh and extract its width
		float currentBiggestWidth = Float.MIN_VALUE;
		for (int i = 0; i < meshes.length; i++) {
			MTTriangleMesh triangleMesh = meshes[i];
			float width = triangleMesh.getWidthXY(TransformSpace.GLOBAL);
			if (width >= currentBiggestWidth || currentBiggestWidth == Float.MIN_VALUE){
				currentBiggestWidth = width;
				currentBiggestMesh = triangleMesh;
			}
		}
		return currentBiggestMesh;
	}
	
	
	
	//@Override
	public void init() {
		mtApp.registerKeyEvent(this);
	}

	//@Override
	public void shutDown() {
		mtApp.unregisterKeyEvent(this);
	}
	
	public void keyEvent(KeyEvent e){
		//System.out.println(e.getKeyCode());
		int evtID = e.getID();
		if (evtID != KeyEvent.KEY_PRESSED)
			return;
		switch (e.getKeyCode()){
		case KeyEvent.VK_F:
			System.out.println("FPS: " + mtApp.frameRate);
			break;
		case KeyEvent.VK_PLUS:
			this.getSceneCam().moveCamAndViewCenter(0, 0, -10);
			break;
		case KeyEvent.VK_MINUS:
			this.getSceneCam().moveCamAndViewCenter(0, 0, +10);
			break;
		case KeyEvent.VK_F12:
			getMTApplication().saveFrame(); //Screenshot
			break;
			default:
				break;
		}
	}


}
