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

import org.apache.log4j.Logger;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial.NormalsMode;
import com.jme.scene.TriMesh.Mode;
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
public class RoadGLTriangleStripBuilder extends ObjectBuilder {
	private static final Logger logger = Logger.getLogger(RoadGLTriangleStripBuilder.class);

	private JME2MapManager localTransformer;

	// The road's width in meters
	private float width=Scale.fromMeter(5);

	private float resolutionPerGLUnit = Scale.toMeter(1);

	//private int resolution=30;

	/**
	 * @param osmObject
	 */
	public RoadGLTriangleStripBuilder(Way osmObject) {
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

		TriMesh sceneObject = null;

		System.out.println("Setting local Node's offset to " + way.getNodes().get(0).getLocation().toString());
		localTransformer.adjustOffsets(way.getNodes().get(0).getLocation());

		System.out.println("Found name: " + searchForName());

		// storage vectors
		Vector3f tempDir = new Vector3f();
		Vector3f tempLoc = new Vector3f();

		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();


		// the previous location
		Vector3f start=null;
		for(edu.poly.bxmc.betaville.osm.Node node : way.getNodes()){
			Vector3f end = localTransformer.locationToBetaville(node.getLocation());

			// if this is the first node, the approach is a bit simpler
			if(start==null){
				start=end.clone();
				continue;
			}
			else{
				int numberOfPoints = (int)(end.distance(start)*resolutionPerGLUnit);
				float increment = 1f/numberOfPoints;

				// do left & right locations for start if this is the first piece
				if(vertices.size()==0){
					end.subtract(start, tempDir);
					tempDir.normalizeLocal();
					tempDir.crossLocal(Vector3f.UNIT_Y);
					start.clone().add(tempDir.mult(-1*(width/2)), tempLoc);
					vertices.add(tempLoc.clone());
					start.clone().add(tempDir.mult(width/2), tempLoc);
					vertices.add(tempLoc.clone());
				}
				
				// generate data for the points in between the start and end nodes
				for(int i=1; i<numberOfPoints; i++){
					Vector3f thisLocation = start.clone();
					thisLocation.interpolate(end, increment*i);

					// find the perpendicular vector for the current location
					end.subtract(thisLocation, tempDir);
					tempDir.normalizeLocal();
					tempDir.crossLocal(Vector3f.UNIT_Y);


					// this is one side of the centerline
					thisLocation.add(tempDir.mult(-1*(width/2)), tempLoc);
					vertices.add(tempLoc.clone());

					// this is the other side of the centerline
					thisLocation.add(tempDir.mult(width/2), tempLoc);
					vertices.add(tempLoc.clone());
				}
				
				// do left & right locations for end
				end.subtract(end, tempDir);
				tempDir.normalizeLocal();
				tempDir.crossLocal(Vector3f.UNIT_Y);
				end.clone().add(tempDir.mult(-1*(width/2)), tempLoc);
				vertices.add(tempLoc.clone());
				end.clone().add(tempDir.mult(width/2), tempLoc);
				vertices.add(tempLoc.clone());
				
				start=end.clone();
			}
		}
		
		logger.info(vertices.size() + " vertices created");

		MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		ms.setAmbient(ColorRGBA.yellow);
		ms.setDiffuse(ColorRGBA.orange);
		
		// START
		
		// create the vertex buffer
		//FloatBuffer vertexBuffer = FloatBuffer.allocate(vertices.size()*3);
		FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(4 * (vertices.size()*3)).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.rewind();
		for(Vector3f v : vertices){
			vertexBuffer.put(v.x);
			vertexBuffer.put(v.y);
			vertexBuffer.put(v.z);
		}

		// create the index buffer (form triangles)
		// NOTE:  Triangles are created in the GL_TRIANGLE_STRIP format
		IntBuffer faces = ByteBuffer.allocateDirect(4 * (vertices.size())).order(ByteOrder.nativeOrder()).asIntBuffer();
		faces.rewind();
		
		// the vertices were created in the correct order, so we just need to put numbers in
		for(int i=0; i<vertices.size(); i++){
			faces.put(i);
		}

		//FloatBuffer coords;
		//TexCoords tc = new TexCoords(null, 2);

		//FloatBuffer normals = FloatBuffer.allocate(faces.capacity()/3);
		FloatBuffer normals = ByteBuffer.allocateDirect(4* (vertices.size()*3)).order(ByteOrder.nativeOrder()).asFloatBuffer();
		normals.rewind();
		for(int i=0; i<vertices.size(); i++){
			normals.put(0);
			normals.put(1);
			normals.put(0);
		}

		sceneObject = new TriMesh("Way_"+way.getId(), vertexBuffer, normals, null, null, faces);
		sceneObject.setMode(Mode.Strip);
		//ms.setMaterialFace(MaterialFace.FrontAndBack);
		sceneObject.setNormalsMode(NormalsMode.Off);
		sceneObject.setSolidColor(ColorRGBA.red);
		sceneObject.updateRenderState();
		sceneObject.setRenderState(ms);
		sceneObject.updateRenderState();
		sceneObject.setLocalTranslation(JME2MapManager.instance.locationToBetaville(way.getNodes().get(0).getLocation()));

		return sceneObject;
	}

}
