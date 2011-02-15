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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.jdom.Element;

import edu.poly.bxmc.betaville.jme.loaders.util.DriveFinder;
import edu.poly.bxmc.betaville.xml.XMLWriter;

/**
 * @author Skye Book
 *
 */
public class BookmarkWriter extends XMLWriter {
	
	/**
	 * @throws IOException if the file cannot be written to.
	 */
	public BookmarkWriter(Bookmark bookmark) throws IOException {
		this(bookmark, new File(DriveFinder.getBookmarksFolder().toString()+"/"+bookmark.getBookmarkID()+".bvbm"));
	}

	/**
	 * @param file
	 * @throws IOException if the file cannot be written to.
	 */
	public BookmarkWriter(Bookmark bookmark, File file) throws IOException {
		super("betaville", file);
		
		Element b = new Element("bookmark");
		b.setAttribute("name", bookmark.getName());
		b.setAttribute("bookmarkID", bookmark.getName());
		b.setAttribute("name", bookmark.getName());
		
		
		ArrayList<String> betavilleKeys = new ArrayList<String>();
		
		Enumeration<?> propertyNames = System.getProperties().propertyNames();
		String name;
		while(propertyNames.hasMoreElements()){
			name = propertyNames.nextElement().toString().toLowerCase();
			if(name.startsWith("betaville")) betavilleKeys.add(name);
		}
		
		for(String key : betavilleKeys){
			String[] structure = key.split("\\.");
			
			// start the loop after the first value ("betaville")
			for(int i=1; i<structure.length; i++){
				Element parent = rootElement;
				for(int j=1; j<i; j++){
					parent = parent.getChild(structure[j]);
				}
				
				// see if the current element is already there.
				boolean exists = parent.getChild(structure[i])!=null;
				
				if(exists) continue;
				
				if(i==structure.length-1){
					Element lastElement = new Element(structure[i]);
					lastElement.addContent(System.getProperty(key));
					parent.addContent(lastElement);
					continue;
				}
				
				parent.addContent(new Element(structure[i]));
			}
		}
		
		
	}

}
