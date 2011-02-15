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
package edu.poly.bxmc.betaville.bookmarks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.xml.XMLReader;

/**
 * @author Skye Book
 *
 */
public class BookmarkReader extends XMLReader {
	private static Logger logger = Logger.getLogger(BookmarkReader.class);
	private boolean xmlLoaded=false;
	private Bookmark bookmark;
	
	public static List<Bookmark> readBookmarks(File folderOfFiles) throws JDOMException, IOException{
		ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
		if(!folderOfFiles.isDirectory()) return bookmarks;
		for(File f : folderOfFiles.listFiles()){
			BookmarkReader reader = new BookmarkReader(f);
			reader.parse();
			bookmarks.add(reader.getResult());
		}
		return bookmarks;
	}

	/**
	 * 
	 * @param xmlFile
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public BookmarkReader(File xmlFile) throws JDOMException, IOException{
		super();
		loadFile(xmlFile);
		xmlLoaded=true;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.xml.XMLReader#parse()
	 */
	@Override
	public void parse(){
		try {
			parseImpl(rootElement);
		} catch (ClassNotFoundException e) {
			logger.error("Your classpath must be incorrect!", e);
		}
	}
	
	private void parseImpl(Element top) throws ClassNotFoundException{
		Element b = top.getChild("bookmark");
		ILocation loc = null;
		Element locEle = b.getChild("coordinate");
		if(Class.forName(locEle.getAttributeValue("type")).equals(UTMCoordinate.class)){
			loc = new UTMCoordinate(Integer.parseInt(locEle.getAttributeValue("easting")),
					Integer.parseInt(locEle.getAttributeValue("northing")),
					Short.parseShort(locEle.getAttributeValue("eastingCentimeters")),
					Short.parseShort(locEle.getAttributeValue("northingCentimeters")),
					Integer.parseInt(locEle.getAttributeValue("lonZone")),
					locEle.getAttributeValue("latZone").charAt(0),
					Integer.parseInt(locEle.getAttributeValue("altitude")));
		}
		else if(Class.forName(locEle.getAttributeValue("type")).equals(GPSCoordinate.class)){
			
		}
		bookmark = new Bookmark(b.getAttributeValue("name"), b.getAttributeValue("bookmarkID"),
				b.getAttributeValue("user"), Long.parseLong(b.getAttributeValue("createdOn")),
				b.getAttributeValue("description"), loc, Float.parseFloat(b.getAttributeValue("directionX")),
				Float.parseFloat(b.getAttributeValue("directionY")), Float.parseFloat(b.getAttributeValue("directionZ")));
	}
	
	public boolean isXMLLoaded(){
		return xmlLoaded;
	}
	
	public Bookmark getResult(){
		return bookmark;
	}
}
