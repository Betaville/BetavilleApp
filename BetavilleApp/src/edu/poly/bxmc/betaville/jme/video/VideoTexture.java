package edu.poly.bxmc.betaville.jme.video;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.jme.video.test.*;

import edu.poly.bxmc.betaville.module.FrameSyncModule;
import edu.poly.bxmc.betaville.module.Module;

//import edu.poly.bxmc.betaville.jme.video.test.AudioVideoTexture;

public class VideoTexture extends Module implements FrameSyncModule {
	
	//private final String file = "videos/Bielefeld_-_.ogg";
	private String file;
	
	private InputStream inputStream = null;
	private Spatial quad;
	private Texture texture;
	private TextureState textureState;
	//private Timer timer;
	
	private OggDecoder oggDecoder;
	
	public VideoTexture(String name) {
		this(name, "http://upload.wikimedia.org/wikipedia/commons/b/b5/I-15bis.ogg");
	}
	
	public VideoTexture(String name, String file) {
		super(name);
		//getInputStreamFromFile(file);
		this.file = file;
		getInputStream(file);
		
		if (inputStream == null) {
			System.err.println("InputStream is empty!");
		} else {
			createShape();
			oggDecoder = new OggDecoder(inputStream);
			oggDecoder.start();
		}
	}
	
	
		//VideoTexture audioVideoTexture = new VideoTexture();
		//audioVideoTexture.start();
	
	private boolean decoderRun = true;
	
	
	private void createShape() {
		quad = new Quad("texturedQuad", Scale.fromMeter(25), Scale.fromMeter(25));
		//quad = new Box("", new Vector3f(), Scale.fromMeter(25), Scale.fromMeter(25), Scale.fromMeter(25));
		
		//quad.setLocalTranslation(new Vector3f(0,0,-400));
		
		texture = TextureManager.loadTexture(
				"data/foliage/A_Bush_1.png", 
				MinificationFilter.BilinearNearestMipMap,
				MagnificationFilter.Bilinear);
		
		if (texture != null) {
			textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
			textureState.setTexture(texture);
			quad.setRenderState(textureState);
		}
		
		//rootNode.attachChild(quad);
	}
	
	public Spatial getVideoQuad() {
		return quad;
	}
	
	private boolean getInputStream(String file) {
		try {
			/* --- Check for Internet Stream Video --- */
			if (file.contains("http")){
				URL url = new URL(file);
				URLConnection con = url.openConnection();
				con.connect();
				inputStream = con.getInputStream();
				return true;
			} else { /* --- Normal Video --- */
				inputStream = new FileInputStream(new File(file));
				return true;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getFile(){
		return file;
	}

	public void deconstruct() {
		// TODO Auto-generated method stub
		
	}
	public void frameUpdate(float timePerFrame) {
		// TODO Auto-generated method stub
		// TODO Play Frame, when in the right time => Sync-Playback-rate
		/*
		 * 
		// Put A Frame according to its Framerate
		 * TODO: Declare    private long controller;
    						private long frameWait;
    						private long starttime;
    						private int iteration;
    				
    			 Initialize starttime = System.currentTimeMillis();
							controller = System.currentTimeMillis();
							frameWait = 1000 / perf.getFramerate();
							iteration = 0;
							
		This one into the update function:
		
		if (System.currentTimeMillis() >= (controller + frameWait)){
				controller = System.currentTimeMillis();
				System.out.println("There you go: " + System.currentTimeMillis());
				drawFrame();
				iteration++;
		} 
		
		if (System.currentTimeMillis() > (starttime + (iteration*frameWait)+frameWait)) {
			// skip Frame
			
			iteration += 2;
			System.out.println("Look ma, no hands: " + iteration);
		}
		 */
		//timer.getTimePerFrame();
		
		if (oggDecoder.isReady()) {
			oggDecoder.readBody();
			TextureManager.releaseTexture(texture.getTextureKey());

			texture = TextureManager.loadTexture(
				Toolkit.getDefaultToolkit().createImage(oggDecoder.getYUVBuffer()),
				MinificationFilter.BilinearNearestMipMap,
				MagnificationFilter.Bilinear, 
				true);
			if(texture != null){
				if (textureState != null)
					textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
				textureState.setTexture(texture);
				quad.setRenderState(textureState);
				quad.updateRenderState();
			}
		}
	}
	
}
