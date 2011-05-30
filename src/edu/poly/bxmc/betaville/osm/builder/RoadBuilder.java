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

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial.NormalsMode;
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
public class RoadBuilder extends ObjectBuilder {

	private JME2MapManager localTransformer;

	// The road's width in meters
	private float width=Scale.fromMeter(5);

	private float resolutionPerGLUnit = Scale.toMeter(1);

	//private int resolution=30;

	/**
	 * @param osmObject
	 */
	public RoadBuilder(Way osmObject) {
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
				start=end.clone();;
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
				
				// generate data for this points in between the start and end nodes
				for(int i=1; i<numberOfPoints; i++){
					Vector3f thisLocation = start.clone();
					thisLocation.interpolate(end, increment*i);

					vertices.add(thisLocation);

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
				
				vertices.add(end);

				// do left & right locations for end
				end.subtract(end, tempDir);
				tempDir.normalizeLocal();
				tempDir.crossLocal(Vector3f.UNIT_Y);
				end.clone().add(tempDir.mult(-1*(width/2)), tempLoc);
				vertices.add(tempLoc.clone());
				end.clone().add(tempDir.mult(width/2), tempLoc);
				vertices.add(tempLoc.clone());
				
				start=end.clone();;
			}
		}

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
		//IntBuffer faces = IntBuffer.allocate(vertices.size()*4);
		IntBuffer faces = ByteBuffer.allocateDirect(4 * (vertices.size()*4)).order(ByteOrder.nativeOrder()).asIntBuffer();
		faces.rewind();
		for(int i=0; i<vertices.size()/3; i++){
			int middle = i*3;
			int left = (i*3)+1;
			int right = (i*3)+2;
			int nextMiddle = middle+3;
			int nextLeft = left+3;
			int nextRight = right+3;

			// left bottom triangle
			faces.put(middle);
			faces.put(left);
			faces.put(nextMiddle);

			// left top triangle
			faces.put(left);
			faces.put(nextMiddle);
			faces.put(nextLeft);

			// right bottom triangle
			faces.put(middle);
			faces.put(right);
			faces.put(nextMiddle);

			// right top triangle
			faces.put(right);
			faces.put(nextMiddle);
			faces.put(nextRight);
		}

		//FloatBuffer coords;
		//TexCoords tc = new TexCoords(null, 2);

		//FloatBuffer normals = FloatBuffer.allocate(faces.capacity()/3);
		FloatBuffer normals = ByteBuffer.allocateDirect(4 * (faces.capacity()*3)).order(ByteOrder.nativeOrder()).asFloatBuffer();
		normals.rewind();
		boolean flip = false;
		for(int i=0; i<normals.capacity(); i+=3){
			if(flip){
				normals.put(0);
				normals.put(1);
				normals.put(0);
			}
			else{
				normals.put(0);
				normals.put(1);
				normals.put(0);
			}
			flip=!flip;
		}

		sceneObject = new TriMesh("", vertexBuffer, normals, null, null, faces);
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
