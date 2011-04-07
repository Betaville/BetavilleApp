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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.ResourceLoader;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Comment;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.EmptyDesign;
import edu.poly.bxmc.betaville.model.ProposalPermission;
import edu.poly.bxmc.betaville.module.Module;

/**
 * Provides an encrypted communications channel to the server
 * @author Skye Book
 *
 */
public class SecureClientManager extends ClientManager{
	private static Logger logger = Logger.getLogger(SecureClientManager.class);

	/**
	 * Constant <PORT_SERVER> - Port of the server
	 */
	private final int PORT_SERVER = 14501;
	
	private char[] keyStorePass = "123456".toCharArray();
	private char[] trustStorePass = keyStorePass;
	             
	/**
	 * Constructor - Creation of the client manager
	 */
	public SecureClientManager(List<Module> modules){
		try{
			KeyStore keyStore = KeyStore.getInstance("JKS");
			KeyStore trustStore = KeyStore.getInstance("JKS");
			
			keyStore.load(ResourceLoader.loadResource("/data/certs/client.keystore").openStream(), keyStorePass);
			trustStore.load(ResourceLoader.loadResource("/data/certs/client.truststore").openStream(), trustStorePass);
			
			KeyManagerFactory keyManager = KeyManagerFactory.getInstance("SunX509");
			keyManager.init(keyStore, keyStorePass);
			TrustManagerFactory trustManager = TrustManagerFactory.getInstance("SunX509");
			trustManager.init(trustStore);
			
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);
			
			SSLSocketFactory sslFactory = context.getSocketFactory();
			clientSocket = (SSLSocket)sslFactory.createSocket();
			
			clientSocket.connect(new InetSocketAddress(SettingsPreferences.getServerIP(), PORT_SERVER));
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			input = new ObjectInputStream(clientSocket.getInputStream());
		}catch(KeyStoreException e){
			logger.fatal("Java KeyStore Issue", e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			logger.fatal("Could not connect to server at "+SettingsPreferences.getServerIP(), e);
			JOptionPane.showMessageDialog(null, "Could not connect to server at "+SettingsPreferences.getServerIP());
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
	
	public boolean authenticateUser(String name, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Authenticating User");
			output.writeObject(new Object[]{"user", "auth", name, pass});
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}
	
	public boolean startSession(String name, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Starting User Session");
			output.writeObject(new Object[]{"user", "startsession", name, pass});
			Object[] response = (Object[])readResponse();
			int sessionID = Integer.parseInt((String)response[0]);
			String sessionToken = (String)response[1];
			busy.getAndSet(false);
			if(sessionID>0){
				logger.info("Logged in as "+ name +", Session ID: "+sessionID);
				SettingsPreferences.setSessionID(sessionID);
				SettingsPreferences.setSessionToken(sessionToken);
				return true;
			}
			else{
				if(sessionID==-3){
					// authentication failed
					logger.warn("Authentication Failed");
				}
				return false;
			}
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}
	
	public boolean endSession(String sessionToken){
		busy.getAndSet(true);
		try {
			logger.info("Ending user session");
			output.writeObject(new Object[]{"user", "endsession", sessionToken});
			int response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			if(response==0){
				SettingsPreferences.setSessionToken("");
				return true;
				}
			else return false;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/**
	 * Adds a user to the server.
	 * @param name The username of the new user.
	 * @param pass The password for the new user.
	 * @param email The email address as provided by the new user.
	 * @param twitter The twitter account/name as provided by the new user.
	 * @param bio The biography as provided by the new user.
	 * @return Whether or not the operation succeeded.
	 */
	public boolean addUser(String name, String pass, String email, String twitter, String bio){
		busy.getAndSet(true);
		try {
			logger.info("Adding User");
			output.writeObject(new Object[]{"user", "add", name, pass, email, twitter, bio});
			String response = (String)readResponse();
			if(Boolean.parseBoolean(response)){
				System.out.println("bool read correctly");
			}
			busy.getAndSet(false);
			return Boolean.parseBoolean(response);
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}
	
	/**
	 * Changes the password for a user.
	 * @param name The user proposing the change.
	 * @param pass The password supplied by the user.
	 * @param newPass The new password to be used.
	 * @return Whether or not the operation succeeded.
	 */
	public boolean changePassword(String name, String pass, String newPass){
		busy.getAndSet(true);
		try {
			logger.info("Changing user password");
			output.writeObject(new Object[]{"user", "changepass", name, pass, newPass});
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/**
	 * Changes the biographical information of a user.
	 * @param name The user proposing the change (hopefully only
	 * the biography's owner!)
	 * @param pass The password supplied by the user.
	 * @param newBio The new biographic information to be used.
	 * @return Whether or not the operation succeeded.
	 */
	public boolean changeBio(String name, String pass, String newBio){
		busy.getAndSet(true);
		try {
			logger.info("Changing user information");
			output.writeObject(new Object[]{"user", "changebio", name, pass, newBio});
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}
	
	/**
	 * Adds a design to the server.
	 * @param design The <code>Design</code> object describing the new entity.
	 * @param user The user proposing this new <code>Design</code>.
	 * @param pass The password supplied by the user.
	 * @param sourceID The ID of the design that this <code>Design</code>
	 * is improving on.  Using the value 0 here signifies that this is not an
	 * iteration on a previously existing <code>Design</code>.
	 * @param pft A <code>PhysicalFileTransporter</code> to carry relevant media data.
	 * @return The ID of the new <code>Design</code>.
	 * @see Design
	 * @see PhysicalFileTransporter
	 */
	public int addDesign(Design design, String user, String pass, PhysicalFileTransporter pft){
		busy.getAndSet(true);
		try {
			logger.info("Adding a design");
			output.writeObject(new Object[]{"design", "addbase", design, user, pass, pft});
			logger.info("Data transmission complete for design addition");
			busy.getAndSet(false);
			return Integer.parseInt((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return -3;
	}
	
	/**
	 * Adds an EmptyDesign to the server.
	 * @param design The <code>Design</code> object describing the new entity.
	 * @param user The user proposing this new <code>Design</code>.
	 * @param pass The password supplied by the user.
	 * @param sourceID The ID of the design that this <code>Design</code>
	 * is improving on.  Using the value 0 here signifies that this is not an
	 * iteration on a previously existing <code>Design</code>.
	 * @param pft A <code>PhysicalFileTransporter</code> to carry relevant media data.
	 * @return The ID of the new <code>Design</code>.
	 * @see Design
	 * @see PhysicalFileTransporter
	 */
	public int addEmptyDesign(EmptyDesign design, String user, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Adding an empty design");
			output.writeObject(new Object[]{"design", "addempty", design, user, pass});
			busy.getAndSet(false);
			return Integer.parseInt((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return -3;
	}
	
	/**
	 * Adds a design to the server.
	 * @param design The <code>Design</code> object describing the new entity.
	 * @param user The user proposing this new <code>Design</code>.
	 * @param pass The password supplied by the user.
	 * @param sourceID The ID of the design that this <code>Design</code>
	 * is improving on.  Using the value 0 here signifies that this is not an
	 * iteration on a previously existing <code>Design</code>.
	 * @param pft A <code>PhysicalFileTransporter</code> to carry relevant media data.
	 * @return The ID of the new <code>Design</code>.
	 * @see Design
	 * @see PhysicalFileTransporter
	 */
	public int addProposal(Design design, String removables, String user, String pass, PhysicalFileTransporter pft, PhysicalFileTransporter thumbTransporter, ProposalPermission permission){
		busy.getAndSet(true);
		try {
			logger.info("Adding a proposal");
			output.writeObject(new Object[]{"design", "addproposal", design, user, pass, pft, removables, thumbTransporter, permission});
			busy.getAndSet(false);
			return Integer.parseInt((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return -3;
	}
	
	/**
	 * Adds a version of the proposal to the server.
	 * @param design The <code>Design</code> object describing the new entity.
	 * @param user The user proposing this new <code>Design</code>.
	 * @param pass The password supplied by the user.
	 * @param sourceID The ID of the design that this <code>Design</code>
	 * is improving on.  Using the value 0 here signifies that this is not an
	 * iteration on a previously existing <code>Design</code>.
	 * @param pft A <code>PhysicalFileTransporter</code> to carry relevant media data.
	 * @return The ID of the new <code>Design</code>.
	 * @see Design
	 * @see PhysicalFileTransporter
	 */
	public int addVersion(Design design, String removables, String user, String pass, PhysicalFileTransporter pft, PhysicalFileTransporter thumbTransporter){
		busy.getAndSet(true);
		try {
			logger.info("Adding a design");
			output.writeObject(new Object[]{"proposal", "addversion", design, user, pass, pft, removables, thumbTransporter});
			logger.info("Data transmission complete for design addition");
			busy.getAndSet(false);
			return Integer.parseInt((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return -3;
	}
	
	/**
	 * Adds a design to the server.
	 * @param design The <code>Design</code> object describing the new entity.
	 * @param user The user proposing this new <code>Design</code>.
	 * @param pass The password supplied by the user.
	 * @param sourceID The ID of the design that this <code>Design</code>
	 * is improving on.  Using the value 0 here signifies that this is not an
	 * iteration on a previously existing <code>Design</code>.
	 * @param pft A <code>PhysicalFileTransporter</code> to carry relevant media data.
	 * @return The ID of the new <code>Design</code>.
	 * @see Design
	 * @see PhysicalFileTransporter
	 */
	public int addBase(Design design, String user, String pass, PhysicalFileTransporter pft){
		busy.getAndSet(true);
		try {
			logger.info("Adding a base design");
			output.writeObject(new Object[]{"design", "addbase", design, user, pass, pft});
			busy.getAndSet(false);
			return Integer.parseInt((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return -3;
	}

	/**
	 * Removes a <code>Design</code> from the database.  Must be performed by the user who
	 * originally added the design.  
	 * @param designID The ID of the <code>Design</code> to remove.
	 * @param user The user proposing the removal.
	 * @param pass The password supplied by the user.
	 * @return 0 for success, -2 for networking error, -3 for failed authentication
	 */
	public int removeDesign(int designID, String user, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Removing a design");
			output.writeObject(new Object[]{"design", "remove", designID, user, pass});
			busy.getAndSet(false);
			return Integer.parseInt((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return -2;
	}

	/**
	 * Changes the stored name of a <code>Design</code>
	 * @param designID The ID of the <code>Design</code> to change the name for.
	 * @param user The user proposing the change.
	 * @param pass The password of the user proposing the change.
	 * @param newName The new name to be applied.
	 * @return Whether or not the name change operation succeeded.
	 */
	public boolean changeDesignName(int designID, String user, String pass, String newName){
		busy.getAndSet(true);
		try {
			logger.info("Changing name of design " + designID + " to \""+newName+"\"");
			output.writeObject(new Object[]{"design", "changename", designID, newName, user, pass});
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}
	
	public boolean changeDesignFile(int designID, String user, String pass, PhysicalFileTransporter pft, boolean textureOnOff){
		busy.getAndSet(true);
		try {
			logger.info("Changing the file for design " + designID);
			output.writeObject(new Object[]{"design", "changefile", designID, user, pass, textureOnOff, pft});
			logger.info("Data transmission complete for file change");
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/**
	 * Changes the stored description of a <code>Design</code>
	 * @param designID The ID of the <code>Design</code> to change the description for.
	 * @param user The user proposing the change.
	 * @param pass The password of the user proposing the change.
	 * @param newDescription The new description to be applied.
	 * @return Whether or not the description change operation succeeded.
	 */
	public boolean changeDesignDescription(int designID, String user, String pass, String newDescription){
		busy.getAndSet(true);
		try {
			logger.info("Changing description for design " + designID);
			output.writeObject(new Object[]{"design", "changedescription", designID, newDescription, user, pass});
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}
	
	/**
	 * Changes the stored address of a <code>Design</code>
	 * @param designID The ID of the <code>Design</code> to change the address for.
	 * @param user The user proposing the change.
	 * @param pass The password of the user proposing the change.
	 * @param newAddress The new address to be applied.
	 * @return Whether or not the address change operation succeeded.
	 */
	public boolean changeDesignAddress(int designID, String user, String pass, String newAddress){
		busy.getAndSet(true);
		try {
			logger.info("Changing the address of design " + designID);
			output.writeObject(new Object[]{"design", "changeaddress", designID, newAddress, user, pass});
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}
	
	/**
	 * Changes the stored url of a <code>Design</code>
	 * @param designID The ID of the <code>Design</code> to change the address for.
	 * @param user The user proposing the change.
	 * @param pass The password of the user proposing the change.
	 * @param url The new URL to be applied.
	 * @return Whether or not the URL change operation succeeded.
	 */
	public boolean changeDesignURL(int designID, String user, String pass, String newURL){
		busy.getAndSet(true);
		try {
			logger.info("Changing the URL for design " + designID);
			output.writeObject(new Object[]{"design", "changeurl", designID, newURL, user, pass});
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/**
	 * Changes the location of a <code>ModeledDesign</code>
	 * @param designID The ID of the <code>ModeledDesign</code> to change.
	 * @param rotY The new rotation of the object about the Y-Xxis.
	 * @param user The user proposing the change.
	 * @param pass The password of the user.
	 * @param newLocation The new location in the form of a <code>UTMCoordinate</code>
	 * @return Whether or not the location was changed.
	 * @see UTMCoordinate
	 */
	public boolean changeModeledDesignLocation(int designID, int rotY, String user, String pass, UTMCoordinate newLocation){
		busy.getAndSet(true);
		try {
			logger.info("Changing the location for design " + designID);
			output.writeObject(new Object[]{"design", "changemodellocation", designID, newLocation, rotY, user, pass});
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}
	
	/**
	 * Favorites a design on the server on behalf of the user
	 * @param user
	 * @param pass
	 * @param designID
	 * @return 0 for success, -1 for SQL error, -2 if its already faved, -3 for failed authentication, or -4 for a network error
	 */
	public int faveDesign(int designID, String user, String pass){
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"fave", "add", user, pass, designID});
			return Integer.parseInt((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return -4;
	}

	/**
	 * Adds a comment in reference to a specific design.
	 * @param comment The <code>Comment</code> to add.
	 * @param pass The user's password.
	 * @return Whether or not the comment was added.
	 * @see Comment
	 */
	public boolean addComment(Comment comment, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Inserting a comment for about design " + comment.getDesignID());
			output.writeObject(new Object[]{"comment", "add", comment, pass});
			boolean response = Boolean.parseBoolean((String)readResponse());
			System.out.println("received net response");
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/**
	 * Deletes a comment on the server.  This can only be done by the user who posted
	 * the comment.  This needs to expand on the server-side to include moderator rights.
	 * @param commentID The ID of the comment to delete.
	 * @param user The user attempting to delete the comment.
	 * @param pass The password supplied for deleting the comment.
	 * @return Whether or not the comment was deleted.
	 */
	public boolean deleteComment(int commentID, String user, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Deleting comment " + commentID);
			output.writeObject(new Object[]{"comment", "delete", commentID, user, pass});
			busy.getAndSet(false);
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}
	
	/**
	 * Adds a new wormhole on the server.
	 * @param location The location for this wormhole
	 * @param name The name of this wormhole
	 * @param cityID The ID of the city in which this wormhole is located
	 * @return The ID of the new wormhole or a server error code.
	 */
	public int addWormhole(ILocation location, String name, int cityID){
		Integer response = null;
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"wormhole", "add", location.getUTM(), name, cityID, SettingsPreferences.getSessionToken()});
			response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return response;
	}
	
	public int deleteWormhole(int wormholeID){
		Integer response = null;
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"wormhole", "delete", wormholeID, SettingsPreferences.getSessionToken()});
			response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return response;
	}
	
	public int editWormholeName(int wormholeID, String newName){
		Integer response = null;
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"wormhole", "editname", newName, wormholeID, SettingsPreferences.getSessionToken()});
			response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return response;
	}
	
	public int editWormholeLocation(int wormholeID, UTMCoordinate newLocation){
		Integer response = null;
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"wormhole", "editname", newLocation, wormholeID, SettingsPreferences.getSessionToken()});
			response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return response;
	}
}
