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

import java.io.PrintWriter;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.scene.QuadMesh;
import com.jme.scene.SharedMesh;
import com.jme.scene.TriMesh;

import edu.poly.bxmc.betaville.jme.loaders.util.AbstractMeshExporter;

/**
 * @author Skye Book
 *
 */
public class OBJMeshExporter extends AbstractMeshExporter implements MeshExporter{

	public OBJMeshExporter(PrintWriter writer) {
		super(writer);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.loaders.util.MeshExporter#exportQuadMesh(com.jme.scene.QuadMesh)
	 */
	public void exportQuadMesh(QuadMesh q) {
		writer.write("# TriMesh: " + q.getName()+"\ng "+q.getName()+"\n");
		writer.flush();

		FloatBuffer vertBuffer = q.getVertexBuffer();
		
		// It seems like there are some cases where this is actually an issue
		// You can most easily encounter this by passing in a jME Teapot
		if(vertBuffer==null){
			System.out.println("Null Vertex Buffer");
		}
		
		System.out.println("rewinding vertex buffer");
		vertBuffer.rewind();
		System.out.println("vertex buffer rewound");
		while(vertBuffer.hasRemaining()){
			writer.write("v ");
			writer.write(Float.toString(vertBuffer.get())+" ");
			writer.write(Float.toString(vertBuffer.get())+" ");
			writer.write(Float.toString(vertBuffer.get())+"\n");
			writer.flush();
		}
		System.out.println("buffer traversed");

		FloatBuffer texBuffer = q.getTextureCoords().get(0).coords;
		System.out.println("rewinding texture buffer");
		texBuffer.rewind();
		System.out.println("texture buffer rewound");
		while(texBuffer.hasRemaining()){
			writer.write("vt ");
			writer.write(Float.toString(texBuffer.get())+" ");
			writer.write(Float.toString(texBuffer.get())+"\n");
		}
		writer.flush();
		System.out.println("buffer traversed");

		FloatBuffer normalBuffer = q.getNormalBuffer();
		System.out.println("rewinding normal buffer");
		normalBuffer.rewind();
		System.out.println("normal buffer rewound");
		while(normalBuffer.hasRemaining()){
			writer.write("vn ");
			writer.write(Float.toString(normalBuffer.get())+" ");
			writer.write(Float.toString(normalBuffer.get())+" ");
			writer.write(Float.toString(normalBuffer.get())+"\n");
		}
		writer.flush();
		System.out.println("buffer traversed");

		IntBuffer indexBuffer = q.getIndexBuffer();
		System.out.println("rewinding index buffer");
		indexBuffer.rewind();
		System.out.println("index buffer rewound");
		while(indexBuffer.hasRemaining()){
			writer.write("f ");
			writer.write((indexBuffer.get()+1) + " " + (indexBuffer.get()+1) + " " + (indexBuffer.get()+1) + " " + (indexBuffer.get()+1) + "\n");
		}
		writer.flush();
		System.out.println("buffer traversed");
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.loaders.util.MeshExporter#exportSharedMesh(com.jme.scene.SharedMesh)
	 */
	public void exportSharedMesh(SharedMesh mesh) {
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.loaders.util.MeshExporter#exportTriMesh(com.jme.scene.TriMesh)
	 */
	public void exportTriMesh(TriMesh t) {
		writer.write("# TriMesh: " + t.getName()+"\ng "+t.getName()+"\n");
		writer.flush();

		FloatBuffer vertBuffer = t.getVertexBuffer();
		
		// It seems like there are some cases where this is actually an issue
		// You can most easily encountered this by passing in a jME Teapot
		if(vertBuffer==null){
			System.out.println("Null Vertex Buffer");
		}
		
		System.out.println("rewinding vertex buffer");
		vertBuffer.rewind();
		System.out.println("vertex buffer rewound");	
		while(vertBuffer.hasRemaining()){
			writer.write("v ");
			writer.write(Float.toString(vertBuffer.get())+" ");
			writer.write(Float.toString(vertBuffer.get())+" ");
			writer.write(Float.toString(vertBuffer.get())+"\n");
		}
		writer.flush();
		System.out.println("buffer traversed");

		FloatBuffer texBuffer = t.getTextureCoords().get(0).coords;
		System.out.println("rewinding texture buffer");
		texBuffer.rewind();
		System.out.println("texture buffer rewound");
		while(texBuffer.hasRemaining()){
			writer.write("vt ");
			writer.write(Float.toString(texBuffer.get())+" ");
			writer.write(Float.toString(texBuffer.get())+"\n");
		}
		writer.flush();
		System.out.println("buffer traversed");

		FloatBuffer normalBuffer = t.getNormalBuffer();
		System.out.println("rewinding normal buffer");
		normalBuffer.rewind();
		System.out.println("normal buffer rewound");
		while(normalBuffer.hasRemaining()){
			writer.write("vn ");
			writer.write(Float.toString(normalBuffer.get())+" ");
			writer.write(Float.toString(normalBuffer.get())+" ");
			writer.write(Float.toString(normalBuffer.get())+"\n");
		}
		writer.flush();
		System.out.println("buffer traversed");

		IntBuffer indexBuffer = t.getIndexBuffer();
		System.out.println("rewinding index buffer");
		indexBuffer.rewind();
		System.out.println("index buffer rewound");
		while(indexBuffer.hasRemaining()){
			writer.write("f ");
			writer.write((indexBuffer.get()+1) + " " + (indexBuffer.get()+1) + " " + (indexBuffer.get()+1) + "\n");
		}
		writer.flush();
		System.out.println("buffer traversed");
	}
}
