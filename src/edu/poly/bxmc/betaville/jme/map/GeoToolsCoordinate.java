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
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Can be a coordinate of any type of coordinate supported by
 * <a href="http://geotools.org">GeoTools</a>
 * @author Skye Book
 *
 */
public class GeoToolsCoordinate implements ILocation {
	private static final long serialVersionUID = 1L;
	public static final Logger logger = Logger.getLogger(GeoToolsCoordinate.class);
	
	private Coordinate geoToolsCoordinate;
	
	private MathTransform gpsTransform;
	
	/**
	 * @throws FactoryException 
	 * @throws NoSuchAuthorityCodeException Indicates that the GeoTools implementation
	 * was unable to locate a coordinate system matching that which was given
	 * 
	 */
	public GeoToolsCoordinate(Coordinate geoToolsCoordinate, CoordinateReferenceSystem geoToolsCoordinateCRS) throws NoSuchAuthorityCodeException, FactoryException {
		this.geoToolsCoordinate=geoToolsCoordinate;
		gpsTransform = CRS.findMathTransform(geoToolsCoordinateCRS, CRS.decode("EPSG:4326"));
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.map.ILocation#getUTM()
	 */
	public UTMCoordinate getUTM() {
		logger.info("converting " + getGPS().toString() + " to " + MapManager.latLonToUTM(getGPS()).toString());
		return MapManager.latLonToUTM(getGPS());
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.map.ILocation#getGPS()
	 */
	public GPSCoordinate getGPS() {
		Coordinate gpsCoordinate;
		try {
			gpsCoordinate = JTS.transform(geoToolsCoordinate, null, gpsTransform);
			//logger.info("gpsCoordinate: " + gpsCoordinate.x +", "+gpsCoordinate.y +", "+gpsCoordinate.z +", ");
			return new GPSCoordinate(0, gpsCoordinate.x, gpsCoordinate.y);
		} catch (TransformException e) {
			logger.fatal("Could not transform coordinate to EPSG:4326", e);
			return null;
		}
	}
}
