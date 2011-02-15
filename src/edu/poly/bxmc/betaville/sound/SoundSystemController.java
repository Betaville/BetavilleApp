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
package edu.poly.bxmc.betaville.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * SoundSystemController manages a collection of {@link SoundSource SoundSources}.
 * It is built around {@link paulscode.sound.SoundSystem}
 * <p>
 * Sounds are either streamed or loaded into memory depending on the flag SoundSource.STREAM
 * being set or not, respectively.<br />
 * Note that loading a file into memory is only an option for short sounds since the maximum
 * file size is restricted through SoundSystem. However, for seamless loops (of short sounds)
 * streaming is not an option as it results in small gap whenever a sound starts over again.  
 * 
 * @see <a href="http://www.paulscode.com/docs/SoundSystem/" title="API Docs">SoundSystem</a>
 * 
 * @author Skye Book
 * @author Peter Schulz
 *
 */
public class SoundSystemController {
	private Logger logger = Logger.getLogger(SoundSystemController.class.getName());
	
	private SoundSystem soundSystem;
	private List<SoundSource> soundSources;
	
	private String backgroundSourceName;
	private Vector3f backgroundLocation;
	private float backgroundFadingMax;
	private boolean backgroundFading;
	private float backgroundVolume;
	
	private Vector3f lastLococation;
	
	private static final float MOVEMENT_THRESHOLD = 0.01f;
	
	public SoundSystemController(SoundSystem ss) {
		soundSystem 		= ss;
		soundSources		= new ArrayList<SoundSource>();
		backgroundLocation	= new Vector3f();
		lastLococation		= new Vector3f();
	}
	
	/**
	 * Adds the specified SoundSource as triggered.
	 * @param source The source to be added
	 */
	public void addSoundSource(SoundSource source) {
		addSoundSource(source, true);
	}
	
	/**
	 * Adds specified SoundSource.
	 * @param source Source to be added
	 * @param triggered Set to {@code true} to activate trigger related features (background fading etc.)
	 * @throws IllegalArgumentException If there already is a SoundSource with the same name
	 */
	public void addSoundSource(SoundSource source, boolean triggered) {
		ensureUniqueName(source);
		
		int attenuation = 0;
		switch (source.getAttenuation().getType()) {
		case NONE:
			attenuation = SoundSystemConfig.ATTENUATION_NONE;
			break;
		case LINEAR:
			attenuation = SoundSystemConfig.ATTENUATION_LINEAR;
			break;
		case ROLLOFF:
			attenuation = SoundSystemConfig.ATTENUATION_ROLLOFF;
			break;
		default:
			attenuation = SoundSystemConfig.getDefaultAttenuation();
			break;
		}
		
		if (source.getFlag(SoundSource.STREAM)) {
			logger.info("adding source (stream) " + source.getName());
			soundSystem
			.newStreamingSource(source.getFlag(SoundSource.PRIO), 
					source.getName(), source.getURL(), source.getURLFileName(), 
					source.getFlag(SoundSource.LOOP), 
					source.getCenter().getX(),
					source.getCenter().getY(), 
					source.getCenter().getZ(), 
					attenuation, source.getAttenuation().getValue());
		} else {
			logger.info("adding source " + source.getName());
			soundSystem
			.newSource(source.getFlag(SoundSource.PRIO), 
					source.getName(), source.getURL(), source.getURLFileName(), 
					source.getFlag(SoundSource.LOOP), 
					source.getCenter().getX(),
					source.getCenter().getY(), 
					source.getCenter().getZ(), 
					attenuation, source.getAttenuation().getValue());
		}
		soundSystem.setVolume(source.getName(), source.getVolume());
		
		if (triggered) {
			soundSources.add(source);
		} else {
			soundSystem.play(source.getName());
		}
			
	}

	/**
	 * Plays specified sound as background sound.
	 * The background sound may be dimmed temporarily by other sounds.
	 * Passing {@code fadingMax} a number greater than 0 enables background fading. 
	 * Therefore any triggered {@link SoundSource} may request background fading by having the 
	 * {@link SoundSource#FADE_BACKGROUND} flag set.
	 * @param source Source to be played
	 * @param fadingMax Maximum fade percentage, between 0 and 1
	 * @throws IllegalStateException If this method has already been called
	 * @throws IllegalArgumentException If {@code fadingMax} less than 0 or greater than 1
	 */
	public void setBackgroundSource(SoundSource source, float fadingMax) {
		if (backgroundSourceName != null)
			throw new IllegalStateException("setBackgroundSource may only be called once");
		if (fadingMax < 0 || fadingMax > 1)
			throw new IllegalArgumentException("fadingMax must be between 0 and 1");
		
		backgroundVolume		= source.getVolume();
		backgroundFading		= fadingMax > 0;
		backgroundFadingMax		= fadingMax;
		int attenuationType 	= SoundSystemConfig.ATTENUATION_NONE;
		float attenuationValue	= 0;
		
		if (source.getFlag(SoundSource.STREAM)) {
			backgroundSourceName = soundSystem.quickStream(source.getFlag(SoundSource.PRIO), 
					source.getURL(), source.getURLFileName(), 
					source.getFlag(SoundSource.LOOP), 0, 0, 0, 
					attenuationType, attenuationValue);
		} else {
			backgroundSourceName = soundSystem.quickPlay(source.getFlag(SoundSource.PRIO), 
					source.getURL(), source.getURLFileName(), 
					source.getFlag(SoundSource.LOOP), 0, 0, 0, 
					attenuationType, attenuationValue);
		}
	}
	
