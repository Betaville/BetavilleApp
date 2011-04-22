/** Copyright (c) 2008-2010, Brooklyn eXperimental Media Center
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

import java.util.ConcurrentModificationException;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.fenggui.FengGUI;
import org.fenggui.layout.StaticLayout;

import com.jme.util.GameTaskQueueManager;

import edu.poly.bxmc.betaville.jme.fenggui.tutorial.TutorialWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.proposals.LiveProposalManager;

/**
 * @author Skye Book
 *
 */
public class KioskUpdater extends AbstractUpdater {
	private static final Logger logger = Logger.getLogger(KioskUpdater.class);

	private long sceneTimeoutMilli;

	private AtomicBoolean updating = new AtomicBoolean(false);

	private boolean movedSinceLastUpdate = false;



	/**
	 * @param updateInterval
	 */
	public KioskUpdater(int updateInterval, int sceneTimeout) {
		super(updateInterval);

		// convert to milliseconds
		sceneTimeoutMilli=sceneTimeout*1000;
		
		// we manually set the updater since technically we haven't moved.
		setLastUpdate(System.currentTimeMillis());
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.updater.Updater#isUpdateRequired()
	 */
	public boolean isUpdateRequired() {
		if((System.currentTimeMillis()-SceneGameState.getInstance().getSceneController().getCameraLastMoved())<sceneTimeoutMilli) return false;
		
		if(!movedSinceLastUpdate){
			if(SceneGameState.getInstance().getSceneController().getCameraLastMoved()>getLastUpdate()) movedSinceLastUpdate=true;
			logger.info("last update:\t" + getLastUpdate());
			logger.info("last movem:\t " + SceneGameState.getInstance().getSceneController().getCameraLastMoved());
		}
		
		if(movedSinceLastUpdate){
			return true;
		}
		else{
			logger.info("No need to update");
			// fake that we updated
			setLastUpdate(System.currentTimeMillis());
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.updater.Updater#doUpdate()
	 */
	public void doUpdate() {
		if(!updating.get()){
			updating.set(true);
			logger.info("Refreshing Scene");

			// move camera back to initialization
			/*
			try {
				GameTaskQueueManager.getManager().update(new Callable<Future>() {

					public Future call() throws Exception {
						// I think this needs to be done in the OpenGL thread
						SceneGameState.getInstance().cameraPerspectiveProjection();
						return null;
					}
				}).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 */

			SceneGameState.getInstance().cameraPerspectiveProjection();


			// replace welcome dialog (perhaps we should try to access the original one from CityPanel)
			/*
			TutorialWindow tw = FengGUI.createWidget(TutorialWindow.class);
			tw.finishSetup();
			StaticLayout.center(tw, GUIGameState.getInstance().getDisp());
			GUIGameState.getInstance().getDisp().addWidget(tw);
			 */


			//LiveProposalManager.getInstance().turnAllVersionsOff();

			movedSinceLastUpdate=false;
			updating.set(false);
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.updater.AbstractUpdater#isInUpdate()
	 */
	@Override
	public boolean isInUpdate() {
		return updating.get();
	}

}
