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
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.AxisRods;
import com.jme.scene.shape.Arrow;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.scene.shape.Tube;

/**
 * @author Vivian(Hyun) Park
 *
 */
public class EditBuildingWindow extends Window implements IBetavilleWindow {
	private static Logger logger = Logger.getLogger(EditBuildingWindow.class);

	private int width=230;
	private int height=400;
	private int offset=15;

	private Container editingContainer;

	private Container buttonContainer;

	private FixedButton translate;
	private AxisRods moveRod;

	private FixedButton rotate;
	private AxisRods rotateAxis;
	private Tube horizontalRotateTube;
	private Tube verticalRotateTube;
	private Tube anotherRotateTube;

	private FixedButton resize;
	private AxisRods scaleRod;
	private Box boundingBox;
	private Box newTipX;
	private Box newTipY;
	private Box newTipZ;

	private FixedButton delete;
	private FixedButton reset;
	private FixedButton save;

	private Container attributesContainer;

	private Container translateContainer;
	private Label translateLabel;
	private String translateString = "Coordinates";

	private Container translateTextContainer;

	private Container xTranslateContainer;
	private Label xTranslateLabel;
	private TextEditor xTranslateTextEditor;

	private Container yTranslateContainer;
	private Label yTranslateLabel;
	private TextEditor yTranslateTextEditor;

	private Container zTranslateContainer;
	private Label zTranslateLabel;
	private TextEditor zTranslateTextEditor;

	private Container rotateContainer;
	private Label rotationLabel;
	private String rotationText = "Rotation";

	private Container rotateSliderContainer;

	private Slider xRotationSlider;
	private Slider yRotationSlider;
	private Slider zRotationSlider;

	private Container rotateTextContainer;

	private Container xRotateContainer;
	private Label xRotationLabel;
	private TextEditor xRotateTextEditor;

	private Container yRotateContainer;
	private Label yRotationLabel;
	private TextEditor yRotateTextEditor;

	private Container zRotateContainer;
	private Label zRotationLabel;
	private TextEditor zRotateTextEditor;

	private Container scaleContainer;
	private Label scaleLabel;
	private String scaleString = "Dimensions";

	private Container scaleTextContainer;	

	private Container xScaleContainer;
	private Label xScaleLabel;
	private TextEditor xScaleTextEditor;

	private Container yScaleContainer;
	private Label yScaleLabel;
	private TextEditor yScaleTextEditor;

	private Container zScaleContainer;
	private Label zScaleLabel;
	private TextEditor zScaleTextEditor;

	private String xPrefix="X:";
	private String yPrefix="Y:";
	private String zPrefix="Z:";

	private List<PanelAction> panelActions;

	private float opacityAmount = 0.5f;

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

		buttonContainer = FengGUI.createWidget(Container.class);
		buttonContainer.setLayoutManager(new RowExLayout(false));
		buttonContainer.addWidget(translate, rotate, resize, delete, reset, save);

		editingContainer = FengGUI.createWidget(Container.class);
		editingContainer.setLayoutManager(new RowExLayout(false));

		//attributesContainer = FengGUI.createWidget(Container.class);
		//attributesContainer.setLayoutManager(new RowExLayout(false));

		/*****************************************************
		 * Translate Labels/Text Edit Fields
		 *****************************************************/		

		translateContainer = FengGUI.createWidget(Container.class);
		translateContainer.setLayoutManager(new RowExLayout(false));

		translateLabel = FengGUI.createWidget(Label.class);
		translateLabel.setText(translateString);

		translateTextContainer = FengGUI.createWidget(Container.class);
		translateTextContainer.setLayoutManager(new RowExLayout(true));

		/* 
		 * xTranslateContainer
		 */
		xTranslateContainer = FengGUI.createWidget(Container.class);

		xTranslateLabel = FengGUI.createWidget(Label.class);
		xTranslateLabel.setText(xPrefix);

		xTranslateTextEditor = FengGUI.createWidget(TextEditor.class);
		xTranslateTextEditor.setText("-");
		xTranslateTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		xTranslateTextEditor.setLayoutData(new RowExLayoutData(true, true));

		xTranslateContainer.addWidget(xTranslateLabel, xTranslateTextEditor);

		/*
		 * yTranslateContainer
		 */
		yTranslateContainer = FengGUI.createWidget(Container.class);

