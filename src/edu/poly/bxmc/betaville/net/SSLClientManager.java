/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.ResourceLoader;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.module.Module;

/**
 * @author Skye Book
 *
 */
public class SSLClientManager extends SecureClientManager{
	private static Logger logger = Logger.getLogger(SSLClientManager.class);

	/**
	 * Constant <PORT_SERVER> - Port of the server
	 */
	private final int PORT_SERVER = 14501;

	private char[] keyStorePass = "123456".toCharArray();
	private char[] trustStorePass = keyStorePass;

	/**
	 * Constructor - Creation of the client manager
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public SSLClientManager(List<Module> modules) throws UnknownHostException, IOException{
		super(modules, false);
		try{
			KeyStore keyStore = KeyStore.getInstance("JKS");
			KeyStore trustStore = KeyStore.getInstance("JKS");

			keyStore.load(ResourceLoader.loadResource("/data/certs/client.keystore").openStream(), keyStorePass);
			trustStore.load(ResourceLoader.loadResource("/data/certs/client.truststore").openStream(), trustStorePass);

			KeyManagerFactory keyManager = KeyManagerFactory.getInstance("SunX509");
			keyManager.init(keyStore, keyStorePass);
			TrustManagerFactory trustManager = TrustManagerFactory.getInstance("SunX509");
			trustManager.init(trustStore);
			
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);
			//context.init(keyManager.getKeyManagers(), trustAll, null);

			SSLSocketFactory sslFactory = context.getSocketFactory();
			clientSocket = (SSLSocket)sslFactory.createSocket();

			clientSocket.connect(new InetSocketAddress(SettingsPreferences.getServerIP(), PORT_SERVER));
			progressOutput = new ProgressOutputStream(clientSocket.getOutputStream());
			output = new ObjectOutputStream(progressOutput);
			
			progressInput = new ProgressInputStream(clientSocket.getInputStream());
			input = new ObjectInputStream(progressInput);
		}catch(KeyStoreException e){
			logger.fatal("Java KeyStore Issue", e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			logger.fatal("Could not connect to server at "+SettingsPreferences.getServerIP(), e);
			JOptionPane.showMessageDialog(null, "Could not connect to server at "+SettingsPreferences.getServerIP());
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	public static TrustManager[] createTrustAll() {
		// Create a trust manager that does not validate certificate chains
		return new TrustManager[] { new X509TrustManager() {

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				return;
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				return;
			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}
		}};

	}


}
