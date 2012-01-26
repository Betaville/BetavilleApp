/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryExporter;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.model.Design.Classification;
import edu.poly.bxmc.betaville.model.ModeledDesign;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.net.PhysicalFileTransporter;
import edu.poly.bxmc.betaville.net.ProtectedManager;
import edu.poly.bxmc.betaville.net.SecureClientManager;
import edu.poly.bxmc.betaville.updater.UpdaterPreferences;

/**
 * Loads models into Betaville by the bulk..  as if the
 * name didn't imply as much
 * @author Skye Book
 *
 */
public class BulkLoader {
	private static final Logger logger = Logger.getLogger(BulkLoader.class);

	private ILocation origin;
	private AtomicInteger currentCounter=new AtomicInteger(0);
	private AtomicInteger completionCounter=new AtomicInteger(0);

	private String xPrefix;
	private String yPrefix;
	private String zPrefix;

	private IBulkLoadProgressListener progress;
	
	private ProtectedManager manager;

	public BulkLoader(ILocation commonOriginForFiles, IBulkLoadProgressListener progressListener){
		this(commonOriginForFiles, progressListener, "x_", "y_", "z_");
	}

	public BulkLoader(ILocation commonOriginForFiles, IBulkLoadProgressListener progressListener, String xPrefix, String yPrefix, String zPrefix){
		origin = commonOriginForFiles;
		progress = progressListener;
		this.xPrefix=xPrefix;
		this.yPrefix=yPrefix;
		this.zPrefix=zPrefix;
	}

