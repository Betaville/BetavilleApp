/**
 * 
 */
package edu.poly.bxmc.betaville.jme.loaders;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.model.ModeledDesign;

/**
 * Loads models into Betaville by the bulk..  as if the
 * name didn't imply as much
 * @author Skye Book
 *
 */
public class BulkLoader {
	private static final Logger logger = Logger.getLogger(BulkLoader.class);
	
	private ILocation origin;
	private volatile int currentCounter=0;
	
	private IBulkLoadProgressListener progress;

	public BulkLoader(ILocation commonOriginForFiles, IBulkLoadProgressListener progressListener){
		origin = commonOriginForFiles;
		progress = progressListener;
	}
	
	/**
	 * Loads the specified files into Betaville.  Uses Filenames as the model's
	 * names
	 * @param files The files to load
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public void load(List<File> files) throws IOException, URISyntaxException{
		while(currentCounter<files.size()){
			// begin the laoding
			progress.modelLoadStarting(files.get(currentCounter).getName(), currentCounter+1, files.size());
			
			// parse the model
			ModeledDesign design = new ModeledDesign(files.get(currentCounter).getName(), origin.getUTM().clone(), "Not Supplied By Bulk Model", SceneScape.getCity().getCityID(), SettingsPreferences.getUser(), "Not Supplied By Bulk Model", files.get(currentCounter).toURI().toURL().toString(), "Not Supplied By Bulk Model", true, 0, 0, 0, true);
			ModelLoader ml = new ModelLoader(design, false, null);
			progress.modelParsed(currentCounter);
			
			// calculate the geographical offset
			Vector3f closestToOrigin = GeometryUtilities.findObjectExtents(ml.getModel())[0];
			ILocation original = design.getCoordinate().clone();
			design.getCoordinate().move((int)closestToOrigin.z*-1, (int)closestToOrigin.x*-1, 0);
			ILocation corrected = design.getCoordinate().clone();
			
			
			
			// move the model to 0,0,0 on its own axis
			GeometryUtilities.relocateObjectToOrigin(ml.getModel());
			SceneGameState.getInstance().getDesignNode().attachChild(ml.getModel());
			SceneScape.getCity().addDesign(design);
			SceneGameState.getInstance().getDesignNode().getChild(design.getFullIdentifier()).setLocalTranslation(MapManager.locationToBetaville(design.getCoordinate()));
			SceneGameState.getInstance().getDesignNode().getChild(design.getFullIdentifier()).updateRenderState();
			progress.modelMovedToLocation(currentCounter, original, corrected);
			
			
			//SceneScape.getCity().addDesign(design);
			logger.debug("Model Added");
			
			// do model upload
			
			incrementCurrentCounter();
		}
	}
	
	private synchronized void incrementCurrentCounter(){
		currentCounter++;
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
