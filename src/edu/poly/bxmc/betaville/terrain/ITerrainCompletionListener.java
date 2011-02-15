/**
 * 
 */
package edu.poly.bxmc.betaville.terrain;

import com.jme.scene.Spatial;

/**
 * @author Skye Book
 *
 */
public interface ITerrainCompletionListener {
	
	/**
	 * Triggered when a terrain object has been created
	 * @param terrainObject The object created
	 */
	public void terrainGenerationComplete(Spatial terrainObject);
}
