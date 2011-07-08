/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.Slider;
import org.fenggui.TextEditor;
import org.fenggui.binding.render.Binding;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.Key;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.event.key.KeyTypedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengTextContentException;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ModeledDesign;
import edu.poly.bxmc.betaville.module.ModuleNameException;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.module.RotateModule;
import edu.poly.bxmc.betaville.module.ResizeModule;
import edu.poly.bxmc.betaville.module.TranslateModule;
import edu.poly.bxmc.betaville.net.NetPool;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.shape.AxisRods;
import com.jme.scene.shape.Arrow;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.scene.shape.Tube;

/**
 * @author Vivian(Hyun) Park
 *
 */
public class EditBuildingWindow extends Window implements IBetavilleWindow {
	private static Logger logger = Logger.getLogger(EditBuildingWindow.class);
	
	private int width=210;
	private int height=330;
	private int offset=15;

	private Container editingContainer;
	private Container resizeContainer;
	private Container xResizeLabelContainer;
	private Container yResizeLabelContainer;
	private Container zResizeLabelContainer;
	private Container xResizeTextContainer;
	private Container yResizeTextContainer;
	private Container zResizeTextContainer;
	
	private FixedButton translate;
	private FixedButton rotate;
	private FixedButton resize;
	private FixedButton delete;
	private FixedButton reset;
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
	private Tube anotherRotateTube;
	
	private Label rotationLabel;
	private String rotationText = "Rotation";
	private Label xRotationLabel;
	private Label yRotationLabel;
	private Label zRotationLabel;
	private String xPrefix="X:";
	private String yPrefix="Y:";
	private String zPrefix="Z:";
	private Slider xRotationSlider;
	private Slider yRotationSlider;
	private Slider zRotationSlider;
	
	private Label scaleLabel;
	private String scaleString = "Dimensions";
	private Label xScaleLabel;
	private Label yScaleLabel;
	private Label zScaleLabel;
	private TextEditor xScaleTextEditor;
	private TextEditor yScaleTextEditor;
	private TextEditor zScaleTextEditor;
	
	private TextEditor rotationTextEditor;
	
	private List<PanelAction> panelActions;
	