	/**
	 * Loads the specified files into Betaville.  Uses Filenames as the model's
	 * names
	 * @param files The files to load
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void load(final List<File> files) throws UnknownHostException, IOException{
		UpdaterPreferences.setBaseEnabled(false);
		manager = new SecureClientManager(new ArrayList<Module>(), true);
		while(currentCounter.get()<(files.size())){

			try {
				loadModel(files.get(currentCounter.get()), files.size());
				incrementCurrentCounter();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			/*
			SettingsPreferences.getThreadPool().submit(new Runnable() {
				public void run() {

				}
			});
			 */
		}
		/*
		while(completionCounter<files.size()){
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		 */
		logger.info("Bulk load completed!");
		UpdaterPreferences.setBaseEnabled(true);
	}

	private void loadModel(File file, int numberFiles) throws IOException, URISyntaxException{
		// begin the loading
		progress.modelLoadStarting(file.getName(), currentCounter.get()+1, numberFiles);

		// Read the x/y/z offsets

		String fileString = file.getName();
		
		
		String xOffsetString = "0";
		String yOffsetString = "0";
		String zOffsetString = "0";
		try{
			xOffsetString = fileString.substring(fileString.indexOf(xPrefix)+xPrefix.length(), fileString.indexOf(yPrefix));
			yOffsetString = fileString.substring(fileString.indexOf(yPrefix)+xPrefix.length(), fileString.indexOf(zPrefix));
			// this is somewhat of a guess...  if a file is named something like "object.mesh.xml" then we're screwed
			zOffsetString = fileString.substring(fileString.indexOf(zPrefix)+xPrefix.length(), fileString.lastIndexOf("."));
		}catch(IndexOutOfBoundsException e){
			// this will be thrown if an improperly formatted filename has been found
			logger.error("Improperly formatted file name: "+fileString+"\n"+
					"Here are the offsets we do have: (x)"+xOffsetString+" (y)"+yOffsetString+" (z)"+zOffsetString);
		}
		


		logger.info("String ops DONE");

		// parse the model

		String nameToUse = fileString;
		try{
			nameToUse = fileString.substring(0, file.getName().indexOf("_x_"));
		}catch(IndexOutOfBoundsException e){
			// this will be thrown if an improperly formatted filename has been found
			logger.error("Improperly formatted file name: "+fileString);
		}

		ModeledDesign design = new ModeledDesign(nameToUse, origin.getUTM().clone(), "Not Supplied By Bulk Model", SettingsPreferences.getCity().getCityID(), SettingsPreferences.getUser(), "Not Supplied By Bulk Model", file.toURI().toURL().toString(), "Not Supplied By Bulk Model", true, 0, 0, 0, true);
		logger.info("Entering ModelLoader");
		ModelLoader ml = new ModelLoader(design, false, null);
		logger.info("ModelLoader created (COLLADA has been parsed at this point)");
		progress.modelParsed(currentCounter.get());

		/*
		 * This code no longer applies since we can't account for the proper transformations once
		 * outside of the modeling application.  It is for this reason that we encourage any batch export
		 * script to do a freeze on the object's transformations so that it is properly zeroed out when
		 * exported from the application.  This will allow the batch importer to properly do its job by
		 * placing the object at its own point of origin while still accounting for its offset within the
		 * original DCC tool's scene (this offset is scripted through the filename)
		 * 
		// calculate the geographical offset
		Vector3f closestToOrigin = GeometryUtilities.findObjectExtents(ml.getModel())[0];
		ILocation original = design.getCoordinate().clone();
		design.getCoordinate().move((int)closestToOrigin.z*-1, (int)closestToOrigin.x*-1, 0);
		ILocation corrected = design.getCoordinate().clone();

		// move the model to 0,0,0 on its own axis
		GeometryUtilities.relocateObjectToOrigin(ml.getModel());
		logger.info("Model Originally At: " + closestToOrigin.toString()+"\nNow At: "+ GeometryUtilities.findObjectExtents(ml.getModel())[0].toString());
		SceneGameState.getInstance().getDesignNode().attachChild(ml.getModel());
		SceneScape.getCity().addDesign(design);
		SceneGameState.getInstance().getDesignNode().getChild(design.getFullIdentifier()).setLocalTranslation(MapManager.locationToBetaville(design.getCoordinate()));
		SceneGameState.getInstance().getDesignNode().getChild(design.getFullIdentifier()).updateRenderState();
		progress.modelMovedToLocation(currentCounter, original, corrected);
		 */

		// calculate the geographical offset
		ILocation original = design.getCoordinate().clone();
		logger.info("Original object cloned");

		float xNum=Float.parseFloat(xOffsetString);
		float yNum=Float.parseFloat(yOffsetString);
		float zNum=Float.parseFloat(zOffsetString);
		//float xNum=0;
		//float yNum=0;
		//float zNum=0;

		// if the have had their transformations frozen, we need to adjust the geometry internally
		if(xNum==0 && yNum==0 && zNum ==0){
			logger.warn("It would appear that the transformations have been frozen prior to being" +
			"imported into Betaville.");

			/*
			logger.warn("Attempting to transform the internal geometry data");
			Vector3f[] extents = GeometryUtilities.findObjectExtents(ml.getModel());

			// set the object's offsets correctly
			xNum = extents[0].x;
			yNum = extents[0].y;
			zNum = extents[0].z;
			 */

			/*
			ml.getModel().setModelBound(new BoundingBox());
			ml.getModel().updateModelBound();
			Vector3f center = ml.getModel().getWorldBound().getCenter();
			logger.info("bounding center " + center.toString());
			xNum = center.x;
			yNum = center.y;
			zNum = center.z;
			 */


			logger.info("Object offsets calculated to be " + xNum+", "+yNum+", "+zNum);
			logger.info("Continuing with internal geometry adjustment");

			GeometryUtilities.adjustObject(ml.getModel(), xNum*-1, yNum*-1, zNum*-1);
		}

		design.getCoordinate().move((int)zNum, (int)xNum, (int)yNum);
		design.setClassification(Classification.BASE);
		logger.debug("design coordinate transformed");
		ILocation corrected = design.getCoordinate().clone();
		logger.debug("corrected object cloned");

		SceneGameState.getInstance().getDesignNode().attachChild(ml.getModel());
		logger.info("model added to scene");
		SettingsPreferences.getCity().addDesign(design);
		logger.info("model design object added to city");
		SceneGameState.getInstance().getDesignNode().getChild(design.getFullIdentifier()).setLocalTranslation(JME2MapManager.instance.locationToBetaville(design.getCoordinate()));
		logger.info("object moved to correct orientation");
		SceneGameState.getInstance().getDesignNode().getChild(design.getFullIdentifier()).updateRenderState();
		progress.modelMovedToLocation(currentCounter.get(), original, corrected);


		//SceneScape.getCity().addDesign(design);
		logger.debug("Model Added");

		// do model upload
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		BinaryExporter.getInstance().save(SceneGameState.getInstance().getDesignNode().getChild(design.getFullIdentifier()), bo);
		//int response = NetPool.getPool().getSecureConnection().addBase(design, SettingsPreferences.getUser(), SettingsPreferences.getPass(), new PhysicalFileTransporter(bo.toByteArray()));
		
		try {
			// sleep for a little bit to catch breath
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int response = manager.addBase(design, new PhysicalFileTransporter(bo.toByteArray()), null, PhysicalFileTransporter.readFromFileSystem(ml.getPackedFile()));
		if(response>0){
			// get handle on model
			Spatial model = SceneGameState.getInstance().getDesignNode().getChild(design.getFullIdentifier());
			// change the ID (now its not local)
			design.setID(response);
			// update the name of the model in the scene
			model.setName(design.getFullIdentifier());
		}

		incrementCompletionCounter();
	}

	private synchronized void incrementCurrentCounter(){
		currentCounter.incrementAndGet();
	}

	private synchronized void incrementCompletionCounter(){
		completionCounter.incrementAndGet();
	}

	public ILocation getOrigin(){
		return origin;
	}

	public interface IBulkLoadProgressListener{
		/**
		 * Provides notification the beginning of a model being loaded
		 * @param filename The filename of the model
		 * @param currentFile The current file being loaded (i.e: first, second, third)
		 * @param totalNumberFiles The total number of files to be loaded
		 */
		public void modelLoadStarting(String filename, int currentFile, int totalNumberFiles);

		/**
		 * Provides notification that a model has been parsed and loaded into the engine.
		 * @param currentFile
		 */
		public void modelParsed(int currentFile);

		/**
		 * Provides notification that a model's geographic location has been calculated and
		 * the model has been relocated to the correct location.
		 * @param currentFile The current file being loaded (i.e: first, second, third)
		 */
		public void modelMovedToLocation(int currentFile, ILocation originalLocation, ILocation correctedLocation);

		/**
		 * Provides notification that a model is currently being uploaded
		 * @param currentFile The current file being loaded (i.e: first, second, third)
		 */
		public void modelUploadStarted(int currentFile);

		/**
		 * Provides notification that an upload operation has finished
		 * @param currentFile The current file being loaded (i.e: first, second, third)
		 * @param success True if the upload operation was successful, false if it failed
		 */
		public void modelUploadCompleted(int currentFile, boolean success);
	}

}
