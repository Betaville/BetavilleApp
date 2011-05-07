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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.module.Module;

/**
 * Class <ClientManager> - Manage the connection and requests of the client to
 * the server
 * 
 * @author Caroline Bouchat
 * @author Skye Book
 */
public class InsecureClientManager extends ClientManager{
	private static Logger logger = Logger.getLogger(InsecureClientManager.class);

	/**
	 * Constant <PORT_SERVER> - Port of the server
	 */
	private final int PORT_SERVER = 14500;
	
	/**
	 * Constructor - Creation of the client manager
	 */
	public InsecureClientManager(List<Module> modules){
		this(modules, SettingsPreferences.getServerIP());
	}
	
	public InsecureClientManager(List<Module> modules, String serverIP){
		try {
			clientSocket = new Socket(serverIP, PORT_SERVER);
			logger.info("Client application : "+ clientSocket.toString());
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			input = new ObjectInputStream(clientSocket.getInputStream());
		} catch (UnknownHostException e) {
			logger.fatal("Could not connect to server at "+SettingsPreferences.getServerIP(), e);
			JOptionPane.showMessageDialog(null, "Could not connect to server at "+SettingsPreferences.getServerIP());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}