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

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.util.Color;



/**
 * @author Skye Book
 *
 */
public class ProgressContainerItem extends Container {

	private String progressTitle;
	private Label statusLabel;
	private ProgressiveItem item;

	public ProgressContainerItem(ProgressiveItem item){
		this.item=item;
		getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
		progressTitle=item.getName();
		statusLabel = FengGUI.createWidget(Label.class);
		statusLabel.setText(progressTitle);
		item.addProgressUpdateListener(new IProgressUpdateListener() {

			public void taskFinished() {
				remove();
			}

			public void statusMessageUpdated(String newStatusMessage) {
				statusLabel.setText(progressTitle+": "+newStatusMessage);
			}

			public void progressUpdated(float percentage, String progressString) {
				statusLabel.setText(progressTitle+": "+progressString);
			}
		});
		
		addWidget(statusLabel);
	}
	
	private void remove(){
		if(getParent()!=null)((Container)getParent()).removeWidget(this);
	}

	/**
	 * Get the progress item associated with this FengGUI widget
	 * @return The {@link ProgressiveItem}
	 */
	public ProgressiveItem getItem() {
		return item;
	}
}
