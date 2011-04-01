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
package edu.poly.bxmc.betaville.net;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.jme.scene.Node;
import com.jme.util.GameTaskQueueManager;

import edu.poly.bxmc.betaville.CacheManager;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.flags.DesktopFlagPositionStrategy;
import edu.poly.bxmc.betaville.flags.FlagProducer;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.ModelLoader;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.EmptyDesign;
import edu.poly.bxmc.betaville.model.ModeledDesign;

/**
 * Loads models from the network.  Different options for what is loaded
 * can be configured by using the constructor's {@link LookupRoutine} parameter.
 * @author Skye Book
 * @see LookupRoutine
 */
public class NetModelLoader{
	private static Logger logger = Logger.getLogger(NetModelLoader.class);

	/** Options for what kind of models to load from the network.
	 * @author Skye Book
	 */
	public static enum LookupRoutine{
		/**
		 * Loads all designs in the set city
		 */
		ALL_IN_CITY,
		/**
		 * Loads all designs by the current user
		 */
		ALL_BY_USER,
		/**
		 * @deprecated - This has never found use
		 */
		CUSTOM,
		/**
		 * @deprecated - This has never found use, but the idea of loading restricted to
		 * a general radius is still a possibility
		 */
		IN_1KM_RADIUS};

	/**
	 * Use this value for implying no limit on the
	 * number of models to load.
	 */
	public static final int NO_LIMIT = -1;

	
	
	/**
	 * 
	 * @param lookupRoutine
	 */
	public static void loadCurrentCity(LookupRoutine lookupRoutine){
		load(lookupRoutine, NO_LIMIT, SceneScape.getCity().getCityID());
	}
	
	/**
	 * 
	 * @param lookupRoutine The {@link LookupRoutine} to use when loading models.
	 * @param limit The maximum number of models to be loaded
	 */
	public static void loadCurrentCity(LookupRoutine lookupRoutine, int limit){
		load(lookupRoutine, limit, SceneScape.getCity().getCityID());
	}

