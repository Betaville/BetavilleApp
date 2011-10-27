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

import com.jme.intersection.PickResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.model.Design;

/**
 * Utilities for performing picking operations in the scene
 * @author Skye Book
 *
 */
public class PickUtils {
	
	/**
	 * Picks the design closest to the camera from this position
	 * @param x The X location on the screen to pick from
	 * @param y The Y location on the screen to pick from
	 * @return The picked {@link Design}, or null if none was picked
	 */
	public static Design pickDesignAtScreenLocation(int x, int y){
		
		Vector2f screenPosition = new Vector2f(x, y);
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		Ray rayToUse = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));

		rayToUse.getDirection().normalizeLocal();
		
		PickResults results = new TrianglePickResults();
		results.setCheckDistance(true);
		results.clear();

		SceneGameState.getInstance().getDesignNode().findPick(rayToUse, results);
		if(results.getNumber()>0){
			Spatial picked = results.getPickData(0).getTargetMesh();
			Node pickedDesignNode = findSelectedDesignNode(picked);
			return SceneScape.getCity().findDesignByFullIdentifier(pickedDesignNode.getName());
		}
		else{
			return null;
		}
	}
	
	private static Node findSelectedDesignNode(Spatial s){
		if(s.getParent().getName().equals("designNode")){
			return (Node)s;
		}
		else{
			return findSelectedDesignNode(s.getParent());
		}
	}

}
