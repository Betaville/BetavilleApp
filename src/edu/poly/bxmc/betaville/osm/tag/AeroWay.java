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
package edu.poly.bxmc.betaville.osm.tag;

/**
 * Documentation taken from the
 * <a href="http://wiki.openstreetmap.org/wiki/Key:aeroway">OSM Wiki</a>
 * @author Skye Book
 *
 */
public class AeroWay extends AbstractTag {
	
	/**
	 * An Aerodrome (UK), Airport (US), see also {@link Military}=airfield
	 */
	public static final String AERODROME = "aerodrome";
	/**
	 * Airport passenger building
	 */
	public static final String TERMINAL = "terminal";
	/**
	 * Helicopter start/landing pad
	 */
	public static final String HELIPAD = "helipad";
	/**
	 * A strip of land kept clear and set aside for aeroplanes to take
	 * off from and land on.
	 */
	public static final String RUNWAY = "runway";
	/**
	 * Where airplanes manouevre between runways and parking areas.
	 */
	public static final String TAXIWAY = "taxiway";
	/**
	 * A place where planes are parked.
	 */
	public static final String APRON = "apron";
	/**
	 * Used to mark the gate numbers at the airports.
	 */
	public static final String GATE = "gate";
	/**
	 * Used to mark the position of a windsock.
	 */
	public static final String WINDSOCK = "windsock";

	/**
	 */
	public AeroWay() {
		super(keyName());
	}
	
	public static String keyName(){
		return "aeroway";
	}

}
