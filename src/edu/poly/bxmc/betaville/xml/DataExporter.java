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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.poly.bxmc.betaville.bookmarks.Bookmark;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.AudibleDesign;
import edu.poly.bxmc.betaville.model.Comment;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.EmptyDesign;
import edu.poly.bxmc.betaville.model.ModeledDesign;
import edu.poly.bxmc.betaville.model.VideoDesign;

/**
 * Writes objects to XML
 * @author Skye Book
 */
public class DataExporter{
	private static final Logger logger = Logger.getLogger(DataExporter.class);
	
	public static Element export(Bookmark b){
		Element bookmark = new Element("bookmark");
		bookmark.setAttribute("name", b.getName());
		bookmark.setAttribute("bookmarkID", b.getBookmarkID());
		bookmark.setAttribute("user", b.getUser());
		bookmark.setAttribute("createdOn", Long.toString(b.getCreatedOn()));
		bookmark.setAttribute("description", b.getDescription());
		bookmark.setAttribute("directionX", Float.toString(b.getDirectionX()));
		bookmark.setAttribute("directionY", Float.toString(b.getDirectionY()));
		bookmark.setAttribute("directionZ", Float.toString(b.getDirectionZ()));
		bookmark.addContent(export(b.getLocation()));
		return bookmark;
	}
	
	public static Element export(ILocation location){
		Element coordinate = new Element("coordinate");
		coordinate.setAttribute("type", location.getClass().getName());
		if(location instanceof UTMCoordinate){
			coordinate.setAttribute("easting", Integer.toString(((UTMCoordinate)location).getEasting()));
			coordinate.setAttribute("northing", Integer.toString(((UTMCoordinate)location).getNorthing()));
			coordinate.setAttribute("eastingCentimeters", Short.toString(((UTMCoordinate)location).getEastingCentimeters()));
			coordinate.setAttribute("northingCentimeters", Short.toString(((UTMCoordinate)location).getNorthingCentimeters()));
			coordinate.setAttribute("lonZone", Integer.toString(((UTMCoordinate)location).getLonZone()));
			coordinate.setAttribute("latZone", Character.toString(((UTMCoordinate)location).getLatZone()));
			coordinate.setAttribute("altitude", Integer.toString(((UTMCoordinate)location).getAltitude()));
		}
		else if(location instanceof GPSCoordinate){
			coordinate.setAttribute("latitude", Double.toString(((GPSCoordinate)location).getLatitude()));
			coordinate.setAttribute("longitude", Double.toString(((GPSCoordinate)location).getLongitude()));
			coordinate.setAttribute("altitude", Double.toString(((GPSCoordinate)location).getAltitude()));
		}
		return coordinate;
	}
	
	public static Element exportDesigns(List<Design> designs){
		Element e = new Element("designs");
		for(Design design : designs){
			e.addContent(export(design));
		}
		return e;
	}
	
	public static Element export(Design design){
		Element d = new Element(design.getClass().getName());
		d.setAttribute("name", design.getName());
		d.setAttribute("classification", design.getClassification().toString());
		d.setAttribute("id", Integer.toString(design.getID()));
		d.setAttribute("sourceID", Integer.toString(design.getSourceID()));
		if(design.getAddress()!=null) d.setAttribute("address", design.getAddress());
		d.setAttribute("cityID", Integer.toString(design.getCityID()));
		d.setAttribute("user", design.getUser());
		if(design.getDescription()!=null) d.setAttribute("description", design.getDescription());
		d.setAttribute("filepath", design.getFilepath());
		if(design.getURL()!=null) d.setAttribute("url", design.getURL());
		d.setAttribute("dateAdded", design.getDateAdded());
		d.setAttribute("isPublic", Boolean.toString(design.isPublic()));
		
		// construct designs to remove string
		StringBuilder dtr = new StringBuilder();
		for(int toRemove : design.getDesignsToRemove()){
			dtr.append(toRemove+";");
		}
		d.setAttribute("designsToRemove", dtr.toString());
		
		// construct user fave string
		StringBuilder favedBy = new StringBuilder();
		for(String userWhoFaved : design.getFavedBy()){
			favedBy.append(userWhoFaved+";");
		}
		d.setAttribute("favedBy", favedBy.toString());
		
		if(design.getProposalPermission()!=null) d.setAttribute("proposalPermission", design.getProposalPermission().toString());
		
		if(design instanceof ModeledDesign){
			d.setAttribute("rotX", Integer.toString(((ModeledDesign)design).getRotationX()));
			d.setAttribute("rotY", Integer.toString(((ModeledDesign)design).getRotationY()));
			d.setAttribute("rotZ", Integer.toString(((ModeledDesign)design).getRotationZ()));
			d.setAttribute("textured", Boolean.toString(((ModeledDesign)design).isTextured()));
		}
		else if(design instanceof AudibleDesign){
			d.setAttribute("directionX", Float.toString(((AudibleDesign)design).getDirectionX()));
			d.setAttribute("directionY", Float.toString(((AudibleDesign)design).getDirectionY()));
			d.setAttribute("directionZ", Float.toString(((AudibleDesign)design).getDirectionZ()));
			d.setAttribute("volume", Integer.toString(((AudibleDesign)design).getVolume()));
			// TODO: SoundStyle serialization
		}
		else if(design instanceof VideoDesign){
			d.setAttribute("directionX", Float.toString(((VideoDesign)design).getDirectionX()));
			d.setAttribute("directionY", Float.toString(((VideoDesign)design).getDirectionY()));
			d.setAttribute("directionZ", Float.toString(((VideoDesign)design).getDirectionZ()));
			d.setAttribute("volume", Integer.toString(((VideoDesign)design).getVolume()));
			d.setAttribute("length", Integer.toString(((VideoDesign)design).getLength()));
			d.setAttribute("format", ((VideoDesign)design).getFormat().toString());
		}
		else if(design instanceof EmptyDesign){
			d.setAttribute("length", Integer.toString(((EmptyDesign)design).getLength()));
			d.setAttribute("width", Integer.toString(((EmptyDesign)design).getWidth()));
		}
		
		d.addContent(export(design.getCoordinate()));
		
		return d;
	}
	
	public static Element exportComments(List<Comment> comments){
		Element e = new Element("comments");
		for(Comment comment : comments){
			e.addContent(export(comment));
		}
		return e;
	}
	
	public static Element export(Comment comment){
		Element e = new Element(comment.getClass().getName());
		e.setAttribute("id", Integer.toString(comment.getID()));
		e.setText(comment.getComment());
		e.setAttribute("user", comment.getUser());
		e.setAttribute("designID", Integer.toString(comment.getDesignID()));
		e.setAttribute("repliesToCommentID", Integer.toString(comment.repliesTo()));
		e.setAttribute("date", comment.getDate());
		return e;
	}
	
	public static void write(Element element, File f) throws IOException{
		Document dom = new Document(element);
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		FileWriter writer = new FileWriter(f);
		outputter.output(dom, writer);
		writer.close();
	}

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, IOException {
		Bookmark bookmark = new Bookmark("hi", "hello", new GPSCoordinate(5, 5, 5), 1, 1, 1);
		write(export(bookmark), new File("bookmark.bvbm"));
	}
}
