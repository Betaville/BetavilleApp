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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Class <City> - Represents a city in the world
 * 
 * @author Caroline Bouchat
 * @author Skye Book (Minor Additions)
 * @version 0.1 - Spring 2009
 */
public class City implements ICity{
	private static final long serialVersionUID = 1L;

	/**
	 * Attribute <city> - City
	 */
	private String city;

	/**
	 * Attribute <state> - The state in which the city is located.
	 */
	private String state;
	
	/**
	 * Attribute <country> - The country in which the city is located.
	 */
	private String country;
	
	private List<Design> designs;
	
	private int cityID;

	/**
	 * Constructor - Create a new city. <City>
	 * 
	 * @param city
	 *            City name
	 * @param country
	 *            Country name
	 * @param state
	 *            State name
	 */
	public City(String city, String state, String country) {
		this.city = city;
		this.country = country;
		designs  = new Vector<Design>();
		this.state = state;
	}
	
	/**
	 * Constructor - Create a new city. <City>
	 * 
	 * @param city
	 *            City name
	 * @param country
	 *            Country name
	 * @param state
	 *            State name
	 * @param cityID
	 *            City ID
	 */
	public City(String city, String state, String country, int cityID) {
		this.city = city;
		this.country = country;
		designs  = new Vector<Design>();
		this.state = state;
		this.cityID=cityID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.poly.bxmc.betaville.model.ICity#getCity()
	 */
	public String getCity() {
		return city;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.poly.bxmc.betaville.model.ICity#getCountry()
	 */
	public String getCountry() {
		return country;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.poly.bxmc.betaville.model.ICity#getState()
	 */
	public String getState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.poly.bxmc.betaville.model.ICity#getNumberDesigns()
	 */
	public int getNumberDesigns() {
		return designs.size();
	}

	public int getCityID() {
		return cityID;
	}
	
	public void setCityID(int id) {
		this.cityID = id;
	}
	/*
	 * Adding getters and setters to accept requests by JsonClientManager
	 */
	public void setCityName(String name) {
		this.city = name;
	}
	
	public void setCityState(String state) {
		this.state = state;
	}
	
	public void setCityCountry(String country) {
		this.country = country;
	}

	public synchronized Design findDesignByFullIdentifier(String name) {
		for(Iterator<Design> designIter=designs.iterator(); designIter.hasNext();)
		{
			Design currentDesign = designIter.next();
			if(name.equals(currentDesign.getFullIdentifier())){
				return currentDesign;
			}
		}
		return null;
	}
	
	public synchronized Design findDesignByName(String name){
		Iterator<Design> it = designs.iterator();
		while(it.hasNext()){
			Design design = it.next();
			if(design.getName().equals(name)){
				return design;
			}
		}
		return null;
	}
	
	public synchronized Design findDesignByID(int designID){
		Iterator<Design> it = designs.iterator();
		while(it.hasNext()){
			Design design = it.next();
			if(design.getID()==designID){
				return design;
			}
		}
		return null;
	}
	
	public synchronized void removeDesignWithID(int designID){
		removeDesign(findDesignByID(designID));
	}
	
	public List<Design> getDesigns(){
		return designs;
	}
	
	public synchronized void addDesign(Design design){
		designs.add(design);
	}
	
	public synchronized void removeDesign(Design design){
		designs.remove(design);
	}
	
	public synchronized void swapDesigns(Design old, Design newDesign){
		designs.remove(old);
		designs.add(newDesign);
	}
	
	public synchronized void sortMaintain(){
		Collections.sort(designs, Design.COMPARE_BY_ID);
	}
}
