/**
 * 
 */
package edu.poly.bxmc.betaville.sound;

/**
 * Defines different behaviors that a SoundTrigger can invoke
 * upon the departure from a trigger area.
 * @author Skye Book
 */
public enum ExitAction {
	/**
	 * Will cause a sound to stop and not hold its position
	 * upon departure from the trigger area.
	 */
	STOP,
	
	/**
	 * Will cause a sound to stop and hold its position upon
	 * departure from the trigger area.
	 */
	PAUSE,
	
	/**
	 * Allows a sound to finish playing upon departure from the
	 * trigger area.
	 */
	FINISH
}
