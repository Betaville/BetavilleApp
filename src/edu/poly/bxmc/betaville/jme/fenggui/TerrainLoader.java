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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.composite.filedialog.FileDialogListener;
import org.fenggui.composite.filedialog.FileDialogWindow;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowLayout;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.terrain.TerrainBlock;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.util.DriveFinder;
import edu.poly.bxmc.betaville.module.GlobalSceneModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;

/**
 * @author Skye Book
 *
 */
public class TerrainLoader extends Window implements IBetavilleWindow {
	private static Logger logger = Logger.getLogger(TerrainLoader.class);
	
	
	private int targetWidth = 300;
	private int targetHeight = 400;
	
	private Label latL;
	private Label lonL;
	private TextEditor latE;
	private TextEditor lonE;
	
	private FixedButton load;
	
	private TerrainBlock terrainBlock;
	private FixedButton wire;
	
	private CheckBox<Boolean> stickToTerrain;
	
	private TerrainBlockHugger terrainMagnet;
	
	public TerrainLoader(){
		super(true, true);
		getContentContainer().setSize(targetWidth, targetHeight-getTitleBar().getHeight());
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		terrainMagnet = new TerrainBlockHugger("TerrainBlock Hugger", "Hugs a terrain block");
		
		Container lat = FengGUI.createWidget(Container.class);
		lat.setLayoutManager(new RowLayout(true));
		lat.setWidth(targetWidth);
		Container lon = FengGUI.createWidget(Container.class);
		lon.setLayoutManager(new RowLayout(true));
		lon.setWidth(targetWidth);
		
		latL = FengGUI.createWidget(Label.class);
		latL.setText("Latitude");
		
		lonL = FengGUI.createWidget(Label.class);
		lonL.setText("Longitude");
		
		latE = FengGUI.createWidget(TextEditor.class);
		latE.setText("                             ");
		
		lonE = FengGUI.createWidget(TextEditor.class);
		lonE.setText("                             ");
		
		latE.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		lonE.setRestrict(TextEditor.RESTRICT_NUMBERSONLYDECIMAL);
		
		lat.addWidget(latL, latE);
		lon.addWidget(lonL, lonE);
		
		wire = FengGUI.createWidget(FixedButton.class);
		wire.setText("Wireframe Mode");
		wire.addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(terrainBlock==null) return;
				
				if(terrainBlock.getRenderState(StateType.Wireframe)!=null){
					terrainBlock.clearRenderState(StateType.Wireframe);
					wire.setText("Wireframe Mode");
				}
				else{
					terrainBlock.setRenderState(DisplaySystem.getDisplaySystem().getRenderer().createWireframeState());
					wire.setText("Solid Mode");
				}
				
				terrainBlock.updateRenderState();
			}
		});
		
		load = FengGUI.createWidget(FixedButton.class);
		load.setText("Load Terrain");
		load.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){
				final FileDialogWindow fileDiag = new FileDialogWindow(true, false, false, false);
				fileDiag.getDialog().setCurrentDirectory(DriveFinder.getHomeDir());

				// populate the drop down list of locations
				Iterator<File> it = DriveFinder.getPartitions().iterator();
				while(it.hasNext()){
					fileDiag.getDialog().addToRoots(it.next());
				}


				fileDiag.setSize((GUIGameState.getInstance().getDisp().getWidth() / 4)*3, (GUIGameState.getInstance().getDisp().getHeight() / 4)*3);
				GUIGameState.getInstance().getDisp().addWidget(fileDiag);

				// ensure that we're only allowing .jme files
				for(FileFilter f : fileDiag.getDialog().getFileFilters()){
					fileDiag.getDialog().removeFileFilter(f);
				}
				
				fileDiag.getDialog().addFileFilter(new FileFilter() {
					
					public boolean accept(File pathname) {
						if(pathname.toString().endsWith("jme")
								|| pathname.isDirectory()){
							return true;
						}
						else return false;
					}
				}, ".jme");
				
				fileDiag.getDialog().addListener(new FileDialogListener(){

					public void cancel() {
						logger.info("File dialog cancel button hit");
					}

					public void fileSelected(File file) {
						// assuming we have the jme file, let's load it
						try {
							logger.info("loading " + file.toString());
							terrainBlock = (TerrainBlock) BinaryImporter.getInstance().load(file);
							terrainBlock.setLocalScale(1/SceneScape.SceneScale);
							//GeometryUtilities.removeRenderState(terrainBlock, StateType.Texture);
							
							float lowest = 0;
							for(int i=0; i<terrainBlock.getHeightMap().length; i++){
								if(i==0){
									lowest = terrainBlock.getHeightMap()[i];
									continue;
								}
								
								float current = terrainBlock.getHeightMap()[i];
								if(current<lowest) lowest=current;
							}
							
							Vector3f loc = DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation().clone();
							loc.setY(terrainBlock.getHeightMap()[0]);
							terrainBlock.setLocalTranslation(loc);
							SceneGameState.getInstance().getTerrainNode().attachChild(terrainBlock);
						} catch (IOException e){
							logger.warn("File: "+file.toString()+" could not be loaded!");
							FengUtils.showNewDismissableWindow("Bad News..",
									"I couldn't load your file!", Labels.get("Generic.ok"), true);

						}
					}
				});
			}
		});
		
		stickToTerrain = FengGUI.createCheckBox();
		stickToTerrain.setText("Hug Terrain");
		stickToTerrain.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(Object sender,
					SelectionChangedEvent selectionChangedEvent) {
				if(stickToTerrain.isSelected()){
					try {
						SceneGameState.getInstance().addModuleToUpdateList(terrainMagnet);
					} catch (ModuleNameException e) {
						logger.error("Unique module names are required!", e);
					}
				}
				else{
					SceneGameState.getInstance().removeModuleFromUpdateList(terrainMagnet);
				}
			}
		});
		
		getContentContainer().addWidget(lat, lon, load, wire, stickToTerrain);
	}
	

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);
	}

	private class TerrainBlockHugger extends Module implements GlobalSceneModule{

		public TerrainBlockHugger(String name, String description) {
			super(name, description);
		}

		public void initialize(Node scene) {}

		public void onUpdate(Node scene, Vector3f cameraLocation, Vector3f cameraDirection) {
			if(terrainBlock==null) return;
			
			float heightHere = terrainBlock.getHeight(cameraLocation);
			if(!Float.isNaN(heightHere)){
				cameraLocation.setY(heightHere);
				DisplaySystem.getDisplaySystem().getRenderer().getCamera().setLocation(cameraLocation);
			}
		}

		public void deconstruct() {}
		
	}
}
