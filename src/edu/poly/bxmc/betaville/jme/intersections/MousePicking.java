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

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.jme.input.MouseInput;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * Handles the picking of geometry within the jME scene.
 * @author Skye Book
 *
 */
public class MousePicking {
	private static Logger logger = Logger.getLogger(MousePicking.class);
	private PickResults sceneResults = new TrianglePickResults();
	private PickResults terrainResults = new TrianglePickResults();
	private PickResults flagResults = new BoundingPickResults();
	private Node pickedDesign;
	private Node pickedTerrain;
	private Geometry selectedMesh;
	private float distanceToObject;
	private Ray rayToUse;
	
	/**
	 * 
	 */
	public MousePicking(Ray rayToUse){
		this.rayToUse=rayToUse;
		pickedDesign=null;
		Vector2f screenPosition = new Vector2f(MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		// Hard to tell which one is more accurate, so we'll stick with the status quo for now ^
		//Vector3f worldCoords = SceneGameState.getInstance().getCamera().getWorldCoordinates(screenPosition, 1.0f);
		rayToUse = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		
		rayToUse.getDirection().normalizeLocal();
		
		flagResults.setCheckDistance(true);
		flagResults.clear();
		sceneResults.setCheckDistance(true);
		sceneResults.clear();
		terrainResults.setCheckDistance(true);
		terrainResults.clear();
		SceneGameState.getInstance().getDesignNode().findPick(rayToUse, sceneResults);
		SceneGameState.getInstance().getTerrainNode().findPick(rayToUse, terrainResults);
		findFlagPicks(SceneGameState.getInstance().getFlagNode());

		if(!flagPicked() && sceneResults.getNumber()>0 && sceneResults.getPickData(0).getTargetTris().size()>0){
			selectedMesh = sceneResults.getPickData(0).getTargetMesh();
			pickedDesign = findSelectedDesignNode(sceneResults.getPickData(0).getTargetMesh());
			logger.debug(pickedDesign.getName() + " picked from designNode");
			distanceToObject = sceneResults.getPickData(0).getDistance();
		}
		
		if(terrainResults.getNumber()>0 && terrainResults.getPickData(0).getTargetTris().size()>0){
			pickedTerrain = findSelectedTerrainNode(terrainResults.getPickData(0).getTargetMesh());
			logger.debug(pickedTerrain.getName() + " picked from terrainNode");
		}
	}

	private void findFlagPicks(Spatial s){
		if(s instanceof SharedMesh){
			((SharedMesh)s).findPick(rayToUse, flagResults);
		}
		else if(s instanceof Node){
			if(((Node)s).getChildren()==null) return;
			Iterator<Spatial> it = ((Node)s).getChildren().iterator();
			while(it.hasNext()){
				findFlagPicks(it.next());
			}
		}
	}
	
	private Node findSelectedDesignNode(Spatial s){
		if(s.getParent().getName().equals("designNode")){
			return (Node)s;
		}
		else{
			return findSelectedDesignNode(s.getParent());
		}
	}
	
	private Node findSelectedTerrainNode(Spatial s){
		if(s.getParent().getName().equals("terrainNode")){
			return (Node)s;
		}
		else{
			return findSelectedTerrainNode(s.getParent());
		}
	}
	
	public Spatial getDesignFromFlag(){
		for(int i=0; i<flagResults.getNumber(); i++){
			if(flagResults.getPickData(i).getTargetMesh() instanceof SharedMesh){
				return SceneGameState.getInstance().getDesignNodeChild(flagResults.getPickData(i).getTargetMesh().getName());
			}
		}
		return null;
	}
	
	public String getNameOfClosestFlag(){
		return flagResults.getPickData(0).getTargetMesh().getName();
	}
	
	public boolean flagPicked(){
		if(flagResults.getNumber()>0){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * @return the distance, in jME units, from the camera to the selected object
	 */
	public float getDistanceToDesignObject() {
		return distanceToObject;
	}

	/**
	 * @return the selected mesh object, *not* the entire design node
	 */
	public Geometry getSelectedDesignMesh() {
		return selectedMesh;
	}

	/**
	 * @return the selected design's highest level node
	 */
	public Node getPickedDesignNode() {
		return pickedDesign;
	}

	/**
	 * @return the picked terrain node
	 */
	public Node getPickedTerrainNode() {
		return pickedTerrain;
	}
}