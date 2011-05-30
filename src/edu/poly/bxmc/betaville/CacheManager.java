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
package edu.poly.bxmc.betaville;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.logging.LogManager;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.ClientManager;
import edu.poly.bxmc.betaville.net.Monitor;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.net.PhysicalFileTransporter;
import edu.poly.bxmc.betaville.net.UnprotectedManager;

/**
 * Manages Betaville's cache directory.
 * @author Skye Book
 *
 */
public class CacheManager {
	private static final Logger logger = Logger.getLogger(CacheManager.class);
	
	private static CacheManager cm = null;
	
	// Holds the maximum size of the cache in MB
	private int maxSize;
	
	// Stores the current size of the cache in KB
	private int currentSize;

	private HashMap<String,Long> files;
	
	/**
	 * Private constructor to enforce singleton class.
	 */
	private CacheManager() {
		files = new HashMap<String,Long>();
		countFiles();
	}
	
	private void countFiles(){
		try {
			File cacheDir = new File(SettingsPreferences.getDataFolder().toURI());
			if(cacheDir.isDirectory()){
				File[] dirFiles = cacheDir.listFiles();
				for(int i=0; i<dirFiles.length; i++){
					doFileRegistration(dirFiles[i]);
				}
				LogManager.enterLog(Level.INFO, "Cache has " + dirFiles.length + " files at a size of " + (currentSize/1000 )+ "MB", CacheManager.class.getName());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private boolean findFile(String file){
		File target;
		try {
			target = new File(new URL(SettingsPreferences.getDataFolder()+file).toURI());
			return target.exists();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean findThumb(int designID){
		File target;
		try {
			target = new File(new URL(SettingsPreferences.getDataFolder()+"thumbnail/"+designID+".png").toURI());
			logger.debug("Looking for thumbnail in " + target.toString());
			return target.exists();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getSizeInMB(String fileName){
		Long value = files.get(removeExtension(fileName));
		if(value!=null){
			return (int)(value/1000);
		}
		else return 0;
	}
	
	/**
	 * Adds a file to those currently tracked by the cache
	 * @param fileName
	 */
	private void registerFileInCache(String fileName){
		File file = new File(SettingsPreferences.getDataFolder()+fileName);
		if(file.exists()){
			doFileRegistration(file);
			if(maxSize<currentSize){
				// Do something drastic
			}
		}
	}
	
	private void doFileRegistration(File file){
		if(file.isFile()){
			files.put(removeExtension(file.getName()), file.length());
			currentSize+=(file.length()/1000);
		}
	}
	
	public void deleteFile(String fileName){
		File file = new File(SettingsPreferences.getDataFolder()+fileName);
		if(file.exists()){
			if(file.delete()){
				files.remove(removeExtension(fileName));
			}
		}
	}
	
	/**
	 * Removes file from the cache that are not in the same
	 * UTM zone as the given coordinate.
	 * @param currentLocation The coordinate from which to
	 * determine which UTM zone's files <em>not</em> to delete.
	 */
	public void maintain(UTMCoordinate currentLocation){
		Iterator<Design> it = SceneScape.getCity().getDesigns().iterator();
		while(it.hasNext()){
			Design design = it.next();
			if(design.getCoordinate().getLatZone()!=currentLocation.getLatZone()
					|| design.getCoordinate().getLonZone()!=currentLocation.getLonZone()){
				deleteFile(design.getFilepath());
			}
		}
	}
	
	/**
	 * Strips a filename, and the final dot, from a String
	 * @param name The filename from which to remove the extension
	 * @return The shortened filename
	 */
	private String removeExtension(String name){
		return name.substring(0, name.lastIndexOf("."));
	}
	
	private boolean doRequestFile(int designID, String filename, UnprotectedManager manager, boolean keepAlive){
		if(!findFile(filename)){
			SettingsPreferences.getThreadPool().submit(new Monitor((ClientManager)manager));
			PhysicalFileTransporter pft = manager.requestFile(designID);
			if(!keepAlive){
				manager.close();
			}
			if(pft!=null){
				try {
					pft.writeToFileSystem(new File(new URL(SettingsPreferences.getDataFolder()+filename).toURI()));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				registerFileInCache(filename);
				return true;
			}
			else return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * Request a file from the server using an externally
	 * maintained client manager.
	 * @param designID
	 * @param filename
	 * @param manager
	 * @return
	 */
	public boolean requestFile(int designID, String filename, UnprotectedManager manager){
		if(findFile(filename)) return true;
		else return doRequestFile(designID, filename, manager, true);
	}
	
	/**
	 * Request a file using a ClientManager created from within this method.
	 * @param designID
	 * @param filename
	 * @return
	 */
	public boolean requestFile(int designID, String filename){
		if(findFile(filename)) return true;
		else return doRequestFile(designID, filename, NetPool.getPool().getConnection(), true);
	}
	
	/**
	 * Requests the thumbnail for the specified design id
	 * @param designID
	 * @return
	 */
	public boolean requestThumbnail(int designID){
		if(!findThumb(designID)){
			logger.debug("Requesting a thumbnail for design "+designID);
			PhysicalFileTransporter pft = NetPool.getPool().getConnection().requestThumbnail(designID);
			if(pft!=null){
				try {
					File thumbFolder = new File(new URL(SettingsPreferences.getDataFolder()+"thumbnail/").toURI());
					File thumbFile = new File(new URL(SettingsPreferences.getDataFolder()+"thumbnail/"+designID+".png").toURI());
					if(thumbFolder.mkdirs()) logger.info("Thumbnail folder created");
					pft.writeToFileSystem(thumbFile);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				return true;
			}
			else{
				logger.debug("thumbnail PFT is null");
				return false;
			}
		}
		else{
			return true;
		}
	}
	
	public static URL getCachedThumbnailURL(int designID){
		try {
			return new URL(SettingsPreferences.getDataFolder().toString().substring(0, SettingsPreferences.getDataFolder().toString().length()-1)+"thumbnail/"+designID+".png");
		} catch (MalformedURLException e) {
			logger.error("A bad URL was created when generating the cached thumbnail URL", e);
			return null;
		}
	}
	
	/**
	 * @return The static instance of the {@link CacheManager}
	 */
	public static CacheManager getCacheManager(){
		if(cm!=null){
			return cm;
		}
		else{
			cm = new CacheManager();
			return cm;
		}
	}
}