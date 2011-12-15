/**
 * 
 */
package bvtest.map;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.GeoToolsBackedLocation;

/**
 * @author Skye Book
 *
 */
public class GeoToolsConverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GPSCoordinate gps = new GPSCoordinate(0, -40, 42, 4.07, -74, 0, 31.28);
			GeoToolsBackedLocation gtbl = new GeoToolsBackedLocation(gps);
			GeoToolsBackedLocation gtbl2 = new GeoToolsBackedLocation(gtbl.getUTM());
			GeoToolsBackedLocation gtbl3 = new GeoToolsBackedLocation(gtbl2.getGPS());
			GeoToolsBackedLocation gtbl4 = new GeoToolsBackedLocation(gtbl3.getUTM());
			System.out.println("1: "+gtbl.getGPS());
			System.out.println("1: "+gtbl.getUTM());
			System.out.println("2: "+gtbl2.getGPS());
			System.out.println("2: "+gtbl2.getUTM());
			System.out.println("3: "+gtbl3.getGPS());
			System.out.println("3: "+gtbl3.getUTM());
			System.out.println("4: "+gtbl4.getGPS());
			System.out.println("4: "+gtbl4.getUTM());
		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
