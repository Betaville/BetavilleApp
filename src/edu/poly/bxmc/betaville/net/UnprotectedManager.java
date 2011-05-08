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
package edu.poly.bxmc.betaville.net;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.City;
import edu.poly.bxmc.betaville.model.Comment;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ProposalPermission;
import edu.poly.bxmc.betaville.model.Wormhole;
import edu.poly.bxmc.betaville.model.IUser.UserType;

/**
 * Outline of all unauthenticated network functionality in Betaville
 * @author Skye Book
 *
 */
public interface UnprotectedManager {

	/**
	 * @return the busy
	 */
	public abstract boolean isBusy();

	/**
	 * Close the socket
	 */
	public abstract void close();

	/**
	 * Tells whether or not the socket is open
	 * @return The state of the socket.  True if open and false if closed.
	 */
	public abstract boolean isAlive();

	/**
	 * Checks whether or not a user already exists with a certain name.
	 * @param name The name to check the availability of.
	 * @return True if this name is <em>not</em> taken, false if it is
	 * already registered
	 */
	public abstract boolean checkNameAvailability(String name);

	/**
	 * Retrieves the email address of a user.
	 * @param user The user in question.
	 * @return The user's email address.
	 */
	public abstract String getUserEmail(String user);

	/**
	 * Gets the <code>Design</code> based on its ID.
	 * @param designID The ID of the design being searched for.
	 * @return A <code>Design</code> object.
	 * @see Design
	 */
	public abstract Design findDesignByID(int designID);

	/**
	 * Gets the designs based on the user they were built by.
	 * @param name The name of the user whose designs are being
	 * searched for.
	 * for were created.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Design> findDesignsByName(String name);

	/**
	 * Gets the designs based on when they were built.
	 * @param user The user who created the designs
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Design> findDesignsByUser(String user);

	/**
	 * Gets the designs based on when they were built.
	 * @param date The date of when the designs being searched
	 * for were created.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Design> findDesignsByDate(Date date);

	/**
	 * Gets all of the designs located in a city.
	 * @param cityID The ID of the city that we are looking in.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	public abstract List<Design> findAllDesignsByCity(int cityID);
	
	public abstract List<Design> findDesignsByCitySetStartEnd(int cityID, boolean onlyBase, int start, int end);

	/**
	 * Gets only the base designs located in a city.
	 * @param cityID The ID of the city that we are looking in.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	public abstract List<Design> findBaseDesignsByCity(int cityID);

	/**
	 * Gets the designs located in a city.
	 * @param cityID The ID of the city that we are looking in.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Design> findTerrainByCity(int cityID);

	public abstract int[] findVersionsOfProposal(int proposalDesignID);

	public abstract ProposalPermission getProposalPermissions(int designID);

	/**
	 * Finds all proposals related to a design
	 * @param source The ID of the design for which to find all proposals
	 * @return ID of all proposals related to this source, requires null checking.
	 * @see #findDesignByID(int)
	 */
	public abstract int[] findAllProposals(int designID);

	/**
	 * Gets the proposals located in a specific area
	 * @param coordinate
	 * @param meterRadius
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Design> findAllProposalsNearLocation(
			UTMCoordinate coordinate, int meterRadius);

	public abstract PhysicalFileTransporter requestThumbnail(int designID);

	/**
	 * Requests a file existing on the server.
	 * @param design The design to request from the server.
	 * @return a <code>PhysicalFileTransporter</code> object that can be used to
	 * interact with data by writing to disk or using internally.
	 * @see Design
	 * @see PhysicalFileTransporter
	 */
	public abstract PhysicalFileTransporter requestFile(Design design);

	/**
	 * Requests a file existing on the server.
	 * @param designID The ID of the design to request from the server.
	 * @return a <code>PhysicalFileTransporter</code> object that can be used to
	 * interact with data by writing to disk or using internally.
	 * @see Design
	 * @see PhysicalFileTransporter
	 */
	public abstract PhysicalFileTransporter requestFile(int designID);

