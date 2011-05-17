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

import java.util.ArrayList;

import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * {@link MouseInputAction} for zooming in and out
 * of a scene.
 * @author Skye Book
 *
 */
public class MouseZoomAction extends MouseInputAction {

	private static final Vector3f tempVa = new Vector3f();
	private int mouseDelta = 0;
	private float sensitivity = 0.001f;
	private Camera camera;

	private ArrayList<ZoomLock> zoomLocks;

	/**
	 * 
	 * @param camera
	 * @param speedPerMouseWheelClick how far the camera will move with each mouse click
	 */
	public MouseZoomAction(Camera camera, float speedPerMouseWheelClick) {
		this.camera=camera;
		speed=speedPerMouseWheelClick;
		zoomLocks = new ArrayList<MouseZoomAction.ZoomLock>();
	}

	/* (non-Javadoc)
	 * @see com.jme.input.action.InputActionInterface#performAction(com.jme.input.action.InputActionEvent)
	 */
	public void performAction(InputActionEvent evt) {

		// check if zoom is locked
		for(ZoomLock zl : zoomLocks){
			if(!zl.zoomIsAllowed()) return;
		}

		mouseDelta = MouseInput.get().getWheelDelta();
		if(mouseDelta==0) return;

		Vector3f loc = camera.getLocation();

		if ( !camera.isParallelProjection() ) {
			loc.addLocal(camera.getDirection().mult(speed * mouseDelta*sensitivity, tempVa));
		} else {
			// move up instead of forward if in parallel mode
			loc.addLocal(camera.getUp().mult(speed * mouseDelta*sensitivity, tempVa));
		}
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

	public void addZoomLock(ZoomLock zl){
		zoomLocks.add(zl);
		System.out.println("zoom lock added");
	}

	public void removeZoomLock(ZoomLock zl){
		zoomLocks.remove(zl);
	}

	public void removeAllZoomLocks(){
		zoomLocks.clear();
	}

	public interface ZoomLock{

		/**
		 * @return True if the zoom action should be allowed
		 * to proceed, false if not
		 */
		public boolean zoomIsAllowed();
	}
}
