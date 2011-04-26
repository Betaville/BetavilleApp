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
package edu.poly.bxmc.betaville.server.inspector.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.gui.SwingLoginWindow;
import edu.poly.bxmc.betaville.gui.SwingLoginWindow.JTextFieldLimit;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.StringVerifier;
import edu.poly.bxmc.betaville.net.IAuthenticationListener;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.net.ProtectedManager;

/**
 * A slim version of {@link SwingLoginWindow} that doesn't start a session
 * but instead authenticates a user (does not provide the same options to
 * remember passwords, create accounts, or recover accounts as its big brother)
 * @author Skye Book
 *
 */
public class LoginWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(LoginWindow.class);
	private boolean loggedIn = false;
	private IAuthenticationListener authListener;
	private ProtectedManager manager;
	private JTextField userField;
	private JPasswordField passField;
	private JTextField serverField;
	private JButton login;

	private JDialog jd;
	private JLabel dialogLabel;
	private JButton dialogDismiss;
	private ComponentListener dialogExitListener;

	private JPanel loginPanel;

	private GridBagConstraints c = new GridBagConstraints();

	private KeyListener keyAuthListener;

	/**
	 * 
	 */
	public LoginWindow(IAuthenticationListener authListener){
		super("Login To Betaville");

		setFocusable(true);
		addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) { }

			public void focusGained(FocusEvent e) {
				logger.info(e);
				if (!userField.getText().isEmpty() && passField.getPassword().length > 0) {
					login.requestFocusInWindow();
				}
			}
		});

		this.authListener=authListener;
		setSize(375, 210);
		setResizable(false);
		setLocation(((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2)-getWidth()/2,
				((int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2)-getHeight()/2);

		c.insets = new Insets(5,5,5,5);

		createLoginPanel();

		jd = new JDialog();
		jd.setSize(200, 100);
		jd.setTitle("OOPS!");
		jd.setLayout(new GridBagLayout());
		dialogLabel = new JLabel();
		c.gridx=1;
		c.gridy=1;
		jd.add(dialogLabel, c);
		dialogDismiss = new JButton("OK");
		dialogDismiss.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				jd.setVisible(false);
			}
		});
		dialogDismiss.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					jd.setVisible(false);
				}
			}

			public void keyPressed(KeyEvent e) {}
		});
		dialogExitListener = new ComponentListener() {
			public void componentShown(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {
				NetPool.getPool().cleanAll();
				System.exit(0);
			}
		};
		c.gridx=1;
		c.gridy=2;
		jd.add(dialogDismiss, c);

		setContentPane(loginPanel);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		logger.info("setup complete");
	}

	public boolean isLoggedIn(){
		return loggedIn;
	}

	private boolean authenticate(){
		String pass="";
		for(int i=0; i<passField.getPassword().length; i++){
			pass+=passField.getPassword()[i];
			passField.getPassword()[i]=0;
		}

		if(manager==null){
			manager = NetPool.getPool().getSecureConnection();
		}

		// We do not want to start a session since we are not actually entering the world.
		boolean response = manager.authenticateUser(userField.getText(), pass);
		if(response){
			logger.info(userField.getText() + " logged in");
			loggedIn=true;
			authListener.onAuthentication(userField.getText(), pass);
			setVisible(false);
		}
		else{
			flashDialog("Please try again!", false);
			passField.setText("");
		}
		pass = null;
		return response;
	}

	private int checkVersion(){
		long serverVersion = NetPool.getPool().getSecureConnection().getDesignVersion();
		logger.info("Server is version " + serverVersion);
		if(Design.serialVersionUID==serverVersion) return 0;
		else if(Design.serialVersionUID>serverVersion) return 1;
		else if(Design.serialVersionUID<serverVersion) return -1;
		else if(serverVersion==-2){
			logger.error("server error");
		}
		return -1;
	}

	private void flashDialog(String message, boolean exitOnClose){
		dialogLabel.setText(message);
		jd.setLocation(((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2)-jd.getWidth()/2,
				((int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2)-jd.getHeight()/2);
		jd.setVisible(true);

		boolean exitExists=false;
		for(ComponentListener l : jd.getComponentListeners()){
			if(l.equals(dialogExitListener)){
				if(!exitOnClose){
					jd.removeComponentListener(dialogExitListener);
				}
				else exitExists=true;
			}
		}

		if(exitOnClose&&!exitExists) jd.addComponentListener(dialogExitListener);
	}

	private void createLoginPanel(){
		keyAuthListener = new KeyListener() {

			public void keyTyped(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					doAuthAction();
				}
			}

			public void keyPressed(KeyEvent e) {}
		};
		
		userField = new JTextField(16);
		userField.setDocument(new JTextFieldLimit(255));
		userField.setName("Username");
		userField.setToolTipText("Enter your user name here");
		//userField.addKeyListener(keyAuthListener);

		passField = new JPasswordField(16);
		passField.setName("Password");
		passField.setToolTipText("Enter your password here");
		passField.addKeyListener(keyAuthListener);

		serverField = new JTextField();
		serverField.setName("Server");
		serverField.setToolTipText("Enter the server's address here");
		
		login = new JButton("Login");
		login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.setProperty("betaville.server", serverField.getText());
				doAuthAction();
			}
		});

		loginPanel = new JPanel();

		loginPanel.setLayout(new GridBagLayout());

		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridy++;
		c.gridx=1;
		loginPanel.add(new JLabel("Username"), c);

		c.gridx=2;
		loginPanel.add(userField, c);

		c.gridy++;
		c.gridx=1;
		loginPanel.add(new JLabel("Password"), c);

		c.gridx=2;
		loginPanel.add(passField, c);
		
		c.gridy++;
		c.gridx=1;
		loginPanel.add(new JLabel("Server"), c);
		
		c.gridx=2;
		loginPanel.add(serverField, c);

		c.gridy++;
		c.gridx=2;
		// where checkbox used to be
		c.gridy++;
		c.gridx=1;
		// another button can go here

		c.gridy++;
		c.gridx=1;
		// another button can go here

		c.gridy++;
		c.gridx=2;
		// another button can go here

		c.gridy++;
		c.gridx=2;
		loginPanel.	add(login, c);
	}

	private void doAuthAction() {
		int versionCheck = checkVersion();
		if(versionCheck<0){
			flashDialog("Update your Betaville Client!", true);
			return;
		}
		else if(versionCheck>0){
			flashDialog("Your Client is to new for your server!", true);
			return;
		}


		if(passField.getPassword()!=null){
			if(userField.getText()!=null && !userField.getText().isEmpty()){
				if(!StringVerifier.isValidUsername(userField.getText())&&
						!StringVerifier.isValidEmail(userField.getText())){
					flashDialog("This is not a valid username!", false);
					return;
				}
				authenticate();
			}
			else{
				flashDialog("Please enter a username", false);
			}
		}
		else{
			flashDialog("Please enter a password", false);
		}
	}

	public static boolean prompt() throws InterruptedException{
		
		LoginWindow lw = new LoginWindow(null);
		lw.setVisible(true);
		/*
		LoginWindow loginWindow = new LoginWindow(new IAuthenticationListener(){
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

		
		while(!loginWindow.isLoggedIn()){
			Thread.sleep(50);
		}
		*/
		return true;
	}
}
