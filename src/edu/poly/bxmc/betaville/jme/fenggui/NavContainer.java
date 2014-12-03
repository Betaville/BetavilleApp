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

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.Pixmap;
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
import org.fenggui.layout.StaticLayout;

import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.KioskMode;
import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState.SOUNDS;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class NavContainer extends Container {
	private static final Logger logger = Logger.getLogger(NavContainer.class);

	private ITexture post;
	private ITexture postOver;
	private ITexture info;
	private ITexture infoOver;
	private ITexture fave;
	private ITexture faveOver;
	private ITexture newDesign;
	private ITexture newDesignOver;
	private InformationWindow infoWindow = null;

	/**
	 * 
	 */
	public NavContainer() {
		super();
		this.setSize(148, 148);
		this.setLayoutManager(new StaticLayout());
		loadTextures();
		createContext();
	}

	private void loadTextures(){
		try {
			post		=	Binding.getInstance().getTexture("data/uiAssets/menu/NavPost.png");
			postOver	=	Binding.getInstance().getTexture("data/uiAssets/menu/NavPost_Over.png");
			info		=	Binding.getInstance().getTexture("data/uiAssets/menu/NavInfo.png");
			infoOver	=	Binding.getInstance().getTexture("data/uiAssets/menu/NavInfo_Over.png");
			fave		=	Binding.getInstance().getTexture("data/uiAssets/menu/NavFave.png");
			faveOver	=	Binding.getInstance().getTexture("data/uiAssets/menu/NavFave_Over.png");
			newDesign		=	Binding.getInstance().getTexture("data/uiAssets/menu/NavNew.png");
			newDesignOver	=	Binding.getInstance().getTexture("data/uiAssets/menu/NavNew_Over.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createContext(){
		try{
			// BUTTONS
			final Button postButton = new Button();
			postButton.setPixmap(new Pixmap(post));
			postButton.setSize(50, 50);
			postButton.setXY(0, this.getHeight()-postButton.getHeight());
			postButton.addMouseListener(new IMouseListener(){
				public void mouseClicked(Object arg0, MouseClickedEvent arg1) {
					SoundGameState.getInstance().playSound(SOUNDS.TOGGLE, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
					GUIGameState.getInstance().removeNavMenu();
					GUIGameState.getInstance().setContextOn(false);
					showComments();
				}
				public void mouseDoubleClicked(Object arg0, MouseDoubleClickedEvent arg1) {}
				public void mouseEntered(Object arg0, MouseEnteredEvent arg1) {
					postButton.setPixmap(new Pixmap(postOver));
				}
				public void mouseExited(Object arg0, MouseExitedEvent arg1) {
					postButton.setPixmap(new Pixmap(post));
				}
				public void mouseMoved(Object arg0, MouseMovedEvent arg1) {}
				public void mousePressed(Object arg0, MousePressedEvent arg1) {}
				public void mouseReleased(Object arg0, MouseReleasedEvent arg1) {}
				public void mouseWheel(Object arg0, MouseWheelEvent arg1) {}
				public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			});
			final Button infoButton = new Button();
			infoButton.setPixmap(new Pixmap(info));
			infoButton.setSize(50, 50);
			infoButton.setXY(this.getWidth()-infoButton.getWidth(), this.getHeight()-infoButton.getHeight());
			infoButton.addMouseListener(new IMouseListener(){
				public void mouseClicked(Object arg0, MouseClickedEvent arg1) {
					SoundGameState.getInstance().playSound(SOUNDS.TOGGLE, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
					GUIGameState.getInstance().removeNavMenu();
					GUIGameState.getInstance().setContextOn(false);
					if(!SceneScape.getTargetSpatial().getName().endsWith("$empty")){
						if(infoWindow==null){
							infoWindow = FengGUI.createWidget(InformationWindow.class);
							infoWindow.finishSetup();
						}
						if(!infoWindow.isInWidgetTree()){
							GUIGameState.getInstance().getDisp().addWidget(infoWindow);
							SceneScape.addSelectionListener(infoWindow.getUpdateListener());
							infoWindow.update(SceneScape.getPickedDesign());
						}
					}
				}
				public void mouseDoubleClicked(Object arg0, MouseDoubleClickedEvent arg1) {}
				public void mouseEntered(Object arg0, MouseEnteredEvent arg1) {
					infoButton.setPixmap(new Pixmap(infoOver));
				}
				public void mouseExited(Object arg0, MouseExitedEvent arg1) {
					infoButton.setPixmap(new Pixmap(info));
				}
				public void mouseMoved(Object arg0, MouseMovedEvent arg1) {}
				public void mousePressed(Object arg0, MousePressedEvent arg1) {}
				public void mouseReleased(Object arg0, MouseReleasedEvent arg1) {}
				public void mouseWheel(Object arg0, MouseWheelEvent arg1) {}
				public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			});
			final Button faveButton = new Button();
			faveButton.setPixmap(new Pixmap(fave));
			faveButton.setSize(50, 50);
			faveButton.setXY(0,0);
			faveButton.addMouseListener(new IMouseListener(){
				public void mouseClicked(Object arg0, MouseClickedEvent arg1) {
					SoundGameState.getInstance().playSound(SOUNDS.TOGGLE, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
					GUIGameState.getInstance().removeNavMenu();
					GUIGameState.getInstance().setContextOn(false);

					if(SettingsPreferences.guestMode()){
						GUIGameState.getInstance().getDisp().addWidget(
								FengUtils.createDismissableWindow("Betaville", "You cannot fave an object in guest mode!", Labels.get("Generic.ok"), true));
					}

					if(!SceneScape.getTargetSpatial().getName().endsWith("$empty")){
						try {
							int response = NetPool.getPool().getSecureConnection().faveDesign(SceneScape.getPickedDesign().getID());
							if(response==0){
								FengUtils.showNewDismissableWindow("Betaville",
										Labels.get(NavContainer.class, "faved"), Labels.get("Generic.ok"), true);
							}
							else if(response == -2){
								FengUtils.showNewDismissableWindow("Betaville",
										Labels.get(NavContainer.class, "already_faved"), Labels.get("Generic.ok"), true);
							}
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				public void mouseDoubleClicked(Object arg0, MouseDoubleClickedEvent arg1) {}
				public void mouseEntered(Object arg0, MouseEnteredEvent arg1) {
					faveButton.setPixmap(new Pixmap(faveOver));
				}
				public void mouseExited(Object arg0, MouseExitedEvent arg1) {
					faveButton.setPixmap(new Pixmap(fave));
				}
				public void mouseMoved(Object arg0, MouseMovedEvent arg1) {}
				public void mousePressed(Object arg0, MousePressedEvent arg1) {}
				public void mouseReleased(Object arg0, MouseReleasedEvent arg1) {}
				public void mouseWheel(Object arg0, MouseWheelEvent arg1) {}
				public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			});
			final Button newButton = new Button();
			newButton.setPixmap(new Pixmap(newDesign));
			newButton.setSize(50, 50);
			newButton.setXY(this.getWidth()-newButton.getWidth(),0);
			newButton.addMouseListener(new IMouseListener(){
				public void mouseClicked(Object arg0, MouseClickedEvent arg1) {
					//SoundGameState.getInstance().playSound(SOUNDS.TOGGLE, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
					GUIGameState.getInstance().removeNavMenu();
					GUIGameState.getInstance().setContextOn(false);
					//GUIGameState.getInstance().turnOnEditContainer();

					// If we're in kiosk mode, the user is not allowed to put up a proposal!
					if(KioskMode.isInKioskMode()){
						logger.info("New proposals cannot be created in kiosk mode - Showing dialog instead");
						GUIGameState.getInstance().getDisp().addWidget(FengUtils.createDismissableWindow("Betaville", "New proposals cannot be created in kiosk mode", Labels.get("Generic.ok"), true));
						return;
					}

					// If the window is already on, stop here
					if(GUIGameState.getInstance().isProposalWindowOn()) 
						return;

					// If it isn't, put it there
					GUIGameState.getInstance().resetProposalWindow();
				}
				public void mouseDoubleClicked(Object arg0, MouseDoubleClickedEvent arg1) {}
				public void mouseEntered(Object arg0, MouseEnteredEvent arg1) {
					newButton.setPixmap(new Pixmap(newDesignOver));
				}
				public void mouseExited(Object arg0, MouseExitedEvent arg1) {
					newButton.setPixmap(new Pixmap(newDesign));
				}
				public void mouseMoved(Object arg0, MouseMovedEvent arg1) {}
				public void mousePressed(Object arg0, MousePressedEvent arg1) {}
				public void mouseReleased(Object arg0, MouseReleasedEvent arg1) {}
				public void mouseWheel(Object arg0, MouseWheelEvent arg1) {}
				public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			});

			Label imageLabel = new Label();
			imageLabel.setPixmap(new Pixmap(Binding.getInstance().getTexture("data/uiAssets/menu/NavFrame.png")));
			this.addWidget(imageLabel);
			this.addWidget(postButton);
			this.addWidget(infoButton);
			this.addWidget(faveButton);
			this.addWidget(newButton);
			this.pack();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showComments(){
		GUIGameState.getInstance().showCommentWindow();

	}
}