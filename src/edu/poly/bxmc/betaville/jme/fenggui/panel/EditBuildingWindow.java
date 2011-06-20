/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.shape.AxisRods;
import com.jme.scene.shape.Arrow;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

/**
 * @author Vivian(Hyun) Park
 *
 */
public class EditBuildingWindow extends Window implements IBetavilleWindow {
	private static Logger logger = Logger.getLogger(EditBuildingWindow.class);
	
	private int width=180;
	private int height=180;

	private Container editingContainer;
	
	private FixedButton translate;
	private FixedButton rotate;
	private FixedButton resize;
	private FixedButton delete;
	private FixedButton save;
	
	private AxisRods moveRod;
	private Arrow upArrow;
	private Arrow rightArrow;
	private Arrow leftArrow;
	private Torus verticalRotateTorus;
	private Torus horizontalRotateTorus;
	
	private List<PanelAction> panelActions;
	
	public EditBuildingWindow() {
		super(true, true);
		panelActions = new ArrayList<PanelAction>();
		internalSetup();
	}
	
	private void internalSetup(){
		//editingContainer.setSize(width, height);
	
		editingContainer = FengGUI.createWidget(Container.class);
		editingContainer.setLayoutManager(new RowExLayout(false));
		
		translate = FengGUI.createWidget(FixedButton.class);
		translate.setText("Translate");
		translate.setWidth(translate.getWidth()+10);
		translate.setEnabled(true);
		translate.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {

					SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
					SceneScape.getTargetSpatial().updateRenderState();
				
					moveRod = new AxisRods("$editorWidget-axis", true, Scale.fromMeter(5));
					SceneGameState.getInstance().getEditorWidgetNode().attachChild(moveRod);
					moveRod.setLocalTranslation(SceneScape.getTargetSpatial().getLocalTranslation());
					
					MaterialState red = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					red.setAmbient(ColorRGBA.red);
					red.setDiffuse(ColorRGBA.red);
					moveRod.getxAxis().setRenderState(red);
					
					MaterialState green = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					green.setAmbient(ColorRGBA.green);
					green.setDiffuse(ColorRGBA.green);
					moveRod.getyAxis().setRenderState(green);
					
					MaterialState blue = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					blue.setAmbient(ColorRGBA.blue);
					blue.setDiffuse(ColorRGBA.blue);
					moveRod.getyAxis().setRenderState(blue);
					
					moveRod.updateRenderState();
					
					logger.info("added rods");
				
			}
		});
		
		rotate = FengGUI.createWidget(FixedButton.class);
		rotate.setText("Rotate");
		rotate.setWidth(rotate.getWidth()+10);
		rotate.setEnabled(true);
		rotate.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				
				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();
				
				Vector3f[] coordi = GeometryUtilities.findObjectExtents(SceneScape.getTargetSpatial());

				float targetLength = Math.abs(coordi[1].x - coordi[0].x);
				//float targetHeight = Math.abs(coordi[1].y - coordi[0].y);
				//float targetWidth = Math.abs(coordi[1].z - coordi[0].z);
				
				System.out.println("*" + targetLength);
				
				verticalRotateTorus = new Torus("$editorWidget-verticalRotateTorus", 100, 100, targetLength, targetLength+5);
				verticalRotateTorus.setLocalTranslation(SceneScape.getTargetSpatial().getLocalTranslation());
				
				MaterialState red = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				red.setAmbient(ColorRGBA.red);
				red.setDiffuse(ColorRGBA.red);
				verticalRotateTorus.setRenderState(red);
				
				verticalRotateTorus.updateRenderState();
				
				logger.info("added torus");
			}
		});
		
		resize = FengGUI.createWidget(FixedButton.class);
		resize.setText("Resize");
		resize.setWidth(resize.getWidth()+10);
		resize.setEnabled(true);
		resize.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				
				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();
				
				MaterialState blue = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				blue.setAmbient(ColorRGBA.blue);
				blue.setDiffuse(ColorRGBA.blue);
				
				Vector3f[] coordi = GeometryUtilities.findObjectExtents(SceneScape.getTargetSpatial());

				float targetLength = Math.abs(coordi[1].x - coordi[0].x);
				float targetHeight = Math.abs(coordi[1].y - coordi[0].y);
				float targetWidth = Math.abs(coordi[1].z - coordi[0].z);
				
				System.out.println("**" + targetLength + " " + targetHeight + " " + targetWidth);
				
				coordi[0].interpolate(coordi[1], .5f);
				
				System.out.println(coordi[0]);
				
				upArrow = new Arrow("$editorWidget-upArrow", Scale.fromMeter(1), Scale.fromMeter(1)*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(upArrow);
				
				upArrow.setRenderState(blue);
				upArrow.updateRenderState();
				
				Vector3f coordinateLocation = JME2MapManager.instance.locationToBetaville(SceneScape.getPickedDesign().getCoordinate());
				
				upArrow.setLocalTranslation(coordinateLocation.x - (targetLength/2), coordinateLocation.y + Scale.fromMeter(5), coordinateLocation.z);
				//upArrow.setLocalTranslation(upArrow.getLocalTranslation().x + coordi[0].x, upArrow.getLocalTranslation().y + targetHeight, upArrow.getLocalTranslation().z);
				
				System.out.println(coordinateLocation.x - (targetLength/2) + " " + coordinateLocation.y + Scale.fromMeter(5) + " " + coordinateLocation.z);
				
				/*

				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();		

				Vector3f[] coordi = GeometryUtilities.findObjectExtents(SceneScape.getTargetSpatial());

				float targetLength = Math.abs(coordi[1].x - coordi[0].x);
				float targetHeight = Math.abs(coordi[1].y - coordi[0].y);
				float targetWidth = Math.abs(coordi[1].z - coordi[0].z);
				
				//Vector3f target = SceneScape.getTargetSpatial().getWorldTranslation();
				
				//System.out.println("**" + length + " " + height + " " + width);
				
				MaterialState blue = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				blue.setAmbient(ColorRGBA.blue);
				blue.setDiffuse(ColorRGBA.blue);
				
				sizeArrow1 = new Arrow("$editorWidget-arrow1", Scale.fromMeter(5), Scale.fromMeter(5)*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(sizeArrow1);
				sizeArrow1.setRenderState(blue);
				sizeArrow1.updateRenderState();
				sizeArrow1.setLocalTranslation(SceneScape.getTargetSpatial().getLocalTranslation());
				Vector3f target = sizeArrow1.getLocalTranslation();
				System.out.println(target);
				sizeArrow1.setLocalTranslation(target.x + (targetLength/2), target.y + targetHeight+10, target.z + (targetWidth/2));
				//sizeArrow1.setLocalTranslation(target.x + (targetLength/2), (target.y + targetHeight+10), target.z + (targetWidth/2));
				
				System.out.println("***" + target.x + (targetLength/2) + " " + (target.y + targetHeight+10) + " " + target.z + (targetWidth/2));
				
				sizeArrow2 = new Arrow("$editorWidget-arrow2", Scale.fromMeter(1), Scale.fromMeter(1)*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(sizeArrow2);
				sizeArrow2.setRenderState(blue);
				sizeArrow2.updateRenderState();
				sizeArrow2.rotateUpTo(new Vector3f(90,0,0));
				sizeArrow2.setLocalTranslation(coordi[1].x + 10, targetHeight/2, targetWidth/2);
				
				sizeArrow3 = new Arrow("$editorWidget-arrow3", Scale.fromMeter(1), Scale.fromMeter(1)*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(sizeArrow3);
				sizeArrow3.setRenderState(blue);
				sizeArrow3.updateRenderState();
				sizeArrow3.rotateUpTo(new Vector3f(-90,0,0));
				sizeArrow3.setLocalTranslation(coordi[0].x - 10, targetHeight/2, targetWidth/2);
				
				//Box box = new Box("$editorWidget-box", SceneScape.getTargetSpatial().get)
				//((Node)SceneScape.getTargetSpatial()).attachChild(arg0);*/
				
				logger.info("added arrows");

			}
		});
		
		delete = FengGUI.createWidget(FixedButton.class);
		delete.setText("Delete");
		delete.setWidth(delete.getWidth()+10);
		delete.setEnabled(true);
		delete.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				
				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();
				
				Window window = FengUtils.createTwoOptionWindow("Delete", "Are you sure that you would like to delete this design?",
						"no", "yes",
						new IButtonPressedListener() {
					public void buttonPressed(Object source, ButtonPressedEvent e) {
						
						logger.info("b1 pressed");
					}
				},
				new IButtonPressedListener() {
					public void buttonPressed(Object source, ButtonPressedEvent e) {
						int designID = SceneScape.getPickedDesign().getID();
						int removed = NetPool.getPool().getSecureConnection().removeDesign(designID, SettingsPreferences.getUser(), SettingsPreferences.getPass());
						if(removed==0){
							SceneGameState.getInstance().removeDesignFromDisplay(designID);
							logger.info("Design successfully removed");
						}
						else if(removed==-3){
							logger.warn("You are either not the owner of this design or not authorized to remove designs");
						}
					}
				},
				true, true
				);
				
				window.setXY(FengUtils.midWidth(GUIGameState.getInstance().getDisp(), window),FengUtils.midHeight(GUIGameState.getInstance().getDisp(), window));
				GUIGameState.getInstance().getDisp().addWidget(window);
			}
		});
		
		save = FengGUI.createWidget(FixedButton.class);
		save.setText("Save");
		save.setWidth(save.getWidth()+10);
		save.setEnabled(true);
		save.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				
				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();
				
				Window window = FengUtils.createTwoOptionWindow("Save", "Do you want to save?  ",
						"no", "yes",
						new IButtonPressedListener() {
					public void buttonPressed(Object source, ButtonPressedEvent e) {
						
					}
				}, 
				
				new IButtonPressedListener() {
					public void buttonPressed(Object source, ButtonPressedEvent e) {
						
					}
				}, 
				true, true);
				window.setXY(FengUtils.midWidth(GUIGameState.getInstance().getDisp(), window),FengUtils.midHeight(GUIGameState.getInstance().getDisp(), window));
				GUIGameState.getInstance().getDisp().addWidget(window);
				
			}
		});
			
		editingContainer.addWidget(translate);
		editingContainer.addWidget(rotate);
		editingContainer.addWidget(resize);
		editingContainer.addWidget(delete);
		editingContainer.addWidget(save);
		
		getContentContainer().addWidget(editingContainer);

		SceneScape.addSelectionListener(new ISpatialSelectionListener() {
			public void selectionCleared(Design previousDesign) {
				
				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();
				
				translate.setEnabled(false);
				delete.setEnabled(false);
				rotate.setEnabled(false);
				delete.setEnabled(false);
				save.setEnabled(false);
				resize.setEnabled(false);
			}
			
			public void designSelected(Spatial spatial, Design design) {

				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();
				
				translate.setEnabled(true);
				delete.setEnabled(true);
				rotate.setEnabled(true);
				delete.setEnabled(true);
				resize.setEnabled(true);
				save.setEnabled(true);
			}
		});
		
		
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup(){
		setTitle("Edit Building");
		setSize(width, height);
	}

}
