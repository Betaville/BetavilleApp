/**
 * 
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

		System.out.println("Original: "+input.length);
		System.out.println("Compressed: "+output.length);
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

		System.out.println("Original: "+bytes.length);
		System.out.println("Extracted: "+output.length);
		//String s = new String(output, "UTF-8");
		return new String(output, "UTF-8");
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws DataFormatException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, IOException, DataFormatException {
		String string = "Skye says hi seriously what the shit is this thing going to do to me right now";
		byte[] bytes = compress(string);
		String returned = uncompress(bytes);
	}

}
