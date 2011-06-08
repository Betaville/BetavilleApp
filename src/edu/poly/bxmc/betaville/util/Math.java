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

/**
 * Math functions to make life [a bit] easier.
 * @author Skye Book
 *
 */
public class Math {

	/**
	 * Splits a floating point number into two integers, its whole
	 * and its fraction
	 * @param number
	 * @return An array with a size of two.  The whole value is located
	 * at position 0 and the fractional value is located at position 1
	 */
	public static int[] splitFraction(float number){
		int whole = (int)number;
		float part = (number-whole);
		if(part>.5f){
			part = (int)(part*100)+1;
		}
		else{
			part = (int)(part*100);
		}

		return new int[]{whole, (int)part};
	}
	
	/**
	 * Splits a double-precision point number into two integers, its whole
	 * and its fraction
	 * @param number
	 * @return An array with a size of two.  The whole value is located
	 * at position 0 and the fractional value is located at position 1
	 */
	public static int[] splitFraction(double number){
		int whole = (int)number;
		double part = (number-whole);
		if(part>.5d){
			part = (int)(part*100)+1;
		}
		else{
			part = (int)(part*100);
		}
		
		return new int[]{whole, (int)part};
	}
}
