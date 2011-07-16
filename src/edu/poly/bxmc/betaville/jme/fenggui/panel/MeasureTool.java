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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Item;
import org.fenggui.Label;
import org.fenggui.RadioButton;
import org.fenggui.ToggableGroup;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.GPSView;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.fenggui.extras.LocationView;
import edu.poly.bxmc.betaville.jme.fenggui.extras.OpenGLView;
import edu.poly.bxmc.betaville.jme.fenggui.extras.UTMView;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * Tool for measuring the distance between two points on the ground.
 * @author Skye Book
 *
 */
public class MeasureTool extends Window implements IBetavilleWindow{
	private static final Logger logger = Logger.getLogger(MeasureTool.class);

	private Container selectorContainer;
	
	private ToggableGroup<RadioButton<Boolean>> togglableGroup;
	private RadioButton<Boolean> latLon;
	private RadioButton<Boolean> utm;
	private RadioButton<Boolean> ogl;
	
	private static final String latLonSelection = "Lat/Lon";
	private static final String utmSelection = "UTM";
	private static final String openGLSelection = "OpenGL Units";

	private Container gpsViews;
	private Container utmViews;
	private Container oglViews;

	private LocationView point1;
	private LocationView point2;

	private GPSView gpsPoint1;
	private GPSView gpsPoint2;
	
	private UTMView utmPoint1;
	private UTMView utmPoint2;

	private OpenGLView oglPoint1;
	private OpenGLView oglPoint2;

	private Container pickPointButtons;

	private Label distance;
	private static final String distancePrefix="Distance: ";


	private boolean point1NeverSet=true;
	private boolean point2NeverSet=true;
	private Vector3f point1Loc=new Vector3f(0, 0, 0);
	private Vector3f point2Loc=new Vector3f(0, 0, 0);

