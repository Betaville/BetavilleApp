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
import java.net.MalformedURLException;

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

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.gui.JARFileFilter;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BlockingScrollContainer;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.plugin.IncorrectPluginTypeException;
import edu.poly.bxmc.betaville.plugin.Plugin;
import edu.poly.bxmc.betaville.plugin.PluginAlreadyLoadedException;
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
	
	private Button addPluginFromFile;
	

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
		
		addPluginFromFile = FengGUI.createWidget(Button.class);
		addPluginFromFile.setText("Add Plugin From File");
		addPluginFromFile.addButtonPressedListener(new LaunchLoadFromFileWindowDelegate());
		
		
		getContentContainer().addWidget(list, addPluginFromFile);
		
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
	
	private class LaunchLoadFromFileWindowDelegate implements IButtonPressedListener{

		/* (non-Javadoc)
		 * @see org.fenggui.event.IButtonPressedListener#buttonPressed(java.lang.Object, org.fenggui.event.ButtonPressedEvent)
		 */
		@Override
		public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
			Window window = createLoadFromFileWindow();
			FengUtils.putAtMiddleOfScreen(window);
		}
	}
	
	private class LoadPluginFromFileDelegate implements IButtonPressedListener{
		
		private TextEditor destination;
		
		private LoadPluginFromFileDelegate(TextEditor destinationEditor){
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
					fileChooser.addChoosableFileFilter(new JARFileFilter());
					fileChooser.showOpenDialog(dialog);
					File jarFile = fileChooser.getSelectedFile();
					destination.setText(jarFile.toString());
					dialog.dispose();
				}
			});
		}
	}
	
	private Window createLoadFromFileWindow(){
		final Window w = FengGUI.createWindow(true, true);
		w.getContentContainer().setLayoutManager(new RowExLayout(false));
		w.setSize(275, 175);
		w.setTitle("Load Plugin From File");
		
		Container jarContainer = FengGUI.createWidget(Container.class);
		jarContainer.setLayoutManager(new RowExLayout(true));
		
		final TextEditor jarEditor = FengGUI.createWidget(TextEditor.class);
		jarEditor.setEmptyText("Location of plugin JAR");
		jarEditor.setLayoutData(new RowExLayoutData(true, true));
		
		Button browse = FengGUI.createWidget(Button.class);
		browse.setText("Browse..");
		browse.setLayoutData(new RowExLayoutData(true, false));
		browse.addButtonPressedListener(new LoadPluginFromFileDelegate(jarEditor));
		
		jarContainer.addWidget(jarEditor, browse);
		
		final TextEditor className = FengGUI.createWidget(TextEditor.class);
		className.setLayoutData(new RowExLayoutData(true, false));
		className.setEmptyText("Put the qualified classname here");
		
		Button load = FengGUI.createWidget(Button.class);
		load.setText("Load");
		load.addButtonPressedListener(new IButtonPressedListener() {
			
			@Override
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				// both fields need to be filled in
				if(FengUtils.getText(jarEditor).isEmpty() || FengUtils.getText(className).isEmpty()){
					FengUtils.showNewDismissableWindow("Betaville", "Please ensure that both fields are filled in", "OK", true);
					return;
				}
				
				try {
					PluginManager.loadPlugin(new File(FengUtils.getText(jarEditor)).toURI().toURL(), FengUtils.getText(className));
					updatePluginList();
					w.close();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PluginAlreadyLoadedException e) {
					FengUtils.showNewDismissableWindow("Plugin Manager", e.getMessage(), "OK", true);
					logger.error("Plugin has been previously loaded", e);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IncorrectPluginTypeException e) {
					FengUtils.showNewDismissableWindow("Plugin Manager", "This plugin appears to be of the incorrect type", "OK", true);
					logger.error("This plugin appears to be of the incorrect type", e);
				}
			}
		});
		
		w.getContentContainer().addWidget(jarContainer, className, load);
		
		return w;
	}

	@Override
	public void panelTurnedOn() {
		updatePluginList();
	}
}
