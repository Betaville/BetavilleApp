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

import org.apache.log4j.Logger;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.Slider;
import org.fenggui.TextEditor;
import org.fenggui.binding.render.Binding;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.event.mouse.MouseEvent;
import org.fenggui.layout.RowExLayout;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ModeledDesign;
import edu.poly.bxmc.betaville.module.LocalSceneModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;

/**
 * @author Skye Book
 *
 */
public class DuplicateWindow extends Window {
	private static Logger logger = Logger.getLogger(DuplicateWindow.class);
	
	private int targetWidth=300;
	private int targetHeight=210;
	
	private Container duplicateContainer;
	private Container translateContainer;
	private Container translateLabelContainer;
	private Container rotateContainer;
	private Container acceptContainer;
	
	private Label duplicateLabel;
	private Label translateLabel;
	private Label rotateLabel;
	private Label northingLabel;
	private Label eastingLabel;
	private Label altitudeLabel;
	
	private TextEditor northing;
	private TextEditor easting;
	private TextEditor altitude;
	private TextEditor rotX;
	private TextEditor rotY;
	private TextEditor rotZ;
	
	private ComboBox modeSelector;
	
	private Slider duplicateSlider;
	private ISliderMovedListener moveSliderListener;
	
	private int copies = 1;
	private int maxCopies=5;
	private UTMCoordinate originalLocation;
	private float originalRotation;
	private Label savedLabel;

