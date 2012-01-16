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

import edu.poly.bxmc.betaville.SceneScape;
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
		uploadButton.setText("Upload");
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
							new PhysicalFileTransporter(b), SettingsPreferences.getUser(), SettingsPreferences.getPass());
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
			Design design = SceneScape.getCity().findDesignByID(designID);
			
			
			
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
				imageLabel.setText("Image Not Found");
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
		setTitle("Upload Thumbnails");
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
