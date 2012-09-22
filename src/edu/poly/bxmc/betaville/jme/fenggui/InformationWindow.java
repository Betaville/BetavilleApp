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
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WindowClosedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.StaticLayout;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;


/**
 * @author Skye Book
 *
 */
public class InformationWindow extends Window implements IBetavilleWindow{
	private static Logger logger = Logger.getLogger(InformationWindow.class);
	private int targetWidth=400;
	private int targetHeight=300;
	
	private Label nameLabel;
	private TextEditor nameEditor;
	
	private Label addressLabel;
	private TextEditor addressEditor;
	
	private Label descriptionLabel;
	private TextEditor descriptionEditor;
	
	private Label urlLabel;
	private TextEditor urlEditor;
	
	private FixedButton update;
	private Label userLabel;
	private final String userPrefix = "Uploaded By: ";
	
	private int currentDesignID;
	
	private ISpatialSelectionListener updateListener;
	
	/**
	 * @param owner
	 */
	public InformationWindow() {
		super(true, true);
		getContentContainer().setLayoutManager(new StaticLayout());
		
		int normalEditorHeight=18;
		
		updateListener = new ISpatialSelectionListener() {
			
			public void selectionCleared(Design previousDesign) {}
			
			public void designSelected(Spatial spatial, Design design) {
				update(design);
			}
		};
		
		nameLabel = FengGUI.createWidget(Label.class);
		nameLabel.setText("name");
		
		nameEditor = FengGUI.createWidget(TextEditor.class);
		nameEditor.setSize(targetWidth-10, normalEditorHeight);
		nameEditor.setReadonly(SettingsPreferences.guestMode());
		nameEditor.setMultiline(true);
		nameEditor.setWordWarping(true);
		nameEditor.setMaxCharacters(100);
		nameEditor.setExpandable(false);
		
		addressLabel = FengGUI.createWidget(Label.class);
		addressLabel.setText("address");
		
		addressEditor = FengGUI.createWidget(TextEditor.class);
		addressEditor.setSize(targetWidth-10, normalEditorHeight);
		addressEditor.setReadonly(SettingsPreferences.guestMode());
		addressEditor.setMultiline(true);
		addressEditor.setWordWarping(true);
		addressEditor.setMaxCharacters(100);
		addressEditor.setExpandable(false);
		
		descriptionLabel = FengGUI.createWidget(Label.class);
		descriptionLabel.setText("description");
		
		descriptionEditor = FengGUI.createWidget(TextEditor.class);
		descriptionEditor.setSize(targetWidth-10, normalEditorHeight*3);
		descriptionEditor.setReadonly(SettingsPreferences.guestMode());
		descriptionEditor.setMultiline(true);
		descriptionEditor.setWordWarping(true);
		descriptionEditor.setMaxLines(3);
		
		urlLabel = FengGUI.createWidget(Label.class);
		urlLabel.setText("url");
		
		urlEditor = FengGUI.createWidget(TextEditor.class);
		urlEditor.setSize(targetWidth-10, normalEditorHeight);
		urlEditor.setReadonly(SettingsPreferences.guestMode());
		urlEditor.setMultiline(true);
		urlEditor.setWordWarping(true);
		urlEditor.setMaxCharacters(100);
		urlEditor.setExpandable(false);
		
		Container nameContainer = mergeItems(nameLabel, nameEditor);
		Container addressContainer = mergeItems(addressLabel, addressEditor);
		Container descriptionContainer = mergeItems(descriptionLabel, descriptionEditor);
		Container urlContainer = mergeItems(urlLabel, urlEditor);
		
		update = FengGUI.createWidget(FixedButton.class);
		update.setText("update");
		update.setWidth(update.getWidth()+10);
		update.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				logger.info("Information should be updating now");
				try {
					commitChanges();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		userLabel = FengGUI.createWidget(Label.class);
		userLabel.setSize(update.getX()-getContentContainer().getWidth()-10, update.getHeight());
		
		int containerY = 5;
		
		update.setXY(targetWidth-10-update.getWidth(), containerY);
		userLabel.setXY(10, containerY);
		containerY+=(update.getHeight()+5);
		getContentContainer().addWidget(userLabel, update);
		
		
		
		urlContainer.setXY(0, containerY);
		containerY+=urlContainer.getHeight();
		getContentContainer().addWidget(urlContainer);
		
		descriptionContainer.setXY(0, containerY);
		containerY+=descriptionContainer.getHeight();
		getContentContainer().addWidget(descriptionContainer);
		
		addressContainer.setXY(0, containerY);
		containerY+=addressContainer.getHeight();
		getContentContainer().addWidget(addressContainer);
		
		nameContainer.setXY(0, containerY);
		containerY+=nameContainer.getHeight();
		getContentContainer().addWidget(nameContainer);
	}
	
	private Container mergeItems(Label label, TextEditor editor){
		Container c = FengGUI.createWidget(Container.class);
		c.setLayoutManager(new StaticLayout());
		
		int containerWidth = targetWidth-10;
		int containerHeight = label.getHeight()+15+editor.getHeight();
		
		logger.info(label.getText() + " expected height: " + containerHeight);
		
		c.setSize(containerWidth, containerHeight);
		
		label.setXY(FengUtils.midWidth(c, label), editor.getHeight()+5);
		editor.setXY(0, 0);
		
		c.addWidget(label, editor);
		
		return c;
	}
	
	private void commitChanges() throws UnknownHostException, IOException{
		update.setEnabled(false);
		Design d = SettingsPreferences.getCity().findDesignByID(currentDesignID);
		
		String updateString = "";
		
		boolean nameResponse=true;
		boolean addressResponse=true;
		boolean descriptionResponse=true;
		boolean urlResponse=true;
		
		String newName = FengUtils.getText(nameEditor);
		if(changeQuery(newName, d.getName())){
			nameResponse = NetPool.getPool().getSecureConnection().changeDesignName(d.getID(), newName);
			if(!updateString.isEmpty()) updateString += ", ";
			updateString += "name";
		}
		
		String newAddress = FengUtils.getText(addressEditor);
		if(changeQuery(newAddress, d.getAddress())){
			addressResponse = NetPool.getPool().getSecureConnection().changeDesignAddress(d.getID(), newAddress);
			if(!updateString.isEmpty()) updateString += ", ";
			updateString += "address";
		}
		
		String newDescription = FengUtils.getText(descriptionEditor);
		if(changeQuery(newDescription, d.getDescription())){
			descriptionResponse = NetPool.getPool().getSecureConnection().changeDesignDescription(d.getID(), newDescription);
			if(!updateString.isEmpty()) updateString += ", ";
			updateString += "description";
		}
		
		String newURL = FengUtils.getText(urlEditor);
		if(changeQuery(newURL, d.getURL())){
			urlResponse = NetPool.getPool().getSecureConnection().changeDesignURL(d.getID(), newURL);
			if(!updateString.isEmpty()) updateString += ", ";
			updateString += "url";
		}
		
		if(updateString.isEmpty()) updateString += "nothing";
		updateString += " updated";
		
		if(nameResponse && addressResponse && descriptionResponse && urlResponse){
			showSimpleDialog(updateString);
			d.setName(newName);
			d.setAddress(newAddress);
			d.setDescription(newDescription);
			d.setURL(newURL);
			GUIGameState.getInstance().forceSelectionWindowUpdate();
		}
		else showSimpleDialog("You are not authorized to change this");
		
		update.setEnabled(true);
	}
	
	private boolean changeQuery(String editorText, String originalText){
		if(editorText.isEmpty()) return false;
		else if(!originalText.equals(editorText)) return true;
		else return false;
	}
	
	/**
	 * Updates this information window to the data provided
	 * by the supplied Design
	 * @param selectedDesign - The design information to set
	 * @see Design
	 */
	public void update(Design selectedDesign){
		currentDesignID=selectedDesign.getID();
		nameEditor.setText(selectedDesign.getName());
		addressEditor.setText(selectedDesign.getAddress());
		descriptionEditor.setText(selectedDesign.getDescription());
		urlEditor.setText(selectedDesign.getURL());
		
		userLabel.setText(userPrefix+selectedDesign.getUser());
	}
	
	public ISpatialSelectionListener getUpdateListener(){
		return updateListener;
	}
	
	private void showSimpleDialog(String text){
		final Window w = FengGUI.createWindow(true, true);
		w.setTitle("Betaville");
		w.addWindowClosedListener(new IWindowClosedListener() {
			public void windowClosed(WindowClosedEvent windowClosedEvent) {
				update.setEnabled(true);
			}
		});
		
		Label l = FengGUI.createWidget(Label.class);
		l.setText(text);
		
		FixedButton b = FengGUI.createWidget(FixedButton.class);
		b.setText("ok");
		b.setWidth(b.getWidth()+10);
		b.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				w.close();
			}
		});
		w.getContentContainer().setLayoutManager(new RowExLayout(false));
		w.getContentContainer().addWidget(l, b);
		w.setSize(l.getWidth()+10, 75);
		w.setXY(getX()+(getWidth()/2)-(w.getWidth()/2), getY()+(getHeight()/2)-(w.getHeight()/2));
		update.setEnabled(false);
		GUIGameState.getInstance().getDisp().addWidget(w);
	}
	
	public void close(){
		super.close();
		SceneScape.removeSelectionListener(updateListener);
	}
	
	public void finishSetup(){
		setSize(targetWidth, targetHeight);
		setTitle("Information");
	}
}
