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
package edu.poly.bxmc.betaville.jme.gamestates;

import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

import com.jme.math.Vector3f;
import com.jme.system.DisplaySystem;
import com.jmex.audio.AudioSystem;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameStateManager;

import edu.poly.bxmc.betaville.ResourceLoader;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.sound.SoundTrigger;
import edu.poly.bxmc.betaville.sound.ExitAction;

/**
 * @author Skye Book
 *
 */
public class SoundGameState extends BasicGameState {
	private static Logger logger = Logger.getLogger(SoundGameState.class);
	private SoundSystem soundSystem;

	private AudioSystem audioSystem;

	private ArrayList<SoundTrigger> soundTriggers = null;
	
	private boolean fadeInProgress = false;

	// TEMPORARY
	private boolean atLeastOneTriggerInRange=false;
	private boolean atLeastOneTriggerPlaying=false;

	public enum SOUNDS{
		DESIGN_SELECTION, DESIGN_CREATION, DESIGN_DESCTRUCTION, TOGGLE, CAMERA
	}
	/**
	 * @param arg0
	 */
	public SoundGameState(String name) {
		super(name);

		soundSystemSetup();
	}
	
	private void soundSystemSetup() {
		soundTriggers = new ArrayList<SoundTrigger>();

		// check for library compatibility and set codecs
		try {
			if(SoundSystem.libraryCompatible(LibraryLWJGLOpenAL.class)){
				SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
				logger.info("Using LWJGL supplied OpenAL");
			}
			else if(SoundSystem.libraryCompatible(LibraryJavaSound.class)){
				SoundSystemConfig.addLibrary(LibraryJavaSound.class);
				logger.info("Using JavaSound");
			}
			else logger.warn("No sound libraries could be loaded.  Sounds will not be played.");

			SoundSystemConfig.setCodec("wav", CodecWav.class);
			SoundSystemConfig.setCodec("ogg", CodecJOgg.class);
		} catch (SoundSystemException e) {
			e.printStackTrace();
		}

		// start SoundSystem
		soundSystem = new SoundSystem();
		// initialize at remembered volume
		try{
			double value = Double.parseDouble(System.getProperty("betaville.sound.volume.master"));
			soundSystem.setMasterVolume((float)value);
		}catch(NumberFormatException numException){
			logger.error("Incompatible Value Found for Master Volume: \""+System.getProperty("betaville.sound.volume.master")+"\"");
			soundSystem.setMasterVolume(1f);
		}

		soundSystem.loadSound(ResourceLoader.loadResource("/data/Sounds/toggleSwitch.wav"), "toggleSwitch.wav");
		soundSystem.loadSound(ResourceLoader.loadResource("/data/Sounds/nikonF4.wav"), "nikonF4.wav");
		soundSystem.loadSound(ResourceLoader.loadResource("/data/Sounds/Hum.wav"), "Hum.wav");

		//FX Joe Sounds

		addSoundTrigger(new SoundTrigger("Bus.Broadway&Chambers", ResourceLoader.loadResource("/data/Sounds/busBrake.ogg"),
				new UTMCoordinate(583902, 4507547, 18, 'T', 0), 100, 100, 100, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Bus.Broadway&Fulton", ResourceLoader.loadResource("/data/Sounds/kneelingBus.ogg"),
				new UTMCoordinate(583677, 4507170, 18, 'T', 0), 100, 100, 100, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Bus.Broadway&Liberty", ResourceLoader.loadResource("/data/Sounds/busBrake.ogg"),
				new UTMCoordinate(583558, 4506969, 18, 'T', 0), 100, 100, 100, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Bus.Broadway&Rector", ResourceLoader.loadResource("/data/Sounds/busBraking3.ogg"),
				new UTMCoordinate(583436, 4506767, 18, 'T', 0), 100, 100, 100, ExitAction.STOP, false, true));



		addSoundTrigger(new SoundTrigger("Bus.brooklynBoroughHall", ResourceLoader.loadResource("/data/Sounds/kneelingBus.ogg"),
				new GPSCoordinate(0, 40, 41, 34.93579f, -73, 59, 26.900373f), 100, 100, 100, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("longSiren.Flatbush&Tillary", ResourceLoader.loadResource("/data/Sounds/longSiren.ogg"),
				new GPSCoordinate(0, 40, 41, 52.208344f, -73, 59, 3.2466002f), 100, 100, 100, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Truck.Willoughby", ResourceLoader.loadResource("/data/Sounds/Truck1.ogg"),
				new GPSCoordinate(0, 40, 41, 34.067627f, -73, 59, 5.5672755f), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Truck.Flatbush&ManhattanBridge", ResourceLoader.loadResource("/data/Sounds/Truck3Rattle.ogg"),
				new GPSCoordinate(0, 40, 41, 57.82427f, -73, 59, 7.5502605f), 100, 100, 100, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Truck.CanaloffManhattanBridge", ResourceLoader.loadResource("/data/Sounds/Truck2pan.ogg"),
				new GPSCoordinate(0, 40, 42, 59.272507f, -73, 59, 43.74148f), 100, 100, 100, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Truck.Canal&Mott", ResourceLoader.loadResource("/data/Sounds/Truck3Rattle.ogg"),
				new GPSCoordinate(0, 40, 43, 2.626864f, -73, 59, 52.812355f), 100, 100, 100, ExitAction.STOP, false, true));



		// FX Bus Idling
		addSoundTrigger(new SoundTrigger("IdleBus.SentonShrine", ResourceLoader.loadResource("/data/Sounds/Idle001.ogg"),
				new GPSCoordinate(0, 40, 42, 8.502132f, -74, 0, 49.489697f), 50, 50, 50, ExitAction.STOP, true, true));

		addSoundTrigger(new SoundTrigger("IdleBus.Jay&Myrtle", ResourceLoader.loadResource("/data/Sounds/Idle002loop.ogg"),
				new GPSCoordinate(0, 40, 41, 40.03062f, -73, 59, 12.464515f), 50, 50, 50, ExitAction.STOP, true, true));

		/*addSoundTrigger(new SoundTrigger("IdleBus.", ResourceLoader.loadResource("/data/Sounds/Idle003.ogg"),
				new UTMCoordinate(new GPSCoordinate(0, 40, 43, 2.626864f, -73, 59, 52.812355f)), 100, 100, 100, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("IdleBus.", ResourceLoader.loadResource("/data/Sounds/Idle004.ogg"),
				new UTMCoordinate(new GPSCoordinate(0, 40, 43, 2.626864f, -73, 59, 52.812355f)), 100, 100, 100, ExitAction.STOP, false, true));*/




		//ANDRES Proposal 001

		addSoundTrigger(new SoundTrigger("Andres.Firhouse.Liberty&Greenwich", ResourceLoader.loadResource("/data/Sounds/firestationalarmLadder10..ogg"),
				new GPSCoordinate(0, 40, 42, 35.75095f, -74, 0, 46.530575f), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.peeper.Morris&Broadway", ResourceLoader.loadResource("/data/Sounds/touriststospringpeeper.ogg"),
				new UTMCoordinate(583325, 4506570, 18, 'T', 0), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.lenape.Pearl&Wagner", ResourceLoader.loadResource("/data/Sounds/trafficcoptolenapewoman.ogg"),
				new UTMCoordinate(584362, 4507093, 18, 'T', 0), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.beaver.Broad&Beaver", ResourceLoader.loadResource("/data/Sounds/caralarmtobeaver.ogg"),
				new UTMCoordinate(583486, 4506480, 18, 'T', 0), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.graywolf.Beekman&ParkRow", ResourceLoader.loadResource("/data/Sounds/trucktograywolf.ogg"),
				new UTMCoordinate(583853, 4507249, 18, 'T', 0), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.trinitybells.Broadway&Wall", ResourceLoader.loadResource("/data/Sounds/trinitychurchbells.ogg"),
				new UTMCoordinate(583453, 4506792, 18, 'T', 0), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.churchtocrow.Church&Barclay", ResourceLoader.loadResource("/data/Sounds/distantchurchtoamericancrow.ogg"),
				new GPSCoordinate(0, 40, 42, 46.62274f, -74, 0, 36.055954f), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.constructiontotreefrog.Vesey&WBroadway", ResourceLoader.loadResource("/data/Sounds/constructiontonortherngraytreefrog.ogg"),
				new GPSCoordinate(0, 40, 42, 46.717995f, -74, 0, 43.385197f), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.vendortolenape.John&Broadway", ResourceLoader.loadResource("/data/Sounds/streetvendortolenapewoman.ogg"),
				new GPSCoordinate(0, 40, 42, 37.60417f, -74, 0, 35.72073f), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.jackhammerwoodpecker.William&Maiden", ResourceLoader.loadResource("/data/Sounds/Jackhammertoredbelliedwoodpecker.ogg"),
				new UTMCoordinate(583768, 4506830, 18, 'T', 0), 150, 150, 150, ExitAction.STOP, false, true));

		addSoundTrigger(new SoundTrigger("Andres.policechickadee.Albany&Greenwich", ResourceLoader.loadResource("/data/Sounds/policehornsqueaktoblackcappedchickadee.ogg"),
				new UTMCoordinate(583327, 4506962, 18, 'T', 0), 150, 150, 150, ExitAction.STOP, false, true));
		
		Vector3f loc = DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation();
		soundSystem.newSource(true, "$background", "Hum.wav", true, loc.getX(), loc.getY(), loc.getZ(), SoundSystemConfig.ATTENUATION_ROLLOFF, .5f);
		soundSystem.setVolume("$background", .2f);
	/* soundSystem.newStreamingSource(true, "$background", ResourceLoader.loadResource("/data/Sounds/Hum.ogg"), "bg.ogg", true,
				loc.getX(), loc.getY(), loc.getZ(),
				SoundSystemConfig.ATTENUATION_ROLLOFF, .5f); */
		soundSystem.play("$background");
	}
	
	/**
	 * Triggers a sound to be played in the sound system.
	 * @param soundSelection - The sound to be played.
	 * @param designLocation - The location of the design to
	 * play the sound from.
	 */
	public void playSound(SOUNDS soundSelection, UTMCoordinate designLocation){
		playSound(soundSelection, JME2MapManager.instance.locationToBetaville(designLocation));
	}

	/**
	 * Triggers a sound to be played in the sound system.
	 * @param soundSelection - The sound to be played.
	 * @param designLocation - The location of the design to
	 * play the sound from.
	 */
	public void playSound(SOUNDS soundSelection, Vector3f designLocation){
		/*
		 * have to get the camera location from DisplaySystem for now until
		 * a resolution can be found to the problem of using gamestates
		 * wrapped in render passes and the GameStateManager at the same time
		 */
		Vector3f cameraPosition = DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation();
		//soundSystem.moveListener(cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ());
		if(soundSelection.equals(SOUNDS.DESIGN_SELECTION)){
			//playDesignSelection(designLocation);
		}
		else if(soundSelection.equals(SOUNDS.DESIGN_CREATION)){
		}
		else if(soundSelection.equals(SOUNDS.DESIGN_DESCTRUCTION)){
		}
		else if(soundSelection.equals(SOUNDS.TOGGLE)){
			playToggle(cameraPosition);
		}
		else if(soundSelection.equals(SOUNDS.CAMERA)){
			soundSystem.quickPlay(true, "nikonF4.wav", false, cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ(), SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
		}
	}

	private void playToggle(Vector3f designPosition){
		soundSystem.quickPlay(true, "toggleSwitch.wav", false, designPosition.getX(), designPosition.getY(), designPosition.getZ(), SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
	}
	
	public void playTriggeredSound(URL soundURL, Vector3f location){
		//soundSystem.quickStream(true, soundURL, "toilet.wav", false, location.getX(), location.getY(), location.getZ(), SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
	}

	/**
	 * Adds a {@link SoundTrigger} to the list that gets processed on update
	 * 
	 * @param newTrigger The new trigger to add
	 * @return false if the trigger could not be added
	 * @see SoundTrigger
	 */
	public boolean addSoundTrigger(SoundTrigger newTrigger){
		for(SoundTrigger trigger : soundTriggers){
			if(trigger.getName().equals(newTrigger.getName())){
				logger.warn("SoundTrigger named \"" + newTrigger.getName() +"\" requires a unique name.");
				return false;
			}
		}
		/*
		soundSystem.newStreamingSource(true, newTrigger.getName(), newTrigger.getURL(), newTrigger.getURLFileName(), newTrigger.isLooped(),
				newTrigger.getCenter().getX(), newTrigger.getCenter().getY(), newTrigger.getCenter().getZ(),
				SoundSystemConfig.ATTENUATION_ROLLOFF, newTrigger.getRolloffFactor());
		soundSystem.setVolume(newTrigger.getName(), newTrigger.getVolume());
		soundTriggers.add(newTrigger);
		*/
		return true;
	}

	/**
	 * Removes a {@link SoundTrigger} from the list of triggers
	 * 
	 * @param soundTriggerName The name of the trigger to remove
	 * @see SoundTrigger
	 */
	public void removeSoundTrigger(String soundTriggerName){
		for(SoundTrigger trigger : soundTriggers){
			if(trigger.getName().equals(soundTriggerName)){
				soundTriggers.remove(trigger);
				return;
			}
		}
	}

	public void render(float tpf){
		// no drawing to be done in this GameState
	}
	
	private void soundSystemUpdate(){
		Vector3f cameraPosition = SceneGameState.getInstance().getCamera().getLocation();

		// move the background sound
		soundSystem.setPosition("$background", cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ());

		// move the listener position to where the camera is located when a sound is playing
		if(soundSystem.playing()){
			Vector3f cameraDirection = SceneGameState.getInstance().getCamera().getDirection();
			soundSystem.setListenerPosition(cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ());
			soundSystem.setListenerOrientation(cameraDirection.getX(), cameraDirection.getY(), cameraDirection.getZ(), 0, 1, 0);
		}

		// TEMPORARY
		atLeastOneTriggerInRange=false;
		atLeastOneTriggerPlaying=false;

		// test for trigger based sound
		for(SoundTrigger trigger : soundTriggers){


			// IN RANGE ACTIONS
			if(trigger.isViolated(cameraPosition)){

				// TEMPORARY
				if(trigger.doesForceFade()){
					atLeastOneTriggerInRange=true;
				}

				// if force fades is set to on, then we need to set the volume lower
				if(trigger.doesForceFade()){
					// set the volume lower
					// doesn't it need to come back after the sound is finished playing?
				}

				// if we were in range and this sound is no longer playing, but is set to loop, play it again
				if(trigger.wasInRangeOnLastUpdate() && !soundSystem.playing(trigger.getName()) && trigger.isLooped()){
					soundSystem.play(trigger.getName());
				}
				// if this is a fresh trigger violation, always play the sound
				else if(!trigger.wasInRangeOnLastUpdate()){
					soundSystem.play(trigger.getName());
				}

				// notify the trigger that it is currently in range
				trigger.isInRangeOnCurrentUpdate(true);
			}

			// OUT OF RANGE ACTIONS
			else{
				if(trigger.wasInRangeOnLastUpdate()){
					if(trigger.getExitAction().equals(ExitAction.STOP)){
						soundSystem.stop(trigger.getName());
					}
					else if(trigger.getExitAction().equals(ExitAction.PAUSE)){
						soundSystem.pause(trigger.getName());
					}
					else if(trigger.getExitAction().equals(ExitAction.FINISH)){
						/* allows the sound to finish playing.. its actually inefficient to do this
						 * check on every update since we don't have to do anything but for little
						 * cost it makes things look prim and proper! yay!
						 * -Skye
						 */
					}
				}

				// notify the trigger of its current status
				trigger.isInRangeOnCurrentUpdate(false);
				if(soundSystem.playing(trigger.getName())){
					trigger.isPlayingOnCurrentUpdate(true);
					atLeastOneTriggerPlaying=true;
				}

				if(atLeastOneTriggerInRange && !fadeInProgress && atLeastOneTriggerPlaying){
					soundSystem.setVolume("$background", 0);
				}
				else if(!atLeastOneTriggerInRange || !atLeastOneTriggerPlaying){
					soundSystem.setVolume("$background", .2f);
				}

			}
		}
	}

	public void update(float tpf){
		soundSystemUpdate();
//		audioSystem.update();
	}

	private void turnOnSound(SoundTrigger trigger){
		soundSystem.play(trigger.getName());
		if(trigger.doesForceFade()){
			// start fading out background

			// start sound
		}
	}

	/**
	 * 
	 * @param soundName The name of the sound contained in SoundSystem to be faded
	 * @param targetVolume The desired final volume of the sound.
	 * @param time The desired length of time for the time to take.
	 * @param resolution The number of times we would the volume to be incremented towards its targetVolume.
	 * @see SoundSystem#setVolume(String, float)
	 * @experimental Don't use me yet!
	 */
	private void fadeOut(final String soundName, final float targetVolume, final long time, final int resolution){
		fadeInProgress=true;
		logger.info("Fading " + soundName + " down to " + targetVolume);
		final float startVolume = soundSystem.getVolume(soundName);

		// first check the fade makes sense (we can't fade down to a higher volume!
		if(targetVolume>startVolume){
			logger.warn("Target volume louder than starting volume");
			return;
		}

		// Submit the actual fade action to the threadPool
		SettingsPreferences.getThreadPool().submit(new Runnable(){
			@SuppressWarnings("static-access")
			public void run(){

				float differencePerStep = (startVolume-targetVolume)/resolution;
				long timePerStep = time/resolution;

				/*
				 * By caching the current volume up here, we eliminate the need
				 * to go back to the sound system and look up the volume by sound
				 * name, which is a potentially expensive activity if there are many sounds.
				 * (Thinking realistically, you would likely need thousands of sounds loaded
				 * to make this a *really* taxing lookup.
				 */
				float current=startVolume;

				// used to mark the beginning of each loop's execution
				long executionStart;

				float projectedFinish;

				while(current>targetVolume){
					executionStart=System.currentTimeMillis();

					/* 'projected finish' is what the volume will be on a normal run of the
					 * fade loop but in the case of a rounding error or some other unforeseen
					 * event, we must be prepared for the possibility of this volume being less than
					 * zero.  If that is the case, we simply clamp the volume to zero and break
					 * from the fade loop.
					 */
					projectedFinish = current-differencePerStep;
					if(projectedFinish>=0){
						soundSystem.setVolume(soundName, projectedFinish);
					}
					else{
						soundSystem.setVolume(soundName, 0);
						break;
					}

					// reset the cached current volume
					current = soundSystem.getVolume(soundName);

					/* sleep until ready: By correcting the sleep time for how long it took to set the volume, we
					 * can ensure a fade that's as close to uniform as possible
					 * (timePerStep - the time it took to get to this point since the loop continued)
					 */
					try {
						Thread.currentThread().sleep(timePerStep-(System.currentTimeMillis()-executionStart));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				fadeInProgress=false;
			}
		});
	}

	public void cleanup(){
		if(AudioSystem.isCreated()){
			audioSystem.cleanup();
		}
	}
	
	public void setVolume(float volume){
		soundSystem.setMasterVolume(volume);
	}

	public static SoundGameState getInstance(){
		return (SoundGameState)GameStateManager.getInstance().getChild("soundGameState");
	}
}
