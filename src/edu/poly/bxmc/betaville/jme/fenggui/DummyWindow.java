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
package edu.poly.bxmc.betaville.jme.fenggui;

import org.apache.log4j.Logger;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.layout.RowExLayout;

import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.net.NetModelLoader;

/**
 * @author Skye Book
 *
 */
public class DummyWindow extends Window {
	private static Logger logger = Logger.getLogger(DummyWindow.class);
	
	private int targetWidth=130;
	private int targetHeight=100;
	
	private Container duplicateContainer;
	private Container translateContainer;
	private Container translateLabelContainer;
	private Container rotateContainer;
	private Container acceptContainer;
	
	private Label duplicateLabel;
	private Label translateLabel;
	private Label rotateLabel;
	
	private ComboBox modeSelector;
	
	private FixedButton save;
	
	private ISliderMovedListener moveSliderListener;
	
	private UTMCoordinate originalLocation;
	private int originalRotation;
	private Label savedLabel;
	
	private String dummyFile;
	
	UTMCoordinate location;

	public DummyWindow(){
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		getContentContainer().setSize(targetWidth-10, targetHeight-getTitleBar().getHeight());
		getContentContainer().setX(5);
		
		
		setupModulators();
		setupConfirmation();
		setAllEnabled(getContentContainer(), true);
		// This listener is what tells the window which object to do work on

		
	}

	private void setupConfirmation() {
		acceptContainer = FengGUI.createWidget(Container.class);
		acceptContainer.setLayoutManager(new RowExLayout());

		save = FengGUI.createWidget(FixedButton.class);
		save.setText("Create");
		save.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(modeSelector.getSelectedValue().equals("Box")){
					dummyFile = "BaseObject1.dae";
					logger.info(dummyFile);
				}else if(modeSelector.getSelectedValue().equals("Marker")){
					dummyFile = "Betaville_Marker_1.dae";
					logger.info(dummyFile);
				}else if(modeSelector.getSelectedValue().equals("Tree")){
					dummyFile = "White_Oak_1.dae";
					logger.info(dummyFile);
				}
				NetModelLoader.createDummy(GUIGameState.getInstance().getCurrentTerrainSelection(), dummyFile);
			}
		});

		acceptContainer.addWidget(save);
		getContentContainer().addWidget(acceptContainer);
	}

	private void setupModulators() {
		duplicateContainer = FengGUI.createWidget(Container.class);
		duplicateContainer.setLayoutManager(new RowExLayout(false));
		duplicateContainer.setWidth(targetWidth);
		
		duplicateLabel = FengGUI.createWidget(Label.class);
		duplicateLabel.setText("Duplications: 1");
		duplicateLabel.setXY(0, 0);
		
		
		getContentContainer().addWidget(duplicateContainer);
		
		Label space = FengGUI.createWidget(Label.class);
		space.setText(" ");
		getContentContainer().addWidget(space);
		
		modeSelector = FengGUI.createWidget(ComboBox.class);
		modeSelector.addItem("Box");
		modeSelector.addItem("Marker");
		modeSelector.addItem("Tree");
		getContentContainer().addWidget(modeSelector);
		
		Label space2 = FengGUI.createWidget(Label.class);
		space2.setText(" ");
		getContentContainer().addWidget(space2);
	}
	
	
	private void setAllEnabled(IWidget parent, boolean enabled){
		for(IWidget w : ((Container)parent).getWidgets()){
			w.setEnabled(enabled);
			if(w instanceof Container){
				setAllEnabled(w, enabled);
			}
		}
	}


	public void setProposalLocation(UTMCoordinate utm){
		location = utm;
	}

	public void finishSetup(){
		setTitle("Dummy Creator");
		setSize(targetWidth, targetHeight);
		setXY(Binding.getInstance().getCanvasWidth()/2-getWidth()/2, Binding.getInstance().getCanvasHeight()/2-getHeight()/2);
	}


	public void close(){
		originalLocation=null;
		super.close();
	}
}

