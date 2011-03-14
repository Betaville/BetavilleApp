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

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.CityManager;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BlockingScrollContainer;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.City;
import edu.poly.bxmc.betaville.module.LocalSceneModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.util.ITextFilter;

/**
 * @author Joschka Zimdars
 *
 */
public class WormholeWindow extends Window implements IBetavilleWindow{
	private static Logger logger = Logger.getLogger(WormholeWindow.class);
	private int targetWidth = 200;
	private int targetHeight = 200;
	
	String location;
	
	
	List<ITextFilter> contentFilters;
	
	private FixedButton jumpButton;
	private FixedButton createWormholeButton;
	private CreateWormholeWindow createWormholeWindow;
	private Label createText;
	private Label row;
	private Label cityText;
	private Label wormholeText;
	private ComboBox globalSelector;
	private String selectedValue;
	private ComboBox localSelector;
	
	private Camera camera = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
	
	private WormholeWindow currentWormholeWindow;
	
	private String[][] globalLocationList;
	private Vector<String[][]> localLocationList = new Vector<String[][]>();
	private Vector<String> cityList;
	List<City> cities = null;
	
	private float cameraMoveSpeed = SceneGameState.getInstance().getMoveSpeed();
	
	/**
	 * 
	 */
	public WormholeWindow(){
		super(true, true);
		currentWormholeWindow = this;
		getContentContainer().setLayoutManager(new RowExLayout(false));
		createCityList();
	}
	
	private void jumpTo(String location){
		for (int i = 0; i < globalLocationList.length; i++) {
			if(globalLocationList[i][1].equals(location)){
				SceneGameState.getInstance().setMoveSpeed(cameraMoveSpeed);
				camera.lookAt(new Vector3f(Float.parseFloat(globalLocationList[i][4]), Float.parseFloat(globalLocationList[i][5]), Float.parseFloat(globalLocationList[i][6])), new Vector3f(0,0,0));
				logger.info("Wormhole to "+location+" selected.");
				camera.setLocation(MapManager.locationToBetaville(new GPSCoordinate(60.0, Double.parseDouble(globalLocationList[i][2]), Double.parseDouble(globalLocationList[i][3]))));
				GPSCoordinate gps = new GPSCoordinate(20.0, Double.parseDouble(globalLocationList[i][2]), Double.parseDouble(globalLocationList[2][3]));
				UTMCoordinate utm = gps.getUTM();
				SceneScape.setUTMZone(utm.getLonZone(), utm.getLatZone());
				if(globalLocationList[i][0].equals("Bremen")){
					CityManager.swapCities(SceneScape.getCurrentCityID(), 3);
				}else{
					CityManager.swapCities(SceneScape.getCurrentCityID(), 2);
					if(globalLocationList[i][0].equals("Wormhole Paradox")){
						camera.setLocation(MapManager.locationToBetaville(new GPSCoordinate(9000.0, Double.parseDouble(globalLocationList[i][2]), Double.parseDouble(globalLocationList[i][3]))));
						GPSCoordinate gps1 = new GPSCoordinate(9000.0, Double.parseDouble(globalLocationList[i][2]), Double.parseDouble(globalLocationList[2][3]));
						UTMCoordinate utm1 = gps1.getUTM();
						SceneScape.setUTMZone(utm1.getLonZone(), utm.getLatZone());
						SceneGameState.getInstance().setMoveSpeed(50);
					}
				}
			}
		}
	}
	
