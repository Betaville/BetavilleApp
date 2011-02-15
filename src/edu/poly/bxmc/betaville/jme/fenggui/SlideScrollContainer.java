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
package edu.poly.bxmc.betaville.jme.fenggui;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.IWidget;
import org.fenggui.event.Event;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.SizeChangedEvent;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Dimension;

import edu.poly.bxmc.betaville.jme.fenggui.listeners.ISlideScrollSpreadChangeListener;

/**
 * Can be used as either a horizontal or vertical sliding/scrolling
 * container.  This is a working replacement for what already exists
 * in FengGUI.  Initializes to vertical mode.
 * @author Skye Book
 *
 */
public class SlideScrollContainer extends Container {
	private static Logger logger = Logger.getLogger(SlideScrollContainer.class);
	private boolean horizontal = false;
	
	private Vector<ISlideScrollSpreadChangeListener> spreadChangedListeners;
	
	private Container holster;
	
	// the total size of all of the widgets in the
	// direction of interest (horizontal or vertical)
	private int totalSize=0;
	
	private int spread=0;
	
	// the percentage of total size viewable by this widget
	private int percentInView=100;
	
	private int startWidth;
	private int startHeight;

	/**
	 * 
	 */
	public SlideScrollContainer() {
		super(new StaticLayout());
		
		spreadChangedListeners = new Vector<ISlideScrollSpreadChangeListener>();
		startWidth=0;
		startHeight=0;
		
		holster = new Container(new StaticLayout());
		//holster = FengGUI.createWidget(Container.class);
		//holster.setLayoutManager(new StaticLayout());
		holster.setXY(0, 0);
		this.addWidget(holster);
	}
	
	public void addToSlideScroller(IWidget w){
		holster.addWidget(w);
		
		if(horizontal){
			w.setXY(totalSize, 0);
			totalSize+=w.getSize().getWidth();
			holster.setSize(totalSize, getHeight());
		}
		else{
			w.setXY(0, totalSize);
			totalSize+=w.getSize().getHeight();
			holster.setSize(getWidth(), totalSize);
		}
		
		recalculateSpread();
		
		w.addEventListener(EVENT_SIZECHANGED, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof SizeChangedEvent){
					recalculateTotalSize();
					recalculateSpread();
				}
			}
		});
		
		//logger.debug("Spread: " + spread);
	}
	
	/**
	 * @return the spread
	 */
	public int getSpread() {
		return spread;
	}

	/**
	 * @return the startWidth
	 */
	public int getStartWidth() {
		return startWidth;
	}
	
	/**
	 * @return the startHeight
	 */
	public int getStartHeight() {
		return startHeight;
	}

	public void setStartSize(int startWidth, int startHeight) {
		this.startWidth = startWidth;
		this.startHeight=startHeight;
	}

	public void clearHolster(){
		holster.removeAllWidgets();
		totalSize=0;
		spread=0;
		percentInView=100;
		holster.setXY(0, 0);
		holster.setSize(0, 0);
		setSize(startWidth, startHeight);
	}
	
	public Iterable<IWidget> getHolsterWidgets(){
		return holster.getWidgets();
	}
	
	public void recalculateTotalSize(){
		totalSize=0;
		
		for(IWidget w : holster.getWidgets()){
			if(horizontal)totalSize+=w.getSize().getWidth();
			else totalSize+=w.getSize().getHeight();
		}
	}
	
	/**
	 * @return the totalSize
	 */
	public int getTotalSize() {
		return totalSize;
	}

	private void recalculatePercentInView(){
		if(totalSize<getWidth()){
			percentInView=100;
			return;
		}
		percentInView = (getWidth()*100)/totalSize;
		if(percentInView>100) logger.warn("More than 100% in view: " + percentInView);
		//logger.debug("percent in view " + percentInView);
	}
	
	/**
	 * @return the percentInView
	 */
	public int getPercentInView() {
		return percentInView;
	}

	private void recalculateSpread(){
		if(horizontal) spread = totalSize-getWidth();
		else spread = totalSize-getHeight();
		
		recalculatePercentInView();
		
		for(ISlideScrollSpreadChangeListener listener : spreadChangedListeners){
			listener.spreadChanged(spread);
		}
	}
	
	/**
	 * where to move slide or scroll the container to
	 * @param value the value of how far to move, from 0 to 1
	 */
	public void moveTo(double value){
		if(spread<1){
			return;
		}
		
		if(horizontal) holster.setX((-1)*(int)(spread*value));
		else holster.setY((-1)*(int)(spread*value));
	}

	/**
	 * @return true if this is set to display horizontally
	 */
	public boolean isHorizontal() {
		return horizontal;
	}

	/**
	 * @param true if this is to be displayed horizontally,
	 * false for vertical.
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}
	
	/*
	 * Override the methods for setting size so we can always
	 * have an up-to-date spread calculation.
	 */
	
	public void setWidth(int width){
		super.setWidth(width);
		recalculateSpread();
	}
	
	public void setHeight(int height){
		super.setHeight(height);
		recalculateSpread();
	}
	
	public void setSize(Dimension dimension){
		super.setSize(dimension.clone());
		recalculateSpread();
	}
	
	public void setSize(int width, int height){
		super.setSize(width, height);
		recalculateSpread();
	}
	
	
	// Listeners
	
	public void addSpreadChangedListener(ISlideScrollSpreadChangeListener listener){
		spreadChangedListeners.add(listener);
	}
	
	public void removeSpreadChangedListener(ISlideScrollSpreadChangeListener listener){
		spreadChangedListeners.remove(listener);
	}
	
	public void removeAllSpreadChangedListeners(){
		spreadChangedListeners.clear();
	}
}
