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
package edu.poly.bxmc.betaville.terrain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.IIOException;

import org.apache.log4j.Logger;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.jme.map.BoundingBox;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.module.FrameSyncModule;
import edu.poly.bxmc.betaville.module.ModuleNameException;

/**
 * Static utilities to load terrain
 * @author Skye Book
 *
 */
public class TerrainLoader {
	private static final Logger logger = Logger.getLogger(TerrainLoader.class);

	private ILocation start;
	private OSMTileRequestGenerator tileGenerator;
	private MappedTerrainGenerator gen = null;
	private String url;
	private int eastSoFar=0;
	private int northSoFar=0;
	private int distanceEast;
	private int distanceNorth;
	private int numLat=0;
	private int numLon=0;
	private Vector3f originalLoc=null;
	private boolean useElevations;
	private BoundingBox acceptableTileArea;

	private int imageLoadAttemptLimit = 2;

	private TileCache cache;

	private AtomicBoolean loaderLock = new AtomicBoolean(false);
	private ArrayList<Spatial> loader = new ArrayList<Spatial>();
	
	public TerrainLoader(ILocation center, final int tilesEast, final int tilesNorth, int zoomLevel, boolean useElevations) {
		start=center;
		
		// if the application isn't initialized, add it to the tasks that get performed on initialization
		if(SceneGameState.getInstance()==null){
			BetavilleNoCanvas.addCompletionListener(new IAppInitializationCompleteListener() {
				public void applicationInitializationComplete() {
					try {
						SceneGameState.getInstance().addModuleToUpdateList(new LoadModule(tilesNorth*tilesEast));
					} catch (ModuleNameException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		else{
			try {
				SceneGameState.getInstance().addModuleToUpdateList(new LoadModule(tilesNorth*tilesEast));
			} catch (ModuleNameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// northeast coordinate
		UTMCoordinate clone = start.getUTM().clone();
		clone.move(tilesEast, tilesNorth, 0);
		clone.setAltitude(0);
		GPSCoordinate ne = clone.getGPS().clone();

		GPSCoordinate swClone = start.getGPS().clone();
		swClone.setAltitude(0);


		acceptableTileArea = new BoundingBox(
				new GPSCoordinate(0, ne.getLatitude(), swClone.getLongitude()),
				ne,
				swClone,
				new GPSCoordinate(0, start.getGPS().getLatitude(), ne.getLongitude()));
		logger.info("BOUNDING BOX IS " + acceptableTileArea.toString());

		this.distanceEast=tilesEast;
		this.distanceNorth=tilesNorth;
		int checkedZoomLevel = zoomLevel;
		if(zoomLevel>18) checkedZoomLevel=18;
		else if(zoomLevel<0) checkedZoomLevel=0;
		tileGenerator = new OSMTileRequestGenerator(checkedZoomLevel);
		cache = new TileCache(tileGenerator.baseTileServerURL);
		this.useElevations=useElevations;
	}

	private GPSCoordinate splitDistances(GPSCoordinate one, GPSCoordinate two){
		logger.info("Averaging:\n"+one.toString()+"\n"+two.toString());
		double avgLat = (one.getLatitude()+two.getLatitude())/2;
		double avgLon = (one.getLongitude()+two.getLongitude())/2;
		double avgAlt = (one.getAltitude()+two.getAltitude())/2;
		return new GPSCoordinate(avgAlt, avgLat, avgLon);
	}

	public void loadTerrain(){
		url = tileGenerator.generateTileRequest(start);
		logger.info("MID-POINT: " +splitDistances(tileGenerator.getCurrentBoundingBox().getSw(), tileGenerator.getCurrentBoundingBox().getNe()).toString());
		while(eastSoFar<distanceEast){

			while(northSoFar<distanceNorth){
				loadSingleTerrain();
				url = tileGenerator.shiftAndGetRequest(0, 1);
				numLat++;
				northSoFar++;
			}


			// reset the northing counter
			url = tileGenerator.shiftAndGetRequest(1, numLat*-1);
			numLat=0;
			numLon++;
			northSoFar=0;
			eastSoFar++;
		}
		logger.info("Finished generation of all terrain");
	}

	private void loadSingleTerrain(){
		if(useElevations){
			logger.info("Elevations enabled");
			gen = new USGSTerrainGenerator(15, tileGenerator.getCurrentBoundingBox().getSw(), (float)MapManager.greatCircleDistanced(tileGenerator.getCurrentBoundingBox().getSw(), tileGenerator.getCurrentBoundingBox().getSe()));
			//gen = new FlatTerrainGenerator(tileGenerator.getCurrentBoundingBox().getSw(), (float)MapManager.greatCircleDistanced(tileGenerator.getCurrentBoundingBox().getSw(), tileGenerator.getCurrentBoundingBox().getSe()));
			gen.addTerrainCompletionListener(new ITerrainCompletionListener() {
				public void terrainGenerationComplete(final Spatial terrainObject) {
					setupTerrainObject(terrainObject, 1);
				}
			});
			gen.createTerrainBlock();
		}
		else{
			//logger.info("Elevations disabled");
			//gen = new HeavyFlatTerrainGenerator(3, tileGenerator.getCurrentBoundingBox().getSw(), (float)MapManager.greatCircleDistanced(tileGenerator.getCurrentBoundingBox().getSw(), tileGenerator.getCurrentBoundingBox().getSe()));
			float distanceAcross = (float)MapManager.greatCircleDistanced(tileGenerator.getCurrentBoundingBox().getSw(), tileGenerator.getCurrentBoundingBox().getSe());
			float distanceHigh = (float)MapManager.greatCircleDistanced(tileGenerator.getCurrentBoundingBox().getSw(), tileGenerator.getCurrentBoundingBox().getNw());
			//logger.info("distance from west to east: " + distanceAcross);
			//logger.info("distance from south to north: " + distanceHigh);

			// use consistent number so we get a square..
			//Quad terrainObject = new Quad(tileGenerator.xySet[0]+"x"+tileGenerator.xySet[1]+"y", distanceAcross, distanceAcross);
			//terrainObject.rotateUpTo(Vector3f.UNIT_Z);
			
			// we can use a shared mesh if an object has already been created for this latitude
			//TriMesh copy = searchTerrainForBlockAtLatitude(tileGenerator.xySet[1]);
			TriMesh copy = null;
			TriMesh terrainObject;
			if(copy==null){
				logger.info("No object previously found at this latitude, creating a fresh mesh");
				terrainObject = new TerrainBlock("", 2, /*new Vector3f(Scale.fromMeter(distanceAcross),Scale.fromMeter(distanceAcross),Scale.fromMeter(distanceAcross))*/ new Vector3f(distanceAcross, 1, distanceAcross), new float[]{0, 0, 0, 0,
				/*0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0*/}, new Vector3f(0, 0, 0));
			}
			else{
				terrainObject = new SharedMesh(copy);
			}
			setupTerrainObject(terrainObject, 1);
		}
	}
	
	private TriMesh searchTerrainForBlockAtLatitude(int yValue){
		String searchString = "x"+yValue+"y";
		Node terrainNode = SceneGameState.getInstance().getTerrainNode();
		if(terrainNode.getQuantity()>0){
			//logger.info("terrainNode has " + terrainNode.getQuantity() + " children");
			for(Spatial child : terrainNode.getChildren()){
				if(child instanceof TriMesh && child.getName().contains(searchString)){
					return ((TriMesh)child);
				}
			}
		}
		return null;
	}

	private void setupTerrainObject(final Spatial terrainObject, int attempt){
		GeometryUtilities.removeRenderState(terrainObject, StateType.Texture);
		//MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		//ms.setDiffuse(ColorRGBA.gray);
		//terrainObject.setRenderState(ms);
		terrainObject.setName(tileGenerator.xySet[0]+"x"+tileGenerator.xySet[1]+"y");
		terrainObject.updateRenderState();
		terrainObject.setLocalScale(1f/SceneScape.SceneScale);
		Vector3f loc=null;
		if(originalLoc==null){
			originalLoc = JME2MapManager.instance.locationToBetaville(tileGenerator.getCurrentBoundingBox().getSw());
			loc = originalLoc.clone();
		}
		else{
			/*
			float latStride = numLat*gen.getBlockSize();
			//if(latStride!=0)latStride=(latStride/1)+1;
			if(latStride!=0)latStride+=(gen.distanceBetweenPolls()*numLat);
			latStride = latStride/SceneScape.SceneScale;

			float lonStride = numLon*gen.getBlockSize();
			//if(lonStride!=0)lonStride=(latStride/1)+1;
			if(lonStride!=0)lonStride+=(gen.distanceBetweenPolls()*numLon);
			lonStride = lonStride/SceneScape.SceneScale;

			logger.info("latStride: "+latStride);
			logger.info("lonStride: "+lonStride);
			loc = new Vector3f(originalLoc.getX()+latStride,0,originalLoc.getZ()+lonStride);
			logger.info("putting terrain at: " + loc.toString());
			 */

			// I wonder if this will work?!
			loc = JME2MapManager.instance.locationToBetaville(tileGenerator.getCurrentBoundingBox().getSw());
		}
		loc.setY(0);
		terrainObject.setLocalTranslation(loc);

		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		url = cache.requestImageTile(tileGenerator.zoomLevel, tileGenerator.xySet[0], tileGenerator.xySet[1]).toString();

		// attempt to load the texture
		Texture t = null;
		try{
			t = TextureManager.loadTexture(new URL(url), false);
		} catch (Exception e){
			/*
			 * If the file is corrupted, we should end up here.  If it simply failed to load,
			 * we'll end up below at the null check for the texture object
			 */
			if(e instanceof IIOException){
				if(attempt<imageLoadAttemptLimit+1){
					logger.error("Tile image could not be loaded (attempt "+attempt+" of "+imageLoadAttemptLimit+", deleting the file and trying again");
					cache.deleteImageCacheEntry(tileGenerator.zoomLevel, tileGenerator.xySet[0], tileGenerator.xySet[1]);
					setupTerrainObject(terrainObject, attempt+1);
				}
				else{
					logger.warn("Maximum number of image loading attempts reached, moving on");
				}
			}
			else if(e instanceof MalformedURLException){
				e.printStackTrace();
			}
		}


		if(t==null){
			logger.error("There was a problem loading the texture!");
		}
		if(t!=null){
			t.setRotation(Rotator.angleZ(270));
			t.setTranslation(new Vector3f(0, 1, 0));
			ts.setTexture(t);
			terrainObject.setRenderState(ts);
			terrainObject.updateRenderState();

			/*
			try {
				File colladaFile = new File(
						cache.createZoomAndXFolder(tileGenerator.zoomLevel, tileGenerator.xySet[0])+"/"+tileGenerator.xySet[1]+".dae");
				if(!colladaFile.exists()){
					ColladaExporter exporter = new ColladaExporter(colladaFile, terrainObject, false);
					exporter.writeData();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}

		// if the application isn't initialized, add it to the tasks that get performed on initialization
		if(SceneGameState.getInstance()==null){
			BetavilleNoCanvas.addCompletionListener(new IAppInitializationCompleteListener() {
				public void applicationInitializationComplete() {
					addToScene(terrainObject);
				}
			});
		}
		else{
			addToScene(terrainObject);
		}
	}

	private void addToScene(Spatial terrainObject){
		while(loaderLock.get()){
			// wait for the lock to expire
		}
		loaderLock.set(true);
		loader.add(terrainObject);
		loaderLock.set(false);
	}

	private class LoadModule extends edu.poly.bxmc.betaville.module.Module implements FrameSyncModule{

		private SceneGameState sceneGameState;
		private int total;
		private int loadedSoFar=0;
		private float targetTPF = .005f;

		public LoadModule(int total) {
			super("Terrain Load Module");
			// retrieve the scene game state ahead of time - yes, micro-optimization but it _can't_ hurt
			sceneGameState = SceneGameState.getInstance();
		}

		public void deconstruct() {}

		public void frameUpdate(float timePerFrame) {
			while(loaderLock.get()){
				// wait for the lock to expire
			}
			long start = System.currentTimeMillis();
			loaderLock.set(true);
			for(int i=0; i<loader.size(); i++){
				sceneGameState.getTerrainNode().attachChild(loader.remove(i));
				loadedSoFar++;
				
				// if we've reached the time limit for this update, move on to release the lock and return to the render loop
				if(System.currentTimeMillis()-start>targetTPF){
					break;
				}
			}
			//loader.clear();
			loaderLock.set(false);
			
			if(total==loadedSoFar){
				logger.info("Loading complete, removing LoadModule");
				sceneGameState.removeModuleFromUpdateList(this);
			}
			
			//SceneGameState.getInstance().getTerrainNode().attachChild(terrainObject);
			//Box debugBox = new Box("box", terrainObject.getLocalTranslation(), .1f, .1f, .1f);
			//GeometryUtilities.removeRenderState(debugBox, StateType.Texture);
			//SceneGameState.getInstance().getTerrainNode().attachChild(debugBox);
		}

	}
}
