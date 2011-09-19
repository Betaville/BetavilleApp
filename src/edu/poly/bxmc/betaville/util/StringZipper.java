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
package edu.poly.bxmc.betaville.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Skye Book
 *
 */
public class StringZipper {

	public static byte[] compress(String string) throws UnsupportedEncodingException, IOException{
		byte[] input = string.getBytes("UTF-8");
		Deflater deflator = new Deflater();
		deflator.setInput(input);

		ByteArrayOutputStream bo = new ByteArrayOutputStream(input.length);
		deflator.finish();
		byte[] buffer = new byte[1024];
		while(!deflator.finished())
		{
			int count = deflator.deflate(buffer);
			bo.write(buffer, 0, count);
		}
		bo.close();
		byte[] output = bo.toByteArray();
		return output;
	}

	public static String uncompress(byte[] bytes) throws UnsupportedEncodingException, IOException, DataFormatException{
		Inflater inflator = new Inflater();
		inflator.setInput(bytes);

		ByteArrayOutputStream bo = new ByteArrayOutputStream(bytes.length);
		byte[] buffer = new byte[1024];
		while(!inflator.finished())
		{
			int count = inflator.inflate(buffer);
			bo.write(buffer, 0, count);
		}
		bo.close();
		byte[] output = bo.toByteArray();
		return new String(output, "UTF-8");
	}
}