	/**
	 * 
	 */
	public MeasureTool() {
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		selectorContainer = FengGUI.createWidget(Container.class);
		selectorContainer.setLayoutManager(new RowExLayout(true));
		//selectorContainer.setLayoutData(new RowExLayoutData(true, true));

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
	    
	    latLon.setSelected(true);
	    selectorContainer.addWidget(latLon, utm, ogl);
	    
	    togglableGroup.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(Object arg0, SelectionChangedEvent arg1) {
				// remove all of the widgets
				getContentContainer().removeAllWidgets();
				
				getContentContainer().addWidget(selectorContainer);
				// add the widgets based on the selection
				if(togglableGroup.getSelectedItem().equals(latLon)){
					point1=gpsPoint1;
					point2=gpsPoint2;
					getContentContainer().addWidget(gpsViews, pickPointButtons, distance);
				}
				else if(togglableGroup.getSelectedItem().equals(utm)){
					point1=utmPoint1;
					point2=utmPoint2;
					getContentContainer().addWidget(utmViews, pickPointButtons, distance);
				}
				else if(togglableGroup.getSelectedItem().equals(ogl)){
					point1=oglPoint1;
					point2=oglPoint2;
					getContentContainer().addWidget(oglViews, pickPointButtons, distance);
				}
				
				// load the values into the new views
				if(!point1NeverSet){
					point1.updateLocation(point1Loc);
				}
				
				if(!point2NeverSet){
					point2.updateLocation(point2Loc);
				}
				
				updateCalculations();
			}
		});
	    
		setupGPSViews();
		setupUTMViews();
		setupOGLViews();
		setupButtons();
		
		point1=gpsPoint1;
		point2=gpsPoint2;
		
		distance = FengGUI.createWidget(Label.class);
		distance.setText(distancePrefix+"Points Not Set");

		getContentContainer().addWidget(selectorContainer, gpsViews, pickPointButtons, distance);
	}

	private void setupButtons(){
		Button selectPoint1 = FengGUI.createWidget(Button.class);
		selectPoint1.setLayoutData(new RowExLayoutData(true, true));
		selectPoint1.setText("Select Point 1");
		selectPoint1.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {

				SettingsPreferences.getGUIThreadPool().submit(new Runnable() {
					public void run() {
						while(!SceneGameState.getInstance().isGroundSelectorAttached()){
							try {
								Thread.sleep(25);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						point1Loc = SceneGameState.getInstance().getGroundSelectorLocation().clone();
						point1.updateLocation(point1Loc);
						point1NeverSet=false;
						updateCalculations();
					}
				});
			}
		});

		Button selectPoint2 = FengGUI.createWidget(Button.class);
		selectPoint2.setLayoutData(new RowExLayoutData(true, true));
		selectPoint2.setText("Select Point 2");
		selectPoint2.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {

				SettingsPreferences.getGUIThreadPool().submit(new Runnable() {
					public void run() {
						while(!SceneGameState.getInstance().isGroundSelectorAttached()){
							try {
								Thread.sleep(25);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						point2Loc = SceneGameState.getInstance().getGroundSelectorLocation().clone();
						point2.updateLocation(point2Loc);
						point2NeverSet=false;
						updateCalculations();
					}
				});
			}
		});

		pickPointButtons = FengGUI.createWidget(Container.class);
		pickPointButtons.setLayoutManager(new RowExLayout(true));
		pickPointButtons.addWidget(selectPoint1, selectPoint2);
	}

	private void setupGPSViews(){
		gpsViews = FengGUI.createWidget(Container.class);
		gpsViews.setLayoutManager(new RowExLayout(true));
		gpsViews.setLayoutData(new RowExLayoutData(true, true));

		gpsPoint1 = new GPSView();
		gpsPoint1.setTitle("Point 1");
		gpsPoint1.setLayoutData(new RowExLayoutData(true, true));
		gpsPoint2 = new GPSView();
		gpsPoint2.setTitle("Point 2");
		gpsPoint2.setLayoutData(new RowExLayoutData(true, true));

		gpsViews.addWidget(gpsPoint1, gpsPoint2);
	}
	
	private void setupUTMViews(){
		utmViews = FengGUI.createWidget(Container.class);
		utmViews.setLayoutManager(new RowExLayout(true));
		utmViews.setLayoutData(new RowExLayoutData(true, true));
		
		utmPoint1 = new UTMView();
		utmPoint1.setTitle("Point 1");
		utmPoint1.setLayoutData(new RowExLayoutData(true, true));
		utmPoint2 = new UTMView();
		utmPoint2.setTitle("Point 2");
		utmPoint2.setLayoutData(new RowExLayoutData(true, true));
		
		utmViews.addWidget(utmPoint1, utmPoint2);
	}

	private void setupOGLViews(){
		oglViews = FengGUI.createWidget(Container.class);
		oglViews.setLayoutManager(new RowExLayout(true));
		oglViews.setLayoutData(new RowExLayoutData(true, true));

		oglPoint1 = new OpenGLView();
		oglPoint1.setTitle("Point 1");
		oglPoint1.setLayoutData(new RowExLayoutData(true, true));
		oglPoint2 = new OpenGLView();
		oglPoint2.setTitle("Point 2");
		oglPoint2.setLayoutData(new RowExLayoutData(true, true));

		oglViews.addWidget(oglPoint1, oglPoint2);
	}

	private void updateCalculations(){
		layout();
		// only do the update if both points have been set during this runtime
		if(point1NeverSet && point2NeverSet) return;
		
		if(togglableGroup.getSelectedItem().equals(latLon)){
			GPSCoordinate gps1 = JME2MapManager.instance.betavilleToUTM(point1Loc).getGPS();
			GPSCoordinate gps2 = JME2MapManager.instance.betavilleToUTM(point2Loc).getGPS();
			
			distance.setText(JME2MapManager.greatCircleDistanced(gps1, gps2)+"m");
		}
		else if(togglableGroup.getSelectedItem().equals(utm)){
			distance.setText(distancePrefix+Scale.toMeter(point1Loc.distance(point2Loc))+"m");
		}
		else if(togglableGroup.getSelectedItem().equals(ogl)){
			distance.setText(point1Loc.distance(point2Loc)+" GL Units");
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("Distance Tool");
	}

	private class SelectorItem extends Item{
		String name;
		private SelectorItem(String name){
			super();
			this.name=name;
		}
		
		public String getText(){
			return name;
		}
	}

}
