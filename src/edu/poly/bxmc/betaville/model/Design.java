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
package edu.poly.bxmc.betaville.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;

/**
 * Designs are the basic building blocks of Betaville.  Data is
 * encapsulated within design objects which contain information
 * about the proposal as well as the media pertaining to it (and
 * what type of media it is, depending on which sub-class is used).
 * @author Skye Book
 *
 */
public class Design implements Serializable{
	// Let's *try* to keep this up to date!
	public static final long serialVersionUID = 3032;
	
	/**
	 * Classifies the purpose served by this Design.  There is an order of inheritance,
	 * meaning that, for example, a version's sourceID will reference a Design classified
	 * as a proposal,
	 * <p>
	 * order:</br>
	 * * Base</br>
	 * * Proposal</br>
	 * * Version</br>
	 * @author Skye Book
	 */
	public enum Classification{
		BASE, PROPOSAL, VERSION
	}
	
	public enum DuplicationFunction{
		/** This design is not a duplicate of another */
		NOT_DUPLICATED,
		/**
		 * This design was originally created as a duplicate of another
		 * but there is no guarantee that it stays the same as that which
		 * it was duplicated from.
		 */
		ONE_TIME_DUPLICATION,
		/**
		 * This design was originally created as a duplicate of another and will
		 * be maintained to stay the same as the original
		 */
		TRACKED_DUPLICATION
	}
	
	protected String name;
	protected Classification classification;
	protected DuplicationFunction duplicationFunction;
	protected int id=0;
	protected int sourceID=0;
	protected UTMCoordinate coordinate;
	protected String address;
	protected int cityID;
	protected String user;
	protected String description;
	protected String filepath;
	protected String url;
	protected String dateAdded;
	protected boolean isPublic;
	protected int[] designsToRemove = new int[]{};
	protected List<String> favedBy;
	protected ProposalPermission proposalPermission;
	
	/**
	 * Creates a new design.
	 * @param name
	 * @param coordinate
	 * @param address
	 * @param user
	 * @param description
	 * @param filepath
	 * @param url URL of a web link related to this design
	 * @param isVisible
	 */
	public Design(String name, UTMCoordinate coordinate, String address,
			String user, String description, String filepath, String url, boolean isVisible){
		this.name=name;
		this.coordinate=coordinate;
		this.address=address;
		this.description=description;
		this.filepath=filepath;
		this.url=url;
		this.isPublic=isVisible;
		this.user=user;
		favedBy = new ArrayList<String>();
	}
	
	/**
	 * Creates a new design.
	 * @param name
	 * @param coordinate
	 * @param address
	 * @param cityID
	 * @param user
	 * @param description
	 * @param filepath
	 * @param url URL of a web link related to this design
	 * @param isVisible
	 */
	public Design(String name, UTMCoordinate coordinate, String address, 
			int cityID, String user, String description, String filepath, String url, boolean isVisible){
		this(name, coordinate, address, user, description, filepath, url, isVisible);
		this.cityID=cityID;
	}
	
	/**
	 * Creates a new design.
	 * @param name
	 * @param coordinate
	 * @param address
	 * @param cityID
	 * @param user
	 * @param description
	 * @param filepath
	 * @param url URL of a web link related to this design
	 * @param isVisible
	 * @param favedBy Users who have faved this design
	 */
	public Design(String name, UTMCoordinate coordinate, String address, 
			int cityID, String user, String description, String filepath, String url, boolean isVisible, List<String> favedBy){
		this(name, coordinate, address, cityID, user, description, filepath, url, isVisible);
		this.favedBy=favedBy;
	}

	/**
	 * Gets the name of this design.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the ID of this design.
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Gets the full identifier of this design, which is comprised of:
	 * name+"$"+designID (or name+"$local" if the design is held locally)
	 */
	public String getFullIdentifier(){
		if(id==0){
			return name+"$local";
		}
		else return "$"+id;
	}
	
	/**
	 * Gets this design's location.
	 */
	public UTMCoordinate getCoordinate(){
		return coordinate;
	}
	
	/**
	 * Gets the street address of this design.
	 */
	public String getAddress(){
		return address;
	}
	
	/**
	 * Gets the ID of the city to which this design belongs.
	 */
	public int getCityID(){
		return cityID;
	}
	
	/**
	 * Gets the user who first proposed this design.
	 */
	public String getUser(){
		return user;
	}
	
	/**
	 * Gets the description of this design, which can include rationale
	 * as well as historical information.
	 * @return
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * Gets the filepath of this design's media file.
	 */
	public String getFilepath(){
		return filepath;
	}
	
