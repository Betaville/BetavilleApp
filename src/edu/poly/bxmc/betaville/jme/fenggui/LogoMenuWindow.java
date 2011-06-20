/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui;


import com.centerkey.utils.BareBonesBrowserLaunch;

import org.apache.log4j.Logger;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.composite.Window;
import org.fenggui.event.Event;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.util.Color;

import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;


/**
 * @author Jarred Humphrey
 *
 */
public class LogoMenuWindow extends Window implements IBetavilleWindow {

	private static Logger logger = Logger.getLogger(LogoMenuWindow.class);
	private static boolean exists = false;

	private Label exit;
	private Label about;
	private Label ourSite;
	private String aboutName = "About Betaville";
	private String aboutMessage = "Betaville is open-source software. Copyright 2008-2011 Brooklyn eXperimental Media Center.\r\n http://betaville.net | http://bxmc.poly.edu | http://betaville.googlecode.com";

	final int height = 72;
	final int width = 125;
	final int finalX = 0;
	final int finalY = Binding.getInstance().getCanvasHeight()-98;
	final int initX = -(width);
	final int initY = finalY;

	public LogoMenuWindow() {
		super(true, true);
	}

	private void initLabels(){
		exit = FengGUI.createWidget(Label.class);
		exit.setText("Exit Application");
		exit.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseEnteredEvent){
					FengUtils.setAppearanceTextStyleColor(exit, Color.WHITE);
					FengUtils.setAppearanceTextStyleColor(about, Color.WHITE_HALF_TRANSPARENT);
					FengUtils.setAppearanceTextStyleColor(ourSite, Color.WHITE_HALF_TRANSPARENT);
				}
				else if(event instanceof MouseExitedEvent){
					FengUtils.setAppearanceTextStyleColor(exit, Color.WHITE_HALF_TRANSPARENT);
				}
				else if(event instanceof MouseReleasedEvent){
					
					SceneGameState.getInstance().getSceneController().exitAction();
					TopSelectionWindow.removeLogoMenu();
					FengUtils.setAppearanceTextStyleColor(exit, Color.WHITE_HALF_TRANSPARENT);
					FengUtils.setAppearanceTextStyleColor(about, Color.WHITE_HALF_TRANSPARENT);
					FengUtils.setAppearanceTextStyleColor(ourSite, Color.WHITE_HALF_TRANSPARENT);
				}
			}
		});
		about = FengGUI.createWidget(Label.class);
		about.setText("About BetaVille");
		about.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseEnteredEvent){
					FengUtils.setAppearanceTextStyleColor(about, Color.WHITE);
					FengUtils.setAppearanceTextStyleColor(exit, Color.WHITE_HALF_TRANSPARENT);
					FengUtils.setAppearanceTextStyleColor(ourSite, Color.WHITE_HALF_TRANSPARENT);
				}
				else if(event instanceof MouseExitedEvent){
					FengUtils.setAppearanceTextStyleColor(about, Color.WHITE_HALF_TRANSPARENT);
				}
				else if(event instanceof MouseReleasedEvent){
					FengUtils.showNewDismissableWindow(aboutName, aboutMessage, "ok", true);
					TopSelectionWindow.removeLogoMenu();
					FengUtils.setAppearanceTextStyleColor(exit, Color.WHITE_HALF_TRANSPARENT);
					FengUtils.setAppearanceTextStyleColor(about, Color.WHITE_HALF_TRANSPARENT);
					FengUtils.setAppearanceTextStyleColor(ourSite, Color.WHITE_HALF_TRANSPARENT);
				}
			}
		});
		ourSite = FengGUI.createWidget(Label.class);
		ourSite.setText("Our Site");
		ourSite.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseEnteredEvent){
					FengUtils.setAppearanceTextStyleColor(ourSite, Color.WHITE);
					FengUtils.setAppearanceTextStyleColor(exit, Color.WHITE_HALF_TRANSPARENT);
					FengUtils.setAppearanceTextStyleColor(about, Color.WHITE_HALF_TRANSPARENT);
				}
				else if(event instanceof MouseExitedEvent){
					FengUtils.setAppearanceTextStyleColor(ourSite, Color.WHITE_HALF_TRANSPARENT);
				}
				else if(event instanceof MouseReleasedEvent){
					BareBonesBrowserLaunch.openURL("http://bxmc.poly.edu/betaville");
					TopSelectionWindow.removeLogoMenu();
					FengUtils.setAppearanceTextStyleColor(exit, Color.WHITE_HALF_TRANSPARENT);
					FengUtils.setAppearanceTextStyleColor(about, Color.WHITE_HALF_TRANSPARENT);
					FengUtils.setAppearanceTextStyleColor(ourSite, Color.WHITE_HALF_TRANSPARENT);
				}
			}
		});
	}
	
	
	public boolean exists(){
		return exists;
	}

	public void setExists(boolean e){
		exists = e;
	}

	public void finishSetup() {
		FengGUI.getTheme().setUp(this);
		removeWidget(getTitleBar());
		setSize(width, height);
		setXY(initX,initY);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		initLabels();
		getContentContainer().addWidget(about);
		getContentContainer().addWidget(ourSite);
		getContentContainer().addWidget(exit);
	}
}

