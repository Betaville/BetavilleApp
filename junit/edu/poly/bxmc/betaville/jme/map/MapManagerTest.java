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
package edu.poly.bxmc.betaville.jme.map;
import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import junit.framework.TestCase;

/**
 * @author Skye Book
 *
 */
public class MapManagerTest extends TestCase {
	public void testLatToUTM(){
		GPSCoordinate gps = new GPSCoordinate(0, 40, 42, 4.07, -74, 0, 31.28);
		UTMCoordinate utm = gps.getUTM();
		assertTrue(gpsClose(gps, utm.getGPS()));
	}
	
	private boolean gpsClose(GPSCoordinate gps1, GPSCoordinate gps2){
		if((int) gps1.getLatitude() == (int)gps2.getLatitude() &&
				(int)gps1.getLongitude() == (int)gps2.getLongitude()){
			return true;
		}
		else return false;
	}
	
	public void testUTMToBetaville(){
		GPSCoordinate gps = new GPSCoordinate(0, 40, 42, 4.07, -74, 0, 31.28);
		UTMCoordinate utm = gps.getUTM();
		Vector3f bvLocation = JME2MapManager.instance.locationToBetaville(utm);
		assertTrue(utmClose(utm, JME2MapManager.instance.betavilleToUTM(bvLocation)));
	}
	
	private boolean utmClose(UTMCoordinate c1, UTMCoordinate c2){
		if(c1.getEasting() <= c2.getEasting()+1 || c1.getEasting() >= c2.getEasting()-1){
			if(c1.getNorthing() <= c2.getNorthing()+1 || c1.getNorthing() >= c2.getNorthing()-1){
				if(c1.getLatZone()==c2.getLatZone() && c1.getLonZone()==c2.getLonZone()){
					return true;
				}
			}
		}
		return false;
	}
	
	public void testDistance(){
		GPSCoordinate gps = new GPSCoordinate(0, 40, 42, 4.07, -74, 0, 31.28);
		System.out.println("From DMS: " + gps.toString());
		UTMCoordinate utm = gps.getUTM();;
		System.out.println("UTM Generated From Lat/Lon: " + utm.toString());
		
		Vector3f bvLocation = JME2MapManager.instance.locationToBetaville(utm);
		System.out.println("In terms of Vector3f: " + bvLocation.toString());
		
		UTMCoordinate utmReverse = JME2MapManager.instance.betavilleToUTM(bvLocation);
		System.out.println("UTM reversed from Vector3f: " + utmReverse.toString());
		
		GPSCoordinate gpsReverse = MapManager.utmToLatLon(utmReverse);
		System.out.println("From UTM: " + gpsReverse.toString());
		
		
		System.out.println("Discrepancy: " + MapManager.greatCircleDistanced(gps, gpsReverse));
	}
}