	private void createCityList(){
		BlockingScrollContainer sc = FengGUI.createWidget(BlockingScrollContainer.class);
		Container container = FengGUI.createWidget(Container.class);
		globalSelector = FengGUI.createWidget(ComboBox.class);
		localSelector = FengGUI.createWidget(ComboBox.class);
		globalSelector.addItem(" ");
		localSelector.addItem(" ");
		container.setLayoutManager(new RowExLayout(false));
		sc.setInnerWidget(container);
		sc.layout();
		cities = NetPool.getPool().getConnection().findAllCities();
		if(cities==null){
			logger.error("Error when retrieving cities");
			return;
		}
		row = FengGUI.createWidget(Label.class);
		row.setText(" ");
		row.setXY(30, 0);
		cityText = FengGUI.createWidget(Label.class);
		cityText.setText("Select City:");
		cityText.setXY(30, 0);
		wormholeText = FengGUI.createWidget(Label.class);
		wormholeText.setText("Select Wormhole:");
		wormholeText.setXY(30, 0);
		jumpButton = FengGUI.createWidget(FixedButton.class);
		jumpButton.setText("Jump");
		jumpButton.setXY(30, 0);
		getContentContainer().addWidget(cityText);
		getContentContainer().addWidget(globalSelector);
		getContentContainer().addWidget(wormholeText);
		getContentContainer().addWidget(localSelector);
		getContentContainer().addWidget(row);
		getContentContainer().addWidget(jumpButton);
		
		globalLocationList = SceneGameState.getInstance().getWormholeData();
		cityList = new Vector<String>();

		for(int a=0;a<globalLocationList.length;a++){
//			logger.info(globalLocationList[a][0]);
			boolean newCity = true;
			for(int b=0;b<cityList.size();b++){
				if(globalLocationList[a][0].equals(cityList.get(b))){
					newCity = false;
				}
			}
			if(newCity){
				cityList.add(globalLocationList[a][0]);
			}
		}
		for(int b=0;b<cityList.size();b++){
			logger.info("Wormholes to "+cityList.get(b)+" were found.");
			localLocationList.add(getLocalWormholes(cityList.get(b)));
			globalSelector.addItem(cityList.get(b));
		}
		this.getLocalWormholeSelectors(globalSelector.getSelectedValue());
		
		try {
			SceneGameState.getInstance().addModuleToUpdateList(new UpdateModule("WormholeWindowUpdater"));
		} catch (ModuleNameException e1) {
			e1.printStackTrace();
		}
		jumpButton.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
					currentWormholeWindow.jumpTo(localSelector.getSelectedValue());
					currentWormholeWindow.close();
			}
		});
	}
	
	private void createWormholeButton(){
		createText = FengGUI.createWidget(Label.class);
		createText.setText("Create:");
		createText.setXY(30, 0);
		getContentContainer().addWidget(createText);
		
		createWormholeButton = FengGUI.createWidget(FixedButton.class);
		createWormholeButton.setText("New Wormhole");
		createWormholeButton.setWidth(29);
		createWormholeButton.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(createWormholeWindow==null){
					createWormholeWindow = FengGUI.createWidget(CreateWormholeWindow.class);
					createWormholeWindow.finishSetup();
					createWormholeWindow.addToDisplay();
					currentWormholeWindow.removedFromWidgetTree();
					currentWormholeWindow.close();
				}else{
					createWormholeWindow.addToDisplay();
					currentWormholeWindow.removedFromWidgetTree();
					currentWormholeWindow.close();
				}
			}
		});
		getContentContainer().addWidget(createWormholeButton);
		createWormholeButton.setEnabled(true);
	}
	
	private String[][] getLocalWormholes(String city){
		String[][] s;
		int count = 0;
		for (int i = 0; i < globalLocationList.length; i++) {
			if(globalLocationList[i][0].equals(city)){
				count++;
			}
		}
		s = new String[count][7];
		count = 0;
		for (int i = 0; i < globalLocationList.length; i++) {
			if(globalLocationList[i][0].equals(city)){
				for (int j = 0; j < globalLocationList[i].length; j++) {
					s[count][j] = globalLocationList[i][j];
				}
				count++;
			}
		}
		return s;
	}
	
	private void getLocalWormholeSelectors(String city){
		localSelector = FengGUI.createWidget(ComboBox.class);
		logger.info(getContentContainer().getChildWidgetCount());
		getContentContainer().removeAllWidgets();
		getContentContainer().addWidget(cityText);
		getContentContainer().addWidget(globalSelector);
		getContentContainer().addWidget(wormholeText);
		getContentContainer().addWidget(localSelector);
		getContentContainer().addWidget(row);
		getContentContainer().addWidget(jumpButton);
		createWormholeButton();
		boolean cityFound = false;
		for (int i = 0; i < localLocationList.size(); i++) {
			if(localLocationList.get(i)[0][0].equals(city)){
				cityFound = true;
				for (int j = 0; j < localLocationList.get(i).length; j++) {
					localSelector.addItem(localLocationList.get(i)[j][1]);
				}
				for (int j = 0; j < 5-localLocationList.get(i).length; j++) {
					localSelector.addItem(" ");
				}
			}
		}
		if(!cityFound){
			for (int i = 0; i < 5; i++) {
				localSelector.addItem(" ");
			}
		}
		selectedValue = globalSelector.getSelectedValue();
	}
	
	public void finishSetup(){
		setTitle("Wormholes");
		setSize(targetWidth, targetHeight);
		setXY(Binding.getInstance().getCanvasWidth()/2-getWidth()/2, Binding.getInstance().getCanvasHeight()/2-getHeight()/2);
	}
	
	public void close(){
		super.close();
	}
	
	private class UpdateModule extends Module implements LocalSceneModule{

		public UpdateModule(String name){
			super(name);
		}

		public void initialize(Node scene){}

		public void onUpdate(Node scene, Vector3f cameraLocation, Vector3f cameraDirection){
			if(!selectedValue.equals(globalSelector.getSelectedValue())){
				currentWormholeWindow.getLocalWormholeSelectors(globalSelector.getSelectedValue());
			}
		}

		public void deconstruct(){}
		
	}
}
