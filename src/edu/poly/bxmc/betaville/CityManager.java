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
package edu.poly.bxmc.betaville;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.flags.DesktopFlagPositionStrategy;
import edu.poly.bxmc.betaville.flags.FlagProducer;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.net.NetModelLoader;
import edu.poly.bxmc.betaville.net.NetModelLoader.LookupRoutine;
import edu.poly.bxmc.betaville.updater.BaseUpdater;
import edu.poly.bxmc.betaville.updater.BetavilleTask;

/**
 * Allows for the removal and addition of cities to the scene.
 * @author Skye Book
 *
 */
public class CityManager {
	private static final Logger logger = Logger.getLogger(CityManager.class);

	public static void swapCities(final int oldCity, final int newCity, final ILocation cityPoint){
		// make sure there is no update in process
		for(BetavilleTask task : BetavilleNoCanvas.getUpdater().getTasks()){
			if(task.getUpdater() instanceof BaseUpdater){
				BetavilleNoCanvas.getUpdater().removeTask(task);
				logger.info("BaseUpdater removed");
				break;
			}
		}

		final AtomicBoolean newCityIsLoaded = new AtomicBoolean(false);
		final AtomicBoolean oldCityIsUnloaded = new AtomicBoolean(false);

		SettingsPreferences.getThreadPool().execute(new Runnable() {

			public void run() {
				deconstructCurrentCity(oldCity);
				oldCityIsUnloaded.set(true);
				constructCity(newCity, cityPoint);
				newCityIsLoaded.set(true);
			}
		});

		if(!newCityIsLoaded.get() || !oldCityIsUnloaded.get()){
			try {
				Thread.currentThread().sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			SceneScape.setCurrentCity(newCity);
		} catch (Exception e) {
			logger.error("City " + newCity + " could not be found", e);
		}
	}

	public static void deconstructCurrentCity(int cityID){
		SceneGameState.getInstance().getDesignNode().detachAllChildren();
		SceneGameState.getInstance().getTerrainNode().detachAllChildren();
	}

	public static void constructCity(int cityID, ILocation cityPoint){
		NetModelLoader.loadCityTerrain(cityID);
		NetModelLoader.loadCity(LookupRoutine.ALL_IN_CITY, NetModelLoader.NO_LIMIT, cityID);
		
		// load proposals
		FlagProducer testFlagger = new FlagProducer(cityPoint.getUTM(), new DesktopFlagPositionStrategy());
		testFlagger.getProposals(30000);
		testFlagger.placeFlags();
	}

}