	/**
	 * Updates all SoundSources according to the specified camera's characteristics.
	 * @param cam
	 */
	public void update(Camera cam) {
		update(cam.getLocation(), cam.getDirection(), cam.getUp());
	}

	/**
	 * Updates all SoundSources based on specified location/direction.
	 * @param loc Head location vector
	 * @param dir Head direction vector
	 * @param up Head up vector
	 */
	public void update(Vector3f loc, Vector3f dir, Vector3f up) {
		// move the listener position to where the camera is located when a sound is playing
		if (soundSystem.playing()) {
			soundSystem.setListenerPosition(loc.getX(), loc.getY(), loc.getZ());
			soundSystem.setListenerOrientation(dir.getX(), dir.getY(), dir.getZ(), 
					up.getX(), up.getY(), up.getZ());
		}
		
		if (!movedSinceLastUpdate(loc)) {
			lastLococation.set(loc);
			return;
		}
		
		/* Holds a value between 0 and 1 representing the effective fading distance
		 * between the current location and the closest's sound origin if background
		 * fading is enabled and at least one sound in range requires it.
		 */
		float relativeFadingDistance = -1;
		
		// Iterate over all sounds
		for (SoundSource source : soundSources) {
			
			// IN RANGE ACTIONS
			if (source.triggeredBy(loc)) {
				if (backgroundFading && source.getFlag(SoundSource.FADE_BACKGROUND)) {
					relativeFadingDistance = Math.max(relativeFadingDistance, source.getRelativeFadeDistance(loc));	
				}
				
				if (source.getFlag(SoundSource.LOOP) && source.getFlag(SoundSource.EXIT_PAUSE) && !soundSystem.playing(source.getName())) {
					logger.info("reanimated " + source.getName());
					soundSystem.activate(source.getName());
					soundSystem.play(source.getName());
				}

				// if this is a fresh trigger violation, always play the sound
				else if (!source.getFlag(SoundSource.IN_RANGE)) {
					logger.info("playing " + source.getName());
					soundSystem.play(source.getName());
				}

				// notify the trigger that it is currently in range
				source.setFlag(SoundSource.IN_RANGE);
			}

			// OUT OF RANGE ACTIONS
			else {
				if (source.getFlag(SoundSource.IN_RANGE)) {
					if (source.getFlag(SoundSource.EXIT_STOP)) {
						logger.info("stopped " + source.getName());
						soundSystem.stop(source.getName());
					}
					else if (source.getFlag(SoundSource.EXIT_PAUSE)) {
						logger.info("paused " + source.getName());
						soundSystem.pause(source.getName());
						soundSystem.cull(source.getName());
					}
//					if (trigger.doesForceSeamless())
//						soundSystem.unloadSound(trigger.getURL().toString());
				}

				// notify the trigger of its current status
				source.setFlag(SoundSource.IN_RANGE, false);
			}
			
		} // end for
		
		// Check if we fade the background sound after all
		if (backgroundFading) {
			// Relocate background sound origin to maintain
			// a constant volume
			backgroundLocation.set(loc);
			if (relativeFadingDistance >= 0 && relativeFadingDistance <= 1) {
				if (relativeFadingDistance > backgroundFadingMax) {
					// Ensure minimum remainder
					// TODO control dynamically through SoundSource
					relativeFadingDistance = backgroundFadingMax;
				}
				soundSystem.setVolume(backgroundSourceName, 
						backgroundVolume * (1 - relativeFadingDistance));
			}
		} // end if

		lastLococation.set(loc);		
	}

	private void ensureUniqueName(SoundSource newSource) {
		for (SoundSource knownSource : soundSources) {
			if (knownSource.getName().equals(newSource.getName())){
				throw new IllegalArgumentException(
						"SoundTrigger named \"" + newSource.getName() + 
						"\" requires a unique name.");
			}
		}
	}

	private boolean movedSinceLastUpdate(Vector3f loc) {
		return (Math.abs(lastLococation.x - loc.x) + 
		Math.abs(lastLococation.y - loc.y) + 
		Math.abs(lastLococation.z - loc.z)) > MOVEMENT_THRESHOLD;
	}
}
