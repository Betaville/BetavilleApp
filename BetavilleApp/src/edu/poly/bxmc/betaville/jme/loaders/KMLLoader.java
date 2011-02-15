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
package edu.poly.bxmc.betaville.jme.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;

/**
 * <code>KMLLoader</code> provides the functionality to interpret
 * the popular KML file format.  The file offers a number of different
 * features such as storing model information (textures needed, file names,
 * etc) as well as storing geographic data about where the geometry belongs
 * and in what orientation and scale.  KML files are freely available from
 * a number of different sites, services, and applications (with Google, of
 * course, being a major supplier).
 * 
 * Documentation: http://code.google.com/apis/kml/documentation/kmlreference.html
 * @author Skye Book
 * @Unused
 *
 */
public class KMLLoader {
	private File file;
	private GPSCoordinate location = null;
	private GPSCoordinate modelLocation=null;

	/**
	 * 
	 */
	public KMLLoader(File file) {
		this.file=file;
		readPreferences();
	}

	

private void readPreferences(){
	try {
		FileInputStream inStream = new FileInputStream(file);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = inputFactory.createXMLEventReader(inStream);
			while(eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();
				if(event.getEventType()==XMLStreamConstants.START_ELEMENT){
					if(event.asStartElement().getName().getLocalPart() == "Document"){
						event = eventReader.nextEvent();
						if(event.asStartElement().getName().getLocalPart() == "name"){
							event = eventReader.nextEvent();
						}
						if(event.asStartElement().getName().getLocalPart() == "Style"){
							event = eventReader.nextEvent();
						}
						if(event.asStartElement().getName().getLocalPart() == "StyleMap"){
							event = eventReader.nextEvent();
						}
						if(event.asStartElement().getName().getLocalPart() == "Placemark"){
							event = eventReader.nextEvent();
							if(event.asStartElement().getName().getLocalPart() == "name"){
								event = eventReader.nextEvent();
							}
							if(event.asStartElement().getName().getLocalPart() == "LookAt"){
								event = eventReader.nextEvent();
								if(event.asStartElement().getName().getLocalPart() == "latitude"){
									event = eventReader.nextEvent();
								}
								if(event.asStartElement().getName().getLocalPart() == "longitude"){
									event = eventReader.nextEvent();
								}
								if(event.asStartElement().getName().getLocalPart() == "altitude"){
									event = eventReader.nextEvent();
								}
							}
							if(event.asStartElement().getName().getLocalPart() == "Point"){
								event = eventReader.nextEvent();
								if(event.asStartElement().getName().getLocalPart() == "coordinates"){
									String rawVal = event.asCharacters().getData();
									double lon = Double.parseDouble(rawVal.substring(0, rawVal.indexOf(",")));
									rawVal = rawVal.substring(rawVal.indexOf(",")+1, rawVal.length());
									double lat = Double.parseDouble(rawVal.substring(0, rawVal.indexOf(",")));
									rawVal = rawVal.substring(rawVal.indexOf(",")+1, rawVal.length());
									double alt = Double.parseDouble(rawVal.substring(0, rawVal.indexOf(",")));
									rawVal = rawVal.substring(rawVal.indexOf(",")+1, rawVal.length());
									modelLocation = new GPSCoordinate(alt, lat, lon);
									event = eventReader.nextEvent();
								}
							}
							if(event.asStartElement().getName().getLocalPart() == "Model"){
								event = eventReader.nextEvent();
								if(event.asStartElement().getName().getLocalPart() == "altitudeMode"){
									event = eventReader.nextEvent();
									if(event.asStartElement().getName().getLocalPart() == "Location"){
										event = eventReader.nextEvent();
										if(event.asStartElement().getName().getLocalPart() == "latitude"){
											event = eventReader.nextEvent();
										}
										if(event.asStartElement().getName().getLocalPart() == "longitude"){
											event = eventReader.nextEvent();
										}
										if(event.asStartElement().getName().getLocalPart() == "altitude"){
											event = eventReader.nextEvent();
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

public GPSCoordinate getLocation(){
	return location;
}

public GPSCoordinate getModelLocation(){
	return modelLocation;
}
}