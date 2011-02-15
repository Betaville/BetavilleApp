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
package edu.poly.bxmc.betaville.jme.map;

import com.jme.scene.measure.Foot;
import com.jme.scene.measure.LengthUnit;
import com.jme.scene.measure.Meter;
import com.jme.scene.measure.LengthUnit.DistanceUnits;

import edu.poly.bxmc.betaville.SceneScape;

/**
 * @author Skye Book
 *
 */
public class Scale {
	/**
	 * Converts from real world meters to scaled meters in Betaville
	 * @param meters distance, in meters, to scale to Betaville
	 * @return scaled number of meters
	 */
	public static float fromMeter(float meters){
		return meters/SceneScape.SceneScale;
	}
	
	/**
	 * Converts from scaled Betaville meters to real world distances
	 * @param floatUnits distance in the 3D scene
	 * @return number of real world meters
	 */
	public static float toMeter(float floatUnits){
		return floatUnits*SceneScape.SceneScale;
	}
	
	/**
	 * Converts from real world meters to scaled feet in Betaville
	 * @param feet distance, in feet, to scale to Betaville
	 * @return scaled number of feet
	 */
	public static float fromFoot(float feet){
		Foot f = new Foot(feet);
		return f.convertToFloat()/SceneScape.SceneScale;
	}
	
	/**
	 * Converts from scaled Betaville meters to real world distances
	 * @param floatUnits distance in the 3D scene
	 * @return number of real world feet
	 */
	public static float toFoot(float floatUnits){
		LengthUnit l = new Meter(floatUnits*SceneScape.SceneScale);
		return l.convert(DistanceUnits.FOOT).convertToFloat();
	}
}
