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
package edu.poly.bxmc.betaville.model;

import com.jme.math.Vector2f;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;

/**
 * A SketchedDesign represents a 2D design meant to be seen as
 * an overlay on the ground, either as a precursor to a later
 * proposal or as a final product
 * @author Skye Book
 *
 */
public class SketchedDesign extends Design{
	private static final long serialVersionUID = 1L;
	private Vector2f dimensions;
	private char upPlane;
	private int rotation;
	
	public SketchedDesign(){}
	
	public SketchedDesign(String name, UTMCoordinate coordinate, String address,
			int cityID, String user, String description, String filepath, String url, boolean isVisible, int rotation, char upPlane){
		super(name, coordinate, address, cityID, user, description, filepath, url, isVisible);
	}

	public char getUpPlane() {
		return upPlane;
	}

	public void setUpPlane(char upPlane) {
		this.upPlane = upPlane;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getLength(){
		return (int)(dimensions.getX()*SceneScape.SceneScale);
	}
	
	public int getWidth(){
		return (int)(dimensions.getY()*SceneScape.SceneScale);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((dimensions == null) ? 0 : dimensions.hashCode());
		result = prime * result + rotation;
		result = prime * result + upPlane;
		return result;
	}
	
	/**
	 * Performs a shallow copy of {@link SketchedDesign} data
	 * into an object
	 * @param design
	 * @see Design#load(Design)
	 */
	public void load(SketchedDesign design){
		super.load(design);
		dimensions = design.dimensions;
		upPlane = design.upPlane;
		rotation = design.rotation;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SketchedDesign other = (SketchedDesign) obj;
		if (dimensions == null) {
			if (other.dimensions != null)
				return false;
		} else if (!dimensions.equals(other.dimensions))
			return false;
		if (rotation != other.rotation)
			return false;
		if (upPlane != other.upPlane)
			return false;
		return true;
	}
}
