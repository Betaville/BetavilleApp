/**
 * 
 */
package edu.poly.bxmc.betaville.obstructions;

/**
 * @author Skye Book
 *
 */
public class ObstructionChangedEvent {
	private int obstructionDesignID;
	private int triggerDesignID;

	/**
	 * 
	 */
	public ObstructionChangedEvent(int obstructionDesignID, int triggerDesignID) {
		this.obstructionDesignID=obstructionDesignID;
		this.triggerDesignID=triggerDesignID;
	}

	/**
	 * @return the obstructionDesignID
	 */
	public int getObstructionDesignID() {
		return obstructionDesignID;
	}

	/**
	 * @return the triggerDesignID
	 */
	public int getTriggerDesignID() {
		return triggerDesignID;
	}
	
	

}
