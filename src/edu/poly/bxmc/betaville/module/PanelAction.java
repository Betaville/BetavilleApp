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
package edu.poly.bxmc.betaville.module;

import org.apache.log4j.Logger;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.IButtonPressedListener;
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

import com.jme.input.MouseInput;
import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.IUser.UserType;

/**
 * @author Skye Book
 *
 */
public class PanelAction extends Module {
	private static Logger logger = Logger.getLogger(PanelAction.class);

	public enum AvailabilityRule{
		/**
		 * This action will always be available to be selected
		 */
		ALWAYS,
		/**
		 * This action will only be available for selection when an object
		 * in the scene is selected
		 */
		OBJECT_SELECTED,
		/**
		 * This action will only be available for selection when no objects
		 * are selected in the scene
		 */
		NO_OBJECT_SELECTED,
		/**
		 * The ability to select this button will not be maintained internally,
		 * leaving it up to the developer's discretion
		 */
		IGNORE};
	protected FixedButton button;
	private Window tooltip;
	private long lastRollover=-1;
	private long rolloverDelay=1000;
	private boolean stillIn=true;
	protected String buttonTitle;
	private AvailabilityRule rule;
	private UserType requiredUserLevel;

	private ISpatialSelectionListener selectionListener;

	private IMouseListener tooltipListener;

	/**
	 * @param name
	 * @param description
	 */
	public PanelAction(String name, String description, IButtonPressedListener listener) {
		this(name, description, name, AvailabilityRule.ALWAYS, UserType.MEMBER, listener);
	}

	/**
	 * @param name
	 * @param description
	 */
	public PanelAction(String name, String description, String buttonTitle, AvailabilityRule ruleToSet, UserType minimumUserLevel, IButtonPressedListener listener) {
		super(name, description);
		this.buttonTitle=buttonTitle;
		rule=ruleToSet;
		requiredUserLevel=minimumUserLevel;

		createButton(listener);
		createToolTip();

		if(rule!=AvailabilityRule.IGNORE){
			selectionListener = new ISpatialSelectionListener() {
				public void selectionCleared(Design previousDesign) {
					if(rule.equals(AvailabilityRule.NO_OBJECT_SELECTED) || rule.equals(AvailabilityRule.ALWAYS)){
						button.setEnabled(true);
					}
					else button.setEnabled(false);
				}

				public void designSelected(Spatial spatial, Design design) {
					if(rule.equals(AvailabilityRule.OBJECT_SELECTED) || rule.equals(AvailabilityRule.ALWAYS)){
						button.setEnabled(true);
					}
					else button.setEnabled(false);
				}
			};
			SceneScape.addSelectionListener(selectionListener);
		}

		tooltipListener = new IMouseListener() {
			public void mouseWheel(Object sender, MouseWheelEvent mouseWheelEvent) {}
			public void mouseReleased(Object sender, MouseReleasedEvent mouseReleasedEvent) {}
			public void mousePressed(Object sender, MousePressedEvent mousePressedEvent) {}
			public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			public void mouseDoubleClicked(Object sender, MouseDoubleClickedEvent mouseDoubleClickedEvent) {}
			public void mouseClicked(Object sender, MouseClickedEvent mouseClickedEvent) {}

			public void mouseMoved(Object sender, MouseMovedEvent mouseMovedEvent) {

				if(tooltip.isInWidgetTree()){
					setLocation(false);
					GUIGameState.getInstance().getDisp().removeWidget(tooltip);
				}

				lastRollover = System.currentTimeMillis();
				SettingsPreferences.getGUIThreadPool().submit(new Runnable(){
					public void run() {
						while(stillIn){
							if(System.currentTimeMillis()-lastRollover>rolloverDelay){
								stillIn=false;
								setLocation(false);
								if(!tooltip.isInWidgetTree()) GUIGameState.getInstance().getDisp().addWidget(tooltip);
								logger.debug("showing tooltip");
							}
						}
					}
				});
			}

			private void setLocation(boolean setAtOffset){
				if(setAtOffset)tooltip.setXY(MouseInput.get().getXAbsolute()-(tooltip.getWidth()/2), MouseInput.get().getYAbsolute()-(tooltip.getHeight()/2));
				else tooltip.setXY(MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());
			}

			public void mouseExited(Object sender, MouseExitedEvent mouseExited) {
				stillIn=false;
				lastRollover=-1;
				if(tooltip.isInWidgetTree()) GUIGameState.getInstance().getDisp().removeWidget(tooltip);
			}

			public void mouseEntered(Object sender, MouseEnteredEvent mouseEnteredEvent) {
				stillIn=true;
			}
		};
	}

	private void createButton(IButtonPressedListener listener){
		button = FengGUI.createWidget(FixedButton.class);
		button.setText(buttonTitle);
		if(listener!=null) button.addButtonPressedListener(listener);
		//button.addMouseListener(tooltipListener);
	}

	private void createToolTip(){
		tooltip = FengGUI.createWindow(true, true);
		tooltip.removeWidget(tooltip.getTitleBar());
		tooltip.getContentContainer().setLayoutManager(new RowExLayout(false));

		Label name = FengGUI.createWidget(Label.class);
		Label developer = FengGUI.createWidget(Label.class);
		Label description = FengGUI.createWidget(Label.class);

		name.setText(getName());
		description.setText(getDescription());
		tooltip.getContentContainer().addWidget(name, description);
		if(getDeveloperName()!=null){
			developer.setText("By: " + getDeveloperName());
			tooltip.getContentContainer().addWidget(developer);
		}
	}

	public FixedButton getButton(){
		return button;
	}

	/**
	 * @return the requiredUserLevel
	 */
	public UserType getRequiredUserLevel() {
		return requiredUserLevel;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
	 */
	public void deconstruct() {
		SceneScape.removeSelectionListener(selectionListener);
	}

}
