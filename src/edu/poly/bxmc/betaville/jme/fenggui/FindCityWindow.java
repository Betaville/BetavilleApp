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
package edu.poly.bxmc.betaville.jme.fenggui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.util.Color;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.search.GeoNamesSearchQuery;
import edu.poly.bxmc.betaville.search.GeoNamesSearchResult;
import edu.poly.bxmc.betaville.search.SearchResult;

/**
 * Window for performing a search to find a city.
 * @author Skye Book
 *
 */
public class FindCityWindow extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(FindCityWindow.class);
	
	private int targetWidth = 250;
	private int targetHeight = 150;
	
	private Container searchContainer;
	private TextEditor entry;
	private FixedButton search;
	
	private ScrollContainer results;
	private Container isc;
	
	// The geonames ID of the selected city feature (Code: P/Class: PPL)
	private int selectedID = -1;
	
	private ArrayList<ISelectionDeselectionListener> selectionListeners;

	/**
	 * 
	 */
	public FindCityWindow() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		getContentContainer().setSize(targetWidth, targetHeight-getTitleBar().getHeight());
		
		selectionListeners = new ArrayList<FindCityWindow.ISelectionDeselectionListener>();
		
		results = FengGUI.createWidget(ScrollContainer.class);
		results.setShowScrollbars(true);
		isc = FengGUI.createWidget(Container.class);
		isc.setLayoutManager(new RowExLayout(false));
		results.setInnerWidget(isc);
		
		searchContainer = FengGUI.createWidget(Container.class);
		searchContainer.setLayoutManager(new RowExLayout(true));
		entry = FengGUI.createWidget(TextEditor.class);
		entry.setText("Enter City Name...");
		searchContainer.addWidget(entry);
		search = FengGUI.createWidget(FixedButton.class);
		search.setText("search");
		search.addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				performSearch();
			}
		});
		searchContainer.addWidget(search);
		
		getContentContainer().addWidget(results, searchContainer);
	}
	
	private void performSearch(){
		
		logger.info("Doing search: "+FengUtils.getText(entry));
		
		selectedID=-1;
		
		// hand the search task off to another thread as it is dependent on a server response.
		SettingsPreferences.getThreadPool().submit(new Runnable() {
			
			public void run() {
				try {
					GeoNamesSearchQuery q = new GeoNamesSearchQuery();
					List<SearchResult> cities = q.citySearch(FengUtils.getText(entry));
					
					for(SearchResult result : cities){
						//ResultLabel l = FengGUI.createWidget(ResultLabel.class);
						ResultLabel l = new ResultLabel();
						l.configure((GeoNamesSearchResult)result);
						isc.addWidget(l);
					}
					
				} catch (Exception e) {
					logger.warn("Could not perform search with text from input", e);
				}
			}
		});
	}
	
	/**
	 * Gets the city currently selected from the results list
	 * @return The {@link GeoNamesSearchResult} representing the selected
	 * city of null if there is no city selected.
	 */
	public GeoNamesSearchResult getSelectedCity(){
		for(IWidget w : isc.getWidgets()){
			if(selectedID == ((ResultLabel)w).result.getGeoNamesID()){
				return ((ResultLabel)w).result;
			}
		}
		return null;
	}
	
	
	/**
	 * Adds a selection-deselection listener to this window
	 * @param listener
	 */
	public void addSelectionDeslectionListener(ISelectionDeselectionListener listener){
		selectionListeners.add(listener);
	}
	
	/**
	 * Removes a selection-deselection listener from this window
	 * @param listener
	 */
	public void removeSelectionDeslectionListener(ISelectionDeselectionListener listener){
		selectionListeners.add(listener);
	}
	
	/**
	 * Removes all selection-deselection listeners from this window
	 */
	public void removeAllSelectionDeslectionListeners(){
		selectionListeners.clear();
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("Find City");
		setSize(targetWidth, targetHeight);
	}

	public class ResultLabel extends Container{
		
		private GeoNamesSearchResult result;
		
		public ResultLabel(){
			setLayoutManager(new RowExLayout(true));
			
			addEventListener(EVENT_MOUSE, new IGenericEventListener() {
				
				public void processEvent(Object source, Event event) {
					handleEvent(event);
				}
			});
		}
		
		private void handleEvent(Event event){
			if(event instanceof MouseEnteredEvent){
				this.getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
			}
			else if(event instanceof MouseExitedEvent){
				this.getAppearance().removeAll();
			}
			else if(event instanceof MouseReleasedEvent){
				this.getAppearance().add(new PlainBackground(Color.BLACK));
				selectedID=result.getGeoNamesID();
				for(ISelectionDeselectionListener listener : selectionListeners){
					listener.resultSelected(result);
				}
			}
		}
		
		private void configure(GeoNamesSearchResult r){
			this.result=r;
			Label n = FengGUI.createWidget(Label.class);
			n.setText(result.getMainTitle());
			this.addWidget(n);
			
			//Label loc = FengGUI.createWidget(Label.class);
		}
	}
	
	public interface ISelectionDeselectionListener{
		/**
		 * Called when a city result is selected in the FindCityWindow
		 * @param The selected city
		 */
		public void resultSelected(GeoNamesSearchResult result);
		
		/**
		 * Called when a result is deselected
		 */
		public void resultDeselected();
	}
}
