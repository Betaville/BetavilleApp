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
package edu.poly.bxmc.betaville.bookmarks;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.util.Crypto;

/**
 * @author Skye Book
 *
 */
public class Bookmark{
	
	private String name;
	private String bookmarkID;
	private String user;
	private long createdOn;
	private String description;
	
	// location of the camera
	private ILocation location;
	
	// direction of the camera
	private float directionX;
	private float directionY;
	private float directionZ;

	public Bookmark(String name, String bookmarkID, String user, long createdOn, String description, ILocation location, float directionX, float directionY, float directionZ) {
		this.name=name;
		this.bookmarkID=bookmarkID;
		this.user=user;
		this.createdOn=createdOn;
		this.description=description;
		this.location=location;
		this.directionX=directionX;
		this.directionY=directionY;
		this.directionZ=directionZ;
	}
	
	public Bookmark(String name, String description, ILocation location, float directionX, float directionY, float directionZ) {
		this(name, Crypto.createUniqueIDFromTime(System.currentTimeMillis()), SettingsPreferences.getUser(), System.currentTimeMillis(), description, location, directionX, directionY, directionZ);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the location
	 */
	public ILocation getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(ILocation location) {
		this.location = location;
	}

	/**
	 * @return the directionX
	 */
	public float getDirectionX() {
		return directionX;
	}

	/**
	 * @param directionX the directionX to set
	 */
	public void setDirectionX(float directionX) {
		this.directionX = directionX;
	}

	/**
	 * @return the directionY
	 */
	public float getDirectionY() {
		return directionY;
	}

	/**
	 * @param directionY the directionY to set
	 */
	public void setDirectionY(float directionY) {
		this.directionY = directionY;
	}

	/**
	 * @return the directionZ
	 */
	public float getDirectionZ() {
		return directionZ;
	}

	/**
	 * @param directionZ the directionZ to set
	 */
	public void setDirectionZ(float directionZ) {
		this.directionZ = directionZ;
	}

	/**
	 * @return the createdOn
	 */
	public long getCreatedOn() {
		return createdOn;
	}

	/**
	 * @return the bookmarkID
	 */
	public String getBookmarkID() {
		return bookmarkID;
	}
}
