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

import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;

/**
 * @author Skye Book
 *
 */
public class StaticTerrainCollisionCheck implements ITerrainCollisionCheck {
	private Camera camera;
	private Ray downY;
	private TrianglePickResults pr;

	/**
	 * 
	 */
	public StaticTerrainCollisionCheck(Camera cam){
		camera=cam;
		downY = new Ray(camera.getLocation(), new Vector3f(0,-1,0));
		pr = new TrianglePickResults();
		pr.setCheckDistance(true);
	}
	public boolean testTerrain(Node terrainNode, float tolerance, Vector3f currentCamLocation) {
		currentCamLocation = camera.getLocation();
		downY.setOrigin(currentCamLocation);
		terrainNode.findPick(downY, pr);
		if(pr.getNumber()>0){
			// get the data of the picked triangle
			TriMesh target = ((TriMesh)pr.getPickData(0).getTargetMesh());
			Vector3f[] indices = new Vector3f[3];
			target.getTriangle(pr.getPickData(0).getTargetTris().get(0), indices);
			
			// find the highest point of the triangle on the y-axis
			float highestY=0;
			for(Vector3f vertex : indices){
				if(vertex.y>highestY) highestY=vertex.y;
			}
			
			if(currentCamLocation.getY()<highestY){
				camera.setLocation(new Vector3f(currentCamLocation.x, highestY, currentCamLocation.z));
				return true;
			}
		}
		return false;
	}
}
