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
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.Slider;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.event.mouse.MouseEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.StaticLayout;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengTextContentException;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.panel.ModelMover;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.Translator;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ModeledDesign;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @deprecated - Use {@link ModelMover} instead
 * @author Skye Book
 *
 */
public class AdminModelMover extends Window {
	private static Logger logger = Logger.getLogger(AdminModelMover.class);
	
	private int targetWidth=300;
	private int targetHeight=200;
	
	private Window errorWindow;
	private FixedButton errorCancel;
	private IButtonPressedListener errorCancelListener;
	
	private Container moveSpeedContainer;
	private Container sliderRotateContainer;
	private Container translateContainer;
	private Container elevateContainer;
	private Container acceptContainer;
	private Container saveStatusContainer;
	
	private Slider rotYSlider;
	private TextEditor rotYText;
	private ISliderMovedListener sliderListener;
	private ITextChangedListener rotYTextListener;
	private Label sliderLabel;
	private Slider moveSpeedSlider;
	private ISliderMovedListener moveSliderListener;
	private Label moveSpeedLabel;
	private int moveSpeed = 1;
	private int maxMoveSpeed=50;
	private UTMCoordinate originalLocation;
	private float originalRotation;
	private Label savedLabel;

	public AdminModelMover(){
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		getContentContainer().setSize(targetWidth-10, targetHeight-getTitleBar().getHeight());
		getContentContainer().setX(5);
		
		addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object source, Event event) {
				if(event instanceof MouseEvent){
					//TODO: Detach sliders if mouse wanders out of the widget `window
				}
			}
		});
		
		// setup core functionality
		setupModulators();
		setupRotator();
		setupTranslator();
		setupConfirmation();
		createErrorWindow();
		
		// This listener is what tells the window which object to do work on
		SceneScape.addSelectionListener(new ISpatialSelectionListener() {
			public void selectionCleared(Design previousDesign){
				rotYSlider.removeSliderMovedListener(sliderListener);
				moveSpeedSlider.removeSliderMovedListener(moveSliderListener);
				
				setAllEnabled(getContentContainer(), false);
			}
			
			public void designSelected(Spatial spatial, Design design) {
				originalLocation=design.getCoordinate().clone();
				if(design instanceof ModeledDesign) originalRotation =  ((ModeledDesign)design).getRotationY();
				rotYSlider.setValue((double)originalRotation/360d);
				rotYText.setText(""+originalRotation);
				rotYSlider.addSliderMovedListener(sliderListener);
				moveSpeedSlider.addSliderMovedListener(moveSliderListener);
				setAllEnabled(getContentContainer(), true);
			}
			
		});
		
		setAllEnabled(getContentContainer(), false);
	}

	private void setupConfirmation() {
		acceptContainer = FengGUI.createWidget(Container.class);
		acceptContainer.setLayoutManager(new RowExLayout());
		
		FixedButton cancel = FengGUI.createWidget(FixedButton.class);
		cancel.setText(Labels.get("Generic.unsaved"));
		cancel.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				revert(getDesign().getID());
				close();
			}});

		FixedButton save = FengGUI.createWidget(FixedButton.class);
		save.setText(Labels.get("Generic.save"));
		save.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				try {
					saveAction();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				savedLabel.setText(getDesign().getName()+" was saved.");
//				close();
			}
		});

		acceptContainer.addWidget(cancel);
		acceptContainer.addWidget(save);
		getContentContainer().addWidget(acceptContainer);
		savedLabel = FengGUI.createWidget(Label.class);
		savedLabel.setText(" ");
		savedLabel.setXY(0, 0);
		getContentContainer().addWidget(savedLabel);
	}

	private void setupModulators() {
		moveSpeedContainer = FengGUI.createWidget(Container.class);
		moveSpeedContainer.setLayoutManager(new RowExLayout(false));
		moveSpeedContainer.setWidth(targetWidth);
		
		moveSpeedLabel = FengGUI.createWidget(Label.class);
		moveSpeedLabel.setText(Labels.get(this.getClass().getSimpleName()+".move_speed")+": 1");
		moveSpeedLabel.setXY(0, 0);
		
		moveSliderListener = new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				int newValue = (int)(moveSpeedSlider.getValue()*(maxMoveSpeed-1))+1;
				moveSpeedLabel.setText(Labels.get(this.getClass().getSimpleName()+".move_speed")+": " + newValue);
				moveSpeed=newValue;
			}
		};
		
		moveSpeedSlider = FengGUI.createSlider(true);
		moveSpeedSlider.setWidth((moveSpeedContainer.getWidth()/4)*3);
		moveSpeedSlider.setXY(moveSpeedContainer.getWidth()-moveSpeedSlider.getWidth()-20, 0);
		
		moveSpeedContainer.addWidget(moveSpeedLabel,moveSpeedSlider);
		getContentContainer().addWidget(moveSpeedContainer);
	}

	private void setupTranslator() {
		translateContainer = FengGUI.createWidget(Container.class);
		translateContainer.setLayoutManager(new RowExLayout());
		
		FixedButton north = FengGUI.createWidget(FixedButton.class);
		north.setText(Labels.compound("Generic.move Generic.north"));
		north.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				Translator.moveX(SceneScape.getTargetSpatial(), moveSpeed);
				SettingsPreferences.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(0, moveSpeed, 0);
			}
		});

		FixedButton south = FengGUI.createWidget(FixedButton.class);
		south.setText(Labels.compound("Generic.move Generic.south"));
		south.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				Translator.moveX(SceneScape.getTargetSpatial(), -moveSpeed);
				SettingsPreferences.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(0, -moveSpeed, 0);
				
			}
		});

		FixedButton east = FengGUI.createWidget(FixedButton.class);
		east.setText(Labels.compound("Generic.move Generic.east"));
		east.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				Translator.moveZ(SceneScape.getTargetSpatial(), moveSpeed);
				SettingsPreferences.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(moveSpeed, 0, 0);
			}
		});

		FixedButton west = FengGUI.createWidget(FixedButton.class);
		west.setText(Labels.compound("Generic.move Generic.west"));
		west.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				Translator.moveZ(SceneScape.getTargetSpatial(), -moveSpeed);
				SettingsPreferences.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(-moveSpeed, 0, 0);
			}
		});

		translateContainer.addWidget(north);
		translateContainer.addWidget(south);
		translateContainer.addWidget(east);
		translateContainer.addWidget(west);
		getContentContainer().addWidget(translateContainer);
		
		elevateContainer = FengGUI.createWidget(Container.class);
		elevateContainer.setLayoutManager(new RowExLayout());
		
		FixedButton up = FengGUI.createWidget(FixedButton.class);
		up.setText(Labels.compound("Generic.move Generic.up"));
		up.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				Translator.moveY(SceneScape.getTargetSpatial(), moveSpeed);
				SettingsPreferences.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(0, 0, moveSpeed);
			}
		});

		FixedButton down = FengGUI.createWidget(FixedButton.class);
		down.setText(Labels.compound("Generic.move Generic.down"));
		down.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				Translator.moveY(SceneScape.getTargetSpatial(), -moveSpeed);
				SettingsPreferences.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(0, 0, -moveSpeed);
			}
		});

		elevateContainer.addWidget(up);
		elevateContainer.addWidget(down);
		getContentContainer().addWidget(elevateContainer);
	}

	private void setupRotator() {
		sliderRotateContainer = FengGUI.createWidget(Container.class);
		sliderRotateContainer.setLayoutManager(new RowExLayout(false));
		sliderRotateContainer.setWidth(targetWidth);
		
		sliderLabel = FengGUI.createWidget(Label.class);
		sliderLabel.setText(Labels.get("Generic.rotation")+": ");
		sliderLabel.setXY(0, 0);
		
		rotYText = FengGUI.createWidget(TextEditor.class);
		rotYText.setRestrict(TextEditor.RESTRICT_NUMBERSONLY);
		rotYText.setText("000");
		rotYTextListener = new ITextChangedListener() {
			public void textChanged(TextChangedEvent textChangedEvent) {
				try {
					int value;
					
					// only get the number if we see a positive value (and the string isn't empty)
					if(!FengUtils.getText(rotYText).contains("-") && !rotYText.getText().isEmpty()){
						value = FengUtils.getNumber(rotYText);
						if(value>360){
							rotYText.setText(""+360);
							value=360;
						}
						else if(value<0){
							rotYText.setText(""+0);
							value=0;
						}
					}
					else{
						// in the event of a negative value, use zero
						rotYText.setText(""+0);
						value=0;
					}

					rotYSlider.removeSliderMovedListener(sliderListener);
					rotYSlider.setValue(value/360);
					rotYSlider.addSliderMovedListener(sliderListener);
					doRotationChange(value);
					
				} catch (FengTextContentException e) {
					logger.error("Error when entering Y rotation", e);
				}
			}
		};
		rotYText.addTextChangedListener(rotYTextListener);
		
		sliderListener = new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				float newValue = (float)(rotYSlider.getValue()*360f);
				rotYText.removeTextChangedListener(rotYTextListener);
				rotYText.setText(""+newValue);
				rotYText.addTextChangedListener(rotYTextListener);
				doRotationChange(newValue);
			}
		};

		rotYSlider = FengGUI.createSlider(true);
		rotYSlider.setWidth((sliderRotateContainer.getWidth()/4)*3);
		rotYSlider.setXY(sliderRotateContainer.getWidth()-rotYSlider.getWidth()-20, 0);
		rotYSlider.setValue(originalRotation/360);
		
		Container rotateTextContainer = FengGUI.createWidget(Container.class);
		rotateTextContainer.setLayoutManager(new RowExLayout(true));
		rotateTextContainer.addWidget(sliderLabel, rotYText);

		sliderRotateContainer.addWidget(rotateTextContainer);
		sliderRotateContainer.addWidget(rotYSlider);
		getContentContainer().addWidget(sliderRotateContainer);
	}
	
	private void doRotationChange(float newValue){
		SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleY(newValue));
		SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(getDesign().getRotationX(), newValue, getDesign().getRotationZ()));
		getDesign().setRotationY(newValue);
	}
	
	private void setAllEnabled(IWidget parent, boolean enabled){
		for(IWidget w : ((Container)parent).getWidgets()){
			w.setEnabled(enabled);
			if(w instanceof Container){
				setAllEnabled(w, enabled);
			}
		}
	}
	
	private void saveAction() throws UnknownHostException, IOException{
		if(SceneScape.isTargetSpatialLocal()){
			logger.warn("Can't change the location of a local design!");
			return;
		}
		
		logger.info("Changing the location of " + getDesign().getID());
		logger.info("OLD: " + originalLocation.toString() + " Altitude: " + originalLocation.getAltitude());
		logger.info("NEW: " + getDesign().getCoordinate().toString() + " Altitude: " + getDesign().getCoordinate().getAltitude());
		logger.info("Rotation will be: " + getDesign().getRotationY());
		
		if(!NetPool.getPool().getSecureConnection().changeModeledDesignLocation(getDesign().getID(),
				getDesign().getRotationY(), getDesign().getCoordinate())){
			logger.error("Network save failed");
		}
		else logger.info("Network Save Success");
	}

	private ModeledDesign getDesign(){
		return (ModeledDesign) SettingsPreferences.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName());
	}

	private void revert(int idToRevertTo){
		
		SceneScape.getTargetSpatial().setLocalTranslation(JME2MapManager.instance.locationToBetaville(originalLocation));
		SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleY(originalRotation));
		getDesign().setCoordinate(originalLocation.clone());
		getDesign().setRotationY(originalRotation);
		rotYSlider.setValue(originalRotation/360);
		sliderLabel.setText(Labels.get("Generic.rotation")+": " + originalRotation);
		
		logger.info("Reverted to "+originalLocation.toString());
	}

	public void finishSetup(){
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);
	}
	
	private void createErrorWindow(){
		errorWindow = FengGUI.createWindow(true, true);
		errorWindow.setSize(100, 75);
		errorWindow.setTitle(Labels.get(this.getClass().getSimpleName()+".unsaved"));
		errorWindow.getTitleBar().removeWidget(errorWindow.getCloseButton());
		
		errorWindow.getContentContainer().setLayoutManager(new StaticLayout());
		
		errorCancel = FengGUI.createWidget(FixedButton.class);
		errorCancel.setText(Labels.get(this.getClass().getSimpleName()+".title"));
		errorCancel.setXY(5, 5);
		
		FixedButton save = FengGUI.createWidget(FixedButton.class);
		save.setText(Labels.get("Generic.save"));
		save.setXY(errorWindow.getWidth()-save.getWidth()-5, 5);
		save.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				try {
					saveAction();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				errorWindow.close();
			}
		});
		
		errorWindow.getContentContainer().addWidget(errorCancel, save);
	}
	
	private void positionErrorWindow(){
		errorWindow.setXY(getX()+((getWidth()/2)-errorWindow.getWidth()/2), getY()+((getHeight()/2)-errorWindow.getHeight()/2));
	}

	public void close(){
		savedLabel.setText("");
		ModeledDesign d = getDesign();
		errorCancel.removeButtonPressedListener(errorCancelListener);
		originalLocation=null;
		super.close();
	}
}
