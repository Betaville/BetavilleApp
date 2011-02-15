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
package edu.poly.bxmc.betaville.util;

import java.util.regex.Pattern;

/**
 * A collection of static methods to verify input
 * @author Skye Book
 *
 */
public class InputVerifier {
	public static boolean checkForValidEmail(String email){
		if(email.contains("..")){
			return false;
		}
		return Pattern.matches("^[-0-9a-zA-Z\\._]+@[-0-9a-zA-Z\\._]+", email);
	}
	
	public static void main(String[] args){
		System.out.println(checkForValidEmail("skye.book@gmail.com"));
	}
	
	/**
	 * Checks for valid usernames..  Perhaps this should be serveriside so it can
	 * be configured by individual system administrators... thoughts?
	 * -Skye -- 1/18/2011
	 * @param name
	 * @return
	 */
	public static boolean checkForValidUsername(String name){
		if(name.length()>16) return false;
		if(name.contains(" ")) return false;
		if(Pattern.matches("[Aa]dm[1Ii]n", name)) return false;
		return false;
	}
}
