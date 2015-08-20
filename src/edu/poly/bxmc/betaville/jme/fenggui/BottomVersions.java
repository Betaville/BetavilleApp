/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.Slider;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.PixmapBackground;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;

import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.CacheManager;
import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.flags.IFlagSelectionListener;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.UpdatingLabel;
import edu.poly.bxmc.betaville.jme.fenggui.listeners.ISlideScrollSpreadChangeListener;
import edu.poly.bxmc.betaville.jme.fenggui.listeners.ITweenFinishedListener;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.proposals.ILiveProposalChangedListener;
import edu.poly.bxmc.betaville.proposals.LiveProposalManager;

/**
 * @author Skye Book
 *
 */
public class BottomVersions extends Window {
	private static Logger logger = Logger.getLogger(BottomVersions.class);
	private int targetWidth;
	private int targetHeight=125;
	
	private Label versionsLabel;
	
	private SlideScrollContainer slideScrollContainer;
	private Slider slideScrollController;
	private ISliderMovedListener sliderListener;
	
	// Decorator keys that link to background colors for the clickable
	// containers in FengGUI
	private final String selectedDecoratorKey = "clicked";
	private final String rollOverDecoratorKey = "over";
	
	// Tweening time in milliseconds
	private int tweenTime = 1000;
	private boolean slidIn = false;
	
	private int loadedDesignID=-1;
	
	/**
	 * 
	 */
	public BottomVersions() {
		super(false, false);
		FengGUI.getTheme().setUp(this);
		removeWidget(getTitleBar());
		getContentContainer().setLayoutManager(new StaticLayout());
		targetWidth = DisplaySystem.getDisplaySystem().getWidth()-BottomProposals.targetWidth;
		setSize(targetWidth, targetHeight);
		
		versionsLabel = FengGUI.createWidget(Label.class);
		
		try {
			versionsLabel.setPixmap(new Pixmap(Binding.getInstance().getTexture("data/uiAssets/BottomWindows/versionsLabel.jpg")));
		} catch (IOException e){
			// If we can not load the texture, then just use a plain-text label
			logger.error("Could not load proposalsLabel texture", e);
			versionsLabel.setText(Labels.get(this.getClass().getSimpleName()+".proposals"));
		}
		versionsLabel.setXY(0, getHeight()-versionsLabel.getHeight());
		
		SceneScape.addFlagSelectionListener(new IFlagSelectionListener() {
			public void flagSelected(List<Design> rootDesigns) {
				slideScrollContainer.clearHolster();
			}
			
			public void flagDeselected(){
				slideOutOfScene();
			}
		});
		
		createVersionsContainer();
		
		getContentContainer().addWidget(versionsLabel, slideScrollContainer, slideScrollController);
	}
	
	private void createVersionsContainer(){
		slideScrollContainer = new SlideScrollContainer();
		slideScrollContainer.setHorizontal(true);
		slideScrollContainer.setStartSize(targetWidth-30, targetHeight-15);
		slideScrollContainer.setSize(slideScrollContainer.getStartWidth(), slideScrollContainer.getStartHeight());
		
		slideScrollController = FengGUI.createSlider(true);
		slideScrollController.setWidth(slideScrollContainer.getWidth() - versionsLabel.getWidth());
		slideScrollController.setXY(versionsLabel.getX()+versionsLabel.getWidth(), versionsLabel.getY()+(versionsLabel.getHeight()/2));
		
		sliderListener = (new ISliderMovedListener() {
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				slideScrollContainer.moveTo(slideScrollController.getValue());
			}
		});
		
