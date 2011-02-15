/**
 * 
 */
package edu.poly.bxmc.betaville.sound;

import java.net.URL;

import org.lwjgl.openal.AL10;

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
public class SoundTest extends SimpleGame {
	
	private AudioSystem audioSystem;
	private AudioTrack positionalTrack;
	private Box visualAid;
	private RangedAudioTracker track1;
	
	/**
	 * 
	 */
	public SoundTest() {
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		
		audioSystem = AudioSystem.getSystem();
		//AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE);

		audioSystem.setMasterGain(1);
		audioSystem.getEar().trackOrientation(DisplaySystem.getDisplaySystem().getRenderer().getCamera());
		audioSystem.getEar().trackPosition(DisplaySystem.getDisplaySystem().getRenderer().getCamera());
		
		positionalTrack = createTrack(ResourceLoader.loadResource("/data/Sounds/busBrake.ogg"));
		positionalTrack.setRolloff(0);
		positionalTrack.setVolume(.5f);
		positionalTrack.setMinVolume(0.1f);
		positionalTrack.setMaxVolume(1.0f);
		positionalTrack.setTargetVolume(1.0f);
		positionalTrack.setReferenceDistance(1);
		positionalTrack.setMaxAudibleDistance(2);
		positionalTrack.play();
		
		visualAid = new Box("visualAid", new Vector3f(0,0,0), 5, 5, 5);
		rootNode.attachChild(visualAid);


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
		audioSystem.update();
		//track1.checkTrackAudible(cam.getLocation());
		
		
		Vector3f camLocation = cam.getLocation();
		AL10.alListener3f(AL10.AL_POSITION, camLocation.x, camLocation.y, camLocation.z);
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
		SoundTest st = new SoundTest();
		st.start();
	}

}
