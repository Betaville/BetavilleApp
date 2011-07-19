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
package edu.poly.bxmc.betaville.terrain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.SettingsPreferences;

/**
 * Provides a cache for map tiles.
 * @author Skye Book
 *
 */
public class TileCache {
	private static final Logger logger = Logger.getLogger(TileCache.class);
	
	private File cacheLocation=null;
	private String tileServer;
	
	public TileCache(String tileServer){
		this.tileServer=tileServer;
		try {
			cacheLocation = new File(SettingsPreferences.getDataFolder().toURI());
			cacheLocation = new File(cacheLocation.toString()+"/tiles/"+tileServer.replace("http://", ""));
			if(!cacheLocation.isDirectory()) cacheLocation.mkdirs();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public URL requestImageTile(int zoom, int x, int y){
		if(doesTileExist(zoom, x, y)){
			// return a locator for the tile
			try {
				return new File(cacheLocation.toString()+"/"+createImagePath(zoom, x, y)).toURI().toURL();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			// download the tile
			try {
				// Yes, feel free to laugh at the rhyming nature of this variable name :)
				File tileFile = new File(cacheLocation.toString()+"/"+createImagePath(zoom, x, y));
				// the zoom and x folders may not be created yet
				createZoomAndXFolder(zoom, x).mkdirs();
				FileOutputStream fos = new FileOutputStream(tileFile);
				
				/*
				
				// request the tile from the server
				logger.info("Requesting tile from " + tileServer+"/"+createPath(zoom, x, y));
				InputStream is = new URL(tileServer+"/"+createPath(zoom, x, y)).openStream();
				
				// read from the URL into a local buffer
				byte[] buffer = new byte[is.available()];
				is.read(buffer);
				
				// write the buffer to a file
				fos.write(buffer);
				fos.flush();
				
				*/
				
			    ReadableByteChannel rbc = Channels.newChannel(new URL(tileServer+"/"+createImagePath(zoom, x, y)).openStream());
			    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
				
				// return a locator for the tile
				return tileFile.toURI().toURL();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	private String createImagePath(int zoom, int x, int y){
		return zoom+"/"+x+"/"+y+".png";
	}
	
	private String createColladaPath(int zoom, int x, int y){
		return zoom+"/"+x+"/"+y+".dae";
	}
	
	public File createZoomAndXFolder(int zoom, int x){
		return new File(cacheLocation.toString()+"/"+zoom+"/"+x);
	}
	
	public boolean doesTileExist(int zoom, int x, int y){
		return doesImageTileExist(createImagePath(zoom, x, y));
	}
	
	public boolean doesImageTileExist(String tilePath){
		return new File(cacheLocation.toString()+"/"+tilePath).exists();
	}
	
	public boolean doesColladaTileExist(int zoom, int x, int y){
		return doesImageTileExist(createImagePath(zoom, x, y));
	}
	
	public boolean doesColladaTileExist(String tilePath){
		return new File(cacheLocation.toString()+"/"+tilePath).exists();
	}
	
	/**
	 * @return the tileServer
	 */
	public String getTileServer() {
		return tileServer;
	}

	/**
	 * @param tileServer the tileServer to set
	 */
	public void setTileServer(String tileServer) {
		this.tileServer = tileServer;
	}
}
