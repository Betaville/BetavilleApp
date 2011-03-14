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

import java.util.Vector;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.binding.render.Binding;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;

import com.jme.renderer.Camera;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.CityManager;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BlockingScrollContainer;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.City;

/**
 * @author Joschka Zimdars
 *
 */
public class LocalWormholeWindow extends Window implements IBetavilleWindow{
	private static Logger logger = Logger.getLogger(LocalWormholeWindow.class);
	private int targetWidth = 250;
	private int targetHeight = 150;
	
	private Camera camera = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
	
	private String[][] locationList = new String[0][0];
	private String[][] locationListq = new String[0][0];
	private String lat;
	private String lon;
	private Vector<FixedButton> buttons = new Vector<FixedButton>();
	
	private City city;
	private LocalWormholeWindow currentLocalWormholeWindow;
	
	private int counter = 0;

	public LocalWormholeWindow(){
		super(true, true);
		currentLocalWormholeWindow = this;
	}
	
	public void initialize(String[][] locationList, City city){
		this.locationList = locationList;
		String[][] locationListx = new String[locationList.length+this.locationListq.length][5];
		for(int i = 0;i<locationList.length+this.locationListq.length;i++){
			if(i<this.locationListq.length){
				locationListx[i] = this.locationListq[i];
			}else{
				locationListx[i] = locationList[i-this.locationListq.length];
			}
		}
		this.locationListq = locationListx;
		
		this.city = city;
		
		getContentContainer().setLayoutManager(new RowExLayout(false));
		createList();
	}
	
	
	private void createList(){
		BlockingScrollContainer sc = FengGUI.createWidget(BlockingScrollContainer.class);
		Container c = FengGUI.createWidget(Container.class);
		c.setLayoutManager(new RowExLayout(false));
		sc.setInnerWidget(c);
		sc.layout();
		String buttonText = "";
		for(int i=0;i<locationList.length;i++){
			logger.info(locationList.length);
			buttonText=locationList[i][0].trim()+", "+locationList[i][1].trim()+", "+locationList[i][2].trim();
			
			FixedButton f = FengGUI.createWidget(FixedButton.class);
			f.setText(buttonText);
			f.setWidth(100);
			buttons.add(f);
			f.addButtonPressedListener(new IButtonPressedListener() {
				public void buttonPressed(Object source, ButtonPressedEvent e) {
					CityManager.swapCities(SceneScape.getCurrentCityID(), city.getCityID());
					for (int j = 0; j < buttons.size(); j++) {
						if(source.equals(buttons.get(j))){
							logger.info("Jumping to: "+locationListq[j][3]+" "+locationListq[j][4]);
							camera.setLocation(MapManager.locationToBetaville(new GPSCoordinate(20.0, Double.parseDouble(locationListq[j][3]), Double.parseDouble(locationListq[j][4]))));
							
							
							GPSCoordinate gps = new GPSCoordinate(20.0, Double.parseDouble(locationListq[j][3]), Double.parseDouble(locationListq[j][4]));
							UTMCoordinate utm = gps.getUTM();
							SceneScape.setUTMZone(utm.getLonZone(), utm.getLatZone());
							
							UTMCoordinate utm2 = new UTMCoordinate(363372, 2640437, 23, 'Q', 20);
//							logger.info("xxxxx"+utm2.getGPSCoordinate());
//							logger.info("xxxxx"+SceneScape.getLonZone()+" "+SceneScape.getLatZone());
							break;
						}
					}
					currentLocalWormholeWindow.close();
				}
			});
			getContentContainer().addWidget(f);
		}
		
//		createWormholeButton = FengGUI.createWidget(FixedButton.class);
//		createWormholeButton.setText("Wormhole");
//		createWormholeButton.setWidth(29);
//		createWormholeButton.addButtonPressedListener(new IButtonPressedListener() {
//			public void buttonPressed(Object source, ButtonPressedEvent e) {
//				if(createWormholeWindow==null){
//					createWormholeWindow = FengGUI.createWidget(CreateWormholeWindow.class);
//					createWormholeWindow.finishSetup();
//					createWormholeWindow.addToDisplay();
//				}
//			}
//		});
//		getContentContainer().addWidget(createWormholeButton);
	}
	
	public void cleanUp(){
		getContentContainer().removeAllWidgets();
	}
	
	public void finishSetup(){
		setTitle("Wormholes of the City");
		setSize(targetWidth, targetHeight);
		setXY(Binding.getInstance().getCanvasWidth()/2-getWidth()/2, Binding.getInstance().getCanvasHeight()/2-getHeight()/2);
	}
	
	public void addToDisplay(){
		setXY(Binding.getInstance().getCanvasWidth()/2-getWidth()/2, Binding.getInstance().getCanvasHeight()/2-getHeight()/2);
	
		GUIGameState.getInstance().getDisp().addWidget(this);
	}
	
	public void close(){
		super.close();
	}
}