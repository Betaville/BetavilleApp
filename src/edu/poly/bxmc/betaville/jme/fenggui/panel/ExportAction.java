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
import org.fenggui.ComboBox;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.xml.XMLExporter;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.gui.ColladaFileFilter;
import edu.poly.bxmc.betaville.gui.FileExtensionFileFilter;
import edu.poly.bxmc.betaville.gui.JMEFileFilter;
import edu.poly.bxmc.betaville.gui.JMEXMLFileFilter;
import edu.poly.bxmc.betaville.gui.WavefrontFileFilter;
import edu.poly.bxmc.betaville.jme.exporters.ColladaExporter;
import edu.poly.bxmc.betaville.jme.exporters.OBJExporter;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * @author Skye Book
 *
 */
public class ExportAction extends PanelAction {
	private static final Logger logger = Logger.getLogger(ExportAction.class);

	private static FileExtensionFileFilter[] filters;
	static{
		filters = new FileExtensionFileFilter[]{
				new ColladaFileFilter(),
				new WavefrontFileFilter(),
				new JMEFileFilter(),
				new JMEXMLFileFilter()
		};
	}

	private Window window;
	private ComboBox exportSelector;

	private static final String OSM = "OpenStreetMap Geometry";
	private static final String SELECTED = "Selected Object";

	/**
	 * @param name
	 * @param description
	 * @param buttonTitle
	 * @param ruleToSet
	 * @param minimumUserLevel
	 * @param listener
	 */
	public ExportAction() {
		super(Labels.generic("export"), "Exports a 3D object", "Export", AvailabilityRule.OBJECT_SELECTED, UserType.MODERATOR, null);

		getButton().addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(!window.isInWidgetTree()) GUIGameState.getInstance().getDisp().addWidget(window);
			}
		});


		// create window
		window = FengGUI.createWindow(true, true);
		window.setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		window.setSize(125, 75);
		window.getContentContainer().setLayoutManager(new RowExLayout(false));

		exportSelector = FengGUI.createWidget(ComboBox.class);
		exportSelector.addItem(OSM);
		exportSelector.addItem(SELECTED);
		exportSelector.setLayoutData(new RowExLayoutData(true, true));
		exportSelector.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(Object arg0, SelectionChangedEvent arg1) {
				if(exportSelector.getSelectedValue().equals(SELECTED) && SceneScape.isTargetSpatialEmpty()){
					GUIGameState.getInstance().getDisp().addWidget(
							FengUtils.createDismissableWindow("Exporter", "An object must be selected to export", Labels.get("Generic.ok"), true)
					);
				}
			}
		});

		Label l = FengGUI.createWidget(Label.class);
		l.setText(Labels.get(this.getClass().getSimpleName()+".select_target"));
		l.setLayoutData(new RowExLayoutData(true, true));

		Button go = FengGUI.createWidget(Button.class);
		go.setText(Labels.get(this.getClass().getSimpleName()+".export"));
		go.setLayoutData(new RowExLayoutData(true, true));
		go.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				SettingsPreferences.getThreadPool().submit(new Runnable() {

					JFileChooser fileChooser;
					File file;
					Spatial toUse;

					public void run() {
						JDialog dialog = new JDialog();
						dialog.setModalityType(ModalityType.APPLICATION_MODAL);
						fileChooser = new JFileChooser(SettingsPreferences.BROWSER_LOCATION);
						for(FileExtensionFileFilter filter : filters){
							fileChooser.addChoosableFileFilter(filter);
						}
						if(fileChooser.showSaveDialog(dialog)==JFileChooser.APPROVE_OPTION){
							logger.info("Save requested using " + fileChooser.getFileFilter().getDescription());
							file = fileChooser.getSelectedFile();

							// adds an extension if one isn't there
							takeCareOfFileExtension();

							if(exportSelector.getSelectedValue().equals(SELECTED)) toUse = SceneScape.getTargetSpatial();
							else if(exportSelector.getSelectedValue().equals(OSM)) toUse = SceneGameState.getInstance().getGISNode();

							logger.info("EXPORTING OBJECT WITH HIERARCHY:");
							GeometryUtilities.printInformation(logger, toUse, true, false, false, false);

							logger.info("Selected File: " + file.toString());
							SettingsPreferences.BROWSER_LOCATION = fileChooser.getCurrentDirectory();
							try {
								if(fileChooser.getFileFilter() instanceof ColladaFileFilter){
									// export COLLADA
									ColladaExporter exporter = new ColladaExporter(file, toUse, true);
									exporter.writeData();
									logger.info("COLLADA file written to " + file.toString());
								}
								else if(fileChooser.getFileFilter() instanceof WavefrontFileFilter){
									OBJExporter exporter = new OBJExporter(toUse, file);
									logger.info("OBJ file written to " + file.toString());
								}
								else if(fileChooser.getFileFilter() instanceof JMEFileFilter){
									BinaryExporter.getInstance().save(toUse, file);
									logger.info("JME file written to " + file.toString());
								}
								else if(fileChooser.getFileFilter() instanceof JMEXMLFileFilter){
									XMLExporter.getInstance().save(toUse, file);
									logger.info("JME-XML file written to " + file.toString());
								}
								else{
									logger.error("NO FILE FILTER SELECTED!");
								}
							} catch (IOException e) {
								logger.error("Exception Occurred When Attempting To Write File", e);
							}
						}
					}

					private void takeCareOfFileExtension(){
						// check to see if one of the available extensions has already been used
						boolean extensionSet = false;
						for(String ext : ((FileExtensionFileFilter)fileChooser.getFileFilter()).getFileExtensions()){
							if(file.toString().endsWith(ext)) extensionSet = true;
						}

						// if an extension hasn't been set, use the first available extension
						if(!extensionSet){
							file = new File(file.toString()+"."+
									((FileExtensionFileFilter)fileChooser.getFileFilter()).getFileExtensions()[0]);
							logger.info("An extension was not set, so one has been added to the filename: " + file.toString());
						}
					}
				});
			}
		});
		window.getContentContainer().addWidget(l, exportSelector, go);
	}

}
