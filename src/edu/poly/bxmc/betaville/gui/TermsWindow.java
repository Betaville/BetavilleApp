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

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.ResourceLoader;
import edu.poly.bxmc.betaville.xml.UpdatedPreferenceWriter;


/**
 * The window that prompts the user to agree to the
 * application's terms of use
 * @author Skye Book
 *
 */
public class TermsWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TermsWindow.class);
	
	private int declineCounter=0;
	private int declineLimit=3;
	
	private ArrayList<TermsAcceptedListener> termsAcceptedListeners = new ArrayList<TermsAcceptedListener>();
	
	/**
	 * @throws HeadlessException
	 */
	public TermsWindow(String licenseName, String licenseLink, URL licenseFile, String licenseUse) throws HeadlessException {
		super("Terms of Use");
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(500, 275);
		
		try {
			
			/**
			 * The code for
			 */
			JHyperLink linkToLicense = new JHyperLink(licenseLink, licenseName, licenseUse);
			add(linkToLicense, BorderLayout.NORTH);
			
			JEditorPane contentLicense = new JEditorPane(licenseFile);
			JScrollPane jsp = new JScrollPane(contentLicense);
			contentLicense.setSize(getSize());
			jsp.setSize(getSize());
			add(jsp, BorderLayout.CENTER);
			
			JButton accept = new JButton("Accept");
			accept.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					logger.info("The license has been accepted");
					
					// Set the system preference
					System.setProperty("betaville.license.content.agree", "true");
					
					// Write the change to the XML preferences file
					try {
						UpdatedPreferenceWriter.writeDefaultPreferences();
					} catch (IOException e1) {
						String writeFailed = "The preference writer could not write the updated preferences file." +
						"  You may need to accept the license again when you next run the application";
						logger.warn(writeFailed);
						JOptionPane.showMessageDialog(getContentPane(), writeFailed);
					}
					
					// Fire the listeners
					for(TermsAcceptedListener listener : termsAcceptedListeners){
						listener.termsAccepted();
					}
					
					// Close the window
					setVisible(false);
				}
			});
			
			
			JButton decline = new JButton("Decline");
			decline.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(declineCounter==declineLimit){
						logger.warn("The user has declined the license more than three times, quitting");
						System.exit(0);
					}
					
					switch (declineCounter) {
					case 0:
						JOptionPane.showMessageDialog(getContentPane(), "You can't run the applicaion without accepting the license. (Warning 1 of 3)");
						break;
					case 1:
						JOptionPane.showMessageDialog(getContentPane(), "You can't run the applicaion without accepting the license.  Why not accept it? (Warning 2 of 3)");
						break;
					case 2:
						JOptionPane.showMessageDialog(getContentPane(), "This is your last chance!  Please reconsider accepting the license :) (Warning 3 of 3)");
						break;
					}
					
					declineCounter++;
				}
			});
			
			JPanel buttons = new JPanel();
			buttons.add(accept);
			buttons.add(decline);
			add(buttons, BorderLayout.SOUTH);
		} catch (IOException e) {
			logger.error("License file could not be found" , e);
			String licenseNotFound = "The license file could not be found." +
					"  You, unfortunately, cannot use the application without selecting the license, so we will be forced to quit";
			JOptionPane.showMessageDialog(getContentPane(), licenseNotFound);
			logger.error(licenseNotFound);
			System.exit(1);
		}
		
		
		setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-getWidth()/2),
				(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-getHeight()/2));
	}
	
	/**
	 * Adds a {@link TermsAcceptedListener} to this window.
	 * @param listener The listener to add
	 */
	public void addTermsAcceptedListener(TermsAcceptedListener listener){
		termsAcceptedListeners.add(listener);
	}
	
	/**
	 * Removes a {@link TermsAcceptedListener} from this window.
	 * @param listener The listener to remove
	 */
	public void removeTermsAcceptedListener(TermsAcceptedListener listener){
		termsAcceptedListeners.remove(listener);
	}
	
	/**
	 * Removes each {@link TermsAcceptedListener} from this window.
	 */
	public void removeAllTermsAcceptedListeners(){
		termsAcceptedListeners.clear();
	}
	
	/**
	 * A listener that notifies when the terms of use have been accepted
	 * @author Skye Book
	 *
	 */
	public interface TermsAcceptedListener{
		
		/**
		 * Called when the terms of use have been accepted.
		 */
		public void termsAccepted();
	}
	
	
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				TermsWindow tw = new TermsWindow("Creative Commons Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)", "http://creativecommons.org/licenses/by-sa/3.0/", ResourceLoader.loadResource("/data/license/by-sa.html"), "Betaville Content License");
				tw.setVisible(true);
			}
		});
		
	}

}
