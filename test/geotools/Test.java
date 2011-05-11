package geotools;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Skye Book
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws FactoryException 
	 * @throws NoSuchAuthorityCodeException 
	 * @throws TransformException 
	 */
	public static void main(String[] args) throws NoSuchAuthorityCodeException, FactoryException, TransformException {
		CoordinateReferenceSystem crs = CRS.decode("EPSG:2263");
		CoordinateReferenceSystem gpsCRS = CRS.decode("EPSG:4326");
		System.out.println(crs.getName());
		Coordinate c = new Coordinate(1001595.9338001014, 186392.22667485476);
		GeodeticCalculator gc = new GeodeticCalculator(crs);
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		Coordinate coord = new Coordinate( 1, 1 );
		Point point = geometryFactory.createPoint( c );
		
		MathTransform mt = CRS.findMathTransform(crs, gpsCRS);
		
		Coordinate newCoordinate = JTS.transform(c, null, mt);
		System.out.println("New Coordinate: " + newCoordinate.toString());
	}

}
