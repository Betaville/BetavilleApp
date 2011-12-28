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
package edu.poly.bxmc.betaville.module;

import org.apache.log4j.Logger;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.VertexMousePick;
import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * 
 * @author Skye Book
 *
 */
public class VertexEditModule extends Module implements LocalSceneModule {
	private static Logger logger = Logger.getLogger(VertexEditModule.class);

	private VertexMousePick pick;
	
	private Box vertexBox;
	

	/**
	 * @param name
	 */
	public VertexEditModule() {
		super("Resize Picker");
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#initialize(com.jme.scene.Node)
	 */
	public void initialize(Node scene) {
		pick = new VertexMousePick();
		pick.setSpatial(SceneScape.getTargetSpatial());
		
		vertexBox = new Box("VertexSelection", new Vector3f(), Scale.fromMeter(2), Scale.fromMeter(2), Scale.fromMeter(2));
		
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#onUpdate(com.jme.scene.Node, com.jme.math.Vector3f, com.jme.math.Vector3f)
	 */
	public void onUpdate(Node scene, Vector3f cameraLocation,
			Vector3f cameraDirection) {

		Vector3f closestVertex = pick.checkPick();
		
		if(closestVertex!=null){
			vertexBox.setLocalTranslation(closestVertex);
			if(vertexBox.getParent()==null){
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(vertexBox);
			}
		}
		else{
			// if there was no vertex picked, remove the box from the scene
			vertexBox.removeFromParent();
		}

	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
	 */
	public void deconstruct() {
		if(vertexBox.getParent()!=null) vertexBox.removeFromParent();
	}

}
