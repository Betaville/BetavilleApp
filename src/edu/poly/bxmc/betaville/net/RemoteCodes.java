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
package edu.poly.bxmc.betaville.net;

/**
 * Command codes that remote controls can send to the client
 * @author Skye Book
 *
 */
public class RemoteCodes {
	
	public static final byte FORWARD = 0x00;
	public static final byte BACKWARD = 0x01;
	public static final byte STRAFE_LEFT = 0x02;
	public static final byte STRAFE_RIGHT = 0x03;
	public static final byte ROTATE_UP = 0x04;
	public static final byte ROTATE_DOWN = 0x05;
	public static final byte ROTATE_LEFT = 0x06;
	public static final byte ROTATE_RIGHT = 0x07;
	public static final byte ALTITUDE_UP = 0x08;
	public static final byte ALTITUDE_DOWN = 0x09;
	public static final byte GO_TO = 0x0A;
	public static final byte ROTATE_TO = 0x0B;
	
	public static final byte VOLUME_MUTE = 0x10;
	public static final byte VOLUME_UP = 0x11;
	public static final byte VOLUME_DOWN = 0x12;
	
	public static final byte REQUEST_CONTROL = 0x20;
	public static final byte ABDICATE_CONTROL = 0x21;
	
}
