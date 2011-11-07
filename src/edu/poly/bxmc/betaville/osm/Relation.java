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
import java.util.List;

/**
 * From the <a href="http://wiki.openstreetmap.org/wiki/Elements">OSM Wiki</a>:
 * 
 * <p>"A relation can group other elements together, nodes, ways,
 * and maybe even other relations. Elements are 'members' of the
 * relation, and each membership has a 'role'. As with other types
 * of elements, a relation may have an arbitrary number of tags.
 * You may also have duplicate nodes, ways or relations within a single relation.
 * 
 * <p>Typically you would expect the 'type' tag to be set, specifying
 * what type of relation it is. Relations can be used to represent
 * things like cycle routes, and turn restrictions. For documentation
 * of different relation types (and proposed new relation types) see
 * the <a href="http://wiki.openstreetmap.org/wiki/Relations">Relations</a> page.
 * 
 * <p>The ordering of elements within a relation is persistent. The members are
 * returned in the order specified at upload. Duplicate elements will retain
 * their specified order.
 * 
 * @author Skye Book
 *
 */
public class Relation extends OSMObject{
	
	private List<RelationMemeber> members = new ArrayList<RelationMemeber>();
	
	/**
	 * 
	 */
	public Relation() {
		// TODO Auto-generated constructor stub
	}
	
	public void addMemeber(RelationMemeber member){
		members.add(member);
	}
	
	public List<RelationMemeber> getMemebers(){
		return members;
	}
	
	@Override
	public String toString() {
		return "Relation "+id;
	}

}
