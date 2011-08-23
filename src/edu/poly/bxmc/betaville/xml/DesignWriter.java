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
import java.io.IOException;

import org.jdom.Element;

import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;

/**
 * @author Skye Book
 *
 */
public class DesignWriter extends XMLWriter {

	/**
	 * Writes a design's metadata out to xml
	 * @param rootElementName
	 * @param file
	 * @throws IOException
	 */
	public DesignWriter(Design design, File file) throws IOException {
		super("design", file);
		
		Element type = new Element("type");
		type.addContent(design.getClass().getName());
		
		Element name = new Element("name");
		name.addContent(design.getName());
		
		Element user = new Element("user");
		user.addContent(design.getUser());
		
		Element id = new Element("id");
		id.addContent(Integer.toString(design.getID()));
		
		Element sourceID = new Element("sourceID");
		sourceID.addContent(Integer.toString(design.getSourceID()));
		
		Element coordinate = new Element("coordinate");
		Element utm = new Element(UTMCoordinate.class.getName());
		utm.addContent(new Element("easting").addContent(Integer.toString(design.getCoordinate().getEasting())));
		utm.addContent(new Element("northing").addContent(Integer.toString(design.getCoordinate().getNorthing())));
		utm.addContent(new Element("lonZone").addContent(Integer.toString(design.getCoordinate().getLonZone())));
		utm.addContent(new Element("latZone").addContent(Character.toString(design.getCoordinate().getLatZone())));
		utm.addContent(new Element("altitude").addContent(Float.toString(design.getCoordinate().getAltitude())));
		coordinate.addContent(utm);
		
		Element address = new Element("address");
		address.addContent(design.getAddress());
		
		Element cityID = new Element("cityID");
		cityID.addContent(Integer.toString(design.getCityID()));
		
		Element description = new Element("description");
		description.addContent(design.getDescription());
		
		Element filepath = new Element("filepath");
		filepath.addContent(design.getFilepath());
		
		Element url = new Element("url");
		url.addContent(design.getURL());
		
		Element dateAdded = new Element("dateAdded");
		dateAdded.addContent(design.getDateAdded());
		
		Element isPublic = new Element("isPublic");
		isPublic.addContent(Boolean.toString(design.isPublic()));
		
		Element designsToRemove = new Element("designsToRemove");
		for(Integer toRemove : design.getDesignsToRemove()){
			designsToRemove.addContent(new Element("remove").addContent(toRemove.toString()));
		}
		
		Element classification = new Element("classification");
		classification.addContent(new Element(design.getClassification().getClass().toString()).addContent(design.getClassification().toString()));
		
		
		rootElement.addContent(type);
		rootElement.addContent(name);
		rootElement.addContent(user);
		rootElement.addContent(id);
		rootElement.addContent(coordinate);
		rootElement.addContent(address);
		rootElement.addContent(cityID);
		rootElement.addContent(description);
		rootElement.addContent(filepath);
		rootElement.addContent(url);
		rootElement.addContent(dateAdded);
		rootElement.addContent(isPublic);
		rootElement.addContent(designsToRemove);
		rootElement.addContent(classification);
	}

}
