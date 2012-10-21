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
import org.fenggui.layout.GridLayout;

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
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * An on screen controller (OSC) for moving the camera.
 * @author Johannes Lier
 */
public class OnScreenControllerPanel extends Container implements IOnScreenController {
	private static final Logger logger = Logger.getLogger(OnScreenControllerPanel.class);

	private static final int CONTROLLER_WIDTH = 82;
	private static final int CONTROLLER_HEIGHT = 204;
	
	private static final String textureBasePath = "data/uiAssets/osc/panel/";
	
	private enum KeyAction {FORWARD, BACK, LEFT, RIGHT, TURN_LEFT, TURN_RIGHT, LOOK_UP, LOOK_DOWN, UP, DOWN};
	
	private Map<KeyAction, KeyInputAction> actionsMap;
	private Map<KeyAction, KeyInputAction> activeActionsMap;
	
	InputActionEvent event = new InputActionEvent();

	public OnScreenControllerPanel() {
		super();
		this.setSize(CONTROLLER_WIDTH, CONTROLLER_HEIGHT);
		this.setLayoutManager(new GridLayout(5, 2));
		
		Camera camera = SceneGameState.getInstance().getCamera();
        SceneController sceneController = SceneGameState.getInstance().getSceneController();
        // Use half the scene's move speed per Carl's request
        float moveSpeed = sceneController.getMoveSpeed()/2f;
        
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
			createButtons();
		} catch (IOException e) {
			logger.error("Could not locate find On Screen Controller textures", e);
		}
		
		sceneController.setOnScreenController(this);
	}


	private void createButtons() throws IOException{
		createButton("up.png", "up_active.png", KeyAction.UP);
		createButton("down.png", "down_active.png", KeyAction.DOWN);
		
		createButton("left.png", "left_active.png", KeyAction.LEFT);
		createButton("right.png", "right_active.png", KeyAction.RIGHT);
		
		createButton("forward.png", "forward_active.png", KeyAction.FORWARD);
		createButton("backward.png", "backward_active.png", KeyAction.BACK);
		
		createButton("turn_left.png", "turn_left_active.png", KeyAction.TURN_LEFT);
		createButton("turn_right.png", "turn_right_active.png", KeyAction.TURN_RIGHT);
		
		createButton("look_up.png", "look_up_active.png", KeyAction.LOOK_UP);
		createButton("look_down.png", "look_down_active.png", KeyAction.LOOK_DOWN);

	}
	
	private void createButton(String texturePath, String activeTexturePath, final KeyAction keyAction ) throws IOException{
		final Pixmap texture = new Pixmap(Binding.getInstance().getTexture(textureBasePath + texturePath));
		final Pixmap textureActive = new Pixmap(Binding.getInstance().getTexture(textureBasePath + activeTexturePath));
		
		Label label = new Label();
		label.setPixmap(texture);
		
		addWidget(label);
		
		label.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			@Override
			public void processEvent(Object source, Event event) {
					setMovementDirection(event, keyAction, (Label)source, texture, textureActive);
			}
		});
	}
	
	private void setMovementDirection(Event event, KeyAction key, Label label, Pixmap texture, Pixmap textureActive) {
		if(event instanceof MousePressedEvent){
			activeActionsMap.put(key, actionsMap.get(key));
			label.setPixmap(textureActive);
		}
		else if (event instanceof MouseReleasedEvent || event instanceof MouseExitedEvent ) {
			activeActionsMap.remove(key);
			label.setPixmap(texture);
		}
	}
	
	@Override
	public void update(float time) {
		event.setTime(time);
		
		for (KeyInputAction keyInputAction : activeActionsMap.values()) {
			keyInputAction.performAction(event);
		}
	}
}