		yTranslateLabel = FengGUI.createWidget(Label.class);
		yTranslateLabel.setText("    " + yPrefix);

		yTranslateTextEditor = FengGUI.createWidget(TextEditor.class);
		yTranslateTextEditor.setText("-");
		yTranslateTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		yTranslateTextEditor.setLayoutData(new RowExLayoutData(true, true));

		yTranslateContainer.addWidget(yTranslateLabel, yTranslateTextEditor);

		/*
		 * zTranslateContainer
		 */
		zTranslateContainer = FengGUI.createWidget(Container.class);

		zTranslateLabel = FengGUI.createWidget(Label.class);
		zTranslateLabel.setText("    " + zPrefix);

		zTranslateTextEditor = FengGUI.createWidget(TextEditor.class);
		zTranslateTextEditor.setText("-");
		zTranslateTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		zTranslateTextEditor.setLayoutData(new RowExLayoutData(true, true));

		zTranslateContainer.addWidget(zTranslateLabel, zTranslateTextEditor);

		translateTextContainer.addWidget(xTranslateContainer, yTranslateContainer, zTranslateContainer);
		translateContainer.addWidget(translateLabel, translateTextContainer);

		/*****************************************************
		 * End of Translate Labels/Text Edit Fields
		 *****************************************************/

		/*****************************************************
		 * Rotation Sliders/Text Edit Fields
		 *****************************************************/
		/*
		 * crashes when the knob reaches the end of the slider
		 */

		rotateContainer = FengGUI.createWidget(Container.class);
		rotateContainer.setLayoutManager(new RowExLayout(false));

		//rotateLabelContainer = FengGUI.createWidget(Container.class);
		//rotateLabelContainer
		/*
		 * rotateSliderContainer
		 */
		rotateSliderContainer = FengGUI.createWidget(Container.class);
		rotateSliderContainer.setLayoutManager(new RowExLayout(false));

		rotationLabel = FengGUI.createWidget(Label.class);
		rotationLabel.setText(rotationText);

		xRotationLabel = FengGUI.createWidget(Label.class);
		xRotationLabel.setText(xPrefix+"0");

		yRotationLabel = FengGUI.createWidget(Label.class);
		yRotationLabel.setText(yPrefix+"0");

		zRotationLabel = FengGUI.createWidget(Label.class);
		zRotationLabel.setText(zPrefix+"0");

		xRotationSlider = FengGUI.createSlider(true);
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

		rotateSliderContainer.addWidget(xRotationLabel, xRotationSlider, yRotationLabel, yRotationSlider, zRotationLabel, zRotationSlider);

		/*
		 * rotateTextEditorContainer
		 */
		rotateTextContainer = FengGUI.createWidget(Container.class);
		rotateTextContainer.setLayoutManager(new RowExLayout(true));

		/* 
		 * xRotateContainer
		 */
		xRotateContainer = FengGUI.createWidget(Container.class);

		xRotationLabel = FengGUI.createWidget(Label.class);
		xRotationLabel.setText(xPrefix);

		xRotateTextEditor = FengGUI.createWidget(TextEditor.class);
		xRotateTextEditor.setText("-");
		xRotateTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		xRotateTextEditor.setLayoutData(new RowExLayoutData(true, true));

		xRotateContainer.addWidget(xRotationLabel, xRotateTextEditor);

		/*
		 * yRotateContainer
		 */
		yRotateContainer = FengGUI.createWidget(Container.class);

		yRotationLabel = FengGUI.createWidget(Label.class);
		yRotationLabel.setText("    " + yPrefix);

		yRotateTextEditor = FengGUI.createWidget(TextEditor.class);
		yRotateTextEditor.setText("-");
		yRotateTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		yRotateTextEditor.setLayoutData(new RowExLayoutData(true, true));

		yRotateContainer.addWidget(yRotationLabel, yRotateTextEditor);

		/*
		 * zRotateContainer
		 */
		zRotateContainer = FengGUI.createWidget(Container.class);

		zRotationLabel = FengGUI.createWidget(Label.class);
		zRotationLabel.setText("    " + zPrefix);

		zRotateTextEditor = FengGUI.createWidget(TextEditor.class);
		zRotateTextEditor.setText("-");
		zRotateTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		zRotateTextEditor.setLayoutData(new RowExLayoutData(true, true));