	public EditBuildingWindow() {
		super(true, true);
		panelActions = new ArrayList<PanelAction>();

		try {
			SceneGameState.getInstance().addModuleToUpdateList(new TranslateModule());
			SceneGameState.getInstance().addModuleToUpdateList(new RotateModule());
			SceneGameState.getInstance().addModuleToUpdateList(new ResizeModule());
			} catch (ModuleNameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		internalSetup();
		
	}
	
	//text field
	private void internalSetup(){
		//editingContainer.setSize(width, height);
	
		editingContainer = FengGUI.createWidget(Container.class);
		//editingContainer.setXY(10, 10);
		editingContainer.setLayoutManager(new RowExLayout(false));
		
		
		
/*****************************************************
 * Rotation Sliders/Text Edit Fields
 *****************************************************/
		/*
		 * crashes when the knob reaches the end of the slider
		 */
		
		rotationLabel = FengGUI.createWidget(Label.class);
		rotationLabel.setText(rotationText);
		
		xRotationLabel = FengGUI.createWidget(Label.class);
		xRotationLabel.setText(xPrefix+"0");

		yRotationLabel = FengGUI.createWidget(Label.class);
		yRotationLabel.setText(yPrefix+"0");

		zRotationLabel = FengGUI.createWidget(Label.class);
		zRotationLabel.setText(zPrefix+"0");

		xRotationSlider = FengGUI.createSlider(true);
		xRotationSlider.setWidth(width-20-xRotationLabel.getWidth());
		xRotationSlider.setXY(width-xRotationSlider.getWidth()-5, rotationLabel.getY()-xRotationSlider.getHeight()-(offset/8));
		xRotationSlider.setValue(0);
		xRotationSlider.addSliderMovedListener(new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				int newValue = (int)(xRotationSlider.getValue()*360);
				xRotationLabel.setText(xPrefix + newValue);
				
				SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleX(newValue));
				SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(newValue, ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationX()));
				((ModeledDesign)SceneScape.getPickedDesign()).setRotationX(newValue);
		
			}
		});

		yRotationSlider = FengGUI.createSlider(true);
		yRotationSlider.setWidth(width-20-xRotationLabel.getWidth());
		yRotationSlider.setXY(width-yRotationSlider.getWidth()-5, xRotationSlider.getY()-yRotationSlider.getHeight()-(offset/4));
		yRotationSlider.setValue(0);
		yRotationSlider.addSliderMovedListener(new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				int newValue = (int)(yRotationSlider.getValue()*360);
				yRotationLabel.setText(yPrefix + newValue);
				
				SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleY(newValue));
				SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getPickedDesign()).getRotationX(), newValue, ((ModeledDesign)SceneScape.getPickedDesign()).getRotationZ()));
				((ModeledDesign)SceneScape.getPickedDesign()).setRotationY(newValue);
			
			}
		});

		zRotationSlider = FengGUI.createSlider(true);
		zRotationSlider.setWidth(width-20-xRotationLabel.getWidth());
		zRotationSlider.setXY(width-zRotationSlider.getWidth()-5, yRotationSlider.getY()-zRotationSlider.getHeight()-(offset/4));
		zRotationSlider.setValue(0);
		zRotationSlider.addSliderMovedListener(new ISliderMovedListener(){
			public void sliderMoved(SliderMovedEvent arg0) {
				int newValue = (int)(zRotationSlider.getValue()*360);
				zRotationLabel.setText(zPrefix + newValue);
				SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleZ(newValue));
				SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getPickedDesign()).getRotationX(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY(), newValue));
				((ModeledDesign)SceneScape.getPickedDesign()).setRotationZ(newValue);
				}
		});

		xRotationLabel.setXY(5, xRotationSlider.getY()-(xRotationLabel.getHeight()/2));
		yRotationLabel.setXY(5, yRotationSlider.getY()-(yRotationLabel.getHeight()/2));
		zRotationLabel.setXY(5, zRotationSlider.getY()-(zRotationLabel.getHeight()/2));
		
		/*
		rotationTextEditor = FengGUI.createWidget(TextEditor.class);
		//rotationTextEditor.setX(2);
		rotationTextEditor.setText("0");
		rotationTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		rotationTextEditor.setLayoutData(new RowExLayoutData(true, true));
		rotationTextEditor.addKeyListener(new IKeyListener() {
			
			public void keyTyped(Object arg0, KeyTypedEvent arg1) {
			}
			
			public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
				if(arg1.getKeyClass().equals(Key.ENTER)){
					// has the object already been moved?
					/*
					if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
						logger.info("A fallback was not previously created for this object, creating one");
						FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
						changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
					}
					
					// perform the rotate action
					int newRotation=0;
					try {
						newRotation = (int)FengUtils.getFloat(rotationTextEditor);
					} catch (FengTextContentException e) {
						// This issue should not come up since FengGUI is only allowing valid numbers
						logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
								"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
						rotationTextEditor.setText("0");
						GUIGameState.getInstance().getDisp().addWidget(
								FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
					}
					SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleY(newRotation));
					SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getPickedDesign()).getRotationX(), newRotation, ((ModeledDesign)SceneScape.getPickedDesign()).getRotationZ()));
					((ModeledDesign)SceneScape.getPickedDesign()).setRotationY(newRotation);
				}
			}
			
			public void keyPressed(Object arg0, KeyPressedEvent arg1) {
			}
		});*/
		
/*****************************************************
 * End of Rotation Sliders/Text Edit Fields
 *****************************************************/

		
		
		
