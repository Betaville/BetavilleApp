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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;
import org.fenggui.layout.StaticLayout;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BlockingScrollContainer;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.GeoToolsCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.net.wfs.WFSConnection;

/**
 * @author Skye Book
 *
 */
public class AddLayersWindow extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(AddLayersWindow.class);

	private WFSConnection wfs;

	private int targetHeight=200;
	private int targetWidth=300;

	private BlockingScrollContainer sc;
	private Container isc;

	private Button wfsConnectButton;
	
	private CreatePrimitiveWindow primitiveWindow;

	/**
	 * 
	 */
	public AddLayersWindow() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		getContentContainer().setSize(targetWidth, targetHeight);
		setupScroller();
		createConnectButton();
		getContentContainer().addWidget(wfsConnectButton);
		setupPrimitiveWindow();
		//getContentContainer().addWidget(sc);
	}
	
	private void setupPrimitiveWindow(){
		primitiveWindow = FengGUI.createWidget(CreatePrimitiveWindow.class);
		primitiveWindow.finishSetup();
	}

	private void setupScroller(){
		sc = FengGUI.createWidget(BlockingScrollContainer.class);
		sc.setSize(targetWidth, targetHeight-20);
		getContentContainer().addWidget(sc);
		sc.setLayoutData(new RowExLayoutData(true, true));
		sc.setShowScrollbars(true);
		isc = FengGUI.createWidget(Container.class);
		isc.setLayoutManager(new RowExLayout(false));
		sc.setInnerWidget(isc);
		sc.layout();
	}

	private void createConnectButton(){
		wfsConnectButton = FengGUI.createWidget(Button.class);
		wfsConnectButton.setText("Connect to GeoServer");
		wfsConnectButton.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				if(wfs!=null) return;
				
				SettingsPreferences.getGUIThreadPool().submit(new Runnable() {
					
					public void run() {
						wfsConnectButton.setEnabled(false);
						
						try {
							wfs = new WFSConnection("http://192.168.1.6:8080/geoserver/");
							for(String typeName : wfs.getAvailableLayers("")){
								createLayerEntry(typeName);
							}
						} catch (IOException e) {
							logger.error("Can't connect to your GeoServer because no one likes you. ", e);
							GUIGameState.getInstance().getDisp().addWidget(
									FengUtils.createDismissableWindow("Betaville", "Could not connect to GeoServer!", "OK", true));
						}
						
						wfsConnectButton.setEnabled(true);
					}
				});
			}
		});
	}

	private void createLayerEntry(String layerName){
		LayerContainer lc = FengGUI.createWidget(LayerContainer.class);
		lc.registerWindows(this, primitiveWindow);
		lc.initialize(layerName);
		isc.addWidget(lc);
	}
	
	public WFSConnection getWFSConnection(){
		return wfs;
	}

	/**
	 * Locks or unlocks all of the buttons in this {@link Window}
	 * @param on
	 */
	public void setButtonLock(boolean on, IWidget exception){
		for(IWidget w : isc.getWidgets()){
			if(w instanceof LayerContainer){
				((LayerContainer)w).setButtonEnabled(on);
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("GIS Layers");
		setSize(targetWidth, targetHeight);
	}

}
