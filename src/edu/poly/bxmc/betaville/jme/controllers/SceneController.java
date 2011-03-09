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
package edu.poly.bxmc.betaville.jme.controllers;

import static com.jme.input.KeyInput.KEY_ESCAPE;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.controls.GameControlManager;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.math.FastMath;
import com.jme.renderer.Camera;
import com.jme.scene.Controller;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.SafeShutdown;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * The controller used to modify SceneGameState through user interaction and
 * environmental changes.
 * 
 * @author Caroline Bouchat
 * @author Skye Book
 */
public class SceneController extends Controller {
	private static Logger logger = Logger.getLogger(SceneController.class);

	/**
	 * Constant <serialVersionUID> - Serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constant <moveSpeed> - The move's speed (originally 750)
	 */
	private float moveSpeed = Scale.fromMeter(125);

	/**
	 * Constant <turnSpeed> - The turn's speed
	 */
	private float turnSpeed = 1.5f;

	/**
	 * Enum <StandardAction> - Enumeration of all standard actions
	 */
	private enum StandardAction {
		EXIT
	};

	/**
	 * Attribute <firstPersonHandler> - Controller the movement of the camera
	 */
	private FirstPersonHandler firstPersonHandler;
	/**
	 * Attribute <manager> - Game Control's manager
	 */
	private GameControlManager controlManager;

	private Window closeWindow;

	private Camera camera = DisplaySystem.getDisplaySystem().getRenderer()
			.getCamera();
	private float cameraDirX = 42;

	double h = 0;
	double t = 1;

	// private Compass compass = new Compass();


	/**
	 * 
	 */
	public SceneController(SceneGameState sceneGameState) {
		// set up the basic movement controls
		firstPersonHandler = new FirstPersonHandler(DisplaySystem
				.getDisplaySystem().getRenderer().getCamera(), moveSpeed,
				turnSpeed);
		firstPersonHandler.setButtonPressRequired(true);
		firstPersonHandler.getMouseLookHandler().getMouseLook()
				.setMouseButtonForRequired(2);
		firstPersonHandler.getMouseLookHandler().getMouseLook().setSpeed(.5f);
		
		MouseZoomAction mouseZoom = new MouseZoomAction(camera, moveSpeed);
		firstPersonHandler.addAction(mouseZoom);
		
		adjustPerLocale();
		camera.getDirection();

		controlManager = new GameControlManager();

		for (StandardAction action : StandardAction.values()) {
			controlManager.addControl(action.name());
		}

		// bind keyboard commands
		bindKey(StandardAction.EXIT, KEY_ESCAPE);
	}

	private void adjustPerLocale() {
		KeyBindingManager keyBindingManager = KeyBindingManager
				.getKeyBindingManager();
		Locale locale = Locale.getDefault();
		if (locale.equals(Locale.FRANCE) || locale.equals(Locale.FRENCH)) {
			keyBindingManager.set("forward", KeyInput.KEY_Z);
			keyBindingManager.set("backward", KeyInput.KEY_S);
			keyBindingManager.set("strafeLeft", KeyInput.KEY_Q);
			keyBindingManager.set("strafeRight", KeyInput.KEY_D);
			keyBindingManager.set("elevateUp", KeyInput.KEY_A);
			keyBindingManager.set("elevateDown", KeyInput.KEY_W);
			keyBindingManager.set("elevateDown", KeyInput.KEY_E);
		} else if (locale.equals(Locale.GERMANY)
				|| locale.equals(Locale.GERMAN)) {
			keyBindingManager.set("forward", KeyInput.KEY_E);
			keyBindingManager.set("forward", KeyInput.KEY_W);
			keyBindingManager.set("backward", KeyInput.KEY_S);
			keyBindingManager.set("strafeLeft", KeyInput.KEY_A);
			keyBindingManager.set("strafeRight", KeyInput.KEY_D);
			keyBindingManager.set("elevateUp", KeyInput.KEY_Q);
			keyBindingManager.set("elevateDown", KeyInput.KEY_Y);
		}
	}

	
	private void exitAction() {
		logger.info("Exit Action");
		if (closeWindow == null) {
			logger.info("Creating exit window");

			/*closeWindow = FengUtils.createTwoOptionWindow("Betaville",
					"Are you sure you want to exit?", "yes", "no",
					new IButtonPressedListener() {
						public void buttonPressed(Object source,
								ButtonPressedEvent e) {
							logger.info("Exit comment confirmed");
							SafeShutdown.doSafeShutdown();
						}
					}, null, true, true);
					*/
			closeWindow = FengUtils.createTwoOptionWindow("Betaville", "Are you sure you want to exit?", "Yes", "No", new IButtonPressedListener() {
				public void buttonPressed(Object source, ButtonPressedEvent e) {
					logger.info("Exit comment confirmed");
					SafeShutdown.doSafeShutdown();
				}
			}, null, true, true);
		}

		if (!closeWindow.isInWidgetTree())
			GUIGameState.getInstance().getDisp().addWidget(closeWindow);
	}

