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

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import com.jme.scene.Node;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ITerrainSelectionListener;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class TerrainDeleterAction extends PanelAction {
	private static Logger logger = Logger.getLogger(TerrainDeleterAction.class);


	/**
	 * @param name
	 * @param description
	 * @param buttonTitle
	 * @param ruleToSet
	 * @param minimumUserLevel
	 * @param listener
	 */
	public TerrainDeleterAction() {
		super(Labels.get(TerrainDeleterAction.class, "title"), "Deletes the selected terrain", Labels.get(TerrainDeleterAction.class, "title"), AvailabilityRule.IGNORE, UserType.MODERATOR,
				new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				Window window = FengUtils.createTwoOptionWindow(Labels.generic("delete"), "Are you sure that you would like to delete this terrain?",
						Labels.generic("no"), Labels.generic("yes"),
						new IButtonPressedListener() {
					public void buttonPressed(Object source, ButtonPressedEvent e) {

						logger.info("b1 pressed");
					}
				},
				new IButtonPressedListener() {
					public void buttonPressed(Object source, ButtonPressedEvent e) {
						int designID = Integer.parseInt(new String(SceneScape.getSelectedTerrain().getName().substring(1)));
						try {
							int removed = NetPool.getPool().getSecureConnection().removeDesign(designID);

							if(removed==0){
								SceneGameState.getInstance().removeTerrainFromDisplay(designID);
								logger.info("Terrain successfully removed");
							}
							else if(removed==-3){
								logger.warn("You are not authorized to remove terrain!");
							}
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				},
				true, true
						);

				window.setXY(FengUtils.midWidth(GUIGameState.getInstance().getDisp(), window),FengUtils.midHeight(GUIGameState.getInstance().getDisp(), window));
				GUIGameState.getInstance().getDisp().addWidget(window);
			}
		});

		button.setEnabled(false);

		SceneScape.addTerrainSelectionListener(new ITerrainSelectionListener() {

			public void terrainSelectionCleared() {
				button.setEnabled(false);
			}

			public void terrainSelected(Node selectedTerrain) {
				button.setEnabled(true);
			}
		});
	}

}
