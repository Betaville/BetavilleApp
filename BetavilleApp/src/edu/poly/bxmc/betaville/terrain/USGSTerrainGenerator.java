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

import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.net.UnexpectedServerResponse;
import edu.poly.bxmc.betaville.xml.USGSResponse;

/**
 * @author Skye Book
 *
 */
public class USGSTerrainGenerator extends ElevatedTerrainGenerator {
	private static final Logger logger = Logger.getLogger(USGSTerrainGenerator.class);
	
	private volatile int completedElevationRequests = 0;
	private float lowest;
	private float highest;
	
	private ArrayList<ILocation> coordinateCache;
	
	/**
	 * @param plotPoints
	 * @param southWestCoordinate
	 */
	public USGSTerrainGenerator(int plotPoints, ILocation southWestCoordinate, float blockSize) {
		super(plotPoints, southWestCoordinate, blockSize);
		coordinateCache = new ArrayList<ILocation>();
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.terrain.MappedTerrainGenerator#buildHeightmap()
	 */
	@Override
	public void buildHeightmap() {
		UTMCoordinate transformable = southWestCoordinate.getUTM().clone();
		int distanceBetweenPolls = (int)distanceBetweenPolls();
		for(int east=0; east<getPlotPoints()+1; east++){
			for(int north=0; north<getPlotPoints()+1; north++){
				coordinateCache.add(transformable.clone().move(east*distanceBetweenPolls, north*distanceBetweenPolls, 0));
			}
		}
		
		heightMap = new float[coordinateCache.size()];
		
		// run elevation requests
		for(int i=0; i<coordinateCache.size(); i++){
			elevationQuery(i);
		}

		// wait for the elevation retrievals to finish
		while(completedElevationRequests<coordinateCache.size()){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// Yeah, whatever.
				e.printStackTrace();
			}
		}
	}

	/**
	 * Submits a coordinate to the threaded request pool.
	 * @param coordinate The index of the coordinate to retrieve from the coordinate cache.
	 */
	private void elevationQuery(final int coordinate){

		requestThreadPool.submit(new Runnable(){
			public void run() {
				GPSCoordinate gps = coordinateCache.get(coordinate).getGPS();
				//logger.debug("Querying " + gps.toString());
				USGSResponse elevationResponse = new USGSResponse(gps.getLatitude(), gps.getLongitude());
				try {
					elevationResponse.parse();
				} catch (UnexpectedServerResponse e) {
					logger.error(e.getMessage());
					return;
				}
				heightMap[coordinate] = (float)elevationResponse.getElevation();

				if(coordinate==0){
					lowest=heightMap[coordinate];
					highest=heightMap[coordinate];
				}
				else{
					if(heightMap[coordinate]<lowest) lowest=heightMap[coordinate];
					else if(heightMap[coordinate]>highest) highest=heightMap[coordinate];
				}

				incrementTotal();
			}
		});
	}

	private synchronized void incrementTotal(){
		completedElevationRequests++;
		if(completedElevationRequests%100==0){
			logger.info(completedElevationRequests+"/"+(coordinateCache.size())+" complete");
		}
		if(completedElevationRequests==coordinateCache.size()-1){
			logger.info("\nElevation retrieval complete!");
		}
	}
}
