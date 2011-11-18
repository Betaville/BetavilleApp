/** Copyright (c) 2008-2011, Brooklyn eXperimental Media Center
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

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * <code>Rotator</code> provides common Quaternions to perform
 * rotations as well as methods to find less popular Quaternion
 * calculations.
 * 
 * NOTES:<br/>
 * Yaw = Rotations about Y
 * Pitch = Rotations about Z
 * Roll = Rotations about X
 * @author Skye Book
 *
 */
public class Rotator {
	public static final Quaternion ROLL045  = new Quaternion().fromAngleAxis(FastMath.PI/4,   new Vector3f(0,0,1));
	public static final Quaternion ROLL090  = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(0,0,1));
	public static final Quaternion ROLL180  = new Quaternion().fromAngleAxis(FastMath.PI  ,   new Vector3f(0,0,1));
	public static final Quaternion ROLL270  = new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(0,0,1));
	public static final Quaternion YAW045n  = new Quaternion().fromAngleAxis(- FastMath.PI/4, new Vector3f(0,1,0));
	public static final Quaternion YAW045   = new Quaternion().fromAngleAxis(FastMath.PI/4,   new Vector3f(0,1,0));
	public static final Quaternion YAW090   = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(0,1,0));
	public static final Quaternion YAW180   = new Quaternion().fromAngleAxis(FastMath.PI  ,   new Vector3f(0,1,0));
	public static final Quaternion YAW270   = new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(0,1,0));
	public static final Quaternion PITCH045 = new Quaternion().fromAngleAxis(FastMath.PI/4,   new Vector3f(1,0,0));
	public static final Quaternion PITCH090 = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(1,0,0));
	public static final Quaternion PITCH180 = new Quaternion().fromAngleAxis(FastMath.PI  ,   new Vector3f(1,0,0));
	public static final Quaternion PITCH270 = new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(1,0,0));
	public static final Quaternion UPRIGHT  = new Quaternion().fromAngleAxis(- FastMath.PI/2, new Vector3f(1,0,0));

	/**
	 * Creates a <code>Quaternion</code> rotation from a supplied
	 * number of degrees around the X axis
	 * @param degrees to be converted to a Quaternion rotation.
	 * @return Quaternion calculation of the requested angle.
	 */
	public static Quaternion angleX(float degrees){
		return new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*degrees,  Vector3f.UNIT_X);
	}
	
	/**
	 * Creates a <code>Quaternion</code> rotation from a supplied
	 * number of degrees around the Y axis
	 * @param degrees to be converted to a Quaternion rotation.
	 * @return Quaternion calculation of the requested angle.
	 */
	public static Quaternion angleY(float degrees){
		return new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*degrees,  Vector3f.UNIT_Y);
	}
	
	/**
	 * Creates a <code>Quaternion</code> rotation from a supplied
	 * number of degrees around the Z axis
	 * @param degrees to be converted to a Quaternion rotation.
	 * @return Quaternion calculation of the requested angle.
	 */
	public static Quaternion angleZ(float degrees){
		return new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*degrees,  Vector3f.UNIT_Z);
	}
	
	public static Quaternion fromThreeAngles(float x, float y, float z){
		return new Quaternion().fromAngles(FastMath.DEG_TO_RAD*x, FastMath.DEG_TO_RAD*y, FastMath.DEG_TO_RAD*z);
	}
}
