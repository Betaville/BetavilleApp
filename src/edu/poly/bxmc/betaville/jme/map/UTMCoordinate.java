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

import java.io.Serializable;


/**
 * Provides a container for data using the
 * UTM projection system.  Raw values are
 * provided with data such as false eastings
 * left intact.
 * @author Skye Book
 */
public class UTMCoordinate implements ILocation, Serializable{
	private static final long serialVersionUID = 1L;
	private int easting;
	private int northing;
	// Initialize these to zero for legacy compatibility
	private short eastingCentimeters=0;
	private short northingCentimeters=0;
	private int lonZone;
	private char latZone;
	private int altitude;

	/**
	 * 
	 */
	public UTMCoordinate(int easting, int northing, int lonZone, char latZone, int altitude) {
		this(easting, northing, (short)0, (short)0, lonZone, latZone, altitude);
	}
	
	/**
	 * 
	 */
	public UTMCoordinate(int easting, int northing, short eastingCM, short northingCM, int lonZone, char latZone, int altitude) {
		this.easting=easting;
		this.northing=northing;
		this.eastingCentimeters=eastingCM;
		this.northingCentimeters=northingCM;
		this.lonZone=lonZone;
		this.latZone=latZone;
		this.altitude=altitude;
	}
	
	/**
	 * @deprecated - Do not use me, I am broken!
	 * @experimental - Do not use me (23 March 2011)
	 * @param eastingDeltaMeters
	 * @param northingDeltaMeters
	 * @param altitudeDelta
	 * @return
	 */
	public UTMCoordinate move(float eastingDeltaMeters, float northingDeltaMeters, float altitudeDelta){
		System.out.println("eastMeters: "+ (int)(eastingDeltaMeters/1));
		System.out.println("northMeters: "+ (int)(northingDeltaMeters/1));
		System.out.println("eastCenti: "+(int)(((float)eastingDeltaMeters%1)*100));
		System.out.println("northCenti: "+(int)(((float)northingDeltaMeters%1)*100));
		return move((int)(eastingDeltaMeters/1), (int)(northingDeltaMeters/1), (int)(((float)eastingDeltaMeters%1)*100), (int)(((float)northingDeltaMeters%1)*100), (int)altitudeDelta);
	}
	
	public UTMCoordinate move(int eastingDeltaMeters, int northingDeltaMeters, int altitudeDelta){
		return move(eastingDeltaMeters, northingDeltaMeters, 0, 0, altitudeDelta);
	}
	
	/**
	 * Moves the UTM coordinate in meters.
	 * @param eastingDeltaMeters The amount of change in meters (can be positive or negative)
	 * @param northingDeltaMeters The amount of change in meters (can be positive or negative)
	 * @param altitudeDelta The amount of change in meters (can be positive or negative)
	 */
	public UTMCoordinate move(int eastingDeltaMeters, int northingDeltaMeters, int eastingDeltaCentimeters, int northingDeltaCentimeters, int altitudeDelta){
		int workingEastingMeters=eastingDeltaMeters;
		int workingNorthingMeters=northingDeltaMeters;
		int workingEastingCentimeters=eastingDeltaCentimeters;
		int workingNorthingCentimeters=northingDeltaCentimeters;
		
		// if the centimeters are in excess of 100, reduce to meters
		while(workingEastingCentimeters>=100){
			workingEastingCentimeters-=100;
			workingEastingMeters+=1;
		}
		while(workingNorthingCentimeters>=100){
			workingNorthingCentimeters-=100;
			workingNorthingMeters+=1;
		}
		
		eastingCentimeters = (short)workingEastingCentimeters;
		northingCentimeters = (short)workingNorthingCentimeters;
		
		// deal with northing
		northing+=workingNorthingMeters;
		
		// width of zone at this northing
		double width = MapManager.findUTMZoneWidthAtLatitude(getGPS().getLatitude());
		
		double eastBorder = 500000+(width/2);
		double westBorder = 500000-(width/2);
		
		if((easting+workingEastingMeters)>eastBorder){
			System.out.println("increasing lonZone");
			if(lonZone==60) lonZone=(0);
			else lonZone++;
			
			
			double toBorder = eastBorder-easting;
			System.out.println("distance to border: " + toBorder);
			double intoNext = workingEastingMeters-toBorder;
			System.out.println("intoNext:" + intoNext);
			
			while(intoNext>width){
				intoNext-=width;
				if(lonZone==60) lonZone=(0);
				else lonZone++;
			}
			
			easting = (int) (westBorder+intoNext);
		}
		else if((easting+workingEastingMeters)<westBorder){
			if(lonZone==0) lonZone=(60);
			else lonZone++;
			
			
			double toBorder = easting-westBorder;
			double intoLast = workingEastingMeters+toBorder;
			
			easting = (int) (eastBorder+intoLast);
		}
		else{
			easting+=workingEastingMeters;
		}
		
		
		// deal with altitude
		altitude+=altitudeDelta;
		return this;
	}

	public int getEasting() {
		return easting;
	}

	public int getNorthing() {
		return northing;
	}
	
	public short getEastingCentimeters() {
		return eastingCentimeters;
	}

	public short getNorthingCentimeters() {
		return northingCentimeters;
	}

	public int getLonZone() {
		return lonZone;
	}

	public char getLatZone() {
		return latZone;
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}
	
	public double getLatitude(){
		return MapManager.utmToLatLon(this).getLatitude();
	}
	
	public double getLongitude(){
		return MapManager.utmToLatLon(this).getLongitude();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UTMCoordinate other = (UTMCoordinate) obj;
		if (altitude != other.altitude)
			return false;
		if (easting != other.easting)
			return false;
		if (latZone != other.latZone)
			return false;
		if (lonZone != other.lonZone)
			return false;
		if (northing != other.northing)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + altitude;
		result = prime * result + easting;
		result = prime * result + latZone;
		result = prime * result + lonZone;
		result = prime * result + northing;
		return result;
	}
	
	public String toString(){
		// sends the UTM coordinate out in a format friendly to the IBM converter
		return lonZone+ " " + latZone + " " + easting + "." + eastingCentimeters +  " " + northing + "." + northingCentimeters;
		
	}
	
	public GPSCoordinate getGPS(){
		return MapManager.utmToLatLon(this);
	}
	
	public UTMCoordinate clone(){
		return new UTMCoordinate(easting, northing, lonZone, latZone, altitude);
		
	}

	public UTMCoordinate getUTM() {
		return this;
	}
}
