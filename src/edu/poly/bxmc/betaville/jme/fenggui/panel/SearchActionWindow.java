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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.Key;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.event.key.KeyTypedEvent;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.text.content.factory.simple.TextStyle;
import org.fenggui.text.content.factory.simple.TextStyleEntry;
import org.fenggui.util.Color;

import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.search.GeoNamesSearchQuery;
import edu.poly.bxmc.betaville.search.LocationalSearchResult;
import edu.poly.bxmc.betaville.search.SearchQuery;
import edu.poly.bxmc.betaville.search.SearchResult;

/**
 * @author Skye Book
 *
 */
public class SearchActionWindow extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(SearchActionWindow.class);

	private int targetWidth=400;
	private int targetHeight=200;
	private int resultsWidthOffset=15;

	private Container typeEditorContainer;

	private ComboBox searchType;

	private TextEditor searchEditor;
	private String emptySearchText = Labels.get(SearchActionWindow.class, "prompt") + "...";

	private Container geonamesOptions;
	private CheckBox<Boolean> searchAll;
	private CheckBox<Boolean> exactMatch;

	private FixedButton search;

	private SearchQuery searchQuery;

	private ScrollContainer sc;

	private Container resultsContainer;

	private Color defaultTextColor = null;
	
	private ILocation rolledOverLocation = null;
	
	private Button goHere;

	public SearchActionWindow() {
		super(true, true);
		getContentContainer().setSize(targetWidth, targetHeight);
		getContentContainer().setLayoutManager(new RowExLayout(false));

		typeEditorContainer = FengGUI.createWidget(Container.class);
		typeEditorContainer.setLayoutManager(new StaticLayout());

		searchEditor = FengGUI.createWidget(TextEditor.class);
		searchEditor.setEmptyText(emptySearchText);
		searchEditor.setWidth(((targetWidth/3)*2)-10);
		searchEditor.setXY(5,0);

		searchEditor.addTextChangedListener(new ITextChangedListener() {
			public void textChanged(TextChangedEvent textChangedEvent){
				if(FengUtils.getText(searchEditor).isEmpty() || FengUtils.getText(searchEditor).equals(emptySearchText)){
					search.setEnabled(false);
				}
				else search.setEnabled(true);
			}
		});
		searchEditor.addKeyListener(new IKeyListener() {

			public void keyTyped(Object sender, KeyTypedEvent keyTypedEvent) {}

			public void keyReleased(Object sender, KeyReleasedEvent keyReleasedEvent) {
				if(keyReleasedEvent.getKeyClass().equals(Key.ENTER)){
					searchAction();
				}
			}
			public void keyPressed(Object sender, KeyPressedEvent keyPressedEvent) {}
		});

		searchType = FengGUI.createWidget(ComboBox.class);
		searchType.addItem(GeoNamesSearchQuery.SEARCH_IDENTIFIER);
		searchType.setXY(searchEditor.getX()+searchEditor.getWidth()+5, 0);
		searchType.setSize(targetWidth-15-searchEditor.getWidth(), searchEditor.getHeight());
		searchType.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(Object sender,
					SelectionChangedEvent selectionChangedEvent) {
				// TODO: change the options panel here and activate a different search query.
			}
		});
		searchQuery = new GeoNamesSearchQuery();

		typeEditorContainer.addWidget(searchType, searchEditor);

		createGeonamesOptions();

		search = FengGUI.createWidget(FixedButton.class);
		search.setText(Labels.get("Generic.search")+"...");
		search.setEnabled(false);
		search.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				searchAction();
			}
		});
		
		goHere = FengGUI.createWidget(Button.class);
		goHere.setText(Labels.get(this.getClass().getSimpleName()+".go_here"));
		goHere.setLayoutData(BorderLayoutData.EAST);
		goHere.addButtonPressedListener(new IButtonPressedListener() {

			@Override
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(rolledOverLocation!=null){
					// get location of target
					//Vector3f target = JME2MapManager.instance.locationToBetaville(((LocationalSearchResult) result).getLocation());
					Vector3f target = JME2MapManager.instance.locationToBetaville(rolledOverLocation);

					Camera camera = SceneGameState.getInstance().getCamera();
					Vector3f cameraLocation = camera.getLocation();
					cameraLocation.setX(target.x-Scale.fromMeter(50));
					cameraLocation.setZ(target.z-Scale.fromMeter(50));
					cameraLocation.setY(Scale.fromMeter(500));
					camera.update();
					camera.lookAt(target, Vector3f.UNIT_Y);
				}
			}
		});

		Container scrollHolder = FengGUI.createWidget(Container.class);
		scrollHolder.setLayoutManager(new StaticLayout());
		//scrollHolder.setSize(targetWidth, targetHeight-getTitleBar().getHeight()-geonamesOptions.getHeight()-search.getHeight());
		scrollHolder.setSize(targetWidth, getTitleBar().getX()-(search.getX()+searchEditor.getHeight()));
		sc = FengGUI.createWidget(ScrollContainer.class);
		sc.setSize(targetWidth-resultsWidthOffset, targetHeight-getTitleBar().getHeight()-geonamesOptions.getHeight()-search.getHeight());
		logger.info("predicted scroller height: " + (targetHeight-getTitleBar().getHeight()-geonamesOptions.getHeight()-search.getHeight()));
		sc.setShowScrollbars(true);
		resultsContainer = FengGUI.createWidget(Container.class);
		resultsContainer.setLayoutManager(new RowExLayout(false));
		sc.setInnerWidget(resultsContainer);
		//sc.layout();
		scrollHolder.addWidget(sc);
		getContentContainer().addWidget(scrollHolder);
		getContentContainer().addWidget(typeEditorContainer);
		getContentContainer().addWidget(geonamesOptions);
		getContentContainer().addWidget(search);
	}

	private void searchAction(){
		// hand the search task off to another thread as it is dependent on a server response.
		SettingsPreferences.getThreadPool().submit(new Runnable() {
			public void run() {
				// clear the search results in the scene
				SceneGameState.getInstance().clearSearchDisplay();
				if(searchType.getSelectedValue().equalsIgnoreCase(GeoNamesSearchQuery.SEARCH_IDENTIFIER)){
					try {
						search.setText(Labels.get(SearchActionWindow.class.getSimpleName()+".searching")+"...");
						List<SearchResult> results=new ArrayList<SearchResult>();
						// process search options and perform searches
						if(searchAll.isSelected()){
							logger.info("full field search");
							results = ((GeoNamesSearchQuery)searchQuery).fullFieldSearch(FengUtils.getText(searchEditor));
						}
						else if(exactMatch.isSelected()){
							logger.info("exact match search");
							results = ((GeoNamesSearchQuery)searchQuery).searchNameField((FengUtils.getText(searchEditor)), true);
						}
						else{
							logger.info("name field search");
							results = ((GeoNamesSearchQuery)searchQuery).searchNameField((FengUtils.getText(searchEditor)), false);
						}
						// search post-processing
						setTitle(MessageFormat.format(Labels.get(this.getClass().getSimpleName()+".results_title"), results.size()));
						search.setText(Labels.get("Generic.search")+"...");

						resultsContainer.removeAllWidgets();
						for(SearchResult result : results){
							resultsContainer.addWidget(createResult(result));
							layout();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}

	private Container createResult(final SearchResult result){
		final Container c = FengGUI.createWidget(Container.class);
		c.setLayoutManager(new BorderLayout());
		c.setWidth(targetWidth-resultsWidthOffset);

		Container left = FengGUI.createWidget(Container.class);
		left.setLayoutManager(new BorderLayout());
		left.setLayoutData(BorderLayoutData.WEST);

		final Label name = FengGUI.createWidget(Label.class);
		name.setText(result.getMainTitle());
		name.setLayoutData(BorderLayoutData.WEST);
		c.setHeight(name.getHeight());
		left.addWidget(name);
		
		c.setHeight(goHere.getHeight());

		if(result instanceof LocationalSearchResult){
			Label location = FengGUI.createWidget(Label.class);
			location.setText(((LocationalSearchResult)result).getLocation().getGPS().getLatitude()+", "+((LocationalSearchResult)result).getLocation().getGPS().getLongitude());
			location.setLayoutData(BorderLayoutData.EAST);
			left.addWidget(location);
			SceneGameState.getInstance().addSearchResult(((LocationalSearchResult)result).getLocation(), result.getWebLink());
		}

		c.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			public void processEvent(Object source, Event event) {
				if(event instanceof MouseEvent){
					if(event instanceof MouseEnteredEvent){
						if(!isMouseOverButton(goHere)){
							if(goHere.isInWidgetTree()){
								((Container)goHere.getParent()).removeWidget(goHere);
							}
							c.addWidget(goHere);
							c.getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
							if(defaultTextColor==null) defaultTextColor=name.getAppearance().getStyle(TextStyle.DEFAULTSTYLEKEY).getTextStyleEntry(TextStyleEntry.DEFAULTSTYLESTATEKEY).getColor();
							name.getAppearance().getStyle(TextStyle.DEFAULTSTYLEKEY).getTextStyleEntry(TextStyleEntry.DEFAULTSTYLESTATEKEY).setColor(Color.YELLOW);
							// set the scene object:
							if(result instanceof LocationalSearchResult){
								SceneGameState.getInstance().setSingledSearchResult(result.getWebLink());
								rolledOverLocation = ((LocationalSearchResult)result).getLocation();
							}
						}
					}
					else if(event instanceof MouseExitedEvent){
						if(!isMouseOverButton(goHere)){
							if(goHere.isInWidgetTree()){
								((Container)goHere.getParent()).removeWidget(goHere);
							}
							c.getAppearance().removeAll();
							/* null check required because its possible that an exit event can happen before an
							 * enter event (where the default color is assigned). i.e: mouse is over result position
							 * when they are first displayed */
							if(defaultTextColor!=null)name.getAppearance().getStyle(TextStyle.DEFAULTSTYLEKEY).getTextStyleEntry(TextStyleEntry.DEFAULTSTYLESTATEKEY).setColor(defaultTextColor);
							else name.getAppearance().getStyle(TextStyle.DEFAULTSTYLEKEY).getTextStyleEntry(TextStyleEntry.DEFAULTSTYLESTATEKEY).setColor(Color.WHITE);
							
							rolledOverLocation = null;
						}
					}
					else if(event instanceof MouseReleasedEvent){
						// TODO: Show individual result popup
					}
				}
			}
		});

		c.addWidget(left);
		return c;
	}

	private boolean isMouseOverButton(Button button){
		int mouseX = MouseInput.get().getXAbsolute();
		int mouseY = MouseInput.get().getYAbsolute();

		return (mouseX > button.getDisplayX() &&
				mouseX < button.getDisplayX()+button.getWidth()
				&& mouseY > button.getDisplayY() &&
				mouseY < button.getDisplayY()+button.getHeight());
	}

	private void createGeonamesOptions(){
		geonamesOptions = FengGUI.createWidget(Container.class);
		geonamesOptions.setLayoutManager(new RowExLayout(true, 5));

		searchAll = FengGUI.createCheckBox();
		searchAll.setText(Labels.get(this.getClass().getSimpleName()+".search_all"));
		searchAll.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(Object sender, SelectionChangedEvent selectionChangedEvent) {
				if(searchAll.isSelected()) exactMatch.setEnabled(false);
				else exactMatch.setEnabled(true);
			}
		});
		exactMatch = FengGUI.createCheckBox();
		exactMatch.setText(Labels.get(this.getClass().getSimpleName()+".exact_match_required"));
		searchAll.setSelected(true);
		geonamesOptions.addWidget(searchAll);
		// We should rethink the logic of this option a bit, or at least test that it works
		//geonamesOptions.addWidget(exactMatch);
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);
		setExpandable(false);
	}

}
