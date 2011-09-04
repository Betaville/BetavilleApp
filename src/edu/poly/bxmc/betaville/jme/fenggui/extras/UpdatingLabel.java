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

import org.fenggui.Label;

import edu.poly.bxmc.betaville.SettingsPreferences;

/**
 * Label that can be constantly updated
 * @author Skye Book
 *
 */
public class UpdatingLabel extends Label {

	private String originalText;
	private String repeatString = ".";
	private int repetitions = 3;
	private long time = 500;

	private boolean run = false;

	public UpdatingLabel() {
		super();
	}

	public void setRepeat(String repeatString, int repetitions){
		this.repeatString=repeatString;
		this.repetitions=repetitions;
	}

	public void setText(String text){
		originalText=text;
		super.setText(text);
	}

	private void directlySetText(String text){
		super.setText(text);
	}

	/**
	 * Starts updating this label
	 */
	public synchronized void start(){
		// only start running the updater if it isn't currently working
		if(!run){
			run = true;
			SettingsPreferences.getGUIThreadPool().execute(new Updater());
		}
	}

	/**
	 * Stops updating this label
	 */
	public synchronized void stop(){
		run = false;
	}

	private class Updater implements Runnable{

		@Override
		public void run() {
			while(run){
				for(int i=0; i<repetitions; i++){
					try {
						if(!run){
							directlySetText(originalText);
							return;
						}
						Thread.sleep(time);
						if(!run){
							directlySetText(originalText);
							return;
						}
					} catch (InterruptedException e) {
						// we couldn't sleep... no sense in bothering with this now
					}
					
					// we've now waited the appropriate amount of time, add the repeated string
					directlySetText(getText()+repeatString);
				}
				// reset the label once we've gotten to the desired number of repeats
				directlySetText(originalText);
			}
		}

	}
}
