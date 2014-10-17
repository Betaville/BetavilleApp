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

import com.jme.scene.state.FogState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.util.GameTaskQueueManager;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * @author Skye Book
 *
 */
public class TurnFogOnOffAction extends PanelAction {

	private FogState fogState;

	private static final String disableText = Labels.get(TurnFogOnOffAction.class, "disable");
	private static final String enableText = Labels.get(TurnFogOnOffAction.class, "enable");

	/**
	 * @param name
	 * @param description
	 * @param buttonTitle
	 * @param ruleToSet
	 * @param minimumUserLevel
	 * @param listener
	 */
	public TurnFogOnOffAction() {
		super(TurnFogOnOffAction.class.getSimpleName(), "Turn the scene's Fog on or off", disableText,
				AvailabilityRule.ALWAYS, UserType.MEMBER, null);

		getButton().addButtonPressedListener(new IButtonPressedListener() {

			@Override
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				GameTaskQueueManager.getManager().update(new Callable<Object>() {

					@Override
					public Object call() throws Exception {

						if(fogState==null){
							fogState = (FogState) SceneGameState.getInstance().getRootNode().getRenderState(StateType.Fog);
							SceneGameState.getInstance().getRootNode().clearRenderState(StateType.Fog);
							SceneGameState.getInstance().getRootNode().updateRenderState();
							getButton().setText(enableText);
						}
						else{
							SceneGameState.getInstance().getRootNode().setRenderState(fogState);
							SceneGameState.getInstance().getRootNode().updateRenderState();
							fogState = null;
							getButton().setText(disableText);
						}

						return null;
					}
				});

			}
		});
	}

}
