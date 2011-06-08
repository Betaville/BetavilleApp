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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Contains hashing and cryptographic functions for
 * use in Betaville
 * @author Skye Book
 *
 */
public class Crypto {
	
	private static MessageDigest sha1;
	private static MessageDigest md5;
	
	/**
	 * Performs MD5 encryption on a given string
	 * @param input The String to equate the hash from
	 * @return The equated hash
	 */
	public static String doMD5(String input){
		try {
			if(md5==null) md5 = MessageDigest.getInstance("MD5");
			byte[] bytes = md5.digest(input.getBytes());
			Formatter formatter = new Formatter();
			for(byte b : bytes){
				formatter.format("%02x"	, b);
			}
			return formatter.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Performs SHA1 encryption on a given string
	 * @param input The String to equate the hash from
	 * @return The equated hash
	 */
	public static String doSHA1(String input){
		try {
			if(sha1==null) sha1 = MessageDigest.getInstance("SHA1");
			byte[] bytes = sha1.digest(input.getBytes());
			Formatter formatter = new Formatter();
			for(byte b : bytes){
				formatter.format("%02x"	, b);
			}
			return formatter.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Performs the flavor of encryption that BetavilleServer uses by default.
	 * @param pass The password
	 * @param salt The salt value to use when equating the hash
	 * @return The equated hash.
	 */
	public static String doSaltedEncryption(String pass, String salt){
		String hash = salt+pass;
		for(int i=0; i<1000; i++){
			hash = doSHA1(hash);
		}
		return hash;
	}
	
	/**
	 * Creates a random (to some-degree) value of a certain length
	 * @param size The length, in characters, of the created value
	 * @return A random String
	 */
	public static String createSalt(int size){
		String salt = "";
		for(int i=0; i<size; i++){
			double random = java.lang.Math.random();
			int asInt = (int)(random*10);
			salt = salt+asInt;
		}
		return salt;
	}
	
	/**
	 * Creates a Betaville hash value as well as the salt for it.
	 * @param password The password to be encrypted
	 * @return a String array with two elements {hash,salt}
	 */
	public static String[] createBetavilleHash(String password){
		String salt = createSalt(12);
		String hash = salt+password;
		for(int i=0; i<1000; i++){
			hash = doSHA1(hash);
		}
		return new String[]{hash, salt};
	}
	
	/**
	 * Not so much a security feature as a way to ensure unique ID's for items such as bookmarks
	 * @param time The time (or really any number to provide)
	 * @return A (hopefully) unique value
	 */
	public static String createUniqueIDFromTime(long time){
		return createBetavilleHash(Long.toString(time))[0];
	}
	
	/** 
	 * Creates a session token
	 * 	How does this work?<br>
	 * 	- The salt, as always, is used to sprinkle randomness over our value<br>
	 *  - The system's nano time is used as it is de-coupled from the system's wall clock<br>
	 *  - The salt is placed before the nano time as the numbers of greatest magnitude are the least kinetic<br>
	 */
	public static String createSessionToken(){
		String hash = Crypto.createSalt(8)+Long.toString(System.nanoTime());
		return Crypto.doSHA1(hash);
	}
}
