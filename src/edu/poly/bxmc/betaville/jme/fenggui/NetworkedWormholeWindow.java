/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Item;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.StaticLayout;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.CityManager;
import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.FindCityWindow.ISelectionDeselectionListener;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.model.City;
import edu.poly.bxmc.betaville.model.Wormhole;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.search.GeoNamesSearchResult;

/**
 * @author Skye Book
 *
 */
public class NetworkedWormholeWindow extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(NetworkedWormholeWindow.class);

	private int targetWidth = 250;
	private int targetHeight = 300;

	private ComboBox citySelector;
	private ComboBox wormholeSelector;
	private Container buttonContainer;

	private Button createCity;

	private FindCityWindow fcw;

	private AtomicBoolean currentlySearching = new AtomicBoolean(false);

	/**
	 * 
	 */
	public NetworkedWormholeWindow() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		createCityList();
		createWormholeSelector();
		createButtonContainer();
		getContentContainer().addWidget(citySelector, wormholeSelector, buttonContainer);
	}

	private void createCityList(){
		citySelector = FengGUI.createWidget(ComboBox.class);

		try {
			List<City> cities = NetPool.getPool().getConnection().findAllCities();
			for(City c : cities){
				citySelector.addItem(new CityItem(c));
			}

			citySelector.addSelectionChangedListener(new ISelectionChangedListener() {

				public void selectionChanged(Object sender,
						SelectionChangedEvent selectionChangedEvent){
					logger.debug("selection changed");
					updateWormholeList(((CityItem)citySelector.getSelectedItem()).getCity().getCityID());
				}
			});
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createWormholeSelector(){
		wormholeSelector = FengGUI.createWidget(ComboBox.class);
		wormholeSelector.addItem("Nothing!");
	}

	private void createButtonContainer(){
		buttonContainer = FengGUI.createWidget(Container.class);
		buttonContainer.setLayoutManager(new RowExLayout(true));

		Button go = FengGUI.createWidget(Button.class);
		go.setText(Labels.generic("go")+"!");
		go.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {

				// perform the wormhole jump here
				Wormhole w = ((WormholeItem)wormholeSelector.getSelectedItem()).getWormhole();

				logger.info("Wormhole to "+w.getName()+" selected.");
				MapManager.setUTMZone(w.getLocation().getLonZone(), w.getLocation().getLatZone());
				CityManager.swapCities(SettingsPreferences.getCurrentCityID(), w.getCityID(), w.getLocation());

				// finally we move the camera
				SceneGameState.getInstance().getCamera().setLocation(JME2MapManager.instance.locationToBetaville(w.getLocation()));
				ILocation lookAt = w.getLocation().clone();
				lookAt.getUTM().move(300, 300, 0);
				SceneGameState.getInstance().getCamera().lookAt(JME2MapManager.instance.locationToBetaville(lookAt), Vector3f.UNIT_Y);
			}
		});

		Button createHere = FengGUI.createWidget(Button.class);
		createHere.setText(Labels.get(this.getClass(), "create_here"));
		createHere.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				try {
					int response = NetPool.getPool().getSecureConnection().addWormhole(JME2MapManager.instance.betavilleToUTM(SceneGameState.getInstance().getCamera().getLocation()), "test", SettingsPreferences.getCurrentCityID());
					logger.info("New Wormhole created (response: "+response+")");
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		Button findCity = FengGUI.createWidget(Button.class);
		findCity.setText(Labels.get(this.getClass(), "find_city"));
		findCity.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(fcw==null){
					fcw = FengGUI.createWidget(FindCityWindow.class);
					fcw.finishSetup();
					StaticLayout.center(fcw, GUIGameState.getInstance().getDisp());

					/* This listener ensures that the create city button is only enabled when
					 * there is a city selected */
					fcw.addSelectionDeslectionListener(new ISelectionDeselectionListener() {
						public void resultSelected(GeoNamesSearchResult result) {
							createCity.setEnabled(true);
						}
						public void resultDeselected() {
							createCity.setEnabled(false);
						}
					});
				}
				if(!fcw.isInWidgetTree()) GUIGameState.getInstance().getDisp().addWidget(fcw);
			}
		});

		createCity = FengGUI.createWidget(Button.class);
		createCity.setText(Labels.get(this.getClass(), "create_city"));
		createCity.setEnabled(false);
		createCity.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				try {
					GeoNamesSearchResult city = fcw.getSelectedCity();
					int newCityID = NetPool.getPool().getConnection().addCity(city.getToponym().getCountryCode(), "", "");
					int response = NetPool.getPool().getSecureConnection().addWormhole(city.getLocation(), city.getMainTitle(), newCityID);
					if(response>0){
						// Success!
						FengUtils.showNewDismissableWindow("Betaville", "Wormhole successfully created", Labels.get("Generic.ok"), true);
					}
					else{
						FengUtils.showNewDismissableWindow("Betaville", "Wormhole could not be created, server returned error code " + response, Labels.get("Generic.ok"), true);
					}
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		buttonContainer.addWidget(go);
		//buttonContainer.addWidget(createHere);
		buttonContainer.addWidget(findCity);
		buttonContainer.addWidget(createCity);
	}

	private synchronized void updateWormholeList(final int cityID){
		wormholeSelector.getList().clear();
		wormholeSelector.addItem("Searching...");
		//citySelector.setEnabled(false);
		SettingsPreferences.getGUIThreadPool().submit(new Runnable() {
			public void run() {
												
				/* clem removed this dec 15 2014.
					seemed to be set but never reset to false. 
					if(currentlySearching.get()) return;
					currentlySearching.set(true);
				*/
				
				logger.info("Looking for wormholes in city " + cityID);
				try {
					List<Wormhole> wormholes = NetPool.getPool().getConnection().getAllWormholesInCity(cityID);
					if(wormholes!=null){
						logger.info(wormholes.size()+" wormholes found");
						wormholeSelector.getList().clear();
						for(Wormhole w : wormholes){
							wormholeSelector.addItem(new WormholeItem(w));
						}
					}
					else{
						logger.warn("Wormhole response was null");
						wormholeSelector.getList().clear();
						wormholeSelector.addItem("No Wormholes");
					}
					// go back to normal status
					//citySelector.setEnabled(true);
					setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
					// clem currentlySearching.set(false);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				} catch (Error e) {
					System.out.println("Default Exception - Model loading Issue");
					e.printStackTrace();
					
				}
				
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);
	}

	private class CityItem extends Item{
		private City city;

		private CityItem(City city){
			super(city.getCity(), citySelector.getList().getAppearance());
			this.city=city;
		}

		private City getCity(){
			return city;
		}
	}

	private class WormholeItem extends Item{
		private Wormhole wormhole;

		private WormholeItem(Wormhole wormhole){
			super(wormhole.getName(), wormholeSelector.getList().getAppearance());
			this.wormhole=wormhole;
		}

		private Wormhole getWormhole(){
			return wormhole;
		}
	}
}
