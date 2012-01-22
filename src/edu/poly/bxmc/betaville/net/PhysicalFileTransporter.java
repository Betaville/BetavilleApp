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
package edu.poly.bxmc.betaville.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * <code>PhysicalFileTransporter</code> wraps a <code>byte[]</code>
 * into an object that stores the byte array length as well as information
 * on how to pack the object back into a physical file.  Used both server and
 * client side.
 * @author Skye Book
 *
 */
public class PhysicalFileTransporter implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PhysicalFileTransporter.class);
	private byte[] bytes;

	/**
	 * 
	 */
	public PhysicalFileTransporter(byte[] b) {
		bytes = b;
	}

	/**
	 * Gets the raw data packed in this transporter
	 * @return The raw data as it was supplied
	 */
	public byte[] getData(){
		return bytes;
	}

	public boolean writeToFileSystem(File fileToWrite){
		if(!fileToWrite.getParentFile().exists()){
			fileToWrite.getParentFile().mkdirs();
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fileToWrite);
			fos.write(bytes);
			fos.flush();
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			logger.fatal("IOException encountered.  Ensure that the following location is writable" +
					new String(fileToWrite.toString().substring(0, fileToWrite.toString().lastIndexOf("/"))), e);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Supports a maximum file of 2 GB
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static PhysicalFileTransporter readFromFileSystem(File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		byte[] contents = new byte[(int)file.length()];
		fis.read(contents);
		fis.close();
		PhysicalFileTransporter pft = new PhysicalFileTransporter(contents);
		return pft;
	}
}
