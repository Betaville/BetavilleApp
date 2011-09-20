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

import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.sound.SoundStyle;

/**
 * An AudibleDesign represents a sound to be experienced at a
 * certain place.  The sound may be heard from a distance, or
 * at a different volume, etc.
 * @author Skye Book
 *
 */
public class AudibleDesign extends Design{
	private static final long serialVersionUID = 3017L;
	
	private float directionX;
	private float directionY;
	private float directionZ;
	private int volume;
	
	private SoundStyle soundStyle;
	
	public AudibleDesign(){}
	
	public AudibleDesign(String name, UTMCoordinate coordinate, String address,
			int cityID, String user, String description, String filepath,
			String url, boolean isVisible, float directionX, float directionY, float directionZ, int volume, SoundStyle soundStyle) {
		super(name, coordinate, address, cityID, user, description, filepath,
				url, isVisible);
		this.directionX=directionX;
		this.directionY=directionY;
		this.directionZ=directionZ;
		this.volume=volume;
		this.soundStyle=soundStyle;
	}
	
	public AudibleDesign(String name, UTMCoordinate coordinate,
			int cityID, String user, String description, String filepath,
			String url, boolean isVisible, float directionX, float directionY, float directionZ, int volume, SoundStyle soundStyle) {
		this(name, coordinate, "None", cityID, user, description, filepath, url, isVisible, 0, 0, 0, 100, soundStyle);
	}
	
	public int getVolume(){
		return volume;
	}
	
	public void setVolume(int volume){
		this.volume=volume;
	}
	
	public float getDirectionX() {
		return directionX;
	}

	/**
	 * Sets the direction in the X vector
	 * @param directionX the value (which clamps to 1)
	 */
	public void setDirectionX(float directionX) {
		if(directionX>1){
			this.directionX=1;
		}
		else this.directionX=directionX;
	}

	public float getDirectionY() {
		return directionY;
	}
	
	public SoundStyle getSoundStyle(){
		return soundStyle;
	}

	/**
	 * Sets the direction in the Y vector
	 * @param directionY the value (which clamps to 1)
	 */
	public void setDirectionY(float directionY) {
		if(directionY>1){
			this.directionY=1;
		}
		else this.directionY=directionY;
	}

	public float getDirectionZ() {
		return directionZ;
	}

	/**
	 * Sets the direction in the Z vector
	 * @param directionZ the value (which clamps to 1)
	 */
	public void setDirectionZ(float directionZ) {
		if(directionZ>1){
			this.directionZ=1;
		}
		else this.directionZ=directionZ;
	}
	
	/**
	 * Performs a shallow copy of {@link AudibleDesign} data
	 * into an object
	 * @param design
	 * @see Design#load(Design)
	 */
	public void load(AudibleDesign design){
		super.load(design);
		directionX = design.directionX;
		directionY = design.directionY;
		directionZ = design.directionZ;
		volume = design.volume;
		soundStyle = design.soundStyle;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Float.floatToIntBits(directionX);
		result = prime * result + Float.floatToIntBits(directionY);
		result = prime * result + Float.floatToIntBits(directionZ);
		result = prime * result + volume;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AudibleDesign other = (AudibleDesign) obj;
		if (Float.floatToIntBits(directionX) != Float
				.floatToIntBits(other.directionX))
			return false;
		if (Float.floatToIntBits(directionY) != Float
				.floatToIntBits(other.directionY))
			return false;
		if (Float.floatToIntBits(directionZ) != Float
				.floatToIntBits(other.directionZ))
			return false;
		if (volume != other.volume)
			return false;
		return true;
	}
}
