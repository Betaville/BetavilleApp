package edu.poly.bxmc.betaville.module;

import org.apache.log4j.Logger;

import com.jme.input.MouseInput;
import com.jme.math.Plane;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Arrow;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ResizeMousePick;
import edu.poly.bxmc.betaville.jme.intersections.TranslatorMousePick;

/**
 * 
 * @author Vivian(Hyun) Park
 *
 */
public class ResizeModule extends Module implements LocalSceneModule {
	private ResizeMousePick pick;
	private static Logger logger = Logger.getLogger(ResizeModule.class);
	
	private boolean wasClickedPreviously=false;
	
	private int mouseXLastClick = -1;
	private int mouseYLastClick = -1;
	
	private int mouseNewX = 0;
	private int mouseNewY = 0;

	private static final Vector2f screenPosition = new Vector2f(0, 0);

	private static final Plane groundPlane = new Plane(Vector3f.UNIT_Y, 0f);
	private static Plane frontPlane;
	
	private static final Vector3f leftCollision = new Vector3f();
	private static final Vector3f rightCollision = new Vector3f();
	
	private int checkPick = -1;
	private final int xBoxPicked = 1;
	private final int yBoxPicked = 2;
	private final int zBoxPicked = 3;

	/**
	 * @param name
	 */
	public ResizeModule() {
		super("Resize Picker");
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#initialize(com.jme.scene.Node)
	 */
	public void initialize(Node scene) {
		pick = new ResizeMousePick(SceneGameState.getInstance().getEditorWidgetNode());
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#onUpdate(com.jme.scene.Node, com.jme.math.Vector3f, com.jme.math.Vector3f)
	 */
	public void onUpdate(Node scene, Vector3f cameraLocation,
			Vector3f cameraDirection) {
		
		checkPick = pick.checkPick();
		
		if(checkPick != -1) {
			
			if(!MouseInput.get().isButtonDown(0)){
				if(mouseXLastClick == -1 || mouseYLastClick == -1) {
					mouseXLastClick = MouseInput.get().getXAbsolute();
					mouseYLastClick = MouseInput.get().getYAbsolute();
					
					wasClickedPreviously = true;
				}
				else {
					if(wasClickedPreviously = true) {
						if(checkPick == zBoxPicked) {
							final float originalZ = SceneScape.getTargetSpatial().getLocalScale().z;
							float ratio = (getLeftCollision().z / getRightCollision().z) * originalZ;
							
							System.out.println("z: " + SceneScape.getTargetSpatial().getLocalScale().z);
							System.out.println("ratio: " + ratio);
							SceneScape.getTargetSpatial().setLocalScale(new Vector3f(SceneScape.getTargetSpatial().getLocalScale().x, SceneScape.getTargetSpatial().getLocalScale().y, ratio));
						}
						else if(checkPick == xBoxPicked) {
							
							float ratio = 0;
							final float originalX = SceneScape.getTargetSpatial().getLocalScale().x;
							ratio = (getRightCollision().x / getLeftCollision().x) * originalX;
							System.out.println("x: " + SceneScape.getTargetSpatial().getLocalScale().x);
							System.out.println("ratio: " + ratio);
							SceneScape.getTargetSpatial().setLocalScale(new Vector3f(ratio, SceneScape.getTargetSpatial().getLocalScale().y, SceneScape.getTargetSpatial().getLocalScale().z));
						
							
						}
						else if(checkPick == yBoxPicked) {
							logger.info("y box picked");
							
						}
						
						wasClickedPreviously = false;
					}
					else {
						wasClickedPreviously = true;
					}
					
				}
				// get the new size of the builidng by subtracting two coordinates x and y
			}
		}
	}
	
	private Vector3f getLeftCollision(){
		screenPosition.x=mouseXLastClick;
		screenPosition.y=mouseYLastClick;
		
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		Ray leftRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		leftRay.getDirection().normalizeLocal();
		
		leftRay.intersectsWherePlane(groundPlane, leftCollision);
		
		System.out.println("left Collsion" + leftCollision);
		return leftCollision;
	}
	
	private Vector3f getRightCollision(){
		
		screenPosition.x=MouseInput.get().getXAbsolute();
		screenPosition.y=MouseInput.get().getYAbsolute();
		
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		
		Ray rightRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		rightRay.getDirection().normalizeLocal();
		
		rightRay.intersectsWherePlane(groundPlane, rightCollision);
		
		System.out.println("right Collision" + rightCollision);
		return rightCollision;
		
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
	 */
	public void deconstruct() {
		// TODO Auto-generated method stub

	}

}
