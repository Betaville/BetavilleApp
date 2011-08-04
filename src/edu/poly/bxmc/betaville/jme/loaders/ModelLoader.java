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
package edu.poly.bxmc.betaville.jme.loaders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.state.RenderState.StateType;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.ModelFormatException;
import com.jmex.model.collada.ThreadSafeColladaImporter;
import com.jmex.model.converters.ObjToJme;
import com.jmex.model.ogrexml.OgreLoader;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.jme.loaders.util.OBJScaler;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ModeledDesign;

/**
 * @author Skye Book
 *
 */
public class ModelLoader {
	private static Logger logger = Logger.getLogger(ModelLoader.class);
	private URL modelURL=null;
	private Node model = null;
	private boolean replacement;
	private String originalFile;

	/**
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * 
	 */
	public ModelLoader(ModeledDesign design, boolean isInCache, String originalFile) throws IOException, URISyntaxException{
		if(originalFile==null){
			replacement=false;
		}
		else{
			replacement=true;
			this.originalFile=originalFile;
		}
		
		String fileExtension = design.getFilepath().substring(design.getFilepath().lastIndexOf(".")+1, design.getFilepath().length());
		if(isInCache){
			modelURL = new URL(SettingsPreferences.getDataFolder()+design.getFilepath());
		}
		else modelURL = new URL(design.getFilepath());
		
		
		ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_MODEL,
				new SimpleResourceLocator(modelURL));
		if(design.isTextured()){
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
					new SimpleResourceLocator(new URL(modelURL.toString().substring(0, modelURL.toString().lastIndexOf("/")+1))));
		}
		
		
		// determine file type
		if(fileExtension.matches("[Dd][Aa][Ee]")){
			loadDAE(design);
		}
		else if(fileExtension.matches("[Oo][Bb][Jj]")){
			
			loadOBJ(design);
		}
		// TODO: For OGRE we need to check for a two part file extension (change to regex for the end of the file string?)
		else if(fileExtension.matches("[Xx][Mm][Ll]")){
			try {
				loadOGRE(design);
			} catch (ModelFormatException e) {
				e.printStackTrace();
			}
		}
		else if(fileExtension.matches("[Jj][Mm][Ee]")){
			logger.debug("Loading JME " + design.getID());
			loadJME(new File(modelURL.toURI()));
		}
	}
	
	private void loadDAE(ModeledDesign design) throws IOException, URISyntaxException{
		ThreadSafeColladaImporter importer = new ThreadSafeColladaImporter(design.getName());
		importer.load(modelURL.openStream());
		model = importer.getModel();
		
		//GeometryUtilities.printInformation(logger, model, true, true, true, true);
		
		// up axis - kind of tough to do correctly without zeroed transforms (though apparently not all exporters are compliant in writing this out correctly)
		if(importer.getUpAxis()!=null){
			String up = importer.getUpAxis().toLowerCase();
			if(!up.startsWith("y")){
				logger.info("Non-Standard Up-Axis: " + up);
				if(up.startsWith("z")){
					if(importer.getTool().toLowerCase().contains("sketchup")){
						logger.info("Model built in Sketchup, rotation should be safe");
						model.setLocalRotation(Rotator.fromThreeAngles(270, 0, 0));
						design.setRotationX(270);
					}
				}
				else{ // This means that the up is X (should be quite rare)
					model.setLocalRotation(Rotator.fromThreeAngles(0, 0, 270));
					design.setRotationZ(270);
				}
			}
		}
		else logger.warn("The Up-Axis could not be determined ");

		model.setName(design.getName()+"$local");
		model.setLocalScale(importer.getUnitMeter()/SceneScape.SceneScale);
		if(importer.getTool().toLowerCase().contains("blender v:249 - illusoft collada exporter")){
			logger.info("COLLADA document exported from Blender.  Scaling bug expected");
			if(importer.getUnitName().contains("centimeter")&&importer.getUnitMeter()==.01f){
				logger.info("Scaling bug found, using `1 meter = 1 unit`");
				//model.setLocalScale(1f);
			}
		}
		finishSetup(design);
	}
	
	private void loadOGRE(Design design) throws IOException, ModelFormatException{
		OgreLoader ol = new OgreLoader();
		model = (Node)ol.loadModel(modelURL, design.getName()+"$local");
		finishSetup(design);
	}
	
	private void loadOBJ(Design design){
		ObjToJme converter = new ObjToJme();
		// Point the converter to where it will find the .mtl file
		converter.setProperty("mtllib", modelURL);
		
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		try {
			// Use the format converter to convert .obj to .jme
			converter.convert(modelURL.openStream(), BO, design.getFullIdentifier());
			converter.setProperty("texdir", modelURL.toString().substring(0, modelURL.toString().lastIndexOf("/")+1));
			// Load the binary .jme format into a scene graph
			Object building = (Object) BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
			if(building instanceof Node){
				model = (Node)building;
			}
			else{
				model = new Node("design");
			}
			model.setName(design.getName()+"$local");
			model.setLocalTranslation(JME2MapManager.instance.locationToBetaville(design.getCoordinate()));
			model.setLocalScale(OBJScaler.fixScale(modelURL)/SceneScape.SceneScale);
			finishSetup(design);
		}
		catch(Exception e){ e.printStackTrace(); }
	}
	
	private void finishSetup(Design design){
		GeometryUtilities.setMaterialFaceApplication(model, true, true);
		
		//GeometryUtilities.printModelOrigins(model);
		if(!((ModeledDesign)design).isTextured()){
			model.clearRenderState(StateType.Texture);
		}
		else{
			GeometryUtilities.setupTextureStorage(model);
		}
		File fileout;
		try {
			if(replacement){
				model.setName(design.getFullIdentifier());
				String newFilename;
				if(originalFile.contains("_")){
					int currentIteration = Integer.parseInt(originalFile.substring(originalFile.lastIndexOf("_")+1, originalFile.lastIndexOf(".")));
					newFilename = design.getID()+"_"+(currentIteration+1)+".jme";
				}
				else{
					newFilename=design.getID()+"_"+1+".jme";
				}
				fileout = new File(new URL(SettingsPreferences.getDataFolder()+newFilename).toURI());
				design.setFilepath(newFilename);
			}
			else{
				fileout = new File(new URL(SettingsPreferences.getDataFolder()+"local/"+design.getFullIdentifier().replaceAll(" ", "")+".jme").toURI());
				design.setFilepath(design.getFullIdentifier()+".jme");
			}
			fileout.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(fileout);
			BinaryExporter.getInstance().save(model, fos);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void loadJME(File file) throws IOException{
		Object loadedObject = BinaryImporter.getInstance().load(file);
		if(loadedObject instanceof Node){
			model = (Node)loadedObject;
			//GeometryUtilities.collapseToSingleLevel(model);
			// if there is only one object beneath this one and it is a node, then remove the top-level node
			/*
			if(((Node)model).getQuantity()==1){
				if(((Node)model).getChild(0) instanceof Node){
					logger.info("Removing top level node from "+file.getName());
					model = (Node)((Node)model).getChild(0);
					model.setLocalScale(1f/SceneScape.SceneScale);
				}
			}
			*/
			if(!SettingsPreferences.isTextured()) GeometryUtilities.removeRenderState(model, StateType.Texture);
			GeometryUtilities.setMaterialFaceApplication(model, true, true);
		}
	}
	
	public Node getModel(){
		return model;
	}
}
