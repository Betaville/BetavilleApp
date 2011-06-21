/** Copyright (c) 2008-2010, Brooklyn eXperimental Media Center
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
package edu.poly.bxmc.betaville;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.SettingsPreferences.SelectionVisuals;
import edu.poly.bxmc.betaville.aesthetics.ColorValues;
import edu.poly.bxmc.betaville.flags.IFlagSelectionListener;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.jme.intersections.ITerrainSelectionListener;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.model.City;
import edu.poly.bxmc.betaville.model.Design;

/**
 * A collection of handy constants having to do with
 * the scene
 * @author Skye Book
 *
 */
public class SceneScape {
	private static Logger logger = Logger.getLogger(SceneScape.class);
	public static final float SceneScale = 100f;

	private static int currentCity;
	private static ArrayList<City> cities = new ArrayList<City>();
	private static int lonZone=18;
	private static char latZone='T';

	public static final ColorRGBA DefaultAmbientColor = new ColorRGBA(1f,1f,1f,.5f);
	public static final ColorRGBA DefaultDiffuseColor = new ColorRGBA(0.5f, 0.5f, 0.5f, .5f);

	private static float minimumHeight = 1.6f;

	private static Node emptySpatial = new Node("$empty");
	private static Spatial targetSpatial = emptySpatial;
	
	private static Node selectedTerrain;

	public static final int SELECTION_EMPTY=0;
	
	private static ArrayList<ISpatialSelectionListener> selectionListeners = new ArrayList<ISpatialSelectionListener>();
	private static ArrayList<ITerrainSelectionListener> terrainListeners = new ArrayList<ITerrainSelectionListener>();
	private static Vector<IFlagSelectionListener> flagSelectionListeners = new Vector<IFlagSelectionListener>();



	/**
	 * Gets the minimum height that the camera is able
	 * to go.
	 * @return The minimum height that the camera
	 * is able to go to in meters
	 */
	public static float getMinimumHeight(){
		return minimumHeight;
	}

	/**
	 * Sets the minimum height that the camera is able
	 * to go.
	 * @param height The minimum height that the camera
	 * is able to go to in terms of meters.
	 */
	public static void setMinimumHeight(int height){
		minimumHeight=height;
	}

	/**
	 * Adds a city to the city list and sets it as the current city.
	 * @param newCity The city to add.
	 */
	public static void addCityAndSetToCurrent(City newCity){
		cities.add(newCity);
		try {
			setCurrentCity(newCity.getCityID());
		} catch (Exception e) {
			logger.error("Adding city apparently failed as it could not be found in the city list", e);
		}
	}
	
	/**
	 * Adds a city to the city list
	 * @param newCity The city to add.
	 */
	public static void addCity(City newCity){
		cities.add(newCity);
	}
	
	public static void setCurrentCity(int cityID) throws Exception{
		boolean cityFound=false;
		for(City c : cities){
			if(c.getCityID()==cityID) cityFound=true;
		}
		if(!cityFound) throw new Exception("City not found in local list of cities");
		currentCity=cityID;
		logger.info("Current City ID Set To: " + cityID);
	}
	
	public static int getCurrentCityID(){
		return currentCity;
	}

	public static City getCity(){
		for(City c : cities){
			if(c.getCityID()==currentCity) return c;
		}
		return null;
	}
	
	public static City getCity(int cityID){
		for(City c : cities){
			if(c.getCityID()==cityID) return c;
		}
		return null;
	}

	public static void setUTMZone(int newLonZone, char newLatZone){
		lonZone = newLonZone;
		latZone = newLatZone;
	}

	public static int getLonZone(){
		return lonZone;
	}

	public static char getLatZone(){
		return latZone;
	}

