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
package edu.poly.bxmc.betaville.net;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.jme.scene.Node;
import com.jme.util.GameTaskQueueManager;

import edu.poly.bxmc.betaville.CacheManager;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.ModelLoader;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.EmptyDesign;
import edu.poly.bxmc.betaville.model.ModeledDesign;
import edu.poly.bxmc.betaville.model.Design.Classification;
import edu.poly.bxmc.betaville.progress.IntegerBasedProgressiveItem;

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
		 * @throws IOException 
		 * @throws UnknownHostException 
		 */
		public static void loadCurrentCity(LookupRoutine lookupRoutine) throws UnknownHostException, IOException{
			loadCity(lookupRoutine, NO_LIMIT, SettingsPreferences.getCity().getCityID());
		}

		/**
		 * 
		 * @param lookupRoutine The {@link LookupRoutine} to use when loading models.
		 * @param limit The maximum number of models to be loaded
		 * @throws IOException 
		 * @throws UnknownHostException 
		 */
		public static void loadCurrentCity(LookupRoutine lookupRoutine, int limit) throws UnknownHostException, IOException{
			loadCity(lookupRoutine, limit, SettingsPreferences.getCity().getCityID());
		}

		/**
		 * 
		 * @param lookupRoutine
		 * @param limit Sets a numeric limit on how many models to load
		 * from the network.  Helpful for testing purposes. {@link NetModelLoader#NO_LIMIT} for no limit,
		 * otherwise, use the number of models desired.
		 * @throws IOException 
		 * @throws UnknownHostException 
		 * @see NetModelLoader#NO_LIMIT
		 */
		public static void loadCity(LookupRoutine lookupRoutine, int limit, final int cityID) throws UnknownHostException, IOException{
			logger.info("Loading City " + cityID);
			List<Design> designs = null;
			final AtomicInteger itemsToLoad = new AtomicInteger(0);
			final AtomicInteger itemsLoaded = new AtomicInteger(0);

			final AtomicBoolean allDesignsProcessed = new AtomicBoolean(false);

			final AtomicBoolean listLock = new AtomicBoolean(false);
			final ArrayList<Node> nodeList = new ArrayList<Node>();
			boolean itemIsInView = false;

			UnprotectedManager manager = NetPool.getPool().getConnection();
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
				
				for(Design d : designs){
					if(d.getClassification().equals(Classification.BASE) && d instanceof ModeledDesign) itemsToLoad.incrementAndGet();
				}
				
				// add progress listener once we know that there are models to load 
				final IntegerBasedProgressiveItem item = new IntegerBasedProgressiveItem("Models Loading", itemsLoaded.get(), itemsToLoad.get());
				item.setLockFromCompletion(true);
				// set the itemsToLoad value to the number of designs minus the amount of proposals

				Collections.sort(designs, Design.distanceComparator(JME2MapManager.instance.betavilleToUTM(SceneGameState.getInstance().getCamera().getLocation())));

				// setup the scene loader
				SettingsPreferences.getThreadPool().submit(new Runnable() {

					public void run() {

						/*
						 * There are two possibilities if nothing is waiting to
						 * be attached to the scene:
						 * 	- The loading is done
						 * 	- A model is currently being processed
						 */
						/*
						while(nodeList.size()==0){
							try {
								// first check if the loading is done, return if there is
								if(itemsLoaded.get()==itemsToLoad.get()) return;
								// if the loading is still in process, sleep and run again
								else Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						*/

						while(!allDesignsProcessed.get()){
							if(nodeList.size()==0) continue;
							GameTaskQueueManager.getManager().update(new Callable<Object>() {
								public Object call() throws Exception {

									if(listLock.get()) return null;
									else{
										listLock.set(true);

										//logger.info("Adding " + nodeList.size() + " objects");

										for(int i = 0; i < nodeList.size(); i++){

											SceneGameState.getInstance().getDesignNode().attachChild(nodeList.get(i));

											nodeList.get(i).updateRenderState();
											itemsLoaded.incrementAndGet();
											item.update(itemsLoaded.get());
										}

										nodeList.clear();
										listLock.set(false);
									}
									return null;
								}
							});

							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});



				for(int i=0; i<designs.size(); i++){
					if(limit==NO_LIMIT || i<limit){
						Design design = designs.get(i);
						if(SceneGameState.getInstance().getCamera().getLocation().distance(
								JME2MapManager.instance.locationToBetaville(design.getCoordinate())) > Scale.fromMeter(50000)){
							itemsToLoad.decrementAndGet();
							item.setMax(itemsToLoad.get());
							item.update(itemsToLoad.get());
							continue;
						}
						logger.debug("adding: " + design.getName() + " | ID: " + design.getID());
						
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



								// optimize the hierarchy

								//int originalSpatialCount = GeometryUtilities.countAllChildren(dNode);
								//GeometryUtilities.collapseToSingleLevel(dNode, new ArrayList<Spatial>(), new ArrayList<Spatial>());
								//int newSpatialCount = GeometryUtilities.countAllChildren(dNode);

								// let's see the difference
								//logger.info(dNode.getName()+"\tOLD COUNT:\t" + originalSpatialCount +"\tNEW COUNT:\t"+newSpatialCount);


								//dNode = ClodSetup.setupClod(dNode);
								//dNode.setLocalScale(1/SceneScape.SceneScale);
								dNode.setName(design.getFullIdentifier());

								dNode.setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)design).getRotationX(),
										((ModeledDesign)design).getRotationY(), ((ModeledDesign)design).getRotationZ()));

								dNode.setLocalTranslation(JME2MapManager.instance.locationToBetaville(design.getCoordinate()));

								SettingsPreferences.getCity(cityID).addDesign(design);

								while(listLock.get()){
									try {
										Thread.sleep(25);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}

								nodeList.add(dNode);
								//itemsToLoad.incrementAndGet();
								
								// if the progress item hasn't been added to the display already, add it
								if(!itemIsInView){
									if(itemsToLoad.get()>0){
										GUIGameState.getInstance().getProgressContainer().addItem(item);
										itemIsInView=true;
									}
								}
								else{
									//item.setMax(itemsToLoad.get());
									//item.update(itemsLoaded.get());
								}
								//item.setMax(itemsToLoad.get());

							}
							else if(design instanceof EmptyDesign){
								SettingsPreferences.getCity(cityID).addDesign(design);
								//itemsLoaded.incrementAndGet();
								//item.update(itemsLoaded.get());
							} 
						}
					}
				}
				allDesignsProcessed.set(true);
				item.setLockFromCompletion(false);
			}

			// wait for everything to load
			while(itemsLoaded.get()<itemsToLoad.get()){
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					logger.info("Interrupted while waiting for models to finish loading");
				}
			}
			logger.info("designNode has " + SceneGameState.getInstance().getDesignNode().getQuantity() + " objects (total: "+ GeometryUtilities.countAllChildren(SceneGameState.getInstance().getDesignNode())+")");
		}

		/**
		 * Loads the terrain for the current city
		 * @throws IOException 
		 * @throws UnknownHostException 
		 */
		public static void loadCurrentCityTerrain() throws UnknownHostException, IOException{
			loadCityTerrain(SettingsPreferences.getCurrentCityID());
		}

		/**
		 * Loads the terrain for the current city
		 * @throws IOException 
		 * @throws UnknownHostException 
		 */
		public static void loadCityTerrain(int cityID) throws UnknownHostException, IOException{
			logger.info("Loading City Terrain " + cityID);
			List<Design> designs = NetPool.getPool().getConnection().findTerrainByCity(cityID);
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
							dNode.setLocalTranslation(JME2MapManager.instance.locationToBetaville(design.getCoordinate()));
							dNode.setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)design).getRotationX(),
									((ModeledDesign)design).getRotationY(), ((ModeledDesign)design).getRotationZ()));
							if(design.getName().contains("$TERRAIN")){
								SettingsPreferences.getCity(cityID).addDesign(design);
								SceneGameState.getInstance().getTerrainNode().attachChild(dNode);
							}
							else{
								SettingsPreferences.getCity(cityID).addDesign(design);
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
							SettingsPreferences.getCity(cityID).addDesign(design);
						}
					}
				}
			}
		}
}
