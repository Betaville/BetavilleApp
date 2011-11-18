package geotools;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.geotools.nature.SunRelativePosition;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.jme.map.CardinalDirections;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.Rotator;

public class SunPosition {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GPSCoordinate coordinate;

		Vector3f sunAngle = new Vector3f();

		SunRelativePosition sunRelativePosition = new SunRelativePosition(Double.NaN);
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(2011, 11, 17, 7, 23);

		Date dateTime = calendar.getTime();
		
		coordinate = new GPSCoordinate(0, 40, -74);
		
		
		sunRelativePosition.setDate(dateTime);
		sunRelativePosition.setCoordinate(coordinate.getLongitude(), coordinate.getLatitude());
		double azimuth = sunRelativePosition.getAzimuth();
		double elevation = sunRelativePosition.getElevation();

		Quaternion a = Rotator.angleY(((360f)-(float)azimuth));
		Quaternion e = Rotator.angleZ((float)elevation);
		Quaternion fin = a.mult(e);
		
		fin.mult(CardinalDirections.NORTH, sunAngle);
		
		System.out.println("Sun Azimuth: " + azimuth);
		System.out.println("Sun Elevation: " + elevation);
		System.out.println("Sun Angle: " + sunAngle.toString());
	}

}
