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
import java.awt.HeadlessException;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * The 'About' window for use on OS X (Windows doesn't have this option)
 * @author Skye Book
 *
 */
public class AboutWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @throws HeadlessException
	 */
	public AboutWindow() throws HeadlessException {
		super("About");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		add(new JLabel("Betaville is open-source software"), c);
		c.gridy++;
		add(new JLabel("Copyright 2008-2011 Brooklyn eXperimental Media Center"), c);
		c.gridy++;
		add(new JLabel("http://betaville.net | http://bxmc.poly.edu | http://betaville.googlecode.com"), c);
		
		setSize(500, 275);
		setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-getWidth()/2),
				(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-getHeight()/2));
	}
	
	
	public static void main(String[] args) {
		AboutWindow aw = new AboutWindow();
		aw.setVisible(true);
	}

}
