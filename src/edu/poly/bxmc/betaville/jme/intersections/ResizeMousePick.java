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
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.jme.fenggui.panel.BArrow;
import edu.poly.bxmc.betaville.jme.fenggui.panel.BAxisRods;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * Handles the picking of geometry within the jME scene.
 * @author Vivian Park
 *
 */
public class ResizeMousePick {
	private static Logger logger = Logger.getLogger(ResizeMousePick.class);
	private PickResults widgetResults = new TrianglePickResults();
	private Ray rayToUse;

	private Spatial spatialToTest;

	private final int xBoxPicked = 1;
	private final int yBoxPicked = 2;
	private final int zBoxPicked = 3;
	private final int boundingBoxPicked = 4;


	public ResizeMousePick(Spatial spatialToTest){
		this.spatialToTest=spatialToTest;

		widgetResults = new BoundingPickResults();
		widgetResults.setCheckDistance(true);
	}

	public int checkPick(){

		Vector2f screenPosition = new Vector2f(MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		rayToUse = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		rayToUse.getDirection().normalizeLocal();

		widgetResults.clear();

		spatialToTest.findPick(rayToUse, widgetResults);

		if(widgetResults.getNumber()>0){
			Geometry widget = widgetResults.getPickData(0).getTargetMesh();
			if(widget instanceof Box || widget instanceof Cylinder) {
				if(widget.getParent() instanceof BArrow){
					if(widget.getParent().getParent() instanceof BAxisRods && widget.getParent().getParent().getName().equals("$editorWidget-scaleRod")){
						if(widget.getParent().getName().equals("xAxis")){
							return xBoxPicked;
						}
							
						else if(widget.getParent().getName().equals("yAxis")){
							return yBoxPicked;
						}
						else if(widget.getParent().getName().equals("zAxis")){
							return zBoxPicked;
						}
						/*else if() {	// if constraint checkbox is checked off.... scale all three ways!! (:
							return boundingBoxPicked;
						}*/
					}
				}

			}
		}


		return -1;
	}
}
