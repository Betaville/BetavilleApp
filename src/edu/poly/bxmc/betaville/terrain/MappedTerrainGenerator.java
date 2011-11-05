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

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.jme.math.Vector3f;
import com.jmex.terrain.TerrainBlock;

import edu.poly.bxmc.betaville.jme.map.BoundingBox;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.MapManager.SquareCorner;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;

/**
 * The base class for creating generated terrain in Betaville.
 * @author Skye Book
 *
 */
public abstract class MappedTerrainGenerator {
	private static final Logger logger = Logger.getLogger(MappedTerrainGenerator.class);
	
	/**
	 * The size of a terrain block, in meters
	 */
	public float blockSize;
	
	/**
	 * Describes the amount of points that are created along each edge.  This means
	 * that the number of generated elevation requests (and responses) should be
	 * plotPoints^2.  It's also moderately notable that this is related to the
	 * {@link TerrainBlock#getSize()} value, which is {@link #plotPoints}+1</br>
	 * </br>
	 * Note: This value must be odd, if an equal number is given it will be brought
	 * down to an odd number.  The rationale for this is so that lower resolution
	 * terrain meshes can always be made by removing every other value with the exception
	 * of the edges.
	 */
	private int plotPoints;
	
	/**
	 * The piece of geometry that represents this area of terrain.
	 */
	private TerrainBlock terrainBlock;
	
	protected ILocation southWestCoordinate;
	
	protected float[] heightMap;
	
	private ArrayList<ITerrainCompletionListener> terrainCompletionListeners;

	protected BoundingBox boundingBox;

	public MappedTerrainGenerator(int plotPoints, ILocation southWestCoordinate, float blockSize) {
		this.blockSize=blockSize;
		logger.info("Block size is: "+blockSize);
		// ensure that the number of plot points is always an odd number.
		if(plotPoints%2!=0)this.plotPoints=plotPoints;
		else{
			logger.warn("plotPoints value must be an odd number! " +plotPoints + " will be changed to " + (plotPoints-1));
			this.plotPoints=plotPoints-1;
		}
		logger.info("Distance between polls: " + distanceBetweenPolls());
		this.southWestCoordinate=southWestCoordinate;
		createBoundingBox();
		
		terrainCompletionListeners = new ArrayList<ITerrainCompletionListener>();
	}
	
	private void createBoundingBox(){
		UTMCoordinate sw = southWestCoordinate.getUTM().clone();
		UTMCoordinate[] box = MapManager.createBox((int)blockSize, (int)blockSize, SquareCorner.SW, sw);
		boundingBox = new BoundingBox(box[0].getGPS(), box[1].getGPS(), box[2].getGPS(), box[3].getGPS());
	}
	
	/**
	 * Adds an {@link ITerrainCompletionListener} to the update list
	 * @param listener The listener to add
	 */
	public void addTerrainCompletionListener(ITerrainCompletionListener listener){
		terrainCompletionListeners.add(listener);
	}
	
	/**
	 * Removes an {@link ITerrainCompletionListener} from the update list
	 * @param listener The listener to remove
	 */
	public void removeTerrainCompletionListener(ITerrainCompletionListener listener){
		terrainCompletionListeners.remove(listener);
	}
	
	/**
	 * Call this when the terrain block is done being constructed
	 */
	private void terrainCreationComplete(){
		for(ITerrainCompletionListener listener : terrainCompletionListeners){
			listener.terrainGenerationComplete(terrainBlock);
		}
	}
	
	/**
	 * This method should end with the initialization and loading of the
	 * {@link #heightMap} to be used in the {@link #terrainBlock}.</br>
	 * </br>
	 * It is important that heightmaps be built south to north and west to east.
	 * For example:</br>
	 * (North)</br>
	 * &nbsp 2 &nbsp 5 &nbsp 8</br>
	 * &nbsp 1 &nbsp 4 &nbsp 7</br>
	 * &nbsp 0 &nbsp 3 &nbsp 6(East)</br>
	 */
	public abstract void buildHeightmap();
	
	public void createTerrainBlock(){
		buildHeightmap();
		// distance between polls in terms of meters.  (subtract 1 from plotPoints because the
		// number includes both starting and ending edges
		terrainBlock =  new TerrainBlock("terrain", plotPoints+1, new Vector3f(distanceBetweenPolls(),1,distanceBetweenPolls()), heightMap, new Vector3f(0,0,0));
		terrainCreationComplete();
	}
	
	/**
	 * Gives the distance between polls in terms of meters.  (subtract 1 from plotPoints because the
	 * number includes both starting and ending edges
	 * @return Distance between polls in terms of meters
	 */
	public float distanceBetweenPolls(){
		return blockSize/(plotPoints-1);
	}

	/**
	 * Describes the amount of points that are created along each edge.  This means
	 * that the number of generated elevation requests (and responses) should be
	 * plotPoints^2.</br>
	 * </br>
	 * Note: This value is always going to be odd.
	 * @see MappedTerrainGenerator#plotPoints
	 * 
	 * @return The number of plot points
	 */
	public int getPlotPoints() {
		return plotPoints;
	}
	
	/**
	 * Returns the bounding box of this terrain item
	 * @return
	 */
	public BoundingBox getBoundingBox(){
		return boundingBox;
	}

	/**
	 * @return the blockSize
	 */
	public float getBlockSize() {
		return blockSize;
	}
}
