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
package edu.poly.bxmc.betaville.xml;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.jme.loaders.util.DriveFinder;

/**
 * Lists all of Betaville's default preferences and sets them
 * up if they are not currently available.
 * @author Skye Book
 *
 */
public class UpdatedPreferenceWriter {
	private static Logger logger = Logger.getLogger(UpdatedPreferenceWriter.class);
	

	/**
	 * @throws IOException 
	 * 
	 */
	public static void writeDefaultPreferences() throws IOException {
		logger.info("Writing default preferences");
		if(System.getProperty("betaville.display.fullscreen")==null) System.setProperty("betaville.display.fullscreen", "false");
		if(System.getProperty("betaville.display.resolution")==null) System.setProperty("betaville.display.resolution", "EMPTY");
		if(System.getProperty("betaville.display.textured")==null) System.setProperty("betaville.display.textured", "true");
		if(System.getProperty("betaville.display.model.loadonstart")==null) System.setProperty("betaville.display.model.loadonstart", "true");
		if(System.getProperty("betaville.display.fog.enabled")==null) System.setProperty("betaville.display.fog.enabled", "true");
		if(System.getProperty("betaville.display.terrain.usegenerated")==null) System.setProperty("betaville.display.terrain.usegenerated", "false");
		if(System.getProperty("betaville.sound.volume.master")==null) System.setProperty("betaville.sound.volume.master", "1.0");
		if(System.getProperty("betaville.cache.location")==null) System.setProperty("betaville.cache.location", new File(DriveFinder.getHomeDir().toString()+"/.betaville/cache/").toURI().toURL().toString());
		if(System.getProperty("betaville.cache.size")==null) System.setProperty("betaville.cache.size", "300");
		if(System.getProperty("betaville.server")==null)System.setProperty("betaville.server", "master.betaville.net");
		if(System.getProperty("betaville.startup.showsettings")==null) System.setProperty("betaville.startup.showsettings", "true");
		if(System.getProperty("betaville.startup.city")==null) System.setProperty("betaville.startup.city", "2");
		if(System.getProperty("betaville.updater.enabled")==null) System.setProperty("betaville.updater.enabled", "true");
		if(System.getProperty("betaville.updater.base")==null) System.setProperty("betaville.updater.base", "true");
		if(System.getProperty("betaville.updater.comments")==null) System.setProperty("betaville.updater.comments", "true");
		if(System.getProperty("betaville.ui.pyramids.offcolor")==null) System.setProperty("betaville.ui.pyramids.offcolor", "RGB 0 0 255");
		if(System.getProperty("betaville.ui.pyramids.oncolor")==null) System.setProperty("betaville.ui.pyramids.oncolor", "RGB 0 255 0");
		if(System.getProperty("betaville.net.usessl")==null) System.setProperty("betaville.net.usessl", "false");
		if(System.getProperty("betaville.kiosk.enabled")==null) System.setProperty("betaville.kiosk.enabled", "false");
		if(System.getProperty("betaville.kiosk.password")==null) System.setProperty("betaville.kiosk.password", ""); // Uses an SHA1 hash
		if(System.getProperty("betaville.kiosk.requirepass")==null) System.setProperty("betaville.kiosk.requirepass", "false");
		if(System.getProperty("betaville.kiosk.refresh")==null) System.setProperty("betaville.kiosk.refresh", "0");
		
		PreferenceWriter pr = new PreferenceWriter();
		pr.writeData();
	}

}