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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;


import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.TriMesh.Mode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.osm.Node;
import edu.poly.bxmc.betaville.osm.Way;

/**
 * Generates a landmass from OSM data
 * @author Skye Book
 *
 */
public class CoastlineGLFanBuilder extends ObjectBuilder {

	/**
	 * @param osmObject
	 */
	public CoastlineGLFanBuilder(Way osmObject) {
		super(osmObject);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.osm.builder.ObjectBuilder#generateObject()
	 */
	@Override
	public Spatial generateObject() {
		TriMesh mesh = null;
		Way way = (Way)osmObject;
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
		
		JME2MapManager localTransformer = new JME2MapManager();
		
		localTransformer.adjustOffsets(way.getNodes().get(0).getLocation());
		
		// convert to a list of vertices
		for(Node node : way.getNodes()){
			vertices.add(localTransformer.locationToBetaville(node.getLocation()).clone());
		}
		
		
		// this one has an extra space for the center point
		/*
		FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(4 *(vertices.size()+1) *3).asFloatBuffer();
		vertexBuffer.rewind();
		vertexBuffer.put(midPoint.x);
		vertexBuffer.put(midPoint.y);
		vertexBuffer.put(midPoint.z);
		*/
		
		FloatBuffer bbBufferSetup = ByteBuffer.allocateDirect(4 *(vertices.size()) *3).order(ByteOrder.nativeOrder()).asFloatBuffer();
		bbBufferSetup.rewind();
		
		for(Vector3f vertex : vertices){
			bbBufferSetup.put(vertex.x);
			bbBufferSetup.put(vertex.y);
			bbBufferSetup.put(vertex.z);
		}
		
		BoundingBox bb = new BoundingBox();
		bb.computeFromPoints(bbBufferSetup);
		
		Vector3f midPoint = bb.getCenter();
		
		FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(4 *(vertices.size()+1) *3).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.rewind();
		vertexBuffer.put(midPoint.x);
		vertexBuffer.put(midPoint.y);
		vertexBuffer.put(midPoint.z);
		
		for(Vector3f vertex : vertices){
			vertexBuffer.put(vertex.x);
			vertexBuffer.put(vertex.y);
			vertexBuffer.put(vertex.z);
		}
		
		IntBuffer indexBuffer = ByteBuffer.allocateDirect(4 * (vertices.size()+1)).order(ByteOrder.nativeOrder()).asIntBuffer();
		indexBuffer.rewind();
		
		for(int i=0; i<vertices.size(); i++){
			indexBuffer.put(i);
		}
		// this last point completes the final triangle
		indexBuffer.put(1);
		
		FloatBuffer normals = ByteBuffer.allocateDirect(4 * ((vertices.size()+1)*3)).order(ByteOrder.nativeOrder()).asFloatBuffer();
		normals.rewind();
		for(int i=0; i<normals.capacity(); i+=3){
			normals.put(0);
			normals.put(1);
			normals.put(0);
		}
		
		MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		ms.setAmbient(ColorRGBA.yellow);
		ms.setDiffuse(ColorRGBA.orange);
		
		com.jme.scene.Node sceneNode = new com.jme.scene.Node("Way_"+way.getId());
		Box mid = new Box("mid", midPoint, Scale.fromMeter(10), Scale.fromMeter(10), Scale.fromMeter(10));
		mid.setSolidColor(ColorRGBA.green);
		mid.setLocalTranslation(mid.getLocalTranslation().z, mid.getLocalTranslation().y+Scale.fromMeter(100), mid.getLocalTranslation().z);
		sceneNode.attachChild(mid);
		
		for(Vector3f point : vertices){
			Box b = new Box("", point, Scale.fromMeter(2), Scale.fromMeter(2), Scale.fromMeter(2));
			sceneNode.attachChild(b);
		}
		sceneNode.setLocalTranslation(JME2MapManager.instance.locationToBetaville(way.getNodes().get(0).getLocation()));
		sceneNode.setLocalTranslation(sceneNode.getLocalTranslation().x, sceneNode.getLocalTranslation().y+Scale.fromMeter(3), sceneNode.getLocalTranslation().z);
		//return sceneNode;
		
		
		mesh = new TriMesh("Way_"+way.getId(), vertexBuffer, normals, null, null, indexBuffer);
		mesh.setMode(Mode.Fan);
		//mesh.setSolidColor(ColorRGBA.red);
		mesh.updateRenderState();
		mesh.setRenderState(ms);
		mesh.updateRenderState();
		mesh.setLocalTranslation(JME2MapManager.instance.locationToBetaville(way.getNodes().get(0).getLocation()));
		mesh.setLocalTranslation(mesh.getLocalTranslation().x, mesh.getLocalTranslation().y+Scale.fromMeter(3), mesh.getLocalTranslation().z);
		return mesh;
		
	}

}
