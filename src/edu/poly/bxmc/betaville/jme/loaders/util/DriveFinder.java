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
package edu.poly.bxmc.betaville.jme.loaders.util;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

import edu.poly.bxmc.betaville.SettingsPreferences;

/**
 * @author Skye Book
 *
 */
public class DriveFinder {
	public static ArrayList<File> getPartitions(){
		ArrayList<File> roots = new ArrayList<File>();
		if(System.getProperty("os.name").toLowerCase().startsWith("win")){

			String[] names = {"A:\\","B:\\","C:\\","D:\\","E:\\","F:\\","G:\\","H:\\",
					"I:\\","J:\\","K:\\","L:\\","M:\\","N:\\","O:\\","P:\\","Q:\\",
					"R:\\","S:\\","T:\\","U:\\","V:\\","W:\\","X:\\","Y:\\","Z:\\"};

			for(int i=0; i<names.length; i++){
				File f = new File(names[i]);
				if(f.exists()){
					roots.add(f);
				}
			}
		}
		else if(System.getProperty("os.name").toLowerCase().startsWith("mac")){
			File volFolder = new File("/Volumes/");
			for(String volume : volFolder.list()){
				File f = new File("/Volumes/"+volume+"/");
				roots.add(f);
			}
		}
		else if(System.getProperty("os.name").toLowerCase().startsWith("lin")){}
		return roots;
	}

	public static File getHomeDir(){
		File homeDir = FileSystemView.getFileSystemView().getHomeDirectory();
		
		// Java thinks the Windows home directory is the desktop folder.
		if(System.getProperty("os.name").startsWith("Windows")){
			homeDir = new File(homeDir.getParent());
		}
		
		return homeDir;
	}
	
	public static File getBetavilleFolder(){
		return new File(getHomeDir().toString()+"/.betaville/");
	}
	
	public static File getServerFolder(){
		File serverFolder = new File(getBetavilleFolder().toString()+"/"+SettingsPreferences.getServerIP()+"/");
		if(!serverFolder.exists()) serverFolder.mkdir();
		return serverFolder;
	}
	
	public static File getBookmarksFolder(){
		File bookmarksFolder = new File(getServerFolder().toString()+"/bookmarks/");
		if(!bookmarksFolder.exists()) bookmarksFolder.mkdir();
		return bookmarksFolder;
	}
}