	/**
	 * Adds a city to the database.
	 * @param name The name of the city.
	 * @param state The state that the city is in.
	 * @param country The country that the city is in.
	 * @return The ID of this new city.
	 */
	public abstract int addCity(String name, String state, String country);

	@SuppressWarnings("unchecked")
	public abstract List<City> findAllCities();

	/**
	 * Finds cities by the name of the city.
	 * @param country The name of the city.
	 * @return A Vector of Integers which reference the ID's of the cities
	 * that were found.
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Integer> findCitiesByName(String name);

	/**
	 * Finds cities by the name of the state.
	 * @param country The name of the state.
	 * @return A Vector of Integers which reference the ID's of the cities
	 * that were found.
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Integer> findCitiesByState(String state);

	/**
	 * Finds cities by the name of the country.
	 * @param country The name of the country.
	 * @return A Vector of Integers which reference the ID's of the cities
	 * that were found.
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Integer> findCitiesByCountry(String country);

	/**
	 * Finds a city by its ID.
	 * <br><br>
	 * Example:
	 * <pre>String[] cityElements = findCityByID(56);
	 * if(cityElements!=null){
	 *      City weirdCity = new City(cityElements[0], cityElements[1], cityElements[2]);
	 * }</pre>
	 * @param cityID The ID of the city.
	 * @return A String array which can be read to form a <code>City</code>.
	 * object.
	 * @see City
	 */
	public abstract String[] findCityByID(int cityID);

	/**
	 * Finds a city by all of its parameters.
	 * <br><br>
	 * Example:
	 * <pre>String[] cityElements = findCityByAll("MyCity","SomeState","ThatCountry");
	 * if(cityElements!=null){
	 *      City weirdCity = new City(cityElements[0], cityElements[1], cityElements[2]);
	 * }</pre>
	 * @param name The name of the city.
	 * @param state The state the city is located in.
	 * @param country The country the city is located in.
	 * @return A String array which can be read to form a <code>City</code>.
	 * object.
	 * @see City
	 */
	public abstract String[] findCityByAll(String name, String state,
			String country);

	/**
	 * Reports a comment as spam on the server.
	 * @param commentID The ID of the comment to report.
	 */
	public abstract void reportSpamComment(int commentID);

	/**
	 * Retrieve the comments from the server.
	 * @param designID The ID of the design to retrieve the comments for.
	 * @return A <code>Vector</code> of Comments
	 * @see Comment
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Comment> getComments(int designID);

	/**
	 * Checks if a user is of a certain type.
	 * @param user
	 * @param userType
	 * @return 1 for yes, 0 for no, negative numbers are errors
	 */
	public abstract int checkUserLevel(String user, UserType userType);

	/**
	 * Checks the current version of the design class that the server
	 * is running
	 * @return The version of the design of -2 for an error
	 */
	public abstract long getDesignVersion();

	/**
	 * Gets the level of a user
	 * @param user The user's name
	 * @return The type of user
	 */
	public abstract UserType getUserLevel(String user);

	@SuppressWarnings("unchecked")
	public abstract List<Wormhole> getWormholesWithin(
			UTMCoordinate location, int extentNorth, int extentEast);

	/**
	 * Retrieves <strong>all</strong> wormholes from the database
	 * @return A list of {@link Wormhole} objects
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Wormhole> getAllWormholes();

	/**
	 * Gets all of the wormholes located in a city (this refers to the destination city, since you
	 * can jump from anywhere)
	 * @param cityID The ID of the city in which to search for wormholes
	 * @return A list of {@link Wormhole} objects
	 */
	@SuppressWarnings("unchecked")
	public abstract List<Wormhole> getAllWormholesInCity(int cityID);

	@SuppressWarnings("unchecked")
	public abstract List<Design> synchronizeData(
			HashMap<Integer, Integer> hashMap);

}