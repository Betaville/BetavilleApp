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
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.jme.map.DecimalDegreeConverter;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;

/**
 * Container to view a Latitude/Longitude coordinate
 * @author Skye Book
 *
 */
public class UTMView extends Container implements LocationView{
	
	private Label title;
	
	private Label latZoneLabel;
	private Label lonZoneLabel;
	private Label northingLabel;
	private Label eastingLabel;
	private Label latZoneValue;
	private Label lonZoneValue;
	private Label northingValue;
	private Label eastingValue;
	
	public UTMView(){
		setLayoutManager(new RowExLayout(false));
		
		title = FengGUI.createWidget(Label.class);
		title.setText("UTM View");
		
		Container latCon = FengGUI.createWidget(Container.class);
		latCon.setLayoutManager(new RowExLayout(true));
		Container lonCon = FengGUI.createWidget(Container.class);
		lonCon.setLayoutManager(new RowExLayout(true));
		Container northingContainer = FengGUI.createWidget(Container.class);
		northingContainer.setLayoutManager(new RowExLayout(true));
		Container eastingContainer = FengGUI.createWidget(Container.class);
		eastingContainer.setLayoutManager(new RowExLayout(true));
		
		latZoneLabel = FengGUI.createWidget(Label.class);
		latZoneLabel.setText("Latitude Zone");
		latZoneLabel.setLayoutData(new RowExLayoutData(true, true));
		lonZoneLabel = FengGUI.createWidget(Label.class);
		lonZoneLabel.setText("Longitude Zone");
		lonZoneLabel.setLayoutData(new RowExLayoutData(true, true));
		northingLabel = FengGUI.createWidget(Label.class);
		northingLabel.setText("Easting");
		northingLabel.setLayoutData(new RowExLayoutData(true, true));
		eastingLabel = FengGUI.createWidget(Label.class);
		eastingLabel.setText("Northing");
		eastingLabel.setLayoutData(new RowExLayoutData(true, true));
		
		latZoneValue = FengGUI.createWidget(Label.class);
		latZoneValue.setLayoutData(new RowExLayoutData(true, true));
		lonZoneValue = FengGUI.createWidget(Label.class);
		lonZoneValue.setLayoutData(new RowExLayoutData(true, true));
		northingValue = FengGUI.createWidget(Label.class);
		northingValue.setLayoutData(new RowExLayoutData(true, true));
		eastingValue = FengGUI.createWidget(Label.class);
		eastingValue.setLayoutData(new RowExLayoutData(true, true));
		
		latCon.addWidget(latZoneLabel, latZoneValue);
		lonCon.addWidget(lonZoneLabel, lonZoneValue);
		northingContainer.addWidget(northingLabel, northingValue);
		eastingContainer.addWidget(eastingLabel, eastingValue);
		
		addWidget(latCon, lonCon, northingContainer, eastingContainer);
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
		ILocation coordinate = JME2MapManager.instance.betavilleToUTM(location);
		
		latZoneValue.setText(""+coordinate.getUTM().getLatZone());
		lonZoneValue.setText(""+coordinate.getUTM().getLonZone());
		northingValue.setText(""+coordinate.getUTM().getNorthing()+"."+coordinate.getUTM().getNorthingCentimeters());
		eastingValue.setText(""+coordinate.getUTM().getEasting()+"."+coordinate.getUTM().getEastingCentimeters());
	}
}
