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
 * Class <GPSCoordinate> - GPS coordinate: Encapsulation of a single gps coordinate.
 *
 * @author Jonas Panten
 * @author Skye Book
 */
public class GPSCoordinate implements ILocation, Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * Attribute <latitude> - Latitude of the coordinate
	 */
	private double latitude;
	
	/**
	 * Attribute <longitude> - Longitude of the coordinate
	 */
	private double longitude;
	
	/**
	 * Attribute <altitude> - Altitude of the coordinate
	 */
	private double altitude;

	/**
	 * Creates a Coordinate by taking in DD values
	 *
	 * @param altitude Altitude of the coordinate
	 * @param latitude Latitude of the coordinate
	 * @param longitude Longitude of the coordinate
	 */
	public GPSCoordinate(double altitude, double latitude, double longitude) {
		super();
		this.altitude = altitude;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Creates a Coordinate by converting a DMS number into a DD number.
	 * @param altitude Altitude of the coordinate
	 * @param latDegrees value for Latitude degrees
	 * @param latMinutes value for Latitude minutes
	 * @param latSeconds value for Latitude seconds
	 * @param lonDegrees value for Longitude degrees
	 * @param lonMinutes value for Longitude minutes
	 * @param lonSeconds value for Longitude seconds
	 */
	public GPSCoordinate(double altitude, int latDegrees, int latMinutes, double latSeconds,
			int lonDegrees, int lonMinutes, double lonSeconds){
		this(altitude,DecimalDegreeConverter.dmsToDD(latDegrees, latMinutes, latSeconds),
				DecimalDegreeConverter.dmsToDD(lonDegrees, lonMinutes, lonSeconds));		
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betavile.model.IGPSCoordinate#getLatitude()
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betavile.model.IGPSCoordinate#setLatitude(double)
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betavile.model.IGPSCoordinate#getLongitude()
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betavile.model.IGPSCoordinate#setLongitude(double)
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betavile.model.IGPSCoordinate#getAltitude()
	 */
	public double getAltitude() {
		return altitude;
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betavile.model.IGPSCoordinate#setAltitude(double)
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public UTMCoordinate getUTM() {
		return MapManager.latLonToUTM(this);
	}
	
	public String toString(){
		return getClass().getName()+":  Latitude: " + latitude + "  Longitude: " + longitude + "  Altitude: " + altitude;
	}

	public GPSCoordinate getGPS() {
		return this;
	}
	
	@Override
	public GPSCoordinate clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new GPSCoordinate(altitude, latitude, longitude);
	}
}
