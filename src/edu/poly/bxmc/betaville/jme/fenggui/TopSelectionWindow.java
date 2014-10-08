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
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;
import org.fenggui.util.Dimension;

import com.centerkey.utils.BareBonesBrowserLaunch;
import com.jme.math.FastMath;
import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.KioskMode;
import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.listeners.ReportBugListener;
import edu.poly.bxmc.betaville.jme.fenggui.panel.CityPanel;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Design;

/**
 * @author Skye Book
 */
public class TopSelectionWindow extends Window{
	private static Logger logger = Logger.getLogger(TopSelectionWindow.class);

	private static final float COMPASS_HALF_OPENING_ANGLE = FastMath.QUARTER_PI;

	// clickable logo that goes to the betaville website
	private Label logoLabel;
	private Pixmap logoPlain;
	private Pixmap logoHover;
	private Label compassLabel;
	/**
	 * Compass pixmap (compass-texture excerpt)
	 */
	private Pixmap compassMap;
	/**
	 * Size of the compass label:<br/> 
	 * <code>compass-texture.width / ( PI / {@link #COMPASS_HALF_OPENING_ANGLE} )</code> 
	 */
	private Dimension compassLabelSize;
	/**
	 * Pixels necessary to display the whole compass 360 degrees or 2PI 
	 */
	private int compassPixFor2PI;

	// identifier for what this container does
	private Label selectionLabel;

	// The name of the selected design, or the contents of emptyText if
	// nothing is selected
	private Label nameLabel;
	private String emptyText = "Nothing Selected";
	public static LogoMenuWindow logoMenuWindow = FengGUI.createWidget(LogoMenuWindow.class);
	private Container nameBGContainer;
	//private Container menuContainer;

	// A clickable link to the URL included in the design data
	private Label urlLabel;
	private String urlToLaunch = null;

	// displays the amount of users who like this item
	private FixedButton faveButton;

	private ISpatialSelectionListener changeListener;

	private FixedButton bugButton;

	private FixedButton panelButton;
	private CityPanel cityPanel;

	int height = 24;

	/**
	 * 
	 */
	public TopSelectionWindow() {
		super(false, false);
		getContentContainer().setLayoutManager(new StaticLayout());
		FengGUI.getTheme().setUp(this);
		removeWidget(getTitleBar());
		setSize(Binding.getInstance().getCanvasWidth(), height);

		initLabels();
		setupCityPanel();
		try {
			functionalitySetup();
		} catch (IOException e) {
			logger.error("Unable to load textures for top panel", e);
		}
		setPositioning();
		
		// only show the Logo and Bug buttons if we are not in kiosk mode
		if(!KioskMode.isInKioskMode()){
			getContentContainer().addWidget(logoLabel,  bugButton);
		}
		getContentContainer().addWidget(nameBGContainer, /*compassLabel,*/ urlLabel);
	}

	private void initLabels(){
		logoLabel = FengGUI.createWidget(Label.class);
		compassLabel = FengGUI.createWidget(Label.class);
		selectionLabel = FengGUI.createWidget(Label.class);
		nameBGContainer = FengGUI.createWidget(Container.class);
		nameBGContainer.getAppearance().add(new PlainBackground(Color.BLACK));
		nameBGContainer.setLayoutManager(new StaticLayout());
		nameBGContainer.setHeight(height);
		nameLabel = FengGUI.createWidget(Label.class);
		FengUtils.setAppearanceTextStyleWhiteColor(nameLabel);
		nameLabel.getAppearance().add(new PlainBackground(Color.BLACK));
		nameBGContainer.addWidget(nameLabel);
		urlLabel = FengGUI.createWidget(Label.class);
		faveButton=FengGUI.createWidget(FixedButton.class);
		bugButton = FengGUI.createWidget(FixedButton.class);
		bugButton.setText(Labels.get(this.getClass(), "feedback"));
		bugButton.setWidth(bugButton.getWidth()+10);
		bugButton.setXY(Binding.getInstance().getCanvasWidth()-bugButton.getWidth(), -1);
		logoMenuWindow.finishSetup();
		clearLabels(false);
	}
	
