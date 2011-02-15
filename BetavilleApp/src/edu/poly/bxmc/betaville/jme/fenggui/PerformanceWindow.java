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

import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowLayout;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Design;

/**
 * Displays performance data about the application.
 * @author Skye Book
 *
 */
public class PerformanceWindow extends Window  implements IBetavilleWindow{
	private Label fpsLabel;
	private Label fpsCount;
	private Label triLabel;
	private Label triCount;
	private Label selectedLabel;
	private Label selectedCount;
	
	private boolean framesInWholeNumbers = true;
	private boolean trisInThousands = true;
	

	/**
	 * 
	 */
	public PerformanceWindow() {
		super(true,true);
		this.setLayoutManager(new RowLayout(false));
		createFPSContainer();
		createTriContainer();
		createOptionsContainer();

		SceneScape.addSelectionListener(new ISpatialSelectionListener() {

			public void selectionCleared(Design previousDesign) {
				if(isInWidgetTree()) selectedCount.setText("0");
			}

			public void designSelected(Spatial spatial, Design design,
					Design previousDesign) {
				if(isInWidgetTree()){
					if(trisInThousands){
						triCount.setText(""+(SceneGameState.getInstance().getRootNode().getTriangleCount()/1000)+"k");
						if(spatial.getTriangleCount()>1000){
							selectedCount.setText(""+(spatial.getTriangleCount()/1000)+"k");
						}
					}
					else{
						triCount.setText(""+SceneGameState.getInstance().getRootNode().getTriangleCount());
						selectedCount.setText(""+spatial.getTriangleCount());
					}
				}
			}
		});
	}
	
	private void createFPSContainer(){
		Container fpsContainer = FengGUI.createWidget(Container.class);
		fpsContainer.setLayoutManager(new RowLayout(true));
		fpsLabel = FengGUI.createWidget(Label.class);
		fpsLabel.setText("Frames Per Second:");
		fpsCount = FengGUI.createWidget(Label.class);
		fpsContainer.addWidget(fpsLabel, fpsCount);
		this.addWidget(fpsContainer);
	}
	
	private void createTriContainer(){
		Container triContainer = FengGUI.createWidget(Container.class);
		triContainer.setLayoutManager(new RowLayout(true));
		triLabel = FengGUI.createWidget(Label.class);
		triLabel.setText("Triangles in Scene:");
		triCount = FengGUI.createWidget(Label.class);
		triContainer.addWidget(triLabel, triCount);
		this.addWidget(triContainer);
		
		Container selectedContainer = FengGUI.createWidget(Container.class);
		selectedContainer.setLayoutManager(new RowLayout(true));
		selectedLabel = FengGUI.createWidget(Label.class);
		selectedLabel.setText("Triangles in Selected: ");
		selectedCount = FengGUI.createWidget(Label.class);
		selectedCount.setText("0");
		selectedContainer.addWidget(selectedLabel, selectedCount);
		this.addWidget(selectedContainer);
	}
	
	private void createOptionsContainer(){
		Container optionsContainer = FengGUI.createWidget(Container.class);
		optionsContainer.setLayoutManager(new RowLayout(true));
		
		final CheckBox<Boolean> fpsRounding = FengGUI.createCheckBox();
		fpsRounding.setSelected(framesInWholeNumbers);
		fpsRounding.setText("Round FPS");
		fpsRounding.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(Object sender,
					SelectionChangedEvent selectionChangedEvent) {
				framesInWholeNumbers=fpsRounding.isSelected();
			}
		});
		
		final CheckBox<Boolean> triRounding = FengGUI.createCheckBox();
		triRounding.setSelected(trisInThousands);
		triRounding.setText("Round Triangle Count");
		triRounding.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(Object sender,
					SelectionChangedEvent selectionChangedEvent) {
				trisInThousands=triRounding.isSelected();
			}
		});
		
		optionsContainer.addWidget(fpsRounding, triRounding);
		this.addWidget(optionsContainer);
	}
	
	public void finishSetup(){
		setTitle("Performance");
		setXY(0,32);
		setWidth(225);
	}
	
	public void updateCounts(float tpf){
		if(framesInWholeNumbers){
			fpsCount.setText(""+(int)(1/tpf));
		}
		else{
			fpsCount.setText(""+1/tpf);
		}
		if(trisInThousands){
			triCount.setText(""+(SceneGameState.getInstance().getRootNode().getTriangleCount()/1000)+"k");
		}
		else{
			triCount.setText(""+SceneGameState.getInstance().getRootNode().getTriangleCount());
		}
	}
}