	public static void setTargetSpatial(Spatial s){
		if(s==null){
			logger.info("Nothing picked!");
			return;
		}
		logger.debug("trying to pick Spatial named: " + s.getName());
		
		// remove the red box
		SceneGameState.getInstance().removeGroundBox();
		
		if(SettingsPreferences.SELECTION_VISUAL.equals(SelectionVisuals.WIREFRAME)){
			targetSpatial.clearRenderState(StateType.Wireframe);
		}
		else if(SettingsPreferences.SELECTION_VISUAL.equals(SelectionVisuals.GLOW_ORANGE)){
			// clear the orange glow
			GeometryUtilities.replaceMaterials(targetSpatial);
		}
		else if(SettingsPreferences.SELECTION_VISUAL.equals(SelectionVisuals.BOUNDING)){
			SceneGameState.getInstance().getDesignNode().detachChildNamed("selectionBounding");
		}
		
		Design previousDesign = null;
		if(!isTargetSpatialEmpty()) previousDesign = getPickedDesign();

		// Now update the render state.  The spatial should now be ready for release.
		targetSpatial.updateRenderState();

		// Assign the new spatial.
		targetSpatial = s;
		

		if(SettingsPreferences.SELECTION_VISUAL.equals(SelectionVisuals.WIREFRAME)){
			targetSpatial.setRenderState(DisplaySystem.getDisplaySystem().getRenderer().createWireframeState());
		}
		else if(SettingsPreferences.SELECTION_VISUAL.equals(SelectionVisuals.GLOW_ORANGE)){
			GeometryUtilities.applyColor(targetSpatial, new ColorRGBA(
					ColorValues.getSelectionDiffuseColorAsUnit()[0],
					ColorValues.getSelectionDiffuseColorAsUnit()[1],
					ColorValues.getSelectionDiffuseColorAsUnit()[2],
					ColorValues.getSelectionDiffuseColorAsUnit()[3]));
		}
		else if(SettingsPreferences.SELECTION_VISUAL.equals(SelectionVisuals.BOUNDING)){
			if(!targetSpatial.equals(emptySpatial)){
				Box b = new Box("selectionBounding", new Vector3f(),
						((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).xExtent,
						((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).yExtent,
						((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).zExtent);
				//((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).getCenter()
				b.setRenderState(DisplaySystem.getDisplaySystem().getRenderer().createWireframeState());
				SceneGameState.getInstance().getDesignNode().attachChild(b);
				b.setLocalTranslation(SceneScape.getTargetSpatial().getLocalTranslation().clone().addLocal(
						((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).xExtent,
						((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).yExtent,
						((BoundingBox)SceneScape.getTargetSpatial().getWorldBound()).zExtent));
				b.updateRenderState();
			}
		}
		
		targetSpatial.updateRenderState();

		for(ISpatialSelectionListener listener : selectionListeners){
			if(!isTargetSpatialEmpty()){
				listener.designSelected(targetSpatial, getPickedDesign());
			}
			else if(previousDesign!=null) listener.selectionCleared(previousDesign);
		}
		
	}

	public static Spatial getTargetSpatial(){
		return targetSpatial;
	}

	public static boolean isTargetSpatialEmpty(){
		if(targetSpatial.getName().equals("$empty")){
			return true;
		}
		else return false;
	}

	public static boolean isTargetSpatialLocal(){
		if(targetSpatial.getName().equals("$local")){
			return true;
		}
		else return false;
	}

	public static void clearTargetSpatial(){
		setTargetSpatial(emptySpatial);
	}
	
	public static Design getPickedDesign(){
		return getCity().findDesignByFullIdentifier(targetSpatial.getName());
	}
	
	public static void addSelectionListener(ISpatialSelectionListener listener){
		selectionListeners.add(listener);
	}
	
	public static void removeSelectionListener(ISpatialSelectionListener listener){
		selectionListeners.remove(listener);
	}
	
	public static void removeAllSelectionListeners(){
		selectionListeners.clear();
	}
	
	public static void addTerrainSelectionListener(ITerrainSelectionListener listener){
		terrainListeners.add(listener);
	}
	
	public static void removeTerrainSelectionListener(ITerrainSelectionListener listener){
		terrainListeners.remove(listener);
	}
	
	public static void removeAllTerrainSelectionListeners(){
		terrainListeners.clear();
	}


	public static void addFlagSelectionListener(IFlagSelectionListener listener){
		flagSelectionListeners.add(listener);
	}

	public static void removeFlagSelectionListener(IFlagSelectionListener listener){
		flagSelectionListeners.remove(listener);
	}

	public static void removaAllFlagSelectionListeners(){
		flagSelectionListeners.removeAllElements();
	}
	
	public static void setSelectedTerrain(Node terrainSelection){
		logger.debug("terrain selected: " + terrainSelection.getName());
		selectedTerrain = terrainSelection;
		for(ITerrainSelectionListener l : terrainListeners){
			l.terrainSelected(selectedTerrain);
		}
	}
	
	public static void clearTerrainSelection(){
		selectedTerrain=null;
		for(ITerrainSelectionListener l : terrainListeners){
			l.terrainSelectionCleared();
		}
	}
	
	public static Node getSelectedTerrain(){
		return selectedTerrain;
	}
	
	public static Vector<IFlagSelectionListener> getFlagSelectionListeners(){
		return flagSelectionListeners;
	}
}