	public DuplicateWindow(){
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		getContentContainer().setSize(targetWidth-10, targetHeight-getTitleBar().getHeight());
		getContentContainer().setX(5);
		
		addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseEvent){
				}
			}
		});
		
		setupTranslatorLabels();
		setupTranslators();
		setupRotators();
		setupModulators();
		setupConfirmation();
		
		// This listener is what tells the window which object to do work on
		SceneScape.addSelectionListener(new ISpatialSelectionListener() {
			public void selectionCleared(Design previousDesign){
//				moveSpeedSlider.removeSliderMovedListener(moveSliderListener);
				
				setAllEnabled(getContentContainer(), false);
			}
			
			public void designSelected(Spatial spatial, Design design) {
				originalLocation=design.getCoordinate().clone();
				if(design instanceof ModeledDesign) originalRotation =  ((ModeledDesign)design).getRotationY();
				duplicateSlider.addSliderMovedListener(moveSliderListener);
				setAllEnabled(getContentContainer(), true);
			}
			
		});
		
		setAllEnabled(getContentContainer(), false);
	}

	private void setupConfirmation() {
		acceptContainer = FengGUI.createWidget(Container.class);
		acceptContainer.setLayoutManager(new RowExLayout());

		FixedButton save = FengGUI.createWidget(FixedButton.class);
		save.setText("Duplicate");
		save.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				saveAction();
				if(copies == 1){
					savedLabel.setText(getDesign().getName() + " has been duplicated.");
				}else{
					savedLabel.setText(getDesign().getName() + " has been duplicated " + copies + " times.");
				}
				
				boolean savingMode = (modeSelector.getSelectedValue().equals("Saving Mode"));

//				NetModelLoader.duplicate(getDesign(), northing.getText(), easting.getText(), altitude.getText(), rotX.getText(), rotY.getText(), rotZ.getText(), copies, savingMode);
			}
		});

		acceptContainer.addWidget(save);
		savedLabel = FengGUI.createWidget(Label.class);
		savedLabel.setText(" ");
		savedLabel.setXY(0, 0);
		getContentContainer().addWidget(acceptContainer);
		getContentContainer().addWidget(savedLabel);
	}

	private void setupModulators() {
		duplicateContainer = FengGUI.createWidget(Container.class);
		duplicateContainer.setLayoutManager(new RowExLayout(false));
		duplicateContainer.setWidth(targetWidth);
		
		duplicateLabel = FengGUI.createWidget(Label.class);
		duplicateLabel.setText("Duplications: 1");
		duplicateLabel.setXY(0, 0);
		
		moveSliderListener = new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				int newValue = (int)(duplicateSlider.getValue()*(maxCopies-1))+1;
				duplicateLabel.setText("Duplications: " + newValue);
				copies=newValue;
			}
		};
		
		duplicateSlider = FengGUI.createSlider(true);
		duplicateSlider.setWidth((duplicateContainer.getWidth()/4)*3);
		duplicateSlider.setXY(duplicateContainer.getWidth()-duplicateSlider.getWidth()-20, 0);
		
		duplicateContainer.addWidget(duplicateLabel,duplicateSlider);
		getContentContainer().addWidget(duplicateContainer);
		
		try {
			SceneGameState.getInstance().addModuleToUpdateList(new UpdateModule("DuplicateWindowUpdater"));
		} catch (ModuleNameException e1) {
			e1.printStackTrace();
		}
		
		
		Label space = FengGUI.createWidget(Label.class);
		space.setText(" ");
		getContentContainer().addWidget(space);
		
		modeSelector = FengGUI.createWidget(ComboBox.class);
		modeSelector.addItem("No Saving Mode");
		modeSelector.addItem("Saving Mode");
		getContentContainer().addWidget(modeSelector);
		
		Label space2 = FengGUI.createWidget(Label.class);
		space2.setText(" ");
		getContentContainer().addWidget(space2);
	}

	private void setupTranslators() {
		translateContainer = FengGUI.createWidget(Container.class);
		translateContainer.setLayoutManager(new RowExLayout());
		
		translateLabel = FengGUI.createWidget(Label.class);
		translateLabel.setText("Translate: ");

		northing = FengGUI.createWidget(TextEditor.class);
		northing.setText("0          ");
		northing.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);

		easting = FengGUI.createWidget(TextEditor.class);
		easting.setText("0           ");
		easting.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);

		altitude = FengGUI.createWidget(TextEditor.class);
		altitude.setText("0          ");
		altitude.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);
		

		translateContainer.addWidget(translateLabel);
		translateContainer.addWidget(northing);
		translateContainer.addWidget(easting);
		translateContainer.addWidget(altitude);
		getContentContainer().addWidget(translateContainer);

	}
	
	private void setupRotators() {
		rotateContainer = FengGUI.createWidget(Container.class);
		rotateContainer.setLayoutManager(new RowExLayout());
		
		rotateLabel = FengGUI.createWidget(Label.class);
		rotateLabel.setText("Rotate:     ");

		rotX = FengGUI.createWidget(TextEditor.class);
		rotX.setText("0          ");
		rotX.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);
		

		rotY = FengGUI.createWidget(TextEditor.class);
		rotY.setText("0           ");
		rotY.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);

		rotZ = FengGUI.createWidget(TextEditor.class);
		rotZ.setText("0          ");
		rotZ.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);
		

		rotateContainer.addWidget(rotateLabel);
		rotateContainer.addWidget(rotX);
		rotateContainer.addWidget(rotY);
		rotateContainer.addWidget(rotZ);
		getContentContainer().addWidget(rotateContainer);

	}
	
	private void setupTranslatorLabels() {
		translateLabelContainer = FengGUI.createWidget(Container.class);
		translateLabelContainer.setLayoutManager(new RowExLayout());
		
		Label lefttop= FengGUI.createWidget(Label.class);
		lefttop.setText("               ");
		

		northingLabel = FengGUI.createWidget(Label.class);
		northingLabel.setText("Northing    ");
		

		eastingLabel = FengGUI.createWidget(Label.class);
		eastingLabel.setText("Easting      ");

		altitudeLabel = FengGUI.createWidget(Label.class);
		altitudeLabel.setText("Altitude ");

		translateLabelContainer.addWidget(lefttop);
		translateLabelContainer.addWidget(northingLabel);
		translateLabelContainer.addWidget(eastingLabel);
		translateLabelContainer.addWidget(altitudeLabel);
		getContentContainer().addWidget(translateLabelContainer);
	}
	
	private void setAllEnabled(IWidget parent, boolean enabled){
		for(IWidget w : ((Container)parent).getWidgets()){
			w.setEnabled(enabled);
			if(w instanceof Container){
				setAllEnabled(w, enabled);
			}
		}
	}
	
	private void saveAction(){
//		if(SceneScape.isTargetSpatialLocal()){
//			logger.warn("Can't change the location of a local design!");
//			return;
//		}
//		
//		logger.info("Changing the location of " + getDesign().getID());
//		logger.info("OLD: " + originalLocation.toString() + " Altitude: " + originalLocation.getAltitude());
//		logger.info("NEW: " + getDesign().getCoordinate().toString() + " Altitude: " + getDesign().getCoordinate().getAltitude());
//		
//		if(!NetPool.getPool().getSecureConnection().changeModeledDesignLocation(getDesign().getID(),
//				getDesign().getRotationY(), SettingsPreferences.getUser(), SettingsPreferences.getPass(),
//				getDesign().getCoordinate())){
//			logger.error("Network save failed");
//		}
	}

	private ModeledDesign getDesign(){
		return (ModeledDesign) SceneScape.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName());
	}


	public void finishSetup(){
		setTitle("Model Duplicator");
		setSize(targetWidth, targetHeight);
		setXY(Binding.getInstance().getCanvasWidth()/2-getWidth()/2, Binding.getInstance().getCanvasHeight()/2-getHeight()/2);
	}


	public void close(){
		originalLocation=null;
		super.close();
	}
	
	private class UpdateModule extends Module implements LocalSceneModule{

		public UpdateModule(String name){
			super(name);
		}

		public void initialize(Node scene){}

		public void onUpdate(Node scene, Vector3f cameraLocation, Vector3f cameraDirection){
			if(modeSelector.getSelectedValue().equals("Saving Mode")){
				maxCopies=1;
			}else{
				maxCopies=500;
			}
			int newValue = (int)(duplicateSlider.getValue()*(maxCopies-1))+1;
			duplicateLabel.setText("Duplications: " + newValue);
			copies=newValue;
		}

		public void deconstruct(){}
		
	}
}
