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
package edu.poly.bxmc.betaville.jme.fenggui;

import java.io.IOException;

import org.fenggui.Button;
import org.fenggui.Container;
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

import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState.SOUNDS;

/**
 * @author Skye Book
 *
 */
public class EditContainer extends Container {
	private ITexture move;
	private ITexture moveOver;
	private ITexture save;
	private ITexture saveOver;
	private ITexture place;
	private ITexture placeOver;
	private ITexture dump;
	private ITexture dumpOver;

	/**
	 * 
	 */
	public EditContainer() {
		super();
		this.setSize(211, 211);
		this.setLayoutManager(new StaticLayout());
		this.getAppearance().disableAll();
		this.getAppearance().removeAll();
		loadTextures();
		createContext();
	}
	
	private void loadTextures(){
		try {
			move		=	Binding.getInstance().getTexture("data/uiAssets/menu/EditMove.png");
			moveOver	=	Binding.getInstance().getTexture("data/uiAssets/menu/EditMove_Over.png");
			save		=	Binding.getInstance().getTexture("data/uiAssets/menu/EditSave.png");
			saveOver	=	Binding.getInstance().getTexture("data/uiAssets/menu/EditSave_Over.png");
			place		=	Binding.getInstance().getTexture("data/uiAssets/menu/EditPlace.png");
			placeOver	=	Binding.getInstance().getTexture("data/uiAssets/menu/EditPlace_Over.png");
			dump		=	Binding.getInstance().getTexture("data/uiAssets/menu/EditDump.png");
			dumpOver	=	Binding.getInstance().getTexture("data/uiAssets/menu/EditDump_Over.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createContext(){
		try{
			// BUTTONS
			final Button placeButton = new Button();
			placeButton.setPixmap(new Pixmap(place));
			placeButton.setSize(60, 60);
			placeButton.setXY(((this.getWidth()/2)-(placeButton.getWidth()/2))-9, this.getHeight()-placeButton.getHeight());
			placeButton.addMouseListener(new IMouseListener(){
				public void mouseClicked(Object arg0, MouseClickedEvent arg1) {
					SoundGameState.getInstance().playSound(SOUNDS.TOGGLE, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
					//GUIGameState.getInstance().removeEditMenu();
					GUIGameState.getInstance().setContextOn(false);
					addModel();
				}
				public void mouseDoubleClicked(Object arg0, MouseDoubleClickedEvent arg1) {}
				public void mouseEntered(Object arg0, MouseEnteredEvent arg1) {
					placeButton.setPixmap(new Pixmap(placeOver));
				}
				public void mouseExited(Object arg0, MouseExitedEvent arg1) {
					placeButton.setPixmap(new Pixmap(place));
				}
				public void mouseMoved(Object arg0, MouseMovedEvent arg1) {}
				public void mousePressed(Object arg0, MousePressedEvent arg1) {}
				public void mouseReleased(Object arg0, MouseReleasedEvent arg1) {}
				public void mouseWheel(Object arg0, MouseWheelEvent arg1) {}
				public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			});
			final Button moveButton = new Button();
			moveButton.setPixmap(new Pixmap(move));
			moveButton.setSize(60, 60);
			moveButton.setXY(((this.getWidth()/2)-(moveButton.getWidth()/2))+10, 1);
			moveButton.addMouseListener(new IMouseListener(){
				public void mouseClicked(Object arg0, MouseClickedEvent arg1) {
					SoundGameState.getInstance().playSound(SOUNDS.TOGGLE, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
					//GUIGameState.getInstance().removeEditMenu();
					GUIGameState.getInstance().setContextOn(false);
				}
				public void mouseDoubleClicked(Object arg0, MouseDoubleClickedEvent arg1) {}
				public void mouseEntered(Object arg0, MouseEnteredEvent arg1) {
					moveButton.setPixmap(new Pixmap(moveOver));
				}
				public void mouseExited(Object arg0, MouseExitedEvent arg1) {
					moveButton.setPixmap(new Pixmap(move));
				}
				public void mouseMoved(Object arg0, MouseMovedEvent arg1) {}
				public void mousePressed(Object arg0, MousePressedEvent arg1) {}
				public void mouseReleased(Object arg0, MouseReleasedEvent arg1) {}
				public void mouseWheel(Object arg0, MouseWheelEvent arg1) {}
				public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			});
			final Button dumpButton = new Button();
			dumpButton.setPixmap(new Pixmap(dump));
			dumpButton.setSize(60, 60);
			dumpButton.setXY((this.getWidth()-dumpButton.getWidth())-0, ((this.getHeight()/2)-(dumpButton.getHeight()/2))-6);
			dumpButton.addMouseListener(new IMouseListener(){
				public void mouseClicked(Object arg0, MouseClickedEvent arg1) {
					SoundGameState.getInstance().playSound(SOUNDS.TOGGLE, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
					//GUIGameState.getInstance().removeEditMenu();
					GUIGameState.getInstance().setContextOn(false);
				}
				public void mouseDoubleClicked(Object arg0, MouseDoubleClickedEvent arg1) {}
				public void mouseEntered(Object arg0, MouseEnteredEvent arg1) {
					dumpButton.setPixmap(new Pixmap(dumpOver));
				}
				public void mouseExited(Object arg0, MouseExitedEvent arg1) {
					dumpButton.setPixmap(new Pixmap(dump));
				}
				public void mouseMoved(Object arg0, MouseMovedEvent arg1) {}
				public void mousePressed(Object arg0, MousePressedEvent arg1) {}
				public void mouseReleased(Object arg0, MouseReleasedEvent arg1) {}
				public void mouseWheel(Object arg0, MouseWheelEvent arg1) {}
				public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			});
			final Button saveButton = new Button();
			saveButton.setPixmap(new Pixmap(save));
			saveButton.setSize(60, 60);
			saveButton.setXY(0, ((this.getHeight()/2)-(saveButton.getHeight()/2))+7);
			saveButton.addMouseListener(new IMouseListener(){
				public void mouseClicked(Object arg0, MouseClickedEvent arg1) {
					SoundGameState.getInstance().playSound(SOUNDS.TOGGLE, DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
					//GUIGameState.getInstance().removeEditMenu();
					GUIGameState.getInstance().setContextOn(false);
				}
				public void mouseDoubleClicked(Object arg0, MouseDoubleClickedEvent arg1) {}
				public void mouseEntered(Object arg0, MouseEnteredEvent arg1) {
					saveButton.setPixmap(new Pixmap(saveOver));
				}
				public void mouseExited(Object arg0, MouseExitedEvent arg1) {
					saveButton.setPixmap(new Pixmap(save));
				}
				public void mouseMoved(Object arg0, MouseMovedEvent arg1) {}
				public void mousePressed(Object arg0, MousePressedEvent arg1) {}
				public void mouseReleased(Object arg0, MouseReleasedEvent arg1) {}
				public void mouseWheel(Object arg0, MouseWheelEvent arg1) {}
				public void mouseDragged(Object sender, MouseDraggedEvent mouseDraggedEvent) {}
			});

			Label imageLabel = new Label();
			imageLabel.setPixmap(new Pixmap(Binding.getInstance().getTexture("data/uiAssets/menu/EditFrame.png")));
			this.addWidget(imageLabel);
			this.addWidget(placeButton);
			this.addWidget(moveButton);
			this.addWidget(dumpButton);
			this.addWidget(saveButton);
			this.pack();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addModel(){
		
		// If the window is already on, stop here
		if(GUIGameState.getInstance().isProposalWindowOn()) return;
		
		// If it isn't, put it there
		GUIGameState.getInstance().resetProposalWindow();
	}
}