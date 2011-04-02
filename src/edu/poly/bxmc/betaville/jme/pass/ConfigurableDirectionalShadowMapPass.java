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
package edu.poly.bxmc.betaville.jme.pass;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.DirectionalShadowMapPass;

/**
 * An extension of {@link DirectionalShadowMapPass} with added getter/setter
 * combinations for configuration
 * @author Skye Book
 *
 */
public class ConfigurableDirectionalShadowMapPass extends DirectionalShadowMapPass {
	private static final long serialVersionUID = 1L;

	/**
	 * @param direction
	 */
	public ConfigurableDirectionalShadowMapPass(Vector3f direction) {
		super(direction);
		// Nothing to see here!
	}

	/**
	 * @param direction
	 * @param shadowMapSize
	 */
	public ConfigurableDirectionalShadowMapPass(Vector3f direction,
			int shadowMapSize) {
		super(direction, shadowMapSize);
		// Nothing to see here!
	}
	
	public ColorRGBA getShadowColor(){
		return shadowCol;
	}
	
	public void setShadowColor(ColorRGBA shadowColor){
		shadowCol = shadowColor;
	}
	
	public float getShadowCameraNearPlane(){
		return nearPlane;
	}
	
	public void setShadowCameraNearPlane(float nearPlane){
		this.nearPlane=nearPlane;
	}
	
	public float getShadowCameraFarPlane(){
		return farPlane;
	}
	
	public void setShadowCameraFarPlane(float farPlane){
		this.farPlane=farPlane;
	}
	
	public float getViewDistance(){
		return dis;
	}
	
	public Vector3f getDirection(){
		return direction;
	}
	
	public void setDirection(Vector3f direction){
		this.direction=direction;
	}
	
	protected void doUpdate(float tpf){
		updateShadowCamera();
		super.doUpdate(tpf);
	}
}