	private void setupCityPanel(){
		// Build admin panel if necessary
		panelButton = FengGUI.createWidget(FixedButton.class);
		panelButton.setText(Labels.get(CityPanel.class, "title"));
		panelButton.setWidth(panelButton.getWidth()+10);
		
		// nudge the city panel over if we are not in Kiosk mode
		if(!KioskMode.isInKioskMode()) panelButton.setXY(bugButton.getX()-5-panelButton.getWidth(), -1);
		else panelButton.setXY(Binding.getInstance().getCanvasWidth()-bugButton.getWidth(), -1);
		
		panelButton.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!cityPanel.isInWidgetTree()){
					GUIGameState.getInstance().getDisp().addWidget(cityPanel);
				}
				else{
					GUIGameState.getInstance().getDisp().removeWidget(cityPanel);
				}
			}
		});

		getContentContainer().addWidget(panelButton);

		cityPanel = FengGUI.createWidget(CityPanel.class);
		cityPanel.finishSetup();
		cityPanel.setXY(Binding.getInstance().getCanvasWidth()-cityPanel.getWidth(), Binding.getInstance().getCanvasHeight()-getHeight()-cityPanel.getHeight());
	}

	private void functionalitySetup() throws IOException{

		// create the listener that controls the information being displayed
		changeListener = new ISpatialSelectionListener() {
			public void designSelected(Spatial spatial, Design design){
				setLabelText(design);
				if(!design.getURL().toLowerCase().equals("none")){
					urlToLaunch = design.getURL();
					urlLabel.setText(urlToLaunch);
				}
				else{
					urlLabel.setText("");
				}
			}

			public void selectionCleared(Design previousDesign) {
				faveButton.setText("");
				if(faveButton.isInWidgetTree()) getContentContainer().removeWidget(faveButton);
				clearLabels(true);
			}
		};

		SceneScape.addSelectionListener(changeListener);
		logoPlain = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/TopWindow/betavilleLogo.jpg"));
		logoHover = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/TopWindow/betavilleLogoHover.jpg"));

		logoLabel.setPixmap(logoPlain);
		//		compassLabel.setPixmap(compassMap);
		logoLabel.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseEnteredEvent){
					logoLabel.setPixmap(logoHover);
				}
				else if(event instanceof MouseExitedEvent){
					logoLabel.setPixmap(logoPlain);
				}
				else if(event instanceof MouseReleasedEvent){
					//BareBonesBrowserLaunch.openURL("http://betaville.net");
					if (!logoMenuWindow.exists()){
						setLogoMenu();
					} else{
						removeLogoMenu();
					}
				}
			}
		});

		compassMap			= new Pixmap(Binding.getInstance().getTexture("data/uiAssets/TopWindow/compass.png"));
		// Number of segments expected in the texture
		int segments		= (int)(FastMath.PI / COMPASS_HALF_OPENING_ANGLE) + 1;
		compassLabelSize	= new Dimension(compassMap.getWidth() / segments, compassMap.getHeight());
		compassPixFor2PI	= compassLabelSize.getWidth() * 4;
		compassLabel.setPixmap(compassMap);

		urlLabel.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseReleasedEvent){
					if(urlToLaunch!=null) BareBonesBrowserLaunch.openURL(urlToLaunch);
				}
			}
		});

		bugButton.addButtonPressedListener(new ReportBugListener());
	}

	public static void setLogoMenu(){
		if(!logoMenuWindow.exists()){
			logoMenuWindow = FengGUI.createWidget(LogoMenuWindow.class);
			logoMenuWindow.finishSetup();
			GUIGameState.getInstance().getDisp().addWidget(logoMenuWindow);
			FengUtils.tweenWidget(logoMenuWindow, logoMenuWindow.finalX, logoMenuWindow.finalY, 500, 10);
			logoMenuWindow.setExists(true);
		}else{
			logoMenuWindow = FengGUI.createWidget(LogoMenuWindow.class);
			logoMenuWindow.finishSetup();
			FengUtils.tweenWidget(logoMenuWindow, logoMenuWindow.finalX, logoMenuWindow.finalY, 500, 10);
		}
	}
	public static void removeLogoMenu(){
		try{
			FengUtils.tweenWidget(logoMenuWindow, logoMenuWindow.initX, logoMenuWindow.initY, 500, 10);
			//GUIGameState.getInstance().getDisp().removeWidget(logoMenuWindow);
			logoMenuWindow.setExists(false);
		} catch(NullPointerException e){
			logger.assertLog(true, "LogoMenuWindow removed but with NullPointerException");
		}
	}
	
	public void forceUpdate(){
		if(!SceneScape.isTargetSpatialEmpty()){
			setLabelText(SceneScape.getPickedDesign());
			if(!SceneScape.getPickedDesign().getURL().toLowerCase().equals("none")){
				urlToLaunch = SceneScape.getPickedDesign().getURL();
				urlLabel.setText(urlToLaunch);
			}
		}
		else{
			faveButton.setText("");
			urlLabel.setText("");
			setLabelText(SceneScape.getPickedDesign());
		}
	}

	private void setLabelText(Design design){
		nameLabel.setText(design.getName());

		nameLabel.setSizeToMinSize();
		nameBGContainer.setWidth(nameLabel.getWidth()+15);
		updateFaveLabel(design.getFavedBy());
		setPositioning();
	}

	private void updateFaveLabel(List<String> faveList){
		if(faveList.size()==0){
			if(faveButton.isInWidgetTree()) getContentContainer().removeWidget(faveButton);
			faveButton.setText("");
		}
		else{
			if(!faveButton.isInWidgetTree()) getContentContainer().addWidget(faveButton);
			if(faveList.size()==1) faveButton.setText(faveList.size() + " loves it!");
			else faveButton.setText(faveList.size() + " love it!");
		}
	}

	private void clearLabels(boolean setPositioning){
		nameLabel.setText(emptyText);
		nameLabel.setSizeToMinSize();
		nameBGContainer.setWidth(nameLabel.getWidth()+15);
		faveButton.setText("");
		if(setPositioning)setPositioning();
	}

	private void setPositioning(){
		// logo label should be on the left edge
		logoLabel.setXY(-5, -5);
		compassLabel.setXY(Binding.getInstance().getCanvasWidth() - (compassLabel.getWidth()/3)-panelButton.getWidth()-bugButton.getWidth()-15, -5);
		// the selection label sits directly to the right of the logo label
		if(!KioskMode.isInKioskMode()) selectionLabel.setXY(logoLabel.getWidth()-11, -5);
		else selectionLabel.setXY(0, -5);

		// the name label is to the right of the selection label (with some padding)
		nameLabel.setXY(5, 3);
		nameBGContainer.setXY(selectionLabel.getX()+selectionLabel.getWidth()+14, 0);

		// the url label (when enabled) is to the right of the name label (with some padding)
		urlLabel.setXY(nameBGContainer.getX()+nameBGContainer.getWidth()+10, 5);

		faveButton.setXY((Binding.getInstance().getCanvasWidth()/2)-(faveButton.getWidth()/2), -1);
	}

	/**
	 * Updates the compass label according to the specified angle.
	 * @param currentAngle An angle, in radians, 
	 * either relative (0 to +/-PI) or absolute (0 to 2PI)
	 * @author Peter Schulz
	 */
	public void updateCompass(double currentAngle) {
		// Shift angle by half of the opening angle
		currentAngle -= COMPASS_HALF_OPENING_ANGLE;

		// Convert to absolute angle between 0 and 2PI
		if (currentAngle < 0) 
			currentAngle += FastMath.TWO_PI;

		// Calculate offset into texture
		int offset = (int)(compassPixFor2PI * ( currentAngle / FastMath.TWO_PI));
		// Update pixmap with new offset
		compassLabel.setPixmap(new Pixmap(compassMap.getTexture(), offset, 0, 
				compassLabelSize.getWidth(), compassLabelSize.getHeight()));
	}

	public CityPanel getCityPanel(){
		return cityPanel;
	}

}
