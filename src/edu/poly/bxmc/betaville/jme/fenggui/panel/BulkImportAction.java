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
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.gui.ColladaFileFilter;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.BulkLoader;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * @author Skye Book
 *
 */
public class BulkImportAction extends PanelAction {
	private static final Logger logger = Logger.getLogger(BulkImportAction.class);

	/**
	 * @param name
	 * @param description
	 * @param buttonTitle
	 * @param ruleToSet
	 * @param minimumUserLevel
	 * @param listener
	 */
	public BulkImportAction() {
		super("Bulk Import", "Imports multiple 3D objects", "Bulk Import", AvailabilityRule.NO_OBJECT_SELECTED, UserType.BASE_COMMITTER,
				new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				SettingsPreferences.getThreadPool().submit(new Runnable() {

					public void run() {
						JDialog dialog = new JDialog();
						dialog.setModalityType(ModalityType.APPLICATION_MODAL);
						JFileChooser fileChooser = new JFileChooser(SettingsPreferences.BROWSER_LOCATION);
						fileChooser.setFileFilter(new ColladaFileFilter());
						fileChooser.setMultiSelectionEnabled(true);
						if(fileChooser.showOpenDialog(dialog)==JFileChooser.APPROVE_OPTION){
							final File[] files = fileChooser.getSelectedFiles();
							logger.info(files.length + " files selected");
							SettingsPreferences.BROWSER_LOCATION = fileChooser.getCurrentDirectory();

							final BulkLoader bl = new BulkLoader(JME2MapManager.instance.betavilleToUTM(SceneGameState.getInstance().getGroundSelectorLocation()), new BulkLoader.IBulkLoadProgressListener() {

								int totalFiles = 0;

								public void modelUploadStarted(int currentFile) {}

								public void modelUploadCompleted(int currentFile, boolean success) {}

								public void modelParsed(int currentFile) {
									logger.info("File " +currentFile+" parsed");
								}

								public void modelMovedToLocation(int currentFile, ILocation originalLocation, ILocation correctedLocation) {
									logger.info("Model "+currentFile+" corrected from " + originalLocation.toString() +" to " + correctedLocation.toString());
								}

								public void modelLoadStarting(String filename, int currentFile,
										int totalNumberFiles) {
									totalFiles = totalNumberFiles;
									logger.info("Loading model " + currentFile + " of " + totalFiles + "\t-\t"+filename);
								}
							});

							SettingsPreferences.getThreadPool().submit(new Runnable() {

								public void run() {
									ArrayList<File> fileList = new ArrayList<File>();
									for(File f : files){
										fileList.add(f);
									}
									bl.load(fileList);	
								}
							});			
						}
					}
				});
			}
		});
	}

}
