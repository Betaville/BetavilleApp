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
package edu.poly.bxmc.betaville.jme.loaders.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.QuadMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.GeometryTool;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.net.PhysicalFileTransporter;

/**
 * A Number of static methods to aid in the manipulation of
 * geometry.
 * 
 * @see RenderState
 * @see TriMesh
 * @see QuadMesh
 * 
 * @author Skye Book
 *
 */
public class GeometryUtilities {
	private static Logger logger = Logger.getLogger(GeometryUtilities.class);
	private static RenderState workingState=null;
	
	private static HashMap<String, MaterialState> materials = new HashMap<String, MaterialState>();

	/**
	 * Applies color to a Spatial in the form of a <code>MaterialState</code>.  This
	 * is a utility method which passes the supplied color as both the diffuse and ambient
	 * values to {@link #applyColor(Spatial, ColorRGBA, ColorRGBA)}
	 * @param spatial The spatial to apply the color to.
	 * @param color The color to apply to the spatial.
	 * 
	 * @see MaterialState
	 * @see #applyColor(Spatial, ColorRGBA, ColorRGBA)
	 */
	public static void applyColor(Spatial spatial, ColorRGBA color){
		applyColor(spatial, color, color);
	}

	/**
	 * Applies color to a Spatial in the form of a <code>MaterialState</code>.
	 * @param spatial
	 * @param diffuseColor
	 * @param ambientColor
	 * 
	 * @see MaterialState
	 */
	public static MaterialState applyColor(Spatial spatial, ColorRGBA diffuseColor, ColorRGBA ambientColor){
		colorStripper(spatial);
		MaterialState targetMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		targetMaterial.setDiffuse(diffuseColor);
		targetMaterial.setAmbient(ambientColor);
		spatial.setRenderState(targetMaterial);
		spatial.updateRenderState();
		if(workingState!=null){
			MaterialState ms = (MaterialState)workingState;
			workingState=null;
			return ms;
		}
		else return null;
	}
	
	public static void applyMaterial(Spatial spatial, MaterialState ms){
		colorStripper(spatial);
		spatial.setRenderState(ms);
		spatial.updateRenderState();
	}

	private static void colorStripper(Spatial spatial){
		// exception for the nodes used for building edit function
		if(spatial.getName().startsWith("$editorWidget")) {
			//logger.info(spatial.getName() + " is a editor widget");
			return ;
		}
		//logger.info(spatial.getName() + " is not editor widget");
		MaterialState rs = (MaterialState)doRemoveRenderState(spatial, StateType.Material);
		if(rs!=null){
			materials.put(spatial.getName(), rs);
		}
		spatial.updateRenderState();
		if(spatial instanceof Node && ((Node)spatial).getChildren()!=null){
			for(int i=0; i<((Node)spatial).getChildren().size(); i++){
				colorStripper(((Node)spatial).getChildren().get(i));
			}
		}
	}
	
	public static void setTranslucent(Spatial spatial){
		MaterialState rs = (MaterialState)doRemoveRenderState(spatial, StateType.Material);
		if(rs!=null){
			materials.put(spatial.getName(), rs);
			
			MaterialState newState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
			
			ColorRGBA ambient = rs.getAmbient();
			ambient.a=.5f;
			ColorRGBA diffuse = rs.getDiffuse();
			diffuse.a=.5f;
			ColorRGBA emissive = rs.getEmissive();
			emissive.a=.5f;
			ColorRGBA specular = rs.getSpecular();
			specular.a=.5f;
			
			newState.setAmbient(ambient);
			newState.setColorMaterial(rs.getColorMaterial());
			newState.setDiffuse(diffuse);
			newState.setEmissive(emissive);
			newState.setMaterialFace(rs.getMaterialFace());
			newState.setShininess(rs.getShininess());
			newState.setSpecular(specular);
		}
		spatial.updateRenderState();
		if(spatial instanceof Node && ((Node)spatial).getChildren()!=null){
			for(int i=0; i<((Node)spatial).getChildren().size(); i++){
				colorStripper(((Node)spatial).getChildren().get(i));
			}
		}
	}
	
