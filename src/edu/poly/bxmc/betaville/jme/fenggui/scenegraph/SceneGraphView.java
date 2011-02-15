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

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.composite.tree.ITreeModel;
import org.fenggui.composite.tree.Tree;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.StaticLayout;

import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * @author Skye Book
 */
public class SceneGraphView extends Container {
	private static final Logger logger = Logger.getLogger(SceneGraphView.class);

	private Label infoLabel;
	private Tree<SceneElement> tree;
	//private TreeItem root;
	private SceneElement rootNode;
	
	private Spatial selection = null;
	
	private ArrayList<ISceneGraphViewAction> viewActions;
	
	@SuppressWarnings("unchecked")
	public SceneGraphView() {
		setLayoutManager(new StaticLayout());
		viewActions = new ArrayList<ISceneGraphViewAction>();
		infoLabel = FengGUI.createWidget(Label.class);
		infoLabel.setText("Stuff goes here!");
		logger.info("label created");
		tree = FengGUI.createWidget(Tree.class);
		tree.getToggableWidgetGroup().addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(Object sender,
					SelectionChangedEvent selectionChangedEvent) {
				
				// notify anyone interested that the selection has changed
				for(ISceneGraphViewAction action : viewActions){
					action.selectionChanged(((SceneElement)tree.getToggableWidgetGroup().getSelectedItem().getData()).spatial, selection);
				}
				
				// set the new selection
				selection = ((SceneElement)tree.getToggableWidgetGroup().getSelectedItem().getData()).spatial;
			}
		});

		logger.info("tree created");
		addWidget(tree);
	}
	
	public void addViewAction(ISceneGraphViewAction action){
		viewActions.add(action);
	}
	
	public void removeViewAction(ISceneGraphViewAction action){
		viewActions.remove(action);
	}

	public void clear(){
		removeWidget(tree);
	}

	public void load(Spatial topItem){
		if(tree.isInWidgetTree()) removeWidget(tree);
		rootNode = new SceneElement(topItem);
		loadCycle(topItem, rootNode);
		tree.setModel(new SceneGraphTreeModel());
		addWidget(tree);
	}

	private void loadCycle(Spatial s, SceneElement e){
		SceneElement child = new SceneElement(s);
		e.children.add(child);
		if(s instanceof Node){
			if(((Node)s).getQuantity()>0){
				for(Spatial c : ((Node)s).getChildren()){
					loadCycle(c, child);
				}
			}
		}
	}
	
	public Spatial getSelectedItem(){
		return selection;
	}

	public class SceneElement
	{
		public SceneElement(Spatial item)
		{
			this.text = item.getName();
			this.spatial = item;
		}

		public Spatial spatial = null;
		public ArrayList<SceneElement> children = new ArrayList<SceneElement>();
		public String            text     = null;
	}

	class SceneGraphTreeModel implements ITreeModel<SceneElement>
	{

		public int getNumberOfChildren(SceneElement node)
		{
			return node.children.size();
		}

		public Pixmap getPixmap(SceneElement node)
		{
			return null;
		}

		public String getText(SceneElement node)
		{
			return node.text;
		}

		public SceneElement getRoot()
		{
			return rootNode;
		}

		public SceneElement getNode(SceneElement parent, int index)
		{
			return parent.children.get(index);
		}

		/* (non-Javadoc)
		 * @see org.fenggui.composite.tree.ITreeModel#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(SceneElement node)
		{
			return !node.children.isEmpty();
		}

	}
}