		zRotateContainer.addWidget(zRotationLabel, zRotateTextEditor);

		rotateTextContainer.addWidget(xRotateContainer, yRotateContainer, zRotateContainer);

		rotateContainer.addWidget(rotationLabel, rotateSliderContainer, rotateTextContainer);


		/*****************************************************
		 * End of Rotation Sliders/Text Edit Fields
		 *****************************************************/

		/*****************************************************
		 * Scale Labels/Text Edit Fields
		 *****************************************************/

		scaleLabel = FengGUI.createWidget(Label.class);
		scaleLabel.setText(scaleString);

		scaleContainer = FengGUI.createWidget(Container.class);
		scaleContainer.setLayoutManager(new RowExLayout(false));

		scaleTextContainer = FengGUI.createWidget(Container.class);
		scaleTextContainer.setLayoutManager(new RowExLayout(true));
		/*
		 * xScaleContainer
		 */
		xScaleContainer = FengGUI.createWidget(Container.class);

		xScaleLabel = FengGUI.createWidget(Label.class);
		xScaleLabel.setText(xPrefix);

		xScaleTextEditor = FengGUI.createWidget(TextEditor.class);
		xScaleTextEditor.setText("-");
		xScaleTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		xScaleTextEditor.setLayoutData(new RowExLayoutData(true, true));

		xScaleContainer.addWidget(xScaleLabel, xScaleTextEditor);

		/*
		 * yScaleContainer
		 */
		yScaleContainer = FengGUI.createWidget(Container.class);

		yScaleLabel = FengGUI.createWidget(Label.class);
		yScaleLabel.setText("    " + yPrefix);

		yScaleTextEditor = FengGUI.createWidget(TextEditor.class);
		yScaleTextEditor.setText("-");
		yScaleTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		yScaleTextEditor.setLayoutData(new RowExLayoutData(true, true));

		yScaleContainer.addWidget(yScaleLabel, yScaleTextEditor);

		/*
		 * zScaleContainer
		 */
		zScaleContainer = FengGUI.createWidget(Container.class);

		zScaleLabel = FengGUI.createWidget(Label.class);
		zScaleLabel.setText("    " + zPrefix);

		zScaleTextEditor = FengGUI.createWidget(TextEditor.class);
		zScaleTextEditor.setText("-");
		zScaleTextEditor.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		zScaleTextEditor.setLayoutData(new RowExLayoutData(true, true));

		zScaleContainer.addWidget(zScaleLabel, zScaleTextEditor);

		scaleTextContainer.addWidget(xScaleContainer, yScaleContainer, zScaleContainer);
		scaleContainer.addWidget(scaleLabel, scaleTextContainer);

		/*****************************************************
		 * End of Scale Labels/Text Edit Fields
		 *****************************************************/


		/*****************************************************
		 * Buttons/Widgets
		 *****************************************************/

		translate = FengGUI.createWidget(FixedButton.class);
		translate.setText("Translate");
		translate.setWidth(translate.getWidth()+10);
		translate.setEnabled(true);
		translate.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {

				SceneGameState.getInstance().getEditorWidgetNode().detachAllChildren();
				SceneScape.getTargetSpatial().updateRenderState();

				//attributesContainer.removeAllWidgets();
				
				MaterialState yellow = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				yellow.setAmbient(ColorRGBA.yellow);
				yellow.setDiffuse(ColorRGBA.yellow);

				MaterialState red = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				red.setAmbient(ColorRGBA.red);
				red.setDiffuse(ColorRGBA.red);

				MaterialState green = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				green.setAmbient(ColorRGBA.green);
				green.setDiffuse(ColorRGBA.green);

				MaterialState blue = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				blue.setAmbient(ColorRGBA.blue);
				blue.setDiffuse(ColorRGBA.blue);

				BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());

				float distance = (float)(Math.sqrt(Math.pow(Math.sqrt(Math.pow(bb.xExtent, 2) + Math.pow(bb.yExtent, 2)), 2) + Math.pow(bb.zExtent, 2)));

				final Vector3f loc = SceneScape.getTargetSpatial().getLocalTranslation();

