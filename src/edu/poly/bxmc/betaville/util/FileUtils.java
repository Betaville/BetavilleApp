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
package edu.poly.bxmc.betaville.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Skye Book
 *
 */
public class FileUtils {
	
	private static final int BUFFER_SIZE = 4096;
	
	/**
	 * 
	 * @param sourceFile
	 * @param destinationFile
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFile(File sourceFile, File destinationFile) throws IOException{
		FileInputStream fis = new FileInputStream(sourceFile);
		FileOutputStream fos = new FileOutputStream(destinationFile);
		
		int bufferSizeForThisUse = BUFFER_SIZE;
		
		byte[] readBuffer = new byte[bufferSizeForThisUse];
		int n=-1;
		while ((n = fis.read(readBuffer, 0, bufferSizeForThisUse)) != -1){
			fos.write(readBuffer, 0, n);
		}
		fis.close();
		fos.close();
		return true;
	}
	
	/**
	 * 
	 * @param sourceFile
	 * @param destinationFile
	 * @return
	 * @throws IOException
	 */
	public static boolean moveFile(File sourceFile, File destinationFile) throws IOException{
		if(copyFile(sourceFile, destinationFile)){
			return sourceFile.delete();
		}
		else return false;
	}
}
