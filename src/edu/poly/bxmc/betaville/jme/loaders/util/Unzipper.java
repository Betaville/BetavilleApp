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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Provides a static method to unzip an archive.
 * @author Skye Book
 *
 */
public class Unzipper {
	private static final int BUFFER_SIZE = 2048;

	/**
	 * Decompresses a zip archive
	 * @param file The zip file to decompress
	 * @param destinationFolder The file to send the extracted files to.  A null argument will cause the file to be unzipped in its location
	 */
	public static void unzip(File file, File destinationFolder){
		try {
			BufferedOutputStream dest = null;
			if(destinationFolder==null) destinationFolder = new File(file.toString().substring(0, file.toString().indexOf(".")));
			else destinationFolder = new File(destinationFolder+"/"+getFilename(file));
			System.out.println("folder: " + destinationFolder);
			if(!destinationFolder.exists()){
				destinationFolder.mkdirs();
			}
			FileInputStream fis = new 
					FileInputStream(file);
			ZipInputStream zis = new 
					ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while((entry = zis.getNextEntry()) != null) {
				System.out.println("Extracting: " +entry);

				// write the files to the disk
				if(!entry.getName().endsWith(".DS_Store") && !entry.getName().startsWith("__MACOSX")){
					File localFolder = new File(destinationFolder+"/"+entry.getName().substring(0, entry.getName().indexOf("/")+1));
					localFolder.mkdirs();
					FileOutputStream fos = new FileOutputStream(destinationFolder+"/"+entry.getName());

					byte[] readBuffer = new byte[BUFFER_SIZE];
					int n;
					while ((n = zis.read(readBuffer, 0, BUFFER_SIZE)) != -1){
						fos.write(readBuffer, 0, n);
					}
					fos.close();
					fos = null;
				}
			}
			zis.close();
			fis.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static String getFilename(File file){
		if(file.toString().contains("\\")){
			System.out.println("Uses backslashes");
			return file.toString().substring(file.toString().lastIndexOf("\\")+1, file.toString().length());
		}
		else{
			return file.toString().substring(file.toString().lastIndexOf("/")+1, file.toString().length());
		}
	}
}