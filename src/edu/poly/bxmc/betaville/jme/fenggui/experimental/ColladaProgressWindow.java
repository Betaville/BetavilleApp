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
package edu.poly.bxmc.betaville.jme.fenggui.experimental;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.ProgressBar;
import org.fenggui.composite.Window;
import org.fenggui.layout.RowLayout;

/**
 * @author Skye Book
 *
 */
public class ColladaProgressWindow extends Window {
	private ProgressBar progressBar;
	private int numSteps;
	private int numSubSteps;
	private Label currentLibrary;
	private Label step;
	private Label subStep;
	
	/**
	 * 
	 */
	public ColladaProgressWindow() {
		super(false,false);
		this.setLayoutManager(new RowLayout(false));
		FengGUI.getTheme().setUp(this);
		buildUI();
		this.setXY(100, 100);
		this.setTitle("Model Loading");
	}

	private void buildUI(){
		progressBar = FengGUI.createWidget(ProgressBar.class);
		
		Container infoContainer = FengGUI.createWidget(Container.class);
		infoContainer.setLayoutManager(new RowLayout(true));
		
		currentLibrary = FengGUI.createWidget(Label.class);
		step = FengGUI.createWidget(Label.class);
		subStep = FengGUI.createWidget(Label.class);
		
		infoContainer.addWidget(currentLibrary,step,subStep);
		this.addWidget(progressBar, infoContainer);
	}
	
	public void setLibrary(String lib, int numSteps){
		this.numSteps=numSteps;
		currentLibrary.setText(lib);
		progressBar.setValue((100/numSteps));
	}
	
	public void setStep(int s){
		step.setText("Step: " + s + " of " + numSteps);
	}
	
	public void setSubStep(int s){
		subStep.setText("Sub Step: " + s + " of " + numSubSteps);
	}
	
	public void setNumberSubSteps(int subSteps){
		numSubSteps=subSteps;
	}

}