				xTranslateTextEditor.setText("" + Round(loc.x, 2));
				xTranslateTextEditor.addKeyListener(new IKeyListener() {
					
					boolean locked = false;
					
					public void keyTyped(Object arg0, KeyTypedEvent arg1) {
					}

					public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
						if(arg1.getKeyClass().equals(Key.ENTER) && !locked){
							locked = true;
							// has the object already been moved?
							/*if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
								logger.info("A fallback was not previously created for this object, creating one");
								FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
								changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
							}
							 */
							// perform the rotate action
							float newLocation=0;
							try {
								newLocation = FengUtils.getFloat(xTranslateTextEditor);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								xRotateTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							
							SceneScape.getTargetSpatial().setLocalTranslation(newLocation, loc.y, loc.z);

						}
						locked = false;
					}
					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});

				yTranslateTextEditor.setText("" + Round(loc.y, 2));
				yTranslateTextEditor.addKeyListener(new IKeyListener() {
					
					boolean locked = false;
					
					public void keyTyped(Object arg0, KeyTypedEvent arg1) {
					}

					public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
						if(arg1.getKeyClass().equals(Key.ENTER) && !locked){
							locked = true;
							// has the object already been moved?
							/*if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
								logger.info("A fallback was not previously created for this object, creating one");
								FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
								changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
							}
							 */
							// perform the rotate action
							float newLocation=0;
							try {
								newLocation = FengUtils.getFloat(yTranslateTextEditor);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								xRotateTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							
							SceneScape.getTargetSpatial().setLocalTranslation(loc.x, newLocation, loc.y);
						}
						locked = false;
					}
					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});

				zTranslateTextEditor.setText("" + Round(loc.z, 2));
				zTranslateTextEditor.addKeyListener(new IKeyListener() {
					
					boolean locked = false;
					
					public void keyTyped(Object arg0, KeyTypedEvent arg1) {
					}

					public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
						if(arg1.getKeyClass().equals(Key.ENTER) && !locked){
							locked = true;
							// has the object already been moved?
							/*if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
								logger.info("A fallback was not previously created for this object, creating one");
								FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
								changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
							}
							 */
							// perform the rotate action
							float newLocation=0;
							try {
								newLocation = FengUtils.getFloat(zTranslateTextEditor);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								xRotateTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							SceneScape.getTargetSpatial().setLocalTranslation(loc.x, loc.y, newLocation);

						}
						locked = false;
					}
					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});

