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
package edu.poly.bxmc.betaville.gui;

import java.awt.Container;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.key.Key;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * A simplified PanelAction who's sole purpose is to turn a Swing
 * window on or off.
 * @author Skye Book
 *
 */
public class SwingOnOffPanelAction extends PanelAction{
	private static final Logger logger = Logger.getLogger(SwingOnOffPanelAction.class);
	protected Container associatedWindow;

	/**
	 * Creates a new OnOffPanelAction that serves no purpose other than turning
	 * a window on or off.  All options can be set through the constructor.
	 * @param name The text that will be displayed on this button
	 * @param description A description of what this action does
	 * @param ruleToSet Describes when this action is selectable
	 * @param minimumUserLevel The lowest user level that will see this action in their panel
	 * @param initOnScreen Displays the window at startup if true
	 * @param window
	 */
	public SwingOnOffPanelAction(String name, String description, AvailabilityRule ruleToSet,
			UserType minimumUserLevel, boolean initOnScreen, Class<? extends Container> window){
		super(name, description, name, ruleToSet, minimumUserLevel, null);

		try{
			associatedWindow = window.newInstance();
			logger.debug("associatedWindow is of type: " + associatedWindow.getClass().getName());
		}catch(Exception e){
			logger.error("All kinds of bad, associatedWindow is of type: " + associatedWindow.getClass().getName(), e);
		}

		button.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(associatedWindow.isVisible()) associatedWindow.setVisible(false);
				else{
					associatedWindow.setVisible(true);
				}
			}
		});

		if(initOnScreen){
			BetavilleNoCanvas.addCompletionListener(new IAppInitializationCompleteListener() {
				public void applicationInitializationComplete() {
					button.fireEvent(Button.EVENT_BUTTONPRESSED, new ButtonPressedEvent(button, new HashSet<Key>()));
				}
			});
		}
	}

}
