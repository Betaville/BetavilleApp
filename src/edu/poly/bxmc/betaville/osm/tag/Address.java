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
 * <a href="http://wiki.openstreetmap.org/wiki/Key:addr">OSM Wiki</a>
 * @author Skye Book
 *
 */
public class Address extends AbstractTag {
	
	public static enum AddressTypes{
		/**
		 * The house number (may contain non-digits).
		 * <p>If a single entry has multiple house numbers,
		 * separate them by ",". e.g. "12b,12c".  Only
		 * required key for an address (except when
		 * {@link AddressTypes#housename} is used), all others are optional.
		 */
		housenumber,
		/**
		 * The name of a house.
		 * <p>This is sometimes used in some countries like England
		 * instead of (or in addition to) a house number.
		 */
		housename,
		/**
		 * The (main) name of the respective street. 
		 * <p>A way with {@link Highway}=* and the corresponding name should be found nearby.
		 */
		street,
		/**
		 * The postal code of the building/area.
		 */
		postcode,
		/**
		 * May or may not be a clone of {@link IsIn}:city=* (in some places the city in the address
		 * corresponds to the post office that serves the area rather than the actual city,
		 * if any, in which the building is located)! The name of the city as given in postal
		 * addresses of the building/area.
		 */
		city,
		conscriptionnumber,
		provisionalnumber,
		streetnumber,
		/**
		 * The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO 3166-1 alpha-2</a> two letter country code in upper case. 
		 * Example: "DE" for Germany, "FR" for France, "IT" for Italy. 
		 * Caveat: The ISO 3166-1 code for Great Britain is "GB" and not "UK".. See also:  {@link IsIn}:country=*
		 */
		country,
		/**
		 * Use this for a full-text, usually multi-line, address if you find the structured address fields unsuitable for denoting
		 * the address of this particular location. Example: "Fifth house on the left after the village oak, Smalltown, Smallcountry"
		 */
		full,
		/**
		 * The hamlet of the object.
		 */
		hamlet,
		/**
		 * The subdistrict of the object.
		 */
		subdistrict,
		/**
		 * The district of the object.
		 */
		district,
		/**
		 * The province of the object.
		 */
		province,
		/**
		 * How to interpolate the house numbers belonging to the way along the respective street.
		 * (Could be 'all', 'even', 'odd', or 'alphabetic'
		 * <p><strong>OR</strong>
		 * <p>Every nth house between the end nodes is represented by the interpolation way.
		 */
		interpolation,
		/**
		 * Optional tag to indicate the accuracy level of survey used to create the address interpolation way. 
		 */
		inclusion
	}

	/**
	 */
	public Address() {
		super(keyName());
	}
	
	public static String keyName(){
		return "addr";
	}

}
