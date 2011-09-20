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

/**
 * Signifies an empty place holder used when proposals
 * are to be created on empty space (or otherwise without
 * any connection/link to the other designs in the area).
 * @author Skye Book
 *
 */
public class EmptyDesign extends Design {
	private static final long serialVersionUID = 1L;
	private int length;
	private int width;
	
	public EmptyDesign(){}

	/**
	 * @param name
	 * @param coordinate
	 * @param address
	 * @param user
	 * @param description
	 * @param filepath
	 * @param url
	 * @param isVisible
	 */
	public EmptyDesign(UTMCoordinate coordinate, String address,
			String user, String description, String url,
			boolean isVisible, int length, int width) {
		super("EMPTY_DESIGN", coordinate, address, user, description, "EMPTY", url,
				isVisible);
		this.length=length;
		this.width=width;
	}

	/**
	 * @param name
	 * @param coordinate
	 * @param address
	 * @param cityID
	 * @param user
	 * @param description
	 * @param filepath
	 * @param url
	 * @param isVisible
	 */
	public EmptyDesign(UTMCoordinate coordinate, String address,
			int cityID, String user, String description,
			String url, boolean isVisible, int length, int width) {
		super("EMPTY_DESIGN", coordinate, address, cityID, user, description, "EMPTY",
				url, isVisible);
		this.length=length;
		this.width=width;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * Performs a shallow copy of {@link EmptyDesign} data
	 * into an object
	 * @param design
	 * @see Design#load(Design)
	 */
	public void load(EmptyDesign design){
		super.load(design);
		length = design.length;
		width = design.width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + length;
		result = prime * result + width;
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
		EmptyDesign other = (EmptyDesign) obj;
		if (length != other.length)
			return false;
		if (width != other.width)
			return false;
		return true;
	}
}
