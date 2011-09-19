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
package edu.poly.bxmc.betaville.jme.fenggui.experimental;

import java.util.ArrayList;
import java.util.List;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;

import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;

/**
 * This is the example that Cemre, Peter, and I discussed at the Hochschule this evening
 * @author Skye Book
 *
 */
public class PanelContainer extends Container {
	
	private List<FixedButton> tabs;
	private Container displayContainer;
	private Container tabContainer;
	private int activeTab;

	/**
	 * 
	 */
	public PanelContainer() {
		activeTab = -1;
		tabs = new ArrayList<FixedButton>();
		displayContainer = FengGUI.createWidget(Container.class);
		displayContainer.getAppearance().add(new PlainBackground(Color.BLACK));
		displayContainer.setSize(200, 80);
		displayContainer.setXY(0,0);
		tabContainer = FengGUI.createWidget(Container.class);
		setLayoutManager(new StaticLayout());
		
		tabContainer.setLayoutManager(new RowExLayout(true));
		addWidget(tabContainer);
		addWidget(displayContainer);		
	}
	
	public void addTab(String name, final Container contentContainer){
		final FixedButton newTab = FengGUI.createWidget(FixedButton.class);
		newTab.setText(name);
		newTab.setWidth(newTab.getWidth()+10);
		tabContainer.addWidget(newTab);
		tabContainer.setXY(0, 100 - tabContainer.getHeight());
		tabs.add(newTab);
		newTab.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				displayContainer.removeAllWidgets();
				
				if(activeTab >= 0 && tabs.get(activeTab).getText().equals(newTab.getText())) {
					activeTab = -1;
					displayContainer.setHeight(0);
				} else {
					int i;
					for(i = 0; i < tabs.size(); i++) {
						if(tabs.get(i).getText().equals(newTab.getText())){
							activeTab = i;
							displayContainer.addWidget(contentContainer);
							System.out.println(activeTab);
						}
					}					
				}
			}
		});
	}
}
