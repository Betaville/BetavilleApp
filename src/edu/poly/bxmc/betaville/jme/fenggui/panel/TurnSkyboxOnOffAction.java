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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.util.concurrent.Callable;

import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import com.jme.scene.Skybox;
import com.jme.util.GameTaskQueueManager;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * @author Skye Book
 *
 */
public class TurnSkyboxOnOffAction extends PanelAction {
	
	private static final String disableText = "Turn off Skybox";
	private static final String enableText = "Turn on Skybox";

	/**
	 * @param name
	 * @param description
	 * @param buttonTitle
	 * @param ruleToSet
	 * @param minimumUserLevel
	 * @param listener
	 */
	public TurnSkyboxOnOffAction() {
		super(TurnSkyboxOnOffAction.class.getSimpleName(), "Turn the skybox on or off", disableText,
				AvailabilityRule.ALWAYS, UserType.MEMBER, null);
		
		getButton().addButtonPressedListener(new IButtonPressedListener() {
			
			@Override
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				GameTaskQueueManager.getManager().update(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						
						Skybox skybox = SceneGameState.getInstance().getSkybox(); 
						if(skybox.getParent()!=null){
							if(skybox.getParent().detachChild(skybox)!=-1){
								SceneGameState.getInstance().getRootNode().updateRenderState();
								getButton().setText(enableText);
							}
						}
						else{
							SceneGameState.getInstance().getRootNode().attachChild(skybox);
							SceneGameState.getInstance().getRootNode().updateRenderState();
							getButton().setText(disableText);
						}
						
						return null;
					}
				});
				
			}
		});
	}

}
