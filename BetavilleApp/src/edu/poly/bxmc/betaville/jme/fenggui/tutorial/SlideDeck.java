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
package edu.poly.bxmc.betaville.jme.fenggui.tutorial;

import java.util.ArrayList;

/**
 * Provides slide show functionality
 * @author Skye Book
 *
 */
public class SlideDeck{
	
	private ArrayList<String> authors = new ArrayList<String>();

	private String date="";
	private String defaultFont="verdana";
	private int defaultSize=8;
	// the default colors in an RGBA format
	private int[] defaultColor = new int[]{0,0,0,255};
	
	private String title = "My Slideshow";
	private ArrayList<Slide> slides = new ArrayList<Slide>();
	/** current slide *index* */
	private int currentLocation=0;

	public SlideDeck(){}
	
	public SlideDeck(Slide... slides) {
		for(Slide slide : slides){
			this.slides.add(slide);
		}
	}
	
	public boolean isAtLastSlide(){
		return currentLocation==slides.size()-1;
	}
	
	public boolean isAtFirstSlide(){
		return currentLocation==0;
	}
	
	public void addSlide(Slide slide){
		slides.add(slide);
	}
	
	public void setAuthors(String... authors){
		this.authors.clear();
		for(String author : authors){
			this.authors.add(author);
		}
	}
	
	public Slide back(){
		// can't rewind past the beginning!
		if(currentLocation==0) return slides.get(currentLocation);
		
		currentLocation--;
		return slides.get(currentLocation);
	}
	
	public Slide next(){
		// can't advance past the end!
		if(currentLocation==slides.size()-1) return slides.get(currentLocation);
		
		currentLocation++;
		return slides.get(currentLocation);
	}
	
	public Slide jumpTo(int slideNumber){
		// clamp to the minimum and maximum number of slides
		if(slideNumber<1) slideNumber=1;
		else if(slideNumber>slides.size()) slideNumber = slides.size();
		
		currentLocation=slideNumber-1;
		return slides.get(currentLocation);
	}
	
	public Slide jumpToLast(){
		currentLocation=slides.size()-1;
		return slides.get(currentLocation);
	}
	
	public Slide jumpToFirst(){
		currentLocation=0;
		return slides.get(currentLocation);
	}
	
	public int currentSlide(){
		return currentLocation+1;
	}
	
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the defaultFont
	 */
	public String getDefaultFont() {
		return defaultFont;
	}

	/**
	 * @param defaultFont the defaultFont to set
	 */
	public void setDefaultFont(String defaultFont) {
		this.defaultFont = defaultFont;
	}

	/**
	 * @return the defaultSize
	 */
	public int getDefaultSize() {
		return defaultSize;
	}

	/**
	 * @param defaultSize the defaultSize to set
	 */
	public void setDefaultSize(int defaultSize) {
		this.defaultSize = defaultSize;
	}

	/**
	 * @return the defaultColor
	 */
	public int[] getDefaultColor() {
		return defaultColor;
	}

	/**
	 * Sets the default color for this slide show to use.  Values
	 * are clamped between 0 and 255
	 * @param defaultColor the defaultColor to set
	 */
	public void setDefaultColor(int r, int g, int b, int a) {
		if(r<0)r=0;
		if(g<0)g=0;
		if(b<0)b=0;
		if(a<0)a=0;
		if(r>255)r=255;
		if(g>255)g=255;
		if(b>255)b=255;
		if(a>255)a=255;
		defaultColor[0]=r;
		defaultColor[1]=g;
		defaultColor[2]=b;
		defaultColor[3]=a;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the authors
	 */
	public ArrayList<String> getAuthors() {
		return authors;
	}
}
