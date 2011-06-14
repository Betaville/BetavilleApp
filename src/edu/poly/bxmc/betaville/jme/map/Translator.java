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

import com.jme.scene.Spatial;

/**
 * Provides utility methods for moving objects around the scene
 * @author Skye Book
 *
 */
public class Translator {

	public  static void moveX(Spatial toMove, float meters){
		toMove.setLocalTranslation(toMove.getLocalTranslation().getX()+(Scale.fromMeter(meters)),
				toMove.getLocalTranslation().getY(),
				toMove.getLocalTranslation().getZ());
	}
	
	public  static void moveY(Spatial toMove, float meters){
		toMove.setLocalTranslation(toMove.getLocalTranslation().getX(),
				toMove.getLocalTranslation().getY()+(Scale.fromMeter(meters)),
				toMove.getLocalTranslation().getZ());
	}
	
	public  static void moveZ(Spatial toMove, float meters){
		toMove.setLocalTranslation(toMove.getLocalTranslation().getX(),
				toMove.getLocalTranslation().getY(),
				toMove.getLocalTranslation().getZ()+(Scale.fromMeter(meters)));
	}
	
	/**
	 * Moves an object north.  (Can also use {@link #moveX(Spatial, float)}_
	 * @param toMove The object to move
	 * @param meters The amount of meters to move the object in this direction
	 */
	public static void moveNorth(Spatial toMove, float meters){
		moveX(toMove, meters);
	}
	
	/**
	 * Moves an object south.  (Can also use {@link #moveX(Spatial, float)}_
	 * @param toMove The object to move
	 * @param meters The amount of meters to move the object in this direction
	 */
	public static void moveSouth(Spatial toMove, float meters){
		moveX(toMove, -meters);
	}
	
	/**
	 * Moves an object east.  (Can also use {@link #moveZ(Spatial, float)})
	 * @param toMove The object to move
	 * @param meters The amount of meters to move the object in this direction
	 */
	public static void moveEast(Spatial toMove, float meters){
		moveZ(toMove, meters);
	}
	
	/**
	 * Moves an object west.  (Can also use {@link #moveZ(Spatial, float)})
	 * @param toMove The object to move
	 * @param meters The amount of meters to move the object in this direction
	 */
	public static void moveWest(Spatial toMove, float meters){
		moveZ(toMove, -meters);
	}
	
	/**
	 * Increases an object's altitude.  (Can also use {@link #moveY(Spatial, float)})
	 * @param toMove The object to move
	 * @param meters The amount of meters to move the object in this direction
	 */
	public static void moveUp(Spatial toMove, float meters){
		moveY(toMove, meters);
	}
	
	/**
	 * Decreases an object's altitude.  (Can also use {@link #moveY(Spatial, float)})
	 * @param toMove The object to move
	 * @param meters The amount of meters to move the object in this direction
	 */
	public static void moveDown(Spatial toMove, float meters){
		moveY(toMove, -meters);
	}
	
}
