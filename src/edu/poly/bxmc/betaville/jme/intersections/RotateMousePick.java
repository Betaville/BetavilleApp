package edu.poly.bxmc.betaville.jme.intersections;

import org.apache.log4j.Logger;

import com.jme.input.MouseInput;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Tube;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * @author Vivian Park
 *
 */

public class RotateMousePick {

	private static Logger logger = Logger.getLogger(RotateMousePick.class);
	private PickResults widgetResults = new TrianglePickResults();
	private Ray rayToUse;
	
	private Spatial spatialToTest;
	
	private final int horizontalRotateTubePicked = 1;
	private final int verticalRotateTubePicked = 2;
	private final int anotherRotateTubePicked = 3;
	
	public RotateMousePick(Spatial spatialToTest) {
		
		this.spatialToTest=spatialToTest;
		
		widgetResults = new BoundingPickResults();
		widgetResults.setCheckDistance(true);
	}
	
	public int checkPick(){
		//logger.info("checkpick called");
		
		Vector2f screenPosition = new Vector2f(MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		rayToUse = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		rayToUse.getDirection().normalizeLocal();
		
		widgetResults.clear();
		
		spatialToTest.findPick(rayToUse, widgetResults);
		
		//logger.info("Results: " + widgetResults.getNumber());
		
		if(widgetResults.getNumber()>0){
			Geometry widget = widgetResults.getPickData(0).getTargetMesh();
			//logger.info("widget results.getnumber");
			if(widget instanceof Tube) {
				//logger.info("widget is an instance of tube");
				if(widget.getName().equals("$editorWidget-horizontalRotateTube")) {
					//logger.info("widget name is horizontal rotate tube");
					return horizontalRotateTubePicked;
				}
				else if (widget.getName().equals("$editorWidget-verticalRotateTube")) {
					//logger.info("widget name is vertical rotate tube");
					return verticalRotateTubePicked;
				}
				else if (widget.getName().equals("$editorWidget-anotherRotateTube")) {
					return anotherRotateTubePicked;
				}
				
			}
		}
	
		return -1;
	}

}
