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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.event.Event;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.event.mouse.MousePressedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.layout.StaticLayout;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyBackwardAction;
import com.jme.input.action.KeyForwardAction;
import com.jme.input.action.KeyInputAction;
import com.jme.input.action.KeyLookDownAction;
import com.jme.input.action.KeyLookUpAction;
import com.jme.input.action.KeyRotateLeftAction;
import com.jme.input.action.KeyRotateRightAction;
import com.jme.input.action.KeyStrafeDownAction;
import com.jme.input.action.KeyStrafeLeftAction;
import com.jme.input.action.KeyStrafeRightAction;
import com.jme.input.action.KeyStrafeUpAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

import edu.poly.bxmc.betaville.jme.controllers.SceneController;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * An on screen controller (OSC) for moving the camera.
 * @author Skye Book
 * @author Johannes Lier
 */
public class OnScreenController extends Container {
	private static final Logger logger = Logger.getLogger(OnScreenController.class);

	private static final int CONTROLLER_WIDTH = 246;
	private static final int CONTROLLER_HEIGHT = 246;
	
	private static final String textureBasePath = "data/uiAssets/osc/";
	
	private enum KeyAction {FORWARD, BACK, LEFT, RIGHT, TURN_LEFT, TURN_RIGHT, LOOK_UP, LOOK_DOWN, UP, DOWN};
	
	private Map<KeyAction, KeyInputAction> actionsMap;
	private Map<KeyAction, KeyInputAction> activeActionsMap;
	
	InputActionEvent event = new InputActionEvent();

	public OnScreenController() {
		super();
		this.setSize(CONTROLLER_WIDTH, CONTROLLER_HEIGHT);
		this.setLayoutManager(new StaticLayout());
		
		Camera camera = SceneGameState.getInstance().getCamera();
        SceneController sceneController = SceneGameState.getInstance().getSceneController();
        float moveSpeed = sceneController.getMoveSpeed();
        
        activeActionsMap = new HashMap<KeyAction, KeyInputAction>();
        actionsMap = new HashMap<KeyAction, KeyInputAction>();
        
		actionsMap.put(KeyAction.FORWARD, new KeyForwardAction(camera, moveSpeed));
		actionsMap.put(KeyAction.BACK, new KeyBackwardAction(camera, moveSpeed));
		actionsMap.put(KeyAction.LEFT, new KeyStrafeLeftAction(camera, moveSpeed));
		actionsMap.put(KeyAction.RIGHT, new KeyStrafeRightAction(camera, moveSpeed));
		KeyRotateLeftAction ratateLeftAction = new KeyRotateLeftAction(camera, moveSpeed);
		ratateLeftAction.setLockAxis(new Vector3f(0, 1, 0));
		actionsMap.put(KeyAction.TURN_LEFT, ratateLeftAction);
		KeyRotateRightAction ratateRightAction = new KeyRotateRightAction(camera, moveSpeed);
		ratateRightAction.setLockAxis(new Vector3f(0, 1, 0));
		actionsMap.put(KeyAction.TURN_RIGHT, ratateRightAction);
		actionsMap.put(KeyAction.DOWN, new KeyStrafeDownAction(camera, moveSpeed));
		actionsMap.put(KeyAction.UP, new KeyStrafeUpAction(camera, moveSpeed));
		actionsMap.put(KeyAction.LOOK_UP, new KeyLookUpAction(camera, moveSpeed));
		actionsMap.put(KeyAction.LOOK_DOWN, new KeyLookDownAction(camera, moveSpeed));
		
		try {
			createBackgroundElement();
			createMovementElements();
			createRotationElements();
			createElevationElements();
		} catch (IOException e) {
			logger.error("Could not locate find On Screen Controller textures", e);
		}
		
		sceneController.setOnScreenController(this);
	}

	private Label createLabelWithTexture(String texturePath) throws IOException{
		Label label = new Label();
		label.setPixmap( new Pixmap(Binding.getInstance().getTexture(texturePath)));
		return label;
	}

