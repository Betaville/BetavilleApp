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
package edu.poly.bxmc.betaville.model;

import java.util.List;

import edu.poly.bxmc.betaville.bookmarks.Bookmark;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;

/**
 * Contains sorters for lists of objects
 * @author Skye Book
 *
 */
public class DistanceSort {
	
	public static void closestDesigns(UTMCoordinate location, List<Design> designs){
		// cache the location's GPS coordinate:
		GPSCoordinate gpsLoc = location.getGPS();
		double distance=Double.NaN;
		double tempDistance;
		int bestCandidate=-1;
		int actionCounter=0;
		while(actionCounter<designs.size()){
			// ensure that the first designs distance will be used
			distance = Double.NaN;
			// go through each design
			for(int i=0; i<designs.size(); i++){
				if(Double.isNaN(distance)){
					distance = MapManager.greatCircleDistanced(gpsLoc, designs.get(i).getCoordinate().getGPS());
					bestCandidate=i;
				}
				else{
					tempDistance=MapManager.greatCircleDistanced(gpsLoc, designs.get(i).getCoordinate().getGPS());
					if(distance>tempDistance){
						distance=tempDistance;
						bestCandidate=i;
					}
				}
			}
			
			// we now have the closest candidate from the original list.
			designs.add(0, designs.remove(bestCandidate));
			actionCounter++;
		}
	}
	
	public static void closestBookmarks(UTMCoordinate location, List<Bookmark> bookmarks){
		// cache the location's GPS coordinate:
		GPSCoordinate gpsLoc = location.getGPS();
		double distance=Double.NaN;
		double tempDistance;
		int bestCandidate=-1;
		int actionCounter=0;
		while(actionCounter<bookmarks.size()){
			// ensure that the first designs distance will be used
			distance = Double.NaN;
			// go through each design
			for(int i=0; i<bookmarks.size(); i++){
				if(Double.isNaN(distance)){
					distance = MapManager.greatCircleDistanced(gpsLoc, bookmarks.get(i).getLocation().getGPS());
					bestCandidate=i;
				}
				else{
					tempDistance=MapManager.greatCircleDistanced(gpsLoc, bookmarks.get(i).getLocation().getGPS());
					if(distance>tempDistance){
						distance=tempDistance;
						bestCandidate=i;
					}
				}
			}
			
			// we now have the closest candidate from the original list.
			bookmarks.add(0, bookmarks.remove(bestCandidate));
			actionCounter++;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