	public static void applyWireframe(Spatial s){
		WireframeState ws = DisplaySystem.getDisplaySystem().getRenderer().createWireframeState();
		colorStripper(s);
		doWireframeApplication(s, true, ws);
		s.updateRenderState();
	}

	public static void stripWireframe(Spatial s){
		doWireframeApplication(s, false, null);
		replaceMaterials(s);
		s.updateRenderState();
	}

	private static void doWireframeApplication(Spatial s, boolean apply, WireframeState ws){
		if(apply) s.setRenderState(ws);
		else if(s.getRenderState(StateType.Wireframe)!=null){
			s.clearRenderState(StateType.Wireframe);
		}
	}

	/**
	 * Strips a specified RenderState from a Spatial and all of its children
	 * if there are any.
	 * @param s The spatial to strip
	 * @param state The RenderState to be removed
	 * @see RenderState
	 */
	public static void removeRenderState(Spatial s, StateType state){
		doRemoveRenderState(s, state);
		if(s instanceof Node && ((Node)s).getChildren()!=null){
			for(int i=0; i<((Node)s).getChildren().size(); i++){
				removeRenderState(((Node)s).getChildren().get(i), state);
			}
		}
		s.updateRenderState();
	}

	private static RenderState doRemoveRenderState(Spatial s, StateType state){
		if(s.getRenderState(state)!=null){
			RenderState rs = s.getRenderState(state);
			s.clearRenderState(state);
			return rs;
		}
		return null;
	}
	
	public static void lookForRenderState(Spatial s, StateType stateType){
		if(s.getRenderState(stateType)!=null){
			System.out.println(s.getName() + " has " + stateType.toString());
		}
		else{
			System.out.println(s.getName() + " lacks " + stateType.toString());
		}
		if(s instanceof Node && ((Node)s).getChildren()!=null){
			for(int i=0; i<((Node)s).getChildren().size(); i++){
				lookForRenderState(((Node)s).getChildren().get(i), stateType);
			}
		}
	}