/*****************************************************
 * Scale Labels/Text Edit Fields
 *****************************************************/
		
		scaleLabel = FengGUI.createWidget(Label.class);
		scaleLabel.setText(scaleString);
		
		resizeContainer = FengGUI.createWidget(Container.class);
		resizeContainer.setLayoutManager(new RowExLayout(true));
		
		xResizeLabelContainer = FengGUI.createWidget(Container.class);
		
		xScaleLabel = FengGUI.createWidget(Label.class);
		xScaleLabel.setText(xPrefix);
		
		xResizeLabelContainer.addWidget(xScaleLabel);
		
		xResizeTextContainer = FengGUI.createWidget(Container.class);

		xScaleTextEditor = FengGUI.createWidget(TextEditor.class);
		xScaleTextEditor.setText("-");
		xScaleTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		xScaleTextEditor.setLayoutData(new RowExLayoutData(true, true));
		
		xResizeTextContainer.addWidget(xScaleTextEditor);

		yResizeLabelContainer = FengGUI.createWidget(Container.class);
		
		yScaleLabel = FengGUI.createWidget(Label.class);
		yScaleLabel.setText("    " + yPrefix);
		
		yResizeLabelContainer.addWidget(yScaleLabel);
		
		yResizeTextContainer = FengGUI.createWidget(Container.class);
		
		yScaleTextEditor = FengGUI.createWidget(TextEditor.class);
		yScaleTextEditor.setText("-");
		yScaleTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		yScaleTextEditor.setLayoutData(new RowExLayoutData(true, true));
		
		yResizeTextContainer.addWidget(yScaleTextEditor);
		
		zResizeLabelContainer = FengGUI.createWidget(Container.class);
		
		zScaleLabel = FengGUI.createWidget(Label.class);
		zScaleLabel.setText("    " + zPrefix);

		zResizeLabelContainer.addWidget(zScaleLabel);
		
		zResizeTextContainer = FengGUI.createWidget(Container.class);
		
		zScaleTextEditor = FengGUI.createWidget(TextEditor.class);
		zScaleTextEditor.setText("-");
		zScaleTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		zScaleTextEditor.setLayoutData(new RowExLayoutData(true, true));

		zResizeTextContainer.addWidget(zScaleTextEditor);
		
		resizeContainer.addWidget(xResizeLabelContainer, xResizeTextContainer, yResizeLabelContainer, yResizeTextContainer, zResizeLabelContainer, zResizeTextContainer);
		
			/*zScaleTextEditor.addKeyListener(new IKeyListener() {
				
				public void keyTyped(Object arg0, KeyTypedEvent arg1) {
				}
				
				public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
					if(arg1.getKeyClass().equals(Key.ENTER)){
						if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
							logger.info("A fallback was not previously created for this object, creating one");
							FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
							changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
						}
						
						// perform the rotate action
						int newXScale = 0;
						try {
							newXScale = (int)FengUtils.getFloat(rotationTextEditor);
						} catch (FengTextContentException e) {
							// This issue should not come up since FengGUI is only allowing valid numbers
							logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
									"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
							rotationTextEditor.setText("0");
							GUIGameState.getInstance().getDisp().addWidget(
									FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
						}

					}
					
				}
				public void keyPressed(Object arg0, KeyPressedEvent arg1) {
				}
			});*/
			//System.out.println("world scale: " + SceneScape.getTargetSpatial().getWorldScale());
			//System.out.println("xExtent: " + bb.xExtent + " yExtent: " + bb.yExtent + " zExtent: " + bb.zExtent);
		
		
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
			
				MaterialState blue = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				blue.setAmbient(ColorRGBA.blue);
				blue.setDiffuse(ColorRGBA.blue);
				
				MaterialState red = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				red.setAmbient(ColorRGBA.red);
				red.setDiffuse(ColorRGBA.red);
				
				MaterialState yellow = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				yellow.setAmbient(ColorRGBA.yellow);
				yellow.setDiffuse(ColorRGBA.yellow);

				BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
				Vector3f center = bb.getCenter();
				
				float distance = (float)(Math.sqrt(Math.pow(Math.sqrt(Math.pow(bb.xExtent, 2) + Math.pow(bb.yExtent, 2)), 2) + Math.pow(bb.zExtent, 2)));
				float height = bb.yExtent * 0.0715f;
				/*
				rotateAxis = new AxisRods("$editorWidget-RotateAxis", true, distance, distance*0.01f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(rotateAxis);
				rotateAxis.setLocalTranslation(SceneScape.getTargetSpatial().getLocalTranslation());
				
				rotateAxis.updateRenderState();
				
				Vector3f xLoc = new Vector3f(0, 0, 0);
				
				if(rotateAxis.getxAxis().getChild("tip") instanceof Pyramid) {
					System.out.println("inside if");
					xLoc = ((Pyramid)rotateAxis.getxAxis().getChild("tip")).getLocalTranslation();
				}
				Vector3f yLoc = ((Pyramid)rotateAxis.getyAxis().getChild("tip")).getLocalTranslation();
				Vector3f zLoc = ((Pyramid)rotateAxis.getzAxis().getChild("tip")).getLocalTranslation();
				
				System.out.println("xLoc: " + xLoc);
				System.out.println("yLoc: " + yLoc);
				System.out.println("zLoc: " + zLoc);
				
				
				horizontalRotateTube = new Tube("$editorWidget-horizontalRotateTube", distance / 2f, distance / 1.9f, height);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(horizontalRotateTube);
				
				horizontalRotateTube.setLocalTranslation(xLoc);
				horizontalRotateTube.setRenderState(blue);
				horizontalRotateTube.updateRenderState();
				
				
				horizontalRotateTube = new Tube("$editorWidget-horizontalRotateTube", distance * 0.1f, distance * 0.05f, 0.01f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(horizontalRotateTube);
				
				horizontalRotateTube.setRenderState(blue);
				horizontalRotateTube.updateRenderState();
				horizontalRotateTube.setLocalTranslation(xLoc);
				*/
				
				horizontalRotateTube = new Tube("$editorWidget-horizontalRotateTube", distance * 1.1f, distance * 1.05f, height);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(horizontalRotateTube);
				
				horizontalRotateTube.setLocalTranslation(center);
				horizontalRotateTube.setRenderState(blue);
				
				horizontalRotateTube.setModelBound(new BoundingBox());
				horizontalRotateTube.updateModelBound();
				
				horizontalRotateTube.updateRenderState();

				verticalRotateTube = new Tube("$editorWidget-verticalRotateTube", distance * 1.1f, distance * 1.05f, height);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(verticalRotateTube);
				
				verticalRotateTube.setLocalTranslation(center);
				verticalRotateTube.rotateUpTo(new Vector3f(0, 0, 90));
				verticalRotateTube.setRenderState(yellow);

				verticalRotateTube.setModelBound(new BoundingBox());
				verticalRotateTube.updateModelBound();
				
				verticalRotateTube.updateRenderState();
				
				anotherRotateTube = new Tube("$editorWidget-anotherRotateTube", distance * 1.1f, distance * 1.05f, height);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(anotherRotateTube);
				
				anotherRotateTube.setLocalTranslation(center);
				anotherRotateTube.rotateUpTo(new Vector3f(90, 0, 0));
				anotherRotateTube.setRenderState(red);

				anotherRotateTube.setModelBound(new BoundingBox());
				anotherRotateTube.updateModelBound();
				
				anotherRotateTube.updateRenderState();
				
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
				
				BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
				Vector3f center = bb.getCenter();
				
				final float originalZ = bb.zExtent * 100;
				final float originalY = bb.yExtent * 100;
				final float originalX = bb.xExtent * 100;
				
				xScaleTextEditor.setText("" + (int)(bb.xExtent * 100));
				xScaleTextEditor.addKeyListener(new IKeyListener() {
					
					/* 
					 * We flip this value back and forth
					 * so that only one of the fired events is used
					 */
					boolean locked=false;
					
					public void keyTyped(Object arg0, KeyTypedEvent arg1) {
					}
					
					public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
						if(arg1.getKeyClass().equals(Key.ENTER) && !locked){
							locked=true;
							/*if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
								logger.info("A fallback was not previously created for this object, creating one");
								FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
								changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
							}*/
							
							logger.info("---------ENTER KEY PRESSED---------");
							
							// perform the rotate action
							float newXScale = 0;
							try {
								newXScale = (float) ((FengUtils.getFloat(xScaleTextEditor) / originalX) * SceneScape.getTargetSpatial().getLocalScale().x);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								rotationTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							
							SceneScape.getTargetSpatial().setLocalScale(new Vector3f(newXScale, SceneScape.getTargetSpatial().getLocalScale().y, SceneScape.getTargetSpatial().getLocalScale().z));

						}
						locked=false;
					}
					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});

				yScaleTextEditor.setText("" + (int)(bb.yExtent * 100));
				yScaleTextEditor.addKeyListener(new IKeyListener() {
					
					public void keyTyped(Object arg0, KeyTypedEvent arg1) {
					}
					
					public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
						if(arg1.getKeyClass().equals(Key.ENTER)){
							/*if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
								logger.info("A fallback was not previously created for this object, creating one");
								FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
								changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
							}*/
							
							// perform the rotate action
							float newYScale = 0;
							try {
								newYScale = (float) ((FengUtils.getFloat(yScaleTextEditor) / originalY) * SceneScape.getTargetSpatial().getLocalScale().y);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								rotationTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							
							SceneScape.getTargetSpatial().setLocalScale(new Vector3f(SceneScape.getTargetSpatial().getLocalScale().x, newYScale, SceneScape.getTargetSpatial().getLocalScale().z));

						}
						
					}
					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});
				
				zScaleTextEditor.setText("" + (int)(bb.zExtent * 100));
				zScaleTextEditor.addKeyListener(new IKeyListener() {
					
					public void keyTyped(Object arg0, KeyTypedEvent arg1) {
					}
					
					public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
						if(arg1.getKeyClass().equals(Key.ENTER)){
							/*if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
								logger.info("A fallback was not previously created for this object, creating one");
								FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
								changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
							}*/
							
							// perform the rotate action
							float newZScale = 0;
							try {
								newZScale = (float) ((FengUtils.getFloat(zScaleTextEditor) / originalZ) * SceneScape.getTargetSpatial().getLocalScale().z);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								rotationTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							
							SceneScape.getTargetSpatial().setLocalScale(new Vector3f(SceneScape.getTargetSpatial().getLocalScale().x, SceneScape.getTargetSpatial().getLocalScale().y, newZScale));

						}
						
					}
					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});
				
				MaterialState blue = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				blue.setAmbient(ColorRGBA.blue);
				blue.setDiffuse(ColorRGBA.blue);
				
				float distance = (float)(Math.sqrt(Math.pow(Math.sqrt(Math.pow(bb.xExtent, 2) + Math.pow(bb.yExtent, 2)), 2) + Math.pow(bb.zExtent, 2)));
				float scale = distance/5;
				System.out.println(distance + " " + Scale.fromMeter(1));
				
				upArrow = new Arrow("$editorWidget-upArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(upArrow);
				
				upArrow.setRenderState(blue);
				upArrow.setModelBound(new BoundingBox());
				upArrow.updateModelBound();
				upArrow.updateRenderState();
				upArrow.setLocalTranslation(center.x, center.y+bb.yExtent, center.z);
				
				leftArrow = new Arrow("$editorWidget-leftArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(leftArrow);
				
				leftArrow.setRenderState(blue);
				leftArrow.setModelBound(new BoundingBox());
				leftArrow.updateModelBound();
				leftArrow.updateRenderState();
				leftArrow.rotateUpTo(new Vector3f(90,0,0));
				leftArrow.setLocalTranslation(center.x+bb.xExtent, center.y, center.z);
				
				rightArrow = new Arrow("$editorWidget-rightArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(rightArrow);
				
				rightArrow.setRenderState(blue);
				rightArrow.setModelBound(new BoundingBox());
				rightArrow.updateModelBound();
				rightArrow.updateRenderState();
				rightArrow.rotateUpTo(new Vector3f(-90,0,0));
				rightArrow.setLocalTranslation(center.x-bb.xExtent, center.y, center.z);
				
				forwardArrow = new Arrow("$editorWidget-forwardArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(forwardArrow);
				
				forwardArrow.setRenderState(blue);
				forwardArrow.setModelBound(new BoundingBox());
				forwardArrow.updateModelBound();
				forwardArrow.updateRenderState();
				forwardArrow.rotateUpTo(new Vector3f(0, 0, 90));
				forwardArrow.setLocalTranslation(center.x, center.y, center.z+bb.zExtent);
				
				backwardArrow = new Arrow("$editorWidget-backwardArrow", scale, scale*0.25f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(backwardArrow);
				
				backwardArrow.setRenderState(blue);
				backwardArrow.setModelBound(new BoundingBox());
				backwardArrow.updateModelBound();
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
		
		editingContainer.addWidget(rotationLabel,
				xRotationLabel, xRotationSlider, yRotationLabel, yRotationSlider, zRotationLabel, zRotationSlider);
		
		editingContainer.addWidget(scaleLabel, resizeContainer);
		
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
				xRotationSlider.setEnabled(false);
				yRotationSlider.setEnabled(false);
				zRotationSlider.setEnabled(false);
		
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
				xRotationSlider.setEnabled(true);
				yRotationSlider.setEnabled(true);
				zRotationSlider.setEnabled(true);
			
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
