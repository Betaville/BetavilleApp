/**
 * 
 */
package edu.poly.bxmc.betaville.jme.map;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import junit.framework.TestCase;

/**
 * @author Skye Book
 *
 */
public class GPSUTMConversion extends TestCase {
	
	public void testMapManagerConversions(){
		GPSCoordinate gps = new GPSCoordinate(0, 40, 42, 4.07, -74, 0, 31.28);
		UTMCoordinate utm = gps.getUTM();
		assertTrue(gpsCorrect(gps, utm.getGPS()));
	}
	
	public void testGeotoolsConversions(){
		GPSCoordinate gps = new GPSCoordinate(0, 40, 42, 4.07, -74, 0, 31.28);
		try {
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
			assertTrue(gtbl2.getGPS().equals(gtbl3.getGPS()));
			assertTrue(gtbl3.getGPS().equals(gtbl4.getGPS()));
			assertTrue(gtbl2.getUTM().equals(gtbl3.getUTM()));
			assertTrue(gtbl3.getUTM().equals(gtbl4.getUTM()));
		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UTMCoordinate utm = gps.getUTM();
		assertTrue(gpsCorrect(gps, utm.getGPS()));
	}
	
	private boolean gpsClose(GPSCoordinate gps1, GPSCoordinate gps2){
		if((int) gps1.getLatitude() == (int)gps2.getLatitude() &&
				(int)gps1.getLongitude() == (int)gps2.getLongitude()){
			return true;
		}
		else return false;
	}
	
	private boolean gpsCorrect(GPSCoordinate gps1, GPSCoordinate gps2){
		System.out.println(gps1.toString());
		System.out.println(gps2.toString());
		if((int) gps1.getLatitude() == (int)gps2.getLatitude() &&
				(int)gps1.getLongitude() == (int)gps2.getLongitude()){
			return true;
		}
		else return false;
	}

}
