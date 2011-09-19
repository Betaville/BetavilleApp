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
package edu.poly.bxmc.betaville.jme.exporters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.jme.image.Image;

/**
 * @author Skye Book
 *
 */
public class ImageExporter {
	public static boolean exportImage(Image image, URL imageDestination){
		
		BufferedImage bi=null;
		ByteBuffer buffer = image.getData().get(0);
		buffer.rewind();
		
		
		// create the buffered image based on the format
		switch (image.getFormat()){
		case RGB8:
			System.out.println("Using RGB8");
			bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			while(buffer.hasRemaining()){
				for(int h=0; h<image.getHeight(); h++){
					for(int w=0; w<image.getWidth(); w++){
						int b = (int) buffer.get();
						int g = (int) buffer.get();
						int r = (int) buffer.get();
						bi.setRGB(w, h, (b*65536)+(g*256)+r);
					}
				}
			}
			break;
		case RGBA8:
			System.out.println("Using RGBA8");
			bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			while(buffer.hasRemaining()){
				for(int h=0; h<image.getHeight(); h++){
					for(int w=0; w<image.getWidth(); w++){
//						byte a = buffer.get();
//						byte b = buffer.get();
//						byte g = buffer.get();
//						byte r = buffer.get();
						byte b = buffer.get();
						byte g = buffer.get();
						byte r = buffer.get();
						byte a = buffer.get();
						//bi.setRGB(w, h, (b*65536)+(g*256)+r);
						bi.getAlphaRaster().setDataElements(w, h, new byte[]{a,b,g,r});
						bi.setRGB(w, h, (b*65536)+(g*256)+r);
					}
				}
			}
			break;
			
			// If we don't support this format, don't load it
		default:
			return false;
		}
		
		
		
		
		
		while(buffer.hasRemaining()){
			for(int h=0; h<image.getHeight(); h++){
				for(int w=0; w<image.getWidth(); w++){
					int b = (int) (buffer.get());
					int g = (int) (buffer.get());
					int r = (int) (buffer.get());
					bi.setRGB(w, h, (b*65536)+(g*256)+r);
				}
			}
		}
		
		try {
			ImageIO.write(bi, "png", new File(imageDestination.getFile()));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
