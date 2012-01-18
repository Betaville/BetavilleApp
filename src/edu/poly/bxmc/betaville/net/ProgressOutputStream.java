/**
 * 
 */
package edu.poly.bxmc.betaville.net;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * @author Skye Book
 *
 */
public class ProgressOutputStream extends FilterOutputStream {
	private static final Logger logger = Logger.getLogger(ProgressOutputStream.class);

	// how often to dispatch an update
	private int granularity = 4096;
	private int granularityCount = 0;

	private int bytesWritten = 0;

	/**
	 * @param outputStream
	 */
	public ProgressOutputStream(OutputStream outputStream) {
		super(outputStream);
	}

	public void resetCounter(){
		bytesWritten=0;
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

	private void updateBytesWritten(int incrementValue){
		bytesWritten+=incrementValue;
		granularityCount+=incrementValue;
		if(granularityCount>granularity){
			//logger.info("Wrote " + bytesWritten + " bytes");
			while(granularityCount>granularity){
				granularityCount = granularityCount-granularity;
			}
		}
	}

}
