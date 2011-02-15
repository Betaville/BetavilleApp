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

import edu.poly.bxmc.betaville.jme.map.MapManager.SquareCorner;

/**
 * Maintains a bounding box that <em>should</em> be square.
 * @author Skye Book
 *
 */
public class BoundingBox {

	GPSCoordinate ne;
	GPSCoordinate nw;
	GPSCoordinate sw;
	GPSCoordinate se;

	public BoundingBox(GPSCoordinate nw, GPSCoordinate ne, GPSCoordinate sw, GPSCoordinate se) {
		this.nw=nw;
		this.ne=ne;
		this.sw=sw;
		this.se=se;
	}

	/**
	 * @return the the northeast coordinate
	 */
	public GPSCoordinate getNe() {
		return ne;
	}

	/**
	 * @param ne the northeast coordinate to set
	 */
	public void setNe(GPSCoordinate ne) {
		this.ne = ne;
	}

	/**
	 * @return the northwest coordinate
	 */
	public GPSCoordinate getNw() {
		return nw;
	}

	/**
	 * @param nw the northwest coordinate to set
	 */
	public void setNw(GPSCoordinate nw) {
		this.nw = nw;
	}

	/**
	 * @return the southwest coordinate
	 */
	public GPSCoordinate getSw() {
		return sw;
	}

	/**
	 * @param sw the southwest coordinate to set
	 */
	public void setSw(GPSCoordinate sw) {
		this.sw = sw;
	}

	/**
	 * @return the southeast coordinate
	 */
	public GPSCoordinate getSe() {
		return se;
	}

	/**
	 * @param se the southeast coordinate to set
	 */
	public void setSe(GPSCoordinate se) {
		this.se = se;
	}
	
	/**
	 * Tests to see if this BoundingBox fits inside of a
	 * supplied BoundingBox
	 * @param outer The BoundingBox that we're trying to
	 * fit into.
	 * @return true is all four points of this bounding box
	 * are contained within the outer parameter, false if they
	 * do not.
	 */
	public boolean fitsInside(BoundingBox outer){
		return !isInside(outer.nw) && !isInside(outer.ne) && !isInside(outer.sw) && !isInside(outer.se);
	}
	
	/**
	 * Checks if a coordinate is located inside of this BoundingBox
	 * @param coordinate The coordinate to check.
	 * @return true of the coordinate is located inside; false if it is not.
	 * @see IGPSCoordinate
	 */
	public boolean isInside(GPSCoordinate coordinate){
		return coordinate.getLatitude()<=nw.getLatitude()&&
		coordinate.getLatitude()>=sw.getLatitude()&&
		coordinate.getLongitude()>=nw.getLongitude()&&
		coordinate.getLongitude()<=ne.getLongitude();
	}
	
	public double[] getOffset(BoundingBox other){
		int wholeNorthMeters = other.sw.getUTM().getNorthing()-sw.getUTM().getNorthing();
		int cmNorthDiff = other.sw.getUTM().getNorthingCentimeters()-sw.getUTM().getNorthingCentimeters();
		double cmNorth = 0;
		if(cmNorthDiff!=0)cmNorth = Double.valueOf(100d/cmNorthDiff);
		int wholeEastMeters = other.sw.getUTM().getEasting()-sw.getUTM().getEasting();
		int cmEastDiff = other.sw.getUTM().getEastingCentimeters()-sw.getUTM().getEastingCentimeters();
		double cmEast = 0;
		if(cmEastDiff!=0)cmEast = Double.valueOf(100d/cmEastDiff);
		
		return new double[]{cmNorth+wholeNorthMeters, cmEast+wholeEastMeters};
	}
	
	public static void main(String[] args){
		UTMCoordinate[] big = MapManager.createBox(5000, 5000, SquareCorner.SW, new GPSCoordinate(0, 42,-73).getUTM());
		UTMCoordinate[] little = MapManager.createBox(2500, 2500, SquareCorner.SW, new GPSCoordinate(0, 42.1,-72.9).getUTM());
		BoundingBox bb = new BoundingBox(big[0].getGPS(), big[1].getGPS(), big[2].getGPS(), big[3].getGPS());
		BoundingBox small = new BoundingBox(little[0].getGPS(), little[1].getGPS(), little[2].getGPS(), little[3].getGPS());
		System.out.println(small.fitsInside(bb));
		double[] offset = bb.getOffset(small);
		System.out.println(offset[0]+","+offset[1]);
	}
}
