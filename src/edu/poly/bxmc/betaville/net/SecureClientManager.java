/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

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
public class SecureClientManager extends ClientManager implements ProtectedManager{
	private static Logger logger = Logger.getLogger(SecureClientManager.class);

	/**
	 * Constant <PORT_SERVER> - Port of the server
	 */
	private final int PORT_SERVER = 14500;

	/**
	 * Constructor - Creation of the client manager
	 */
	public SecureClientManager(List<Module> modules, boolean createSocketHere){
		this(modules, createSocketHere, SettingsPreferences.getServerIP());
	}

	/**
	 * Constructor - Creation of the client manager
	 */
	public SecureClientManager(List<Module> modules, boolean createSocketHere, String serverIP){

		if(!createSocketHere) return;

		try {
			clientSocket = new Socket(serverIP, PORT_SERVER);
			logger.info("Client application : "+ clientSocket.toString());
			progressOutput = new ProgressOutputStream(clientSocket.getOutputStream());
			output = new ObjectOutputStream(progressOutput);
			
			progressInput = new ProgressInputStream(clientSocket.getInputStream());
			input = new ObjectInputStream(progressInput);
		} catch (UnknownHostException e) {
			logger.fatal("Could not connect to server at "+SettingsPreferences.getServerIP(), e);
			JOptionPane.showMessageDialog(null, "Could not connect to server at "+SettingsPreferences.getServerIP());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#authenticateUser(java.lang.String, java.lang.String)
	 */
	public boolean authenticateUser(String name, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Authenticating User");
			output.writeObject(new Object[]{"user", "auth", name, pass});
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#startSession(java.lang.String, java.lang.String)
	 */
	public boolean startSession(String name, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Starting User Session");
			output.writeObject(new Object[]{"user", "startsession", name, pass});
			Object[] response = (Object[])readResponse();
			int sessionID = Integer.parseInt((String)response[0]);
			String sessionToken = (String)response[1];
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#endSession(java.lang.String)
	 */
	public boolean endSession(String sessionToken){
		busy.getAndSet(true);
		try {
			logger.info("Ending user session");
			output.writeObject(new Object[]{"user", "endsession", sessionToken});
			int response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#addUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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
			touchLastUsed();
			return Boolean.parseBoolean(response);
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#changePassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean changePassword(String name, String pass, String newPass){
		busy.getAndSet(true);
		try {
			logger.info("Changing user password");
			output.writeObject(new Object[]{"user", "changepass", name, pass, newPass});
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#changeBio(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean changeBio(String name, String pass, String newBio){
		busy.getAndSet(true);
		try {
			logger.info("Changing user information");
			output.writeObject(new Object[]{"user", "changebio", name, pass, newBio});
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#addEmptyDesign(edu.poly.bxmc.betaville.model.EmptyDesign, java.lang.String, java.lang.String)
	 */
	public int addEmptyDesign(EmptyDesign design, String user, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Adding an empty design");
			output.writeObject(new Object[]{"design", "addempty", design, user, pass});
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#addProposal(edu.poly.bxmc.betaville.model.Design, java.lang.String, java.lang.String, java.lang.String, edu.poly.bxmc.betaville.net.PhysicalFileTransporter, edu.poly.bxmc.betaville.net.PhysicalFileTransporter, edu.poly.bxmc.betaville.model.ProposalPermission)
	 */
	public int addProposal(Design design, String removables, String user, String pass, PhysicalFileTransporter pft, PhysicalFileTransporter thumbTransporter, PhysicalFileTransporter sourceMediaTransporter, ProposalPermission permission){
		busy.getAndSet(true);
		try {
			logger.info("Adding a proposal");
			output.writeObject(new Object[]{"design", "addproposal", design, user, pass, pft, removables, thumbTransporter, permission});
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#addVersion(edu.poly.bxmc.betaville.model.Design, java.lang.String, java.lang.String, java.lang.String, edu.poly.bxmc.betaville.net.PhysicalFileTransporter, edu.poly.bxmc.betaville.net.PhysicalFileTransporter)
	 */
	public int addVersion(Design design, String removables, String user, String pass, PhysicalFileTransporter pft, PhysicalFileTransporter thumbTransporter, PhysicalFileTransporter sourceMediaTransporter){
		busy.getAndSet(true);
		try {
			logger.info("Adding a design");
			output.writeObject(new Object[]{"proposal", "addversion", design, user, pass, pft, removables, thumbTransporter});
			logger.info("Data transmission complete for design addition");
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#addBase(edu.poly.bxmc.betaville.model.Design, java.lang.String, java.lang.String, edu.poly.bxmc.betaville.net.PhysicalFileTransporter)
	 */
	public int addBase(Design design, String user, String pass, PhysicalFileTransporter pft, PhysicalFileTransporter thumbTransporter, PhysicalFileTransporter sourceMediaTransporter){
		busy.getAndSet(true);
		try {
			logger.info("Adding a base design");
			// send the thumbnail along as well if its transporter isn't null
			if(thumbTransporter==null){
				output.writeObject(new Object[]{"design", "addbase", design, user, pass, pft});
			}
			else{
				output.writeObject(new Object[]{"design", "addbase", design, user, pass, pft, thumbTransporter});
			}
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#setThumbnailForObject(int, edu.poly.bxmc.betaville.net.PhysicalFileTransporter, java.lang.String, java.lang.String)
	 */
	@Override
	public int setThumbnailForObject(int designID,
			PhysicalFileTransporter pft, String user, String pass) {
		busy.getAndSet(true);
		try {
			logger.info("Adding a base design");
			// send the thumbnail along as well if its transporter isn't null
			output.writeObject(new Object[]{"design", "setthumb", designID, pft, user, pass});
			busy.getAndSet(false);
			touchLastUsed();
			return Integer.parseInt((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return -1;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#removeDesign(int, java.lang.String, java.lang.String)
	 */
	public int removeDesign(int designID, String user, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Removing a design");
			output.writeObject(new Object[]{"design", "remove", designID, user, pass});
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#changeDesignName(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean changeDesignName(int designID, String user, String pass, String newName){
		busy.getAndSet(true);
		try {
			logger.info("Changing name of design " + designID + " to \""+newName+"\"");
			output.writeObject(new Object[]{"design", "changename", designID, newName, user, pass});
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#changeDesignFile(int, java.lang.String, java.lang.String, edu.poly.bxmc.betaville.net.PhysicalFileTransporter, boolean)
	 */
	public boolean changeDesignFile(int designID, String user, String pass, PhysicalFileTransporter pft, PhysicalFileTransporter sourceMedia, boolean textureOnOff){
		busy.getAndSet(true);
		try {
			logger.info("Changing the file for design " + designID);
			output.writeObject(new Object[]{"design", "changefile", designID, user, pass, textureOnOff, pft});
			logger.info("Data transmission complete for file change");
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#changeDesignDescription(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean changeDesignDescription(int designID, String user, String pass, String newDescription){
		busy.getAndSet(true);
		try {
			logger.info("Changing description for design " + designID);
			output.writeObject(new Object[]{"design", "changedescription", designID, newDescription, user, pass});
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#changeDesignAddress(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean changeDesignAddress(int designID, String user, String pass, String newAddress){
		busy.getAndSet(true);
		try {
			logger.info("Changing the address of design " + designID);
			output.writeObject(new Object[]{"design", "changeaddress", designID, newAddress, user, pass});
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#changeDesignURL(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean changeDesignURL(int designID, String user, String pass, String newURL){
		busy.getAndSet(true);
		try {
			logger.info("Changing the URL for design " + designID);
			output.writeObject(new Object[]{"design", "changeurl", designID, newURL, user, pass});
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#changeModeledDesignLocation(int, float, java.lang.String, java.lang.String, edu.poly.bxmc.betaville.jme.map.UTMCoordinate)
	 */
	public boolean changeModeledDesignLocation(int designID, float rotY, String user, String pass, UTMCoordinate newLocation){
		busy.getAndSet(true);
		try {
			logger.info("Changing the location for design " + designID);
			output.writeObject(new Object[]{"design", "changemodellocation", designID, newLocation, rotY, user, pass});
			String response = (String)readResponse();
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean(response);
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#faveDesign(int, java.lang.String, java.lang.String)
	 */
	public int faveDesign(int designID, String user, String pass){
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"fave", "add", user, pass, designID});
			String response = (String)readResponse();
			busy.getAndSet(false);
			touchLastUsed();
			return Integer.parseInt(response);
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return -4;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#addComment(edu.poly.bxmc.betaville.model.Comment, java.lang.String)
	 */
	public boolean addComment(Comment comment, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Inserting a comment for design " + comment.getDesignID());
			output.writeObject(new Object[]{"comment", "add", comment, pass});
			boolean response = Boolean.parseBoolean((String)readResponse());
			System.out.println("received net response");
			busy.getAndSet(false);
			touchLastUsed();
			return response;
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#deleteComment(int, java.lang.String, java.lang.String)
	 */
	public boolean deleteComment(int commentID, String user, String pass){
		busy.getAndSet(true);
		try {
			logger.info("Deleting comment " + commentID);
			output.writeObject(new Object[]{"comment", "delete", commentID, user, pass});
			busy.getAndSet(false);
			touchLastUsed();
			return Boolean.parseBoolean((String)readResponse());
		} catch (IOException e) {
			logger.error("Network issue detected", e);
		} catch (UnexpectedServerResponse e) {
			e.printStackTrace();
		}
		busy.getAndSet(false);
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#addWormhole(edu.poly.bxmc.betaville.jme.map.ILocation, java.lang.String, int)
	 */
	public int addWormhole(ILocation location, String name, int cityID){
		Integer response = null;
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"wormhole", "add", location.getUTM(), name, cityID, SettingsPreferences.getSessionToken()});
			response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#deleteWormhole(int)
	 */
	public int deleteWormhole(int wormholeID){
		Integer response = null;
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"wormhole", "delete", wormholeID, SettingsPreferences.getSessionToken()});
			response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#editWormholeName(int, java.lang.String)
	 */
	public int editWormholeName(int wormholeID, String newName){
		Integer response = null;
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"wormhole", "editname", newName, wormholeID, SettingsPreferences.getSessionToken()});
			response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			touchLastUsed();
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

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.net.ProtectedManager#editWormholeLocation(int, edu.poly.bxmc.betaville.jme.map.UTMCoordinate)
	 */
	public int editWormholeLocation(int wormholeID, UTMCoordinate newLocation){
		Integer response = null;
		busy.getAndSet(true);
		try {
			output.writeObject(new Object[]{"wormhole", "editname", newLocation, wormholeID, SettingsPreferences.getSessionToken()});
			response = Integer.parseInt((String)readResponse());
			busy.getAndSet(false);
			touchLastUsed();
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
