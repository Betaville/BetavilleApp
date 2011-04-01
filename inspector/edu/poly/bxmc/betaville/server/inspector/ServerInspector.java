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

import java.awt.HeadlessException;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import edu.poly.bxmc.betaville.logging.LogManager;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.server.inspector.tabs.UserTab;

/**
 * @author Skye Book
 *
 */
public class ServerInspector extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane tabs;

	/**
	 * @throws HeadlessException
	 */
	public ServerInspector() throws HeadlessException {
		super("Betaville Server Inspector");
		setJMenuBar(new InspectorMenuBar());
		setSize(840, 600);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e) {
			}
			
			public void windowIconified(WindowEvent e) {
			}
			
			public void windowDeiconified(WindowEvent e) {
			}
			
			public void windowDeactivated(WindowEvent e) {
			}
			
			public void windowClosing(WindowEvent e) {
				System.out.println("Closing Network Connections");
				NetPool.getPool().cleanAll();
				System.exit(0);
			}
			
			public void windowClosed(WindowEvent e) {
			}
			
			public void windowActivated(WindowEvent e) {
			}
		});
		
		getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {
			
			public void ancestorResized(HierarchyEvent e) {
				//System.out.println(getSize());
			}
			
			public void ancestorMoved(HierarchyEvent e) {}
		});
		
		tabs = new JTabbedPane();
		getContentPane().add(tabs);
		tabs.add(new UserTab(), "Users");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (System.getProperty("os.name").startsWith("Mac")) {
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name",
			"Betaville Server Inspector");
			System.setProperty("apple.laf.useScreenMenuBar", "true");

		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		// All we really need here to start with are the console loggers
		LogManager.setupConsoleLogger();
		
		ServerInspector si = new ServerInspector();
		si.setVisible(true);
	}

}
