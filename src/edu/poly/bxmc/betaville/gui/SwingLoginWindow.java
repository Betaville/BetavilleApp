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
package edu.poly.bxmc.betaville.gui;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.LoginManager;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.gui.TermsWindow.TermsAcceptedListener;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.StringVerifier;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.net.IAuthenticationListener;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.net.ProtectedManager;
import edu.poly.bxmc.betaville.xml.UpdatedPreferenceWriter;

/**
 * @author Skye Book
 *
 */
public class SwingLoginWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SwingLoginWindow.class);
	private boolean loggedIn = false;
	private IAuthenticationListener authListener;
	private ProtectedManager manager;
	private JTextField userField;
	private JPasswordField passField;
	private JCheckBox savePassword;
	private JButton forgotPassword;
	private JButton registerAccount;
	private JButton login;
	private JButton changeServer;

	private JTextField emailField;
	private JTextField serverField;


	private JDialog jd;
	private JLabel dialogLabel;
	private JButton dialogDismiss;
	private ComponentListener dialogExitListener;

	private JPanel loginPanel;
	private JPanel registerPanel;
	private JPanel forgotPanel;
	private JPanel serverPanel;

	private LoginManager loginManager;
	private String[] loginData;

	private GridBagConstraints c = new GridBagConstraints();

	private KeyListener keyAuthListener;

	// registration panel items
	private JTextField registerAccountUserField;
	private long delayForCheckingAvailability=1500;
	private long lastInputInUserRegistrationWindow;
	private Timer userRegistrationInputTimer;
	private JTextField registerAccountPasswordField;
	private long lastInputInEmailRegistrationWindow;
	private JTextField registerAccountEmailField;
	private JTextField registerAccountVerifyPasswordField;
	private boolean currentUsernameRegistrationInputHasBeenChecked;

	private AtomicBoolean loginInProgress = new AtomicBoolean(false);

	/**
	 * 
	 */
	public SwingLoginWindow(IAuthenticationListener authListener){
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

		createRegisterPanel();

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

		boolean response = manager.startSession(userField.getText(), pass);
		if(response){
			saveCookie();
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

	private void saveCookie(){
		loginManager = new LoginManager();
		if(savePassword.isSelected()){
			loginData = new String[2];
			loginData[0] = userField.getText();
			char[] c1 = passField.getPassword();
			String s1 = "";
			for (int i = 0; i < c1.length; i++) {
				s1 += c1[i];
			}
			loginData[1] = s1;
			try {
				loginManager.saveCookie(loginData);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}else{
			try {
				loginManager.deleteLogin();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @return 0 if this is acceptable
	 * 1 if the client is newer than the server
	 * -1 if the client is older than the server
	 * -2 if a network connection couldn't be made
	 */
	private int checkVersion(){
		try{
		long serverVersion = NetPool.getPool().getSecureConnection().getDesignVersion();
		logger.info("Server is version " + serverVersion);
		if(Design.serialVersionUID==serverVersion) return 0;
		else if(Design.serialVersionUID>serverVersion) return 1;
		else if(Design.serialVersionUID<serverVersion) return -1;
		else if(serverVersion==-2){
			logger.error("server error");
		}
		return -1;
		}catch (NullPointerException e) {
			// can't connect? Throw a special error
			return -2;
		}
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
		loginManager = new LoginManager();
		loginData = new String[2];
		try {
			loginData = loginManager.loadCookie();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		userField = new JTextField(16);
		userField.setDocument(new JTextFieldLimit(255));
		userField.setName("Username");
		userField.setToolTipText("Enter your user name here");
		//userField.addKeyListener(keyAuthListener);

		passField = new JPasswordField(16);
		passField.setName("Password");
		passField.setToolTipText("Enter your password here");
		passField.addKeyListener(keyAuthListener);

		savePassword = new JCheckBox("Remember Login");
		savePassword.setSelected(false);
		if(loginData!=null){
			userField.setText(loginData[0]);
			passField.setText(loginData[1]);
			savePassword.setSelected(true);
		}

		forgotPassword = new JButton("Forgot Password");
		forgotPassword.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				createForgotPanel();
				setContentPane(forgotPanel);
				validate();
			}
		});

		registerAccount = new JButton("Register Account");
		registerAccount.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//BareBonesBrowserLaunch.openURL("http://betaville.net");
				setContentPane(registerPanel);
				validate();  
			}
		});

		login = new JButton("Login");
		login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//login.setEnabled(false);
				
				// check that the terms have been accepted first
				if(!Boolean.parseBoolean(System.getProperty("betaville.license.content.agree"))){
					// agree to the license
					TermsWindow contentLicenseWindow = new TermsWindow("Creative Commons Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)",
							"http://creativecommons.org/licenses/by-sa/3.0/",
							new File("data/license/by-sa.html"),
							"Betaville Content License");
					contentLicenseWindow.setVisible(true);
					contentLicenseWindow.addTermsAcceptedListener(new TermsAcceptedListener() {
						
						@Override
						public void termsAccepted() {
							doAuthAction();
						}
					});
				}
				else{
					doAuthAction();
				}
				
				//login.setEnabled(true);
			}
		});

		changeServer = new JButton("Change Server");
		changeServer.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				createServerPanel();
				setContentPane(serverPanel);
				validate();
			}});

		loginPanel = new JPanel();

		loginPanel.setLayout(new GridBagLayout());

		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridy=0;
		c.gridx=1;
		loginPanel.add(new JLabel("Username"), c);

		c.gridx=2;
		loginPanel.add(userField, c);

		c.gridy=1;
		c.gridx=1;
		loginPanel.add(new JLabel("Password"), c);

		c.gridx=2;
		loginPanel.add(passField, c);

		c.gridy=2;
		c.gridx=2;
		loginPanel.add(savePassword, c);

		c.gridy=3;
		c.gridx=1;
		loginPanel.add(forgotPassword, c);

		c.gridy=4;
		c.gridx=1;
		loginPanel.add(changeServer, c);

		c.gridy=3;
		c.gridx=2;
		loginPanel.add(registerAccount, c);

		c.gridy=4;
		c.gridx=2;
		loginPanel.	add(login, c);
	}

	private void createServerPanel(){
		serverPanel = new JPanel();
		serverPanel.setLayout(new GridBagLayout());

		c.gridy=0;
		c.gridx=0;
		serverPanel.add(new JLabel("Betaville Server"), c);

		serverField = new JTextField();
		serverField.setText(SettingsPreferences.getServerIP());
		serverField.setToolTipText("Enter the URL or IP address of a Betaville server");
		c.gridx=1;
		serverPanel.add(serverField, c);

		JButton back = new JButton("Back");
		back.setToolTipText("Return to the login screen");
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setContentPane(loginPanel);
			}
		});

		JButton submit = new JButton("Submit");
		submit.setToolTipText("Sets the new Betaville Server");
		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.setProperty("betaville.server", serverField.getText());
				logger.info("Server changed to " + SettingsPreferences.getServerIP());
				setContentPane(loginPanel);
				try {
					UpdatedPreferenceWriter.writeDefaultPreferences();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		c.gridy=1;
		c.gridx=0;
		serverPanel.add(back, c);

		c.gridy=1;
		c.gridx=1;
		serverPanel.add(submit, c);
	}

	private void createRegisterPanel(){
		registerPanel = new JPanel();
		registerPanel.setLayout(new GridBagLayout());

		c.gridy=0;
		c.gridx=0;
		registerPanel.add(new JLabel("Username"), c);

		currentUsernameRegistrationInputHasBeenChecked = false;
		lastInputInUserRegistrationWindow = -1;
		userRegistrationInputTimer = new Timer();
		userRegistrationInputTimer.schedule(new TimerTask(){
			@Override
			public void run() {
				// only run this if the field is currently selected and is not empty
				if(registerAccountUserField.isFocusOwner() && !registerAccountUserField.getText().isEmpty() && !currentUsernameRegistrationInputHasBeenChecked){
					// if we've now waited longer than the delay value, check if the username is available
					if((System.currentTimeMillis()-lastInputInUserRegistrationWindow)>delayForCheckingAvailability){
						if(!NetPool.getPool().getSecureConnection().checkNameAvailability(registerAccountUserField.getText())){
							logger.info("The username: "+registerAccountUserField.getText() +" is not available");
							flashDialog("The username "+registerAccountUserField.getText()+" is not available", false);
						}
						currentUsernameRegistrationInputHasBeenChecked=true;
					}
				}
			}
		}, 50, 50);


		registerAccountUserField = new JTextField();
		registerAccountUserField.setToolTipText("Enter your desired username here");
		registerAccountUserField.addKeyListener(new KeyListener() {

			// this will be used to detect how long it has been since text input has happened
			public void keyTyped(KeyEvent arg0) {
				lastInputInUserRegistrationWindow = System.currentTimeMillis();
				currentUsernameRegistrationInputHasBeenChecked=false;
			}

			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent arg0) {}
		});

		c.gridx=1;
		registerPanel.add(registerAccountUserField, c);

		c.gridy++;
		c.gridx=0;
		registerPanel.add(new JLabel("Password"), c);

		c.gridx=1;
		registerAccountPasswordField = new JPasswordField();
		registerAccountPasswordField.setToolTipText("Choose a password and make it a good one!");
		registerPanel.add(registerAccountPasswordField, c);

		c.gridy++;
		c.gridx=0;
		registerPanel.add(new JLabel("Verify Password"), c);

		c.gridx=1;
		registerAccountVerifyPasswordField = new JPasswordField();
		registerAccountVerifyPasswordField.setToolTipText("Verify your password");
		registerPanel.add(registerAccountVerifyPasswordField, c);

		c.gridy++;
		c.gridx=0;
		registerPanel.add(new JLabel("Email Address"), c);

		c.gridx=1;
		registerAccountEmailField = new JTextField();
		registerAccountEmailField.setToolTipText("Pleas supply your email address");
		registerPanel.add(registerAccountEmailField, c);
		lastInputInEmailRegistrationWindow = -1;
		registerAccountEmailField.addKeyListener(new KeyListener() {

			// this will be used to detect how long it has been since text input has happened
			public void keyTyped(KeyEvent arg0) {
				lastInputInEmailRegistrationWindow = System.currentTimeMillis();
			}

			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent arg0) {}
		});

		userRegistrationInputTimer.schedule(new TimerTask(){
			@Override
			public void run() {
				// only run this if the field is currently selected and is not empty
				if(registerAccountEmailField.isFocusOwner() && !registerAccountEmailField.getText().isEmpty()){
					// if we've now waited longer than the delay value, check if the username is available
					if((System.currentTimeMillis()-lastInputInEmailRegistrationWindow)>delayForCheckingAvailability){
						/* (There is no method on the server yet that supports checking if a username is in use
						if(!NetPool.getPool().getSecureConnection().checkNameAvailability(registerAccountUserField.getText())){
							logger.info("The email address: "+registerAccountEmailField.getText() +" is already in use");
						}
						 */
					}
				}
			}
		}, 50);



		JButton back = new JButton("Back");
		back.setToolTipText("Return to the login screen");
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setContentPane(loginPanel);
			}
		});

		JButton submitRegistration = new JButton("Register");
		submitRegistration.setToolTipText("Register your account!");
		submitRegistration.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!registerAccountVerifyPasswordField.getText().equals(registerAccountPasswordField.getText())){
					flashDialog("Please ensure that your passwords match!", false);
					return;
				}
				if(NetPool.getPool().getSecureConnection().addUser(registerAccountUserField.getText(), registerAccountPasswordField.getText(), registerAccountEmailField.getText(), "", "")){
					flashDialog("Cool!  You're good to go!", false);
					setContentPane(loginPanel);
					validate();
				}
				else{
					flashDialog("You couldn't be registered!", false);
				}
			}
		});

		c.gridy++;
		c.gridx=0;
		registerPanel.add(back, c);

		c.gridx=1;
		registerPanel.add(submitRegistration, c);
	}

	private void createForgotPanel(){
		forgotPanel = new JPanel();
		forgotPanel.setLayout(new GridBagLayout());

		c.gridy=0;
		c.gridx=1;
		forgotPanel.add(new JLabel("E-Mail Address"), c);

		emailField = new JTextField(16);
		emailField.setToolTipText("Enter your email address here");
		c.gridx=2;
		forgotPanel.add(emailField, c);

		JButton back = new JButton("Back");
		back.setToolTipText("Return to the login screen");
		back.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setContentPane(loginPanel);
			}
		});

		JButton submit = new JButton("Submit");
		submit.setToolTipText("Send your request for a password change");
		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// Send request
			}
		});

		c.gridy=1;
		c.gridx=0;
		forgotPanel.add(back, c);

		c.gridy=1;
		c.gridx=2;
		forgotPanel.add(submit, c);
	}

	private void doAuthAction() {
		if(loginInProgress.get()){
			logger.info("Login already in progress, please be patient!");
			return;
		}
		else{
			loginInProgress.set(true);
		}
		int versionCheck = checkVersion();
		if(versionCheck==-2){
			// can't connect
			loginInProgress.set(false);
			return;
		}
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
				loginInProgress.set(false);
			}
			else{
				flashDialog("Please enter a username", false);
			}
		}
		else{
			flashDialog("Please enter a password", false);
		}
	}

	public static class JTextFieldLimit extends PlainDocument{
		private static final long serialVersionUID = 1L;
		private int limit;

		public JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}

		public void insertString
		(int offset, String  str, AttributeSet attr)
		throws BadLocationException {
			if (str == null) return;

			if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
		}
	}

	public static boolean prompt() throws InterruptedException{
		SwingLoginWindow loginWindow = new SwingLoginWindow(new IAuthenticationListener(){
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

		return true;
	}
}
