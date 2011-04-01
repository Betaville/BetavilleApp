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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WindowClosedEvent;
import org.fenggui.layout.RowExLayout;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.LightAngleModifier;
import edu.poly.bxmc.betaville.jme.fenggui.LightTweaker;
import edu.poly.bxmc.betaville.jme.fenggui.ModelSwapWindow;
import edu.poly.bxmc.betaville.jme.fenggui.MyLocationWindow;
import edu.poly.bxmc.betaville.jme.fenggui.NetworkedWormholeWindow;
import edu.poly.bxmc.betaville.jme.fenggui.TerrainLoader;
import edu.poly.bxmc.betaville.jme.fenggui.experimental.ButtonToy;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BlockingScrollContainer;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.fenggui.tutorial.TutorialWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.gamestates.ShadowPassState;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.module.PanelAction.AvailabilityRule;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * A break-out point from which to perform activities delegated to a specific user-role
 * @author Skye Book
 *
 */
public class CityPanel extends Window implements IBetavilleWindow{
	private static Logger logger = Logger.getLogger(CityPanel.class);
	private int targetWidth= 180;
	private int targetHeight = 400;
	
	private BlockingScrollContainer sc;
	private Container isc;
	
	private FixedButton move;
	private FixedButton delete;
	private FixedButton terrain;
	private FixedButton swap;
	private FixedButton button;
	private FixedButton lockToTerrain;
	
	private AdminModelMover mover;
	private TerrainLoader terrainWindow;
	private ModelSwapWindow modelSwapper;
	private ButtonToy buttonToy;
	
	private List<PanelAction> panelActions;
	

	public CityPanel(){
		super(true, true);
		panelActions = new ArrayList<PanelAction>();
		internalSetup();
	}
	
	private void internalSetup(){
		//getContentContainer().setLayoutManager(new RowExLayout(false));
		//getContentContainer().setLayoutManager(new StaticLayout());
		//getContentContainer().setSize(targetWidth, targetHeight-getTitleBar().getHeight());
		
		
		
		sc = FengGUI.createWidget(BlockingScrollContainer.class);
		sc.setSize(targetWidth, targetHeight);
		getContentContainer().addWidget(sc);
		sc.setShowScrollbars(true);
		isc = FengGUI.createWidget(Container.class);
		isc.setLayoutManager(new RowExLayout(false));
		sc.setInnerWidget(isc);
		sc.layout();
		
		
		addAction(new TerrainDeleterAction());
		addAction(new VolumeControl());
		addAction(new WireframePanelAction());
		addAction(new FocusOnSelectedAction());
		addAction(new BulkImportAction());
		//addAction(new BookmarkPanel());
		/*
		addAction(new PanelAction("Wormhole", "Wormhole", "Wormhole", AvailabilityRule.ALWAYS, UserType.BASE_COMMITTER, new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(wormholeWindow == null){
					GUIGameState.getInstance().showWormholeWindow("Wormhole Garage NY");
				}
			}
		}));
		*/
		addAction(new SearchAction());
		//addAction(new ExportAction());
		//addAction(new TranslucentPanelAction());
		addAction(new OnOffPanelAction("Lights", "Light tweaking functionality", AvailabilityRule.ALWAYS, UserType.BASE_COMMITTER, false, LightTweaker.class, false));
		addAction(new OnOffPanelAction("Move Speed", "Controls the speed of movement", AvailabilityRule.ALWAYS, UserType.MEMBER, false, AdminCustomMoveSpeed.class,false));
		addAction(new OnOffPanelAction("Tutorials", "Learn some stuff!", AvailabilityRule.ALWAYS, UserType.MEMBER, true, TutorialWindow.class, false));
		//addAction(new OnOffPanelAction("Hierarchy Editor", "Stuff", AvailabilityRule.ALWAYS, UserType.MODERATOR, false, HierarchyEditorWindow.class, false));
		addAction(new OnOffPanelAction("My Location", "Shows My Location!", AvailabilityRule.ALWAYS, UserType.MEMBER, false, MyLocationWindow.class, false));
		//addAction(new OnOffPanelAction("Bookmarks", "Manage your bookmarks", AvailabilityRule.ALWAYS, UserType.MODERATOR, false, BookmarkWindow.class, false));
		addAction(new OnOffPanelAction("Wormhole", "Wormholes", AvailabilityRule.ALWAYS, UserType.MODERATOR, false, NetworkedWormholeWindow.class, false));
		addAction(new PerformancePanelAction());
		addAction(new PanelAction("Toggle Shadows", "Shadows", new IButtonPressedListener() {
			
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				ShadowPassState.getInstance().toggleMapPass();
				//ShadowPassState.getInstance().getShadowPass().setRenderShadows(!ShadowPassState.getInstance().getShadowPass().getRenderShadows());
			}
		}));
		
