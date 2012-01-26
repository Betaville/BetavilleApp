/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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

import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.util.CoordinateConversion;

import edu.poly.bxmc.betaville.SceneScape;

/**
 * Maintains coordinate control for Betaville based on
 * a UTM projection of the earth.  Scene partitioning is
 * also enacted at this level in the conversions between
 * UTM and Vector3f coordinates.  Scene scaling is also
 * taken into account within these methods even though it
 * is declared externally.
 * <br><br>
 * Information on the UTM system can be found at:
 * http://www.uwgb.edu/dutchs/FieldMethods/UTMSystem.htm
 * <br><br>
 * This class contains a number of methods to aid in working with
 * world coordinates as well as getting them to and from coordinates
 * of spatiality within the engine.
 * 
 * @author Skye Book
 * @param <T>
 */
public abstract class MapManager<T>{
	private static final Logger logger = Logger.getLogger(MapManager.class);
	
	protected static int lonZone=18;
	protected static char latZone='T';
	
	/**
	 * The size of a block node in meters
	 */
	protected int blockSize=1000;
	
	/**
	 * The four corners of a box plus the center.
	 */
	public static enum SquareCorner{
		/** Northeast */
		NE,
		/** Northwest */
		NW,
		/** Southeast */
		SE,
		/** Southwest */
		SW,
		/** The geometric center of a square */
		CENTER
	};

	/** The Radius of the earth according to the WGS84 model.*/
	private static int WGS84_equatorialRadius = 6378137;
	
	private float xOffset=0;
	private float zOffset=0;
	
