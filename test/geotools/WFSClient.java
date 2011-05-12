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
package geotools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.EmptyGeometry;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import edu.poly.bxmc.betaville.net.wfs.WFSConnection;
import edu.poly.bxmc.betaville.net.wfs.WFSParams;

/**
 * @author Skye Book
 *
 */
public class WFSClient {
	
	private String server = "http://192.168.1.6:8080/geoserver/";
	
	private CoordinateReferenceSystem epsg4326;
	private MathTransform transform;

	public WFSClient() throws IOException, NoSuchAuthorityCodeException, FactoryException, TransformException{
		
		epsg4326 = CRS.decode("EPSG:4326");
		
		String getCapabilities = server+"wfs?REQUEST=GetCapabilities";

		Map<String, String> connectionParameters = new HashMap<String, String>();
		connectionParameters.put(WFSParams.GET_CAPABILITIES_URL, getCapabilities);

		// Step 2 - connection
		DataStore data = DataStoreFinder.getDataStore(connectionParameters);

		// Step 3 - type discovery
		String typeNames[] = data.getTypeNames();
		if(typeNames.length>0){
			for(String name : typeNames){
				System.out.println("typeName " + name);
			}
		}
		String typeName = typeNames[0];
		typeName = "Betaville:DOITT_SUBWAY_ENTRANCE_01_13SEPT2010";
		SimpleFeatureTypeImpl schema = (SimpleFeatureTypeImpl) data.getSchema(typeName);

		// Step 4 - target
		// reaches out to the network
		FeatureSource<SimpleFeatureType, SimpleFeature> source = data.getFeatureSource(typeName);
		
		
		System.out.println("Metadata Bounds:"+ source.getBounds());

		// Step 5 - query
		String geomName = schema.getGeometryDescriptor().getLocalName();
		Envelope bbox = new Envelope(-100.0, -70, 25, 40);

		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
		Object polygon = JTS.toGeometry(bbox);
		Intersects filter = ff.intersects(ff.property(geomName), ff.literal(polygon));

		//Query query = new DefaultQuery(typeName, filter, new String[]{geomName});
		Query query = new DefaultQuery(typeName);
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = source.getFeatures(query);
		
		ReferencedEnvelope bounds = new ReferencedEnvelope();
		Iterator<SimpleFeature> iterator = features.iterator();
		try {
			while(iterator.hasNext()){
				SimpleFeature feature = (SimpleFeature) iterator.next();
				System.out.println("Feature: " + feature.getIdentifier().getID());
				bounds.include(feature.getBounds());
				if(feature.getDefaultGeometry() instanceof Point){
					Point point = (Point) feature.getDefaultGeometry();
					//System.out.println("SRID: "+point.getSRID());
					transform = CRS.findMathTransform(feature.getType().getCoordinateReferenceSystem(), epsg4326);
					Coordinate latLon = JTS.transform(point.getCoordinate(), null, transform);
					System.out.println(latLon.toString());
				}
				else if(feature.getDefaultGeometry() instanceof Polygon){
					
				}
				else if(feature.getDefaultGeometry() instanceof EmptyGeometry){
					
				}
				System.out.println(feature.getDefaultGeometry().getClass().getName());
				for(Property p : feature.getProperties()){
					System.out.println("\t"+p.getType().getName());
				}
			}
			System.out.println("Calculated Bounds:"+ bounds);
		}
		finally {
			features.close(iterator);
		}
	}
	
	public static void main(String[] args) throws IOException, NoSuchAuthorityCodeException, FactoryException, TransformException{
		long start = System.currentTimeMillis();
		//new WFSClient();
		WFSConnection wfsc = new WFSConnection("http://192.168.1.6:8080/geoserver/");
		for(String typeName : wfsc.getAvailableLayers("Betaville")){
			System.out.println(typeName);
		}
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = wfsc.requestSomething("Betaville:DOITT_SUBWAY_ENTRANCE_01_13SEPT2010");
		FeatureIterator iterator = features.features();
		while(iterator.hasNext()){
			Feature feature = iterator.next();
		}
		
		System.out.println("Request took " + (System.currentTimeMillis()-start));
	}
	
}
