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
package edu.poly.bxmc.betaville.osm.builder;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.osm.Way;
import edu.poly.bxmc.betaville.osm.tag.Highway;

/**
 * Builds a road with just the node data represented as boxes
 * @author Skye Book
 *
 */
public class RoadNodeBuilder extends ObjectBuilder {
	
	private JME2MapManager localTransformer;
	
	/**
	 * @param osmObject
	 */
	public RoadNodeBuilder(Way osmObject) {
		super(osmObject);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.osm.builder.ObjectBuilder#generateObject()
	 */
	@Override
	public Spatial generateObject() {
		localTransformer = new JME2MapManager();
		Way way = (Way)osmObject;
		if(way.findTag(Highway.class)==null){
			System.out.println("This is not a road!");
			return null;
		}
		
		Node sceneObject = new Node(((searchForName()!=null)?searchForName():"OSM Object"));
		sceneObject.setLocalTranslation(JME2MapManager.instance.locationToBetaville(way.getNodes().get(0).getLocation()));
		
		System.out.println("Setting local Node's offset to " + way.getNodes().get(0).getLocation().toString());
		localTransformer.adjustOffsets(way.getNodes().get(0).getLocation());
		
		System.out.println("Found name: " + searchForName());
		Box target = new Box("target", new Vector3f(), Scale.fromMeter(2), Scale.fromMeter(2), Scale.fromMeter(2));
		MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		ms.setAmbient(ColorRGBA.yellow);
		ms.setDiffuse(ColorRGBA.orange);
		target.setRenderState(ms);
		target.updateRenderState();
		
		for(edu.poly.bxmc.betaville.osm.Node node : way.getNodes()){
			SharedMesh nodeMesh = new SharedMesh(target);
			node.getLocation().setAltitude(30);
			nodeMesh.setLocalTranslation(localTransformer.locationToBetaville(node.getLocation()));
			sceneObject.attachChild(nodeMesh);
			sceneObject.setRenderState(ms);
			sceneObject.updateRenderState();
		}
		return sceneObject;
	}

}
