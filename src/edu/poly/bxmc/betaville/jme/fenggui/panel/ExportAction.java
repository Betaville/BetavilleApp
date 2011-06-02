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
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.gui.ColladaFileFilter;
import edu.poly.bxmc.betaville.gui.FileExtensionFileFilter;
import edu.poly.bxmc.betaville.gui.WavefrontFileFilter;
import edu.poly.bxmc.betaville.jme.exporters.ColladaExporter;
import edu.poly.bxmc.betaville.jme.exporters.OBJExporter;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * @author Skye Book
 *
 */
public class ExportAction extends PanelAction {
	private static final Logger logger = Logger.getLogger(ExportAction.class);
	
	private static ColladaFileFilter colladaFilter = new ColladaFileFilter();
	
	private static WavefrontFileFilter objFilter = new WavefrontFileFilter();

	/**
	 * @param name
	 * @param description
	 * @param buttonTitle
	 * @param ruleToSet
	 * @param minimumUserLevel
	 * @param listener
	 */
	public ExportAction() {
		super("Export", "Exports a 3D object", "Export", AvailabilityRule.OBJECT_SELECTED, UserType.MODERATOR,
				new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				SettingsPreferences.getThreadPool().submit(new Runnable() {
					
					JFileChooser fileChooser;
					File file;

					public void run() {
						JDialog dialog = new JDialog();
						dialog.setModalityType(ModalityType.APPLICATION_MODAL);
						fileChooser = new JFileChooser(SettingsPreferences.BROWSER_LOCATION);
						fileChooser.addChoosableFileFilter(colladaFilter);
						fileChooser.addChoosableFileFilter(objFilter);
						if(fileChooser.showSaveDialog(dialog)==JFileChooser.APPROVE_OPTION){
							logger.info("Save requested using " + fileChooser.getFileFilter().getDescription());
							file = fileChooser.getSelectedFile();
							
							// adds an extension if one isn't there
							takeCareOfFileExtension();
							
							logger.info("Selected File: " + file.toString());
							SettingsPreferences.BROWSER_LOCATION = fileChooser.getCurrentDirectory();
							try {
								if(fileChooser.getFileFilter().equals(colladaFilter)){
									// export COLLADA
									ColladaExporter exporter = new ColladaExporter(file, SceneScape.getTargetSpatial(), true);
									exporter.writeData();
									logger.info("COLLADA file written to " + file.toString());
								}
								else{
									new OBJExporter(SceneScape.getTargetSpatial(), file);
									logger.info("OBJ file written to " + file.toString());
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
							file = new File(file.toString()+"."+((FileExtensionFileFilter)fileChooser.getFileFilter()).getFileExtensions()[0]);
							logger.info("An extension was not set, so one has been added to the filename: " + file.toString());
						}
					}
				});
			}
		});
	}

}