	/**
	 * Gets a URL with more information about the design.
	 */
	public String getURL(){
		return url;
	}
	
	/**
	 * Tells us whether this design is published publicly or privately.
	 */
	public boolean isPublic(){
		return isPublic;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setCoordinate(UTMCoordinate coordinate) {
		this.coordinate = coordinate;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCityID(int cityID) {
		this.cityID = cityID;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public void setPublic(boolean isVisible) {
		this.isPublic = isVisible;
	}
	
	public int getSourceID() {
		return sourceID;
	}

	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}
	
	public void update(String name, String description, String address, String url){
		this.name=name;
		this.description=description;
		this.address=address;
		this.url=url;
	}
	
	public int[] getDesignsToRemove(){
		return designsToRemove;
	}
	
	public void setDesignsToRemove(List<Integer> removables){
		designsToRemove = new int[removables.size()];
		for(int i =0; i<removables.size(); i++){
			designsToRemove[i] = removables.get(i);
		}
	}
	
	public void setClassification(Classification c){
		classification=c;
	}
	
	public Classification getClassification(){
		return classification;
	}
	
	public boolean isClassification(Classification c){
		return classification.equals(c);
	}
	
	public boolean isBase(){
		return classification.equals(Classification.BASE);
	}
	
	public boolean isProposal(){
		return classification.equals(Classification.PROPOSAL);
	}
	
	public boolean isVersion(){
		return classification.equals(Classification.VERSION);
	}
	
	public List<String> getFavedBy() {
		return favedBy;
	}

	public void setFavedBy(List<String> favedBy) {
		this.favedBy = favedBy;
	}
	
	public ProposalPermission getProposalPermission() {
		return proposalPermission;
	}

	public void setProposalPermission(ProposalPermission proposalPermission) {
		this.proposalPermission = proposalPermission;
	}

	/**
	 * @return the duplicationFunction
	 */
	public DuplicationFunction getDuplicationFunction() {
		return duplicationFunction;
	}

	/**
	 * @param duplicationFunction the duplicationFunction to set
	 */
	public void setDuplicationFunction(DuplicationFunction duplicationFunction) {
		this.duplicationFunction = duplicationFunction;
	}

	public static Comparator<Design> COMPARE_BY_ID = new Comparator<Design>(){
		public int compare(Design o1, Design o2) {
			return ((Integer)o1.getID()).compareTo(o2.getID());
		}
	};

	public static Comparator<Design> distanceComparator(final UTMCoordinate location){
		return new Comparator<Design>(){
			UTMCoordinate place=location.clone();
			public int compare(Design o1, Design o2) {
				int dist1 = (int)MapManager.greatCircleDistanced(place.getGPS(), o1.getCoordinate().getGPS());
				int dist2 = (int)MapManager.greatCircleDistanced(place.getGPS(), o2.getCoordinate().getGPS());
				return ((Integer)dist1).compareTo(dist2);
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Design other = (Design) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (cityID != other.cityID)
			return false;
		if (classification != other.classification)
			return false;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals(other.coordinate))
			return false;
		if (dateAdded == null) {
			if (other.dateAdded != null)
				return false;
		} else if (!dateAdded.equals(other.dateAdded))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (!Arrays.equals(designsToRemove, other.designsToRemove))
			return false;
		if (duplicationFunction != other.duplicationFunction)
			return false;
		if (favedBy == null) {
			if (other.favedBy != null)
				return false;
		} else if (!favedBy.equals(other.favedBy))
			return false;
		if (filepath == null) {
			if (other.filepath != null)
				return false;
		} else if (!filepath.equals(other.filepath))
			return false;
		if (id != other.id)
			return false;
		if (isPublic != other.isPublic)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (proposalPermission == null) {
			if (other.proposalPermission != null)
				return false;
		} else if (!proposalPermission.equals(other.proposalPermission))
			return false;
		if (sourceID != other.sourceID)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + cityID;
		result = prime * result
				+ ((classification == null) ? 0 : classification.hashCode());
		result = prime * result
				+ ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result
				+ ((dateAdded == null) ? 0 : dateAdded.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + Arrays.hashCode(designsToRemove);
		result = prime
				* result
				+ ((duplicationFunction == null) ? 0 : duplicationFunction
						.hashCode());
		result = prime * result + ((favedBy == null) ? 0 : favedBy.hashCode());
		result = prime * result
				+ ((filepath == null) ? 0 : filepath.hashCode());
		result = prime * result + id;
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((proposalPermission == null) ? 0 : proposalPermission
						.hashCode());
		result = prime * result + sourceID;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	
	public String toString(){
		return Design.class.getName()+": " + name;
	}
}