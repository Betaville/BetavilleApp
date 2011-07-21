package edu.poly.bxmc.betaville.module;

import org.apache.log4j.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.input.MouseInput;
import com.jme.math.Plane;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Arrow;
import com.jme.scene.shape.AxisRods;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.panel.EditBuildingWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ResizeMousePick;

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
	
	private static final Vector2f screenPosition = new Vector2f(0, 0);
	private static final Vector2f newScreenPosition = new Vector2f(0, 0);

	private static final Plane groundPlane = new Plane(Vector3f.UNIT_Y, 0f);
	private static Plane frontPlane;
	
	private static final Vector3f leftCollision = new Vector3f();
	private static final Vector3f rightCollision = new Vector3f();
	
	private int checkPick = -1;
	private final int xBoxPicked = 1;
	private final int yBoxPicked = 2;
	private final int zBoxPicked = 3;
	
	private Vector3f worldCoords = new Vector3f(0, 0, 0);
	private Vector3f newWorldCoords = new Vector3f(0, 0, 0);

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
						if(checkPick == xBoxPicked) {
							BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
							final float originalX = bb.xExtent * 100;
							
							float distanceFromOldPos = SceneScape.getTargetSpatial().getWorldTranslation().x - worldCoords.x;
							float distanceFromNewPos = SceneScape.getTargetSpatial().getWorldTranslation().x - newWorldCoords.x;
							
							if(distanceFromNewPos < distanceFromOldPos)
							{
								float newX = ((originalX + Math.abs(calculateXZdifference().x)) / originalX) * SceneScape.getTargetSpatial().getLocalScale().x;
								SceneScape.getTargetSpatial().setLocalScale(new Vector3f(newX, SceneScape.getTargetSpatial().getLocalScale().y, SceneScape.getTargetSpatial().getLocalScale().z));
							
							}
							else
							{
								float newX = ((originalX - Math.abs(calculateXZdifference().x)) / originalX) * SceneScape.getTargetSpatial().getLocalScale().x;
								SceneScape.getTargetSpatial().setLocalScale(new Vector3f(newX, SceneScape.getTargetSpatial().getLocalScale().y, SceneScape.getTargetSpatial().getLocalScale().z));
							
							}
							
						}
						else if(checkPick == yBoxPicked) {
							BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
							final float originalY = bb.xExtent * 100;
							
							if(screenPosition.y < newScreenPosition.y)
							{
								float newY = ((originalY + Math.abs(calculateYdifference().y * 0.05f)) / originalY) * SceneScape.getTargetSpatial().getLocalScale().y;
								SceneScape.getTargetSpatial().setLocalScale(new Vector3f(SceneScape.getTargetSpatial().getLocalScale().x, newY, SceneScape.getTargetSpatial().getLocalScale().z));
							
							}
							else
							{
								float newY = ((originalY - Math.abs(calculateYdifference().y * 0.05f)) / originalY) * SceneScape.getTargetSpatial().getLocalScale().y;
								SceneScape.getTargetSpatial().setLocalScale(new Vector3f(SceneScape.getTargetSpatial().getLocalScale().x, newY, SceneScape.getTargetSpatial().getLocalScale().z));
							
							}
							
						}
						else if(checkPick == zBoxPicked) {
							BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
							final float originalZ = bb.zExtent * 100;
							
							float distanceFromOldPos = SceneScape.getTargetSpatial().getWorldTranslation().z - worldCoords.z;
							float distanceFromNewPos = SceneScape.getTargetSpatial().getWorldTranslation().z - newWorldCoords.z;
							
							if(distanceFromNewPos < distanceFromOldPos)
							{
								float newZ = ((originalZ + Math.abs(calculateXZdifference().z)) / originalZ) * SceneScape.getTargetSpatial().getLocalScale().z;
								SceneScape.getTargetSpatial().setLocalScale(new Vector3f(SceneScape.getTargetSpatial().getLocalScale().x, SceneScape.getTargetSpatial().getLocalScale().y, newZ));
								}
							else
							{
								float newZ = ((originalZ - Math.abs(calculateXZdifference().z)) / originalZ) * SceneScape.getTargetSpatial().getLocalScale().z;
								SceneScape.getTargetSpatial().setLocalScale(new Vector3f(SceneScape.getTargetSpatial().getLocalScale().x, SceneScape.getTargetSpatial().getLocalScale().y, newZ));
							}
							
						}
						
						wasClickedPreviously = false;
						
						BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
						float distance = (float)(Math.sqrt(Math.pow(Math.sqrt(Math.pow(bb.xExtent, 2) + Math.pow(bb.yExtent, 2)), 2) + Math.pow(bb.zExtent, 2)));
						
						((AxisRods)SceneGameState.getInstance().getEditorWidgetNode().getChild("$editorWidget-scaleRod")).updateGeometry(distance, distance * 0.01f, true);
						
					}
					
					
					else {
						wasClickedPreviously = true;
					}
					
				}
			}
		}
		mouseXLastClick = MouseInput.get().getXAbsolute();
		mouseYLastClick = MouseInput.get().getYAbsolute();
	}
	
	private Vector3f calculateXZdifference(){
		screenPosition.x=mouseXLastClick;
		screenPosition.y=mouseYLastClick;
		
		worldCoords = new Vector3f(0, 0, 0);
		worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		Ray leftRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		leftRay.getDirection().normalizeLocal();
		
		leftRay.intersectsWherePlane(groundPlane, leftCollision);
		
		newScreenPosition.x=MouseInput.get().getXAbsolute();
		newScreenPosition.y=MouseInput.get().getYAbsolute();
		
		newWorldCoords = new Vector3f(0, 0, 0);
		newWorldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(newScreenPosition, 1.0f);
		
		Ray rightRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), newWorldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		rightRay.getDirection().normalizeLocal();
		
		rightRay.intersectsWherePlane(groundPlane, rightCollision);
		
		return leftCollision.subtract(rightCollision);
	}
	
	private Vector3f calculateYdifference(){
		screenPosition.x=mouseXLastClick;
		screenPosition.y=mouseYLastClick;
		
		frontPlane = new Plane(new Vector3f(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().x, 0, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().z), 0f);

		worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		
		newScreenPosition.x=MouseInput.get().getXAbsolute();
		newScreenPosition.y=MouseInput.get().getYAbsolute();
		
		newWorldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(newScreenPosition, 1.0f);
		
		return worldCoords.subtractLocal(newWorldCoords);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
	 */
	public void deconstruct() {
		// TODO Auto-generated method stub

	}

}
