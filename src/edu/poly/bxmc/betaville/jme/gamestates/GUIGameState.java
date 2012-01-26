/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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
package edu.poly.bxmc.betaville.jme.gamestates;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.TextEditor;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.lwjgl.LWJGLBinding;
import org.fenggui.event.IWidgetListChangedListener;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.WidgetListChangedEvent;
import org.fenggui.event.WindowClosedEvent;

import com.jme.image.Texture;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.math.Plane;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.flags.IFlagSelectionListener;
import edu.poly.bxmc.betaville.jme.fenggui.BottomProposals;
import edu.poly.bxmc.betaville.jme.fenggui.BottomVersions;
import edu.poly.bxmc.betaville.jme.fenggui.NavContainer;
import edu.poly.bxmc.betaville.jme.fenggui.CommentWindow;
import edu.poly.bxmc.betaville.jme.fenggui.NewProposalWindow;
import edu.poly.bxmc.betaville.jme.fenggui.TopSelectionWindow;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BetavilleXMLTheme;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengJMEInputHandler;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.SavableBetavilleWindow;
import edu.poly.bxmc.betaville.jme.intersections.MousePicking;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.module.GUIModule;
import edu.poly.bxmc.betaville.progress.ProgressContainer;

/**
 * Class <GUIGameState> - Create and Manage the GUI
 * 
 * @author <a href="mailto:skye.book@gmail.com">Skye Book</a>
 * @author <a href="mailto:bouchat.caroline@gmail.com">Caroline Bouchat</a>
 * @version 1.0 - Fall 2009
 */
public class GUIGameState extends GameState {
	private static Logger logger = Logger.getLogger(GUIGameState.class);
	// CONSTANTS
	/**
	 * Constant value for a label "Building Info and proposals" of the participatory context menu
	 */
	public static final String LABEL_PARTICIPATORY_CONTEXT_INFO = "BUILDING INFO\n& PROPOSALS";
	/**
	 * Constant value for a label "Create proposal" of the participatory context menu
	 */
	public static final String LABEL_PARTICIPATORY_CONTEXT_PROPOSE = "CREATE\nPROPOSAL";
	/**
	 * Constant value for a label "Talk" of the participatory context menu
	 */
	public static final String LABEL_PARTICIPATORY_CONTEXT_TALK = "TALK";
	/**
	 * Constant value for a label "Vote" of the participatory context menu
	 */
	public static final String LABEL_PARTICIPATORY_CONTEXT_VOTE = "VOTE";

	// OFFSETs
	public static final int OFFSET_BIG = 16;
	public static final int OFFSET_MEDIUM = 8;
	public static final int OFFSET_SMALL = 4;

	// STATIC ATTRIBUTES
	/**
	 * Attribute <disp> - The FengGUI display container
	 */
	private Display disp;
	/**
	 * Attribute <input> - The FengGUI input handler
	 */
	private FengJMEInputHandler input;

	// ATTRIBUTES
	/**
	 * Attribute <defaultTextureState> - Default TextureState used by FengGUI
	 */
	private TextureState defaultTextureState = null;


	// Theme
	/**
	 * Theme apply to the GUI
	 */
	private BetavilleXMLTheme theme;

	// Windows
	private TopSelectionWindow topWindow;
	private NavContainer navContainer;
	//private EditContainer editContainer;
	private CommentWindow commentWindow;
	private NewProposalWindow newProposalWindow;
	private BottomProposals bottomProposals;
	private BottomVersions bottomVersions;
	private ProgressContainer progressContainer;
	private boolean contextOn;
	private boolean previousRightClick;
	private boolean previousLeftClick;
	private static ArrayList<TextEditor> textEditors = new ArrayList<TextEditor>();
	private boolean textEntryMode = false;
	private boolean flagIsSelected=false;

	private List<GUIModule> modules;

	private UTMCoordinate location;

	// CONSTRUCTORS
	/**
	 * Constructor of the GUI GameState
	 */
	public GUIGameState(String name) {

		this.setName(name);

		modules = new ArrayList<GUIModule>();

		Texture defTex = TextureState.getDefaultTexture().createSimpleClone();
		defTex.setScale(new Vector3f(1, 1, 1));

		defaultTextureState = DisplaySystem.getDisplaySystem().getRenderer()
		.createTextureState();

		defaultTextureState.setTexture(defTex);

		buildUI();
	}

