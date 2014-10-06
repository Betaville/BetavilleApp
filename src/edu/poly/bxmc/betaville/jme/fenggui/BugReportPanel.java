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
package edu.poly.bxmc.betaville.jme.fenggui;

import org.fenggui.ComboBox;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;

import com.centerkey.utils.BareBonesBrowserLaunch;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.net.BugzillaOptions;

/**
 * A window for submitting a bug!
 * @author Skye Book
 *
 */
public class BugReportPanel extends Window implements IBetavilleWindow{
	
	private int targetWidth=300;
	private int targetHeight=200;
	
	private Label thanks;
	private String thanksText = "By reporting bugs you are helping to make Betaville a" +
			"better place for everyone, thank you!";
	
	private ComboBox components;
	
	private String component3D = "3D Content";
	private String componentCoord = "Scale";
	private String componentGraphics = "Graphics";
	private String componentGUI = "GUI";
	private String componentInteraction = "Interaction";
	
	private FixedButton submit;

	/**
	 * 
	 */
	public BugReportPanel() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		thanks = FengGUI.createWidget(Label.class);
		thanks.setMultiline(true);
		thanks.setText(thanksText);
		
		components = FengGUI.createWidget(ComboBox.class);
		components.addItem(component3D);
		components.addItem(componentCoord);
		components.addItem(componentGraphics);
		components.addItem(componentGUI);
		components.addItem(componentInteraction);
		
		submit = FengGUI.createWidget(FixedButton.class);
		submit.setText(Labels.get("Generic.submit"));
		submit.addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				launchAction();
			}
		});
		
		getContentContainer().addWidget(thanks, components, submit);
	}
	
	private void launchAction(){
		String toUse;
		if(components.getSelectedValue().equals(component3D)){
			toUse = BugzillaOptions.constructURL(BugzillaOptions.component_3dContent);
		}
		else if(components.getSelectedValue().equals(componentCoord)){
			toUse = BugzillaOptions.constructURL(BugzillaOptions.component_CoordinateSystem);
		}
		else if(components.getSelectedValue().equals(componentGraphics)){
			toUse = BugzillaOptions.constructURL(BugzillaOptions.component_Graphics);
		}
		else if(components.getSelectedValue().equals(componentGUI)){
			toUse = BugzillaOptions.constructURL(BugzillaOptions.component_GUI);
		}
		else if(components.getSelectedValue().equals(componentInteraction)){
			toUse = BugzillaOptions.constructURL(BugzillaOptions.component_Interaction);
		}
		else{
			toUse = BugzillaOptions.baseURL;
		}
		BareBonesBrowserLaunch.openURL(toUse);
	}

	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);
	}
}
