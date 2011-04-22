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
package edu.poly.bxmc.betaville.jme.fenggui.tutorial;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ScrollContainer;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IWidgetListChangedListener;
import org.fenggui.event.WidgetListChangedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.jdom.JDOMException;

import com.centerkey.utils.BareBonesBrowserLaunch;

import edu.poly.bxmc.betaville.KioskMode;
import edu.poly.bxmc.betaville.ResourceLoader;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.xml.SlideLoader;
import edu.poly.bxmc.betaville.xml.TutorialListLoader;

/**
 * @author Skye Book
 *
 */
public class TutorialWindow extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(TutorialWindow.class);

	private int width=400;
	private int height=400;

	private Container splash;

	private Container bottomNavigation;
	
	private ScrollContainer introScrollContainer;
	private Label intro;
	
	private FixedButton moreInfo;
	private FixedButton backButton;
	private FixedButton nextButton;
	private FixedButton closeButton;

	private ArrayList<SlideDeck> slideDecks = new ArrayList<SlideDeck>();
	private int activeSlideDeck=-1;

	/**
	 * 
	 */
	public TutorialWindow() {
		super(true, true);
		//removeWidget(getTitleBar());
		getContentContainer().setSize(width, height-titleBar.getHeight());
		getContentContainer().setLayoutManager(new RowExLayout(false, 20));
		
		// control the appearance of the back and next buttons
		getContentContainer().addWidgetListChangedListener(new IWidgetListChangedListener() {
			public void widgetRemoved(Object sender,
					WidgetListChangedEvent widgetAddedEvent) {
				if(widgetAddedEvent.getWidget()[0].equals(splash)){
					bottomNavigation.addWidget(backButton, nextButton);
				}
			}

			public void widgetAdded(Object sender,
					WidgetListChangedEvent widgetAddedEvent) {
				if(widgetAddedEvent.getWidget()[0].equals(splash)){
					bottomNavigation.removeWidget(backButton, nextButton);
				}
			}
		});

		createNavigator();
		
		try {
			TutorialListLoader tl = new TutorialListLoader(ResourceLoader.loadResource("/data/tutorial"));
			for(URL url : tl.getTutoralList()){
				SlideLoader sl = new SlideLoader(url);
				sl.parse();
				slideDecks.add(sl.getSlideDeck());
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		createSplash();
		if(KioskMode.isInKioskMode()){
			createKioskWelcome();
			content.addWidget(intro);
		}
		content.addWidget(splash, bottomNavigation);
	}
	
	private void createKioskWelcome(){
		introScrollContainer = FengGUI.createWidget(ScrollContainer.class);
		intro = FengGUI.createWidget(Label.class);
		
		intro.setMultiline(true);
		intro.setWordWarping(true);
		
		intro.setText("Betaville is a massively participatory open-source platform for new ideas, discussion, and development " +
				"for the public realm.  New works of outdoor art, buildings, open spaces, or entire districts can be uploaded " +
				"from your favorite 3D modeling application.\n" +
				"\n" +
				"If you're seeing this in \"kiosk\" mode, you'll only be able to fly around, look at, and read information " +
				"panels and proposal forums as a guest.  When you get home, go to betaville.net to register and get started " +
				"as a full citizen of your world's next generation, or bxmc.poly.edu/betaville for in-depth information about " +
				"the project.\n" +
				"\n" +
				"Every living city is in Beta. Let's play.");
		
		//intro.setSize(width-10, height-getTitleBar().getHeight()-bottomNavigation.getHeight()-splash.getHeight());
		intro.setSize(width-10, 100);
		//introScrollContainer.setHeight(70);
		//introScrollContainer.setInnerWidget(intro);
	}

	private void createSplash(){
		splash = FengGUI.createWidget(Container.class);
		splash.setLayoutManager(new RowExLayout(false));

		for(final SlideDeck slideDeck : slideDecks){
			FixedButton button = FengGUI.createWidget(FixedButton.class);
			button.setText(slideDeck.getTitle());
			button.setWidth(button.getWidth()+10);
			button.addButtonPressedListener(new IButtonPressedListener() {
				public void buttonPressed(Object source, ButtonPressedEvent e) {
					activateSlideDeck(slideDecks.indexOf(slideDeck));
				}
			});
			splash.addWidget(button);
		}
	}

	private void activateSlideDeck(int slideDeckIndex){
		getContentContainer().removeAllWidgets();
		getContentContainer().addWidget(slideDecks.get(slideDeckIndex).jumpToFirst(), bottomNavigation);
		activeSlideDeck=slideDeckIndex;
		nextButton.setEnabled(!slideDecks.get(activeSlideDeck).isAtLastSlide());
		backButton.setEnabled(!slideDecks.get(activeSlideDeck).isAtFirstSlide());
	}
	
	private void returnToSplash(){
		logger.info("returning to splash screen");
		getContentContainer().removeAllWidgets();
		getContentContainer().addWidget(splash, bottomNavigation);
		activeSlideDeck=-1;
	}

	private void createNavigator(){
		bottomNavigation = FengGUI.createWidget(Container.class);
		bottomNavigation.setLayoutManager(new StaticLayout());

		int offset=5;
		moreInfo = FengGUI.createWidget(FixedButton.class);
		moreInfo.setText("More Info");
		moreInfo.setWidth(moreInfo.getWidth()+10);
		moreInfo.setXY(offset, 0);
		moreInfo.addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				BareBonesBrowserLaunch.openURL("http://betaville.net/wiki");
			}
		});

		backButton = FengGUI.createWidget(FixedButton.class);
		backButton.setText("Back");
		backButton.setWidth(backButton.getWidth()+10);
		backButton.setXY((width/2)-backButton.getWidth()-offset, 0);
		backButton.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(activeSlideDeck==-1) return;
				getContentContainer().removeAllWidgets();
				getContentContainer().addWidget(slideDecks.get(activeSlideDeck).back(), bottomNavigation);
				backButton.setEnabled(!slideDecks.get(activeSlideDeck).isAtFirstSlide());
				nextButton.setEnabled(!slideDecks.get(activeSlideDeck).isAtLastSlide());
			}
		});

		nextButton = FengGUI.createWidget(FixedButton.class);
		nextButton.setText("Next");
		nextButton.setWidth(nextButton.getWidth()+10);
		nextButton.setXY((width/2)+offset, 0);
		nextButton.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(activeSlideDeck==-1) return;
				logger.info("advancing slide deck " + activeSlideDeck);
				getContentContainer().removeAllWidgets();
				getContentContainer().addWidget(slideDecks.get(activeSlideDeck).next(), bottomNavigation);
				nextButton.setEnabled(!slideDecks.get(activeSlideDeck).isAtLastSlide());
				backButton.setEnabled(!slideDecks.get(activeSlideDeck).isAtFirstSlide());
			}
		});

		closeButton = FengGUI.createWidget(FixedButton.class);
		closeButton.setText("Close");
		closeButton.setWidth(closeButton.getWidth()+10);
		closeButton.setXY(width-closeButton.getWidth()-offset, 0);
		closeButton.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(activeSlideDeck!=-1) returnToSplash();
				else close();
			}
		});

		bottomNavigation.setSize(width, closeButton.getHeight());
		bottomNavigation.addWidget(moreInfo);
		bottomNavigation.addWidget(backButton, nextButton, closeButton);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("Welcome to Betaville");
		setSize(width, height);
	}

}
