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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.Event;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.flags.IFlagSelectionListener;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.listeners.ITweenFinishedListener;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class BottomProposals extends Window {
	private static Logger logger = Logger.getLogger(BottomProposals.class);
	public static int targetWidth=330;
	public static int targetHeight = 125;
	private Label proposalsLabel;
	
	// Decorator keys that link to background colors for the clickable
	// containers in FengGUI
	private final String selectedDecoratorKey = "clicked";
	private final String rollOverDecoratorKey = "over";
	
	// Tweening time in milliseconds
	private int tweenTime = 1000;
	private boolean slidIn = false;
	
	private int proposalNameCharLimit = 32;
	
	/**
	 * Contains all of the clickable containers.  Must only have containers
	 * as direct children.
	 */
	private Container proposalContainer;

	/**
	 * 
	 */
	public BottomProposals() {
		super(false, false);
		FengGUI.getTheme().setUp(this);
		removeWidget(getTitleBar());
		getContentContainer().setLayoutManager(new StaticLayout());
		setSize(targetWidth, targetHeight);
		
		proposalsLabel = FengGUI.createWidget(Label.class);
		try {
			proposalsLabel.setPixmap(new Pixmap(Binding.getInstance().getTexture("data/uiAssets/BottomWindows/proposalsLabel.jpg")));
		} catch (IOException e){
			// If we can not load the texture, then just use a plain-text label
			logger.error("Could not load proposalsLabel texture", e);
			proposalsLabel.setText("PROPOSALS");
		}
		proposalsLabel.setXY(getWidth()-proposalsLabel.getWidth(), getHeight()-proposalsLabel.getHeight());

		SceneScape.addFlagSelectionListener(new IFlagSelectionListener() {
			public void flagSelected(Design rootDesign) {
				if(slidIn) proposalContainer.removeAllWidgets();
				loadProposals(rootDesign);
			}

			public void flagDeselected() {
				slideOutOfScene();
			}
		});
		
		createProposalContainer();
		
		getContentContainer().addWidget(proposalsLabel, proposalContainer);
	}
	
	private void createProposalContainer(){
		proposalContainer = FengGUI.createWidget(Container.class);
		proposalContainer.setLayoutManager(new StaticLayout());
		proposalContainer.setXY(0, 0);
		proposalContainer.setSize(targetWidth-30, targetHeight-15);
	}

	private void loadProposals(Design rootDesign){
		final int[] proposalList = NetPool.getPool().getConnection().findAllProposals(rootDesign.getID());
		if(proposalList == null){
			logger.error("Finding the proposals returned a null int[]");
			return;
		}

		slideInToScene();
		SettingsPreferences.getThreadPool().submit(new Runnable() {
			public void run() {
				int heightToUse=0;
				for(int i=0; i<proposalList.length; i++){
					Design p = NetPool.getPool().getConnection().findDesignByID(proposalList[i]);
					if(p==null) continue;
					SceneScape.getCity().addDesign(p);
					Container c = createClickableContainer(p);
					c.setXY(0, heightToUse);
					heightToUse+=c.getHeight();
					proposalContainer.addWidget(c);
				}
			}
		});
	}

	/**
	 * Creates a clickable container that shows the current proposals for an area.
	 * TODO: Add a tooltip for names that are too long (that would then show the whole name)
	 * @param proposalDesign
	 * @return
	 */
	private Container createClickableContainer(final Design proposalDesign){
		final Container clickableContainer = new Container(new StaticLayout());
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
				else if(event instanceof MouseReleasedEvent){
					GUIGameState.getInstance().getVersionsWindow().loadVersions(proposalDesign);
					GUIGameState.getInstance().getVersionsWindow().slideInToScene();
					
					// change the background to black
					turnOffBlackBackground();
					clickableContainer.getAppearance().setEnabled(selectedDecoratorKey, true);
				}
			}
		});
		
		Label propLabel = FengGUI.createWidget(Label.class);
		Label userLabel = FengGUI.createWidget(Label.class);
		
		String textToUse = proposalDesign.getName();
		
		// If the name is too long, reduce it to something manageable
		if(textToUse.length()>proposalNameCharLimit){
			textToUse = textToUse.substring(0, proposalNameCharLimit)+"...";
		}
		propLabel.setText(textToUse);
		
		userLabel.setText(proposalDesign.getUser());
		
		clickableContainer.setSize(proposalContainer.getWidth(), propLabel.getHeight()+5);
		
		propLabel.setXY(0, 0);
		userLabel.setXY(clickableContainer.getWidth()-userLabel.getWidth(), 0);
		
		clickableContainer.addWidget(propLabel, userLabel);
		return clickableContainer;
	}
	
	/**
	 * Disables all of the black backgrounds in the proposal container
	 */
	private void turnOffBlackBackground(){
		for(IWidget w : proposalContainer.getWidgets()){
			((Container)w).getAppearance().setEnabled("clicked", false);
		}
	}
	
	public void slideInToScene(){
		if(slidIn) return;
		FengUtils.tweenWidget(this, 0, 0, tweenTime, 30);
		slidIn=true;
	}
	
	public void slideOutOfScene(){
		if(!slidIn) return;
		FengUtils.tweenWidget(this, 0, getHeight()*-1, tweenTime, 30, new ITweenFinishedListener() {
			public void tweenComplete() {
				proposalContainer.removeAllWidgets();
			}
		});
		slidIn=false;
	}
	
	public boolean isSlidIn(){
		return slidIn;
	}
}
