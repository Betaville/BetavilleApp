/**
 * 
 */
package edu.poly.bxmc.betaville.sound;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.lwjgl.openal.AL10;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.system.DisplaySystem;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.RangedAudioTracker;
import com.jmex.audio.AudioTrack.TrackType;

import edu.poly.bxmc.betaville.ResourceLoader;

/**
 * Tests the jME2 audio system.
 * @author Skye Book
 *
 */
public class SoundSystemTest extends SimpleGame {
	
	private AudioSystem audioSystem;
	private AudioTrack positionalTrack;
	private Box visualAid;
	private RangedAudioTracker track1;
	private SoundSystem soundSystem;
	private SoundSystemController soundSystemController;
	private Logger logger = Logger.getLogger(SoundSystemTest.class.getName());
	
	/**
	 * 
	 */
	public SoundSystemTest() {
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		
		try {
//			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.addLibrary(LibraryJavaSound.class);

			SoundSystemConfig.setCodec("wav", CodecWav.class);
			SoundSystemConfig.setCodec("ogg", CodecJOgg.class);
			SoundSystemConfig.PREFIX_URL = "^file:.*";
			SoundSystemConfig.setDefaultFadeDistance(50);
		} catch (SoundSystemException e) {
			e.printStackTrace();
		}
		
		try {
			soundSystem = new SoundSystem(LibraryJavaSound.class);
			soundSystemController = new SoundSystemController(soundSystem);
		} catch (SoundSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		URL url1 = ResourceLoader.loadResource("/data/Sounds/Hum.wav");
		URL url2 = ResourceLoader.loadResource("/data/Sounds/numbers.wav");
		URL url3 = ResourceLoader.loadResource("/data/Sounds/beep.wav");
		
		soundSystemController.setBackgroundSource(new SoundSystemSource("bg", url1).setFlag(SoundSource.PRIO, SoundSource.LOOP), 0.6f);
		soundSystemController.addSoundSource(
				new SoundSystemSource("numbers", url2, new Vector3f(0, 0, 0), SoundSystemSource.LinearAttenuation(), 1)
				.setFlag(SoundSource.LOOP, SoundSource.STREAM, SoundSource.EXIT_PAUSE, SoundSource.FADE_BACKGROUND));
		soundSystemController.addSoundSource(
				new SoundSystemSource("beep", url3, new Vector3f(0, -5, -20), SoundSystemSource.RolloffAttenuation(), 0.3f)
				.setFlag(SoundSource.LOOP), false);

//		audioSystem = AudioSystem.getSystem();
//		//AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE);
//
//		audioSystem.setMasterGain(1);
//		audioSystem.getEar().trackOrientation(DisplaySystem.getDisplaySystem().getRenderer().getCamera());
//		audioSystem.getEar().trackPosition(DisplaySystem.getDisplaySystem().getRenderer().getCamera());
//		
//		positionalTrack = createTrack(ResourceLoader.loadResource("/data/Sounds/busBrake.ogg"));
//		positionalTrack.setRolloff(0);
//		positionalTrack.setVolume(.5f);
//		positionalTrack.setMinVolume(0.1f);
//		positionalTrack.setMaxVolume(1.0f);
//		positionalTrack.setTargetVolume(1.0f);
//		positionalTrack.setReferenceDistance(1);
//		positionalTrack.setMaxAudibleDistance(2);
//		positionalTrack.play();
//		
		visualAid = new Box("visualAid", new Vector3f(0,0,0), 5, 5, 5);
		rootNode.attachChild(visualAid);
		
		rootNode.attachChild(new Box("beepSource", new Vector3f(0, -5, -20), 2, 2, 2));


		//track1 = new RangedAudioTracker(positionalTrack, 10, 30);
		//track1.setToTrack(emit1);
		//track1.setPosition(new Vector3f(0,0,0));
		//positionalTrack.setWorldPosition(new Vector3f(0,0,0));
		//track1.setTrackIn3D(true);
		//track1.setMaxVolume(0.35f);
	}

	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		//System.out.println(audioSystem.getEar().getPosition().toString());
//		audioSystem.update();
		//track1.checkTrackAudible(cam.getLocation());
		
		soundSystemController.update(cam);
		
//		AL10.alListener3f(AL10.AL_POSITION, camLoc.x, camLoc.y, camLoc.z);
	}
	
	public AudioTrack createTrack(URL resource){
		AudioTrack sound = AudioSystem.getSystem().createAudioTrack(resource, false);
        sound.setType(TrackType.POSITIONAL);
        sound.setRelative(true);
        sound.setLooping(true);
        return sound;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SoundSystemTest st = new SoundSystemTest();
		st.start();
	}
	
	@Override
	protected void cleanup() {
		super.cleanup();
		soundSystem.cleanup();
	}

}
