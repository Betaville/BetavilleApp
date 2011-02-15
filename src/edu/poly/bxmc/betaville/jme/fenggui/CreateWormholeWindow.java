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
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.binding.render.Binding;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.StaticLayout;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.module.LocalSceneModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;

/**
 * Window for creating a new Wormhole
 * @author Joschka Zimdars
 *
 */
public class CreateWormholeWindow extends Window implements IBetavilleWindow{
	private static Logger logger = Logger.getLogger(CreateWormholeWindow.class);
	
	private int targetWidth=240;
	private int targetHeight=200;
	
	private Container container0;
	private Container container1;
	private Container container2;
	private Container container3;
	private Container container4;
	private Container container5;
	private Container rotContainer0;
	private Container rotContainer1;
	private Container rotContainer2;
	
	private FixedButton createButton;
	
	private Label lonLabel;
	private TextEditor lonTxt;
	
	private Label latLabel;
	private TextEditor latTxt;
	
	private Label cityLabel;
	private TextEditor cityText;
	
	private Label streetLabel;
	private TextEditor streetText;
	
	private Label rotxLabel;
	private TextEditor rotxText;
	
	private Label rotyLabel;
	private TextEditor rotyText;
	
	private Label rotzLabel;
	private TextEditor rotzText;
	
