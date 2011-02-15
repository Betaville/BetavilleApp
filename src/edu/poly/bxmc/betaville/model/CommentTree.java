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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * Basic implementation of a tree commenting system.  Only allows
 * for a single level of indentation/quotation functionality at
 * this point in time.
 * @author Skye Book
 */
public class CommentTree {
	private Vector<Comment> comments;
	private ArrayList<ArrayList<Comment>> commentSets;

	/**
	 * Creates a new <code>CommentTree</code> and automatically
	 * sorts it.
	 * @param commentLoad The comments to add, must be ordered from oldest
	 * at index 0 to newest.
	 */
	@SuppressWarnings("unchecked")
	public CommentTree(Vector<Comment> commentLoad){
		comments=(Vector<Comment>) commentLoad.clone();
		commentSets = new ArrayList<ArrayList<Comment>>();
		sortComments();
	}
	
	private void sortComments(){
		// SET UP INDENTATION
		Iterator<Comment> it = comments.iterator();
		while(it.hasNext()){
			Comment c = it.next();
			// check for comment replying to the root
			if(c.repliesTo()==0){
				c.setIndentLevel(0);
			}
			// or find the comment which this comment is replying to
			else{
				Iterator<Comment> it2 = comments.iterator();
				while(it2.hasNext()){
					Comment c2 = it2.next();
					if(c2.getID()==c.repliesTo()){
						// Set the indent level and drop from the iterator
						c.setIndentLevel(c2.getIndentLevel()+1);
						break;
					}
				}
			}
		}
		
		// SET UP ORDER
		for(int i=0; i<comments.size(); i++){
			if(comments.get(i).getIndentLevel()>0){
				for(int j=0; j<i; j++){
					if(comments.get(i).repliesTo()==comments.get(j).getID()){
						findRoot(comments.get(j).getID()).add(comments.get(i));
					}
				}
			}
			else{
				ArrayList<Comment> set = new ArrayList<Comment>();
				set.add(comments.get(i));
				commentSets.add(set);
			}
		}
	}
	
	private ArrayList<Comment> findRoot(int rootID){
		for(int i=0; i<commentSets.size(); i++){
			if(commentSets.get(i).get(0).getID()==rootID){
				return commentSets.get(i);
			}
		}
		return null;
	}
	
	/**
	 * This needs to be re-thought and re-written.
	 * @param commentSet
	 * @experimental
	 */
	public void loadComments(Vector<Comment> commentSet){
		
	}
	
	/**
	 * creates an ordered <code>Vector</code> of comments which can be
	 * read and displayed in a coherent order.
	 * @see Comment#getID()
	 * @see Comment#getIndentLevel()
	 * @return
	 */
	public Vector<Comment> printToVector(){
		Vector<Comment> orderedComments = new Vector<Comment>();
		for(int i=0; i<commentSets.size(); i++){
			for(int j=0; j<commentSets.get(i).size(); j++){
				orderedComments.add(commentSets.get(i).get(j));
			}
		}
		return orderedComments;
	}
	
	/**
	 * creates an ordered <code>Vector</code> of comments which can be
	 * read and displayed in a coherent order.
	 * @see Comment#getID()
	 * @see Comment#getIndentLevel()
	 * @return
	 */
	public ArrayList<Comment> printToArrayList(){
		ArrayList<Comment> orderedComments = new ArrayList<Comment>();
		for(int i=0; i<commentSets.size(); i++){
			for(int j=0; j<commentSets.get(i).size(); j++){
				orderedComments.add(commentSets.get(i).get(j));
			}
		}
		return orderedComments;
	}
	
	public void clear(){
		Iterator<ArrayList<Comment>> it = commentSets.iterator();
		while(it.hasNext()){
			it.next().clear();
		}
		commentSets.clear();
	}
	
	public static void main(String args[]){
		Vector<Comment> comments = new Vector<Comment>();
		comments.add(new Comment(1, 5, "base", "comment", 0, "date"));
		comments.add(new Comment(2, 5, "base", "comment", 1, "date"));
		comments.add(new Comment(3, 5, "base", "comment", 0, "date"));
		comments.add(new Comment(4, 5, "base", "comment", 0, "date"));
		comments.add(new Comment(5, 5, "base", "comment", 1, "date"));
		//comments.add(new Comment(6, 5, "base", "comment", 2, "date"));
		
		CommentTree ct = new CommentTree(comments);
		Vector<Comment> ocm = ct.printToVector();
		for(int i=0; i<ocm.size(); i++){
			System.out.println("Comment " + ocm.get(i).getID() + " Replies To " + ocm.get(i).repliesTo());
		}
	}
}
