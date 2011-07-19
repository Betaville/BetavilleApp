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

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Can be a coordinate of any type of coordinate supported by
 * <a href="http://geotools.org">GeoTools</a>
 * @author Skye Book
 *
 */
public class GeoToolsBackedLocation implements ILocation {
	private static final long serialVersionUID = 1L;
	public static final Logger logger = Logger.getLogger(GeoToolsBackedLocation.class);


	private float altitude=0;

	private Coordinate geoToolsCoordinate;
	private CoordinateReferenceSystem currentCRS;

	public static final int EPSG_WGS84 = 4326;

	public static final int EPSG_UTM_NORTHERN_HEMISPHERE = 32600;
	public static final int EPSG_UTM_SOUTHERN_HEMISPHERE = 32700;
	private int epsgUTMCode;

	// for internal use only
	private GeoToolsBackedLocation(){}

	/**
	 * @throws FactoryException 
	 * @throws NoSuchAuthorityCodeException Indicates that the GeoTools implementation
	 * was unable to locate a coordinate system matching that which was given
	 * 
	 */
	public GeoToolsBackedLocation(GPSCoordinate gps) throws NoSuchAuthorityCodeException, FactoryException {
		geoToolsCoordinate = new Coordinate(gps.getLatitude(), gps.getLongitude());
		altitude=(int)gps.getAltitude();
		
		int zone = getLonZone(gps);
		System.out.println("UTM Zone: " + zone);

		// determine the EPSG code to use
		int epsgCode;
		if(gps.getLatitude()>0){
			// 32600 -> northern hemisphere
			epsgCode = 32600+zone;
		}
		else{
			// 32700 -> southern hemisphere
			epsgCode = 32700+zone;
		}

		epsgUTMCode=epsgCode;
		currentCRS = CRS.decode("EPSG:"+EPSG_WGS84);
	}

	public GeoToolsBackedLocation(UTMCoordinate utm) throws NoSuchAuthorityCodeException, FactoryException {
		geoToolsCoordinate = new Coordinate(utm.getNorthing()+utm.getNorthingCentimeters(), utm.getEasting()+utm.getEastingCentimeters());
		altitude=utm.getAltitude();
		int zone = utm.getLonZone();

		// determine the EPSG code to use
		int epsgCode;
		System.out.println("Lat Zone: " + utm.getLatZone());
		if(utm.getLatZone()>'N'){
			// 32600 -> northern hemisphere
			epsgCode = EPSG_UTM_NORTHERN_HEMISPHERE+zone;
		}
		else{
			// 32700 -> southern hemisphere
			epsgCode = EPSG_UTM_SOUTHERN_HEMISPHERE+zone;
		}

		System.out.println("EPSG Code for UTM: " + epsgCode);
		epsgUTMCode=epsgCode;

		currentCRS = CRS.decode("EPSG:"+epsgCode);
	}

	private int getLonZone(GPSCoordinate gps){
		return MapManager.latLonToUTM(gps).getLonZone();
	}

	private char getLatZone(GPSCoordinate gps){
		return MapManager.latLonToUTM(gps).getLatZone();
	}
	
	/**
	 * @return the altitude
	 */
	public float getAltitude() {
		return altitude;
	}

	/**
	 * @param altitude the altitude to set
	 */
	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.map.ILocation#getUTM()
	 */
	public UTMCoordinate getUTM() {
		try {
			Coordinate utmCoordinate = JTS.transform(geoToolsCoordinate, null, CRS.findMathTransform(currentCRS, CRS.decode("EPSG:"+epsgUTMCode)));
			int lonZone = epsgUTMCode%100;
			System.out.println("LonZone: " + lonZone);
			double lat = getGPS().getLatitude();

			System.out.println("RAW X/Y:"+utmCoordinate.x+","+utmCoordinate.y);
			
			// get centimeter values
			short xcm = (short)((utmCoordinate.x*100)%100);
			short ycm = (short)((utmCoordinate.y*100)%100);
			System.out.println("X-CM"+xcm);
			System.out.println("Y-CM"+ycm);
			
			char latZone;
			int increaseBy;
			// there are 164 degrees of latitude
			if(lat>0){
				latZone = 'N';
				increaseBy = ((int)Math.abs(lat)/8)+1;
			}
			else{
				latZone = 'C';
				increaseBy = ((int)Math.abs(lat)/8)-1;
			}
			
			latZone+=increaseBy;			
			
			System.out.println("LatZone in conversion: " + latZone);

			return new UTMCoordinate((int)utmCoordinate.y, (int)utmCoordinate.x, lonZone, latZone, 0);
			//return new UTMCoordinate((int)utmCoordinate.y, (int)utmCoordinate.x, ycm, xcm, lonZone, latZone, 0);
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.map.ILocation#getGPS()
	 */
	public GPSCoordinate getGPS() {
		try {
			Coordinate gpsCoordinate = JTS.transform(geoToolsCoordinate, null, CRS.findMathTransform(currentCRS, CRS.decode("EPSG:"+EPSG_WGS84)));
			return new GPSCoordinate(0, gpsCoordinate.x, gpsCoordinate.y);
		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public GeoToolsBackedLocation clone(){
		// this is a shallow copy of the object, we should check in the GeoTools documentation
		// to what it actually does
		GeoToolsBackedLocation gtc = new GeoToolsBackedLocation();
		gtc.geoToolsCoordinate = geoToolsCoordinate;
		gtc.currentCRS = currentCRS;
		gtc.epsgUTMCode = epsgUTMCode;
		gtc.altitude = altitude;
		return gtc;
	}
}
