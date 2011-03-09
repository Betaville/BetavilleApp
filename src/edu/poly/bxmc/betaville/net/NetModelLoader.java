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

import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.swing.JDialog;

import org.apache.log4j.Logger;

import com.jme.scene.Node;
import com.jme.util.GameTaskQueueManager;

import edu.poly.bxmc.betaville.CacheManager;
import edu.poly.bxmc.betaville.ResourceLoader;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.flags.DesktopFlagPositionStrategy;
import edu.poly.bxmc.betaville.flags.FlagProducer;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.ModelLoader;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.Design.Classification;
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
	
	private static int duplicated = 0;
	
//	stuff for saving
	
	private static String modelIdentifier = "";
	private static URL mediaURL = null;
	private static Design designToSave;
	private static String northingDesign;
	private static String eastingDesign;
	private static String altitudeDesign;
	private static String rotXDesign;
	private static String rotYDesign;
	private static String rotZDesign;
	private static String designName;
	private static UTMCoordinate location;

	/** Options for what kind of models to load from the network.
	 * @author Skye Book
	 */
	public static enum LookupRoutine{ALL_IN_CITY, ALL_BY_USER, CUSTOM, IN_1KM_RADIUS};

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
	 * @param lookupRoutine
	 * @param limit Sets a numeric limit on how many models to load
	 * from the network.  Helpful for testing purposes. {@link NetModelLoader#NO_LIMIT} for no limit,
	 * otherwise, use the number of models desired.
	 * @see NetModelLoader#NO_LIMIT
	 */
	public static void load(LookupRoutine lookupRoutine, int limit, int cityID){
		Vector<Design> duplicateDesigns = new Vector<Design>();
		logger.info("Loading City " + cityID);
		Vector<Design> designs = null;
		ClientManager manager = NetPool.getPool().getConnection();
		if(lookupRoutine.equals(LookupRoutine.ALL_IN_CITY)){
			designs = manager.findBaseDesignsByCity(cityID);
		}
		else if(lookupRoutine.equals(LookupRoutine.ALL_BY_USER)){
			designs = new Vector<Design>();
			designs.add(manager.findDesignByID(45));
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
	
	
	
	public static void initDummies() throws URISyntaxException{
		File boxFile = new File(ResourceLoader.loadResource("/data/terrain/BaseObject1.dae").toURI());
		File markerFile = new File(ResourceLoader.loadResource("/data/terrain/Betaville_Marker_1.dae").toURI());
		File treeFile1 = new File(ResourceLoader.loadResource("/data/terrain/White_Oak_1.dae").toURI());
		File treeFile2 = new File(ResourceLoader.loadResource("/data/terrain/White_Oak.png").toURI());
		URL u1 = null;
		URL u2 = null;
		URL u3 = null;
		URL u4 = null;
		try {
			u1 = new URL(SettingsPreferences.getDataFolder()+"Betaville_Marker_1.dae");
			u2 = new URL(SettingsPreferences.getDataFolder()+"BaseObject1.dae");
			u3 = new URL(SettingsPreferences.getDataFolder()+"White_Oak_1.dae");
			u4 = new URL(SettingsPreferences.getDataFolder()+"White_Oak.png");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try {
			copyFile(markerFile, new File(u1.getFile()));
			copyFile(boxFile, new File(u2.getFile()));
			copyFile(treeFile1, new File(u3.getFile()));
			copyFile(treeFile2, new File(u4.getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 private static void copyFile(File in, File out)throws IOException{
	     FileChannel inChannel = new FileInputStream(in).getChannel();
	     FileChannel outChannel = new FileOutputStream(out).getChannel();
	     try {
	         inChannel.transferTo(0, inChannel.size(),
	                 outChannel);
	     } 
	     catch (IOException e) {
	         throw e;
	     }
	     finally {
	         if (inChannel != null) inChannel.close();
	         if (outChannel != null) outChannel.close();
	     }
	 }

	
	public static void createDummy(UTMCoordinate selection, String file){
		designName = file;
		location = selection;
		savingMode();
	}
	
	private static void savingMode(){
		SettingsPreferences.getThreadPool().submit(new Runnable() {
			public void run() {
				
//				BROWSE
				
				JDialog dialog = new JDialog();
				dialog.setModalityType(ModalityType.APPLICATION_MODAL);

				try {
					mediaURL = new URL(""+SettingsPreferences.getDataFolder()+designName);
				} catch (MalformedURLException e) {
					logger.warn("Problem occured when selecting from file browser", e);
				}
				
//				IMPORT
				
				UTMCoordinate utm = location;
//				utm.move(Integer.parseInt(eastingDesign.trim()), Integer.parseInt(northingDesign.trim()), Integer.parseInt(altitudeDesign.trim()));
			
				
				ModeledDesign design = new ModeledDesign("Dummy", utm, "None", 2, SettingsPreferences.getUser(), "None", mediaURL.toString(), "None", true, 0, 0, 0, false);
				
				design.setClassification(Classification.BASE);
				
				try {
					SceneGameState.getInstance().addDesignToCity(design, mediaURL, mediaURL, design.getSourceID());
					modelIdentifier=design.getFullIdentifier();
					logger.info("modelIdentifier"+modelIdentifier);
					/*
					Vector3f distanceFromZero = GeometryUtilities.getDistanceFromZero(SceneGameState.getInstance().getDesignNode().getChild(modelIdentifier));
					if(distanceFromZero.getX()!=0 || distanceFromZero.getY()!=0 || distanceFromZero.getZ()!=0){
						showSimpleError("Not at Zero! " + distanceFromZero.getX() +","+distanceFromZero.getY()+distanceFromZero.getZ());
					}
					*/
					
					logger.info(design.toString() + " imported");
				} catch (URISyntaxException uriException){
					logger.warn(uriException);
					// send error to add design window
				} catch (IOException ioException){
					logger.warn("File could not be found when trying to import!", ioException);
				}
				
//				PUBLISH
				
				Design design2 = SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier);
				try {
					SecureClientManager manager = NetPool.getPool().getSecureConnection();
					int response=-4;
					logger.info("+"+design2.getID());
					logger.info("+"+design2.getFilepath());
					response = manager.addBase(design2, SettingsPreferences.getUser(), SettingsPreferences.getPass(), GeometryUtilities.getPFT(design2.getFullIdentifier()));
					if(response>0){
						SceneScape.getCity().findDesignByFullIdentifier(modelIdentifier).setID(response);
						SceneGameState.getInstance().getDesignNode().getChild(modelIdentifier).setName(SceneScape.getCity().findDesignByID(response).getFullIdentifier());
						
						SceneScape.clearTargetSpatial();
						logger.info("Added design: " + response);
					}
					else if(response == -3){
						logger.warn("Authentication failed when uploading model");
					}
					else if (response == -2){
						logger.warn("A currently unsupported type of design was not able to be uploaded");
					}
					else if(response == -1){
						logger.warn("Database error on the server");
					}
				} catch (FileNotFoundException e1) {
					logger.error("File could not be found", e1);
				} catch (URISyntaxException e1) {
					logger.error("URI exception", e1);
				} catch (IOException e1) {
					logger.error("Error uploading file", e1);
				}
			}
		});
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
		logger.info("Loading City Terrin" + cityID);
		Vector<Design> designs = NetPool.getPool().getConnection().findTerrainByCity(cityID);
		if(designs==null){
			throw new NullPointerException("designs not received!");
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
