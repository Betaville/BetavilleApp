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
package edu.poly.bxmc.betaville.jme.fenggui;

import org.apache.log4j.Logger;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.key.IKeyListener;
import org.fenggui.event.key.KeyPressedEvent;
import org.fenggui.event.key.KeyReleasedEvent;
import org.fenggui.event.key.KeyTypedEvent;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;

/**
 * A prompt for a user to input a password before allowing a shutdown to proceed.  Useful
 * for when the application is being on kiosks or somewhere of a public nature.
 * @author Skye Book
 *
 */
public class ShutdownPasswordPrompt extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(ShutdownPasswordPrompt.class);

	private int targetWidth = 200;
	private int targetHeight = 125;

	private Label passwordLabel;
	private TextEditor passwordField;

	/**
	 * 
	 */
	public ShutdownPasswordPrompt() {
		super(true, true);

		passwordLabel = FengGUI.createWidget(Label.class);
		passwordLabel.setText("Password:");

		passwordField = FengGUI.createWidget(TextEditor.class);
		passwordField.setPasswordField(true);
		
		passwordField.addKeyListener(new IKeyListener() {

			public void keyTyped(Object sender, KeyTypedEvent keyTypedEvent) {
			}

			public void keyReleased(Object sender, KeyReleasedEvent keyReleasedEvent) {
				// TODO Auto-generated method stub

			}

			public void keyPressed(Object sender, KeyPressedEvent keyPressedEvent) {
				// TODO Auto-generated method stub

			}
		});
	}
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);
	}

}
