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

import java.io.IOException;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.layout.RowExLayout;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.search.Geocoder;
import edu.poly.bxmc.betaville.search.OpenStreetMapGeocoder;

/**
 * Container to retrieve and view the street
 * at a location
 * @author Skye Book
 *
 */
public class StreetView extends Container implements LocationView{

	private Label title;

	private Label spacer;
	private Label street;

	private boolean useUpdateLabel = true;
	private UpdatingLabel updating;
	private final String updatePrefix = "Updating";

	private long lastStreetUpdate = -1;
	private long streetUpdateInterval = 1000;
	private boolean streetUpdateIsInProgress = false;
	private Geocoder geocoder;

	public StreetView(){
		setLayoutManager(new RowExLayout(false));

		geocoder = new OpenStreetMapGeocoder();

		title = FengGUI.createWidget(Label.class);
		title.setText(Labels.get(this.getClass().getSimpleName()+".title"));

		spacer = FengGUI.createWidget(Label.class);
		spacer.setText("\n                       ");

		street = FengGUI.createWidget(Label.class);
		street.setWordWarping(false);
		street.setMultiline(true);
		street.setText(Labels.get(this.getClass().getSimpleName()+".location_not_updated"));

		updating = FengGUI.createWidget(UpdatingLabel.class);
		updating.setText(updatePrefix);

		addWidget(spacer, street);
	}
	
	/**
	 * Sets whether or not an "update" placeholder label should be
	 * used while reverse lookups are taking place
	 * @param useUpdateLabel True to use the placeholder
	 */
	public void useUpdateLabel(boolean useUpdateLabel){
		this.useUpdateLabel=useUpdateLabel;
	}
	
	/**
	 * Checks whether or not an "update" placeholder label should be
	 * used while reverse lookups are taking place
	 * @return True if the placeholder is being used
	 */
	public boolean usesUpdateLabel(){
		return useUpdateLabel;
	}
	
	/**
	 * Sets the {@link Geocoder} to be used in street lookups
	 * @param clazz
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void setGeocoder(Class<? extends Geocoder> clazz) throws InstantiationException, IllegalAccessException{
		geocoder = clazz.newInstance();
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
		updateLocation(JME2MapManager.instance.betavilleToUTM(location).getGPS());
	}

	public void updateLocation(final GPSCoordinate location){
		if(System.currentTimeMillis()-lastStreetUpdate>streetUpdateInterval && !streetUpdateIsInProgress){
			// lock out other attempts at updating while this update is in progress
			streetUpdateIsInProgress=true;

			// off-load this onto the thread pool so that we don't 
			SettingsPreferences.getGUIThreadPool().execute(new Runnable() {

				@Override
				public void run() {
					try {
						if(useUpdateLabel){
							updating.start();
							removeWidget(street);
							addWidget(updating);
						}

						String streetResponse = geocoder.reverse(location);
						if(streetResponse!=null){
							street.setText(streetResponse);
						}
						else{
							street.setText(Labels.get(StreetView.this.getClass().getSimpleName()+".nearest_street_not_retrieved"));
						}
					} catch (IOException e) {
						street.setText(Labels.get(StreetView.this.getClass().getSimpleName()+".geocoder_fail_to_connect"));
					} finally {
						/* check if the update label is in the widget tree to avoid
						 * having the label stuck on the screen if someone disabled it
						 * while it was active
						 */
						if(useUpdateLabel || updating.isInWidgetTree()){
							updating.stop();
							removeWidget(updating);
							addWidget(street);
						}
						lastStreetUpdate=System.currentTimeMillis();
						streetUpdateIsInProgress=false;
					}
				}
			});
		}
		else if(streetUpdateIsInProgress){

		}
	}
}
