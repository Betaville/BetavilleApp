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
package edu.poly.bxmc.betaville.sound;

import java.net.URL;

import org.apache.log4j.Logger;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * A <code>SoundTrigger</code> is a point in
 * space that, upon entry or violation, can
 * trigger a sound to be played.  The trigger
 * must be checked periodically to determine
 * if a sound is to be played.  Simply put,
 * this is not a self-updating object.
 * @author Skye Book
 *
 */
public class SoundTrigger {
	private static Logger logger = Logger.getLogger(SoundTrigger.class);
	
	private Vector3f center;
	private Vector3f nw;
	private Vector3f ne;
	private Vector3f se;
	
	private URL soundURL;
	private String name;
	private boolean loop;
	private boolean nameLock=false;
	
	private boolean inRangeOnLastUpdate=false;
	private boolean playingOnLastUpdate=false;
	private ExitAction exitAction;
	
	private boolean forceFades=true;
	
	private float volume;
	private float rolloffFactor;
	

	/**
	 * Creates a new SoundTrigger
	 * @param name - Name of new <code>SoundTrigger</code>.  This is the
	 * name to use when removing a trigger from <code>SoundGameState</code>.
	 * @param soundURL - <code>URL</code> pointing to the sound that we wish
	 * to play.
	 * @param locationOfTrigger - Location of <code>SoundTrigger</code>,
	 * this will always be the center of your trigger.
	 * @param northSouth - The size of the north-south dimension of this trigger in meters
	 * @param sizeY - Size of the area influenced by the <code>SoundTrigger</code>
	 * on the Y plane.  Note that this is not the distance from
	 * the edge of the trigger to the location of broadcast, but rather from border
	 * to border.
	 * @param eastWest - The size of the east-west dimension of this trigger in meters
	 * @param exitAction - The action to take upon leaving the trigger area
	 * @param loop - Whether or not to loop the trigger's sound
	 * @param forceFades - Whether or not triggering this object causes other sounds to fade down
	 * @param volume - The volume of this trigger's sound
	 * @param rolloffFactor - The rolloff factor of this trigger's sound
	 * @see ExitAction
	 */
	public SoundTrigger(String name, URL soundURL, ILocation locationOfTrigger, float northSouth, float sizeY, float eastWest,
			ExitAction exitAction, boolean loop, boolean forceFades, float volume, float rolloffFactor) {
		this.name=name;
		this.soundURL=soundURL;
		this.loop=loop;
		this.exitAction=exitAction;
		this.forceFades=forceFades;
		setVolume(volume);
		this.rolloffFactor=rolloffFactor;
		center = JME2MapManager.instance.locationToBetaville(locationOfTrigger);
		nw = new Vector3f(center.getX()+Scale.fromMeter(northSouth/2), sizeY, center.getZ()-Scale.fromMeter(eastWest)/2);
		ne = new Vector3f(center.getX()+Scale.fromMeter(northSouth/2), sizeY, center.getZ()+Scale.fromMeter(eastWest)/2);
		se = new Vector3f(center.getX()-Scale.fromMeter(northSouth/2), sizeY, center.getZ()+Scale.fromMeter(eastWest)/2);
	}

