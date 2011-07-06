/**
 * 
 */
package edu.poly.bxmc.betaville.module;

import org.apache.log4j.Logger;

import com.jme.input.MouseInput;
import com.jme.math.Plane;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.TranslatorMousePick;

/**
 * @author skyebook
 *
 */
public class TranslateModule extends Module implements GlobalSceneModule {
	private TranslatorMousePick pick;
	private static Logger logger = Logger.getLogger(TranslateModule.class);
	
	private boolean wasClickedPreviously=false;
	
	private int mouseXLastClick = -1;
	private int mouseYLastClick = -1;
	
	private int checkPick = -1;
	private final int xAxisPicked = 1;
	private final int yAxisPicked = 2;
	private final int zAxisPicked = 3;
	
	private static final Vector2f screenPosition = new Vector2f(0, 0);

	private static final Plane groundPlane = new Plane(Vector3f.UNIT_Y, 0f);
	private static Plane frontPlane;
	
	private static final Vector3f leftCollision = new Vector3f();
	private static final Vector3f rightCollision = new Vector3f();
	//private static final Vector3f topCollision = new Vector3f();
	//private static final Vector3f bottomCollision = new Vector3f();
	

	/**
	 * @param name
	 */
	public TranslateModule() {
		super("Translate Picker");
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#initialize(com.jme.scene.Node)
	 */
	public void initialize(Node scene) {
		pick = new TranslatorMousePick(SceneGameState.getInstance().getEditorWidgetNode());
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#onUpdate(com.jme.scene.Node, com.jme.math.Vector3f, com.jme.math.Vector3f)
	 */
	public void onUpdate(Node scene, Vector3f cameraLocation,
			Vector3f cameraDirection) {
		
		checkPick = pick.checkPick();
		//logger.info("checkPick: "+checkPick);
		if(checkPick != -1){
			
			if(!MouseInput.get().isButtonDown(0)) {
				
				// do the drag
				
				if(mouseXLastClick==-1 || mouseYLastClick==-1) {
					mouseXLastClick = MouseInput.get().getXAbsolute();
					mouseYLastClick = MouseInput.get().getYAbsolute();
					
					wasClickedPreviously = true;
					
				}
				else{
					
					if(wasClickedPreviously = true) {

						if(checkPick == xAxisPicked) {
							
							Vector3f diff = viewportWidthAtGround();
							Vector3f originalPos = SceneScape.getTargetSpatial().getLocalTranslation();
							SceneScape.getTargetSpatial().setLocalTranslation(originalPos.x+diff.x, originalPos.y, originalPos.z);
						
						}
						
						else if(checkPick == yAxisPicked) {

							Vector3f diff = viewportHeightAtGround();
							float diffY = diff.y * -0.003f;
							//logger.info("1: yAxis picked. " + SceneScape.getTargetSpatial().getLocalTranslation());
							//logger.info("diff: " + diff);
							Vector3f originalPos = SceneScape.getTargetSpatial().getLocalTranslation();
							SceneScape.getTargetSpatial().setLocalTranslation(originalPos.x, originalPos.y+diffY, originalPos.z);
							
							//logger.info("2: yAxis picked. " + SceneScape.getTargetSpatial().getLocalTranslation());
							
						}
						
						else if(checkPick == zAxisPicked) {
							Vector3f diff = viewportWidthAtGround();
							Vector3f originalPos = SceneScape.getTargetSpatial().getLocalTranslation();
							SceneScape.getTargetSpatial().setLocalTranslation(originalPos.x, originalPos.y, originalPos.z+diff.z);
							
						}
						
						wasClickedPreviously = false;
					}
					else {
						wasClickedPreviously = true;
					}
					
				}
				
			}
		}
		else {
			wasClickedPreviously = false;
		}

		mouseXLastClick = MouseInput.get().getXAbsolute();
		mouseYLastClick = MouseInput.get().getYAbsolute();
	}


	private Vector3f viewportWidthAtGround(){
		screenPosition.x=mouseXLastClick;
		screenPosition.y=mouseYLastClick;
		
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		Ray leftRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		leftRay.getDirection().normalizeLocal();
		
		leftRay.intersectsWherePlane(groundPlane, leftCollision);
		
		screenPosition.x=MouseInput.get().getXAbsolute();
		screenPosition.y=MouseInput.get().getYAbsolute();
		
		worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		
		Ray rightRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		rightRay.getDirection().normalizeLocal();
		
		rightRay.intersectsWherePlane(groundPlane, rightCollision);
		
		return rightCollision.subtract(leftCollision);
	}
	
	private Vector3f viewportHeightAtGround(){
		screenPosition.x=mouseXLastClick;
		screenPosition.y=mouseYLastClick;
		
		//System.out.println("Old Screen Position: " + screenPosition);
		
		// get the plane of the camera direction
		frontPlane = new Plane(new Vector3f(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().x, 0, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().z), 0f);

		//System.out.println("**" + DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().x + " " + DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().z);
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		
		//Ray topRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		//topRay.getDirection().normalizeLocal();
		
		//topRay.intersectsWherePlane(frontPlane, topCollision);
		
		screenPosition.x=MouseInput.get().getXAbsolute();
		screenPosition.y=MouseInput.get().getYAbsolute();
		
		//System.out.println("New Screen Position: " + screenPosition);
		
		Vector3f newWorldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		
		//Ray bottomRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		//bottomRay.getDirection().normalizeLocal();
		
		//bottomRay.intersectsWherePlane(frontPlane, bottomCollision);
		
		//System.out.println("Top Collision: " + topCollision + "     Bottom Collision: " + bottomCollision);
		//return bottomCollision.subtract(topCollision);
		
		return worldCoords.subtractLocal(newWorldCoords);
	}

	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
	 */
	public void deconstruct() {
		// TODO Auto-generated method stub

	}

}
