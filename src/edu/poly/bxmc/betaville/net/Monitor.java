/**
 * 
 */
package edu.poly.bxmc.betaville.net;

import java.io.IOException;

import javax.swing.ProgressMonitorInputStream;

import org.apache.log4j.Logger;

/**
 * @author Skye Book
 *
 */
public class Monitor implements Runnable {
	private static final Logger logger = Logger.getLogger(Monitor.class);
	private ProgressInputStream monitor;
	private int maximum=0;
	private int available=0;
	private int lastAvailable=0;

	/**
	 * 
	 */
	public Monitor(ClientManager manager) {
		this.monitor=manager.getProgressInputStream();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		boolean hadData=false;
		while(available>0||!hadData){
			try {
				available = monitor.available();
				if(available>0){

					// if we're at the same place, there is no need to run again
					if(available==lastAvailable){
						continue;
					}

					// we may need to reset the maximum size for larger data streams
					if(available>maximum) maximum=available;
					else logger.info((maximum-available)+"/"+maximum);
					hadData=true;
				}
				else if(hadData){
					logger.info("Transmission Complete");
				}
				lastAvailable=available;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
