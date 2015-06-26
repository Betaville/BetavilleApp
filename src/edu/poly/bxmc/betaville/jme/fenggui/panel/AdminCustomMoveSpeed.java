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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.Slider;
import org.fenggui.TextEditor;
import org.fenggui.binding.render.Binding;
import org.fenggui.composite.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengTextContentException;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.module.GlobalSceneModule;
import edu.poly.bxmc.betaville.module.LocalSceneModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;

/**
 * A City Panel component to allow for the manipulation of movement
 * and rotation speeds.
 * @author Skye Book
 *
 */
public class AdminCustomMoveSpeed extends Window implements IBetavilleWindow{
	private static Logger logger = Logger.getLogger(AdminCustomMoveSpeed.class);
	
	private int targetWidth=420;
	private int targetHeight=80;
	
	private Container nextToEachOtherContainer;
	
	private Container speedContainer;
	private Container rotateSpeedContainer;
	
	private AltitudeUpdater altitudeUpdater;
	private GroundMagnet groundMagnet;
	
	private Label rotateSpeedLabel;
	private String rotateSpeedLabelPrefix = Labels.get(this.getClass().getSimpleName()+".rotate_speed")+": ";
	private Slider rotateSpeedSlider;
	
	private Label speedLabel;
	private String labelPrefix = Labels.get(this.getClass().getSimpleName()+".move_speed")+": ";
	private Slider speedSlider;
	
	private int speedMin = 1;
	private int speedMax = 500;
	
	private Label altitude;
	private String altitudePrefix = Labels.get(this.getClass().getSimpleName()+".altitude")+": ";
	
	private boolean speedIsSafe=true;
	private boolean constantSpeed = true;
	private int finalSpeed=500;
	
	public AdminCustomMoveSpeed(){
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		nextToEachOtherContainer = FengGUI.createWidget(Container.class);
		nextToEachOtherContainer.setLayoutManager(new RowExLayout (true));
		
		rotateSpeedContainer = FengGUI.createWidget(Container.class);
		rotateSpeedContainer.setLayoutManager(new StaticLayout());
		rotateSpeedContainer.setWidth(targetWidth);
		
		rotateSpeedLabel = FengGUI.createWidget(Label.class);
		rotateSpeedLabel.setText(rotateSpeedLabelPrefix);
		rotateSpeedLabel.setXY(0, 0);
		rotateSpeedLabel.setHeight(40);
		rotateSpeedLabel.setWidth(200);
			
		rotateSpeedSlider = FengGUI.createSlider(true);
		rotateSpeedSlider.setWidth(180);
		rotateSpeedSlider.setXY(10, 0);
		rotateSpeedSlider.addSliderMovedListener(new ISliderMovedListener() {
			
			@Override
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				// The default rotate speed is .5f, so we can scale from 0f-1f by using the default slider range
				SceneGameState.getInstance().getSceneController().setTurnSpeed((float)rotateSpeedSlider.getValue());
				Double value = (double)rotateSpeedSlider.getValue();
				DecimalFormat df = new DecimalFormat("##.###");
				rotateSpeedLabel.setText(rotateSpeedLabelPrefix+df.format(value));
			}
		});
		
		speedContainer = FengGUI.createWidget(Container.class);
		speedContainer.setLayoutManager(new StaticLayout());

		
		speedLabel = FengGUI.createWidget(Label.class);
		speedLabel.setText(labelPrefix);
		speedLabel.setXY(0, 0);
		speedLabel.setHeight(40);
		speedLabel.setWidth(200);
		
		speedSlider = FengGUI.createSlider(true);
		speedSlider.setWidth(180);
		speedSlider.setXY(10, 0);
		

