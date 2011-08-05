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

import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Comment;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.EmptyDesign;
import edu.poly.bxmc.betaville.model.ProposalPermission;

/**
 * Outline of the authenticated network functionality in Betaville
 * @author Skye Book
 *
 */
public interface ProtectedManager extends UnprotectedManager{

	public abstract boolean authenticateUser(String name, String pass);

	public abstract boolean startSession(String name, String pass);

	public abstract boolean endSession(String sessionToken);

	/**
	 * Adds a user to the server.
	 * @param name The username of the new user.
	 * @param pass The password for the new user.
	 * @param email The email address as provided by the new user.
	 * @param twitter The twitter account/name as provided by the new user.
	 * @param bio The biography as provided by the new user.
	 * @return Whether or not the operation succeeded.
	 */
	public abstract boolean addUser(String name, String pass, String email,
			String twitter, String bio);

	/**
	 * Changes the password for a user.
	 * @param name The user proposing the change.
	 * @param pass The password supplied by the user.
	 * @param newPass The new password to be used.
	 * @return Whether or not the operation succeeded.
	 */
	public abstract boolean changePassword(String name, String pass,
			String newPass);

	/**
	 * Changes the biographical information of a user.
	 * @param name The user proposing the change (hopefully only
	 * the biography's owner!)
	 * @param pass The password supplied by the user.
	 * @param newBio The new biographic information to be used.
	 * @return Whether or not the operation succeeded.
	 */
	public abstract boolean changeBio(String name, String pass, String newBio);

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
	public abstract int addDesign(Design design, String user, String pass,
			PhysicalFileTransporter pft);

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
	public abstract int addEmptyDesign(EmptyDesign design, String user,
			String pass);

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
	public abstract int addProposal(Design design, String removables,
			String user, String pass, PhysicalFileTransporter pft,
			PhysicalFileTransporter thumbTransporter,
			ProposalPermission permission);

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
	public abstract int addVersion(Design design, String removables,
			String user, String pass, PhysicalFileTransporter pft,
			PhysicalFileTransporter thumbTransporter);

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
	public abstract int addBase(Design design, String user, String pass,
			PhysicalFileTransporter pft);

	/**
	 * Removes a <code>Design</code> from the database.  Must be performed by the user who
	 * originally added the design.  
	 * @param designID The ID of the <code>Design</code> to remove.
	 * @param user The user proposing the removal.
	 * @param pass The password supplied by the user.
	 * @return 0 for success, -2 for networking error, -3 for failed authentication
	 */
	public abstract int removeDesign(int designID, String user, String pass);

	/**
	 * Changes the stored name of a <code>Design</code>
	 * @param designID The ID of the <code>Design</code> to change the name for.
	 * @param user The user proposing the change.
	 * @param pass The password of the user proposing the change.
	 * @param newName The new name to be applied.
	 * @return Whether or not the name change operation succeeded.
	 */
	public abstract boolean changeDesignName(int designID, String user,
			String pass, String newName);

	public abstract boolean changeDesignFile(int designID, String user,
			String pass, PhysicalFileTransporter pft, boolean textureOnOff);

	/**
	 * Changes the stored description of a <code>Design</code>
	 * @param designID The ID of the <code>Design</code> to change the description for.
	 * @param user The user proposing the change.
	 * @param pass The password of the user proposing the change.
	 * @param newDescription The new description to be applied.
	 * @return Whether or not the description change operation succeeded.
	 */
	public abstract boolean changeDesignDescription(int designID, String user,
			String pass, String newDescription);

	/**
	 * Changes the stored address of a <code>Design</code>
	 * @param designID The ID of the <code>Design</code> to change the address for.
	 * @param user The user proposing the change.
	 * @param pass The password of the user proposing the change.
	 * @param newAddress The new address to be applied.
	 * @return Whether or not the address change operation succeeded.
	 */
	public abstract boolean changeDesignAddress(int designID, String user,
			String pass, String newAddress);

	/**
	 * Changes the stored url of a <code>Design</code>
	 * @param designID The ID of the <code>Design</code> to change the address for.
	 * @param user The user proposing the change.
	 * @param pass The password of the user proposing the change.
	 * @param url The new URL to be applied.
	 * @return Whether or not the URL change operation succeeded.
	 */
	public abstract boolean changeDesignURL(int designID, String user,
			String pass, String newURL);

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
	public abstract boolean changeModeledDesignLocation(int designID, float rotY,
			String user, String pass, UTMCoordinate newLocation);

	/**
	 * Favorites a design on the server on behalf of the user
	 * @param user
	 * @param pass
	 * @param designID
	 * @return 0 for success, -1 for SQL error, -2 if its already faved, -3 for failed authentication, or -4 for a network error
	 */
	public abstract int faveDesign(int designID, String user, String pass);

	/**
	 * Adds a comment in reference to a specific design.
	 * @param comment The <code>Comment</code> to add.
	 * @param pass The user's password.
	 * @return Whether or not the comment was added.
	 * @see Comment
	 */
	public abstract boolean addComment(Comment comment, String pass);

	/**
	 * Deletes a comment on the server.  This can only be done by the user who posted
	 * the comment.  This needs to expand on the server-side to include moderator rights.
	 * @param commentID The ID of the comment to delete.
	 * @param user The user attempting to delete the comment.
	 * @param pass The password supplied for deleting the comment.
	 * @return Whether or not the comment was deleted.
	 */
	public abstract boolean deleteComment(int commentID, String user,
			String pass);

	/**
	 * Adds a new wormhole on the server.
	 * @param location The location for this wormhole
	 * @param name The name of this wormhole
	 * @param cityID The ID of the city in which this wormhole is located
	 * @return The ID of the new wormhole or a server error code.
	 */
	public abstract int addWormhole(ILocation location, String name, int cityID);

	public abstract int deleteWormhole(int wormholeID);

	public abstract int editWormholeName(int wormholeID, String newName);

	public abstract int editWormholeLocation(int wormholeID,
			UTMCoordinate newLocation);

}