				moveRod = new AxisRods("$editorWidget-axis", true, distance, distance*0.01f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(moveRod);

				moveRod.setModelBound(new BoundingBox());
				moveRod.updateModelBound();
				moveRod.setLocalTranslation(SceneScape.getTargetSpatial().getLocalTranslation());

				moveRod.getxAxis().setRenderState(red);

				if(moveRod.getxAxis().getChild("tip") instanceof Pyramid) {

					float arrowLength = ((Arrow)moveRod.getxAxis()).getLength();
					float arrowWidth = ((Arrow)moveRod.getxAxis()).getWidth();

					((Pyramid)moveRod.getxAxis().getChild("tip")).updateGeometry(8 * arrowWidth, arrowLength / 9f);
					((Pyramid)moveRod.getxAxis().getChild("tip")).translatePoints(0, arrowLength * .5f, 0);

				}

				moveRod.getyAxis().setRenderState(green);

				if(moveRod.getyAxis().getChild("tip") instanceof Pyramid) {

					float arrowLength = ((Arrow)moveRod.getyAxis()).getLength();
					float arrowWidth = ((Arrow)moveRod.getyAxis()).getWidth();

					((Pyramid)moveRod.getyAxis().getChild("tip")).updateGeometry(8 * arrowWidth, arrowLength / 9f);
					((Pyramid)moveRod.getyAxis().getChild("tip")).translatePoints(0, arrowLength * .5f, 0);

				}


				moveRod.getyAxis().setRenderState(blue);

				if(moveRod.getzAxis().getChild("tip") instanceof Pyramid) {

					float arrowLength = ((Arrow)moveRod.getzAxis()).getLength();
					float arrowWidth = ((Arrow)moveRod.getzAxis()).getWidth();

					((Pyramid)moveRod.getzAxis().getChild("tip")).updateGeometry(8 * arrowWidth, arrowLength / 9f);
					((Pyramid)moveRod.getzAxis().getChild("tip")).translatePoints(0, arrowLength * .5f, 0);

				}

				moveRod.updateRenderState();

				////attributesContainer.addWidget(translateContainer);
				
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

				//attributesContainer.removeAllWidgets();
				
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

				float[] angles = new float[3];

				SceneScape.getTargetSpatial().getLocalRotation().toAngles(angles);


				xRotateTextEditor.setText("" + angles[0]);
				xRotateTextEditor.addKeyListener(new IKeyListener() {

					public void keyTyped(Object arg0, KeyTypedEvent arg1) {
					}

					public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
						if(arg1.getKeyClass().equals(Key.ENTER)){
							// has the object already been moved?
							/*if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
								logger.info("A fallback was not previously created for this object, creating one");
								FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
								changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
							}
							 */
							// perform the rotate action
							int newRotation=0;
							try {
								newRotation = (int)FengUtils.getFloat(xRotateTextEditor);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								xRotateTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleX(newRotation));
							SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(newRotation, ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationZ()));
							((ModeledDesign)SceneScape.getPickedDesign()).setRotationX(newRotation);
						}
					}

					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});

				yRotateTextEditor.setText("" + angles[1]);
				yRotateTextEditor.addKeyListener(new IKeyListener() {

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
							 */
							// perform the rotate action
							int newRotation=0;
							try {
								newRotation = (int)FengUtils.getFloat(yRotateTextEditor);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								yRotateTextEditor.setText("0");
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
				});

				zRotateTextEditor.setText("" + angles[2]);
				zRotateTextEditor.addKeyListener(new IKeyListener() {

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
							 */
							// perform the rotate action
							int newRotation=0;
							try {
								newRotation = (int)FengUtils.getFloat(zRotateTextEditor);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								zRotateTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleZ(newRotation));
							SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getPickedDesign()).getRotationX(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY(), newRotation));
							((ModeledDesign)SceneScape.getPickedDesign()).setRotationZ(newRotation);
						}
					}

					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});

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

				//attributesContainer.addWidget(rotateContainer);
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

				//attributesContainer.removeAllWidgets();
				
				MaterialState yellow = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				yellow.setAmbient(ColorRGBA.yellow);
				yellow.setDiffuse(ColorRGBA.yellow);

				MaterialState red = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				red.setAmbient(ColorRGBA.red);
				red.setDiffuse(ColorRGBA.red);

				MaterialState green = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				green.setAmbient(ColorRGBA.green);
				green.setDiffuse(ColorRGBA.green);

				MaterialState blue = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				blue.setAmbient(ColorRGBA.blue);
				blue.setDiffuse(ColorRGBA.blue);

				BoundingBox bb = ((BoundingBox)SceneScape.getTargetSpatial().getWorldBound());
				Vector3f center = bb.getCenter();

				float distance = (float)(Math.sqrt(Math.pow(Math.sqrt(Math.pow(bb.xExtent, 2) + Math.pow(bb.yExtent, 2)), 2) + Math.pow(bb.zExtent, 2)));
				//float newTipDimensions = distance/30;

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
								xScaleTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							System.out.println("\nname: " + SceneScape.getTargetSpatial().getName());
							System.out.println("original X: " + originalX);
							System.out.println("original local scale: " + SceneScape.getTargetSpatial().getLocalScale() + "\nnewXscale: " + newXScale);
							SceneScape.getTargetSpatial().setLocalScale(new Vector3f(newXScale, SceneScape.getTargetSpatial().getLocalScale().y, SceneScape.getTargetSpatial().getLocalScale().z));
							System.out.println("new local scale: " + SceneScape.getTargetSpatial().getLocalScale());

						}
						locked=false;
					}
					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});

				yScaleTextEditor.setText("" + (int)(bb.yExtent * 100));
				yScaleTextEditor.addKeyListener(new IKeyListener() {

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
							float newYScale = 0;
							try {
								newYScale = (float) ((FengUtils.getFloat(yScaleTextEditor) / originalY) * SceneScape.getTargetSpatial().getLocalScale().y);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								xScaleTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							System.out.println("\nname: " + SceneScape.getTargetSpatial().getName());
							System.out.println("original Y: " + originalY);
							System.out.println("original local scale: " + SceneScape.getTargetSpatial().getLocalScale() + "\nnewYscale: " + newYScale);
							SceneScape.getTargetSpatial().setLocalScale(new Vector3f(SceneScape.getTargetSpatial().getLocalScale().y, newYScale, SceneScape.getTargetSpatial().getLocalScale().z));
							System.out.println("new local scale: " + SceneScape.getTargetSpatial().getLocalScale());

						}
						locked=false;
					}
					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});

				zScaleTextEditor.setText("" + (int)(bb.zExtent * 100));
				zScaleTextEditor.addKeyListener(new IKeyListener() {

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
							float newZScale = 0;
							try {
								newZScale = (float) ((FengUtils.getFloat(zScaleTextEditor) / originalZ) * SceneScape.getTargetSpatial().getLocalScale().z);
							} catch (FengTextContentException e) {
								// This issue should not come up since FengGUI is only allowing valid numbers
								logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
										"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
								zScaleTextEditor.setText("0");
								GUIGameState.getInstance().getDisp().addWidget(
										FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
							}
							System.out.println("\nname: " + SceneScape.getTargetSpatial().getName());
							System.out.println("original Z: " + originalZ);
							System.out.println("original local scale: " + SceneScape.getTargetSpatial().getLocalScale() + "\nnewZscale: " + newZScale);
							SceneScape.getTargetSpatial().setLocalScale(new Vector3f(SceneScape.getTargetSpatial().getLocalScale().x, SceneScape.getTargetSpatial().getLocalScale().y, newZScale));
							System.out.println("new local scale: " + SceneScape.getTargetSpatial().getLocalScale());

						}
						locked=false;
					}
					public void keyPressed(Object arg0, KeyPressedEvent arg1) {
					}
				});

				/*
				 * Create TransparentColor
				 */
				MaterialState transparentColor = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
				transparentColor.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, opacityAmount));
				transparentColor.setDiffuse(new ColorRGBA(0.1f, 0.5f, 0.8f, opacityAmount));
				transparentColor.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
				transparentColor.setShininess(128.0f);
				transparentColor.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.0f, opacityAmount));
				transparentColor.setEnabled(true);

				transparentColor.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);

				final BlendState alphaState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
				alphaState.setBlendEnabled(true);
				alphaState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
				alphaState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
				alphaState.setTestEnabled(true);
				alphaState.setTestFunction(BlendState.TestFunction.GreaterThan);
				alphaState.setEnabled(true);

				/*
				 * Create a box called boundingBox and set its color+transparency
				 */
				boundingBox = new Box("$editorWidget-boundingBox", center, bb.xExtent, bb.yExtent, bb.zExtent);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(boundingBox);

				boundingBox.setRenderState(transparentColor);
				boundingBox.setModelBound(new BoundingBox());
				boundingBox.updateModelBound();
				boundingBox.updateRenderState();
				boundingBox.setRenderState(alphaState);
				boundingBox.updateRenderState();
				boundingBox.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
				//boundingBox.setLocalRotation(SceneScape.getTargetSpatial().getLocalRotation());
				//boundingBox.updateRenderState();

				/*
				 * Create a scale rod + take off arrow tips(pyramids) and replace them with boxes
				 */

				scaleRod = new BAxisRods("$editorWidget-scaleRod", true, distance, distance*0.01f);
				SceneGameState.getInstance().getEditorWidgetNode().attachChild(scaleRod);

				scaleRod.setModelBound(new BoundingBox());
				scaleRod.updateModelBound();
				scaleRod.setLocalTranslation(center);
				System.out.println("local rotation of the model: " + SceneScape.getTargetSpatial().getLocalRotation());
				System.out.println("world rotation of the model: " + SceneScape.getTargetSpatial().getWorldRotation()); 
				System.out.println("local rotation of scaleRod: " + scaleRod.getLocalRotation());
				System.out.println("world rotation of scaleRod: " + scaleRod.getWorldRotation());
				
				scaleRod.updateRenderState();

				logger.info("added arrows");
				
				//attributesContainer.addWidget(scaleContainer);

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

		buttonContainer.addWidget(translate, rotate, resize, delete, reset, save);
		editingContainer.addWidget(buttonContainer, translateContainer, scaleContainer, rotateContainer);

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

	/**
	 * http://www.roseindia.net/java/beginners/RoundTwoDecimalPlaces.shtml
	 * 
	 * @param Rval	value you want rounded
	 * @param Rpl	rounding decimal place
	 * @return result of rounding
	 */
	private static float Round(float Rval, int Rpl) {
		float p = (float)Math.pow(10,Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (float)tmp/p;
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