	/**
	 * 
	 * @param lookupRoutine
	 * @param limit Sets a numeric limit on how many models to load
	 * from the network.  Helpful for testing purposes. {@link NetModelLoader#NO_LIMIT} for no limit,
	 * otherwise, use the number of models desired.
	 * @see NetModelLoader#NO_LIMIT
	 */
	public static void load(LookupRoutine lookupRoutine, int limit, int cityID){
		logger.info("Loading City " + cityID);
		List<Design> designs = null;
		ClientManager manager = NetPool.getPool().getConnection();
		if(lookupRoutine.equals(LookupRoutine.ALL_IN_CITY)){
			designs = manager.findBaseDesignsByCity(cityID);
		}
		else if(lookupRoutine.equals(LookupRoutine.ALL_BY_USER)){
			try{
			designs = manager.findDesignsByUser(SettingsPreferences.getUser());
			}catch(NullPointerException e){
				logger.error("User was not set, not loading models");
				return;
			}
		}

		if(designs==null){
			throw new NullPointerException("designs not received!");
		}
		else{
			Collections.sort(designs, Design.distanceComparator(MapManager.betavilleToUTM(SceneGameState.getInstance().getCamera().getLocation())));
			for(int i=0; i<designs.size(); i++){
				if(limit==-1 || i<limit){
					Design design = designs.get(i);
//					if(design.getName().equals("Wormhole Paradox Garage")&&(duplicated < 10)){
//						
//						
//						
//						designs.add(design);
//						logger.info("duplicated");
//					}
					if(SceneGameState.getInstance().getCamera().getLocation().distance(MapManager.locationToBetaville(design.getCoordinate())) < Scale.fromMeter(50000)){
						//logger.info("adding: " + design.getName() + " | ID: " + design.getID());
						


						boolean fileResponse = false;
						if(!(design instanceof EmptyDesign)){
							fileResponse = CacheManager.getCacheManager().requestFile(design.getID(), design.getFilepath());
						}

						if(fileResponse || design instanceof EmptyDesign){
							if(design instanceof ModeledDesign){
								logger.debug("Loading design: "+design.getID());
								ModelLoader loader = null;
								try {
									loader = new ModelLoader((ModeledDesign)design, true, null);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (URISyntaxException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
								Node dNode = loader.getModel();
							
								//dNode = ClodSetup.setupClod(dNode);
								//dNode.setLocalScale(1/SceneScape.SceneScale);
								dNode.setName(design.getFullIdentifier());
								
								dNode.setLocalTranslation(MapManager.locationToBetaville(design.getCoordinate()));
								
								
								dNode.setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)design).getRotationX(),
										((ModeledDesign)design).getRotationY(), ((ModeledDesign)design).getRotationZ()));
								
								
								if(design.getName().contains("$TERRAIN")){
									SceneGameState.getInstance().getTerrainNode().attachChild(dNode);
								}else{
									SceneScape.getCity(cityID).addDesign(design);
									SceneGameState.getInstance().getDesignNode().attachChild(dNode);
								}
								
								
								dNode.updateRenderState();
								
								
								/*
								GameTaskQueueManager.getManager().update(new Callable<Object>() {
									public Object call() throws Exception {
										//dNode.lockMeshes();
										return null;
									}
								});
								*/
							}
							else if(design instanceof EmptyDesign){
								SceneScape.getCity(cityID).addDesign(design);
							} 
						}
					}
					else{
						//logger.debug("Not adding: " + design.getName() + " | Too far away ("+design.getCoordinate().toString()+")");
					}
				}
			}
			
		}

		FlagProducer testFlagger = new FlagProducer(MapManager.betavilleToUTM(SceneGameState.getInstance().getCamera().getLocation()), new DesktopFlagPositionStrategy());
		testFlagger.getProposals(5000);
		testFlagger.placeFlags();
	}
	
	/**
	 * Loads the terrain for the current city
	 */
	public static void loadCurrentCityTerrain(){
		loadCityTerrain(SceneScape.getCurrentCityID());
	}

	/**
	 * Loads the terrain for the current city
	 */
	public static void loadCityTerrain(int cityID){
		logger.info("Loading City Terrain " + cityID);
		Vector<Design> designs = NetPool.getPool().getConnection().findTerrainByCity(cityID);
		if(designs==null){
			//throw new NullPointerException("designs not received!");
			return;
		}
		else{
			for(int i=0; i<designs.size(); i++){
				Design design = designs.get(i);
				logger.debug("adding: " + design.getName() + " | ID: " + design.getID());

				boolean fileResponse = false;
				if(!(design instanceof EmptyDesign)){
					fileResponse = CacheManager.getCacheManager().requestFile(design.getID(), design.getFilepath());
				}

				if(fileResponse || design instanceof EmptyDesign){
					if(design instanceof ModeledDesign){
						ModelLoader loader = null;
						try {
							loader = new ModelLoader((ModeledDesign)design, true, null);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Node dNode = loader.getModel();
						dNode.setName(design.getFullIdentifier());
						dNode.setLocalTranslation(MapManager.locationToBetaville(design.getCoordinate()));
						dNode.setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)design).getRotationX(),
								((ModeledDesign)design).getRotationY(), ((ModeledDesign)design).getRotationZ()));
						if(design.getName().contains("$TERRAIN")){
							SceneGameState.getInstance().getTerrainNode().attachChild(dNode);
						}
						else{
							SceneScape.getCity(cityID).addDesign(design);
							SceneGameState.getInstance().getDesignNode().attachChild(dNode);
						}
						dNode.updateRenderState();

						GameTaskQueueManager.getManager().update(new Callable<Object>() {
							public Object call() throws Exception {
								//dNode.lockMeshes();
								return null;
							}
						});
					}
					else if(design instanceof EmptyDesign){
						SceneScape.getCity(cityID).addDesign(design);
					}
				}
			}
		}
	}
}
