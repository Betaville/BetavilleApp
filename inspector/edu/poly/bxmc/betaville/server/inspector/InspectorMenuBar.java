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
package edu.poly.bxmc.betaville.server.inspector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.net.IAuthenticationListener;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.server.inspector.windows.LoginWindow;

/**
 * @author Skye Book
 *
 */
public class InspectorMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	private JMenu file;
	private JMenu help;

	/**
	 * 
	 */
	public InspectorMenuBar() {
		setupFile();
		setupHelp();
	}
	
	private void setupFile(){
		file = new JMenu("File");
		
		JMenuItem newConnection = new JMenuItem("New Connection");
		newConnection.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				LoginWindow loginWindow = new LoginWindow(new IAuthenticationListener(){
					private Logger logger = Logger.getLogger(IAuthenticationListener.class);
					public void onAuthentication(String user, String pass) {
						SettingsPreferences.setUserPass(user, pass);
						SettingsPreferences.setAuthenticated(true);
						UserType ut = NetPool.getPool().getConnection().getUserLevel(user);
						if(ut==null){
							logger.error(UserType.class.getName()+" is null");
						}
						else{
							SettingsPreferences.setUserType(ut);
							logger.info(user + " is " + ut.name());
						}
					}});
				loginWindow.setVisible(true);
			}
		});
		file.add(newConnection);
		
		file.add(new JSeparator());
		
		JMenuItem launchBV = new JMenuItem("Launch Betaville");
		launchBV.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				launchBetaville();
			}
		});
		file.add(launchBV);
		
		file.add(new JSeparator());
		
		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		
		add(file);
	}
	
	private void launchBetaville(){
		SettingsPreferences.getThreadPool().submit(new Runnable() {
			
			public void run() {
				try {
					BetavilleNoCanvas.main(null);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	private void setupHelp(){
		help = new JMenu("Help");
		
		JMenuItem contents = new JMenuItem("Help Contents");
		help.add(contents);
		
		help.add(new JSeparator());
		
		JMenuItem about = new JMenuItem("About");
		help.add(about);
		
		add(help);
	}

}
