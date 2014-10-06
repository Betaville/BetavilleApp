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

import java.io.File;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.RadioButton;
import org.fenggui.ToggableGroup;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.IWindowClosedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.WindowClosedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import edu.poly.bxmc.betaville.CacheManager;
import edu.poly.bxmc.betaville.ICacheModifiedListener;
import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;

/**
 * @author Skye Book
 *
 */
public class CacheManagerUI extends Window implements IBetavilleWindow, IPanelOnScreenAwareWindow{
	private static final Logger logger = Logger.getLogger(CacheManagerUI.class);

	private Container selectorContainer;
	private ToggableGroup<RadioButton<Boolean>> togglableGroup;
	private RadioButton<Boolean> jmeCache;
	private RadioButton<Boolean> osmCache;

	private Container jmeContainer;
	private Label size;
	private Label numberOfFiles;

	private Container osmContainer;

	private ICacheModifiedListener modelCacheModifiedListener;

	/**
	 * 
	 */
	public CacheManagerUI(){
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false, 10));

		togglableGroup = new ToggableGroup<RadioButton<Boolean>>();
		jmeCache = FengGUI.createRadioButton();
		jmeCache.setLayoutData(new RowExLayoutData(true, true));
		jmeCache.setRadioButtonGroup(togglableGroup);
		jmeCache.setText("Models");

		osmCache = FengGUI.createRadioButton();
		osmCache.setLayoutData(new RowExLayoutData(true, true));
		osmCache.setRadioButtonGroup(togglableGroup);
		osmCache.setText("Map Tiles");

		selectorContainer = FengGUI.createWidget(Container.class);
		selectorContainer.setLayoutManager(new RowExLayout(true));

		jmeCache.setSelected(true);
		selectorContainer.addWidget(jmeCache/*, osmCache*/);

		createJMEContainer();
		createOSMContainer();

		getContentContainer().addWidget(selectorContainer, jmeContainer);

		togglableGroup.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(Object arg0, SelectionChangedEvent arg1) {
				getContentContainer().removeAllWidgets();

				getContentContainer().addWidget(selectorContainer);
				// add the widgets based on the selection
				if(togglableGroup.getSelectedItem().equals(jmeCache)){
					getContentContainer().addWidget(jmeContainer);
				}
				else if(togglableGroup.getSelectedItem().equals(osmCache)){
					getContentContainer().addWidget(osmContainer);
				}

				layout();

			}
		});
	}

	private void createJMEContainer(){
		jmeContainer = FengGUI.createWidget(Container.class);
		jmeContainer.setLayoutManager(new RowExLayout(false, 5));


		Button clearCache = FengGUI.createWidget(Button.class);
		clearCache.setText("Clear");
		clearCache.addButtonPressedListener(new IButtonPressedListener() {

			@Override
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				// warn the user, only clear the cache if they accept
				Window deleteConfirmation = FengUtils.createTwoOptionWindow(
						"Betaville",
						"Deleting the cache will mean longer startup times the next time you run Betaville. Are you sure you wish to proceed?",
						Labels.get("Generic.yes"), Labels.get("Generic.no"),
						// The "Yes" listener
						new IButtonPressedListener() {

							@Override
							public void buttonPressed(Object source, ButtonPressedEvent e) {
								logger.info("User has requested model cache deletion");
								// clear the cache
								CacheManager.getCacheManager().deleteAllFiles();
								// let the user know what just happened
								FengUtils.showNewDismissableWindow("Betaville", "Model cache has been deleted", Labels.get("Generic.ok"), true);
							}
						},
						// The "No" listener
						new IButtonPressedListener() {

							@Override
							public void buttonPressed(Object source, ButtonPressedEvent e) {
								logger.debug("The user has declined to delete the model cache after being prompted");
							}
						}, true, true
				);
				GUIGameState.getInstance().getDisp().addWidget(deleteConfirmation);
			}
		});

		size = FengGUI.createWidget(Label.class);
		numberOfFiles = FengGUI.createWidget(Label.class);

		forceUpdate();


		modelCacheModifiedListener = new ICacheModifiedListener() {

			@Override
			public void fileRemoved(File file, long newCacheSize) {
				logger.info("removed");
				size.setText("Cache Size: " + newCacheSize);
				numberOfFiles.setText("Number Of Files: " + CacheManager.getCacheManager().getNumberOfFiles());
			}

			@Override
			public void fileAdded(File file, long newCacheSize) {
				size.setText("Cache Size: " + newCacheSize);
				numberOfFiles.setText("Number Of Files: " + CacheManager.getCacheManager().getNumberOfFiles());
			}
		};

		addWindowClosedListener(new IWindowClosedListener() {

			@Override
			public void windowClosed(WindowClosedEvent windowClosedEvent) {
				CacheManager.getCacheManager().removeCacheModifiedListener(modelCacheModifiedListener);
			}
		});


		jmeContainer.addWidget(size, numberOfFiles, clearCache);
	}

	private void forceUpdate(){
		logger.info("Forcing update");
		size.setText("Cache Size: " + CacheManager.getCacheManager().getSizeOfCache());
		numberOfFiles.setText("Number Of Files: " + CacheManager.getCacheManager().getNumberOfFiles());
	}

	private void createOSMContainer(){
		osmContainer = FengGUI.createWidget(Container.class);
		osmContainer.setLayoutManager(new RowExLayout(false));
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	@Override
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.panel.IPanelOnScreenAwareWindow#panelTurnedOn()
	 */
	@Override
	public void panelTurnedOn() {
		forceUpdate();
		CacheManager.getCacheManager().addCacheModifiedListener(modelCacheModifiedListener);
	}

}
