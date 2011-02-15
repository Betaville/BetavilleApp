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
package edu.poly.bxmc.betaville.aesthetics;

import java.util.ArrayList;

import com.jme.renderer.ColorRGBA;

/**
 * @author Skye Book
 * @author Anand C
 *
 */
public class ColorParser {
	private static String validColorRegex = "^(RGB( [0-9]{1,3}){3})|(RGBA( [0-9]{1,3}){4})";
	private static String numberPattern = "^([0-9]{1,3} ?){1,4}";
	
	public static ColorRGBA parseColor(String colorString) throws InvalidColorException{
		if(!colorString.matches(validColorRegex)) throw new InvalidColorException("Invalid Color Argument: " + colorString);

		String colorType = colorString.substring(0, colorString.indexOf(" "));
		colorString=colorString.substring(colorString.indexOf(" ")+1);
		
		System.out.println(colorType);
		System.out.println(colorString);

		ArrayList<Integer> vals = new ArrayList<Integer>();
		while(colorString.matches(numberPattern)){
			System.out.println("matched");
			try{
				vals.add(Integer.parseInt(colorString.substring(0, colorString.indexOf(" "))));
			}catch(IndexOutOfBoundsException e){
				vals.add(Integer.parseInt(colorString.substring(0, colorString.length())));
				colorString="a";
			}
			try{
				colorString=colorString.substring(colorString.indexOf(" ")+1);
			}catch(IndexOutOfBoundsException e){
				System.out.println("stuck!");
			}
		}
		
		for(Integer val : vals){
			System.out.println("array value: " + val);
		}
		
		if(colorType.contains("RGBA")) return new ColorRGBA(ColorUtil.convertEightBitToUnit(vals.get(0)), ColorUtil.convertEightBitToUnit(vals.get(1)), ColorUtil.convertEightBitToUnit(vals.get(2)),ColorUtil.convertEightBitToUnit(vals.get(3)));
		else return new ColorRGBA(ColorUtil.convertEightBitToUnit(vals.get(0)), ColorUtil.convertEightBitToUnit(vals.get(1)), ColorUtil.convertEightBitToUnit(vals.get(2)),1);
	}

	/**
	 * @param args
	 * @throws InvalidColorException 
	 */
	public static void main(String[] args) throws InvalidColorException {
		ColorRGBA test = parseColor("RGB 2 128 2");
		System.out.println(test.toString());
	}

}
