package edu.poly.bxmc.betaville.module;

import org.apache.log4j.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.input.MouseInput;
import com.jme.math.LineSegment;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.jme.intersections.RotateMousePick;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ModeledDesign;

/**
 * @author Vivian Park
 *
 */

public class RotateModule extends Module implements GlobalSceneModule {
	private boolean wasClickedPreviously=false;
	
	private RotateMousePick pick;
	private static Logger logger = Logger.getLogger(RotateModule.class);
	
	private static final Vector2f screenPosition = new Vector2f(0, 0);
	private static final Vector2f newScreenPosition = new Vector2f(0, 0);
	
	private int checkPick = -1;
	private int newVerticalRotation = -1;
	private int newHorizontalRotation = -1;
	private int newAnotherRotation = -1;
	private int mouseXLastClick = -1;
	private int mouseYLastClick = -1;
	private final int horizontalRotateTubePicked = 1;
	private final int verticalRotateTubePicked = 2;
	private final int anotherRotateTubePicked = 3;
	
	private LineSegment firstLine;
	private LineSegment secondLine;
	
	public RotateModule() {
		super("Rotate Picker");
	}

	public void initialize(Node scene) {
		pick = new RotateMousePick(SceneGameState.getInstance().getEditorWidgetNode());

		SceneScape.addSelectionListener(new ISpatialSelectionListener() {
			public void selectionCleared(Design previousDesign) {
				
				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();
				
				wasClickedPreviously = false;
				newHorizontalRotation = -1;
				newVerticalRotation = -1;
				newAnotherRotation = -1;
			}
			
			public void designSelected(Spatial spatial, Design design) {

				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();
				
				wasClickedPreviously = false;
				newHorizontalRotation = -1;
				newVerticalRotation = -1;
				newAnotherRotation = -1;
			}
		});
	}

	public void onUpdate(Node scene, Vector3f cameraLocation,
			Vector3f cameraDirection) {
		
		checkPick = pick.checkPick();
		
		if(checkPick != -1) {
			if(!MouseInput.get().isButtonDown(0)) {
				if(mouseXLastClick == -1 || mouseYLastClick == -1) {
					mouseXLastClick = MouseInput.get().getXAbsolute();
					mouseYLastClick = MouseInput.get().getYAbsolute();
					
					wasClickedPreviously = true;
				}
				else {
					if(wasClickedPreviously = true) {
						if(checkPick == horizontalRotateTubePicked) {
							if(newHorizontalRotation == -1) {
								newHorizontalRotation = (int)calculateRotation();
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleY(newHorizontalRotation));
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getPickedDesign()).getRotationX(), newHorizontalRotation, ((ModeledDesign)SceneScape.getPickedDesign()).getRotationZ()));
								((ModeledDesign)SceneScape.getPickedDesign()).setRotationY(newHorizontalRotation);
							}
							else {
								newHorizontalRotation += (int)calculateRotation();
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleY(newHorizontalRotation));
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getPickedDesign()).getRotationX(), newHorizontalRotation, ((ModeledDesign)SceneScape.getPickedDesign()).getRotationZ()));
								((ModeledDesign)SceneScape.getPickedDesign()).setRotationY(newHorizontalRotation);
							}
							
						}
							
						else if(checkPick == verticalRotateTubePicked) {
							//logger.info("Vertical Rotate Tube picked");
							if(newVerticalRotation == -1) {
								newVerticalRotation = (int)calculateRotation();
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleZ(newVerticalRotation));
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getPickedDesign()).getRotationX(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY(), newVerticalRotation));
								((ModeledDesign)SceneScape.getPickedDesign()).setRotationZ(newVerticalRotation);
							}
							else {
								newVerticalRotation += (int)calculateRotation();
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleZ(newVerticalRotation));
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getPickedDesign()).getRotationX(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY(), newVerticalRotation));
								((ModeledDesign)SceneScape.getPickedDesign()).setRotationZ(newVerticalRotation);
							}
						}
						
						else if(checkPick == anotherRotateTubePicked) {
							//logger.info("Vertical Rotate Tube picked");
							if(newAnotherRotation == -1) {
								newAnotherRotation = (int)calculateRotation();
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleX(newAnotherRotation));
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(newAnotherRotation, ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationX()));
								((ModeledDesign)SceneScape.getPickedDesign()).setRotationX(newAnotherRotation);
							}
							else {
								newAnotherRotation += (int)calculateRotation();
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleX(newAnotherRotation));
								SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(newAnotherRotation, ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationX()));
								((ModeledDesign)SceneScape.getPickedDesign()).setRotationX(newAnotherRotation);
							}
						}
						
						wasClickedPreviously = false;
					}
					
					else{
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
	
	public float calculateRotation() {
		BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
		Vector3f center = bb.getCenter();
		
		screenPosition.x=mouseXLastClick;
		screenPosition.y=mouseYLastClick;
		
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		firstLine = new LineSegment(center, worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		
		newScreenPosition.x=MouseInput.get().getXAbsolute();
		newScreenPosition.y=MouseInput.get().getYAbsolute();
		
		Vector3f newWorldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(newScreenPosition, 1.0f);
		secondLine = new LineSegment(center, newWorldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		
		Vector3f firstVector = firstLine.getDirection();
		Vector3f secondVector = secondLine.getDirection();
		
		float angleInDegrees = (float) (firstVector.angleBetween(secondVector) * (180/Math.PI));
		
		//System.out.println("angle in degrees: " + angleInDegrees);
		
		//if the second line is to the right of second line
		
		//secondLine.getDirection().x > firstLine.getDirection()
		if(newScreenPosition.x < screenPosition.x || newScreenPosition.y > screenPosition.y)
			return -angleInDegrees;
		
		return angleInDegrees;
		//return leftVector.angleBetween(rightVector);
		
	}
	

	public void deconstruct() {
		// TODO Auto-generated method stub

	}

}
