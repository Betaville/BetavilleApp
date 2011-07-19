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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.exporters.ColladaExporter;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.jme.map.BoundingBox;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;

/**
 * Static utilities to load terrain
 * @author Skye Book
 *
 */
public class TerrainLoader {
	private static final Logger logger = Logger.getLogger(TerrainLoader.class);

	private ILocation start;
	private OSMTileRequestGenerator tileGenerator;
	private MappedTerrainGenerator gen;
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
	
	private TileCache cache;

	public TerrainLoader(ILocation center, int tilesEast, int tilesNorth, int zoomLevel, boolean useElevations) {
		start=center;
		
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
		}
		else{
			logger.info("Elevations disabled");
			gen = new HeavyFlatTerrainGenerator(5, tileGenerator.getCurrentBoundingBox().getSw(), (float)MapManager.greatCircleDistanced(tileGenerator.getCurrentBoundingBox().getSw(), tileGenerator.getCurrentBoundingBox().getSe()));
		}

		//skgen = new FlatTerrainGenerator(tileGenerator.getCurrentBoundingBox().getSw(), (float)MapManager.greatCircleDistanced(tileGenerator.getCurrentBoundingBox().getSw(), tileGenerator.getCurrentBoundingBox().getSe()));
		gen.addTerrainCompletionListener(new ITerrainCompletionListener() {
			public void terrainGenerationComplete(final Spatial terrainObject) {
				GeometryUtilities.removeRenderState(terrainObject, StateType.Texture);
				//MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				//ms.setDiffuse(ColorRGBA.gray);
				//terrainObject.setRenderState(ms);
				terrainObject.setName(tileGenerator.xySet[0]+"/"+tileGenerator.xySet[1]);
				terrainObject.updateRenderState();
				terrainObject.setLocalScale(1f/SceneScape.SceneScale);
				Vector3f loc=null;
				if(originalLoc==null){
					originalLoc = JME2MapManager.instance.locationToBetaville(tileGenerator.getCurrentBoundingBox().getSw());
					loc = originalLoc.clone();
				}
				else{
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
				}
				loc.setY(0);
				terrainObject.setLocalTranslation(loc);

				TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
				try {
					url = cache.requestImageTile(tileGenerator.zoomLevel, tileGenerator.xySet[0], tileGenerator.xySet[1]).toString();
					Texture t = TextureManager.loadTexture(new URL(url), false);
					if(t!=null){
						t.setRotation(Rotator.angleZ(270));
						t.setTranslation(new Vector3f(0, 1, 0));
						ts.setTexture(t);
						terrainObject.setRenderState(ts);
						terrainObject.updateRenderState();
						/*
						try {
							ColladaExporter exporter = new ColladaExporter(new File(cache.createZoomAndXFolder(tileGenerator.zoomLevel, tileGenerator.xySet[0])+"/"+tileGenerator.xySet[1]+".dae"), terrainObject, false);
							exporter.writeData();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						*/
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
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
		});
		gen.addTerrainCompletionListener(new ITerrainCompletionListener() {
			
			public void terrainGenerationComplete(Spatial terrainObject) {
				String path = url.substring(0, url.lastIndexOf("."));
				path = path.substring(path.lastIndexOf(".")+1);
				System.out.println(path);
				// This path location will end up looking like: /zoom/folder/tile
				path = path.substring(path.indexOf("/"));
				logger.info("path created: "+path);
			}
		});
		gen.createTerrainBlock();
	}

	private void addToScene(Spatial terrainObject){
		SceneGameState.getInstance().getTerrainNode().attachChild(terrainObject);
		//Box debugBox = new Box("box", terrainObject.getLocalTranslation(), .1f, .1f, .1f);
		//GeometryUtilities.removeRenderState(debugBox, StateType.Texture);
		//SceneGameState.getInstance().getTerrainNode().attachChild(debugBox);
	}
}
