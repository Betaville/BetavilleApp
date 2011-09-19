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
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.composite.tab.TabContainer;
import org.fenggui.composite.tab.TabItem;
import org.fenggui.layout.RowExLayout;
import org.fenggui.util.Alignment;

import com.jme.scene.Spatial;

/**
 * @author Skye Book
 *
 */
public class RenderEditor extends TabContainer {
	
	private Spatial s;
	
	private Label wireframeLabel;
	private CheckBox<Boolean> wireframe;
	
	private Label renderQueueModeLabel;
	private ComboBox renderQueueMode;
	
	private TabItem simple;
	private TabItem mats;

	public RenderEditor() {
		super(Alignment.TOP, Alignment.BOTTOM);
		
		wireframeLabel = FengGUI.createWidget(Label.class);
		wireframeLabel.setText("Wireframe");
		wireframe = FengGUI.createCheckBox();
		
		renderQueueModeLabel = FengGUI.createWidget(Label.class);
		renderQueueModeLabel.setText("Render Queue Mode");
		renderQueueMode  = FengGUI.createWidget(ComboBox.class);
		renderQueueMode.addItem("Inherit");
		renderQueueMode.addItem("Opaque");
		renderQueueMode.addItem("Ortho");
		renderQueueMode.addItem("Skip");
		renderQueueMode.addItem("Transparent");
		
		simple = FengGUI.createWidget(TabItem.class);
		simple.getHeadWidget().setText("Simple");
		simple.addWidget(createSet(wireframeLabel, wireframe));
		simple.addWidget(createSet(renderQueueModeLabel, renderQueueMode));
		addTab(simple);
		
		mats = FengGUI.createWidget(TabItem.class);
		mats.getHeadWidget().setText("Material");
		addTab(mats);
	}
	
	private Container createSet(Label label, IWidget widget){
		Container c = FengGUI.createWidget(Container.class);
		c.setLayoutManager(new RowExLayout(true));
		c.addWidget(label, widget);
		return c;
	}
	
	public void setSpatial(Spatial s){
		this.s=s;
	}
	
}
