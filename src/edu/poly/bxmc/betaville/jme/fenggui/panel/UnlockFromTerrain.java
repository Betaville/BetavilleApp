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
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import com.jme.scene.Node;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ITerrainSelectionListener;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class UnlockFromTerrain extends PanelAction {
	private static final Logger logger = Logger.getLogger(UnlockFromTerrain.class);

	/**
	 * 
	 */
	public UnlockFromTerrain() {
		super("Unlock From Terrain", "Unlocks an object from the terrain",
				"Unlock From Terrain", AvailabilityRule.IGNORE,
				UserType.BASE_COMMITTER,null);
		
		// turned off by default
		getButton().setEnabled(false);

		SceneScape.addTerrainSelectionListener(new ITerrainSelectionListener() {

			public void terrainSelectionCleared() {
				getButton().setEnabled(false);
			}

			public void terrainSelected(Node selectedTerrain) {
				getButton().setEnabled(true);
			}
		});

		getButton().addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				try {
					//logger.info("Looking for ID from design named: " + SceneScape.getSelectedTerrain().getName());
					int itemToLock = SettingsPreferences.getCity().findDesignByFullIdentifier(SceneScape.getSelectedTerrain().getName()).getID();
					String name = SettingsPreferences.getCity().findDesignByID(itemToLock).getName();
					name = new String(name.substring(0, name.indexOf("$TERRAIN")));
					if(!NetPool.getPool().getSecureConnection().changeDesignName(itemToLock, name)){
						FengUtils.showNewDismissableWindow("Betaville", "You don't have permissions to do this!", Labels.get("Generic.ok"), true);
					}
					else{
						FengUtils.showNewDismissableWindow("Betaville", "Success!", Labels.get("Generic.ok"), true);
						SceneGameState.getInstance().getDesignNode().attachChild(SceneScape.getSelectedTerrain());
						SceneScape.clearTerrainSelection();
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

}