	private void createBackgroundElement() throws IOException{
		Label bg = createLabelWithTexture(textureBasePath+"background.png");
		addWidget(bg);
	}

	private void createMovementElements() throws IOException{
		Label forward = createLabelWithTexture(textureBasePath+"forward.png");
		addWidget(forward);
		forward.setX((this.getWidth() / 4) - (forward.getWidth() / 2) );
		forward.setY( this.getHeight() / 4 - forward.getHeight() / 2 );
		forward.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.FORWARD);
			}
		});

		Label backward = createLabelWithTexture(textureBasePath+"backward.png");
		addWidget(backward);
		backward.setX((this.getWidth() / 4) * 3 - (backward.getWidth() / 2) );
		backward.setY( this.getHeight() / 4 - backward.getHeight() / 2 );
		backward.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.BACK);
			}
		});

		Label strafeLeft = createLabelWithTexture(textureBasePath+"left.png");
		addWidget(strafeLeft);
		strafeLeft.setX(0);
		strafeLeft.setY(FengUtils.midHeight(this, strafeLeft));
		strafeLeft.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.LEFT);
			}
		});

		Label strafeRight = createLabelWithTexture(textureBasePath+"right.png");
		addWidget(strafeRight);
		strafeRight.setX(getWidth()-strafeRight.getWidth());
		strafeRight.setY(FengUtils.midHeight(this, strafeLeft));
		strafeRight.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.RIGHT);
			}
		});
	}
	
	private void createRotationElements() throws IOException{
		Label rotateUp = createLabelWithTexture(textureBasePath+"rotate_up.png");
		addWidget(rotateUp);
		rotateUp.setX(FengUtils.midWidth(this, rotateUp));
		rotateUp.setY(this.getHeight() / 2);
		rotateUp.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.LOOK_UP);
			}
		});
		
		Label rotateDown = createLabelWithTexture(textureBasePath+"rotate_down.png");
		addWidget(rotateDown);
		rotateDown.setX(FengUtils.midWidth(this, rotateDown));
		rotateDown.setY(this.getHeight() / 2 - rotateDown.getHeight());
		rotateDown.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.LOOK_DOWN);
			}
		});

		Label rotateRight = createLabelWithTexture(textureBasePath+"rotate_right.png");
		addWidget(rotateRight);
		rotateRight.setX(this.getWidth() / 2);
		rotateRight.setY(FengUtils.midHeight(this, rotateRight));
		rotateRight.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.TURN_RIGHT);
			}
		});
		
		Label rotateLeft = createLabelWithTexture(textureBasePath+"rotate_left.png");
		addWidget(rotateLeft);
		rotateLeft.setX(this.getWidth() / 2 - rotateLeft.getWidth());
		rotateLeft.setY(FengUtils.midHeight(this, rotateLeft));
		rotateLeft.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.TURN_LEFT);
			}
		});
	}

	private void createElevationElements() throws IOException{
		Label elevateUp = createLabelWithTexture(textureBasePath+"up.png");
		addWidget(elevateUp);
		elevateUp.setX(FengUtils.midWidth(this, elevateUp));
		elevateUp.setY(getHeight() - elevateUp.getHeight());
		elevateUp.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.UP);
			}
		});

		Label elevateDown = createLabelWithTexture(textureBasePath+"down.png");
		addWidget(elevateDown);
		elevateDown.setX(FengUtils.midWidth(this, elevateDown));
		elevateDown.setY(0);
		elevateDown.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				setMovementDirection(event, KeyAction.DOWN);
			}
		});
	}
	
	private void setMovementDirection(Event event, KeyAction key) {
		if(event instanceof MousePressedEvent){
			activeActionsMap.put(key, actionsMap.get(key));
		}
		else if (event instanceof MouseReleasedEvent || event instanceof MouseExitedEvent ) {
			activeActionsMap.remove(key);
		}
	}
	
	public void update(float time) {
		event.setTime(time);
		
		for (KeyInputAction keyInputAction : activeActionsMap.values()) {
			keyInputAction.performAction(event);
		}
	}
}