/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.binding.render.Binding;
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
import edu.poly.bxmc.betaville.module.ModuleNameException;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.module.TranslateModule;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.shape.AxisRods;
import com.jme.scene.shape.Arrow;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.scene.shape.Tube;

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
	private AxisRods rotateAxis;
	private Arrow upArrow;
	private Arrow rightArrow;
	private Arrow leftArrow;
	private Arrow forwardArrow;
	private Arrow backwardArrow;
	private Tube horizontalRotateTube;
	private Tube verticalRotateTube;
	
	private List<PanelAction> panelActions;
	
	public EditBuildingWindow() {
		super(true, true);
		panelActions = new ArrayList<PanelAction>();
		internalSetup();
		try {
			SceneGameState.getInstance().addModuleToUpdateList(new TranslateModule());
		} catch (ModuleNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void internalSetup(){
		//editingContainer.setSize(width, height);
	
		editingContainer = FengGUI.createWidget(Container.class);
		//editingContainer.setXY(10, 10);
		editingContainer.setLayoutManager(new RowExLayout(false));
		
		translate = FengGUI.createWidget(FixedButton.class);
		translate.setText("Translate");
		translate.setWidth(translate.getWidth()+10);
		translate.setEnabled(true);
		translate.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {

					SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
					SceneScape.getTargetSpatial().updateRenderState();
				

					BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
					
					float distance = (float)(Math.sqrt(Math.pow(Math.sqrt(Math.pow(bb.xExtent, 2) + Math.pow(bb.yExtent, 2)), 2) + Math.pow(bb.zExtent, 2)));
					
					moveRod = new AxisRods("$editorWidget-axis", true, distance, distance*0.01f);
					moveRod.setModelBound(new BoundingBox());
					moveRod.updateModelBound();
					
					MaterialState yellow = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					yellow.setAmbient(ColorRGBA.yellow);
					yellow.setDiffuse(ColorRGBA.yellow);
					
					SceneGameState.getInstance().getEditorWidgetNode().attachChild(moveRod);
					moveRod.setLocalTranslation(SceneScape.getTargetSpatial().getLocalTranslation());
					
					MaterialState red = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					red.setAmbient(ColorRGBA.red);
					red.setDiffuse(ColorRGBA.red);
					moveRod.getxAxis().setRenderState(red);
					
					if(moveRod.getxAxis().getChild("tip") instanceof Pyramid) {
						
						float arrowLength = ((Arrow)moveRod.getxAxis()).getLength();
						float arrowWidth = ((Arrow)moveRod.getxAxis()).getWidth();
						
						((Pyramid)moveRod.getxAxis().getChild("tip")).updateGeometry(8 * arrowWidth, arrowLength / 9f);
						((Pyramid)moveRod.getxAxis().getChild("tip")).translatePoints(0, arrowLength * .5f, 0);
						
					}
						
					
					MaterialState green = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					green.setAmbient(ColorRGBA.green);
					green.setDiffuse(ColorRGBA.green);
					moveRod.getyAxis().setRenderState(green);
					
					if(moveRod.getyAxis().getChild("tip") instanceof Pyramid) {
						
						float arrowLength = ((Arrow)moveRod.getyAxis()).getLength();
						float arrowWidth = ((Arrow)moveRod.getyAxis()).getWidth();
						
						((Pyramid)moveRod.getyAxis().getChild("tip")).updateGeometry(8 * arrowWidth, arrowLength / 9f);
						((Pyramid)moveRod.getyAxis().getChild("tip")).translatePoints(0, arrowLength * .5f, 0);
						
					}
					
					MaterialState blue = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
					blue.setAmbient(ColorRGBA.blue);
					blue.setDiffuse(ColorRGBA.blue);
					moveRod.getyAxis().setRenderState(blue);
					
					if(moveRod.getzAxis().getChild("tip") instanceof Pyramid) {
						
						float arrowLength = ((Arrow)moveRod.getzAxis()).getLength();
						float arrowWidth = ((Arrow)moveRod.getzAxis()).getWidth();
						
						((Pyramid)moveRod.getzAxis().getChild("tip")).updateGeometry(8 * arrowWidth, arrowLength / 9f);
						((Pyramid)moveRod.getzAxis().getChild("tip")).translatePoints(0, arrowLength * .5f, 0);
						
					}
					
					moveRod.updateRenderState();
					
					System.out.println("length: " + moveRod.getLength());
					System.out.println("distance: " + distance);
					
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
				
/*				MaterialState red = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				red.setAmbient(ColorRGBA.red);
				red.setDiffuse(ColorRGBA.red);
				*/
				
			
				MaterialState blue = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				blue.setAmbient(ColorRGBA.blue);
				blue.setDiffuse(ColorRGBA.blue);

				
				BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
				Vector3f center = bb.getCenter();
				
				float distance = (float)(Math.sqrt(Math.pow(Math.sqrt(Math.pow(bb.xExtent, 2) + Math.pow(bb.yExtent, 2)), 2) + Math.pow(bb.zExtent, 2)));
				float height = bb.yExtent * 0.0715f;
				
				/*
				rotateAxis = new AxisRods("$editorWidget-RotateAxis", true, distance, distance*0.01f);
				
				Vector3f xOrigin = ((Pyramid)rotateAxis.getxAxis().getChild("tip")).getLocalTranslation();
				Vector3f yOrigin = ((Pyramid)rotateAxis.getyAxis().getChild("tip")).getLocalTranslation();
				Vector3f zOrigin = ((Pyramid)rotateAxis.getzAxis().getChild("tip")).getLocalTranslation();
				
				((Pyramid)rotateAxis.getxAxis().getChild("tip")).removeFromParent();
				((Pyramid)rotateAxis.getyAxis().getChild("tip")).removeFromParent();
				((Pyramid)rotateAxis.getzAxis().getChild("tip")).removeFromParent();
				
				horizontalRotateTube = new Tube("$editorWidget-horizontalRotateTube", distance * 0.1f, distance * 0.05f, 0.01f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(horizontalRotateTube);
				
				horizontalRotateTube.setRenderState(blue);
				horizontalRotateTube.updateRenderState();
				horizontalRotateTube.setLocalTranslation(xOrigin);
				*/
				
				
				horizontalRotateTube = new Tube("$editorWidget-horizontalRotateTube", distance * 1.1f, distance * 1.05f, height);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(horizontalRotateTube);
				
				horizontalRotateTube.setRenderState(blue);
				horizontalRotateTube.updateRenderState();
				horizontalRotateTube.setLocalTranslation(center);

				verticalRotateTube = new Tube("$editorWidget-verticalRotateTube", distance * 1.1f, distance * 1.05f, height);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(verticalRotateTube);
				
				verticalRotateTube.rotateUpTo(new Vector3f(0, 0, 90));
				verticalRotateTube.setRenderState(blue);
				verticalRotateTube.updateRenderState();
				verticalRotateTube.setLocalTranslation(center);
				
				
				logger.info("added tubes");
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
				
				BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
				Vector3f center = bb.getCenter();
				
				float distance = (float)(Math.sqrt(Math.pow(Math.sqrt(Math.pow(bb.xExtent, 2) + Math.pow(bb.yExtent, 2)), 2) + Math.pow(bb.zExtent, 2)));
				float scale = distance/5;
				System.out.println(distance + " " + Scale.fromMeter(1));
				
				upArrow = new Arrow("$editorWidget-upArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(upArrow);
				
				upArrow.setRenderState(blue);
				upArrow.updateRenderState();
				upArrow.setLocalTranslation(center.x, center.y+bb.yExtent, center.z);
				
				leftArrow = new Arrow("$editorWidget-leftArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(leftArrow);
				
				leftArrow.setRenderState(blue);
				leftArrow.updateRenderState();
				leftArrow.rotateUpTo(new Vector3f(90,0,0));
				leftArrow.setLocalTranslation(center.x+bb.xExtent, center.y, center.z);
				
				rightArrow = new Arrow("$editorWidget-rightArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(rightArrow);
				
				rightArrow.setRenderState(blue);
				rightArrow.updateRenderState();
				rightArrow.rotateUpTo(new Vector3f(-90,0,0));
				rightArrow.setLocalTranslation(center.x-bb.xExtent, center.y, center.z);
				
				forwardArrow = new Arrow("$editorWidget-forwardArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(forwardArrow);
				
				forwardArrow.setRenderState(blue);
				forwardArrow.updateRenderState();
				forwardArrow.rotateUpTo(new Vector3f(0, 0, 90));
				forwardArrow.setLocalTranslation(center.x, center.y, center.z+bb.zExtent);
				
				backwardArrow = new Arrow("$editorWidget-backwardArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(backwardArrow);
				
				backwardArrow.setRenderState(blue);
				backwardArrow.updateRenderState();
				backwardArrow.rotateUpTo(new Vector3f(0, 0, -90));
				backwardArrow.setLocalTranslation(center.x, center.y, center.z-bb.zExtent);
				
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
		setXY(Binding.getInstance().getCanvasWidth() - this.width, 0);
	}

}
