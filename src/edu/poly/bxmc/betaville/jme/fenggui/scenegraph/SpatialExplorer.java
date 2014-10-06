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
package edu.poly.bxmc.betaville.jme.fenggui.scenegraph;

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FG;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.composite.tab.TabContainer;
import org.fenggui.composite.tab.TabItem;
import org.fenggui.layout.RowExLayout;
import org.fenggui.util.Alignment;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;

/**
 * @author Skye Book
 *
 */
public class SpatialExplorer extends Window implements IBetavilleWindow {
	
	private int targetWidth = 300;
	private int targetHeight = 225;
	
	private Container info;
	private TextEditor name;
	private CheckBox<Boolean> onOff;
	private CheckBox<Boolean> isLayer;
	
	private RenderEditor render;
	
	private TabContainer tabs;
	private TabItem infoTab;
	private TabItem renderTab;

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	public SpatialExplorer() {
		super(true, true);
		getContentContainer().setSize(targetWidth, targetHeight-getTitleBar().getHeight());
		tabs = FG.createTab(getContentContainer(), Alignment.TOP, Alignment.BOTTOM);
		
		infoTab = FengGUI.createWidget(TabItem.class);
		infoTab.getHeadWidget().setText("Information");
		createInfoContainer();
	    infoTab.addWidget(info);
	    
	    renderTab = FengGUI.createWidget(TabItem.class);
	    renderTab.getHeadWidget().setText("Renderer");
	    render = FengGUI.createWidget(RenderEditor.class);
	    render.setSize(targetWidth, targetHeight-renderTab.getHeadWidget().getHeight());
	    renderTab.addWidget(render);
	    tabs.addTab(infoTab);
	    tabs.addTab(renderTab);
	    getContentContainer().addWidget(tabs);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
	    setSize(targetWidth, targetHeight);
	}
	
	private void createInfoContainer(){
		info = FengGUI.createWidget(Container.class);
		info.setSize(targetWidth, targetHeight-infoTab.getHeadWidget().getHeight());
		info.setLayoutManager(new RowExLayout(false));
		
		name = FengGUI.createWidget(TextEditor.class);
		
		onOff = FengGUI.createCheckBox();
		onOff.setText("Show Spatial");
		
		isLayer = FengGUI.createCheckBox();
		isLayer.setText("Enable as layer");
		
		info.addWidget(name, onOff, isLayer);
	}
}
