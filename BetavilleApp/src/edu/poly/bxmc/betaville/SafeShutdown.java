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
package edu.poly.bxmc.betaville;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.loaders.util.LocalExporter;
import edu.poly.bxmc.betaville.logging.LogManager;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class SafeShutdown {
	private static Logger logger = Logger.getLogger(SafeShutdown.class);
	
	public static void doSafeShutdown() {
		BetavilleNoCanvas.getGame().shutdown();
		BetavilleNoCanvas.getUpdater().shutdown();
		logger.info("Doing Safe Shutdown");
		NetPool.getPool().getSecureConnection().endSession(SettingsPreferences.getSessionToken());
		saveLocalDesigns();
		logger.info("Flushing loggers");
		LogManager.flushFileHandler();
		logger.info("Shutting down network connections");
		NetPool.getPool().cleanAll();
		logger.info("Cleaning up Main Thread Pool");
		SettingsPreferences.getThreadPool().shutdownNow();
		logger.info("Cleaning up GUI Thread Pool");
		SettingsPreferences.getGUIThreadPool().shutdownNow();
		System.exit(0);
	}
	
	public static void saveLocalDesigns(){
		for(Design d : SceneScape.getCity().getDesigns()){
			if(d.getFullIdentifier().endsWith("$local")){
				SettingsPreferences.getThreadPool().submit(new LocalExporter(d));
			}
		}
	}
}
