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

import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;
import org.jdom.JDOMException;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.gui.XMLFileFilter;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BlockingScrollContainer;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.plugin.Plugin;
import edu.poly.bxmc.betaville.plugin.PluginConfigReader;
import edu.poly.bxmc.betaville.plugin.PluginConfigReader.PluginParsedCallback;
import edu.poly.bxmc.betaville.plugin.PluginEntry;
import edu.poly.bxmc.betaville.plugin.PluginEntry.RemoveCallback;
import edu.poly.bxmc.betaville.plugin.PluginManager;

/**
 * A break-out point from which to perform activities delegated to a specific user-role
 * @author Skye Book
 *
 */
public class PluginManagerUI extends Window implements IBetavilleWindow, IPanelOnScreenAwareWindow{
	private static Logger logger = Logger.getLogger(PluginManagerUI.class);
	private int targetWidth= 300;
	private int targetHeight = 200;
	
	private BlockingScrollContainer sc;
	private Container isc;
	
	private Container list;
	
	public PluginManagerUI(){
		super(true, true);
		internalSetup();
	}
	
	private void internalSetup(){
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		sc = FengGUI.createWidget(BlockingScrollContainer.class);
		sc.setSize(targetWidth, targetHeight);
		sc.setShowScrollbars(true);
		isc = FengGUI.createWidget(Container.class);
		isc.setLayoutManager(new RowExLayout(false));
		sc.setInnerWidget(isc);
		sc.layout();
		
		list = FengGUI.createWidget(Container.class);
		list.setLayoutManager(new RowExLayout(false));
		
		Button addPluginFromWeb = FengGUI.createWidget(Button.class);
		addPluginFromWeb.setText("Add Plugin From Web");
		addPluginFromWeb.addButtonPressedListener(new LaunchLoadFromWebWindowDelegate());
		
		
		getContentContainer().addWidget(list, addPluginFromWeb);
		
		updatePluginList();
	}
	
	private void updatePluginList(){
		for(Plugin plugin : PluginManager.getList()){
			PluginEntry entry = FengGUI.createWidget(PluginEntry.class);
			entry.setPlugin(plugin.getClass().getName());
			//isc.addWidget(entry);
			list.addWidget(entry);
			entry.setRemoveCallback(new RemoveCallback() {
				
				@Override
				public void onPluginRemoval(PluginEntry pluginEntry) {
					//isc.removeWidget(pluginEntry);
					list.removeWidget(pluginEntry);
				}
			});
		}
		
		layout();
	}
	
	public void finishSetup(){
		setTitle("Plugin Manager");
		setSize(targetWidth, targetHeight);
	}
	
	private class LaunchLoadFromWebWindowDelegate implements IButtonPressedListener{
		
		/* (non-Javadoc)
		 * @see org.fenggui.event.IButtonPressedListener#buttonPressed(java.lang.Object, org.fenggui.event.ButtonPressedEvent)
		 */
		@Override
		public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
			Window window = createLoadFromWebWindow();
			FengUtils.putAtMiddleOfScreen(window);
		}
	}
	
	private class LoadPluginFromWebDelegate implements IButtonPressedListener{
		
		private TextEditor destination;
		
		private LoadPluginFromWebDelegate(TextEditor destinationEditor){
			destination = destinationEditor;
		}
		
		/* (non-Javadoc)
		 * @see org.fenggui.event.IButtonPressedListener#buttonPressed(java.lang.Object, org.fenggui.event.ButtonPressedEvent)
		 */
		@Override
		public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
			
			SettingsPreferences.getGUIThreadPool().submit(new Runnable() {
				
				public void run() {
					JDialog dialog = new JDialog();
					dialog.setModalityType(ModalityType.APPLICATION_MODAL);
					JFileChooser fileChooser = new JFileChooser(SettingsPreferences.BROWSER_LOCATION);
					fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
					fileChooser.addChoosableFileFilter(new XMLFileFilter());
					fileChooser.showOpenDialog(dialog);
					File xmlFile = fileChooser.getSelectedFile();
					destination.setText(xmlFile.toString());
					logger.debug("Web plugin file selected: " + xmlFile.toString());
					dialog.dispose();
				}
			});
		}
	}
	
	private Window createLoadFromWebWindow(){
		final Window w = FengGUI.createWindow(true, true);
		w.getContentContainer().setLayoutManager(new RowExLayout(false));
		w.setSize(275, 175);
		w.setTitle("Load Plugin From Web");
		
		Container xmlContainer = FengGUI.createWidget(Container.class);
		xmlContainer.setLayoutManager(new RowExLayout(true));
		
		final TextEditor xmlEditor = FengGUI.createWidget(TextEditor.class);
		xmlEditor.setEmptyText("Location of plugin configuration");
		xmlEditor.setLayoutData(new RowExLayoutData(true, true));
		
		Button browse = FengGUI.createWidget(Button.class);
		browse.setText("Browse..");
		browse.setLayoutData(new RowExLayoutData(true, false));
		browse.addButtonPressedListener(new LoadPluginFromWebDelegate(xmlEditor));
		
		xmlContainer.addWidget(xmlEditor, browse);
		
		Button load = FengGUI.createWidget(Button.class);
		load.setText("Load");
		load.addButtonPressedListener(new IButtonPressedListener() {
			
			@Override
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				try {
					logger.info("Attempting to load plugin from "+FengUtils.getText(xmlEditor));
					PluginConfigReader reader = new PluginConfigReader(new File(FengUtils.getText(xmlEditor)));
					reader.addCallback(new PluginParsedCallback() {
						
						@Override
						public void onPluginParsed(String name, String description, String author,
								String classname) {
							updatePluginList();
							w.close();
						}
					});
					reader.parse();
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		w.getContentContainer().addWidget(xmlContainer, load);
		
		return w;
	}

	@Override
	public void panelTurnedOn() {
		updatePluginList();
	}
}
