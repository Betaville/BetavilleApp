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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.poly.bxmc.betaville.jme.loaders.util.DriveFinder;
import edu.poly.bxmc.betaville.model.IUser.UserType;

/**
 * Singleton class for keeping track of user preferences as well as
 * for interfacing with the stored file: <i>home</i>/.betaville/startup.xml
 * @author Skye Book
 *
 */
public class SettingsPreferences {
	private static int sessionID=0;
	private static String sessionToken="";
	
	public static void setSessionID(int id){
		sessionID=id;
	}
	
	public static int getSessionID(){
		return sessionID;
	}
	
	public static void setSessionToken(String token){
		sessionToken=token;
	}
	
	public static String getSessionToken(){
		return sessionToken;
	}
	
	
	private static ExecutorService threadPool = Executors.newCachedThreadPool(new BetavilleThreadFactory("BetavilleMainThreadPool"));
	private static ExecutorService guiThreadPool = Executors.newCachedThreadPool(new BetavilleThreadFactory("BetavilleGUIThreadPool"));
	
	public static String getServerIP(){
		String ip = System.getProperty("betaville.server");
		if(ip.equals("devstreak.net")) ip = "master.betaville.net";
		return ip;
	}
	
	/**
	 * Options for the visual characteristics of the selected spatial.
	 * @author Skye Book
	 *
	 */
	public enum SelectionVisuals{WIREFRAME, GLOW_ORANGE};
	public static SelectionVisuals SELECTION_VISUAL = SelectionVisuals.GLOW_ORANGE;
	
	private static String userName;
	private static String userPass;
	private static UserType userType;
	private static boolean authenticated = false;
	
	private static URL DATA_FOLDER = null;
	
	public static URL getDataFolder(){
		if(DATA_FOLDER==null){
			try {
				DATA_FOLDER = new URL(System.getProperty("betaville.cache.location")+"/");
				File cacheFile = new File(DATA_FOLDER.toURI());
				if(!cacheFile.exists()){
					cacheFile.mkdirs();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return DATA_FOLDER;
	}
	
	public static File BROWSER_LOCATION = DriveFinder.getHomeDir();
	
	public static File getLocalServerFolder(){
		File file = new File(DriveFinder.getHomeDir().toString()+"/.betaville/"+getServerIP()+"/");
		if(!file.exists()) file.mkdirs();
		return new File(DriveFinder.getHomeDir().toString()+"/.betaville/"+getServerIP()+"/");
	}
	
	private static boolean shadowsOn=false;
	private static boolean waterOn=false;
	
	public static void setAuthenticated(boolean value){
		authenticated=value;
	}
	
	public static boolean isAuthenticated(){
		return authenticated;
	}
	
	public static void setUserPass(String user, String pass){
		userName=user;
		userPass=pass;
	}
	
	/**
	 * @return the userType
	 */
	public static UserType getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public static void setUserType(UserType userType) {
		SettingsPreferences.userType = userType;
	}

	public static String getPass(){
		return userPass;
	}
	
	public static String getUser(){
		return userName;
	}
	
	public static boolean isShadowsOn() {
		return shadowsOn;
	}

	public static boolean isWaterOn() {
		return waterOn;
	}

	public static ExecutorService getThreadPool(){
		return threadPool;
	}
	
	public static ExecutorService getGUIThreadPool(){
		return guiThreadPool;
	}
	
	public static boolean isTextured(){
		return Boolean.parseBoolean(System.getProperty("betaville.display.textured"));
	}
	
	public static boolean isFullscreen(){
		return Boolean.parseBoolean(System.getProperty("betaville.display.fullscreen"));
	}
	
	public static String getResolution(){
		return System.getProperty("betaville.display.resolution");
	}
	
	public static Boolean isFogEnabled(){
		return Boolean.parseBoolean(System.getProperty("betaville.display.fog.enabled"));
	}
	
	public static boolean showTutorialWindow(){
		String value = System.getProperty("betaville.tutorial.show");
		if(value!=null) return Boolean.parseBoolean(value);
		else return true;
	}
	
	public static boolean alwaysShowSettings(){
		String value = System.getProperty("betaville.startup.showsettings");
		if(value!=null) return Boolean.parseBoolean(value);
		else return true; 
	}
	
	public static boolean loadModelsOnStart(){
		String value = System.getProperty("betaville.display.model.loadonstart");
		if(value!=null) return Boolean.parseBoolean(value);
		else return true;
	}
	
	public static boolean useGeneratedTerrainEnabled(){
		return readTrueFalseValue("betaville.display.terrain.usegenerated");
	}
	
	private static boolean readTrueFalseValue(String property){
		String value = System.getProperty(property);
		if(value!=null) return Boolean.parseBoolean(value);
		else return true;
	}

	/**
	 * Gets the startup city described in the user's xml file on startup.
	 * @return The ID of the city, or 2 if the value was invalid.
	 */
	public static int getStartupCity(){
		String value = System.getProperty("betaville.startup.city");
		if(value==null) return 2;
		try{
			return Integer.parseInt(value);
		}catch (NumberFormatException e) {
			return 2;
		}
	}
}
