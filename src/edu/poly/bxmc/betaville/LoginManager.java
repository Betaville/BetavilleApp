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

package edu.poly.bxmc.betaville;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author Joschka Zimdars
 *
 */

public class LoginManager {
	private static Logger logger = Logger.getLogger(LoginManager.class);
	
	private BufferedWriter out;
	private BufferedReader in;
	private String dir;
	
	public void saveCookie(String[] login) throws IOException{
		dir = System.getProperty("user.home");
		dir += "/.betaville/properties.pc";
		
		for(int i = 0; i < login.length; i++) {
			login[i] = encrypt(login[i], 11029, i);
		}
		try {
			out = new BufferedWriter(new FileWriter(dir));
		} catch (FileNotFoundException e) {
			dir = System.getProperty("user.home");
			dir += "\\.betaville\\properties.pc";
			out = new BufferedWriter(new FileWriter(dir));
		}
		for(int i = 0; i < login.length; i++) {
			out.write(login[i] + "\r\n");
		}
		out.close();
	}
	
	public String[] loadCookie() throws IOException{
		dir = System.getProperty("user.home");
		dir += "/.betaville/properties.pc";
		boolean fileFound = true;
		String[] login = new String[2];
		try {
			in = new BufferedReader(new FileReader(dir));
		} catch (FileNotFoundException e) {
			fileFound = false;
		}
		if(!fileFound){
			try {
				dir = System.getProperty("user.home");
				dir += "\\.betaville\\properties.pc";
				in = new BufferedReader(new FileReader(dir));
			} catch (FileNotFoundException e) {
				System.err.println("No cookie available");
				fileFound = false;
			}
		}
		if(fileFound){
			String s;
			int i = 0;
			while((s = in.readLine()) != null) {	
				login[i] = s;
				if(s.endsWith("/n")) {
					break;
				}
				i++;
			}
			in.close();
			for (int i2 = 0; i2 < login.length; i2++) {
				if(login[i2].length()%6==0	&& login[i2].length()!=0){
					login[i2] = decrypt(login[i2], 11029, i2);
				}else{
					login[0] = "";
					login[1] = "";
				}
			}
			return login;
		}
		return null;
	}
	
	public void deleteLogin() throws IOException{
		dir = System.getProperty("user.home");
		dir += "/.betaville/properties.pc";
		
		try {
			out = new BufferedWriter(new FileWriter(dir));
		} catch (FileNotFoundException e) {
			dir = System.getProperty("user.home");
			dir += "\\.betaville\\properties.pc";
			out = new BufferedWriter(new FileWriter(dir));
		}
		for(int i = 0; i < 2; i++) {
			out.write("" + "\r\n");
		}
		out.close();
	}
	
	private String encrypt(String s, int key1, int key2){
		char[] c = s.toCharArray();
		int num;
		String[] integerString = new String[c.length];
		int limitI = 0;
		for (int i = 0; i < c.length; i++) {
			limitI = i%6+1;
			num = (int)c[i];
			num += (96973+limitI*85999) + (Math.pow(-1, limitI+key2)*key1*limitI);
			integerString[i] = Integer.toString(num);
		}
		s="";
		for (int i = 0; i < c.length; i++) {
			s = s+(integerString[i]);
		}
		return s;
	}
	
	private String decrypt(String s, int key1, int key2){
		char[] strToChar = s.toCharArray();
		char[][] marks = new char[strToChar.length/6][6];
		int split = 0;
		for (int i = 0; i < strToChar.length; i++) {
			marks[split][i%6] = strToChar[i];
			if(i%6==5){
				split++;
			}
		}
		String[] integerString = new String[marks.length];
		for (int i = 0; i < integerString.length; i++) {
			integerString[i] = "";
		}
		for (int i = 0; i < marks.length; i++) {
			for (int j = 0; j < marks[i].length; j++) {
				integerString[i] += marks[i][j];
			}
		}
		int[] charInteger = new int[marks.length];
		char[] c = new char[marks.length];
		s = "";
		int limitI = 0;
		for (int i = 0; i < integerString.length; i++) {
			limitI = i%6+1;
			charInteger[i] = Integer.valueOf(integerString[i]).intValue();
			charInteger[i] -= (96973+limitI*85999) + (Math.pow(-1, limitI+key2)*key1*limitI);
			c[i] = (char)charInteger[i];
			s += c[i];
		}
		return s;
		
	}
}