	// PRIVATE METHODS
	/**
	 * Method <buildUI> - Build the UI of the game
	 */
	private void buildUI() {
		try {
			GameTaskQueueManager.getManager().update(new Callable<Object>() {
				/* (non-Javadoc)
				 * @see java.util.concurrent.Callable#call()
				 */
				public Object call() throws Exception {
					Binding binding = new LWJGLBinding();
					Binding.getInstance().setUseClassLoader(true);

					disp = new Display(binding);
					input = new FengJMEInputHandler(disp);

					disp.addWidgetListChangedListener(new IWidgetListChangedListener() {

						public void widgetRemoved(Object sender,
								WidgetListChangedEvent widgetRemovedEvent) {
							for(IWidget w : widgetRemovedEvent.getWidget()){
								if(w instanceof Container) removeTextEditorsFromList((Container) w);
							}
						}

						public void widgetAdded(Object sender,
								WidgetListChangedEvent widgetAddedEvent) {
							for(IWidget w : widgetAddedEvent.getWidget()){
								if(w instanceof Container) addTextEditorsToList((Container) w);
							}
						}
					});

					// Creation of the themes
					theme = new BetavilleXMLTheme("data/themes/default/default.xml");
					FengGUI.AddTheme("default", theme);
					FengGUI.setTheme("default");

					//NetModelLoader.initDummies();

					topWindow = new TopSelectionWindow();
					topWindow.setXY(0, Binding.getInstance().getCanvasHeight()-topWindow.getHeight()+5);
					disp.addWidget(topWindow);

					commentWindow = FengGUI.createWidget(CommentWindow.class);
					commentWindow.finishSetup();
					commentWindow.addWindowClosedListener(new IWindowClosedListener() {
						public void windowClosed(WindowClosedEvent windowClosedEvent) {
							logger.debug("comment window closed");
							removeTextEditorsFromList(commentWindow);
						}
					});

					navContainer = new NavContainer();
					//editContainer = new EditContainer();

					newProposalWindow = FengGUI.createWidget(NewProposalWindow.class);
					newProposalWindow.finishSetup();
					newProposalWindow.setXY((disp.getWidth()/2)-(newProposalWindow.getWidth()/2),
							(disp.getHeight()/2)-(newProposalWindow.getHeight()/2));

					bottomProposals = new BottomProposals();
					bottomProposals.setXY(0, bottomProposals.getHeight()*-1);
					disp.addWidget(bottomProposals);

					bottomVersions = new BottomVersions();
					bottomVersions.setXY(bottomProposals.getWidth(), bottomVersions.getHeight()*-1);
					disp.addWidget(bottomVersions);
					
					progressContainer = FengGUI.createWidget(ProgressContainer.class);
					progressContainer.setXY(0, 0);
					disp.addWidget(progressContainer);

					logger.debug("FENGGUI VERSION: " + FengGUI.VERSION);

					disp.layout();
					return null;
				}
			}).get();
		} catch (Exception e){
			e.printStackTrace();
		}

		disp.layout();
		MouseInput.get().setCursorVisible(true);
	}

	private void rightClickAction(){
		if(!previousRightClick){
			/*
			if(editContainer.isInWidgetTree()){
				disp.removeWidget(editContainer);
				mousePickingEnabled=true;
			}
			else*/ if(!contextOn){

				FengUtils.setAtSafeMousePosition(navContainer);
				disp.addWidget(navContainer);
				contextOn = true;
			}
			else{
				disp.removeWidget(navContainer);
				contextOn = false;
			}
		}
	}

