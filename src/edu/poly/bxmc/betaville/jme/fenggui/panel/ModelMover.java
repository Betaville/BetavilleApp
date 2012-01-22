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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.Key;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.event.key.KeyTypedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengTextContentException;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.Translator;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ModeledDesign;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * Utility window for rotating and moving objects
 * @author Skye Book
 *
 */
public class ModelMover extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(ModelMover.class);

	private HashMap<Integer, FallbackSet> changeFallbacks;

	private static enum Direction{
		NORTH,SOUTH,EAST,WEST,UP,DOWN
	}

	private final String rotationPrefix = "Rotation";
	private Label rotationLabel;
	private TextEditor rotation;

	private Label translateLabel;
	private TextEditor translateSpeed;
	private Button north;
	private Button south;
	private Button east;
	private Button west;

	private Button up;
	private Button down;

	private Button save;
	private Button reset;

	/**
	 * 
	 */
	public ModelMover() {

		changeFallbacks  = new HashMap<Integer, ModelMover.FallbackSet>();

		getContentContainer().setLayoutManager(new RowExLayout(false));

		// ROTATION CONTROL

		rotationLabel = FengGUI.createWidget(Label.class);
		rotationLabel.setText(rotationPrefix);
		rotationLabel.setLayoutData(new RowExLayoutData(false, true));

		rotation = FengGUI.createWidget(TextEditor.class);
		rotation.setText("0");
		rotation.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		rotation.setLayoutData(new RowExLayoutData(true, true));
		rotation.addKeyListener(new IKeyListener() {

			public void keyTyped(Object arg0, KeyTypedEvent arg1) {
			}

			public void keyReleased(Object arg0, KeyReleasedEvent arg1) {
				if(arg1.getKeyClass().equals(Key.ENTER)){
					// has the object already been moved?
					if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
						logger.info("A fallback was not previously created for this object, creating one");
						FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
						changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
					}

					// perform the rotate action
					float newRotation=0;
					try {
						newRotation = FengUtils.getFloat(rotation);
					} catch (FengTextContentException e) {
						// This issue should not come up since FengGUI is only allowing valid numbers
						logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
								"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
						rotation.setText("0");
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

		Container rotateContainer = FengGUI.createWidget(Container.class);
		rotateContainer.setLayoutManager(new RowExLayout(true));
		rotateContainer.addWidget(rotationLabel, rotation);

		// TRANSLATE CONTROL

		translateLabel = FengGUI.createWidget(Label.class);
		translateLabel.setText("Movement Per Click");
		translateLabel.setLayoutData(new RowExLayoutData(false, true));

		translateSpeed = FengGUI.createWidget(TextEditor.class);
		translateSpeed.setText("0");
		translateSpeed.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		translateSpeed.setLayoutData(new RowExLayoutData(true, true));

		Container translateSpeedContainer = FengGUI.createWidget(Container.class);
		translateSpeedContainer.setLayoutManager(new RowExLayout(true));
		translateSpeedContainer.addWidget(translateLabel, translateSpeed);

		north = FengGUI.createWidget(Button.class);
		north.setText("North");
		north.setLayoutData(new RowExLayoutData(false, true));
		north.addButtonPressedListener(new MoveListener(Direction.NORTH));

		south = FengGUI.createWidget(Button.class);
		south.setText("South");
		south.setLayoutData(new RowExLayoutData(false, true));
		south.addButtonPressedListener(new MoveListener(Direction.SOUTH));

		east = FengGUI.createWidget(Button.class);
		east.setText("East");
		east.setLayoutData(new RowExLayoutData(false, true));
		east.addButtonPressedListener(new MoveListener(Direction.EAST));

		west = FengGUI.createWidget(Button.class);
		west.setText("West");
		west.setLayoutData(new RowExLayoutData(false, true));
		west.addButtonPressedListener(new MoveListener(Direction.WEST));

		Container translateActionContainer = FengGUI.createWidget(Container.class);
		translateActionContainer.setLayoutManager(new RowExLayout(true));
		translateActionContainer.addWidget(north, south, east, west);

		// ALTITUDE CONTROL
		up = FengGUI.createWidget(Button.class);
		up.setText("Up");
		up.setLayoutData(new RowExLayoutData(false, true));
		up.addButtonPressedListener(new MoveListener(Direction.UP));

		down = FengGUI.createWidget(Button.class);
		down.setText("Down");
		down.setLayoutData(new RowExLayoutData(false, true));
		down.addButtonPressedListener(new MoveListener(Direction.DOWN));

		Container upDownContainer = FengGUI.createWidget(Container.class);
		upDownContainer.setLayoutManager(new RowExLayout(true));
		upDownContainer.addWidget(up, down);

		// SAVE/REVERT CONTROL

		save = FengGUI.createWidget(Button.class);
		save.setText("Save");
		save.setLayoutData(new RowExLayoutData(false, true));
		save.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				if(changeFallbacks.get(SceneScape.getPickedDesign().getID())!=null){
					// check if the location has been changed

					// check if the rotation has been updated

					if(NetPool.getPool().getSecureConnection().changeModeledDesignLocation(SceneScape.getPickedDesign().getID(),
							((ModeledDesign)SceneScape.getPickedDesign()).getRotationY(), SceneScape.getPickedDesign().getCoordinate())){
						changeFallbacks.remove(SceneScape.getPickedDesign().getID());
						logger.error("Network Save Success");
						GUIGameState.getInstance().getDisp().addWidget(
								FengUtils.createDismissableWindow("Betaville",
										"This object's location has been saved", "ok", true));
					}
					else{
						logger.info("Network Save Failed");
						GUIGameState.getInstance().getDisp().addWidget(
								FengUtils.createDismissableWindow("Betaville",
										"This object's location could not be saved", "ok", true));
					}
				}
				else{
					logger.warn("The object does not appear to have been changed should the " +
					"reset button really be visible?");
					GUIGameState.getInstance().getDisp().addWidget(
							FengUtils.createDismissableWindow("Betaville",
									"This object does not appear to have been moved", "ok", true));
				}
			}
		});

		reset = FengGUI.createWidget(Button.class);
		reset.setText("Reset");
		reset.setLayoutData(new RowExLayoutData(false, true));
		reset.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				FallbackSet fallbacks = changeFallbacks.get(SceneScape.getPickedDesign().getID());

				// If a set of fallbacks has been stored for this object, then we can put things back
				if(fallbacks!=null){
					SceneScape.getTargetSpatial().setLocalTranslation(JME2MapManager.instance.locationToBetaville(fallbacks.fallbackLocation));
					SceneScape.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).setCoordinate((UTMCoordinate)fallbacks.fallbackLocation);

					SceneScape.getTargetSpatial().setLocalRotation(Rotator.angleY((int)fallbacks.fallbackRotation));
					SceneScape.getTargetSpatial().setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)SceneScape.getPickedDesign()).getRotationX(), (int)fallbacks.fallbackRotation, ((ModeledDesign)SceneScape.getPickedDesign()).getRotationZ()));
					((ModeledDesign)SceneScape.getPickedDesign()).setRotationY((int)fallbacks.fallbackRotation);
				}
				else{
					logger.warn("No fallbacks were available for the selected object, should the " +
					"reset button really be visible?");
					GUIGameState.getInstance().getDisp().addWidget(
							FengUtils.createDismissableWindow("Betaville",
									"This object does not appear to have been previously moved", "ok", true));
				}
			}
		});

		Container saveRevertContainer = FengGUI.createWidget(Container.class);
		saveRevertContainer.setLayoutManager(new RowExLayout(true));
		saveRevertContainer.addWidget(reset, save);

		// ADD CONTAINERS TO THE WINDOW

		getContentContainer().addWidget(rotateContainer);
		getContentContainer().addWidget(translateSpeedContainer);
		getContentContainer().addWidget(translateActionContainer);
		getContentContainer().addWidget(upDownContainer);
		getContentContainer().addWidget(saveRevertContainer);



		SceneScape.addSelectionListener(new ISpatialSelectionListener() {

			public void selectionCleared(Design previousDesign) {
				if(!isInWidgetTree()) return;

				enableAll(false);
			}

			public void designSelected(Spatial spatial, Design design) {
				if(!isInWidgetTree()) return;

				enableAll(true);
			}
		});
	}

	private void enableAll(boolean enabled){
		north.setEnabled(enabled);
		south.setEnabled(enabled);
		east.setEnabled(enabled);
		west.setEnabled(enabled);
		rotation.setEnabled(enabled);
		translateSpeed.setEnabled(enabled);
		up.setEnabled(enabled);
		down.setEnabled(enabled);
		save.setEnabled(enabled);
		reset.setEnabled(enabled);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("Model Mover");
		setHeight(getHeight()+10);
	}


	private class MoveListener implements IButtonPressedListener{

		private Direction direction;

		private MoveListener(Direction direction){
			this.direction=direction;
		}

		public void buttonPressed(Object arg0, ButtonPressedEvent arg1){
			// has the object already been moved?
			if(changeFallbacks.get(SceneScape.getPickedDesign().getID())==null){
				logger.info("A fallback was not previously created for this object, creating one");
				FallbackSet newSet = new FallbackSet(SceneScape.getPickedDesign().getCoordinate(), ((ModeledDesign)SceneScape.getPickedDesign()).getRotationY());
				changeFallbacks.put(SceneScape.getPickedDesign().getID(), newSet);
			}

			float moveAmount = 0;
			try {
				moveAmount = FengUtils.getFloat(translateSpeed);
			} catch (FengTextContentException e) {
				// This issue should not come up since FengGUI is only allowing valid numbers
				logger.warn("An invalid value somehow made its way from a FengGUI input field that should have been " +
						"restricted to numbers and decimals only.  Please ensure that this is the case!", e);
				translateSpeed.setText("0");
				GUIGameState.getInstance().getDisp().addWidget(
						FengUtils.createDismissableWindow("Betaville", "Please input a valid floating point number", "ok", true));
			}

			switch (direction) {
			case NORTH:
				Translator.moveNorth(SceneScape.getTargetSpatial(), moveAmount);
				SceneScape.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(0, moveAmount, 0);
				break;
			case SOUTH:
				Translator.moveSouth(SceneScape.getTargetSpatial(), moveAmount);
				SceneScape.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(0, -moveAmount, 0);
				break;
			case EAST:
				Translator.moveEast(SceneScape.getTargetSpatial(), moveAmount);
				SceneScape.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(moveAmount, 0, 0);
				break;
			case WEST:
				Translator.moveWest(SceneScape.getTargetSpatial(), moveAmount);
				SceneScape.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(-moveAmount, 0, 0);
				break;
			case UP:
				Translator.moveUp(SceneScape.getTargetSpatial(), moveAmount);
				SceneScape.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(0, 0, moveAmount);
				break;
			case DOWN:
				Translator.moveDown(SceneScape.getTargetSpatial(), moveAmount);
				SceneScape.getCity().findDesignByFullIdentifier(SceneScape.getTargetSpatial().getName()).getCoordinate().move(0, 0, -moveAmount);
				break;
			}
		}

	}


	private class FallbackSet{
		private ILocation fallbackLocation;
		private float fallbackRotation;

		private FallbackSet(ILocation location, float roation){
			this.fallbackLocation=location.clone();
			this.fallbackRotation=roation;
		}
	}
}
