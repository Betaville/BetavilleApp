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
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowLayout;

import com.jme.math.Vector3f;
import com.jme.scene.Node;

import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.module.LocalSceneModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;

/**
 * Displays the camera's location and orientation
 * @author Skye Book
 *
 */
public class MyLocationWindow extends Window implements IBetavilleWindow {
	private static Logger logger = Logger.getLogger(MyLocationWindow.class);
	
	private int targetWidth = 300;
	
	private enum Mode {UTM, GPS};
	private Mode displayMode = Mode.GPS;
	
	private FixedButton goGPS;
	private FixedButton goUTM;
	
	private Container gpsContainer;
	private Label lat;
	private Label lon;
	
	private Container utmContainer;
	private Label zone;
	private Label northing;
	private Label easting;
	
	public MyLocationWindow(){
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		goGPS = FengGUI.createWidget(FixedButton.class);
		goGPS.setText("GPS");
		goGPS.setWidth(goGPS.getWidth()+10);
		goGPS.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(displayMode.equals(Mode.UTM)){
					switchModes();
				}
			}
		});
		
		goUTM = FengGUI.createWidget(FixedButton.class);
		goUTM.setText("UTM");
		goUTM.setWidth(goUTM.getWidth()+10);
		goUTM.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(displayMode.equals(Mode.GPS)){
					switchModes();
				}
			}
		});
		
		Container buttonContainer = FengGUI.createWidget(Container.class);
		buttonContainer.setLayoutManager(new RowLayout(true));
		buttonContainer.addWidget(goGPS, goUTM);
		
		gpsContainer = FengGUI.createWidget(Container.class);
		gpsContainer.setLayoutManager(new RowLayout(true));
		lat = FengGUI.createWidget(Label.class);
		lon = FengGUI.createWidget(Label.class);
		lat.setText("lat");
		lon.setText("lon");
		lat.setWidth(targetWidth/2);
		lon.setWidth(targetWidth/2);
		gpsContainer.addWidget(lat, lon);
		
		utmContainer = FengGUI.createWidget(Container.class);
		utmContainer.setLayoutManager(new RowLayout(true));
		zone = FengGUI.createWidget(Label.class);
		northing = FengGUI.createWidget(Label.class);
		easting = FengGUI.createWidget(Label.class);
		zone.setText("zone");
		northing.setText("northing");
		easting.setText("easting");
		utmContainer.addWidget(zone, northing, easting);
		
		getContentContainer().addWidget(buttonContainer, gpsContainer);
		
		try {
			SceneGameState.getInstance().addModuleToUpdateList(new UpdateModule("LocationWindowUpdater"));
		} catch (ModuleNameException e1) {
			e1.printStackTrace();
		}
	}
	
	private void switchModes(){
		if(displayMode.equals(Mode.GPS)){
			getContentContainer().removeWidget(gpsContainer);
			getContentContainer().addWidget(utmContainer);
			displayMode=Mode.UTM;
		}
		else{
			getContentContainer().removeWidget(utmContainer);
			getContentContainer().addWidget(gpsContainer);
			displayMode=Mode.GPS;
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup(){
		setSize(targetWidth, getHeight()+35);
		setTitle("My Location");
	}
	
	private class UpdateModule extends Module implements LocalSceneModule{

		public UpdateModule(String name){
			super(name);
		}

		public void initialize(Node scene){}

		public void onUpdate(Node scene, Vector3f cameraLocation, Vector3f cameraDirection){
			if(!isInWidgetTree()) return;  // No need to update if the window isn't being shown..
			UTMCoordinate utm = MapManager.betavilleToUTM(cameraLocation);
			GPSCoordinate gps = utm.getGPS();
			
			// update UTM
			zone.setText(utm.getLonZone()+""+utm.getLatZone());
			northing.setText(utm.getNorthing()+"N");
			easting.setText(utm.getEasting()+"E");
			
			// update GPS
			lat.setText(Double.toString(gps.getLatitude()));
			lon.setText(Double.toString(gps.getLongitude()));
		}
		
		public void deconstruct(){}
		
	}
}
