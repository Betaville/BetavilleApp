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

import org.fenggui.Container;
import org.fenggui.decorator.background.Background;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.Event;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.util.Color;

/**
 * An {@link IGenericEventListener} that can be applied to a
 * {@link Container} to stylize rollover events
 * @author Skye Book
 *
 */
public class RolloverColorChanger implements IGenericEventListener {
	
	private Background background;
	private Container container;

	/**
	 * 
	 * @param container The container that the rollover will apply to
	 * @param highlightColor The color of the container's background
	 * when highlighted
	 */
	public RolloverColorChanger(Container container, Color highlightColor) {
		this.container=container;
		background = new PlainBackground(highlightColor);
	}

	/* (non-Javadoc)
	 * @see org.fenggui.event.IGenericEventListener#processEvent(java.lang.Object, org.fenggui.event.Event)
	 */
	public void processEvent(Object arg0, Event event) {
		// Filter out any non-mouse events
		if(!(event instanceof MouseEvent)) return;
		
		if(event instanceof MouseEnteredEvent){
			container.getAppearance().add(background);
		}
		else if(event instanceof MouseExitedEvent){
			container.getAppearance().removeAll();
		}
	}

}
