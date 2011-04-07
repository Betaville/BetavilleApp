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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.CacheManager;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.City;
import edu.poly.bxmc.betaville.model.Comment;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ProposalPermission;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.model.Wormhole;

/**
 * @author Skye Book
 *
 */
public abstract class ClientManager {
	private static Logger logger = Logger.getLogger(ClientManager.class);
	/**
	 * Attribute <clientSocket> - Client socket
	 */
	protected Socket clientSocket;
	/**
	 * Attribute <input> - input of the socket
	 */
	protected ObjectInputStream input;
	/**
	 * Attribute <output> - output of the socket
	 */
	protected ObjectOutputStream output;

	protected List<Integer> modules;
	/**
	 * @return the busy
	 */
	public synchronized boolean isBusy() {
		return busy.get();
	}

	protected AtomicBoolean busy = new AtomicBoolean(false);
	
	/**
	 * Close the socket
	 */
	public void close() {
		try {
			output.writeObject(ConnectionCodes.CLOSE);
			//output.close();
			//clientSocket.close();
		} catch (IOException e) {
			logger.error("Network issue detected related to "+SettingsPreferences.getServerIP(), e);
		}
	}

	/**
	 * Tells whether or not the socket is open
	 * @return The state of the socket.  True if open and false if closed.
	 */
	public boolean isAlive(){
		return clientSocket.isConnected();
	}

	protected Object readResponse() throws UnexpectedServerResponse, IOException{
		try {
			Object obj = input.readObject();
			
			// check for errors
			
			return obj;
		} catch (ClassNotFoundException e) {
			logger.error("the server returned a bad class", e);
			throw new UnexpectedServerResponse("An unexpected class was encountered");
		}
	}

	/**
	 * Checks whether or not a user already exists with a certain name.
	 * @param name The name to check the availability of.
	 * @return True if this name is <em>not</em> taken, false if it is
	 * already registered
	 */
	public boolean checkNameAvailability(String name){
		busy.getAndSet(true);
		try {
			logger.info("Checking if a name is available");
			output.writeObject(new Object[]{"user", "available", name});
			String response = (String)readResponse();
			busy.getAndSet(false);
			return Boolean.parseBoolean(response);
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
		} catch (UnexpectedServerResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			busy.getAndSet(false);
		}
		busy.getAndSet(false);
		return false;
	}

