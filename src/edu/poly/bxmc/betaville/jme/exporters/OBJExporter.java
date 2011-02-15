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
package edu.poly.bxmc.betaville.jme.exporters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.QuadMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState.StateType;

/**
 * Exports an OBJ file from a jME mesh.
 * @author Skye Book
 *
 */
public class OBJExporter {
	private String meshData="";
	private String mtlData="";
	private boolean finished = false;
	
	private File objFile;
	private File mtlFile;
	
	private MeshExporter meshExporter;
	
	/**
	 * 
	 * @param object The object being converted to OBJ
	 * @param rawOBJFile The file to write the obj to
	 */
	public OBJExporter(Spatial object, File rawOBJFile) throws IOException{
		
		// normalize the filename
		if(!rawOBJFile.toString().endsWith(".obj")){
			objFile = new File(rawOBJFile.toString()+".obj");
		}
		else{
			objFile = rawOBJFile;
		}
		
		if(!objFile.exists()) objFile.createNewFile();
		
		mtlFile = new File(rawOBJFile.toString().substring(0, objFile.toString().lastIndexOf("."))+".mtl");
		
		PrintWriter objWriter = new PrintWriter(new FileOutputStream(rawOBJFile));
		
		objWriter.write("# This file has been created by Betaville and is exported in Meters\n\n");
		objWriter.flush();
		

		PrintWriter mtlWriter = new PrintWriter(new FileOutputStream(objFile));
		
		mtlWriter.write("# This material file has been created by Betaville\n\n");
		mtlWriter.flush();
		
		meshExporter = new OBJMeshExporter(objWriter);
		
		export(object);
		
		objWriter.write(meshData);
		objWriter.flush();
		
		mtlWriter.write(mtlData);
		mtlWriter.flush();
		finished=true;
	}
	
	public boolean wasExported(){
		return finished;
	}
	
	public void export(Spatial object){
		
		if(object instanceof Node){
			meshData+="g "+object.getName()+"\n";
			if(((Node)object).getChildren()!=null){
				for(Spatial s : ((Node)object).getChildren()){
					export(s);
				}
			}
		}
		
		if(object instanceof Geometry){
			MTLExporter mtlExporter = new MTLExporter();
			try {
				mtlExporter.createMaterial(
						(MaterialState)object.getRenderState(StateType.Material),
						(TextureState)object.getRenderState(StateType.Texture),
						new File(objFile.toString().substring(0, objFile.toString().lastIndexOf("/"))).toURI().toURL(),
						object.getName());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (StringIndexOutOfBoundsException e){
				try {
					mtlExporter.createMaterial(
							(MaterialState)object.getRenderState(StateType.Material),
							(TextureState)object.getRenderState(StateType.Texture),
							new File(objFile.toString()+".obj").toURI().toURL(),
							object.getName());
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		if(object instanceof TriMesh){
			System.out.println("Sending trimesh '"+object.getName()+"' to " + meshExporter.getClass().getName());
			meshExporter.exportTriMesh((TriMesh)object);
		}
		
		if(object instanceof QuadMesh){
			System.out.println("Sending quadmesh '"+object.getName()+"' to " + meshExporter.getClass().getName());
			meshExporter.exportQuadMesh((QuadMesh)object);
		}
	}
}
