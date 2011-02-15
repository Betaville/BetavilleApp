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

import java.io.Serializable;

/**
 * A comment about a {@link Design}
 * @author Skye Book
 *
 */
public class Comment extends Criticism implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id;
	private String comment;
	private int repliesToCommentID;
	private String date;
	private int indentLevel;

	/**
	 * 
	 */
	public Comment(int id, int designID, String user, String comment){
		super(designID, user);
		this.id=id;
		this.comment=comment;
	}
	
	/**
	 * 
	 */
	public Comment(int id, int designID, String user, String comment, int repliesTo){
		super(designID, user);
		this.id=id;
		this.comment=comment;
		this.repliesToCommentID=repliesTo;
	}
	
	/**
	 * 
	 */
	public Comment(int id, int designID, String user, String comment, int repliesTo, String date){
		super(designID, user);
		this.id=id;
		this.comment=comment;
		this.repliesToCommentID=repliesTo;
		this.date=date;
	}
	
	/**
	 * Gets a user's comments about a proposal
	 * @return User's comments
	 */
	public String getComment(){
		return comment;
	}
	
	/**
	 * 
	 * @return
	 */
	public int repliesTo(){
		return repliesToCommentID;
	}
	
	public void setID(int id){
		this.id=id;
	}
	
	/**
	 * Retrieves the ID of this <code>Comment</code>
	 * @return Unique identification number
	 */
	public int getID(){
		return id;
	}
	
	public String getDate(){
		return date;
	}
	
	public void setIndentLevel(int level){
		indentLevel=level;
	}
	
	/**
	 * Retrieves the level of indentation based on
	 * which comment this is replying to.
	 * @return Level of indentation from 0 (root) to theoretical infinity
	 */
	public int getIndentLevel(){
		return indentLevel;
	}
}
