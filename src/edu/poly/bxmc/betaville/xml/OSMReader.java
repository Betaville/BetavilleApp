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
package edu.poly.bxmc.betaville.xml;

import java.io.File;
import java.util.List;

import org.jdom.Element;

import edu.poly.bxmc.betaville.osm.KeyMatcher;
import edu.poly.bxmc.betaville.osm.Node;
import edu.poly.bxmc.betaville.osm.OSMObject;
import edu.poly.bxmc.betaville.osm.Relation;
import edu.poly.bxmc.betaville.osm.Way;
import edu.poly.bxmc.betaville.osm.tag.AbstractTag;

/**
 * @author Skye Book
 *
 */
public class OSMReader extends XMLReader {

	/**
	 * 
	 */
	public OSMReader(){
		super();
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.xml.XMLReader#parse()
	 */
	@Override
	public void parse() throws Exception {
		// get all of the node data first
		List<?> nodes = rootElement.getChildren("node");
		for(Object node : nodes){
			
		}
	}
	
	private Node createNode(Element element) throws InstantiationException, IllegalAccessException{
		Node node = new Node();
		processGenerics(node, element);
		return node;
	}
	
	private Way createWay(Element element) throws InstantiationException, IllegalAccessException{
		Way way = new Way();
		processGenerics(way, element);
		return way;
	}
	
	private Relation createRelation(Element element) throws InstantiationException, IllegalAccessException{
		Relation relation = new Relation();
		processGenerics(relation, element);
		return relation;
	}
	
	private void processGenerics(OSMObject object, Element element) throws InstantiationException, IllegalAccessException{
		object.setId(Long.parseLong(element.getAttributeValue("id")));
		object.setChangeset(Long.parseLong(element.getAttributeValue("changeset")));
		object.setTimestamp(element.getAttributeValue("timestamp"));
		object.setUser(element.getAttributeValue("user"));
		for(Object tag : element.getChildren("tag")){
			AbstractTag tagItem = KeyMatcher.getKey(((Element)tag).getAttributeValue("k")).newInstance();
			tagItem.setValue(((Element)tag).getAttributeValue("v"));
			object.addTag(tagItem);
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		OSMReader reader = new OSMReader();
		reader.loadFile(new File("/Users/skyebook/Downloads/map.osm"));
		reader.parse();
	}

}
