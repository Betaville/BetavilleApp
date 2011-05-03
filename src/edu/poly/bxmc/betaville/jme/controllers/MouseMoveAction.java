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

import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * {@link MouseInputAction} for panning the camera in a
 * manner similar to DCC tools like Maya, Blender, etc
 * @author Skye Book
 *
 */
public class MouseMoveAction extends MouseInputAction {
	
	private static final Vector3f tempVa = new Vector3f();
	private int xDelta = 0;
	private int yDelta = 0;
	private float sensitivity = 0.001f;
	private boolean invert = true;
	
	private Camera camera;
	
	private boolean requireButtonPressToDrag = false;
	private int requiredButton = LEFT_MOUSE_BUTTON;
	
	private boolean keysRequired = false;
	private int[] acceptedKeys = {KeyInput.KEY_LMENU, KeyInput.KEY_RMENU};
	
	public static final int LEFT_MOUSE_BUTTON = 0;
	public static final int RIGHT_MOUSE_BUTTON = 1;

	/**
	 * 
	 * @param camera
	 * @param speed
	 * @param requireButtonPressToDrag
	 * @param keysRequired True means that one of the default keys will be
	 * required for the scene to drag.  The defaults are {@link KeyInput#KEY_LMENU}
	 * and {@link KeyInput#KEY_RMENU}
	 */
	public MouseMoveAction(Camera camera, float speed, boolean requireButtonPressToDrag, boolean keysRequired) {
		this.camera=camera;
		this.speed=speed;
		this.requireButtonPressToDrag=requireButtonPressToDrag;
		this.keysRequired=keysRequired;
	}
	
	/**
	 * @param camera
	 * @param speed
	 * @param requireButtonPressToDrag
	 * @param acceptedKeys The keys that can be used to activate this control.  Passing in no arguments here
	 * will turn off the setting for required key presses
	 * @see KeyInput
	 */
	public MouseMoveAction(Camera camera, float speed, boolean requireButtonPressToDrag, int... acceptedKeys) {
		this.camera=camera;
		this.speed=speed;
		this.requireButtonPressToDrag=requireButtonPressToDrag;
		if(acceptedKeys.length>0)keysRequired=true;
		this.acceptedKeys=acceptedKeys;
	}

	/* (non-Javadoc)
	 * @see com.jme.input.action.InputActionInterface#performAction(com.jme.input.action.InputActionEvent)
	 */
	public void performAction(InputActionEvent evt) {
		if(requireButtonPressToDrag && !MouseInput.get().isButtonDown(requiredButton)) return;
		
		if(keysRequired){
			boolean keyFound=false;
			for(int key : acceptedKeys){
				if(KeyInput.get().isKeyDown(key)){
					keyFound=true;
					break;
				}
			}
			if(!keyFound){
				return;
			}
		}
		
		xDelta = MouseInput.get().getXDelta();
		yDelta = MouseInput.get().getYDelta();
		if(xDelta==0 && yDelta==0) return;

		Vector3f loc = camera.getLocation();

		loc.addLocal(camera.getLeft().mult(speed *(xDelta*(invert ? 1 : -1))*sensitivity, tempVa));
		loc.addLocal(camera.getUp().mult(speed *(yDelta*(invert ? -1 : 1))*sensitivity, tempVa));

		camera.update();
	}

	/**
	 * @return The sensitivity of the mouse or trackpad
	 * wheel/scroller
	 */
	public float getSensitivity() {
		return sensitivity;
	}

	/**
	 * @param sensitivity The sensitivity of the trackpad or
	 * mouse wheel wheel/scroll.  Clamped between 0 and 1
	 */
	public void setSensitivity(float sensitivity) {
		if(sensitivity>1)this.sensitivity=1;
		else if(sensitivity<0)this.sensitivity=0;
		else this.sensitivity = sensitivity;
	}

	/**
	 * @return True if the scene dragging is set to inverted, false if not
	 */
	public boolean isInverted() {
		return invert;
	}

	/**
	 * @param invert Setting true will have the scene pan left when the mouse
	 * is dragged right and up when the mouse is dragged down.
	 */
	public void setInverted(boolean invert) {
		this.invert = invert;
	}
}
