/**
 * 
 */
package bvtest.map;

import java.math.BigDecimal;

import edu.poly.bxmc.betaville.util.Math;

/**
 * @author Skye Book
 *
 */
public class FloatingTest {
	
	static boolean doBigTest=false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		if(doBigTest){
			// 584177.0	4506457.0
			int easting = 584177;
			short eastingCentimeters = 9;
			int northing = 4506457;
			short northingCentimeters = 2;

			int eastingDeltaMeters = 0;
			int northingDeltaMeters = 0;
			int eastingDeltaCentimeters = 3;
			int northingDeltaCentimeters = -1;

			// go to floats for current data
			double currentEasting = (double)easting+((double) eastingCentimeters/100d);
			double currentNorthing = (double)northing+((double) northingCentimeters/100d);

			System.out.println("Current easting/northing:\t" + currentEasting+"\t"+currentNorthing);

			// go to floats to changes
			double cmEastingChange = ((double) eastingDeltaCentimeters/100d);
			double cmNorthingChange = ((double) northingDeltaCentimeters/100d);
			System.out.println("Centimeter easting/northing:\t"+cmEastingChange+"\t"+cmNorthingChange);

			// add the whole meters
			currentEasting=currentEasting+eastingDeltaMeters;
			currentNorthing=currentNorthing+northingDeltaMeters;

			// add the centimeters
			currentEasting=currentEasting+cmEastingChange;
			currentNorthing=currentNorthing+cmNorthingChange;

			System.out.println("Current easting/northing:\t" + currentEasting+"\t"+currentNorthing);

			/* Return from whence you came
			 * ^ Arrested Development quote, if you didn't recognize this line
			 * stop immediately and go watch all three seasons of the series. 
			 */
			int[] eastingParts = Math.splitFraction(currentEasting);
			int[] northingParts = Math.splitFraction(currentNorthing);
			easting=eastingParts[0];
			eastingCentimeters=(short)eastingParts[1];
			northing=northingParts[0];
			northingCentimeters=(short)northingParts[1];

			System.out.println(easting+"\t"+eastingCentimeters+"\t"+northing+"\t"+northingCentimeters);
		}
		else{
			BigDecimal one = new BigDecimal(4506048f);
			BigDecimal result = one.add(new BigDecimal(.2f));
			System.out.println(((float)result.doubleValue()));
		}
	}

}
