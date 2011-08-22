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
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.jme.map.DecimalDegreeConverter;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;

/**
 * Container to view a Latitude/Longitude coordinate
 * @author Skye Book
 *
 */
public class GPSView extends Container implements LocationView{
	
	private Label title;
	
	private Label latLabel;
	private Label lonLabel;
	private Label latValue;
	private Label lonValue;
	
	private ILocation coordinate = null;
	
	private CheckBox<Boolean> ddDMSOption;
	
	public GPSView(){
		setLayoutManager(new RowExLayout(false));
		
		title = FengGUI.createWidget(Label.class);
		title.setText("GPS View");
		
		Container latCon = FengGUI.createWidget(Container.class);
		latCon.setLayoutManager(new RowExLayout(true));
		Container lonCon = FengGUI.createWidget(Container.class);
		lonCon.setLayoutManager(new RowExLayout(true));
		
		latLabel = FengGUI.createWidget(Label.class);
		latLabel.setText("Latitude");
		latLabel.setLayoutData(new RowExLayoutData(true, true));
		lonLabel = FengGUI.createWidget(Label.class);
		lonLabel.setText("Longitude");
		lonLabel.setLayoutData(new RowExLayoutData(true, true));
		
		latValue = FengGUI.createWidget(Label.class);
		latValue.setLayoutData(new RowExLayoutData(true, true));
		lonValue = FengGUI.createWidget(Label.class);
		lonValue.setLayoutData(new RowExLayoutData(true, true));
		
		latCon.addWidget(latLabel, latValue);
		lonCon.addWidget(lonLabel, lonValue);
		
		ddDMSOption = FengGUI.createCheckBox();
		ddDMSOption.setText("Display in Degrees/Minutes/Seconds");
		ddDMSOption.setSelected(false);
		ddDMSOption.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(Object arg0, SelectionChangedEvent arg1) {
				// only update the view if a coordinate has been previously set
				if(coordinate!=null) doLocationUpdate();
			}
		});
		
		addWidget(latCon, lonCon, ddDMSOption);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.LocationView#setTitle(java.lang.String)
	 */
	public void setTitle(String name){
		title.setText(name);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.LocationView#updateLocation(com.jme.math.Vector3f)
	 */
	public void updateLocation(Vector3f location){
		coordinate = JME2MapManager.instance.betavilleToUTM(location);
		doLocationUpdate();
	}
	
	public void updateLocation(GPSCoordinate location){
		coordinate = location;
		doLocationUpdate();
	}
	
	private void doLocationUpdate(){
		if(ddDMSOption.isSelected()){
			float[] latDMS = DecimalDegreeConverter.ddToDMS(coordinate.getGPS().getLatitude());
			float[] lonDMS = DecimalDegreeConverter.ddToDMS(coordinate.getGPS().getLongitude());
			latValue.setText(latDMS[0]+", "+latDMS[1]+", "+latDMS[2]);
			lonValue.setText(lonDMS[0]+", "+lonDMS[1]+", "+lonDMS[2]);
		}
		else{
			latValue.setText(""+coordinate.getGPS().getLatitude());
			lonValue.setText(""+coordinate.getGPS().getLongitude());
		}
	}
}
