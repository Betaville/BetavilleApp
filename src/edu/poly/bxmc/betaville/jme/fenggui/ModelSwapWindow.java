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

import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowLayout;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.gui.AcceptedModelFilter;
import edu.poly.bxmc.betaville.gui.ColladaFileFilter;
import edu.poly.bxmc.betaville.gui.WavefrontFileFilter;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.fenggui.listeners.ReportBugListener;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.net.PhysicalFileTransporter;

/**
 * @author Skye Book
 *
 */
public class ModelSwapWindow extends Window implements IBetavilleWindow {
	private static Logger logger = Logger.getLogger(ModelSwapWindow.class);

	private int targetWidth=300;
	private int targetHeight=200;

	private URL model = null;
	private TextEditor modelEditor;
	private FixedButton browseButton;
	private Label textureNotifier;
	private ComboBox textureSelect;
	private Container textureContainer;
	private FixedButton applyButton;

	private String modelEditorText = "Model File...";

	/**
	 * 
	 */
	public ModelSwapWindow() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));

		modelEditor = FengGUI.createWidget(TextEditor.class);
		modelEditor.setReadonly(true);
		modelEditor.setText(modelEditorText);

		browseButton = FengGUI.createWidget(FixedButton.class);
		browseButton.setText("Browse...");
		browseButton.addButtonPressedListener(new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e){

				SettingsPreferences.getThreadPool().submit(new Runnable() {

					public void run() {
						JDialog dialog = new JDialog();
						dialog.setModalityType(ModalityType.APPLICATION_MODAL);
						JFileChooser fileChooser = new JFileChooser(SettingsPreferences.BROWSER_LOCATION);
						AcceptedModelFilter modelFilter = new AcceptedModelFilter();
						fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
						fileChooser.addChoosableFileFilter(modelFilter);
						fileChooser.addChoosableFileFilter(new ColladaFileFilter());
						fileChooser.addChoosableFileFilter(new WavefrontFileFilter());
						fileChooser.setFileFilter(modelFilter);
						fileChooser.showOpenDialog(dialog);
						File file = fileChooser.getSelectedFile();

						// flash an error if the file is larger than 5mb
						if(file.length()>5000000){
							logger.warn(file.toString()+" is "+file.length()+"bytes.  This is rather large");

							GUIGameState.getInstance().getDisp().addWidget(
									FengUtils.createDismissableWindow("Betaville", "The selected file is rather large, at "+
											(file.length()/1000000f)+"MB, why don't you see if you can't get that down a bit?", "ok", false));

						}

						String pathToDisplay = file.toString();
						if(pathToDisplay.contains("/")){
							pathToDisplay = pathToDisplay.substring(pathToDisplay.lastIndexOf("/")+1);
						}
						else if(pathToDisplay.contains("\\")){
							pathToDisplay = pathToDisplay.substring(pathToDisplay.lastIndexOf("\\")+1);
						}
						SettingsPreferences.BROWSER_LOCATION = fileChooser.getCurrentDirectory();
						logger.info("path "+pathToDisplay);
						modelEditor.setText(pathToDisplay);
						try {
							model = file.toURI().toURL();
						} catch (MalformedURLException e) {
							logger.warn("Problem occured when selecting from file browser", e);
						}
					}
				});

			}
		});

		textureNotifier = FengGUI.createWidget(Label.class);
		textureNotifier.setText("Texture Info");

		textureSelect = FengGUI.createWidget(ComboBox.class);
		textureSelect.addItem("Yes");
		textureSelect.addItem("No");

		textureContainer = FengGUI.createWidget(Container.class);
		textureContainer.setLayoutManager(new RowLayout(false));
		textureContainer.addWidget(textureNotifier);
		textureContainer.addWidget(textureSelect);

		applyButton = FengGUI.createWidget(FixedButton.class);
		applyButton.setText("Swap Model");

		applyButton.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(model==null){
					modelEditor.setText("MODEL NOT SELECTED!");
					return;
				}
				boolean textureOnOff = textureSelect.getSelectedValue().equals("Yes");
				try {
					Design thisDesign = SceneScape.getPickedDesign();
					int designID = thisDesign.getID();
					SceneGameState.getInstance().replaceModelFile(designID, model, textureOnOff);

					File designFile = new File(new URL(SettingsPreferences.getDataFolder()+SceneScape.getCity().findDesignByID(designID).getFilepath()).toURI());
					FileInputStream fis;
					fis = new FileInputStream(designFile);
					byte[] b = new byte[fis.available()];
					fis.read(b);
					fis.close();
					PhysicalFileTransporter transport = new PhysicalFileTransporter(b);
					boolean netResponse = NetPool.getPool().getSecureConnection().changeDesignFile(designID, SettingsPreferences.getUser(), SettingsPreferences.getPass(), transport, textureOnOff);
					if(netResponse){
						logger.info("Design " + designID + " model swap successful");
						FengUtils.showNewDismissableWindow("Betaville", "Model Swap Successful!", "ok", true);
					}
					else{
						logger.error("model file could not be changed");
						GUIGameState.getInstance().getDisp().addWidget(FengUtils.createTwoOptionWindow("Betaville", "Model Swap Failed", "ok", "report bug",
								null, new ReportBugListener(), true, true));
					}

				} catch (IOException e1) {
					logger.error("Could not access the requested file!", e1);
				} catch (URISyntaxException e1) {
					logger.error("Love a good URIException", e1);
				}
				resetForUse();
			}
		});

		getContentContainer().addWidget(modelEditor, browseButton, textureContainer, applyButton);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("Swap Models");
		setSize(targetWidth, targetHeight);
	}

	private void resetForUse(){
		model=null;
		modelEditor.setText(modelEditorText);
	}

}
