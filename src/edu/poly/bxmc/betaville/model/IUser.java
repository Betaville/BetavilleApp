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
import java.util.Comparator;

/**
 * Interface <IUser> -
 * 
 * @author <a href="mailto:skye.book@gmail.com">Skye Book
 */
public interface IUser{
	public static enum UserType implements Serializable{
		/**
		 * Can view and do nothing else
		 */
		GUEST,
		/**
		 * Can make proposals, versions, comments
		 */
		MEMBER,
		
		/**
		 * Member who can also edit the base model
		 */
		BASE_COMMITTER,
		
		/**
		 * Member who can retrieve statistical data from the server
		 */
		DATA_SEARCHER,
		
		/**
		 * Base Committer who can also confirm (or dispel) spam claims
		 */
		MODERATOR,
		
		/**
		 * Moderator who can do most other things :)
		 */
		ADMIN};
	
	/**
	 * Method <getUserName> - Returns the current user's name
	 * 
	 * @return
	 */
	public String getUserName();

	/**
	 * Method <setUserName> - Sets the user name.
	 * 
	 * @param newUserName
	 *            New user name
	 */
	public void setUserName(String newUserName);
	
	public String getUserPass();
	
	public static Comparator<UserType> HIGHER_OR_EQUAL = new Comparator<UserType>(){
		public int compare(UserType arg0, UserType arg1) {
			return getLevel(arg0).compareTo(getLevel(arg1));
		}
		
		private Integer getLevel(UserType type){
			switch (type) {
			case GUEST:
				return 0;
			case MEMBER:
				return 1;
			case BASE_COMMITTER:
				return 2;
			case DATA_SEARCHER:
				return 3;
			case MODERATOR:
				return 4;
			case ADMIN:
				return 5;
			
			/* If a mysterious user type finds its
			 * way here, then force it to a regular
			 * member role (this could otherwise
			 * be a potential security risk for
			 * impersonating a moderator or admin
			 */
			default:
				return 1;
			}
		}
	};
}
