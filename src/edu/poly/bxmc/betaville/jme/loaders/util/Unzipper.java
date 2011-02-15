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
package edu.poly.bxmc.betaville.jme.loaders.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <code>Unzipper</code> provides a static method to unzip a file.
 * @author Skye Book
 *
 */
public class Unzipper {
	public static void unzip(URL modelURL)
	{
		ZipEntry zipEntry;
		try {
			File kmzFile = new File(modelURL.toURI());
			File dataDir = new File("data/" + kmzFile.getName().subSequence(0, kmzFile.getName().length()-3) + "/");
			dataDir.mkdir();
			FileInputStream fileIn = new FileInputStream(kmzFile);
			ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(fileIn));
			while((zipEntry = zipIn.getNextEntry())!=null)
			{
				String entryName = zipEntry.getName();

				// handle missing directories -- PUT IN FUNCTION
				System.out.println("entry name: " + entryName);
				if(entryName.contains("/"))
				{
					// make directory
					//File dirToMake = new File();
					System.out.println("dealing with directory");
					String[] directories = zipEntry.getName().split("/");
					// iterate through the directories array with the
					// exception of the last result (which is the filename)
					for(int i=0; i<(directories.length-1);i++)
					{
						System.out.println("creating directory: " + directories[i]);
						File newDir = new File(directories[i]);
						newDir.mkdir();
						System.out.println("dir done");
					}
				}
				else{
					// make file
				}

				//END DIRECTORY HANDLING

				int size;
				byte[] buffer = new byte[1024];


				FileOutputStream fileOut = new FileOutputStream(dataDir.getAbsolutePath()+zipEntry.getName());
				BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut, buffer.length);




				while((size=zipIn.read(buffer,0,buffer.length)) != -1)
				{
					bufferedOut.write(buffer,0,size);
				}

				bufferedOut.flush();
				bufferedOut.close();

			}
			zipIn.close();
			fileIn.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		/*
		try {
			unzip(new File("data/test.zip").toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */


		int BUFFER = 2048;
		try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new 
			FileInputStream(new File("data/test.zip"));
			ZipInputStream zis = new 
			ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while((entry = zis.getNextEntry()) != null) {
				System.out.println("Extracting: " +entry);
				int count;
				byte data[] = new byte[BUFFER];
				// write the files to the disk
				if(!entry.getName().endsWith(".DS_Store") && !entry.getName().startsWith("__MACOSX")){
					FileOutputStream fos = new 
					FileOutputStream(entry.getName());
					dest = new 
					BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) 
							!= -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				}
			}
			zis.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}