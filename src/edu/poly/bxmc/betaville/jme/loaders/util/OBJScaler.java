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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * <code>OBJScaler</code> contains utility methods for manipulating obj imports
 * @author Skye Book
 *
 */
public class OBJScaler {
	private static Logger logger = Logger.getLogger(OBJScaler.class);
	
	/**
	 * Accounts for OBJ's inability to deal with units of measure.  (currently works with Maya & 3ds Max)
	 * @param modelURL - OBJ file to read the unit of measure data from.
	 * @throws IOException
	 */
	public static float fixScale(URL modelURL) throws IOException{
		float objScale = 1;
		BufferedReader objReader = new BufferedReader(new InputStreamReader(modelURL.openStream()));
		String firstLine = objReader.readLine();
		if(firstLine==null) return objScale;
		
		// This is the way that Maya files are created 
		if(firstLine.startsWith("# This file uses")){
			if(firstLine.startsWith("# This file uses millimeters")){
				logger.info("Maya OBJ was created in millimeters");
				objScale = .001f;
			}
			if(firstLine.startsWith("# This file uses centimeters")){
				logger.info("Maya OBJ was created in centimeters");
				objScale = .01f;
			}
			else if(firstLine.startsWith("# This file uses meters")){
				logger.info("Maya OBJ was created in meters");
				objScale = 1;
			}
			else if(firstLine.startsWith("# This file uses inches")){
				logger.info("Maya OBJ was created in inches");
				objScale = .0254f;
			}
			else if(firstLine.startsWith("# This file uses feet")){
				logger.info("Maya OBJ was created in feet");
				objScale = .3048f;
			}
			else if(firstLine.startsWith("# This file uses yards")){
				logger.info("Maya OBJ was created in yards");
				objScale = .9144f;
			}
			
			// force centimeters for maya
			objScale = .01f;
			
			objReader.close();
		}
		
		logger.info("OBJ Scale set to " + objScale);
		return objScale;
	}
}
