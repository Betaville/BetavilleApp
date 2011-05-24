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
 * <a href="http://wiki.openstreetmap.org/wiki/Key:access">OSM Wiki</a>
 * @author Skye Book
 *
 */
public class Access extends AbstractTag {
	
	public static enum AccessTypes{
		lhv,
		roadtrain,
		roadtrain_trailers
	}
	
	/**
	 * The access conditions are unknown or unclear. This is the default value for most features.
	 */
	public static final String UNKNOWN = "unknown";
	/**
	 * The public has an official, legally-enshrined right of access, i.e. it's a right of way.
	 */
	public static final String YES = "yes";
	/**
	 * The way is a preferred/designated route for a specific vehicle type or types (by law or
	 * otherwise) but not compulsory, often marked by a traffic sign.
	 */
	public static final String DESIGNATED = "designated";
	/**
	 * The way is dedicated to a specific mode of travel by law. Usually marked by traffic signs
	 * and exclusive. In Germany use is also compulsory.
	 */
	public static final String OFFICAL = "official";
	/**
	 * The public has right of access only if this is the only road to your destination.
	 */
	public static final String DESTINATION = "destination";
	/**
	 * Only for agricultural traffic.
	 */
	public static final String AGRICULTURAL = "agricultural";
	/**
	 * Only for forestry traffic.
	 */
	public static final String FORESTRY = "forestry";
	/**
	 * This route may only be used to deliver goods to a customer.
	 */
	public static final String DELIVERY = "delivery";
	/**
	 * The owner gives general permission for access.
	 */
	public static final String PERMISSIVE = "permissive";
	/**
	 * The owner gives permission to customers.
	 */
	public static final String CUSTOMERS = "customers";
	/**
	 * The owner may give permission on an individual basis.
	 */
	public static final String PRIVATE = "private";
	/**
	 * Access by this transport mode is not permitted, public does not have a right of way.
	 */
	public static final String NO = "no";

	/**
	 */
	public Access() {
		super(keyName());
	}
	
	public static String keyName(){
		return "access";
	}

}