	/**
	 * Method <bindKey> - Binds the key to a StandardAction
	 * 
	 * @param action
	 *            Standard action
	 * @param keys
	 *            Key associated
	 */
	private void bindKey(StandardAction action, int... keys) {
		for (int key : keys) {
			controlManager.getControl(action.name()).addBinding(
					new KeyboardBinding(key));
		}
	}

	/**
	 * Method <value> - Return the value of the control associated to the action
	 * (parameter)
	 * 
	 * @param action
	 *            Standard action
	 * @return Value
	 */
	private float value(StandardAction action) {
		// Check if camera turned since last update
		if (cameraDirX != camera.getDirection().x) {
			GUIGameState
					.getInstance()
					.getTopSelectionWindow()
					.updateCompass(
							FastMath.atan2(camera.getDirection().z,
									camera.getDirection().x));
		}

		cameraDirX = camera.getDirection().x;

		return controlManager.getControl(action.name()).getValue();
	}
	
	/**
	 * @return true if elevate key pressed
	 */
	public boolean getElevate(){
		if(KeyInput.get().isKeyDown(KeyInput.KEY_Q)){
			return true;
		}
		return false;
	}

	/** 
	 * @return true if any key for forward, backward, up and down is pressed
	 */
	
	public boolean getMoveKey(){
				
		if(KeyInput.get().isKeyDown(KeyInput.KEY_W)) {
			return true;
		}else if (KeyInput.get().isKeyDown(KeyInput.KEY_Q)) {
			return true;
		}else if (KeyInput.get().isKeyDown(KeyInput.KEY_Z)) {
			return true;
		}else if (KeyInput.get().isKeyDown(KeyInput.KEY_Y)) {
			return true;
		}else if (KeyInput.get().isKeyDown(KeyInput.KEY_S)) {
			return true;
		}
		
		return false;
	}

	
	/**
	 * @return the moveSpeed
	 */
	public float getMoveSpeed() {
		return moveSpeed;
	}

	/**
	 * @param moveSpeed
	 *            the moveSpeed to set
	 */
	public void setMoveSpeed(float moveSpeed) {
		firstPersonHandler.getKeyboardLookHandler().setMoveSpeed(moveSpeed);
		this.moveSpeed = moveSpeed;
	}

	/**
	 * @return the turnSpeed
	 */
	public float getTurnSpeed() {
		return turnSpeed;
	}

	/**
	 * @param turnSpeed
	 *            the turnSpeed to set
	 */
	public void setTurnSpeed(float turnSpeed) {
		this.turnSpeed = turnSpeed;
	}

	@Override
	public void update(float time) {
		// firstPersonHandler.getKeyboardLookHandler().setMoveSpeed(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation().getY());
		firstPersonHandler.update(time);
		if (DisplaySystem.getDisplaySystem().isClosing()
				|| value(StandardAction.EXIT) > 0) {
			exitAction();
		}
	}
}