		slideScrollContainer.addSpreadChangedListener(new ISlideScrollSpreadChangeListener() {
			public void spreadChanged(int spread) {
				// this is the correct value, i wish I could figure out why it won't apply in FengGUI
				//double sliderSize=(double)((slideScrollController.getWidth()*slideScrollContainer.getPercentInView()/100));
				//logger.debug("new slider size: " + sliderSize);
				//slideScrollController.setSize(sliderSize);
			}
		});
	}
	
	public void loadVersions(Design proposalDesign) throws UnknownHostException, IOException{
		if(proposalDesign.getID()==loadedDesignID) return;
		else{
			// if the proposal has changed, clear what is currently displayed
			slideScrollContainer.clearHolster();
			loadedDesignID = proposalDesign.getID();
		}
		
		int[] versionList = NetPool.getPool().getConnection().findVersionsOfProposal(proposalDesign.getID());
		
		slideScrollController.setValue(0);
		slideScrollController.addSliderMovedListener(sliderListener);
		
		SettingsPreferences.getCity().addDesign(proposalDesign);
		Container firstC = createVersionContainer(proposalDesign);
		slideScrollContainer.addToSlideScroller(firstC);
		
		if(versionList == null){
			logger.error("Finding the versions returned a null int[]");
			return;
		}
		
		for(int i=0; i<versionList.length; i++){
			Design version = NetPool.getPool().getConnection().findDesignByID(versionList[i]);
			if(version==null) continue;
			SettingsPreferences.getCity().addDesign(version);
			Container c = createVersionContainer(version);
			slideScrollContainer.addToSlideScroller(c);
		}
	}
	
	
	private Container createVersionContainer(final Design versionDesign){
		final Container clickableContainer = new Container(new StaticLayout());
		clickableContainer.setSize(305, 80);
		clickableContainer.getAppearance().add(selectedDecoratorKey, new PlainBackground(Color.BLACK));
		clickableContainer.getAppearance().add(rollOverDecoratorKey, new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
		clickableContainer.getAppearance().setEnabled(selectedDecoratorKey, false);
		clickableContainer.getAppearance().setEnabled(rollOverDecoratorKey, false);
		
		clickableContainer.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseEnteredEvent){
					clickableContainer.getAppearance().setEnabled(rollOverDecoratorKey, true);
				}
				else if(event instanceof MouseExitedEvent){
					clickableContainer.getAppearance().setEnabled(rollOverDecoratorKey, false);
				}
			}
		});

		final Label thumbnail = FengGUI.createWidget(Label.class);
		Pixmap px;
		PixmapBackground bg = null;
		try {
			//Pixmap px = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/buildings/test.png"));
			px = new Pixmap(Binding.getInstance().getTexture(CacheManager.getCachedThumbnailURL(versionDesign.getID())));
			bg = new PixmapBackground(px);
		} catch (IOException e) {
			logger.error("Using the default thumbnail image");
			try {
				px = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/buildings/test.png"));
				bg = new PixmapBackground(px);
			} catch (IOException e1) {
				logger.error("Could not load the default thumbnail image", e);
				thumbnail.setText(Labels.get(this.getClass().getSimpleName()+".image_not_found"));
			}
		}
		
		thumbnail.setSize(105, 70);
		thumbnail.setXY(5, 0);
		if(bg!=null){
			bg.setScaled(true);
			thumbnail.getAppearance().add("default", bg);
			thumbnail.getAppearance().setEnabled("default", true);
		}

		
		SettingsPreferences.getGUIThreadPool().submit(new Runnable() {
			public void run() {
				logger.info("Looking for thumbnail for design " + versionDesign.getID());
				
				try {
				
					if(CacheManager.getCacheManager().requestThumbnail(versionDesign.getID())){
					
						Label netnail = FengGUI.createWidget(Label.class);
						netnail.setSize(105, 70);
						netnail.setXY(5, 0);
						logger.info("netnail label created");
						logger.info("getting thumbnail from " + CacheManager.getCachedThumbnailURL(versionDesign.getID()).toString());
						Pixmap netpx = new Pixmap(Binding.getInstance().getTexture(CacheManager.getCachedThumbnailURL(versionDesign.getID())));
						PixmapBackground netbg = new PixmapBackground(netpx);
						logger.info("bg loaded");
						netnail.getAppearance().add("custom", netbg);
						netnail.getAppearance().setEnabled("custom", true);
						logger.info("netnail created");
						if(thumbnail.isInWidgetTree()) clickableContainer.removeWidget(thumbnail);
						thumbnail.getAppearance().removeAll();
						clickableContainer.addWidget(netnail);
						logger.info("netnail added to widget tree");
					}
				} catch (IOException e) {
					logger.error("Could not load the thumbnail image for the proposal, using default image", e);
				}
			}
		});
		

		
		Label name = FengGUI.createWidget(Label.class);
		name.setText(versionDesign.getName());
		name.setXY(thumbnail.getWidth()+5, thumbnail.getHeight()-name.getHeight());
		
		Label date = FengGUI.createWidget(Label.class);
		date.setText(versionDesign.getDateAdded());
		date.setXY(name.getX(), 0);
		
		Label user = FengGUI.createWidget(Label.class);
		user.setText(Labels.get("Generic.by")+": " + versionDesign.getUser());
		user.setXY(name.getX(), date.getHeight());
		
		Label description = FengGUI.createWidget(Label.class);
		description.setSize(clickableContainer.getWidth()-thumbnail.getWidth(), name.getY()-(user.getY()+user.getHeight()));
		description.setWordWarping(true);
		description.setMultiline(true);
		description.setText(versionDesign.getDescription());
		description.setXY(name.getX(), name.getY()-description.getHeight());
		
		final FixedButton show = FengGUI.createWidget(FixedButton.class);
		show.getAppearance().add("orange", new PlainBackground(new Color(252,58,0)));
		show.getAppearance().setEnabled("orange", false);
		if(LiveProposalManager.getInstance().isVersionOn(versionDesign.getID())) show.setText(Labels.get("Generic.hide"));
		else show.setText(Labels.get("Generic.show"));
		show.setWidth(show.getWidth()+5);
		show.setXY(clickableContainer.getWidth()-show.getWidth(), 0);
		show.addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				
				if(show.getText().equals(Labels.get("Generic.show"))){
					//show.removeGreen();
					//show.getAppearance().setEnabled("orange", true);
					
					
					// Create and start the loading label
					final UpdatingLabel loading = FengGUI.createWidget(UpdatingLabel.class);
					loading.setText(Labels.get("Generic.loading"));
					clickableContainer.addWidget(loading);
					loading.setXY(clickableContainer.getWidth()-(loading.getWidth()+10), show.getY()+(show.getHeight()*2));
					loading.start();
					
					SettingsPreferences.getThreadPool().execute(new Runnable() {
						
						@Override
						public void run() {
							// make the button un-clickable
							show.setEnabled(false);
							
							
							LiveProposalManager.getInstance().turnProposalOff(versionDesign.getSourceID());
							
							LiveProposalManager.getInstance().turnVersionOn(versionDesign);
							
							loading.stop();
							clickableContainer.removeWidget(loading);
							
							LiveProposalManager.getInstance().addProposalChangedListener(new ILiveProposalChangedListener() {
								
								public void isChanged(int rootProposalID) {
									show.setText(Labels.get("Generic.show"));
								}
							});
							
							// sets only this show button to say "hide"
							show.setText(Labels.get("Generic.hide"));
							
							// change the background to black
							turnOffBlackBackground();
							clickableContainer.getAppearance().setEnabled(selectedDecoratorKey, true);
							
							// re-enable the button
							show.setEnabled(true);
						}
					});
				}
				else{
					LiveProposalManager.getInstance().turnVersionOff(versionDesign.getID());
					
					turnOffBlackBackground();
					//show.replaceGreen();
					//show.getAppearance().setEnabled("orange", false);
					show.setText(Labels.get("Generic.show"));
				}
				
			}
		});
		
		clickableContainer.addWidget(thumbnail, name, description, date, user, show);
		return clickableContainer;
	}
	
	
	/**
	 * Disables all of the black backgrounds in the proposal container
	 */
	private void turnOffBlackBackground(){
		for(IWidget w : slideScrollContainer.getHolsterWidgets()){
			((Container)w).getAppearance().setEnabled("clicked", false);
		}
	}
	
	
	public void slideInToScene(){
		if(slidIn) return;
		FengUtils.tweenWidget(this, getX(), 0, tweenTime, 30);
		slidIn=true;
	}
	
	public void slideOutOfScene(){
		if(!slidIn) return;
		
		FengUtils.tweenWidget(this, getX(), getHeight()*-1, tweenTime, 30, new ITweenFinishedListener() {
			public void tweenComplete() {
				slideScrollContainer.clearHolster();
				loadedDesignID=-1;
				slideScrollController.removeSliderMovedListener(sliderListener);
			}
		});
		slidIn=false;
	}
	
	public boolean isSlidIn(){
		return slidIn;
	}
}
