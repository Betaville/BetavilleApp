/** Copyright (c) 2008-2011, Brooklyn eXperimental Media Center
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

import org.apache.log4j.Logger;

import com.jme.input.MouseInput;
import com.jme.intersection.PickResults;
import com.jme.intersection.TriangleCollisionResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * @author Skye Book
 *
 */

public class VertexMousePick {
	private static Logger logger = Logger.getLogger(VertexMousePick.class);

	private PickResults triangleResults = new TrianglePickResults();
	private Ray rayToUse;

	private Spatial spatialToTest;

	private Vector3f[] triangleVertices;

	private Vector3f[] rawScreenCoordinates;
	private Vector2f[] screenCoordinates;
	
	private ArrayList<Integer> temp = new ArrayList<Integer>();

	public VertexMousePick(){
		triangleVertices = new Vector3f[3];
		rawScreenCoordinates = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f()};
		screenCoordinates = new Vector2f[]{new Vector2f(), new Vector2f(), new Vector2f()};
	}

	public void setSpatial(Spatial spatialToTest){
		this.spatialToTest = spatialToTest;
	}

	public Vector3f checkPick(){
		Vector2f screenPosition = new Vector2f(MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		rayToUse = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		rayToUse.getDirection().normalizeLocal();

		triangleResults.clear();
		triangleResults.setCheckDistance(true);

		spatialToTest.findPick(rayToUse, triangleResults);

		if(triangleResults.getNumber()>0){
			
			logger.info("Picked " + triangleResults.getNumber());
			
			for(int i=0; i<triangleResults.getNumber(); i++){
				Geometry widget = triangleResults.getPickData(i).getTargetMesh();

				if(widget instanceof TriMesh){
					
					logger.info("Target is a trimesh");
					
					((TriMesh)widget).findTrianglePick(rayToUse, temp);
					logger.info("temp has " + temp.size());
					

					// have we picked a triangle?
					if(temp.size()>0){
						
						// get the vertices that make up the selected triangle
						((TriMesh)widget).getTriangle(temp.get(0), triangleVertices);
						
						for(Vector3f v : triangleVertices){
							logger.info("VERTEX:\t" + v.x+"\t"+v.y+"\t"+v.z);
						}

						// find the closest vertex based on the screen position of each of the vertices
						DisplaySystem.getDisplaySystem().getScreenCoordinates(triangleVertices[0], rawScreenCoordinates[0]);
						DisplaySystem.getDisplaySystem().getScreenCoordinates(triangleVertices[1], rawScreenCoordinates[1]);
						DisplaySystem.getDisplaySystem().getScreenCoordinates(triangleVertices[2], rawScreenCoordinates[2]);

						screenCoordinates[0].x=rawScreenCoordinates[0].x;
						screenCoordinates[0].y=rawScreenCoordinates[0].y;
						screenCoordinates[1].x=rawScreenCoordinates[1].x;
						screenCoordinates[1].y=rawScreenCoordinates[1].y;
						screenCoordinates[2].x=rawScreenCoordinates[2].x;
						screenCoordinates[2].y=rawScreenCoordinates[2].y;
						
						logger.info("Screen:\t" + screenPosition.x+"\t"+screenPosition.y);
						
						for(Vector2f v : screenCoordinates){
							logger.info("Triangle:\t" + v.x+"\t"+v.y);
						}

						float distance0 = screenPosition.distance(screenCoordinates[0]);
						float distance1 = screenPosition.distance(screenCoordinates[1]);
						float distance2 = screenPosition.distance(screenCoordinates[2]);
						

						if(distance0 < distance1 && distance0 < distance2) return rawScreenCoordinates[0];
						if(distance1 < distance0 && distance1 < distance2) return rawScreenCoordinates[1];
						if(distance2 < distance0 && distance2 < distance1) return rawScreenCoordinates[2];					
					}
				}
			}
		}


		return null;

	}

}
