/**
 * 
 */
package edu.poly.bxmc.betaville.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Skye Book
 *
 */
public abstract class DoubleClickActionListener implements ActionListener {
	
	private long last=-1;
	private int threshold = 1000;

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent action) {
		if(System.currentTimeMillis()-last<threshold){
			// reset the last value before calling the delegate method
			last = System.currentTimeMillis();
			
			doubleClickPerformed(action);
		}
		else{
			last = System.currentTimeMillis();
		}
	}
	
	/**
	 * 
	 * @param action
	 */
	public abstract void doubleClickPerformed(ActionEvent action);

}
