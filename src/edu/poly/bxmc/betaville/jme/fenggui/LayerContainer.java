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
import java.util.Set;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;
import org.fenggui.layout.StaticLayout;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.FidFilterImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.jme.bounding.BoundingBox;
import com.jme.input.MouseInput;
import com.jme.intersection.PickResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.TriMesh;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.vividsolutions.jts.geom.Geometry;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.GeoToolsCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.module.GUIModule;

public class LayerContainer extends Container{
	private static final Logger logger = Logger.getLogger(LayerContainer.class);
	private String thisLayerName;

	private FeaturePopup featurePopup;

	private AddLayersWindow addLayersWindow;

	private Label title;
	private Button showHide;

	private FeatureCollection<SimpleFeatureType, SimpleFeature> features;

	private boolean on=false;

	private MaterialState boxMaterial;

	private CreatePrimitiveWindow primitiveWindow;

	private TriMesh jmeGeometry;

	public LayerContainer(){

		addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			public void processEvent(Object arg0, Event arg1) {
				// TODO Auto-generated method stub
				getAppearance();
			}
		});

		BetavilleNoCanvas.addCompletionListener(new IAppInitializationCompleteListener() {

			public void applicationInitializationComplete() {
				boxMaterial = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				boxMaterial.setDiffuse(ColorRGBA.green);
			}
		});
	}

	private void flipStatus(boolean statusOn){
		if(statusOn){
			on=true;
			showHide.setText(Labels.generic("hide"));
		}
		else{
			on=false;
			showHide.setText(Labels.generic("show"));
		}
	}

	public void setButtonEnabled(boolean enabled){
		showHide.setEnabled(enabled);
	}

	public void registerWindows(AddLayersWindow alw, CreatePrimitiveWindow primitiveWindow){
		addLayersWindow=alw;
		this.primitiveWindow=primitiveWindow;
	}
	
	/**
	 * @return The layer's name
	 */
	public String getThisLayerName() {
		return thisLayerName;
	}

	public void initialize(String layerName){
		thisLayerName=layerName;
		featurePopup = new FeaturePopup();

		setLayoutData(new RowExLayoutData(true, true));
		setLayoutManager(new BorderLayout());

		title = FengGUI.createWidget(Label.class);
		title.setText(layerName);
		title.setLayoutData(BorderLayoutData.WEST);

		showHide = FengGUI.createWidget(Button.class);
		showHide.setText(Labels.generic("show"));
		showHide.setLayoutData(BorderLayoutData.EAST);
		showHide.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				if(on){
					SceneGameState.getInstance().getGISNode().detachChildNamed(thisLayerName);
					GUIGameState.getInstance().removeModuleFromUpdateList(featurePopup);
					flipStatus(false);
				}
				else{
					SettingsPreferences.getGUIThreadPool().submit(new Runnable() {

						public void run() {
							try{
								callButtonLock(true);
								// create the geometry if the user hasn't set one up already
								if(jmeGeometry==null){
									StaticLayout.center(primitiveWindow, GUIGameState.getInstance().getDisp());
									GUIGameState.getInstance().getDisp().addWidget(primitiveWindow);
									while(!primitiveWindow.isReady()){
										Thread.sleep(25);
									}
									GUIGameState.getInstance().getDisp().removeWidget(primitiveWindow);
									jmeGeometry = primitiveWindow.generateShape();
									BoundingBox bb = new BoundingBox();
									jmeGeometry.setModelBound(bb);
									jmeGeometry.updateModelBound();
									primitiveWindow.reset();
								}
								logger.info("Getting features");
								features = addLayersWindow.getWFSConnection().requestSomething(thisLayerName, null);
								logger.info("Got " + features.size() + " features");
								FeatureIterator<SimpleFeature> it = features.features();
								logger.info("features obtained");
								Node node = (Node) SceneGameState.getInstance().getGISNode().getChild(thisLayerName);
								if(node==null){
									node = new Node(thisLayerName);
									SceneGameState.getInstance().getGISNode().attachChild(node);
									node.updateRenderState();
								}

								while(it.hasNext()){
									SimpleFeature feature = it.next();

									// Create a coordinate that can be digested by the Betaville scene
									GeoToolsCoordinate gtc = new GeoToolsCoordinate(((Geometry)feature.getDefaultGeometry()).getCoordinate(),
											feature.getType().getCoordinateReferenceSystem());

									// Put *something* in the scene
									//if(feature instanceof Geometry){
									//logger.info("Feature is a point, putting a box at its location");

									SharedMesh m = new SharedMesh(feature.getID(), jmeGeometry);
									m.setLocalTranslation(JME2MapManager.instance.locationToBetaville(gtc));
									//logger.info("Feature put at: " + m.getLocalTranslation().toString());
									m.setLocalTranslation(m.getLocalTranslation().x, Scale.fromMeter(primitiveWindow.getAltitude()), m.getLocalTranslation().z);
									node.attachChild(m);
									m.updateRenderState();
									m.updateModelBound();
									m.updateWorldBound();
									m.updateGeometricState(0f, true);

									//}
								}
								logger.info("adding feature popup");
								GUIGameState.getInstance().addModuleToUpdateList(featurePopup);
								flipStatus(true);
								callButtonLock(false);
							} catch (IOException e) {
								e.printStackTrace();
								callButtonLock(false);
							} catch (NoSuchAuthorityCodeException e) {
								logger.info("No Such Authority Code Exception");
								e.printStackTrace();
								callButtonLock(false);
							} catch (FactoryException e) {
								logger.info("Factory Exception");
								e.printStackTrace();
								callButtonLock(false);
							} catch (InterruptedException e) {
								e.printStackTrace();
								callButtonLock(false);
							}
						}
					});

				}
			}
		});

		addWidget(title, showHide);
	}

	private void callButtonLock(boolean status){
		//addLayersWindow.setButtonLock(status, this);
	}

	public class FeaturePopup implements GUIModule{

		private Window window;
		private Label layer;
		private Label name;
		private Label temp;
		
		private int offset = 2;

		private PickResults gisResults = new TrianglePickResults();
		private Ray rayToUse;

		public FeaturePopup(){
			window = FengGUI.createWindow(true, true);
			window.removeWidget(window.getTitleBar());
			window.getContentContainer().setLayoutManager(new RowExLayout(false));

			layer = FengGUI.createWidget(Label.class);
			layer.setLayoutData(new RowExLayoutData(true, true));
			layer.setText(thisLayerName);
			name = FengGUI.createWidget(Label.class);
			name.setLayoutData(new RowExLayoutData(true, true));
			temp = FengGUI.createWidget(Label.class);
			temp.setLayoutData(new RowExLayoutData(true, true));
			temp.setText(Labels.get(LayerContainer.class, "loading_properties"));

			window.getContentContainer().addWidget(layer, name);
			window.setExpandable(false);
		}

		public void deconstruct() {}

		public void buildComponents() {}

		public void layoutComponents() {}

		public void addToScene() {}

		public void removeFromScene() {}

		public void update() {
			//logger.info("updating feature popup");
			Vector2f screenPosition = new Vector2f(MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());
			Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
			Camera camera = SceneGameState.getInstance().getCamera();
			rayToUse = new Ray(camera.getLocation(), worldCoords.subtractLocal(camera.getLocation()));

			rayToUse.getDirection().normalizeLocal();

			gisResults.setCheckDistance(true);
			gisResults.clear();
			Node thisLayer = (Node)SceneGameState.getInstance().getGISNode().getChild(thisLayerName);
			//Node thisLayer = SceneGameState.getInstance().getRootNode();
			if(thisLayer==null) return;
			//logger.info("testing " + thisLayer.getQuantity() + " children in " + thisLayer.getName());
			thisLayer.findPick(rayToUse, gisResults);

			if(gisResults.getNumber()>0){
				//logger.info("Object Picked");
				final String id = gisResults.getPickData(0).getTargetMesh().getName();

				// if the window is already showing the right text then we can stop here
				if(window.isInWidgetTree() && name.getText().equals(id)){
					window.setXY(MouseInput.get().getXAbsolute()+offset, MouseInput.get().getYAbsolute()+offset);
					return;
				}
				
				name.setText(id);
				
				window.getContentContainer().removeAllWidgets();
				window.pack();
				window.getContentContainer().addWidget(layer, name, temp);

				// Do the feature search on another thread in case there are too many features
				SettingsPreferences.getGUIThreadPool().submit(new Runnable() {

					public void run(){
						if(features==null) return;
						FeatureIterator<SimpleFeature> it = features.features();
						while(it.hasNext()){
							SimpleFeature feature = it.next();
							if(!feature.getID().equals(id)) continue;
							//logger.debug("FEATURE FOUND");
							window.getContentContainer().removeAllWidgets();
							window.pack();
							window.getContentContainer().addWidget(layer, name);
							for(Property p : feature.getProperties()){
								logger.info("property: " + p.getName());
								if(p.getName().toString().equals("the_geom")) continue;
								
								Label l = FengGUI.createWidget(Label.class);
								l.setText(p.getName()+": "+p.getValue());
								window.getContentContainer().addWidget(l);
							}
							break;
						}
						window.pack();
					}
				});

				if(!window.isInWidgetTree()) GUIGameState.getInstance().getDisp().addWidget(window);
				window.setXY(MouseInput.get().getXAbsolute()+offset, MouseInput.get().getYAbsolute()+offset);
			}
			else{
				//logger.info("nothing picked");
				if(window.isInWidgetTree()) window.close();
			}
		}
	}

	private class MyFidFilter extends FidFilterImpl{
		public MyFidFilter(Set<FeatureIdImpl> featureSet){
			super(featureSet);
		}
	}
}