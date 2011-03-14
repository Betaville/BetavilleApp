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
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import org.fenggui.ScrollContainer;
import org.fenggui.event.Event;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseEvent;
import org.fenggui.event.mouse.MouseExitedEvent;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.controllers.MouseZoomAction.ZoomLock;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * @author Skye Book
 *
 */
public class BlockingScrollContainer extends ScrollContainer {
	
	private ScrollContainerZoomLock zl = new ScrollContainerZoomLock();

	/**
	 * 
	 */
	public BlockingScrollContainer() {
		super();
		
		addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object arg0, Event arg1) {
				if(arg1 instanceof MouseEvent){
					if(arg1 instanceof MouseEnteredEvent){
						zl.isSafeToZoom=false;
					}
					else if(arg1 instanceof MouseExitedEvent){
						zl.isSafeToZoom=true;
					}
				}
			}
		});
		
		if(SceneGameState.getInstance()!=null){
			SceneGameState.getInstance().getSceneController().getMouseZoom().addZoomLock(zl);
		}
		else{
			// Wait until SceneGameState is accessible before getting the scene controller
			BetavilleNoCanvas.addCompletionListener(new IAppInitializationCompleteListener() {

				/*
				 * (non-Javadoc)
				 * @see edu.poly.bxmc.betaville.IAppInitializationCompleteListener#applicationInitializationComplete()
				 */
				public void applicationInitializationComplete() {
					SceneGameState.getInstance().getSceneController().getMouseZoom().addZoomLock(zl);
				}
			});
		}
	}
	

	private class ScrollContainerZoomLock implements ZoomLock{

		private boolean isSafeToZoom=true;

		/*
		 * (non-Javadoc)
		 * @see edu.poly.bxmc.betaville.jme.controllers.MouseZoomAction.ZoomLock#zoomIsAllowed()
		 */
		public boolean zoomIsAllowed() {
			return isSafeToZoom;
		}
	}
}
