package edu.poly.bxmc.betaville.jme.fenggui;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayoutData;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.GeoToolsCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;

public class LayerContainer extends Container{
	private static final Logger logger = Logger.getLogger(LayerContainer.class);
	private String thisLayerName;
	
	private AddLayersWindow addLayersWindow;

	private Label title;
	private Button showHide;

	private boolean on=false;
	
	private int size=5;
	private int height=50;
	
	private MaterialState boxMaterial;

	public LayerContainer(){
		
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
			showHide.setText("hide");
		}
		else{
			on=false;
			showHide.setText("show");
		}
	}
	
	public void registerLayerWindow(AddLayersWindow alw){
		addLayersWindow=alw;
	}

	public void initialize(String layerName){
		thisLayerName=layerName;
		setLayoutData(new RowExLayoutData(true, true));
		setLayoutManager(new BorderLayout());

		title = FengGUI.createWidget(Label.class);
		title.setText(layerName);
		title.setLayoutData(BorderLayoutData.WEST);

		showHide = FengGUI.createWidget(Button.class);
		showHide.setText("show");
		showHide.setLayoutData(BorderLayoutData.EAST);
		showHide.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				if(on){
					SceneGameState.getInstance().getGISNode().detachChildNamed(thisLayerName);
					flipStatus(false);
				}
				else{
					try {
						addLayersWindow.setButtonLock(true);
						FeatureCollection<SimpleFeatureType, SimpleFeature> features = addLayersWindow.getWFSConnection().requestSomething(thisLayerName);
						FeatureIterator<SimpleFeature> it = features.features();
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
								Box b = new Box(feature.getID(), new Vector3f(), Scale.fromMeter(size), Scale.fromMeter(size), Scale.fromMeter(size));
								b.setRenderState(boxMaterial);
								b.setLocalTranslation(JME2MapManager.instance.locationToBetaville(gtc));
								logger.info("Feature put at: " + b.getLocalTranslation().toString());
								b.setLocalTranslation(b.getLocalTranslation().x, Scale.fromMeter(height), b.getLocalTranslation().z);
								node.attachChild(b);
								b.updateRenderState();
							//}
						}
						flipStatus(true);
						addLayersWindow.setButtonLock(false);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						addLayersWindow.setButtonLock(false);
					} catch (NoSuchAuthorityCodeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						addLayersWindow.setButtonLock(false);
					} catch (FactoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						addLayersWindow.setButtonLock(false);
					}
				}
			}
		});
		
		addWidget(title, showHide);
	}
}