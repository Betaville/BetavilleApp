/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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
package edu.poly.bxmc.betaville;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * Performs a shutdown of the Betaville application
 * @author Skye Book
 */
public class ShutdownManager {
	private static final Logger logger = Logger.getLogger(ShutdownManager.class);
	
	public static final ArrayList<IShutdownProcedure> shutdownProcedures = new ArrayList<IShutdownProcedure>();
	
	public static void doSafeShutdown() {
		BetavilleNoCanvas.getGame().shutdown();
		BetavilleNoCanvas.getUpdater().shutdown();
		logger.info("Doing Safe Shutdown");
		try {
			NetPool.getPool().getSecureConnection().endSession(SettingsPreferences.getSessionToken());
		} catch (UnknownHostException e) {
			logger.error("Could not reach server during shutdown", e);
		} catch (IOException e) {
			logger.error("Could not log out during shutdown", e);
		}
		logger.info("Shutting down network connections");
		NetPool.getPool().cleanAll();
		logger.info("Cleaning up Main Thread Pool");
		SettingsPreferences.getThreadPool().shutdownNow();
		logger.info("Cleaning up GUI Thread Pool");
		SettingsPreferences.getGUIThreadPool().shutdownNow();
		System.exit(0);
	}
	
	/**
	 * Provides a way of adding steps to the shutdown sequence
	 * @author Skye Book
	 */
	public interface IShutdownProcedure{
		
		/**
		 * The meat of the procedure.  Put the code
		 * that needs to be run here. 
		 * @return Lets the shutdown manager know whether
		 * the procedure ran successfully.  Note that
		 * if this method notifies the manager that it
		 * was unsuccessful, the shutdown will terminate.
		 */
		public boolean runProcedure();
	}
}