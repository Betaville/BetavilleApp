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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import com.jme.image.Image;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * Simple panel action to take a screenshot of the current scene,
 * including the GUI
 * @author Skye Book
 *
 */
public class ScreenshotPanelAction extends PanelAction {
	private static final Logger logger = Logger.getLogger(ScreenshotPanelAction.class);

	/**
	 * 
	 */
	public ScreenshotPanelAction() {
		super("Take Screenshot", "Takes a screenshot", "Take Screenshot",
				AvailabilityRule.ALWAYS, UserType.GUEST, null);



		getButton().addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				final int width = DisplaySystem.getDisplaySystem().getWidth();
				final int height = DisplaySystem.getDisplaySystem().getHeight();
				final ByteBuffer buff = BufferUtils.createByteBuffer(DisplaySystem.getDisplaySystem().getWidth()*
						DisplaySystem.getDisplaySystem().getHeight()*
						3);

				DisplaySystem.getDisplaySystem().getRenderer().grabScreenContents(buff, Image.Format.RGB8, 0, 0, DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());


				SettingsPreferences.getGUIThreadPool().submit(new Runnable(){
					public void run() {
						BufferedImage img = new BufferedImage(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight(), BufferedImage.TYPE_INT_RGB);

						// Grab each pixel information and set it to the BufferedImage info.
						for (int x = 0; x < width; x++) {
							for (int y = 0; y < height; y++) {

								int index = 3 * ((height- y - 1) * width + x);
								int argb = (((int) (buff.get(index+0)) & 0xFF) << 16) //r
								| (((int) (buff.get(index+1)) & 0xFF) << 8)  //g
								| (((int) (buff.get(index+2)) & 0xFF));      //b

								img.setRGB(x, y, argb);
							}
						}

						JDialog dialog = new JDialog();
						dialog.setModalityType(ModalityType.APPLICATION_MODAL);
						JFileChooser fileChooser = new JFileChooser(SettingsPreferences.BROWSER_LOCATION);
						fileChooser.showSaveDialog(dialog);
						File file = fileChooser.getSelectedFile();

						if(!file.toString().endsWith(".png")){
							// do we need to do something here?
							file = new File(file.toString().concat(".png"));
						}

						// write out the screenshot image to a file.
						try {
							ImageIO.write(img, "png", file);
						} catch (IOException e) {
							logger.warn("Screenshot " + file.getName() + " could not be written");
						}
					}
				});
			}
		});
	}
}
