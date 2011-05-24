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
package edu.poly.bxmc.betaville.osm.keys;

/**
 * Documentation taken from the
 * <a href="http://wiki.openstreetmap.org/wiki/Map_Features#Aerialway">OSM Wiki</a>
 * <p>The aerialway tag is used to describe any kind of lifts.
 * @author Skye Book
 *
 */
public class AerialWay extends AbstractKey {
	
	/**
	 * A cable car run. Cable_cars are aerial tramways that are just one pair of
	 * big cars that go in alternating directions to each other from A to B
	 * (Usually Valley to Hill or Mountain)
	 */
	public static final String CABLE_CAR = "cable_car";
	/**
	 * A gondola. A Gondola is an aerialway where the cabins go around in a circle.
	 * The cabins are usually smaller than cabins of a cable car. Usual maximum
	 * occupancy is 4,6 or 8 persons per cabin
	 */
	public static final String GONDOLA = "gondola";
	/**
	 * An open chairlift run. These have a single seat to sit on and are open to the
	 * outside air. Beginners may struggle to get off them without making a fool of
	 * themselves!
	 */
	public static final String CHAIR_LIFT = "chair_lift";
	/**
	 * A mixed lift, containing both gondolas and chairs.
	 */
	public static final String MIXED_LIFT = "mixed_lift";
	/**
	 * An overhead tow-line for skiers and riders, known as a T-bar lift, with a T
	 * shaped bit which two skiers share by resting their bottom on the bar or a
	 * button lift where the skier places a circular disk in between their legs. More
	 * difficult and uncomfortable for snowboarders. drag_lift would also cover more
	 * simple looped rope drag lifts, or loops of wire with handles to grab
	 */
	public static final String DRAG_LIFT = "drag_lift";
	/**
	 * A station, where passengers can enter and/or leave the aerialway
	 */
	public static final String STATION = "station";
	/**
	 * A pylon supporting the aerialway cable.
	 */
	public static final String PYLON = "pylon";
	/**
	 * A lift for goods. Passenger transport is usually not allowed
	 */
	public static final String GOODS = "goods";
	
	/**
	 */
	public AerialWay() {
		super(keyName());
	}
	
	public static String keyName(){
		return "aerialway";
	}

}
