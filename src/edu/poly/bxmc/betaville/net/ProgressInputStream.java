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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Skye Book
 *
 */
public class ProgressInputStream extends FilterInputStream {
	
	// how often to dispatch an update
	private int granularity = 4096;
	private int granularityCount = 0;

	private int bytesRead = 0;

	private ProgressInputListener listener = null;

	/**
	 * @param inputStream
	 */
	public ProgressInputStream(InputStream inputStream) {
		super(inputStream);
	}

	public void resetCounter(){
		bytesRead=0;
		listener = null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException{
		int bytesRead = in.read();
		updateBytesRead(bytesRead);
		return bytesRead;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException{
		int bytesRead = in.read(b);
		updateBytesRead(bytesRead);
		return bytesRead;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException{
		int bytesRead = in.read(b, off, len);
		updateBytesRead(bytesRead);
		return bytesRead;
	}

	public int getBytesRead(){
		return bytesRead;
	}

	private void updateBytesRead(int incrementValue){
		bytesRead+=incrementValue;
		granularityCount+=incrementValue;
		if(granularityCount>granularity){
			if(listener!=null) listener.readProgressUpdate(bytesRead);
			while(granularityCount>granularity){
				granularityCount = granularityCount-granularity;
			}
		}
	}
	
	public void setListener(ProgressInputListener listener){
		this.listener = listener;
	}

	public interface ProgressInputListener{
		public void readProgressUpdate(int bytesRead);
	}

}
