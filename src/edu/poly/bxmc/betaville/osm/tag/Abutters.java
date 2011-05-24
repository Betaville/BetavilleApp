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
 * <a href="http://wiki.openstreetmap.org/wiki/Map_Features#Abutters">OSM Wiki</a>
 * @author Skye Book
 *
 */
public class Abutters extends AbstractTag {
	
	/**
	 * Predominantly houses and apartments
	 */
	public static final String RESIDENTIAL  = "residential";
	/**
	 * Predominantly shops
	 */
	public static final String RETAIL = "retail";
	/**
	 * Predominantly office buildings, business parks, etc.
	 */
	public static final String COMMERCIAL = "commercial";
	/**
	 * Predominantly workshops, factories, warehouses
	 */
	public static final String INDUSTRIAL = "industrial";
	/**
	 * Where there is no clear predominance, for example mixed shops and residences in an inner city ring
	 */
	public static final String MIXED = "mixed";
	

	/**
	 */
	public Abutters() {
		super(keyName());
	}
	
	public static String keyName(){
		return "abutters";
	}

}
