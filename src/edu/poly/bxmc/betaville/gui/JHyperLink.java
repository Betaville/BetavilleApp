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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.centerkey.utils.BareBonesBrowserLaunch;

/**
 * @author Skye Book
 *
 */
public class JHyperLink extends JButton{
	private static final long serialVersionUID = 1L;
	
	private String url;
	
	public JHyperLink(String link){
		this(link, link, link);
	}

	public JHyperLink(String link, String text, String tooltip){
		
		/**
		 * Original button-hyperlink code appeared on Stack Overflow, contributed by user, McDowell
		 * http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel
		 * Contributions to Stack Overflow are licensed under Creative Commons Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)
		 * http://creativecommons.org/licenses/by-sa/3.0/
		 */
		super();
		url=link;
		setText("<HTML><FONT color=\"#000099\"><U>"+text+"</U></FONT></HTML>");
		//setHorizontalAlignment(SwingConstants.LEFT);
		setBorderPainted(false);
		setOpaque(false);
		//setBackground(Color.WHITE);
		setToolTipText(tooltip);
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BareBonesBrowserLaunch.openURL(url);
			}
		});
	}
}
