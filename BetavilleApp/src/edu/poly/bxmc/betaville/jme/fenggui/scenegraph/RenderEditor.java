/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.scenegraph;

import org.fenggui.CheckBox;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Item;
import org.fenggui.Label;
import org.fenggui.composite.tab.TabContainer;
import org.fenggui.composite.tab.TabItem;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Alignment;

import com.jme.renderer.Renderer;
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
