/**
 * 
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
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;

import com.vividsolutions.jts.geom.Envelope;

import edu.poly.bxmc.betaville.net.wfs.WFSParams;

/**
 * @author Skye Book
 *
 */
public class WFSClient {
	
	private String server = "http://192.168.1.6:8080/geoserver/";

	public WFSClient() throws IOException{
		
		
		
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
		SimpleFeatureTypeImpl schema = (SimpleFeatureTypeImpl) data.getSchema(typeName);

		// Step 4 - target
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
				Feature feature = (Feature) iterator.next();
				bounds.include(feature.getBounds());
			}
			System.out.println("Calculated Bounds:"+ bounds);
		}
		finally {
			features.close(iterator);
		}
	}
	
	public static void main(String[] args) throws IOException{
		long start = System.currentTimeMillis();
		new WFSClient();
		System.out.println("Request took " + (System.currentTimeMillis()-start));
	}

}
