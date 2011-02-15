/** Copyright (c) 2008-2010, Brooklyn eXperimental Media Center
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Brooklyn eXperimental Media Center nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Brooklyn eXperimental Media Center BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package edu.poly.bxmc.betaville.jme.intersections;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.intersection.PickData;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * Class <AxisZPickResults> - Manage the collision on the Z axis
 *
 * @author Caroline Bouchat
 * @version 0.1 - Spring 2009
 */
public class AxisZPickResults extends AxisPickResults {
	/**
	 * Constant <LOGGER> - LOGGER is used internally by jME to write log
	 * messages to standard error output.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(AxisZPickResults.class.getName());

	/**
	 * Constructor - Instantiate the class
	 * 
	 * @param worldGameState
	 *            The game state
	 */
	public AxisZPickResults(SceneGameState sceneGameState) {
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
				float diff = getGameState().getCamera().getLocation().getZ()
						- loc.z;
				// If the camera if to close of the wall
				if (Math.abs(diff) < DIST_WALL) {
					if (diff >= 0)
						getGameState().getCamera().getLocation().setZ(
								loc.z + DIST_WALL);
					else
						getGameState().getCamera().getLocation().setZ(
								loc.z - DIST_WALL);
					getGameState().getCamera().update();
				}
			} else {
				LOGGER.info("No triangles");
			}
		}
	}
}
