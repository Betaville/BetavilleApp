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
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.Slider;
import org.fenggui.composite.Window;
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

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Design;

/**
 * Window used to select which models to remove from the scene
 * when displaying the proposal in the process of being created.
 * @author Skye Book
 *
 */
public class MakeRoomWindow extends Window  implements IBetavilleWindow{
	private static Logger logger = Logger.getLogger(MakeRoomWindow.class);
	private int targetWidth=300;
	private int targetHeight=200;
	
	private ICloseAction closeAction;
	private IFinishedListener finishedListener;
	
	private FixedButton removeObstructionButton;
	
	private Container bc;

	private SlideScrollContainer listContainer;
	private Slider listController;
	
	private String sName;
	private int sID;
	
	/**
	 * Holds the names and ID's of all designs to be removed
	 * when the proposal being planned is added to the display
	 */
	private Map<String, Integer> removables;

	/**
	 * 
	 */
	public MakeRoomWindow() {
		super(true, false);
		getContentContainer().setLayoutManager(new StaticLayout());
		
		removables = new LinkedHashMap<String, Integer>();
		
		createButtonContainer();
		
		listContainer = new SlideScrollContainer();
		listContainer.setHorizontal(false);
		listContainer.setStartSize(targetWidth-30, targetHeight-60);
		listContainer.setSize(listContainer.getStartWidth(), listContainer.getStartHeight());
		listContainer.setXY(5, bc.getHeight()+5);
		
		listController = FengGUI.createSlider(false);
		listController.setHeight(listContainer.getHeight());
		listController.setXY(listContainer.getWidth(), listContainer.getY());
		
		listController.addSliderMovedListener(new ISliderMovedListener() {
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				listContainer.moveTo(listController.getValue());
			}
		});
		
		bc.setXY(5, 5);
		
		getContentContainer().addWidget(listContainer, listController, bc);
		
		SceneScape.addSelectionListener(new ISpatialSelectionListener() {
			public void designSelected(Spatial spatial, Design design) {
				if(!isInWidgetTree()) return;
				logger.debug("something selected");
				setSelectable(design.getName(), design.getID());
				setActive(true);
			}
			public void selectionCleared(Design previousDesign) {
				if(!isInWidgetTree()) return;
				logger.debug("nothing selected");
				setActive(false);
			}
		});
	}

	private void createButtonContainer() {
		removeObstructionButton = FengGUI.createWidget(FixedButton.class);
		removeObstructionButton.setText("remove");
		removeObstructionButton.setWidth(removeObstructionButton.getWidth()+10);
		removeObstructionButton.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				addToList(sName, sID);
			}
		});
		
		bc = FengGUI.createWidget(Container.class);
		bc.setLayoutManager(new StaticLayout());
		bc.setSize(targetWidth-10, removeObstructionButton.getHeight());
		
		removeObstructionButton.setXY(0, 0);
		removeObstructionButton.setEnabled(false);
		bc.addWidget(removeObstructionButton);
	}

	public void addToList(String name, int id){
		removables.put(name, id);
		
		SceneGameState.getInstance().removeDesignFromDisplay(id);
		
		/* I know this may raise a performance red flag to some.  If there were
		 * thousands of records and this was being done every second, then
		 * relisting the entire set of records would be superfluous.  In our
		 * case, I think it will be fine - Skye
		 */ 
		relist();
	}

	public void removeFromList(String name){
		try {
			SceneGameState.getInstance().addDesignToDisplay(removables.get(name));
		} catch (IOException e1) {
			logger.warn("Media for " + removables.get(name) + " could not be found", e1);
		} catch (URISyntaxException e1){}
		
		removables.remove(name);
		relist();
	}
	
	/**
	 * Removes all records from the list container and relists
	 * them based on the contents of {@linkplain #removables}
	 */
	private void relist(){
		
		listContainer.clearHolster();
		listController.setValue(0);
		
		for(Entry<String, Integer> e : removables.entrySet()){
			RemovableListMember member = new RemovableListMember(e);
			listContainer.addToSlideScroller(member);
		}
	}
	
	public void setSelectable(String sName, int sID){
		this.sName=sName;
		this.sID=sID;
	}
	
	public void setActive(boolean active){
		removeObstructionButton.setEnabled(active);
	}
	
	public void finishSetup(){
		setTitle("Make Room");
		setSize(targetWidth, targetHeight);
	}
	
	public void finishSetup(ICloseAction action, IFinishedListener finishedListener){
		closeButton.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				completeAndClose();
			}
		});
		closeAction=action;
		this.finishedListener=finishedListener;
		removeWidget(getCloseButton());
		finishSetup();
	}
	
	/**
	 * Triggers the close action and the finished action.
	 * @see ICloseAction#close()
	 * @see IFinishedListener#finished(Collection)
	 */
	public void completeAndClose(){
		closeAction.close();
		finishedListener.finished(removables.values());
		
	}
	
	public static interface IFinishedListener{
		public void finished(Collection<Integer> values);
	}
	
	public class RemovableListMember extends Container{
		private Label label;
		private FixedButton replaceButton;
		private RemovableListMember(final Entry<String, Integer> entry){
			super(new StaticLayout());
			setWidth(listContainer.getStartWidth());
			
			label = FengGUI.createWidget(Label.class);
			label.setText(entry.getKey());
			
			replaceButton = FengGUI.createWidget(FixedButton.class);
			replaceButton.setText("put back");
			replaceButton.setWidth(replaceButton.getWidth()+10);
			replaceButton.addButtonPressedListener(new IButtonPressedListener() {
				public void buttonPressed(Object source, ButtonPressedEvent e) {
					removeFromList(entry.getKey());
				}
			});
			
			label.setXY(0, 0);
			replaceButton.setXY(this.getWidth()-replaceButton.getWidth(), -5);
			this.addWidget(label, replaceButton);
			this.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
				public void processEvent(Object source, Event event) {
					handleMouseEvent(event);
				}
			});
		}
		
		private void handleMouseEvent(Event event){
			if(event instanceof MouseEnteredEvent){
				this.getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
			}
			else if(event instanceof MouseExitedEvent){
				this.getAppearance().removeAll();
			}
		}
	}
}