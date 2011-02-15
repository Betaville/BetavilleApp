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
package edu.poly.bxmc.betaville.bookmarks;

import java.util.ArrayList;
import java.util.List;

import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.model.DistanceSort;

/**
 * @author Skye Book
 *
 */
public class BookmarkManager {
	
	private static final BookmarkManager bookmarkManager = new BookmarkManager();
	private ArrayList<Bookmark> bookmarks;
	private ArrayList<IBookmarkChangeListener> listeners;

	/**
	 * 
	 */
	private BookmarkManager() {
		bookmarks = new ArrayList<Bookmark>();
		listeners = new ArrayList<IBookmarkChangeListener>();
	}
	
	public void addListener(IBookmarkChangeListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(IBookmarkChangeListener listener){
		listeners.remove(listener);
	}
	
	public void removeAllListenevers(){
		listeners.clear();
	}
	
	public List<Bookmark> getBookmarks(){
		return bookmarks;
	}
	
	public Bookmark getBookmark(String bookmarkID){
		for(Bookmark b : bookmarks){
			if(b.getBookmarkID().equals(bookmarkID)) return b;
		}
		return null;
	}
	
	public void addBookmark(Bookmark bm){
		bookmarks.add(bm);
		for(IBookmarkChangeListener listener : listeners){
			listener.bookmarkAdded(bm);
		}
	}
	
	public void removeBookmark(String bookmarkID){
		for(Bookmark bm : bookmarks){
			if(bm.getBookmarkID().equals(bookmarkID)){
				bookmarks.remove(bm);
				break;
			}
		}
		for(IBookmarkChangeListener listener : listeners){
			listener.bookmarkRemoved(bookmarkID);
		}
	}
	
	/**
	 * Sorts and retrieves the bookmarks according to the proximity to the
	 * supplied location.  This is a persistent sort (bookmarks remain sorted
	 * after method call)
	 * @param location The location to sort according to
	 * @return
	 */
	public List<Bookmark> getBookmarksNearestTo(ILocation location){
		DistanceSort.closestBookmarks(location.getUTM(), bookmarks);
		return bookmarks;
	}
	
	public List<Bookmark> searchBookmarks(String searchTerm){
		// TODO: do a real search
		
		// search descriptions followed by titles so that the descriptions float up the list
		for(Bookmark b : bookmarks){
			if(b.getDescription().contains(searchTerm)) bookmarks.add(0, bookmarks.remove(bookmarks.indexOf(b)));
		}
		for(Bookmark b : bookmarks){
			if(b.getName().contains(searchTerm)) bookmarks.add(0, bookmarks.remove(bookmarks.indexOf(b)));
		}
		return bookmarks;
	}
	
	public static BookmarkManager get(){
		return bookmarkManager;
	}
}
