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

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

import edu.poly.bxmc.betaville.jme.loaders.util.DriveFinder;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;

/**
 * @author Skye Book
 *
 */
public class BXBReader extends XMLReader {
	private static Logger logger = Logger.getLogger(BXBReader.class);
	private boolean xmlLoaded=false;
	
	private int designID;
	private ILocation coordinate;

	/**
	 * 
	 * @param xmlFile
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public BXBReader(File xmlFile) throws JDOMException, IOException{
		super();
		loadFile(xmlFile);
		xmlLoaded=true;
		parse();
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.xml.XMLReader#parse()
	 */
	@Override
	public void parse(){
		String designIDString = rootElement.getChild("id").getText();
		designID = Integer.parseInt(designIDString);
		Element coordinateElement = rootElement.getChild("coordinate");
		coordinate=null;
		if(coordinateElement.getChild("type").getText().equals(UTMCoordinate.class.getName())){
			try{
			int altitude = Integer.parseInt(coordinateElement.getChild("altitude").getText());
			int northing = Integer.parseInt(coordinateElement.getChild("northing").getText());
			int easting = Integer.parseInt(coordinateElement.getChild("easting").getText());
			int lonZone = Integer.parseInt(coordinateElement.getChild("lonZone").getText());
			char latZone = coordinateElement.getChild("latZone").getText().charAt(0);
			coordinate = new UTMCoordinate(easting, northing, lonZone, latZone, altitude);
			}catch(NumberFormatException e){
				logger.error("Invalid integer found in bxb file, please ensure that elements use the proper data types", e);
			}catch (NullPointerException e) {
				logger.error("A child element was missing from the bxb <coordinate/> element", e);
			}
		}
	}
	
	public int getDesignID(){
		return designID;
	}
	
	public ILocation getCoordinate(){
		return coordinate;
	}
	
	public boolean isXMLLoaded(){
		return xmlLoaded;
	}

	public static void main(String[] args) throws JDOMException, IOException{
		BXBReader pr = new BXBReader(new File(DriveFinder.getHomeDir().toString()+"/.betaville/preferences.xml"));
		pr.parse();
		PreferenceWriter pw = new PreferenceWriter();
		pw.writeData();
	}
}
