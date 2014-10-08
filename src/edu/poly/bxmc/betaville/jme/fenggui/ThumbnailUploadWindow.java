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
package edu.poly.bxmc.betaville.jme.fenggui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.ScrollContainer;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.FlowLayout;
import org.fenggui.layout.GridLayout;

import com.jme.input.MouseInput;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BlockingScrollContainerFactory;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.fenggui.panel.IPanelOnScreenAwareWindow;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.net.PhysicalFileTransporter;

public class ThumbnailUploadWindow extends Window implements IBetavilleWindow, IPanelOnScreenAwareWindow{
	private static final Logger logger = Logger.getLogger(ThumbnailUploadWindow.class);
	
	private ScrollContainer sc;
	
	private Container galleryContainer;
	
	private File thumbStorage;
	
	private Button uploadButton;
	
	private ThumbContainer hoveredImageContainer;
	
	
	public ThumbnailUploadWindow(){
		super(true, true);
		
		uploadButton = FengGUI.createWidget(Button.class);
		uploadButton.setText(Labels.generic("upload"));
		uploadButton.addButtonPressedListener(new IButtonPressedListener() {
			
			@Override
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(hoveredImageContainer==null) return;
				FileInputStream fis;
				try {
					fis = new FileInputStream(hoveredImageContainer.file);
					byte[] b = new byte[fis.available()];
					fis.read(b);
					fis.close();
					NetPool.getPool().getSecureConnection().setThumbnailForObject(hoveredImageContainer.designID,
							new PhysicalFileTransporter(b));
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
				
			}
		});
		
		sc = BlockingScrollContainerFactory.createBlockingScrollCotnainer();
		sc.setSize(640, 480);
		getContentContainer().addWidget(sc);
		sc.setShowScrollbars(true);
		galleryContainer = FengGUI.createWidget(Container.class);
		galleryContainer.setLayoutManager(new FlowLayout());
		sc.setInnerWidget(galleryContainer);
		sc.layout();
		
		//getContentContainer().addWidget(scrollContainer);
		
		try {
			thumbStorage = new File(new File(SettingsPreferences.getDataFolder().toURI()).toString()+"/local/thumbnail/");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
	
	private void generateGallery(){
		int columns=3;
		galleryContainer.setLayoutManager(new GridLayout((thumbStorage.listFiles().length/columns)+1, columns));
		
		for(File thumbnail : thumbStorage.listFiles()){
			// get the design ID
			thumbnail.getName();
			int designID = Integer.parseInt(new String(thumbnail.getName().substring(0, thumbnail.getName().indexOf("-"))));
			Design design = SettingsPreferences.getCity().findDesignByID(designID);
			
			
			
			// only do something if the Design is loaded
			if(design!=null){
				galleryContainer.addWidget(createImageContainer(design, thumbnail));
				layout();
			}
		}
		
		// some guess-work needs to be done to compute the min-height and width
		//galleryContainer.setMinSize(galleryContainer.getSize().getWidth(), galleryContainer.getHeight()+200);
	}
	
	private IWidget createImageContainer(Design design, File thumbnail){
		//final ThumbContainer imageContainer = FengGUI.createWidget(ThumbContainer.class);
		final ThumbContainer imageContainer = new ThumbContainer();
		imageContainer.designID=design.getID();
		imageContainer.file=thumbnail;
		
		imageContainer.setLayoutManager(new BorderLayout());
		
		Label imageLabel = FengGUI.createWidget(Label.class);
		Pixmap px;
		//PixmapBackground bg = null;
		try {
			px = new Pixmap(Binding.getInstance().getTexture(new FileInputStream(thumbnail)));
			imageLabel.setPixmap(px);
		} catch (IOException e) {
			logger.error("Using the default thumbnail image");
			try {
				px = new Pixmap(Binding.getInstance().getTexture("data/uiAssets/buildings/test.png"));
				imageLabel.setPixmap(px);
			} catch (IOException e1) {
				logger.error("Could not load the default thumbnail image", e);
				imageLabel.setText(Labels.generic("image_not_found"));
			}
		}
		imageLabel.setLayoutData(BorderLayoutData.NORTH);
		
		Label designLabel = FengGUI.createWidget(Label.class);
		designLabel.setText(design.getName());
		designLabel.setLayoutData(BorderLayoutData.SOUTH);
		
		imageContainer.addWidget(imageLabel, designLabel);
		
		imageContainer.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
			
			@Override
			public void processEvent(Object source, Event event) {
				
				if(event instanceof MouseEnteredEvent){
					if(!isMouseOverUploadButton()){
						removeUploadButton();
						imageContainer.addWidget(uploadButton);
						hoveredImageContainer = imageContainer;
					}
				}
				else if(event instanceof MouseExitedEvent){
					if(!isMouseOverUploadButton()){
						removeUploadButton();
						hoveredImageContainer = null;
					}
				}
				
			}
		});
		
		return imageContainer;
	}
	
	private boolean isMouseOverUploadButton(){
		int mouseX = MouseInput.get().getXAbsolute();
		int mouseY = MouseInput.get().getYAbsolute();
		
		return (mouseX > uploadButton.getDisplayX() &&
				mouseX < uploadButton.getDisplayX()+uploadButton.getWidth()
				&& mouseY > uploadButton.getDisplayY() &&
				mouseY < uploadButton.getDisplayY()+uploadButton.getHeight());
	}
	
	private void removeUploadButton(){
		if(uploadButton.isInWidgetTree()){
			((Container)uploadButton.getParent()).removeWidget(uploadButton);
		}
	}

	@Override
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize((int)((float)Binding.getInstance().getCanvasWidth()*.75),
				(int)((float)Binding.getInstance().getCanvasHeight()*.75));
	}

	@Override
	public void panelTurnedOn() {
		galleryContainer.removeAllWidgets();
		galleryContainer.setWidth(getContentContainer().getWidth());
		sc.setSize(getContentContainer().getSize());
		//galleryContainer.setSize(getContentContainer().getSize());
		generateGallery();
	}
	
	private class ThumbContainer extends Container{
		
		private ThumbContainer(){
			super();
		}
		
		int designID;
		File file;
	}

}
