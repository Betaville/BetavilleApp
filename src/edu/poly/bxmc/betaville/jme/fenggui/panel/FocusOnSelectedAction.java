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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import com.jme.bounding.BoundingBox;
import com.jme.math.Plane;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.GlobalSceneModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * @author Skye Book
 *
 */
public class FocusOnSelectedAction extends PanelAction {
	private static final Logger logger = Logger.getLogger(FocusOnSelectedAction.class);
	
	private static final Plane groundPlane = new Plane(Vector3f.UNIT_Y, 0f);
	private static Plane frontPlane;
	
	private static final Vector3f leftCollision = new Vector3f();
	private static final Vector3f rightCollision = new Vector3f();
	private static final Vector3f topCollision = new Vector3f();
	private static final Vector3f bottomCollision = new Vector3f();
	
	private static final Vector2f screenPosition = new Vector2f(0, 0);
	
	public FocusOnSelectedAction() {
		super("Focuser", "Focuses on Objects", "Focus on Object", AvailabilityRule.OBJECT_SELECTED, UserType.BASE_COMMITTER, new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				
				logger.info("Distance between sides at ground: " + viewportWidthAtGround());
				
				if(!SceneScape.isTargetSpatialEmpty()){
					if(SceneScape.getTargetSpatial().getWorldBound()==null){
						logger.warn("No bounding box found for design "+SceneScape.getPickedDesign().getID()+", creating one");
						SceneScape.getTargetSpatial().setModelBound(new BoundingBox());
						SceneScape.getTargetSpatial().updateModelBound();
					}
					
					final float x = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).xExtent;
					final float y = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).yExtent;
					final float z = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).zExtent;
					
					logger.info("Model extent is "+x+","+y+","+z);
					//if(true) return;
					
					// find the average of the two sides and use that
					final float avg = (x+z)/2f;
					
					final Vector3f objectCenter = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).getCenter();
					
					
					final Vector3f camLocation = new Vector3f(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
					
					// turn off regular camera control (via SceneController)
					SceneGameState.getInstance().detatchSceneController();
					
					
					// we want to end up with this object filling the view.					
					GameTaskQueueManager.getManager().update(new Callable<Object>() {

						public Object call() throws Exception {
							float increment =.01f;
							
							float sceneWidth = viewportWidthAtGround();
							float sceneHeight = viewportHeightAtGround();
							
							Vector3f toUse = new Vector3f();
							
							// get closer until the object takes up the screen
							while((sceneWidth>avg /*|| sceneHeight>y*/) && increment < .9f){
								toUse.interpolate(camLocation, objectCenter, increment);
								DisplaySystem.getDisplaySystem().getRenderer().getCamera().setLocation(toUse);
								DisplaySystem.getDisplaySystem().getRenderer().getCamera().update();
								sceneWidth = viewportWidthAtGround();
								sceneHeight = viewportHeightAtGround();
								increment+=.01f;
							}
							
							logger.info("Incremented the camera " + increment);
							
							DisplaySystem.getDisplaySystem().getRenderer().getCamera().lookAt(objectCenter, Vector3f.UNIT_Y);
							return null;
						}
					});
					
					
					// return camera control to SceneController
					SceneGameState.getInstance().reattachSceneController();
				}
			}
		});
	}
	
	private static float viewportWidthAtGround(){
		screenPosition.x=0;
		screenPosition.y=0;
		
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		Ray leftRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		leftRay.getDirection().normalizeLocal();
		
		leftRay.intersectsWherePlane(groundPlane, leftCollision);
		
		screenPosition.x = DisplaySystem.getDisplaySystem().getWidth();
		worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		
		Ray rightRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		rightRay.getDirection().normalizeLocal();
		
		rightRay.intersectsWherePlane(groundPlane, rightCollision);
		
		return rightCollision.distance(leftCollision);
	}
	
	private static float viewportHeightAtGround(){
		
		// get the plane of the camera direction
		frontPlane = new Plane(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection(), 0f);
		
		screenPosition.x=0;
		screenPosition.y=DisplaySystem.getDisplaySystem().getHeight();
		
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		Ray topRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		topRay.getDirection().normalizeLocal();
		
		topRay.intersectsWherePlane(frontPlane, topCollision);
		
		screenPosition.y=0;
		worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		
		Ray bottomRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(), worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
		bottomRay.getDirection().normalizeLocal();
		
		bottomRay.intersectsWherePlane(frontPlane, bottomCollision);
		
		return topCollision.distance(bottomCollision);
	}
	
	private class ZoomModule extends Module implements GlobalSceneModule{

		private long lengthOfMove;
		private long startTime; 

		public ZoomModule(int objectID) {
			this(objectID, 1500);
		}
		
		public ZoomModule(int objectID, long lengthOfMove) {
			super("FocusOn"+objectID+"Module");
			this.lengthOfMove=lengthOfMove;
		}

		public void initialize(Node scene) {
			startTime = System.currentTimeMillis();
		}

		public void onUpdate(Node scene, Vector3f cameraLocation,
				Vector3f cameraDirection) {
			
		}

		public void deconstruct() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