	/**
	 * Re-calibrates Betaville's internal coordinate system to a new
	 * center-point.  This does not update any objects in the scene,
	 * they will need be re-positioned accordingly.
	 * @param newZeroPoint
	 */
	public void adjustOffsets(ILocation newZeroPoint){
		BVVec3f location = locationToBetavilleInternal(newZeroPoint);
		
		if(location.x<0) xOffset = location.x*-1;
		else xOffset = location.x;
		
		if(location.z<0) xOffset = location.z*-1;
		else zOffset = location.z;
		
		logger.info("New MapManager offset set to ("+xOffset+","+zOffset+")");
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

	public static double findCircumferenceAtLatitude(double lat){
		return (WGS84_equatorialRadius*2*StrictMath.PI)*(StrictMath.cos(lat));
	}

	/**
	 * Calculates the beginning of the UTM zone in terms of
	 * meters east of 180th Meridian (<b>NOT</b> the Prime
	 * Meridian).
	 * @param lonZone Zone to find the beginning of
	 * @return the beginning of the UTM zone in terms of
	 * meters east of 180th Meridian
	 */
	public static double findStartOfUTMZone(int lonZone){
		double earthCircumference = WGS84_equatorialRadius*2*StrictMath.PI;
		double zoneWidthAtEquator = earthCircumference/60;
		return zoneWidthAtEquator*(lonZone-1);
	}

	public static int getNorthingStartOfZone(char latZone){
		int northingStart;
		int northingDivisor=1000000;
		if((int)latZone>(int)'J' && (int)latZone<(int)'O'){
			northingStart = ((int)latZone-69)*northingDivisor;
		}
		else if((int)latZone>(int)'O'){
			northingStart = ((int)latZone-70)*northingDivisor;
		}
		else{
			northingStart = ((int)latZone-3)*northingDivisor;
		}

		// If we are above the equator, subtract the false northing
		if((int)latZone>(int)'M'){
			northingStart-=10000000;
		}
		return northingStart;
	}

	public static double findZoneWidth(int firstZone, int secondZone){
		return findStartOfUTMZone(secondZone)-findStartOfUTMZone(firstZone);
	}

	/**
	 * Calculates the location of a UTM zone's meridian in terms of
	 * meters east of 180th Meridian (<b>NOT</b> the Prime
	 * Meridian).
	 * @param lonZone Zone to find the meridian for.
	 * @return The UTM zone's meridian in terms of meters east
	 * of 180th Meridian
	 */
	public static double findZoneMeridian(int lonZone){
		double currentZone = findStartOfUTMZone(lonZone);
		double nextZone = findStartOfUTMZone(lonZone+1);
		double localMeridian = ((nextZone-currentZone)/2);
		return currentZone + localMeridian;
	}

	/**
	 * Calculates the midpoint between two GPS coordinates.
	 * @param gps1 The first coordinate.
	 * @param gps2 The second coordinate.
	 * @return The midpoint.
	 */
	public static GPSCoordinate getPathMidpoint(GPSCoordinate gps1, GPSCoordinate gps2){
		return new GPSCoordinate((gps2.getAltitude()+gps1.getAltitude())/2, (gps2.getLatitude()+gps1.getLatitude())/2, (gps2.getLongitude()+gps1.getLongitude())/2);
	}

	/**
	 * Converts a Latitude/Longitude coordinate to the
	 * UTM system.
	 * @param geo Coordinate to convert.
	 * @return UTMCoordinate
	 */
	public static UTMCoordinate latLonToUTM(GPSCoordinate geo){
		String utmVal = CoordinateConversion.latLon2UTM(geo.getLatitude(), geo.getLongitude());
		int lonZone = Integer.parseInt(utmVal.substring(0, utmVal.indexOf(" ")));
		utmVal = utmVal.substring(utmVal.indexOf(" ")+1, utmVal.length());
		String latZone = utmVal.substring(0, utmVal.indexOf(" "));
		utmVal = utmVal.substring(utmVal.indexOf(" ")+1, utmVal.length());
		int easting = Integer.parseInt(utmVal.substring(0, utmVal.indexOf(" ")));
		utmVal = utmVal.substring(utmVal.indexOf(" ")+1, utmVal.length());
		int northing = Integer.parseInt(utmVal.substring(0, utmVal.length()));
		utmVal = utmVal.substring(utmVal.indexOf(" ")+1, utmVal.length());

		return new UTMCoordinate(easting, northing, lonZone, latZone.toCharArray()[0], (int)geo.getAltitude());
	}

	public static GPSCoordinate utmToLatLon(UTMCoordinate u){
		double[] latLon= CoordinateConversion.utm2LatLon(u.toString());
		return new GPSCoordinate(u.getAltitude(), latLon[0], latLon[1]);
	}

	/**
	 * Finds the width of any UTM zone at a given latitude
	 * @param lat Latitude at which to find the zone width.
	 * @return The zone width.
	 */
	public static double findUTMZoneWidthAtLatitude(double lat){
		GPSCoordinate a = new GPSCoordinate(0, lat, 0);
		GPSCoordinate b = new GPSCoordinate(0, lat, 1);
		return greatCircleDistanced(a, b)*6;
	}

	/**
	 * Translates UTM coordinate into one to be placed
	 * within Betaville.  This method resolves the use
	 * of false eastings so that objects from different
	 * zones can mesh together correctly.  This should be
	 * the <b>ONLY</b> method that goes from any sort of world
	 * coordinates to Betaville.
	 * @param utm Coordinate to translate
	 * @return Vector3f that can be used as a location
	 * within Betaville.
	 */
	public abstract T locationToBetaville(ILocation location);

	/**
	 * Translates UTM coordinate into one to be placed
	 * within Betaville.  This method resolves the use
	 * of false eastings so that objects from different
	 * zones can mesh together correctly.  This should be
	 * the <b>ONLY</b> method that goes from any sort of world
	 * coordinates to Betaville.
	 * @param utm Coordinate to translate
	 * @return Vector3f that can be used as a location
	 * within Betaville.
	 */
	protected BVVec3f locationToBetavilleInternal(ILocation location){
		UTMCoordinate utm = location.getUTM();
		int northingStart = getNorthingStartOfZone(utm.getLatZone());

		double zoneWidth = findZoneWidth(utm.getLonZone(), utm.getLonZone()+1);
		double median = zoneWidth/2;
		float easting=(float)(median+(utm.getEasting()-500000));
		easting+=(((float)utm.getEastingCentimeters())/100f);
		
		float fullNorthing = ((float)utm.getNorthing());
		// localize the northing early so that it fits within the scope of float-precision
		fullNorthing-=northingStart;
		fullNorthing+=(((float)utm.getNorthingCentimeters())/100f);

		return new BVVec3f((fullNorthing/SceneScape.SceneScale)-xOffset, utm.getAltitude()/SceneScape.SceneScale, ((easting/SceneScape.SceneScale)*1)-zOffset);
	}

	/**
	 * Converts from a <code>Vector3f</code> Betaville world
	 * coordinate to a <code>UTMCoordinate</code> which is used
	 * in placing and locating designs.
	 * @param Betaville world coordinate to be converted to UTM
	 * @return UTM location of the coordinate supplied
	 */
	public abstract UTMCoordinate betavilleToUTM(T vec);

	/**
	 * Converts from a <code>Vector3f</code> Betaville world
	 * coordinate to a <code>UTMCoordinate</code> which is used
	 * in placing and locating designs.
	 * @param Betaville world coordinate to be converted to UTM
	 * @return UTM location of the coordinate supplied
	 */
	protected UTMCoordinate betavilleToUTMInternal(BVVec3f vec){
		vec.x+=xOffset;
		vec.z+=zOffset;
		float mNorthOfZoneStart = vec.x*SceneScape.SceneScale;
		float mEastOfZoneStart = -1*vec.z*SceneScape.SceneScale;

		double zoneWidth = findZoneWidth(lonZone, lonZone+1);
		double median = zoneWidth/2;
		int realEasting = (int)(500000-(median+mEastOfZoneStart));

		int realNorthing = getNorthingStartOfZone(latZone)+(int)mNorthOfZoneStart;

		int altitude = (int)(vec.y*SceneScape.SceneScale);

		return new UTMCoordinate(realEasting, realNorthing, lonZone, latZone, altitude);
	}

	/**
	 * 
	 * @param northSouthSize The size of the box to create in meters measured from north to south
	 * @param eastWestSize The size of the box to create in meters measured from east to west
	 * @param anchor The corner where the 'startFrom' coordinate will be located.
	 * @param startFrom The location to tie to the anchor point
	 * @return An array of coordinates creating a box in the following order:
	 * North West, North East, South West, South East
	 * @see SquareCorner
	 */
	public static UTMCoordinate[] createBox(int northSouthSize, int eastWestSize, SquareCorner anchor, UTMCoordinate startFrom){
		UTMCoordinate[] coordinates = new UTMCoordinate[4];
		if(anchor.equals(SquareCorner.NW)){
			coordinates[0]=startFrom.clone();

			coordinates[1]=startFrom.clone();
			coordinates[1].move(eastWestSize, 0, 0);

			coordinates[2]=startFrom.clone();
			coordinates[2].move(0, -northSouthSize, 0);

			coordinates[3]=startFrom.clone();
			coordinates[3].move(eastWestSize, -northSouthSize, 0);
		}
		if(anchor.equals(SquareCorner.NE)){
			coordinates[0]=startFrom.clone();
			coordinates[0].move(-eastWestSize, 0, 0);

			coordinates[1]=startFrom.clone();

			coordinates[2]=startFrom.clone();
			coordinates[2].move(-eastWestSize, -northSouthSize, 0);

			coordinates[3]=startFrom.clone();
			coordinates[3].move(0, -northSouthSize, 0);

		}
		if(anchor.equals(SquareCorner.SW)){
			coordinates[0]=startFrom.clone();
			coordinates[0].move(0, northSouthSize, 0);

			coordinates[1]=startFrom.clone();
			coordinates[1].move(eastWestSize, northSouthSize, 0);

			coordinates[2]=startFrom.clone();

			coordinates[3]=startFrom.clone();
			coordinates[3].move(eastWestSize, 0, 0);
		}
		if(anchor.equals(SquareCorner.SE)){
			coordinates[0]=startFrom.clone();
			coordinates[0].move(-eastWestSize, northSouthSize, 0);

			coordinates[1]=startFrom.clone();
			coordinates[1].move(0, northSouthSize, 0);

			coordinates[2]=startFrom.clone();
			coordinates[2].move(-eastWestSize, 0, 0);

			coordinates[3]=startFrom.clone();
		}
		if(anchor.equals(SquareCorner.CENTER)){
			coordinates[0]=startFrom.clone();
			coordinates[0].move(-(eastWestSize/2), northSouthSize/2, 0);

			coordinates[1]=startFrom.clone();
			coordinates[1].move(eastWestSize/2, northSouthSize/2, 0);

			coordinates[2]=startFrom.clone();
			coordinates[2].move(-(eastWestSize/2), -(northSouthSize/2), 0);

			coordinates[3]=startFrom.clone();
			coordinates[3].move(eastWestSize/2, -(northSouthSize/2), 0);
		}


		return coordinates;
	}

	/**
	 * Gets the great circle distance from one geographical location
	 * to another.  This is the way that planes find their routes on
	 * longer trips as a curved path around the Earth is much faster
	 * (and less distance) than a straight line.
	 * @param igpsCoordinate 
	 * @param igpsCoordinate2
	 * @return Distance in the double form of meters
	 */
	public static double greatCircleDistanced(GPSCoordinate igpsCoordinate, GPSCoordinate igpsCoordinate2){
		double deltaLon;
		if(igpsCoordinate.getLongitude() < 0 && igpsCoordinate2.getLongitude() > 0)
			deltaLon = (-1*igpsCoordinate.getLongitude())+igpsCoordinate2.getLongitude();
		else if(igpsCoordinate.getLongitude() > 0 && igpsCoordinate2.getLongitude() < 0)
			deltaLon = igpsCoordinate.getLongitude()+(-1*igpsCoordinate2.getLongitude());
		else if(igpsCoordinate.getLongitude() > igpsCoordinate2.getLongitude())
			deltaLon = igpsCoordinate.getLongitude() - igpsCoordinate2.getLongitude();
		else
			deltaLon = igpsCoordinate2.getLongitude() - igpsCoordinate.getLongitude();

		double polar1 = 90-igpsCoordinate.getLatitude();
		double polar2 = 90-igpsCoordinate2.getLatitude();
		double cosA = (StrictMath.cos(StrictMath.toRadians(polar1))*StrictMath.cos(StrictMath.toRadians(polar2))) + (StrictMath.sin(StrictMath.toRadians(polar1))*StrictMath.sin(StrictMath.toRadians(polar2))*StrictMath.cos(StrictMath.toRadians(deltaLon)));
		return ((40074*StrictMath.toDegrees(StrictMath.acos(cosA)))/360)*1000;
	}
	
	/**
	 * Calculates the average point from a number of locations
	 * @param locations The list of locations to calculate the average from
	 * @return The average location
	 */
	public static ILocation averageLocations(List<ILocation> locations){
		double latitude=0;
		double longitude=0;
		double altitude=0;
		for(ILocation location : locations){
			GPSCoordinate gps = location.getGPS();
			latitude+=gps.getLatitude();
			longitude+=gps.getLongitude();
			altitude+=gps.getAltitude();
		}
		
		return new GPSCoordinate(altitude/locations.size(), latitude/locations.size(), longitude/locations.size());
		
	}

	/**
	 * Calculates the angle between the starting coordinate and the destination
	 * coordinate.
	 * TODO More Accuracy Testing
	 * @param gps1 Starting coordinate
	 * @param gps2 Destination coordinate
	 * @return Angle of Azimuth in degrees
	 */
	public double calculateAzimuth(GPSCoordinate gps1, GPSCoordinate gps2){
		BVVec3f loc1 = locationToBetavilleInternal(gps1);
		BVVec3f loc2 = locationToBetavilleInternal(gps2);
		double toAtan = (loc2.z-loc1.z)/(loc2.x-loc1.x);
		if(toAtan<0) toAtan*=-1; // make sure we're using the absolute value
		double azimuth = StrictMath.atan(toAtan);
		return StrictMath.toDegrees(azimuth);
	}
}
