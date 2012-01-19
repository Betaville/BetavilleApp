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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import edu.poly.bxmc.betaville.net.ProgressInputStream.ProgressInputListener;

/**
 * @author Skye Book
 *
 */
public class ProgressOutputStream extends FilterOutputStream {

	// how often to dispatch an update
	private int granularity = 4096;
	private int granularityCount = 0;

	private int bytesWritten = 0;
	
	private ProgressOutputListener listener = null;

	/**
	 * @param outputStream
	 */
	public ProgressOutputStream(OutputStream outputStream) {
		super(outputStream);
	}

	public void resetCounter(){
		bytesWritten=0;
		listener = null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.FilterOutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException{
		out.write(b);
		updateBytesWritten(b.length);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.FilterOutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException{
		out.write(b, off, len);
		updateBytesWritten(len);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.FilterOutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException{
		out.write(b);
		updateBytesWritten(1);
	}
	
	public int getBytesWritten(){
		return bytesWritten;
	}

	private void updateBytesWritten(int incrementValue){
		bytesWritten+=incrementValue;
		granularityCount+=incrementValue;
		if(granularityCount>granularity){
			listener.writeProgressUpdate(bytesWritten);
			while(granularityCount>granularity){
				granularityCount = granularityCount-granularity;
			}
		}
	}
	
	public void setListener(ProgressOutputListener listener){
		this.listener = listener;
	}
	
	public interface ProgressOutputListener{
		public void writeProgressUpdate(int bytesWritten);
	}

}