	private CreateWormholeWindow currentCreateWormholeWindow;
	
	
	public CreateWormholeWindow(){
		super(true, true);
		currentCreateWormholeWindow = this;
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		Vector3f cameraLocation = DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation();
		UTMCoordinate utm = MapManager.betavilleToUTM(cameraLocation);
		GPSCoordinate gps = utm.getGPS();
		
		container0 = FengGUI.createWidget(Container.class);
		container0.setLayoutManager(new StaticLayout());
		container0.setWidth(targetWidth-10);
		
		latLabel = FengGUI.createWidget(Label.class);
		latLabel.setText("Latitude");
		latLabel.setXY(0, 0);
		
		latTxt = FengGUI.createWidget(TextEditor.class);
		latTxt.setText(Double.toString(gps.getLatitude()));
		
		latTxt.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		latTxt.setXY(latLabel.getWidth()+10, 0);

		container0.setHeight(latTxt.getHeight());
		container0.addWidget(latLabel, latTxt);
		
		container1 = FengGUI.createWidget(Container.class);
		container1.setLayoutManager(new StaticLayout());
		container1.setWidth(targetWidth-10);
		
		lonLabel = FengGUI.createWidget(Label.class);
		lonLabel.setText("Longitude");
		lonLabel.setXY(0, 0);
		
		lonTxt = FengGUI.createWidget(TextEditor.class);
		lonTxt.setText(Double.toString(gps.getLongitude()));
		lonTxt.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		lonTxt.setXY(latLabel.getWidth()+10, 0);
		
		container1.setHeight(lonTxt.getHeight());
		container1.addWidget(lonLabel, lonTxt);
		
		container2 = FengGUI.createWidget(Container.class);
		container2.setLayoutManager(new StaticLayout());
		container2.setWidth(targetWidth-10);
		
		
		cityLabel = FengGUI.createWidget(Label.class);
		cityLabel.setText("City");
		cityLabel.setXY(0, 0);
		
		cityText = FengGUI.createWidget(TextEditor.class);
		cityText.setText("      ");
		cityText.setXY(latLabel.getWidth()+10, 0);
	
		
		container2.setHeight(lonTxt.getHeight());
		container2.addWidget( cityLabel, cityText);
		
		
		container5 = FengGUI.createWidget(Container.class);
		container5.setLayoutManager(new StaticLayout());
		container5.setWidth(targetWidth-10);
		
		
		streetLabel = FengGUI.createWidget(Label.class);
		streetLabel.setText("Street");
		streetLabel.setXY(0, 0);
		
		streetText = FengGUI.createWidget(TextEditor.class);
		streetText.setText("      ");
		streetText.setXY(latLabel.getWidth()+10, 0);
		
		container5.setHeight(lonTxt.getHeight());
		container5.addWidget( streetLabel, streetText);
		
		rotContainer0 = FengGUI.createWidget(Container.class);
		rotContainer0.setLayoutManager(new StaticLayout());
		rotContainer0.setWidth(targetWidth-10);
		
		
		rotxLabel = FengGUI.createWidget(Label.class);
		rotxLabel.setText("View X");
		rotxLabel.setXY(0, 0);
		
		rotxText = FengGUI.createWidget(TextEditor.class);
		rotxText.setText(" ");
		rotxText.setXY(latLabel.getWidth()+10, 0);
		
		rotContainer0.setHeight(lonTxt.getHeight());
		rotContainer0.addWidget( rotxLabel, rotxText);
		
		rotContainer1 = FengGUI.createWidget(Container.class);
		rotContainer1.setLayoutManager(new StaticLayout());
		rotContainer1.setWidth(targetWidth-10);
		
		
		rotyLabel = FengGUI.createWidget(Label.class);
		rotyLabel.setText("View Y");
		rotyLabel.setXY(0, 0);
		
		rotyText = FengGUI.createWidget(TextEditor.class);
		rotyText.setText(" ");
		rotyText.setXY(latLabel.getWidth()+10, 0);
		
		rotContainer1.setHeight(lonTxt.getHeight());
		rotContainer1.addWidget( rotyLabel, rotyText);
		
		rotContainer2 = FengGUI.createWidget(Container.class);
		rotContainer2.setLayoutManager(new StaticLayout());
		rotContainer2.setWidth(targetWidth-10);
		
		
		rotzLabel = FengGUI.createWidget(Label.class);
		rotzLabel.setText("View Z");
		rotzLabel.setXY(0, 0);
		
		rotzText = FengGUI.createWidget(TextEditor.class);
		rotzText.setText(" ");
		rotzText.setXY(latLabel.getWidth()+10, 0);
		
		rotContainer2.setHeight(lonTxt.getHeight());
		rotContainer2.addWidget( rotzLabel, rotzText);
		
		container3 = FengGUI.createWidget(Container.class);
		container3.setLayoutManager(new StaticLayout());
		container3.setWidth(200);
		
		createButton = FengGUI.createWidget(FixedButton.class);
		createButton.setText("Create");
		createButton.setWidth(createButton.getWidth()+10);
		createButton.setHeight(createButton.getWidth()-30);
		createButton.setXY(0, 0);
		createButton.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				logger.info("New Wormhole created");
				SceneGameState.getInstance().setWormholeData(cityText.getText(), streetText.getText(), latTxt.getText(), lonTxt.getText());
				
				currentCreateWormholeWindow.removedFromWidgetTree();
				currentCreateWormholeWindow.close();
			}
		});
		
		container3.setHeight(lonTxt.getHeight());
		container3.addWidget(createButton);
		
		getContentContainer().addWidget(container0, container1, rotContainer0, rotContainer1, rotContainer2, container2, container4, container5,  container3);
		try {
			SceneGameState.getInstance().addModuleToUpdateList(new UpdateModule("CreateWormholeWindowUpdater"));
		} catch (ModuleNameException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public void addToDisplay(){
		setXY(Binding.getInstance().getCanvasWidth()/2-getWidth()/2, Binding.getInstance().getCanvasHeight()/2-getHeight()/2);
		
		GUIGameState.getInstance().getDisp().addWidget(this);
	}
	
	public void close(){
		super.close();
	}

	public void finishSetup() {
		setTitle("Create Wormhole Window");
		setSize(targetWidth, targetHeight);
	}
	
	private class UpdateModule extends Module implements LocalSceneModule{

		public UpdateModule(String name){
			super(name);
		}

		public void initialize(Node scene){}

		public void onUpdate(Node scene, Vector3f cameraLocation, Vector3f cameraDirection){
			UTMCoordinate utm = MapManager.betavilleToUTM(cameraLocation);
			GPSCoordinate gps = utm.getGPS();
			rotxText.setText(Double.toString(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().x));
			rotyText.setText(Double.toString(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().y));
			rotzText.setText(Double.toString(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getDirection().z));
			lonTxt.setText(Double.toString(gps.getLongitude()));
			latTxt.setText(Double.toString(gps.getLatitude()));
		}

		public void deconstruct(){}
		
	}
	

}
