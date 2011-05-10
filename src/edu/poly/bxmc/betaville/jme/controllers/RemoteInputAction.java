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

import java.io.IOException;
import java.nio.charset.CharacterCodingException;

import org.apache.log4j.Logger;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * @author Skye Book
 *
 */
public class RemoteInputAction extends InputAction {
	private static final Logger logger = Logger.getLogger(RemoteInputAction.class);

	private Vector3f tempVa = new Vector3f();

	private float forwardTemp = 0;
	private float leftTemp = 0;
	private float rotateTemp = 0;
	private float rotateUpDownTemp=0;
	private Camera camera;

	private Matrix3f incr = new Matrix3f();

	private boolean enabled = false;

	/**
	 * @throws IOException 
	 * @throws CharacterCodingException 
	 * 
	 */
	public RemoteInputAction(Camera camera){
		this.camera=camera;
	}

	/* (non-Javadoc)
	 * @see com.jme.input.action.InputActionInterface#performAction(com.jme.input.action.InputActionEvent)
	 */
	public void performAction(InputActionEvent evt) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return enabled true if the action is turned on, false if it is turned off
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled true to turn the action on, false to turn it off
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void update(float time){
		// don't do anything if this action is not enabled
		if(!enabled) return;

		camera.getLocation().addLocal(camera.getDirection().mult(forwardTemp, tempVa));
		camera.getLocation().addLocal(camera.getLeft().mult(leftTemp, tempVa));

		/*
		if(rotateTemp!=0){
			incr.fromAngleNormalAxis(rotateTemp, camera.getUp());
			incr.mult(camera.getUp(), camera.getUp());
			incr.mult(camera.getLeft(), camera.getLeft());
			incr.mult(camera.getDirection(), camera.getDirection());
			camera.normalize();
			camera.update();
		}
		
		if(rotateUpDownTemp!=0){
			incr.fromAngleNormalAxis(rotateUpDownTemp, camera.getLeft());
	        incr.mult(camera.getLeft(), camera.getLeft());
	        incr.mult(camera.getDirection(), camera.getDirection());
	        incr.mult(camera.getUp(), camera.getUp());
			camera.normalize();
			camera.update();
		}
		*/
		
		if(rotateUpDownTemp!=0 || rotateTemp!=0){
			incr.fromAngleNormalAxis(rotateUpDownTemp, camera.getLeft());
			incr.fromAngleNormalAxis(rotateTemp, camera.getUp());
			incr.mult(camera.getLeft(), camera.getLeft());
			incr.mult(camera.getDirection(), camera.getDirection());
			incr.mult(camera.getUp(), camera.getUp());
			camera.normalize();
			camera.update();
		}

		clearTemporaries();
		
	}

	private void clearTemporaries(){
		forwardTemp=0;
		leftTemp=0;
		rotateTemp=0;
		rotateUpDownTemp=0;
	}

	public void moveForward(float distance){
		if(!enabled) return;
		forwardTemp+=distance;
	}

	public void moveBackward(float distance){
		if(!enabled) return;
		forwardTemp-=distance;
	}

	public void strafeLeft(float distance){
		if(!enabled) return;
		leftTemp+=distance;
	}

	public void strafeRight(float distance){
		if(!enabled) return;
		leftTemp-=distance;
	}

	public void rotateLeft(float distance){
		if(!enabled) return;
		rotateTemp+=distance;
	}

	public void rotateRight(float distance){
		if(!enabled) return;
		rotateTemp-=distance;
	}
	
	public void rotateUp(float distance){
		if(!enabled) return;
		rotateUpDownTemp+=distance;
	}
	
	public void rotateDown(float distance){
		if(!enabled) return;
		rotateUpDownTemp-=distance;
	}
}