	/**
	 * Creates a new SoundTrigger using the default sound and rolloff
	 * @param name - Name of new <code>SoundTrigger</code>.  This is the
	 * name to use when removing a trigger from <code>SoundGameState</code>.
	 * @param soundURL - <code>URL</code> pointing to the sound that we wish
	 * to play.
	 * @param locationOfTrigger - Location of <code>SoundTrigger</code>,
	 * this will always be the center of your trigger.
	 * @param northSouth - The size of the north-south dimension of this trigger in meters
	 * @param sizeY - Size of the area influenced by the <code>SoundTrigger</code>
	 * on the Y plane.  Note that this is not the distance from
	 * the edge of the trigger to the location of broadcast, but rather from border
	 * to border.
	 * @param eastWest - The size of the east-west dimension of this trigger in meters
	 * @param exitAction - The action to take upon leaving the trigger area
	 * @param loop - Whether or not to loop the trigger's sound
	 * @param forceFades - Whether or not triggering this object causes other sounds to fade down
	 * @param volume - The volume of this trigger's sound
	 * @param rolloffFactor - The rolloff factor of this trigger's sound
	 * @see ExitAction
	 * @see SoundValues#DEFAULT_ROLLOFF
	 * @see SoundValues#DEFAULT_VOLUME
	 */
	public SoundTrigger(String name, URL soundURL, ILocation locationOfTrigger, float northSouth, float sizeY, float eastWest,
			ExitAction exitAction, boolean loop, boolean forceFades) {
		this(name, soundURL, locationOfTrigger, northSouth, sizeY, eastWest, exitAction, loop, forceFades, SoundValues.DEFAULT_VOLUME,
				SoundValues.DEFAULT_ROLLOFF);
	}
	
	public boolean isViolated(Vector3f pointToTest){
		if(pointToTest.getX() <= ne.getX()  &&
				pointToTest.getX() >= se.getX() &&
				pointToTest.getY() <= nw.getY() &&
				pointToTest.getY() >= 0 &&
				pointToTest.getZ() <= ne.getZ() &&
				pointToTest.getZ() >= nw.getZ()){
			return true;
		}
		else return false;
	}
	
	/**
	 * Gets the name of the <code>SoundTrigger</code>.
	 * @return <code>SoundSystem</code>'s name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Sets a new name for this trigger.
	 * @param name The new name for this trigger, names must be unique
	 */
	public void setName(String name){
		if(!nameLock){
			this.name=name;
		}
		else logger.warn("This trigger is locked, its name may not be modified until it is unlocked");
	}
	
	public void lockName(){
		nameLock=true;
	}
	
	public void unlockName(){
		nameLock=false;
	}
	
	public URL getURL(){
		return soundURL;
	}
	
	public String getURLFileName(){
		return soundURL.toString().substring(soundURL.toString().lastIndexOf("/")+1, soundURL.toString().length());
	}
	
	public boolean isLooped(){
		return loop;
	}
	
	public Vector3f getCenter(){
		return center;
	}
	
	public boolean wasInRangeOnLastUpdate(){
		return inRangeOnLastUpdate;
	}
	
	public void isInRangeOnCurrentUpdate(boolean isInRange){
		inRangeOnLastUpdate=isInRange;
	}
	
	public ExitAction getExitAction(){
		return exitAction;
	}
	
	public void setExitAction(ExitAction exitAction){
		this.exitAction=exitAction;
	}
	
	/**
	 * @return the volume
	 */
	public float getVolume() {
		return volume;
	}
	
	
	/**
	 * Sets the volume of this trigger.
	 * @param volume The volume to set, which gets clamped between 0 and 1
	 */
	public void setVolume(float volume){
		if(volume>1) this.volume = 1;
		else if(volume<0) this.volume=0;
		else this.volume=volume;
	}

	/**
	 * @return the rolloffFactor
	 */
	public float getRolloffFactor() {
		return rolloffFactor;
	}
	
	/**
	 * @param factor
	 */
	public void setRolloffFactor(float factor){
		if(factor>1) rolloffFactor=1;
		else if(factor<0) rolloffFactor=0;
		else rolloffFactor=factor;
	}

	/**
	 * @return the forcesFade
	 */
	public boolean doesForceFade() {
		return forceFades;
	}

	/**
	 * @param forcesFade the forcesFade to set
	 */
	public void setForcesFade(boolean forcesFade) {
		this.forceFades = forcesFade;
	}

	/**
	 * @return the playingOnLastUpdate
	 */
	public boolean wasPlayingOnLastUpdate() {
		return playingOnLastUpdate;
	}

	/**
	 * @param playingOnLastUpdate the playingOnLastUpdate to set
	 */
	public void isPlayingOnCurrentUpdate(boolean playingOnLastUpdate) {
		this.playingOnLastUpdate = playingOnLastUpdate;
	}
}
