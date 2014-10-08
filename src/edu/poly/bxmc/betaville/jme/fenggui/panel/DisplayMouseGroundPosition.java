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
import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WindowClosedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.input.MouseInput;
import com.jme.math.Plane;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.shape.AxisRods;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.fenggui.extras.GPSView;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.DecimalDegreeConverter;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.module.FrameSyncModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;

/**
 * Utility window for checking the mouse pointer's position on the ground
 * @author Skye Book
 *
 */
public class DisplayMouseGroundPosition extends Window implements IBetavilleWindow, IPanelOnScreenAwareWindow{
	private static final Logger logger = Logger.getLogger(DisplayMouseGroundPosition.class);
	
	private Container gpsView;
	
	private Label latLabel;
	private Label lonLabel;
	private Label latValue;
	private Label lonValue;
	
	private CheckBox<Boolean> ddDMSOption;
	private CheckBox<Boolean> showAxisOption;
	
	private PointerModule pointerModule = new PointerModule();
	

	/**
	 * 
	 */
	public DisplayMouseGroundPosition() {
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		setupGPSView();
		
		// default to the GPS view
		getContentContainer().addWidget(gpsView);
	}
	
	private void setupGPSView(){
		gpsView = FengGUI.createWidget(Container.class);
		gpsView.setLayoutManager(new RowExLayout(false));
		
		Container latCon = FengGUI.createWidget(Container.class);
		latCon.setLayoutManager(new RowExLayout(true));
		Container lonCon = FengGUI.createWidget(Container.class);
		lonCon.setLayoutManager(new RowExLayout(true));
		
		latLabel = FengGUI.createWidget(Label.class);
		latLabel.setText(Labels.get("Generic.latitude"));
		latLabel.setLayoutData(new RowExLayoutData(true, true));
		lonLabel = FengGUI.createWidget(Label.class);
		lonLabel.setText(Labels.get("Generic.longitude"));
		lonLabel.setLayoutData(new RowExLayoutData(true, true));
		
		latValue = FengGUI.createWidget(Label.class);
		latValue.setLayoutData(new RowExLayoutData(true, true));
		lonValue = FengGUI.createWidget(Label.class);
		lonValue.setLayoutData(new RowExLayoutData(true, true));
		
		latCon.addWidget(latLabel, latValue);
		lonCon.addWidget(lonLabel, lonValue);
		
		ddDMSOption = FengGUI.createCheckBox();
		ddDMSOption.setText(Labels.get(GPSView.class, "dd_dms_option"));
		ddDMSOption.setSelected(false);
		
		showAxisOption = FengGUI.createCheckBox();
		showAxisOption.setText(Labels.get(this.getClass(), "show_axis_rods"));
		showAxisOption.setSelected(false);
		
		gpsView.addWidget(latCon, lonCon, ddDMSOption, showAxisOption);
		
		addWindowClosedListener(new IWindowClosedListener() {
			
			public void windowClosed(WindowClosedEvent windowClosedEvent) {
				SceneGameState.getInstance().removeModuleFromUpdateList(pointerModule);
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.panel.IPanelOnScreenAwareWindow#panelTurnedOn()
	 */
	public void panelTurnedOn() {
		// add the module to the update list
		try {
			logger.info("Adding pointerModule to update list");
			SceneGameState.getInstance().addModuleToUpdateList(pointerModule);
		} catch (ModuleNameException e) {
			logger.error("The module could not be added to the update list!");
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setHeight(getHeight()+10);
	}
	
	private class PointerModule extends Module implements FrameSyncModule{
		
		Ray mouseRay;
		Vector3f location = new Vector3f();
		Vector2f screenPosition = new Vector2f();
		Vector3f worldCoords = new Vector3f();
		
		AxisRods axisRods = new AxisRods("PointerRods", true, Scale.fromMeter(5));
		float rodScale=.05f;
		
		boolean wasShowingRods=false;
		boolean forceOff;

		public PointerModule() {
			super("PointerLocationModule");
		}

		public void deconstruct() {}

		public void frameUpdate(float timePerFrame) {
			// update the screen position
			screenPosition.x=MouseInput.get().getXAbsolute();
			screenPosition.y=MouseInput.get().getYAbsolute();
			
			worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f, worldCoords);
			mouseRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(),
					worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));
			if(mouseRay.intersectsWherePlane(new Plane(new Vector3f(0,1,0), 0), location)){
				forceOff=false;
				GPSCoordinate gps = JME2MapManager.instance.betavilleToUTM(location).getGPS();
				
				// calculate size of rods
				float distance = SceneGameState.getInstance().getCamera().getLocation().distance(location);
				axisRods.updateGeometry(distance*rodScale, distance*rodScale*.125f, true);
				axisRods.setLocalTranslation(location.clone());
				
				if(ddDMSOption.isSelected()){
					// convert to from DD to DMS
					float[] latDMS = DecimalDegreeConverter.ddToDMS(gps.getLatitude());
					float[] lonDMS = DecimalDegreeConverter.ddToDMS(gps.getLongitude());
					
					latValue.setText(latDMS[0]+", "+latDMS[1]+", "+latDMS[2]);
					lonValue.setText(lonDMS[0]+", "+lonDMS[1]+", "+lonDMS[2]);
				}
				else{
					latValue.setText(""+gps.getLatitude());
					lonValue.setText(""+gps.getLongitude());
				}
			}
			else{
				forceOff=true;
				latValue.setText(Labels.get(DisplayMouseGroundPosition.class.getSimpleName()+".ground_not_touched"));
				lonValue.setText(Labels.get(DisplayMouseGroundPosition.class.getSimpleName()+".ground_not_touched"));
			}
			
			if(wasShowingRods!=showAxisOption.isSelected()){
				if(showAxisOption.isSelected() && !forceOff){
					SceneGameState.getInstance().getGroundBoxNode().attachChild(axisRods);
					axisRods.updateRenderState();
				}
				else{
					SceneGameState.getInstance().getGroundBoxNode().detachChild(axisRods);
				}
				if(forceOff){
					wasShowingRods=false;
				}
				else{
					wasShowingRods=showAxisOption.isSelected();
				}
			}
		}
		
	}
}