		speedSlider.addSliderMovedListener(new ISliderMovedListener() {
			
	
		
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				try {
					int toAdd = (int) (speedSlider.getValue()*calculateRange());
					finalSpeed = speedMin+toAdd;
					if(verifyConstantSpeed()){
						SceneGameState.getInstance().setMoveSpeed(Scale.fromMeter(finalSpeed));
						
						speedLabel.setText(labelPrefix+finalSpeed);
					}
					
				} catch (FengTextContentException e) {
					// SHOW ERROR!
					logger.error("Bad Value Given", e);
				}
			}
		});
		
		speedContainer.setHeight(speedLabel.getHeight());
     	speedContainer.setWidth(targetWidth);
		speedContainer.addWidget(speedLabel, speedSlider);
		
		rotateSpeedContainer.setHeight(speedLabel.getHeight());
     	rotateSpeedContainer.setWidth(targetWidth);
		rotateSpeedContainer.addWidget(rotateSpeedLabel, rotateSpeedSlider);
		
		nextToEachOtherContainer.setWidth(420);		
		nextToEachOtherContainer.addWidget(rotateSpeedContainer, speedContainer);
		
		
		Container bottomContainer = FengGUI.createWidget(Container.class);
		bottomContainer.setLayoutManager(new RowLayout(true));
		
		altitude = FengGUI.createWidget(Label.class);
		altitude.setText(altitudePrefix);
		
		SceneGameState.getInstance().setConstantSpeed(true);
		SceneGameState.getInstance().setMoveSpeed(Scale.fromMeter(finalSpeed));
		
		bottomContainer.addWidget(altitude);
		
		getContentContainer().addWidget(nextToEachOtherContainer, bottomContainer);
		
		try {
			speedSlider.setValue(calculateRange()/SceneGameState.getInstance().getSceneController().getMoveSpeed());
			rotateSpeedSlider.setValue(SceneGameState.getInstance().getSceneController().getTurnSpeed());
		} catch (FengTextContentException e) {
			// SHOW ERROR!
			logger.error("Bad Value Given", e);
		}
		
		altitudeUpdater = new AltitudeUpdater("Altitude Updater", "Retrieves the altitude on every udpate");
		groundMagnet = new GroundMagnet("Ground Magnet", "Forces the camera to be glued to the ground");
		try {
			SceneGameState.getInstance().addModuleToUpdateList(altitudeUpdater);
		} catch (ModuleNameException e) {
			e.printStackTrace();
		}
	}
	
	public void addToDisplay(){
		setXY(Binding.getInstance().getCanvasWidth()/2-getWidth()/2, Binding.getInstance().getCanvasHeight()/2-getHeight()/2);
		try {
			SceneGameState.getInstance().addModuleToUpdateList(altitudeUpdater);
		} catch (ModuleNameException e) {}
		GUIGameState.getInstance().getDisp().addWidget(this);
	}
	
	/**
	 * Tests that the maximum speed is a number larger than the given minimum speed
	 * @return true of the maximum speed is larger; false if not
	 * @throws FengTextContentException 
	 */

	public boolean verifyConstantSpeed(){
		return constantSpeed;
	}
	public void close(){
		SceneGameState.getInstance().removeModuleFromUpdateList(altitudeUpdater);
		super.close();
	}

	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);
	}
	
	private class AltitudeUpdater extends Module implements LocalSceneModule{

		public AltitudeUpdater(String name, String description) {
			super(name, description);
		}

		public void initialize(Node scene) {}

		public void onUpdate(Node scene, Vector3f cameraLocation, Vector3f cameraDirection) {
			altitude.setText(altitudePrefix+Scale.toMeter(cameraLocation.getY()));
		}

		public void deconstruct() {}
	}
	
	private int calculateRange() throws FengTextContentException{
		return speedMax-speedMin;
	}
	
	private class GroundMagnet extends Module implements GlobalSceneModule{

		public GroundMagnet(String name, String description) {
			super(name, description);
		}

		public void initialize(Node scene) {}
		

		public void onUpdate(Node scene, Vector3f cameraLocation, Vector3f cameraDirection) {
			Vector3f newLocation = cameraLocation.clone();
			newLocation.setY(Scale.fromMeter(SceneScape.getMinimumHeight()));
			DisplaySystem.getDisplaySystem().getRenderer().getCamera().setLocation(newLocation);
		}

		public void deconstruct() {}
	}
	
}