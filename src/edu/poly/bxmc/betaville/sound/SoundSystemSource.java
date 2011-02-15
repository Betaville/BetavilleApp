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

import java.net.URL;

import com.jme.math.Vector3f;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

/**
 * SoundSystemSource wraps a sound source as understood by {@link SoundSystem}.
 * 
 * @author Peter Schulz
 */
public class SoundSystemSource extends SoundSource {
	
	private static float defaultVolume = 0.5f;
	
	/**
	 * Sets the default volume used for new SoundSystemSource instances
	 * @param value The volume, a value between 0 and 1 (inclusive)
	 */
	public static void setDefaultVolume(float value) {
		defaultVolume  = value;
	}
	
	public SoundSystemSource(String name, URL url, Vector3f center, Attenuation attenuation, float volume) {
		super(name, url, center, attenuation, volume);
	}
	
	public SoundSystemSource(String name, URL url, Vector3f center, Attenuation attenuation) {
		super(name, url, center, attenuation, defaultVolume);
	}
	
	public SoundSystemSource(String name, URL url) {
		super(name, url, new Vector3f(), NoAttenuation(), defaultVolume);
	}
	
	/** 
	 * Creates a linear Attenuation object using the default distance.
	 * @see #LinearAttenuation(float)
	 * @return An instance of Attenuation representing linear attenuation
	 */
	public static Attenuation LinearAttenuation() {
		return LinearAttenuation(SoundSystemConfig.getDefaultFadeDistance());
	}
	
	/**
	 * Creates a linear Attenuation object with the specified fading distance.
	 * @param distance The fading distance
	 * @return An instance of Attenuation representing linear attenuation
	 */
	public static Attenuation LinearAttenuation(final float distance) {
		return new Attenuation() {
			public Type getType() {	return Type.LINEAR;	}
			public float getValue() { return distance; }
		};
	}
	
	/** 
	 * Creates a roll-off Attenuation object using the default factor.
	 * @see #LinearAttenuation(float)
	 * @return An instance of Attenuation representing roll-off attenuation
	 */
	public static Attenuation RolloffAttenuation() {
		return RolloffAttenuation(SoundSystemConfig.getDefaultRolloff());
	}
	
	/**
	 * Creates a roll-off Attenuation object with the specified factor.
	 * @param distance The roll-off factor
	 * @return An instance of Attenuation representing roll-off attenuation
	 */
	public static Attenuation RolloffAttenuation(final float factor) {
		return new Attenuation() {
			public Type getType() { return Type.ROLLOFF; }
			public float getValue() { return factor; }
		};
	}
	
	/** 
	 * Creates No-Attenuation object.
	 * @return An instance of Attenuation representing no attenuation
	 */
	public static Attenuation NoAttenuation() {
		return new Attenuation() {
			public Type getType() { return Type.NONE; }
			public float getValue() { return 0; }
		};
	}

}
