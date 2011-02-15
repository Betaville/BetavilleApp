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

import org.apache.log4j.Logger;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.layout.StaticLayout;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Design;

/**
 * @author Skye Book
 *
 */
public class HierarchyEditorWindow extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(HierarchyEditorWindow.class);
	
	private int targetWidth=300;
	private int targetHeight=300;
	private SceneGraphView sg;
	
	private SpatialExplorer explorer;
	
	/**
	 * 
	 */
	public HierarchyEditorWindow(){
		super(true, true);
		getContentContainer().setSize(targetWidth, getTitleBar().getHeight()-targetHeight);
		sg = FengGUI.createWidget(SceneGraphView.class);
		explorer = FengGUI.createWidget(SpatialExplorer.class);
		explorer.finishSetup();
		BetavilleNoCanvas.addCompletionListener(new IAppInitializationCompleteListener() {
			public void applicationInitializationComplete() {
				StaticLayout.center(explorer, GUIGameState.getInstance().getDisp());
			}
		});
		
		SceneScape.addSelectionListener(new ISpatialSelectionListener() {
			
			public void selectionCleared(Design previousDesign) {
				if(isInWidgetTree()) sg.clear();
			}
			
			public void designSelected(Spatial spatial, Design design,
					Design previousDesign) {
				if(isInWidgetTree()) sg.load(spatial);
			}
		});
		
		sg.addViewAction(new ISceneGraphViewAction() {
			
			public void selectionChanged(Spatial newSpatial, Spatial oldSpatial) {
				logger.info(newSpatial.getName()+" selected from scene graph tree");
				if(!explorer.isInWidgetTree()) GUIGameState.getInstance().getDisp().addWidget(explorer);
			}
		});
		
		StaticLayout.center(sg, getContentContainer());
		getContentContainer().addWidget(sg);
		layout();
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setSize(targetWidth, targetHeight);
		setTitle("Hierarchy Editor");
	}
}
