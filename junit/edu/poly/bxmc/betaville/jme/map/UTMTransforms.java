/**
 * 
 */
package edu.poly.bxmc.betaville.jme.map;

import junit.framework.TestCase;

/**
 * @author Skye Book
 *
 */
public class UTMTransforms extends TestCase {
	
	public void testNorthingSubtraction(){
		UTMCoordinate utm = new UTMCoordinate(550000, 4500000, 18, 'T', 0);
		utm.move(0, 0, 0, -105, 0);
		System.out.println(utm.toString());
		assertTrue(!utm.toString().contains(".-"));
	}
	
	public void testEastingSubtraction(){
		UTMCoordinate utm = new UTMCoordinate(550000, 4500000, 18, 'T', 0);
		utm.move(0, 0, -105, 0, 0);
		System.out.println(utm.toString());
		assertTrue(!utm.toString().contains(".-"));
	}
	
}
