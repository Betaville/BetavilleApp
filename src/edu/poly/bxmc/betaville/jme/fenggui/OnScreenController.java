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

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.event.Event;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.layout.StaticLayout;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

import edu.poly.bxmc.betaville.jme.controllers.SceneController;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * An on screen controller (OSC) for moving the camera.
 * @author Skye Book
 */
public class OnScreenController extends Container {
	private static final Logger logger = Logger.getLogger(OnScreenController.class);

	private Vector3f temp = new Vector3f();
	private Matrix3f incr = new Matrix3f();

	private static final String textureBasePath = "data/uiAssets/osc/";

	public OnScreenController() {
		super();
		this.setSize(161, 161);
		this.setLayoutManager(new StaticLayout());

		try {
			createBackgroundElement();
			createMovementElements();
			createRotationElements();
			createElevationElements();
		} catch (IOException e) {
			logger.error("Could not locate find On Screen Controller textures", e);
		}

	}

	private Label createLabelWithTexture(String texturePath) throws IOException{
		Label label = FengGUI.createWidget(Label.class);
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
		forward.setX(FengUtils.midWidth(this, forward));
		forward.setY(getHeight()-forward.getHeight());
		forward.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				moveCameraForwardBackward(true);
			}
		});

		Label backward = createLabelWithTexture(textureBasePath+"backward.png");
		addWidget(backward);
		backward.setX(FengUtils.midWidth(this, backward));
		backward.setY(0);
		backward.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				moveCameraForwardBackward(false);
			}
		});

		Label strafeLeft = createLabelWithTexture(textureBasePath+"strafe_left.png");
		addWidget(strafeLeft);
		strafeLeft.setX(0);
		strafeLeft.setY(FengUtils.midHeight(this, strafeLeft));
		strafeLeft.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				strafeCameraLeftRight(true);
			}
		});

		Label strafeRight = createLabelWithTexture(textureBasePath+"strafe_right.png");
		addWidget(strafeRight);
		strafeRight.setX(getWidth()-strafeRight.getWidth());
		strafeRight.setY(FengUtils.midHeight(this, strafeLeft));
		strafeRight.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				strafeCameraLeftRight(false);
			}
		});
	}

	private void createRotationElements() throws IOException{
		Label rotateDown = createLabelWithTexture(textureBasePath+"rotate_down.png");
		addWidget(rotateDown);
		rotateDown.setX(FengUtils.midWidth(this, rotateDown));
		rotateDown.setY(FengUtils.midHeight(this, rotateDown)+rotateDown.getHeight());
		rotateDown.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				rotateCameraUpDown(false);
			}
		});

		Label rotateLeft = createLabelWithTexture(textureBasePath+"rotate_left.png");
		addWidget(rotateLeft);
		rotateLeft.setX(FengUtils.midWidth(this, rotateLeft)-rotateLeft.getWidth());
		rotateLeft.setY(FengUtils.midHeight(this, rotateLeft));
		rotateLeft.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				rotateCameraLeftRight(true);
			}
		});

		Label rotateUp = createLabelWithTexture(textureBasePath+"rotate_up.png");
		addWidget(rotateUp);
		rotateUp.setX(FengUtils.midWidth(this, rotateUp));
		rotateUp.setY(FengUtils.midHeight(this, rotateUp)-rotateUp.getHeight());
		rotateUp.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				rotateCameraUpDown(true);
			}
		});

		Label rotateRight = createLabelWithTexture(textureBasePath+"rotate_right.png");
		addWidget(rotateUp);
		rotateRight.setX(FengUtils.midWidth(this, rotateRight)+rotateRight.getWidth());
		rotateRight.setY(FengUtils.midHeight(this, rotateRight));
		rotateLeft.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				rotateCameraLeftRight(false);
			}
		});
	}

	private void createElevationElements() throws IOException{
		Label elevateUp = createLabelWithTexture(textureBasePath+"elevate_up.png");
		addWidget(elevateUp);
		elevateUp.setX(FengUtils.midWidth(this, elevateUp));
		elevateUp.setY(FengUtils.midHeight(this, elevateUp) + (elevateUp.getHeight()/2));
		elevateUp.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				moveCameraUpDown(true);
			}
		});

		Label elevateDown = createLabelWithTexture(textureBasePath+"elevate_down.png");
		addWidget(elevateDown);
		elevateDown.setX(FengUtils.midWidth(this, elevateDown));
		elevateDown.setY(FengUtils.midHeight(this, elevateDown) - (elevateDown.getHeight()/2));
		elevateDown.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				moveCameraUpDown(false);
			}
		});
	}

	private void moveCameraForwardBackward(boolean forward){
		Camera camera = SceneGameState.getInstance().getCamera();
		SceneController sc = SceneGameState.getInstance().getSceneController();
		Vector3f loc = camera.getLocation();
		if ( !camera.isParallelProjection() ) {
			loc.addLocal(camera.getDirection().mult(sc.getMoveSpeed()*(forward?1:-1), temp));
		} else {
			// move up instead of forward if in parallel mode
			loc.addLocal(camera.getUp().mult(sc.getMoveSpeed()*(forward?1:-1), temp));
		}
		camera.update();
	}

	private void strafeCameraLeftRight(boolean left){
		Camera camera = SceneGameState.getInstance().getCamera();
		SceneController sc = SceneGameState.getInstance().getSceneController();
		Vector3f loc = camera.getLocation();
		if(left){
			loc.addLocal(camera.getLeft().mult(sc.getMoveSpeed(), temp));
		}
		else{
			loc.subtractLocal(camera.getLeft().mult(sc.getMoveSpeed(), temp));
		}
		camera.update();
	}

	private void rotateCameraUpDown(boolean up){
		Camera camera = SceneGameState.getInstance().getCamera();
		SceneController sc = SceneGameState.getInstance().getSceneController();
		incr.fromAngleNormalAxis(sc.getTurnSpeed()*(up?-1:1), camera.getLeft());
		incr.mult(camera.getLeft(), camera.getLeft());
		incr.mult(camera.getDirection(), camera.getDirection());
		incr.mult(camera.getUp(), camera.getUp());
		camera.normalize();
		camera.update();
	}

	private void rotateCameraLeftRight(boolean left){
		Camera camera = SceneGameState.getInstance().getCamera();
		SceneController sc = SceneGameState.getInstance().getSceneController();
		incr.fromAngleNormalAxis(sc.getTurnSpeed()*(left?1:-1), camera.getUp());
		incr.mult(camera.getUp(), camera.getUp());
		incr.mult(camera.getLeft(), camera.getLeft());
		incr.mult(camera.getDirection(), camera.getDirection());
		camera.normalize();
		camera.update();
	}

	private void moveCameraUpDown(boolean up){
		Camera cam = SceneGameState.getInstance().getCamera();
		SceneController sc = SceneGameState.getInstance().getSceneController();
		Vector3f location = SceneGameState.getInstance().getCamera().getLocation();
		location.y+=Scale.fromMeter(sc.getMoveSpeed()*(up?1:-1));
		cam.update();
	}
}