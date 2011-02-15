/**
 * 
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
		utm.addContent(new Element("altitude").addContent(Integer.toString(design.getCoordinate().getAltitude())));
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
