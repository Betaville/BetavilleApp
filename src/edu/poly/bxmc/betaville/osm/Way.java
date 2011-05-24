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

import java.util.ArrayList;

/**
 * From the <a href="http://wiki.openstreetmap.org/wiki/Elements">OSM Wiki</a>:
 * 
 * <p>"A way is an ordered interconnection of at least 2 and at most 2000
 * nodes (as of API v0.6) that describe a linear feature such as a street,
 * or similar. Should you reach the node limit simply split your way and
 * group all ways in a relation if necessary. Nodes can be members of multiple
 * ways.
 * 
 * <p>One way is characterized with uniform properties. Same properties are
 * for example, same priority (motorway, federal highway, ...), same surface
 * quality, same speed, etc. Ways can be split later into shorter ways if
 * different properties exist (for instance, if a street has a one-way
 * section, that section would be a different way from the two-way section,
 * even though they share the same name)."
 * 
 * @author Skye Book
 *
 */
public class Way extends OSMObject{
	
	private ArrayList<Node> nodes = new ArrayList<Node>();

	/**
	 * 
	 */
	public Way() {
	}

}
