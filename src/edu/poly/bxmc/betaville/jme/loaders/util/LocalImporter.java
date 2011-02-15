/** Copyright (c) 2008-2010, Brooklyn eXperimental Media Center
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
package edu.poly.bxmc.betaville.jme.loaders.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;

/**
 * Imports a design file that was previously saved.
 * @author Skye Book
 *
 */
public class LocalImporter{
	
	public static Design importDesign(File f) throws FileNotFoundException{
		Scanner scanner = new Scanner(f);
		scanner.useDelimiter("\n");
		String name = scanner.next();
		String cityID = scanner.next();
		String filepath = scanner.next();
		String address = scanner.next();
		String coordinateString = scanner.next();
		createUTM(coordinateString);
		String description = scanner.next();
		String url = scanner.next();
		scanner.close();
		System.out.println("name: " + name);
		return null;
	}
	
	private static UTMCoordinate createUTM(String coordinateString){
		int lonZone = Integer.parseInt(coordinateString.substring(0, coordinateString.indexOf(" ")));
		coordinateString = coordinateString.substring(coordinateString.indexOf(" ")+1);
		System.out.println(lonZone);
		
		
		char latZone = coordinateString.substring(0,1).toCharArray()[0];
		coordinateString = coordinateString.substring(2);
		System.out.println(latZone);
		
		int easting = Integer.parseInt(coordinateString.substring(0, coordinateString.indexOf(" ")));
		coordinateString = coordinateString.substring(coordinateString.indexOf(" ")+1);
		System.out.println(easting);
		
		
		int northing = Integer.parseInt(coordinateString.substring(0, coordinateString.indexOf("\r")));
		System.out.println(northing);
		return new UTMCoordinate(easting, northing, lonZone, latZone, 0);
	}
	
	public static void main(String[] args){
		try {
			importDesign(new File(DriveFinder.getHomeDir().toString()+"/.betaville/test.design"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
