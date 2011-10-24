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
package edu.poly.bxmc.betaville.jme.fenggui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayout;

import com.jme.image.Image;
import com.jme.input.MouseInput;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.fenggui.panel.IPanelOnScreenAwareWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState.SOUNDS;
import edu.poly.bxmc.betaville.jme.intersections.PickUtils;
import edu.poly.bxmc.betaville.model.Design;

/**
 * @author Skye Book
 *
 */
public class ThumbnailCaptureWindow extends Window implements IBetavilleWindow, IPanelOnScreenAwareWindow{
	private static final Logger logger = Logger.getLogger(ThumbnailCaptureWindow.class);

	private Button capture;
	private Button upload;
	private Label imageOfNotification;
	private final String imageOfPrefix = "Image of: ";
	private final String noTarget = "Nothing Detected";

	// must be added directly to the window whenever the item is displayed
	private Label photoFrame;

	// keep a handle to the image file up here so it is accessible to the thread saving the image
	private File imageFile;

	/**
	 * 
	 */
	public ThumbnailCaptureWindow() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));

		setupControls();

		setupImageOfNotification();

		setupViewfinder();

		slaveViewfinderToThisWindow();
	}

	private void setupControls(){
		Container controls = FengGUI.createWidget(Container.class);
		controls.setLayoutManager(new BorderLayout());

		capture = FengGUI.createWidget(Button.class);
		capture.setText("Capture");
		capture.setLayoutData(BorderLayoutData.WEST);

		upload = FengGUI.createWidget(Button.class);
		upload.setText("Upload");
		upload.setLayoutData(BorderLayoutData.EAST);

		controls.addWidget(capture, upload);

		getContentContainer().addWidget(controls);

		capture.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {


				// Shutter click sound
				SoundGameState.getInstance().playSound(SOUNDS.CAMERA, SceneGameState.getInstance().getCamera().getLocation());

				// generate the filename
				try {
					imageFile = new File(new URL(SettingsPreferences.getDataFolder()+"local/test.png").toURI());
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				// calculate where to capture the image from
				final int startingX = photoFrame.getX()+5;
				final int startingY = photoFrame.getY()+5;
				final int width = 300;
				final int height = 200;
				final ByteBuffer buff = BufferUtils.createByteBuffer(width * height * 3);

				DisplaySystem.getDisplaySystem().getRenderer().grabScreenContents(buff, Image.Format.RGB8, startingX, startingY, width, height);


				SettingsPreferences.getGUIThreadPool().submit(new Runnable(){
					public void run() {
						BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

						// Grab each pixel information and set it to the BufferedImage info.
						for (int x = 0; x < width; x++) {
							for (int y = 0; y < height; y++) {

								int index = 3 * ((height- y - 1) * width + x);
								int argb = (((int) (buff.get(index+0)) & 0xFF) << 16)	//r
										| (((int) (buff.get(index+1)) & 0xFF) << 8)		//g
										| (((int) (buff.get(index+2)) & 0xFF));			//b

								img.setRGB(x, y, argb);
							}
						}
						// write out the screenshot image to a file.
						try {
							logger.info("Writing screenshot to: " + imageFile.toString());
							ImageIO.write(img, "png", imageFile);
						} catch (IOException e) {
							logger.warn("Screenshot " + imageFile.getName() + " could not be written");
						}
					}
				});
			}
		});
	}

	private void setupImageOfNotification(){
		imageOfNotification = FengGUI.createWidget(Label.class);
		imageOfNotification.setText(imageOfPrefix+noTarget);
		imageOfNotification.setMultiline(true);
		imageOfNotification.setWordWarping(true);
		getContentContainer().addWidget(imageOfNotification);
	}

	private void setupViewfinder(){
		photoFrame = FengGUI.createWidget(Label.class);
		photoFrame.addEventListener(EVENT_MOUSE, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {
				int mouseX = MouseInput.get().getXAbsolute();
				int mouseY = MouseInput.get().getYAbsolute();

				if(mouseX>photoFrame.getDisplayX() && mouseX<photoFrame.getDisplayX()+photoFrame.getWidth()
						&& mouseY>photoFrame.getDisplayY() && mouseY<photoFrame.getDisplayY()+photoFrame.getHeight()){
					// try the center of the viewfinder
					int x = photoFrame.getDisplayX()+(photoFrame.getWidth()/2);
					int y = photoFrame.getDisplayY()+(photoFrame.getHeight()/2);
					logger.info("trying pick at " + x + ", " + y);
					Design pickedDesign = PickUtils.pickDesignAtScreenLocation(x, y);
					if(pickedDesign==null){
						imageOfNotification.setText(imageOfPrefix+noTarget);
					}
					else{
						imageOfNotification.setText(imageOfPrefix+pickedDesign.getName());
					}
					layout();
				}
			}
		});

		Pixmap bracketedFrame;
		try {
			bracketedFrame = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/screenshot/bracketed_frame.png"));
			photoFrame.setPixmap(bracketedFrame);
		} catch (IOException e) {
			logger.warn("Bracketed photo frame could not be found", e);
		}
	}

	/*
	 * keeps the viewfinder anchored to the bottom of the window
	 */
	private void slaveViewfinderToThisWindow(){
		addEventListener(EVENT_POSITIONCHANGED, new IGenericEventListener() {

			@Override
			public void processEvent(Object source, Event event) {

				// test x
				if(getX()<0){
					setX(0);
				}
				else if(getX()+getWidth()>DisplaySystem.getDisplaySystem().getWidth()){
					setX(DisplaySystem.getDisplaySystem().getWidth()-getWidth());
				}

				photoFrame.setXY(getX()-5, getY()-photoFrame.getHeight());

				// test y
				if(photoFrame.getY()<0){
					photoFrame.setY(0);
					setY(photoFrame.getHeight());
				}
				else if(getY()+getHeight()>DisplaySystem.getDisplaySystem().getHeight()){
					setY(DisplaySystem.getDisplaySystem().getHeight()-getHeight());
				}
			}
		});

	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.panel.IPanelOnScreenAwareWindow#panelTurnedOn()
	 */
	@Override
	public void panelTurnedOn() {
		GUIGameState.getInstance().getDisp().addWidget(photoFrame);
	}

	/*
	 * (non-Javadoc)
	 * @see org.fenggui.composite.Window#close()
	 */
	@Override
	public void close(){
		super.close();
		// remove the photo frame from display
		GUIGameState.getInstance().getDisp().removeWidget(photoFrame);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	@Override
	public void finishSetup() {
		setTitle("Create Thumbnails");
		setWidth(photoFrame.getWidth());
	}

}
