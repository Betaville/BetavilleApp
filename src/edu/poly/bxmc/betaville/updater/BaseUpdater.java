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
package edu.poly.bxmc.betaville.updater;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class BaseUpdater extends AbstractUpdater {
	private static Logger logger = Logger.getLogger(BaseUpdater.class);

	private boolean updating=false;

	/**
	 * @param updateInterval
	 */
	public BaseUpdater(long updateInterval) {
		super(updateInterval);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.updater.Updater#isUpdateRequired()
	 */
	public boolean isUpdateRequired() {
		return UpdaterPreferences.baseUpdatesOn();
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.updater.Updater#doUpdate()
	 */
	public void doUpdate(){
		updating=true;

		try {
			long startTime = System.currentTimeMillis();

			HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
			for(Design d : SceneScape.getCity().getDesigns()){
				hashMap.put(d.getID(), d.hashCode());
			}

			List<Design> updated = NetPool.getPool().getConnection().synchronizeData(hashMap);


			logger.info(updated.size()+" designs changed (took "+(System.currentTimeMillis()-startTime)+"ms)");


			startTime = System.currentTimeMillis();
			List<Design> updatedDesigns = NetPool.getPool().getConnection().findBaseDesignsByCity(SceneScape.getCity().getCityID());
			logger.info("full payload city design request ("+updatedDesigns.size()+" objects) took "+(System.currentTimeMillis()-startTime)+"ms");



			/*
		if(SceneGameState.getInstance()==null){
			// If the graphics layer has shutdown or otherwise crashed, shutdown the application.
			ShutdownManager.doSafeShutdown();
			return;
		}
		logger.info("Synchronizing base model");
		try {
			List<Design> updatedDesigns = NetPool.getPool().getConnection().findBaseDesignsByCity(SceneScape.getCity().getCityID());

			for(Design d : updatedDesigns){
				// ignore the currently selected object
				if(SceneScape.getPickedDesign()!=null){
					if(d.getID()==SceneScape.getPickedDesign().getID()) continue;
				}

				Design old = SceneScape.getCity().findDesignByID(d.getID());
				if(SceneGameState.getInstance().getDesignNode().getChild(d.getFullIdentifier())==null
						&& !(d instanceof EmptyDesign)){
					logger.info("Design " + d.getID() + " could not be found in the scene");
					// add to scene
					SceneScape.getCity().addDesign(d);
					SceneGameState.getInstance().addDesignToDisplay(d.getID());
				}
				else{
					if(old==null){
						logger.warn("Design " + d.getID() + " skipped");
						continue;
					}
					if(!old.equals(d)){
						// if the design has changed, SWAP!
						SceneScape.getCity().swapDesigns(old, d);
						if(!old.getFilepath().equals(d.getFilepath())){
							SceneGameState.getInstance().removeDesignFromDisplay(old.getID());
							SceneGameState.getInstance().addDesignToDisplay(d.getID());
						}
					}
				}
			}

			updating=false;
		} catch (IOException e) {
			logger.error("Dearie me!  File not found!", e);
			updating=false;
		} catch (URISyntaxException e) {
			logger.error("URI exception, this really shouldn't happen", e);
			updating=false;
		}
			 */
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updating = false;
	}

	@Override
	public boolean isInUpdate() {
		return updating;
	}

}
