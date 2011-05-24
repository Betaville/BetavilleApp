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
package edu.poly.bxmc.betaville.osm;

import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;

/**
 * From the <a href="http://wiki.openstreetmap.org/wiki/Elements">OSM Wiki</a>:
 * 
 * <p>"A node is the basic element of the OSM scheme. Nodes consist of latitude
 * and longitude (a single geospatial point).  Nodes are needed to define a
 * {@link Way}, but a node can also be a standalone unconnected point
 * representing something like a telephone box, a pub, a "place" name label,
 * or all kinds of other points of interest (POI). Standalone nodes should
 * always have at least one Tag such as amenity=telephone.
 * 
 * <p>Nodes forming part of a way often do not have tags themselves (they only
 * exist to describe the way), but this isn't a hard and fast rule. For example,
 * a node can be tagged railway=station and also appear along a way tagged
 * railway=rail."
 * 
 * @author Skye Book
 *
 */
public class Node extends OSMObject{
	
	private GPSCoordinate location;

	/**
	 * 
	 */
	public Node() {
		super();
	}

	/**
	 * @return the location
	 */
	public GPSCoordinate getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(GPSCoordinate location) {
		this.location = location;
	}
}
