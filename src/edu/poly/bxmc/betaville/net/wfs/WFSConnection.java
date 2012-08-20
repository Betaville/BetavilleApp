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
package edu.poly.bxmc.betaville.net.wfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.geometry.jts.JTS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Skye Book
 *
 */
public class WFSConnection {
	
	private final String getCapabilitiesSuffix ="wfs?service=wfs&version=1.1.0&request=GetCapabilities";
	
	private DataStore capabilities;

	/**
	 * @throws IOException 
	 * 
	 */
	public WFSConnection(String server) throws IOException {
		HashMap<String, String> capabilityParameters = new HashMap<String, String>();
		capabilityParameters.put(WFSParams.GET_CAPABILITIES_URL, server+getCapabilitiesSuffix);
		capabilities = DataStoreFinder.getDataStore(capabilityParameters);
	}
	
	public List<String> getAvailableLayers(String workspaceFilter) throws IOException{
		ArrayList<String> typeNames = new ArrayList<String>();
		for(String typeName : capabilities.getTypeNames()){
			if(typeName.startsWith(workspaceFilter)) typeNames.add(typeName);
		}
		return typeNames;
	}
	
	public FeatureCollection<SimpleFeatureType, SimpleFeature> requestSomething(String layer) throws IOException{
		SimpleFeatureTypeImpl schema = (SimpleFeatureTypeImpl) capabilities.getSchema(layer);

		// Step 4 - target
		// reaches out to the network
		FeatureSource<SimpleFeatureType, SimpleFeature> source = capabilities.getFeatureSource(layer);
		
		
		System.out.println("Metadata Bounds:"+ source.getBounds());

		// Step 5 - query
		String geomName = schema.getGeometryDescriptor().getLocalName();
		Envelope bbox = new Envelope(-100.0, -70, 25, 40);

		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
		Object polygon = JTS.toGeometry(bbox);
		Intersects filter = ff.intersects(ff.property(geomName), ff.literal(polygon));

		//Query query = new DefaultQuery(typeName, filter, new String[]{geomName});
		Query query = new Query(layer);
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = source.getFeatures(query);
		return features;
	}
}
