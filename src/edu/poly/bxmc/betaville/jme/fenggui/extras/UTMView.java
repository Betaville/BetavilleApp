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

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;

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
		title.setText(Labels.get(this.getClass().getSimpleName()+".title"));
		
		Container latCon = FengGUI.createWidget(Container.class);
		latCon.setLayoutManager(new RowExLayout(true));
		Container lonCon = FengGUI.createWidget(Container.class);
		lonCon.setLayoutManager(new RowExLayout(true));
		Container northingContainer = FengGUI.createWidget(Container.class);
		northingContainer.setLayoutManager(new RowExLayout(true));
		Container eastingContainer = FengGUI.createWidget(Container.class);
		eastingContainer.setLayoutManager(new RowExLayout(true));
		
		latZoneLabel = FengGUI.createWidget(Label.class);
		latZoneLabel.setText(Labels.get(this.getClass().getSimpleName()+".lat_zone"));
		latZoneLabel.setLayoutData(new RowExLayoutData(true, true));
		lonZoneLabel = FengGUI.createWidget(Label.class);
		lonZoneLabel.setText(Labels.get(this.getClass().getSimpleName()+".lon_zone"));
		lonZoneLabel.setLayoutData(new RowExLayoutData(true, true));
		northingLabel = FengGUI.createWidget(Label.class);
		northingLabel.setText(Labels.get(this.getClass().getSimpleName()+".northing"));
		northingLabel.setLayoutData(new RowExLayoutData(true, true));
		eastingLabel = FengGUI.createWidget(Label.class);
		eastingLabel.setText(Labels.get(this.getClass().getSimpleName()+".easting"));
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
		updateLocation(coordinate.getUTM());
	}
	
	public void updateLocation(UTMCoordinate location){
		latZoneValue.setText(""+location.getUTM().getLatZone());
		lonZoneValue.setText(""+location.getUTM().getLonZone());
		northingValue.setText(""+location.getUTM().getNorthing()+toDecimal(location.getUTM().getNorthingCentimeters()));
		eastingValue.setText(""+location.getUTM().getEasting()+toDecimal(location.getUTM().getEastingCentimeters()));
	}
	
	private String toDecimal(short cm){
		String value = Float.toString(((float)cm)/100f);
		return new String(value.substring(1, value.length()));
	}
}
