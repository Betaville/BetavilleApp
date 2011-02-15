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
import java.util.BitSet;

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;

/**
 * SoundSource represents a sound and its origin in space.
 * A SoundSource basically knows where to find the file to be played,
 * has trigger bounds and bunch of flags to indicate desired behaviors, 
 * such as looping, streaming, etc. It's up to the actual sound system 
 * to handle these flags. 
 * 
 * @author Peter Schulz
 */
public class SoundSource {
	
	public interface Attenuation {
		static enum Type { NONE, LINEAR, ROLLOFF }
		Type getType();
		float getValue();
	}
	
	private static float defaultRadius = 50;
	
	/**
	 * Returns the default radius.
	 * @return
	 */
	public static float getDefaultRadius() {
		return defaultRadius;
	}

	/**
	 * Sets the default radius.
	 * @param value
	 */
	public static void setDefaultRadius(float value) {
		defaultRadius = value;
	}
	
	private Attenuation attenuation;
	private float volume;
	private URL url;
	private String name;
	private BoundingVolume boundingVolume;
	
	/**
	 * Squared approximated fading radius.
	 */
	private float fadeRadiusSquared;
	
	private static int numFlags		= 0;
	
	public static final int LOOP	= 0;
	public static final int CULL	= ++numFlags;
	public static final int PRIO	= ++numFlags;
	public static final int STREAM	= ++numFlags;
	
	public static final int EXIT_FINISH 	= ++numFlags;
	public static final int EXIT_STOP		= ++numFlags;
	public static final int EXIT_PAUSE		= ++numFlags;
	public static final int FADE_BACKGROUND = ++numFlags;
	
	/**
	 * For internal use.
	 */
	public static final int IN_RANGE		= ++numFlags;
	
	private BitSet flags = new BitSet(numFlags);
	
	/**
	 * Creates a new SoundSource
	 * @param name Unique meaningful name
	 * @param url Resource locator
	 * @param bounds Trigger bounds
	 * @param fadeRadius Approximated fade radius, used for faster fading calculations
	 * @param attenuation Attenuation behavior
	 * @param volume Max volume
	 */
	public SoundSource(String name, URL url, BoundingVolume bounds, float fadeRadius, Attenuation attenuation, float volume) {
		this.name				= name;
		this.url				= url;
		this.volume				= volume;
		this.attenuation		= attenuation;
		this.boundingVolume		= bounds;
		this.fadeRadiusSquared	= fadeRadius * fadeRadius;
		System.out.println(bounds);
	}
	
	/**
	 * Creates a new SoundSource with spherical bounds
	 * @param name Unique meaningful name
	 * @param url Resource locator
	 * @param center Location of the sound
	 * @param attenuation Attenuation behavior
	 * @param volume Max volume
	 */
	public SoundSource(String name, URL url, Vector3f center, Attenuation attenuation, float volume) {
		this(name, url, new BoundingSphere(defaultRadius, center), defaultRadius, attenuation, volume);
	}
	
	/**
	 * Creates a new SoundSource with spherical bounds with a specified radius
	 * @param name Unique meaningful name
	 * @param url Resource locator
	 * @param center Location of the sound
	 * @param radius The bounding sphere's radius
	 * @param attenuation Attenuation behavior
	 * @param volume Max volume
	 */
	public SoundSource(String name, URL url, Vector3f center, float radius, Attenuation attenuation, float volume) {
		this(name, url, new BoundingSphere(radius, center), radius, attenuation, volume);
	}
	
	/**
	 * Returns the attenuation behavior.
	 * @return The attenuation behavior object
	 */
	public Attenuation getAttenuation() {
		return attenuation;
	}

	/**
	 * Returns the location of this source.
	 * @return The location vector
	 */
	public Vector3f getCenter() {
		return boundingVolume.getCenter();
	}
	
	/**
	 * Returns state of the specified flag.
	 * @param flag Flag to be checked
	 * @return True if enabled false else
	 */
	public boolean getFlag(int flag) {
		return flags.get(flag);
	}
	
	/**
	 * Returns this source's name.
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a value indicating the closeness of the specified point to this source's center.
	 * @param point The point to be tested
	 * @return A value between 0 and 1 
	 * where 0 means distance equals fading radius and 1 means {@code point} equals the center
	 */
	public float getRelativeFadeDistance(Vector3f point) {
		return 1 - getCenter().distanceSquared(point) / fadeRadiusSquared;
	}
	
	/**
	 * Returns the resource locator.
	 * @return The source URL
	 */
	public URL getURL() {
		return url;
	}
	
	/**
	 * Returns the filename of this source's resource locator.
	 * @return The source filename
	 */
	public String getURLFileName(){
		return url.toString().substring(url.toString().lastIndexOf("/")+1, url.toString().length());
	} 
	
	/**
	 * Returns the volume.
	 * @return The max volume
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * Sets the indicated flag (enables it)
	 * @param flag Flag to be enabled
	 * @return The source
	 */
	public SoundSource setFlag(int ... flags) {
		for (int flag : flags) 
			this.flags.set(flag);
		return this;
	}
	
	/**
	 * Enables/disables specified flag.
	 * @param flag The flag to be (un)set
	 * @param value True to set, false to unset the flag
	 * @return The source
	 */
	public SoundSource setFlag(int flag, boolean value) {
		flags.set(flag, value);
		return this;
	}
	
	/**
	 * Tests if the specified point triggers this source.
	 * @param point The point to be tested
	 * @return True if {@code point} lies inside the trigger bounds, false else
	 */
	public boolean triggeredBy(Vector3f point) {
		return boundingVolume.contains(point);
	}
}
