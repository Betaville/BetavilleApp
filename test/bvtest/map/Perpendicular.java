/**
 * 
 */
package bvtest.map;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial.NormalsMode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;

/**
 * @author Skye Book
 *
 */
public class Perpendicular extends SimpleGame {

	public Perpendicular() {
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {

		boolean debugMode = false;

		Vector3f start = new Vector3f();
		Vector3f end = new Vector3f(100, 0, 60);

		MaterialState ms = display.getRenderer().createMaterialState();
		ms.setAmbient(ColorRGBA.red);
		ms.setDiffuse(ColorRGBA.red);

		Box startingBox = new Box("start", new Vector3f(), 1, 1, 1);
		startingBox.setLocalTranslation(start);
		startingBox.setRenderState(ms);
		Box endingBox = new Box("end", new Vector3f(), 1, 1, 1);
		endingBox.setLocalTranslation(end);
		endingBox.setRenderState(ms);

		if(debugMode){
			rootNode.attachChild(startingBox);
			rootNode.attachChild(endingBox);
			rootNode.updateRenderState();
		}

		// storage vectors
		Vector3f tempDir = new Vector3f();
		Vector3f tempLoc = new Vector3f();

		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		// the total width of the object (i.e: from left extent to right extent)
		int width=7;
		// the number of points to create for each *GL* unit.
		int resolutionPerGLUnit=1;

		float distance = end.distance(start);
		int numberOfPoints = (int)(distance*resolutionPerGLUnit);

		vertices.add(start);



		// do left & right locations for start
		end.subtract(start, tempDir);
		tempDir.normalizeLocal();
		tempDir.crossLocal(Vector3f.UNIT_Y);
		start.clone().add(tempDir.mult(-1*(width/2)), tempLoc);
		vertices.add(tempLoc.clone());
		start.clone().add(tempDir.mult(width/2), tempLoc);
		vertices.add(tempLoc.clone());

		float increment = 1f/numberOfPoints;
		for(int i=1; i<numberOfPoints; i++){
			Vector3f thisLocation = start.clone();
			thisLocation.interpolate(end, increment*i);

			vertices.add(thisLocation);

			// find the perpendicular vector for the current location
			end.subtract(thisLocation, tempDir);
			tempDir.normalizeLocal();
			tempDir.crossLocal(Vector3f.UNIT_Y);


			Box left = new Box(i+"a", new Vector3f(), .5f, .5f, .5f);
			thisLocation.add(tempDir.mult(-1*(width/2)), tempLoc);
			vertices.add(tempLoc.clone());
			left.setLocalTranslation(tempLoc.clone());

			Box right = new Box(i+"c", new Vector3f(), .5f, .5f, .5f);
			thisLocation.add(tempDir.mult(width/2), tempLoc);
			vertices.add(tempLoc.clone());
			right.setLocalTranslation(tempLoc.clone());

			Box center = new Box(i+"", new Vector3f(), 1, 1, 1);
			center.setLocalTranslation(thisLocation);

			if(debugMode){
				rootNode.attachChild(left);
				rootNode.attachChild(right);
				rootNode.attachChild(center);
			}
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

		TriMesh t = new TriMesh("", vertexBuffer, normals, null, null, faces);
		//ms.setMaterialFace(MaterialFace.FrontAndBack);
		t.setNormalsMode(NormalsMode.Off);
		t.setSolidColor(ColorRGBA.red);
		t.updateRenderState();
		rootNode.attachChild(t);
		t.setRenderState(ms);
		t.updateRenderState();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Perpendicular p = new Perpendicular();
		p.start();
	}

}
