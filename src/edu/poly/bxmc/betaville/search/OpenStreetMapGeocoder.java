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
package edu.poly.bxmc.betaville.search;

import java.io.IOException;
import java.net.URL;

import org.jdom.Element;

import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.xml.XMLReader;

/**
 * Performs geocoding, and reverse geocoding, operations
 * @author Skye Book
 *
 */
public class OpenStreetMapGeocoder implements Geocoder {
	
	private static final String SERVICE_URL = "http://nominatim.openstreetmap.org/";
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.search.Geocoder#search(java.lang.String)
	 */
	@Override
	public ILocation search(String searchTerm) throws IOException {
		try {
			// construct the request
			URL request = new URL(SERVICE_URL+"search?format=xml&addressdetails=1&zoom=18&q="+searchTerm);
			
			// parse and return the results
			SearchResults sr = new SearchResults();
			sr.loadFile(request);
			sr.parse();
			return sr.getLocation();
		} catch (Exception e) {
			if(e instanceof IOException) throw new IOException(e.getMessage());
			else return null;
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.search.Geocoder#reverse(edu.poly.bxmc.betaville.jme.map.ILocation)
	 */
	@Override
	public String reverse(ILocation location) throws IOException{
		try {
			// construct the request
			GPSCoordinate gps = location.getGPS();
			URL request = new URL(SERVICE_URL+"reverse?format=xml&addressdetails=1&zoom=18&lat="+gps.getLatitude()+"&lon="+gps.getLongitude());
			
			// parse and return the results
			ReverseGeocode rr = new ReverseGeocode();
			rr.loadFile(request);
			rr.parse();
			return rr.getLocation();
		} catch (Exception e) {
			if(e instanceof IOException) throw new IOException(e.getMessage());
			else return null;
		}
	}
	
	private class SearchResults extends XMLReader{
		
		private ILocation location = null;

		/* (non-Javadoc)
		 * @see edu.poly.bxmc.betaville.xml.XMLReader#parse()
		 */
		@Override
		public void parse() throws Exception {
			// root element is called "searchresults"
			Element place = rootElement.getChild("place");
			
			// place should have the lat/lon attributes.. easy, eh?
			double lat = Double.parseDouble(place.getAttributeValue("lat"));
			double lon = Double.parseDouble(place.getAttributeValue("lon"));
			
			location = new GPSCoordinate(0, lat, lon);
		}
		
		public ILocation getLocation(){
			return location;
		}
	}
	
	private class ReverseGeocode extends XMLReader{
		
		private String location = null;

		/* (non-Javadoc)
		 * @see edu.poly.bxmc.betaville.xml.XMLReader#parse()
		 */
		@Override
		public void parse() throws Exception {
			/*
			 * We are requesting the more complex 'addressdetails' in addition to the result, which
			 * gives us the parts of the full result split up into house number, street, city, etc
			 */
			
			// root element is called "reversegeocode"
			Element details = rootElement.getChild("addressparts");
			location = details.getChild("road").getText();
			
			
		}
		
		public String getLocation(){
			return location;
		}
		
	}

}
