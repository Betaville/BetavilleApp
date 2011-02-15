/**
 * Copyright 2008-2009 Brooklyn eXperimental Media Center
 * Betaville Project by Brooklyn eXperimental Media Center at NYU-Poly
 * http://bxmc.poly.edu
 */
package edu.poly.bxmc.betaville.jme.intersections;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.intersection.PickData;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * Class <AxisXPickResults> - Manage the collision on the X axis
 *
 * @author Caroline Bouchat
 * @version 0.1 - Spring 2009
 */
public class AxisXPickResults extends AxisPickResults {
	/**
	 * Constant <LOGGER> - LOGGER is used internally by jME to write log
	 * messages to standard error output.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(AxisXPickResults.class.getName());
	
	/**
	 * Constructor - Instantiate the class
	 *
	 * @param worldGameState The game state
	 */
	public AxisXPickResults(SceneGameState sceneGameState) {
		super(sceneGameState);
	}

	/* (non-Javadoc)
	 * @see edu.poly.idmi.betaville.jme.intersection.AxisPickResults#processPick()
	 */
	public void processPick() {
		// If there are collision
		if (getNumber() > 0) {
			PickData pData = getPickData(0);
			ArrayList<Integer> tris = pData.getTargetTris();
			TriMesh mesh = (TriMesh) pData.getTargetMesh();
			
			if (tris.size() > 0) {
				int triIndex = ((Integer) tris.get(0)).intValue();
				Vector3f[] vec = new Vector3f[3];
				mesh.getTriangle(triIndex, vec);
				for (int x = 0; x < vec.length; x++) {
					vec[x].multLocal(mesh.getWorldScale());
					mesh.getWorldRotation().mult(vec[x], vec[x]);
					vec[x].addLocal(mesh.getWorldTranslation());
				}

				Vector3f loc = new Vector3f();
				pData.getRay().intersectWhere(vec[0], vec[1], vec[2], loc);
				// Calculate the distance between the wall and the camera
				float diff = getGameState().getCamera().getLocation().getX()
						- loc.x;
				// If the camera if to close of the wall
				if (Math.abs(diff) < DIST_WALL) {
					if (diff >= 0)
						getGameState().getCamera().getLocation().setX(
								loc.x + DIST_WALL);
					else
						getGameState().getCamera().getLocation().setX(
								loc.x - DIST_WALL);
					getGameState().getCamera().update();
				}
			} else {
				LOGGER.info("No triangles");
			}
		}
	}
}
