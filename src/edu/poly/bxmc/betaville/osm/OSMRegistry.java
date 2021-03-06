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
 * @author Skye Book
 *
 */
public class OSMRegistry {
	
	private static final OSMRegistry registry = new OSMRegistry();
	
	private List<Node> nodes = new ArrayList<Node>();
	private List<Way> ways = new ArrayList<Way>();
	private List<Relation> relations = new ArrayList<Relation>();

	/**
	 * 
	 */
	private OSMRegistry() {}
	
	public static OSMRegistry get(){
		return registry;
	}
	
	public void addNode(Node node){
		nodes.add(node);
	}
	
	public void addWay(Way way){
		ways.add(way);
	}
	
	public void addRelation(Relation relation){
		relations.add(relation);
	}
	
	public List<Node> getNodes() {
		return nodes;
	}

	public List<Way> getWays() {
		return ways;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public Node getNode(long nodeID){
		for(Node node : nodes){
			if(node.getId()==nodeID) return node;
		}
		return null;
	}
	
	public Way getWay(long wayID){
		for(Way way : ways){
			if(way.getId()==wayID) return way;
		}
		return null;
	}
	
	public Relation getRelation(long relationID){
		for(Relation relation : relations){
			if(relation.getId()==relationID) return relation;
		}
		return null;
	}
}
