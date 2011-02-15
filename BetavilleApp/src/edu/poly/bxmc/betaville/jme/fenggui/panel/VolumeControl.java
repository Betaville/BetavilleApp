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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.fenggui.FengGUI;
import org.fenggui.Slider;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.event.mouse.IMouseListener;
import org.fenggui.event.mouse.MouseClickedEvent;
import org.fenggui.event.mouse.MouseDoubleClickedEvent;
import org.fenggui.event.mouse.MouseDraggedEvent;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.event.mouse.MouseMovedEvent;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.event.mouse.MouseWheelEvent;
import org.fenggui.layout.RowExLayout;

import edu.poly.bxmc.betaville.jme.fenggui.panel.CityPanel.ICityPanelClosedListener;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.xml.PreferenceWriter;

/**
 * @author Skye Book
 *
 */
public class VolumeControl extends PanelAction implements ICityPanelClosedListener{
	private static final Logger logger = Logger.getLogger(VolumeControl.class);
	
	private Window volumeWindow;

	/**
	 * @param name
	 * @param description
	 * @param buttonTitle
	 * @param ruleToSet
	 * @param minimumUserLevel
	 * @param listener
	 */
	public VolumeControl() {
		super("Volume", "Controls the volume", "Volume", AvailabilityRule.ALWAYS, UserType.MEMBER,null);
		initializeWindow();
		button.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e){
				if(volumeWindow.isInWidgetTree()){
					GUIGameState.getInstance().getDisp().removeWidget(volumeWindow);
				}
				else{
					GUIGameState.getInstance().getDisp().addWidget(volumeWindow);
					volumeWindow.setXY(GUIGameState.getInstance().getTopSelectionWindow().getCityPanel().getDisplayX()-volumeWindow.getWidth(), GUIGameState.getInstance().getTopSelectionWindow().getCityPanel().getDisplayY());
				}
			}
		});
	}
	
	private void initializeWindow(){
		
		volumeWindow = FengGUI.createWindow(true, true);
		//volumeWindow.removeWidget(volumeWindow.getTitleBar());
		volumeWindow.setTitle("");
		volumeWindow.removeWidget(volumeWindow.getTitleLabel());
		int windowWidth=volumeWindow.getCloseButton().getWidth();
		int windowHeight=120;
		volumeWindow.setSize(windowWidth, windowHeight);
		volumeWindow.getContentContainer().setLayoutManager(new RowExLayout(true));
		
		final Slider control = FengGUI.createSlider(false);
		control.setSize(windowWidth, windowHeight-40);
		// initialize at remembered volume
		try{
			double value = Double.parseDouble(System.getProperty("betaville.sound.volume.master"));
			control.setValue(value);
		}catch(NumberFormatException numException){
			logger.error("Incompatible Value Found for Master Volume: \""+System.getProperty("betaville.sound.volume.master")+"\"");
			control.setValue(1d);
		}
		
		control.addSliderMovedListener(new ISliderMovedListener() {
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				SoundGameState.getInstance().setVolume((float)control.getValue());
				System.setProperty("betaville.sound.volume.master", Double.toString(control.getValue()));
			}
		});
		control.addMouseListener(new IMouseListener() {
			public void mouseWheel(Object sender, MouseWheelEvent mouseWheelEvent) {}
			
			public void mouseReleased(Object sender, MouseReleasedEvent mouseReleasedEvent) {
				try{
				logger.debug("Writing volume");
				PreferenceWriter pr = new PreferenceWriter();
				pr.writeData();
				}catch(IOException e){
					logger.error("Could not write preferences file!  This is not a huge deal, but your volume settings will not be saved :(", e);
				}
			}
			
			public void mousePressed(Object sender, MousePressedEvent mousePressedEvent) {}
			public void mouseMoved(Object sender, MouseMovedEvent mouseMovedEvent) {}
			public void mouseExited(Object sender, MouseExitedEvent mouseExited) {}
			public void mouseEntered(Object sender, MouseEnteredEvent mouseEnteredEvent) {}
			public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			public void mouseDoubleClicked(Object sender, MouseDoubleClickedEvent mouseDoubleClickedEvent) {}
			public void mouseClicked(Object sender, MouseClickedEvent mouseClickedEvent) {}
		});
		
		volumeWindow.addWidget(control);
	}

	public void cityPanelClosed() {
		if(volumeWindow!=null) if(volumeWindow.isInWidgetTree())volumeWindow.close();
	}
}