	public static boolean checkForRenderState(Spatial s, StateType stateType){
		if(s.getRenderState(stateType)!=null){
			return true;
		}
		else{
			if(s instanceof Node && ((Node)s).getChildren()!=null){
				for(int i=0; i<((Node)s).getChildren().size(); i++){
					if(checkForRenderState(((Node)s).getChildren().get(i), stateType)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean checkForRenderState(Spatial s, RenderState rs){
		if(s.getRenderState(rs.getStateType())!=null){
			return s.getRenderState(rs.getStateType()).equals(rs);
		}
		else return false;
	}

	public static int countAllChildren(Spatial s){
		// by virtue of getting an object we have at least one!
		if(s!=null){
			int count=1;
			if(s instanceof Node){
				count+=((Node) s).getQuantity();
				for(int i=0; i<((Node)s).getQuantity(); i++){
					count+=countAllChildren(((Node)s).getChild(i));
				}
			}
			return count;
		}
		else return 0;
	}
	
	public static void optimize(Spatial s){
		if(s instanceof TriMesh){
			GeometryTool.minimizeVerts((TriMesh)s, 0);
		}
		else if(s instanceof Node){
			if(((Node)s).getChildren() != null){
				for(int i=0; i< ((Node)s).getChildren().size(); i++){
					optimize(((Node)s).getChildren().get(i));
				}
			}
		}
		else return;
	}
	
	public static void replaceMaterials(Spatial s){
		MaterialState ms = materials.remove(s.getName());
		if(ms!=null){
			s.setRenderState(ms);
		}
		s.updateRenderState();
		if(s instanceof Node){
			if(((Node)s).getChildren()!=null){
				for(Spatial child : ((Node)s).getChildren()){
					replaceMaterials(child);
				}
			}
		}
	}
	
	/**
	 * Sets the MaterialFaces for a given object.  By using the applyToFront and
	 * applyToBack parameters, each desired combination of face settings can be
	 * produced (None, Front, Back, Both).
	 * @param spatial The spatial for which to set the MaterialFace
	 * @param applyToFront True if the front face should have the material applied
	 * @param applyToBack True if the back face should have the material applied
	 * @see MaterialFace
	 */
	public static void setMaterialFaceApplication(Spatial spatial, boolean applyToFront, boolean applyToBack){
		if(applyToFront&&applyToBack){
			if(spatial.getRenderState(StateType.Material)!=null)
				((MaterialState)spatial.getRenderState(StateType.Material)).setMaterialFace(MaterialFace.FrontAndBack);
		}
		else if(applyToFront){
			if(spatial.getRenderState(StateType.Material)!=null)
				((MaterialState)spatial.getRenderState(StateType.Material)).setMaterialFace(MaterialFace.Front);
		}
		else if(applyToBack){
			if(spatial.getRenderState(StateType.Material)!=null)
				((MaterialState)spatial.getRenderState(StateType.Material)).setMaterialFace(MaterialFace.Back);
		}
		
		if(spatial instanceof Node){
			if(((Node)spatial).getChildren()!=null){
				for(Spatial child : ((Node)spatial).getChildren()){
					setMaterialFaceApplication(child, applyToFront, applyToBack);
				}
			}
		}
	}

	public static void printStructure(Spatial s){
		if(s.getParent()!=null){
			logger.debug(s.getParent().getName()+"."+s.getName() + " ("+s.getClass().getName() + ")");
		}
		else{
			logger.debug("Printing Structure of " + s.getName());
		}
		if(s instanceof Node){
			if(((Node)s).getChildren()!=null){
				for(Spatial child : ((Node)s).getChildren()){
					printStructure(child);
				}
			}
		}
	}
	
	public static void deepRotate(Spatial s, int x, int y, int z){
		Quaternion q = s.getLocalRotation();
		//  only transform objects with non-zero rotation
		if(q.x!=0 || q.y!=0 || q.z!=0 || q.w !=1){
			s.setLocalRotation(q.addLocal(Rotator.fromThreeAngles(x, y, z)));
		}
		
		if(s instanceof Node){
			if(((Node)s).getChildren()!=null){
				for(Spatial child : ((Node)s).getChildren()){
					deepRotate(child, x, y, z);
				}
			}
		}
	}

	public static void setupTextureStorage(Spatial s){
		if(s instanceof Node){
			doTextureStorage(s);
			if(((Node)s).getChildren()!=null){
				Iterator<Spatial> it = ((Node)s).getChildren().iterator();
				while(it.hasNext()){
					setupTextureStorage(it.next());
				}
			}
		}
		else{
			doTextureStorage(s);
		}
	}
	
	private static void doTextureStorage(Spatial s){
		if(s.getRenderState(StateType.Texture)!=null){
			TextureState ts = (TextureState)s.getRenderState(StateType.Texture);
			for(int i=0; i<ts.getNumberOfSetTextures(); i++){
				Texture tex = ts.getTexture(i);
				if(tex!=null){
					tex.setStoreTexture(true);
				}
			}
		}
	}
	
	public static float findHeightOfObject(Spatial s){
		if(s instanceof TriMesh) return findHeightOfTriMesh((TriMesh)s);
		else if(s instanceof Node){
			float largest=0;
			if(((Node)s).getChildren()!=null){
				for(int i=0; i<((Node)s).getChildren().size(); i++){
					float h = doFindHeightOfObject(((Node)s).getChild(i));
					if(h>largest) largest=h;
				}
				return largest;
			}
			else return 0;
		}
		else return 0;
	}
	
	private static float doFindHeightOfObject(Spatial s){
		if(s instanceof TriMesh) return findHeightOfTriMesh((TriMesh)s);
		else return 0;
	}
	
	public static float findHeightOfTriMesh(TriMesh s){
		float currentMaxHeight=0;
		Triangle[] t = ((TriMesh)s).getMeshAsTriangles(null);
		for(int i=0; i<t.length; i++){
			for(int j=0; j<3; j++){
				if(t[i].get(j).y>currentMaxHeight){
					currentMaxHeight=t[i].get(j).y;
				}
			}
		}
		
		return currentMaxHeight;
	}
	
	/**
	 * Packages a geometry into a {@link PhysicalFileTransporter} that can be written to
	 * the disk or sent out over the network.
	 * @param identifier The name of the file to retrieve
	 * @return The packaged object
	 * @throws URISyntaxException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static PhysicalFileTransporter getPFT(String identifier) throws URISyntaxException, FileNotFoundException, IOException{
		// Get the local file
		File localFile = new File(new URL(SettingsPreferences.getDataFolder()+"local/"+identifier.replaceAll(" ", "")+".jme").toURI());
		FileInputStream fis = new FileInputStream(localFile.getCanonicalFile());
		
		// Read the contents and pack it into a PFT
		byte[] b = new byte[fis.available()];
		fis.read(b);
		PhysicalFileTransporter transport = new PhysicalFileTransporter(b);
		fis.close();
		return transport;
	}
	
	public static void printModelOrigins(Spatial s){
		if(s instanceof Node){
			logger.info("Node \"" + s.getName() + "\" located at " + s.getLocalTranslation().getX() + ", " + s.getLocalTranslation().getY() + ", " + s.getLocalTranslation().getZ());
			for(Spatial child : ((Node)s).getChildren()){
				printModelOrigins(child);
			}
		}
	}
	
	/**
	 * Calculates an object's distance from the 0,0,0 location
	 * @param s The object who's distance is being calculated.
	 * @return The distance wrapped into a Vector3f
	 */
	public static Vector3f getDistanceFromZero(Spatial s){
		logger.debug("Examining " + s.getName());
		
		Vector3f closest = null;
		Vector3f temp = new Vector3f(0,0,0);
		int faceCount;
		
		if(s instanceof TriMesh){
			faceCount = ((TriMesh)s).getTriangleCount();
			
			FloatBuffer vb = ((TriMesh)s).getVertexBuffer();
			vb.rewind();
			
			for(int i=0; i<faceCount; i++){
				
				// If we are on the first run, then the first
				// vertex is the closest.
				if(i==0){
					closest = new Vector3f(vb.get(0), vb.get(1), vb.get(2));
					continue;
				}
				
				// if this isn't the first run, set the temp vertex
				temp.x=vb.get(i*3);
				temp.y=vb.get(i*3+1);
				temp.z=vb.get(i*3+2);
				
				// compare i, all three values need to be closer to 0
				if(FastMath.abs(temp.x) < FastMath.abs(closest.x) &&
						FastMath.abs(temp.y) < FastMath.abs(closest.y) &&
						FastMath.abs(temp.z) < FastMath.abs(closest.z)){
					closest = temp.clone();
				}
			}
			
			return closest;
		}
		if(s instanceof QuadMesh){
			faceCount = ((QuadMesh)s).getQuadCount();
			
			FloatBuffer vb = ((QuadMesh)s).getVertexBuffer();
			vb.rewind();
			
			for(int i=0; i<faceCount; i++){
				
				// If we are on the first run, then the first
				// vertex is the closest.
				if(i==0){
					closest = new Vector3f(vb.get(0), vb.get(1), vb.get(2));
					continue;
				}
				
				// if this isn't the first run, set the temp vertex
				temp.x=vb.get(i*4);
				temp.y=vb.get(i*4+1);
				temp.z=vb.get(i*4+2);
				
				// compare i, all three values need to be closer to 0
				if(FastMath.abs(temp.x) < FastMath.abs(closest.x) &&
						FastMath.abs(temp.y) < FastMath.abs(closest.y) &&
						FastMath.abs(temp.z) < FastMath.abs(closest.z)){
					closest = temp.clone();
				}
			}
			
			return closest;
		}
		if(s instanceof Node){
			if(((Node)s).getChildren()==null){
				logger.warn("initial node null");
				return new Vector3f(0,0,0);
			}
			
			for(Spatial child : ((Node)s).getChildren()){
				
				// if the child is a node with no children, don't pass it
				if(child instanceof Node){
					if(((Node)child).getChildren()==null){
						logger.debug("Ignoring Node: "  + child.getName() + " with no children.");
						continue;
					}
				}
				
				Vector3f childLocation = getDistanceFromZero(child);
				
				// check if  distance was returned null.  This should only happen
				// if a Node with no children was checked against.
				if(childLocation==null) continue;
				
				// closest will be null on the first run
				if(closest==null){
					closest = childLocation.clone();
					continue;
				}
				
				if(FastMath.abs(childLocation.x) < FastMath.abs(closest.x) &&
						FastMath.abs(childLocation.y) < FastMath.abs(closest.y) &&
						FastMath.abs(childLocation.z) < FastMath.abs(closest.z)){
					closest = childLocation.clone();
				}
			}
			
			if(closest == null) return new Vector3f(0,0,0);
			else return closest;
		}
		logger.warn("Un-Implemented Geometry Type Given");
		throw new IllegalArgumentException("Un-Implemented Geometry Type Given");
	}
	
	public static void adjustObject(Spatial s, Vector3f adjustment){
		adjustObject(s, adjustment.x, adjustment.y, adjustment.z);
	}
	
	/**
	 * Configurable method to print information about an object.
	 * @param toUse
	 * @param s
	 * @param recursive
	 * @param translation
	 * @param rotation
	 * @param scale
	 */
	public static void printInformation(Logger toUse, Spatial s, boolean recursive,
			boolean translation, boolean rotation, boolean scale){
		logger.info(s.getClass().getSimpleName()+": "+s.getName());
		if(translation) toUse.info(s.getName()+" Translation: " + s.getLocalTranslation().x+","+s.getLocalTranslation().y+","+s.getLocalTranslation().z);
		if(rotation){
			float[] angles = s.getLocalRotation().toAngles(null);
			toUse.info(s.getName()+" Rotation: " + angles[0]+","+angles[1]+","+angles[2]);
		}
		if(scale) toUse.info(s.getName()+" Scale: " + s.getLocalScale().x+","+s.getLocalScale().y+","+s.getLocalScale().z);
		
		if(recursive){
			if(s instanceof Node){
				if(((Node)s).getQuantity()>0){
					for(Spatial child : ((Node)s).getChildren()){
						printInformation(toUse, child, recursive, translation, rotation, scale);
					}
				}
			}
		}
	}
	
	/**
	 * Nudges an entire object in the given direction through manipulation
	 * of its vertices rather than position of the Spatial itself.
	 * @param s The Spatial to move.
	 * @param x The amount of x units to move the object.
	 * @param y The amount of y units to move the object.
	 * @param z The amount of z units to move the object.
	 */
	public static void adjustObject(Spatial s, float x, float y, float z){
		logger.info("Moving " + s.getName() + " " + x+","+y+","+z);
		if(s instanceof TriMesh){
			FloatBuffer vb = ((TriMesh)s).getVertexBuffer();
			vb.rewind();
			
			float[] floatArray = new float[vb.capacity()];
			
			for(int i=0; i<((TriMesh)s).getTriangleCount(); i++){
				floatArray[i*3]=vb.get(i*3)+x;
				floatArray[i*3]=vb.get(i*3+1)+y;
				floatArray[i*3]=vb.get(i*3+2)+z;
			}
			
			FloatBuffer newBuffer = FloatBuffer.allocate(vb.capacity());
			newBuffer.put(floatArray);
			((TriMesh)s).setVertexBuffer(newBuffer);
		}
		if(s instanceof QuadMesh){
			FloatBuffer vb = ((QuadMesh)s).getVertexBuffer();
			vb.rewind();
			
			float[] floatArray = new float[vb.capacity()];
			
			for(int i=0; i<((QuadMesh)s).getQuadCount(); i++){
				floatArray[i*4]=vb.get(i*4)+x;
				floatArray[i*4]=vb.get(i*4+1)+y;
				floatArray[i*4]=vb.get(i*4+2)+z;
			}
			
			FloatBuffer newBuffer = FloatBuffer.allocate(vb.capacity());
			newBuffer.put(floatArray);
			((QuadMesh)s).setVertexBuffer(newBuffer);
		}
		if(s instanceof Node){
			if(((Node)s).getChildren()!=null){
				for(Spatial child : ((Node)s).getChildren()){
					adjustObject(child, x, y, z);
				}
			}
		}
	}
	
	/**
	 * Moves all children of this {@link Node} to a single parent.  Please note
	 * that any {@link RenderState}  and scale/transform/rotate information associated
	 * with nodes attached to the input Node will be lost.  For simplicity's sake, this
	 * method will accept any {@link Spatial} but will only ever do anything for a Node.
	 * @param topLevel The top-level object that all children will be collapsed to
	 * @param incomingChildList A previously cached {@link ArrayList} may be passed in here to conserve memory
	 * @param incomingNodesToKill  A previously cached {@link ArrayList} may be passed in here to conserve memory
	 */
	public static void collapseToSingleLevel(Spatial topLevel, ArrayList<Spatial> incomingChildList, ArrayList<Spatial> incomingNodesToKill){
		//logger.info("Optimizing " + topLevel.getName());
		
		
		ArrayList<Spatial> nodesToKill;
		if(incomingNodesToKill==null) nodesToKill = new ArrayList<Spatial>();
		else{
			logger.info("Using incoming node list");
			nodesToKill = incomingNodesToKill;
			nodesToKill.clear();
		}
		
		ArrayList<Spatial> childList;
		if(incomingChildList==null) childList = new ArrayList<Spatial>();
		else{
			logger.info("Using incoming child list");
			childList = incomingChildList;
			childList.clear();
		}
		
		if(topLevel instanceof Node){
			if(((Node)topLevel).getQuantity()>0){
				for(Spatial child : ((Node)topLevel).getChildren()){
					collapseToSingleLevelImpl((Node)topLevel, child, childList, nodesToKill);
				}
			}
			
			//logger.info("childList contains " + childList.size() + " objects");
			for(int i=0; i<childList.size(); i++){
				((Node)topLevel).attachChild(childList.get(i));
			}
			
			// we need to go in reverse so that the objects are still accessible to be removed (they were added in descending order)
			//logger.info("nodesToKill contains " + nodesToKill.size() + " objects");
			for(int i=nodesToKill.size()-1; i>-1; i--){
				nodesToKill.get(i).removeFromParent();
			}
		}
		else{
			//logger.warn("Collapse to single level requires a Node");
		}
		
		//logger.info("Optimization Complete");
	}
	
	private static void collapseToSingleLevelImpl(Node parentBeingCollapsedTo, Spatial child, ArrayList<Spatial> childList, ArrayList<Spatial> nodesToKill){
		//logger.info("Examining\t"+parentBeingCollapsedTo.getName()+":"+child.getName());
		if(child instanceof Node){
			if(((Node)child).getQuantity()>0){
				nodesToKill.add(child);
				for(Spatial subChild : ((Node)child).getChildren()){
					//logger.info("Submitting\t"+parentBeingCollapsedTo.getName()+":"+subChild.getName());
					collapseToSingleLevelImpl(parentBeingCollapsedTo, subChild, childList, nodesToKill);
				}
			}
			// now that we've removed everything from the child, take it out of the scene
			//child.removeFromParent();
		}
		else{
			// if the object is not a spatial then we move it up to the top level
			//logger.info("Collapsing object to top level "+parentBeingCollapsedTo.getName()+": " + child.getName());
			
			childList.add(child);
			//logger.info("Object Added To Templist");
			
			/*
			if(child.removeFromParent()){
				parentBeingCollapsedTo.attachChild(child);
				logger.info("spatial moved");
			}
			*/
		}
	}
	
	/**
	 * 
	 * @param s  The {@link Spatial} to find the extents of
	 * @return An array made up of two {@link Vector3f} objects.  Index 0
	 * represents the minimum extent while index 1 represents the maximum
	 * extent
	 */
	public static Vector3f[] findObjectExtents(Spatial s){
		Vector3f min = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
		Vector3f max = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
		findObjectExtentsImpl(s, min, max);
		return new Vector3f[]{min, max};
	}
	
	private static void findObjectExtentsImpl(Spatial s, Vector3f min, Vector3f max){
		if(s instanceof Node){
			if(((Node)s).getQuantity()>0){
				for(Spatial child : ((Node)s).getChildren()){
					findObjectExtentsImpl(child, min, max);
				}
			}
		}
		else if(s instanceof Geometry){
			float tempX;
			float tempY;
			float tempZ;
			FloatBuffer buffer = ((Geometry)s).getVertexBuffer();
			buffer.rewind();
			for(int i=0; i<((Geometry)s).getVertexCount(); i++){
				tempX=buffer.get(i*3);
				tempY=buffer.get((i*3)+1);
				tempZ=buffer.get((i*3)+2);
				if(tempX<min.x || Float.isNaN(min.x)){
					logger.info("Min X updated");
					min.x=tempX;
				}
				if(tempY<min.y || Float.isNaN(min.y)){
					logger.info("Min Y updated");
					min.y=tempY;
				}
				if(tempZ<min.z || Float.isNaN(min.z)){
					logger.info("Min Z updated");
					min.z=tempZ;
				}
				if(tempX>max.x || Float.isNaN(max.x)){
					logger.info("Max X updated");
					max.x=tempX;
				}
				if(tempY>max.y || Float.isNaN(max.y)){
					logger.info("Max Y updated");
					max.y=tempY;
				}
				if(tempZ>max.z || Float.isNaN(max.z)){
					logger.info("Max Z updated");
					max.z=tempZ;
				}
			}
		}
	}
	
	/**
	 * Internally adjusts a model by its own offset from the origin so that its
	 * point closest to the origin rests <em>at</em> the origin
	 * @param s The model to adjust
	 */
	public static void relocateObjectToOrigin(Spatial s){
		Vector3f pointClosestToOrigin = findObjectExtents(s)[0];
		relocateObjectToOriginImpl(s, pointClosestToOrigin);
	}
	
	private static void relocateObjectToOriginImpl(Spatial s, Vector3f pointClosestToOrigin){
		if(s instanceof Node){
			if(((Node)s).getQuantity()>0){
				for(Spatial child : ((Node)s).getChildren()){
					relocateObjectToOriginImpl(child, pointClosestToOrigin);
				}
			}
		}
		else if(s instanceof Geometry){
			float tempX;
			float tempY;
			float tempZ;
			FloatBuffer buffer = ((Geometry)s).getVertexBuffer();
			buffer.rewind();
			for(int i=0; i<((Geometry)s).getVertexCount(); i++){
				tempX=buffer.get(i*3);
				tempY=buffer.get((i*3)+1);
				tempZ=buffer.get((i*3)+2);
				buffer.put(i*3, tempX-pointClosestToOrigin.x);
				buffer.put((i*3)+1, tempY-pointClosestToOrigin.y);
				buffer.put((i*3)+2, tempZ-pointClosestToOrigin.z);
			}
		}
	}
}