	/**
	 * Retrieves the email address of a user.
	 * @param user The user in question.
	 * @return The user's email address.
	 */
	public String getUserEmail(String user){
		busy.getAndSet(true);
		try {
			logger.info("Getting user email");
			output.writeObject(new Object[]{"user", "getmail", user});
			String response = (String)readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Gets the <code>Design</code> based on its ID.
	 * @param designID The ID of the design being searched for.
	 * @return A <code>Design</code> object.
	 * @see Design
	 */
	public Design findDesignByID(int designID){
		busy.getAndSet(true);
		try {
			logger.debug("Finding design " + designID);
			output.writeObject(new Object[]{"design", "findbyid", designID});
			Object response = readResponse();
			if(response instanceof Design){
				CacheManager.getCacheManager().requestFile(((Design)response).getID(), ((Design)response).getFilepath());
				CacheManager.getCacheManager().requestThumbnail(designID);
				
				busy.getAndSet(false);
				return (Design)response;
			}
			else{
				busy.getAndSet(false);
				return null;
			}
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Gets the designs based on the user they were built by.
	 * @param name The name of the user whose designs are being
	 * searched for.
	 * for were created.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	@SuppressWarnings("unchecked")
	public Vector<Design> findDesignsByName(String name){
		busy.getAndSet(true);
		try {
			logger.info("Finding designs named \""+name+"\"");
			output.writeObject(new Object[]{"design", "findbyname", name});
			Object response = readResponse();
			if(response instanceof Vector<?>){
				if(((Vector<?>)(response)).get(0) instanceof Design){
					busy.getAndSet(false);
					return (Vector<Design>)response;
				}
				else{
					busy.getAndSet(false);
					return null;
				}
			}
			else{
				busy.getAndSet(false);
				return null;
			}
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Gets the designs based on when they were built.
	 * @param user The user who created the designs
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	@SuppressWarnings("unchecked")
	public List<Design> findDesignsByUser(String user){
		busy.getAndSet(true);
		try {
			logger.info("Finding designs by \""+user+"\"");
			output.writeObject(new Object[]{"design", "findbyuser", user});
			Object response = readResponse();
			if(response instanceof List<?>){
				logger.info("returning");
				busy.getAndSet(false);
				return (List<Design>)response;
			}
			else{
				logger.error("Not a list!");
				busy.getAndSet(false);
				return null;
			}
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Gets the designs based on when they were built.
	 * @param date The date of when the designs being searched
	 * for were created.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	@SuppressWarnings("unchecked")
	public List<Design> findDesignsByDate(Date date){
		busy.getAndSet(true);
		try {
			logger.info("Finding designs dates \""+date+"\"");
			output.writeObject(new Object[]{"design", "findbydate", date.getTime()});
			Object response = readResponse();
			if(response instanceof List<?>){
				logger.info("returning");
				busy.getAndSet(false);
				return (List<Design>)response;
			}
			else{
				logger.error("Not a list!");
				busy.getAndSet(false);
				return null;
			}
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Gets all of the designs located in a city.
	 * @param cityID The ID of the city that we are looking in.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	public Vector<Design> findAllDesignsByCity(int cityID){
		return findDesignsByCity(cityID, false);
	}

	/**
	 * Gets only the base designs located in a city.
	 * @param cityID The ID of the city that we are looking in.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	public Vector<Design> findBaseDesignsByCity(int cityID){
		return findDesignsByCity(cityID, true);
	}

	/**
	 * Gets the designs located in a city.
	 * @param cityID The ID of the city that we are looking in.
	 * @param onlyBase Whether to retrieve only the base designs
	 * (true) or all of them (false).
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	@SuppressWarnings("unchecked")
	private Vector<Design> findDesignsByCity(int cityID, boolean onlyBase){
		busy.getAndSet(true);
		try {
			logger.info("Finding designs from city " + cityID);
			output.writeObject(new Object[]{"design", "findbycity", cityID, onlyBase});
			Object response = readResponse();
			if(response instanceof Vector<?>){
				if(((Vector<?>)response).size()>0){
					if(((Vector<?>)(response)).get(0) instanceof Design){
						busy.getAndSet(false);
						return (Vector<Design>)response;
					}
					else{
						busy.getAndSet(false);
						return null;
					}
				}
				else{
					busy.getAndSet(false);
					return null;
				}
			}
			else{
				busy.getAndSet(false);
				return null;
			}
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Gets the designs located in a city.
	 * @param cityID The ID of the city that we are looking in.
	 * @return A Vector of <code>Design</code> objects.
	 * @see Design
	 */
	@SuppressWarnings("unchecked")
	public Vector<Design> findTerrainByCity(int cityID){
		busy.getAndSet(true);
		try {
			logger.info("Finding designs from city " + cityID);
			output.writeObject(new Object[]{"design", "terrainbycity", cityID});
			Object response = readResponse();
			if(response instanceof Vector<?>){
				if(((Vector<?>)response).size()>0){
					if(((Vector<?>)(response)).get(0) instanceof Design){
						busy.getAndSet(false);
						return (Vector<Design>)response;
					}
					else{
						busy.getAndSet(false);
						return null;
					}
				}
				else{
					busy.getAndSet(false);
					return null;
				}
			}
			else{
				busy.getAndSet(false);
				return null;
			}
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	public int[] findVersionsOfProposal(int proposalDesignID){
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"version", "versionsofproposal", proposalDesignID});
			Object response = readResponse();
			if(response!=null){
				if(response instanceof int[]){
					busy.getAndSet(false);
					return (int[])response;
				}
			}
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
		}
		busy.getAndSet(false);
		return null;
	}

	public ProposalPermission getProposalPermissions(int designID){
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"proposal", "getpermissions", designID});
			ProposalPermission response = (ProposalPermission) readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
		busy.getAndSet(false);
		return null;
	}

	/**
	 * Finds all proposals related to a design
	 * @param source The ID of the design for which to find all proposals
	 * @return ID of all proposals related to this source, requires null checking.
	 * @see #findDesignByID(int)
	 */
	public int[] findAllProposals(int designID){
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"design", "allproposals", designID});
			int[] response = (int[]) readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
		}
		busy.getAndSet(false);
		return null;
	}
	
	/**
	 * Gets the proposals located in a specific area
	 * @param coordinate
	 * @param meterRadius
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Design> findAllProposalsNearLocation(UTMCoordinate coordinate, int meterRadius){
		busy.getAndSet(true);
		try {
			logger.info("Finding proposals within " + meterRadius + " of " + coordinate.toString());
			output.writeObject(new Object[]{"proposal", "findinradius", coordinate, meterRadius});
			Object response = readResponse();
			if(response instanceof ArrayList){
				busy.getAndSet(false);
				return (ArrayList<Design>)response;
			}
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
		}
		busy.getAndSet(false);
		return null;
	}
	
	public PhysicalFileTransporter requestThumbnail(int designID){
		busy.getAndSet(true);
		try{
			logger.debug("Requesting thumbnail for design " + designID);
			output.writeObject(new Object[]{"design", "requestthumb", designID});
			PhysicalFileTransporter response = (PhysicalFileTransporter)readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
		
	}

	/**
	 * Requests a file existing on the server.
	 * @param design The design to request from the server.
	 * @return a <code>PhysicalFileTransporter</code> object that can be used to
	 * interact with data by writing to disk or using internally.
	 * @see Design
	 * @see PhysicalFileTransporter
	 */
	public PhysicalFileTransporter requestFile(Design design){
		return requestFile(design.getID());
	}

	/**
	 * Requests a file existing on the server.
	 * @param designID The ID of the design to request from the server.
	 * @return a <code>PhysicalFileTransporter</code> object that can be used to
	 * interact with data by writing to disk or using internally.
	 * @see Design
	 * @see PhysicalFileTransporter
	 */
	public PhysicalFileTransporter requestFile(int designID){
		busy.getAndSet(true);
		try {
			logger.debug("Requesting file for design " + designID);
			output.writeObject(new Object[]{"design", "requestfile", designID});
			PhysicalFileTransporter response = (PhysicalFileTransporter)readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Adds a city to the database.
	 * @param name The name of the city.
	 * @param state The state that the city is in.
	 * @param country The country that the city is in.
	 * @return The ID of this new city.
	 */
	public int addCity(String name, String state, String country){
		busy.getAndSet(true);
		try {
			logger.info("Adding a city [if it doesn't already exist]: " + name + ", " + state + ", " + country);
			output.writeObject(new Object[]{"city", "add", name, state, country});
			int response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
		}
		busy.getAndSet(false);
		return -3;
	}

	@SuppressWarnings("unchecked")
	public List<City> findAllCities(){
		try {
			busy.getAndSet(true);
			output.writeObject(new String[]{"city", "getall"});
			List<City> response = (List<City>)readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Finds cities by the name of the city.
	 * @param country The name of the city.
	 * @return A Vector of Integers which reference the ID's of the cities
	 * that were found.
	 */
	@SuppressWarnings("unchecked")
	public Vector<Integer> findCitiesByName(String name){
		busy.getAndSet(true);
		try {
			logger.info("Finding cities named \""+name+"\"");
			output.writeObject(new Object[]{"city", "findbyname", name});
			Vector<Integer> response = (Vector<Integer>)readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Finds cities by the name of the state.
	 * @param country The name of the state.
	 * @return A Vector of Integers which reference the ID's of the cities
	 * that were found.
	 */
	@SuppressWarnings("unchecked")
	public Vector<Integer> findCitiesByState(String state){
		busy.getAndSet(true);
		try {
			logger.info("Finding cities located in \""+state+"\"");
			output.writeObject(new Object[]{"city", "findbystate", state});
			Vector<Integer> response = (Vector<Integer>)readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Finds cities by the name of the country.
	 * @param country The name of the country.
	 * @return A Vector of Integers which reference the ID's of the cities
	 * that were found.
	 */
	@SuppressWarnings("unchecked")
	public Vector<Integer> findCitiesByCountry(String country){
		busy.getAndSet(true);
		try {
			logger.info("Finding cities located in \""+country+"\"");
			output.writeObject(new Object[]{"city", "findbycountry", country});
			Vector<Integer> response = (Vector<Integer>)readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

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
	public String[] findCityByID(int cityID){
		busy.getAndSet(true);
		try {
			logger.info("Finding city with ID: "+cityID);
			output.writeObject(new Object[]{"city", "findbyid", cityID});
			String[] response = (String[])readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

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
	public String[] findCityByAll(String name, String state, String country){
		busy.getAndSet(true);
		try {
			logger.info("Finding city: \""+name+"\", \""+state+"\", \""+country+"\"");
			output.writeObject(new Object[]{"city", "findbyall", name, state, country});
			String[] response = (String[])readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
			return null;
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
			return null;
		}
	}

	/**
	 * Reports a comment as spam on the server.
	 * @param commentID The ID of the comment to report.
	 */
	public void reportSpamComment(int commentID){
		busy.getAndSet(true);
		try {
			logger.info("Reporting comment " + commentID + " as spam");
			output.writeObject(new Object[]{"comment", "reportspam", commentID});
			busy.getAndSet(false);
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		}
		busy.getAndSet(false);
	}

	/**
	 * Retrieve the comments from the server.
	 * @param designID The ID of the design to retrieve the comments for.
	 * @return A <code>Vector</code> of Comments
	 * @see Comment
	 */
	@SuppressWarnings("unchecked")
	public Vector<Comment> getComments(int designID){
		busy.getAndSet(true);
		try {
			logger.info("Retrieving comments for design " + designID);
			output.writeObject(new Object[]{"comment", "getforid", designID});
			Vector<Comment> response = (Vector<Comment>)readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
		}
		busy.getAndSet(false);
		return null;
	}

	/**
	 * Checks if a user is of a certain type.
	 * @param user
	 * @param userType
	 * @return 1 for yes, 0 for no, negative numbers are errors
	 */
	public int checkUserLevel(String user, 	UserType userType){
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"user", "checklevel", user, userType});
			int response =  Integer.parseInt((String) readResponse());
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
		}
		busy.getAndSet(false);
		return -2;
	}

	/**
	 * Checks the current version of the design class that the server
	 * is running
	 * @return The version of the design of -2 for an error
	 */
	public long getDesignVersion(){
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"softwareversion", "getdesign"});
			long response =  Long.parseLong((String) readResponse());
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
			busy.getAndSet(false);
		}
		busy.getAndSet(false);
		return -2;
	}

	/**
	 * Gets the level of a user
	 * @param user The user's name
	 * @return The type of user
	 */
	public UserType getUserLevel(String user){
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"user", "getlevel", user});
			UserType response = (UserType)readResponse();
			busy.getAndSet(false);
			return response;
		} catch (IOException e){
			logger.error("Network issue detected", e);
			busy.getAndSet(false);
		} catch (UnexpectedServerResponse e){
			e.printStackTrace();
			busy.getAndSet(false);
		}
		busy.getAndSet(false);
		return null;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Wormhole> getWormholesWithin(UTMCoordinate location, int extentNorth, int extentEast){
		ArrayList<Wormhole> wormholes = null;
		busy.getAndSet(true);
		try{
			output.writeObject(new Object[]{"wormhole", "getwithin", location, extentNorth, extentEast});
			wormholes = (ArrayList<Wormhole>)readResponse();
		} catch (IOException e){
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e){
			logger.error("The server has returned an unexpected type!", e);
		}
		busy.getAndSet(false);
		return wormholes;
	}
	
	/**
	 * Retrieves <strong>all</strong> wormholes from the database
	 * @return A list of {@link Wormhole} objects
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Wormhole> getAllWormholes(){
		ArrayList<Wormhole> wormholes = null;
		busy.getAndSet(true);
		try{
			output.writeObject(new Object[]{"wormhole", "getall"});
			wormholes = (ArrayList<Wormhole>)readResponse();
		} catch (IOException e){
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e){
			logger.error("The server has returned an unexpected type!", e);
		}
		busy.getAndSet(false);
		return wormholes;
	}
	
	/**
	 * Gets all of the wormholes located in a city (this refers to the destination city, since you
	 * can jump from anywhere)
	 * @param cityID The ID of the city in which to search for wormholes
	 * @return A list of {@link Wormhole} objects
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Wormhole> getAllWormholesInCity(int cityID){
		logger.info("looking for all wormholes in city");
		ArrayList<Wormhole> wormholes = null;
		busy.getAndSet(true);
		try{
			output.writeObject(new Object[]{"wormhole", "getallincity", cityID});
			wormholes = (ArrayList<Wormhole>)readResponse();
		} catch (IOException e){
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e){
			logger.error("The server has returned an unexpected type!", e);
		}
		busy.getAndSet(false);
		return wormholes;
	}
	
	@SuppressWarnings("unchecked")
	public List<Design> synchronizeData(HashMap<Integer, Integer> hashMap){
		List<Design> designs = null;
		busy.getAndSet(true);
		try{
			output.writeObject(new Object[]{"design", "synchronizedata", hashMap});
			designs = (List<Design>)readResponse();
		} catch (IOException e){
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e){
			logger.error("The server has returned an unexpected type!", e);
		}
		busy.getAndSet(false);
		return designs;
	}
}
