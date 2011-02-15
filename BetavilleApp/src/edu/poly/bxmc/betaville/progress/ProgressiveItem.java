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
package edu.poly.bxmc.betaville.progress;

import java.util.ArrayList;

/**
 * @author Skye Book
 *
 */
public abstract class ProgressiveItem {
	
	protected String name;
	protected String currentStatusMessage;
	protected ArrayList<IProgressUpdateListener> listeners;

	/**
	 * 
	 */
	public ProgressiveItem(String name) {
		this.name=name;
		listeners = new ArrayList<IProgressUpdateListener>();
	}
	
	/**
	 * Sets the current status message for this progress item.  This is the field where
	 * updates such as "paused", "delayed", or "caught in existential crisis" would normally
	 * go.
	 * @param currentStatusMessage The status message to set; Overwrites the previous message
	 */
	public void setCurrentStatusMessage(String currentStatusMessage){
		this.currentStatusMessage=currentStatusMessage;
		for(IProgressUpdateListener listener : listeners){
			listener.statusMessageUpdated(this.currentStatusMessage);
		}
	}
	
	public String getCurrentStatusMessage(){
		return currentStatusMessage;
	}
	
	/**
	 * Call this when a progress item's task is finished
	 */
	public void finished(){
		for(IProgressUpdateListener listener : listeners){
			listener.taskFinished();
		}
	}
	
	/**
	 * Adds a progress update listener to this progress item
	 * @param listener The listener to add
	 */
	public void addProgressUpdateListener(IProgressUpdateListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Removes a progress update listener from this progress item
	 * @param listener The listener to remove
	 */
	public void removeProgressUpdateListener(IProgressUpdateListener listener){
		listeners.remove(listener);
	}
	
	/**
	 * 
	 * @return A floating point number representing the current progress
	 */
	public abstract float getPercentage();
	
	/**
	 * 
	 * @return A fractional string representing the current progress
	 */
	public abstract String getCurrentProgress();
}