		addAction(new OnOffPanelAction("Light Angles", "Light Angle Modifier", AvailabilityRule.ALWAYS, UserType.BASE_COMMITTER, false, LightAngleModifier.class, false));
		/*
		addAction(new PanelAction("Toggle Shadow Volumes", "Volumes", new IButtonPressedListener() {
			
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				ShadowPassState.getInstance().getShadowPass().setRenderVolume(!ShadowPassState.getInstance().getShadowPass().getRenderVolume());
			}
		}));
		*/
		
		mover = FengGUI.createWidget(AdminModelMover.class);
		mover.finishSetup();
		move = FengGUI.createWidget(FixedButton.class);
		move.setText("Move");
		move.setWidth(move.getWidth()+10);
		move.setEnabled(false);
		move.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!mover.isInWidgetTree()) GUIGameState.getInstance().getDisp().addWidget(mover);
			}
		});
		
		terrainWindow = FengGUI.createWidget(TerrainLoader.class);
		terrainWindow.finishSetup();
		terrain = FengGUI.createWidget(FixedButton.class);
		terrain.setText("Add Terrain");
		terrain.setWidth(terrain.getWidth()+10);
		terrain.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!terrainWindow.isInWidgetTree()) GUIGameState.getInstance().getDisp().addWidget(terrainWindow);
			}
		});
		
		delete = FengGUI.createWidget(FixedButton.class);
		delete.setText("Delete");
		delete.setWidth(delete.getWidth()+10);
		delete.setEnabled(false);
		delete.addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				Window window = FengUtils.createTwoOptionWindow("Delete", "Are you sure that you would like to delete this design?",
						"no", "yes",
						new IButtonPressedListener() {
					public void buttonPressed(Object source, ButtonPressedEvent e) {
						
						logger.info("b1 pressed");
					}
				},
				new IButtonPressedListener() {
					public void buttonPressed(Object source, ButtonPressedEvent e) {
						int designID = SceneScape.getPickedDesign().getID();
						int removed = NetPool.getPool().getSecureConnection().removeDesign(designID, SettingsPreferences.getUser(), SettingsPreferences.getPass());
						if(removed==0){
							SceneGameState.getInstance().removeDesignFromDisplay(designID);
							logger.info("Design successfully removed");
						}
						else if(removed==-3){
							logger.warn("You are either not the owner of this design or not authorized to remove designs");
						}
					}
				},
				true, true
				);
				
				window.setXY(FengUtils.midWidth(GUIGameState.getInstance().getDisp(), window),FengUtils.midHeight(GUIGameState.getInstance().getDisp(), window));
				GUIGameState.getInstance().getDisp().addWidget(window);
			}
		});
		

		modelSwapper = FengGUI.createWidget(ModelSwapWindow.class);
		modelSwapper.finishSetup();
		swap = FengGUI.createWidget(FixedButton.class);
		swap.setText("Swap Model");
		swap.setWidth(swap.getWidth()+10);
		swap.setEnabled(false);
		swap.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!modelSwapper.isInWidgetTree()) GUIGameState.getInstance().getDisp().addWidget(modelSwapper);
			}
		});
		
		button = FengGUI.createWidget(FixedButton.class);
		button.setText("Button Toy");
		button.setWidth(button.getWidth()+10);
		button.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(buttonToy == null){
					buttonToy = FengGUI.createWidget(ButtonToy.class);
					buttonToy.finishSetup();
				}
				if(!buttonToy.isInWidgetTree()){
					GUIGameState.getInstance().getDisp().addWidget(buttonToy);
					buttonToy.setXY(FengUtils.midWidth(GUIGameState.getInstance().getDisp(), buttonToy),
							FengUtils.midHeight(GUIGameState.getInstance().getDisp(), buttonToy));
				}
			}
		});
		
		lockToTerrain = FengGUI.createWidget(FixedButton.class);
		lockToTerrain.setText("Lock to Terrain");
		lockToTerrain.setEnabled(false);
		lockToTerrain.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				int itemToLock = SceneScape.getPickedDesign().getID();
				SceneGameState.getInstance().getTerrainNode().attachChild(SceneGameState.getInstance().getSpecificDesign(itemToLock));
				if(!NetPool.getPool().getSecureConnection().changeDesignName(itemToLock, SettingsPreferences.getUser(), SettingsPreferences.getPass(), SceneScape.getCity().findDesignByID(itemToLock).getName()+"$TERRAIN")){
					FengUtils.showNewDismissableWindow("Betaville", "You don't have permissions to do this!", "ok", true);
				}
				else{
					SceneScape.clearTargetSpatial();
					FengUtils.showNewDismissableWindow("Betaville", "Success!", "ok", true);
				}
			}
		});
		
		
		SceneScape.addSelectionListener(new ISpatialSelectionListener() {
			public void selectionCleared(Design previousDesign) {
				move.setEnabled(false);
				delete.setEnabled(false);
				swap.setEnabled(false);
				lockToTerrain.setEnabled(false);
			}
			
			public void designSelected(Spatial spatial, Design design) {
				move.setEnabled(true);
				delete.setEnabled(true);
				swap.setEnabled(true);
				lockToTerrain.setEnabled(true);
			}
		});
		
		if(SettingsPreferences.getUserType().compareTo(UserType.MODERATOR)>=0)isc.addWidget(move);
		if(SettingsPreferences.getUserType().compareTo(UserType.MODERATOR)>=0)isc.addWidget(delete);
		if(SettingsPreferences.getUserType().compareTo(UserType.MODERATOR)>=0)isc.addWidget(swap);
		//if(SettingsPreferences.getUserType().compareTo(UserType.MODERATOR)>=0)getContentContainer().addWidget(terrain);
		if(SettingsPreferences.getUserType().compareTo(UserType.MODERATOR)>=0)isc.addWidget(lockToTerrain);
		
		addWindowClosedListener(new IWindowClosedListener() {
			public void windowClosed(WindowClosedEvent windowClosedEvent) {
				for(PanelAction action : panelActions){
					if(action instanceof ICityPanelClosedListener){
						((ICityPanelClosedListener)action).cityPanelClosed();
					}
				}
			}
		});
	}
	
	public void addAction(PanelAction action){
		if(SettingsPreferences.getUserType().compareTo(action.getRequiredUserLevel())>=0){
			panelActions.add(action);
			isc.addWidget(action.getButton());
		}
	}
	
	public void removeAction(PanelAction action){
		if(action.getButton().isInWidgetTree()){
			isc.removeWidget(action.getButton());
		}
	}
	
	public void finishSetup(){
		setTitle("City Panel");
		setSize(targetWidth, targetHeight);
	}
	
	/**
	 * Triggers some action when the city panel is closed.
	 * @author Skye Book
	 *
	 */
	public interface ICityPanelClosedListener{
		/**
		 * Called when the city panel is closed
		 */
		public void cityPanelClosed();
	}
}
