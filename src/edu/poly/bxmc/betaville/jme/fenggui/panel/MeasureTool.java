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
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.GPSView;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * Tool for measuring the distance between two points on the ground.
 * @author Skye Book
 *
 */
public class MeasureTool extends Window implements IBetavilleWindow{
	private static final Logger logger = Logger.getLogger(MeasureTool.class);

	private GPSView point1;
	private GPSView point2;

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

		Container views = FengGUI.createWidget(Container.class);
		views.setLayoutManager(new RowExLayout(true));

		point1 = new GPSView();
		point1.setTitle("Point 1");
		point2 = new GPSView();
		point2.setTitle("Point 2");

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
						point1.updateLocation(JME2MapManager.instance.betavilleToUTM(point1Loc));
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
						point2.updateLocation(JME2MapManager.instance.betavilleToUTM(point2Loc));
						point2NeverSet=false;
						updateCalculations();
					}
				});
			}
		});

		Container buttons = FengGUI.createWidget(Container.class);
		buttons.setLayoutManager(new RowExLayout(true));
		buttons.addWidget(selectPoint1, selectPoint2);


		distance = FengGUI.createWidget(Label.class);
		distance.setText(distancePrefix+"Points Not Set");


		// default to the GPS view
		views.addWidget(point1, point2);
		getContentContainer().addWidget(views, buttons, distance);
	}

	private void updateCalculations(){
		layout();
		// only do the update if both points have been set during this runtime
		if(point1NeverSet && point2NeverSet) return;

		distance.setText(distancePrefix+Scale.toMeter(point1Loc.distance(point2Loc))+"m");
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("Distance Tool");
	}

}
