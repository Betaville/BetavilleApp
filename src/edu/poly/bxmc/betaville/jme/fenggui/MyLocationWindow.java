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

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.RadioButton;
import org.fenggui.ToggableGroup;
import org.fenggui.composite.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.math.Vector3f;
import com.jme.scene.Node;

import edu.poly.bxmc.betaville.jme.fenggui.extras.GPSView;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.fenggui.extras.OpenGLView;
import edu.poly.bxmc.betaville.jme.fenggui.extras.StreetView;
import edu.poly.bxmc.betaville.jme.fenggui.extras.UTMView;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
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
	private int targetWidth = 300;
	
	private Container locationContainer;
	private Container selectorContainer;
	
	private ToggableGroup<RadioButton<Boolean>> togglableGroup;
	private RadioButton<Boolean> latLon;
	private RadioButton<Boolean> utm;
	private RadioButton<Boolean> ogl;
	private RadioButton<Boolean> str;
	
	private static final String latLonSelection = "Lat/Lon";
	private static final String utmSelection = "UTM";
	private static final String openGLSelection = "OpenGL Units";
	private static final String streetSelection = "Street";

	private GPSView gpsView;
	
	private UTMView utmView;

	private OpenGLView oglView;
	
	private StreetView streetView;
	
	public MyLocationWindow(){
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		locationContainer = FengGUI.createWidget(Container.class);
		locationContainer.setLayoutManager(new RowExLayout(false));
		
		gpsView = new GPSView();
		gpsView.setLayoutData(new RowExLayoutData(true, true));
		
		utmView = new UTMView();
		utmView.setLayoutData(new RowExLayoutData(true, true));
		
		oglView = new OpenGLView();
		oglView.setLayoutData(new RowExLayoutData(true, true));
		
		streetView = new StreetView();
		streetView.setLayoutData(new RowExLayoutData(true, true));
		
		selectorContainer = FengGUI.createWidget(Container.class);
		selectorContainer.setLayoutManager(new RowExLayout(true));
		
		togglableGroup = new ToggableGroup<RadioButton<Boolean>>();
	    latLon = FengGUI.createRadioButton();
	    latLon.setLayoutData(new RowExLayoutData(true, true));
	    latLon.setRadioButtonGroup(togglableGroup);
	    latLon.setText(latLonSelection);
	    
	    utm = FengGUI.createRadioButton();
	    utm.setLayoutData(new RowExLayoutData(true, true));
	    utm.setRadioButtonGroup(togglableGroup);
	    utm.setText(utmSelection);
	    
	    ogl = FengGUI.createRadioButton();
	    ogl.setLayoutData(new RowExLayoutData(true, true));
	    ogl.setRadioButtonGroup(togglableGroup);
	    ogl.setText(openGLSelection);
	    
	    str = FengGUI.createRadioButton();
	    str.setLayoutData(new RowExLayoutData(true, true));
	    str.setRadioButtonGroup(togglableGroup);
	    str.setText(streetSelection);
	    
	    latLon.setSelected(true);
	    selectorContainer.addWidget(latLon, utm, ogl, str);
	    
	    locationContainer.addWidget(selectorContainer, gpsView);
	    
	    togglableGroup.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(Object arg0, SelectionChangedEvent arg1) {
				// remove widgets from location container
				locationContainer.removeAllWidgets();
				
				locationContainer.addWidget(selectorContainer);
				// add the widgets based on the selection
				if(togglableGroup.getSelectedItem().equals(latLon)){
					locationContainer.addWidget(gpsView);
				}
				else if(togglableGroup.getSelectedItem().equals(utm)){
					locationContainer.addWidget(utmView);
				}
				else if(togglableGroup.getSelectedItem().equals(ogl)){
					locationContainer.addWidget(oglView);
				}
				else if(togglableGroup.getSelectedItem().equals(str)){
					locationContainer.addWidget(streetView);
				}
				
				layout();
				
			}
		});
		
		getContentContainer().addWidget(locationContainer);

		try {
			SceneGameState.getInstance().addModuleToUpdateList(new UpdateModule("LocationWindowUpdater"));
		} catch (ModuleNameException e1) {
			e1.printStackTrace();
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
			
			// NEW
			
			UTMCoordinate utm = JME2MapManager.instance.betavilleToUTM(cameraLocation);
			GPSCoordinate gps = utm.getGPS();
			
			// update the location views
			gpsView.updateLocation(gps);
			utmView.updateLocation(utm);
			oglView.updateLocation(cameraLocation);
			streetView.updateLocation(gps);
			
		}

		public void deconstruct(){}

	}
}
