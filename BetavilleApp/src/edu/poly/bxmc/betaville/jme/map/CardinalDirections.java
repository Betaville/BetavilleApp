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

import com.jme.math.Vector3f;

/**
 * @author Skye Book
 *
 */
public class CardinalDirections {
	public static final Vector3f NORTH = new Vector3f(1,0,0);
	public static final Vector3f SOUTH = new Vector3f(-1,0,0);
	public static final Vector3f EAST = new Vector3f(0,0,1);
	public static final Vector3f WEST = new Vector3f(0,0,-1);
	
	
	public static final Vector3f NW = new Vector3f(.75f,0,-.75f);
	public static final Vector3f NE = new Vector3f(.75f,0,.75f);
	public static final Vector3f SW = new Vector3f(-.75f,0,-.75f);
	public static final Vector3f SE = new Vector3f(-.75f,0,.75f);
	
	/**
	 * Calculates a vector's direction in degrees.
	 * @param direction The directional vector to interpret
	 * @return The degrees, between 0 and 360
	 */
	public static float getDirectionInDegrees(Vector3f direction){
		// Update Compass
		float x = direction.getX();
		float z = direction.getZ();
		float angle=-1;
		
		/*              x=1
		 *              |
		 *       270-360|0-90
		 *z=-1 <----------------> z=1
		 *       180-270|90-180
		 *              |
		 *              x=-1
		 */
		
		if(x>0){
			// 270 - 90
			if(z>0){
				// 0-90
				angle = 90*z;
			}
			else if(z<0){
				// 270-360
				angle = 270+(90*x);
			}
			else angle = 0;
		}
		else if(x<0){
			// 90 - 270
			if(z>0){
				// 90-180
				angle = 90+(90*z);
			}
			else if(z<0){
				// 180-270
				angle = 180+(90*x);
			}
			else angle = 180;
		}
		else{
			if(z==1) angle=90;
			else if(z==-1) angle=180;
		}
		
		return angle;
	}
}