	private void leftClickAction(){
		/*
		 * Order of operations:
		 * 
		 * 1. The new proposal window gets precedence: If it
		 * is currently being displayed, then we see the current step
		 * and act appropriately
		 * 
		 * 2. Normal picking operation
		 * 		- check for flag pick
		 * 		- check for designNode pick
		 */

		if(!FengUtils.checkIfWithin(MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute(), TopSelectionWindow.logoMenuWindow)){
			TopSelectionWindow.removeLogoMenu();
		}
		
		if(previousLeftClick){
			return;
		}
		
		// Ray casting from mouse
		Vector2f screenPosition = new Vector2f(MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());
		Vector3f worldCoords = DisplaySystem.getDisplaySystem().getWorldCoordinates(screenPosition, 1.0f);
		Ray mouseRay = new Ray(SceneGameState.getInstance().getCamera().getLocation(),
				worldCoords.subtractLocal(SceneGameState.getInstance().getCamera().getLocation()));

		// Place red square
		Vector3f location = new Vector3f();
		if(mouseRay.intersectsWherePlane(new Plane(new Vector3f(0,1,0), 0), location)){
			SceneGameState.getInstance().placeGroundBoxSelector(new Vector3f(location.x, Scale.fromMeter(.1f), location.z));
		}
		else{
			SceneGameState.getInstance().removeGroundBox();
		}

		this.location = JME2MapManager.instance.betavilleToUTM(location);
		// Check if new proposal window is active
		if(newProposalWindow.isInWidgetTree()){
			// set the location if we are at step two and a model has not been loaded
			if(newProposalWindow.getCurrentStep()==2 && !newProposalWindow.isModelLoaded() && !newProposalWindow.isMakeRoomWindowActive()){
				newProposalWindow.setProposalLocation(JME2MapManager.instance.betavilleToUTM(location));
				this.location = JME2MapManager.instance.betavilleToUTM(location);
				return;
			}
		}

		MousePicking mp = new MousePicking(mouseRay);
		
		if(mp.widgetPicked()){
			// If a widget has been left-clicked we don't want to deselect anything
			return;
		}
		
		if(!mp.flagPicked() && flagIsSelected){
			for(IFlagSelectionListener listener : SceneScape.getFlagSelectionListeners()){
				listener.flagDeselected();
			}

			flagIsSelected=false;
		}

		if(mp.getPickedTerrainNode()==null){
			if(SceneScape.getSelectedTerrain()!=null) SceneScape.clearTerrainSelection();
		}
		else{
			SceneScape.setSelectedTerrain(mp.getPickedTerrainNode());
		}

		if(mp.flagPicked()){
			flagIsSelected=true;
			
			// split this into a list of IDs
			String[] flagIDs = mp.getNameOfClosestFlag().split(";");
			
			List<Design> designs = new ArrayList<Design>();
			for(String rootDesignID : flagIDs){
				Design d = SettingsPreferences.getCity().findDesignByID(Integer.parseInt(rootDesignID));
				if(d!=null) designs.add(d);
				else logger.error(rootDesignID+" could not be found");
			}

			
			for(IFlagSelectionListener listener : SceneScape.getFlagSelectionListeners()){
				listener.flagSelected(designs);
			}

			// EmptyDesigns don't appear in the scene, so we can't set them as target spatials
			/*
			 * NOTE: Commented out on August 15, 2011.  Won't work with multiple proposals on a flag,
			 * and I'm not sure that this was necessarily doing anything in the first place
			if(!(d instanceof EmptyDesign)){
				SceneScape.setTargetSpatial(mp.getDesignFromFlag());
			}
			*/
		}
		else if(mp.getPickedDesignNode()!=null){
			SceneScape.setTargetSpatial(mp.getPickedDesignNode());
		}
		else{
			SceneScape.clearTargetSpatial();
			SceneGameState.getInstance().placeGroundBoxSelector(new Vector3f(location.x, Scale.fromMeter(.1f), location.z));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jmex.game.state.BasicGameState#cleanup()
	 */
	public void cleanup() {
	}

	/**
	 * Gets the display
	 * @return the display
	 */
	public Display getDisp() {
		return disp;
	}

	/**
	 * Adds all of the <code>TextEditor</code> objects in the supplied container
	 * to a list which is checked on update for being in an active/writable state.
	 * This functionality serves to prevent information input by the user from being
	 * construed as movement [or other in-world] commands.
	 * This method should be called whenever a new object is added to the display.
	 * @param container <code>Container</code> to add.
	 */
	private void addTextEditorsToList(Container container){
		if(container.getChildWidgetCount()==0) return;
		for(int i=0; i<container.getContent().size(); i++){
			if(container.getContent().get(i) instanceof TextEditor){
				textEditors.add((TextEditor)container.getContent().get(i));
			}
			else if(container.getContent().get(i) instanceof Container){
				addTextEditorsToList((Container)container.getContent().get(i));
			}
		}
	}

	/**
	 * Removes all of the <code>TextEditor</code> objects in the supplied container
	 * from a list which is checked on update for being in an active/writable state.
	 * This functionality serves to prevent information input by the user from being
	 * construed as movement [or other in-world] commands.
	 * This method should be called whenever a new object is removed the display.
	 * @param container <code>Container</code> to remove.
	 */
	private void removeTextEditorsFromList(Container container){
		if(container.getChildWidgetCount()==0) return;
		for(int i=0; i<container.getContent().size(); i++){
			if(container.getContent().get(i) instanceof TextEditor){
				textEditors.remove((TextEditor)container.getContent().get(i));
			}
			else if(container.getContent().get(i) instanceof Container){
				removeTextEditorsFromList((Container)container.getContent().get(i));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jmex.game.state.BasicGameState#render(float)
	 */
	public void render(float tpf) {
		// set a default TextureState, this is needed to not let FengGUI inherit
		// wrong Texture coordinates and stuff.
		for (RenderState r : Renderer.defaultStateList)
			r.apply();
		defaultTextureState.apply();

		disp.display();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jmex.game.state.BasicGameState#update(float)
	 */
	public void update(float tpf) {
		input.update(tpf);

		if(disp.getPopupWidget()!=null){
			disp.bringToFront(disp.getPopupWidget());
		}

		for(GUIModule module : modules){
			module.update();
		}

		if(MouseInput.get().isButtonDown(0) && withinClickBounds()  && !KeyInput.get().isKeyDown(KeyInput.KEY_LMENU)
				&& !KeyInput.get().isKeyDown(KeyInput.KEY_RMENU)){
			leftClickAction();
			previousLeftClick = true;
		}
		else previousLeftClick=false;


		if(MouseInput.get().isButtonDown(1)){
			rightClickAction();
			previousRightClick = true;
		}
		else previousRightClick=false;

		checkEditorStatus();
	}

	private boolean withinClickBounds(){
		int x = MouseInput.get().getXAbsolute();
		int y = MouseInput.get().getYAbsolute();
		for(IWidget w : disp.getWidgets()){
			if(y>w.getY() && y<w.getY()+w.getSize().getHeight()
					&& x>w.getX() && x<w.getX()+w.getSize().getWidth()){
				return false;
			}
		}
		return true;
	}

	private void checkEditorStatus(){
		boolean currentState = false;


		// If any of the text editors are on, then 'currentState' is true
		for(int i=0; i<textEditors.size(); i++){
			if(textEditors.get(i).isInWritingState()){
				currentState=true;
				break;
			}
		}

		// If any of the text editors or on, we need to turn off the jME Controller
		// used by SceneGameState to control the camera
		if(currentState){
			// jME will crash if we try to add a controller that's already in the tree,
			// so we keep track of whether or not its already there
			if(!textEntryMode){
				((SceneGameState)GameStateManager.getInstance().getChild("sceneGameState")).detatchSceneController();
				textEntryMode=true;
				return;
			}
		}
		else if(textEntryMode){
			((SceneGameState)GameStateManager.getInstance().getChild("sceneGameState")).reattachSceneController();
			textEntryMode=false;
		}
	}

	public void resetProposalWindow(){
		newProposalWindow.resetForNewProposal();
		disp.addWidget(newProposalWindow);
		addTextEditorsToList(newProposalWindow);
		newProposalWindow.preload(SceneScape.getPickedDesign());
	}

	public boolean isProposalWindowOn(){
		return newProposalWindow.isInWidgetTree();
	}

	public void setContextOn(boolean on){
		contextOn=on;
	}

	/*
	public void removeEditMenu(){
		disp.removeWidget(editContainer);
	}
	 */

	public void removeNavMenu(){
		disp.removeWidget(navContainer);
	}

	/*
	public void turnOnEditContainer(){
		disp.addWidget(editContainer);
		editContainer.setXY(MouseInput.get().getXAbsolute()-(editContainer.getWidth()/2), MouseInput.get().getYAbsolute()-(editContainer.getHeight()/2));
		mousePickingEnabled=false;
	}
	 */

	public CommentWindow getCommentWindow() {
		return commentWindow;
	}

	public TopSelectionWindow getTopSelectionWindow() {
		return topWindow;
	}

	public UTMCoordinate getCurrentTerrainSelection(){
		return location;
	}

	public void showCommentWindow(){
		if(!commentWindow.isInWidgetTree()){
			disp.addWidget(commentWindow);
			addTextEditorsToList(commentWindow);
			if(!SceneScape.isTargetSpatialEmpty() && !SceneScape.isTargetSpatialLocal()) commentWindow.setCurrentDesign(SceneScape.getPickedDesign().getID());
		}
	}

	public void forceSelectionWindowUpdate(){
		topWindow.forceUpdate();
	}

	public BottomVersions getVersionsWindow(){
		return bottomVersions;
	}
	
	public BottomProposals getProposalsWindow(){
		return bottomProposals;
	}
	
	public ProgressContainer getProgressContainer(){
		return progressContainer;
	}

	public void forceFocus(IWidget w, boolean focusOn){
		for(IWidget child : disp.getWidgets()){
			if(child!=w) child.setEnabled(!focusOn);
		}
	}

	public void addModuleToUpdateList(GUIModule module){
		modules.add(module);
	}

	public void removeModuleFromUpdateList(GUIModule module){
		modules.remove(module);
	}
	
	/**
	 * Writes any available GUI preferences starting
	 * with FengGUI's top-level {@link Display}
	 * container.
	 */
	public void writeGUIPreferences(){
		writeGUIPreferences(disp);
	}
	
	/**
	 * Searches for instances of {@link SavableBetavilleWindow}
	 * for which to write out any available preferences
	 * @param w
	 */
	public void writeGUIPreferences(IWidget w){
		if(w instanceof SavableBetavilleWindow) ((SavableBetavilleWindow)w).writeOptions();
		if(w instanceof Container){
			if(((Container)w).hasChildWidgets()){
				for(IWidget child : ((Container)w).getContent()){
					writeGUIPreferences(child);
				}
			}
		}
	}

	public static GUIGameState getInstance(){
		return (GUIGameState)GameStateManager.getInstance().getChild("guiGameState");
	}
}