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
import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;

import edu.poly.bxmc.betaville.KioskMode;
import edu.poly.bxmc.betaville.ShutdownManager;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.util.Crypto;

/**
 * @author Skye Book
 *
 */
public class KioskQuitPrompt extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(KioskQuitPrompt.class);

	private int targetHeight = 125;
	private int targetWidth = 275;

	private Label description;
	private TextEditor password;
	private Button submit;

	/**
	 * 
	 */
	public KioskQuitPrompt() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowLayout(false));
		getContentContainer().setSize(targetWidth, targetHeight);

		description = FengGUI.createWidget(Label.class);
		description.setText("Kiosk Mode is enabled and a password is required to quit");
		description.setHeight(description.getHeight()*2);
		description.setMultiline(true);
		description.setWordWarping(true);
		
		password = FengGUI.createWidget(TextEditor.class);
		password.setEmptyText("password");
		password.setWidth((int)(targetWidth*.75));
		password.setPasswordField(true);
		
		// limit the size of the button by putting it in a container
		Container buttonContainer = FengGUI.createWidget(Container.class);
		buttonContainer.setLayoutManager(new RowLayout(true));
		
		submit = FengGUI.createWidget(Button.class);
		submit.setText("OK");
		submit.addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				// check password
				if(Crypto.doSHA1(FengUtils.getText(password)).equals(KioskMode.getKioskPasswordHash())){
					clearPassword();
					logger.info("Exit password confirmed");
					ShutdownManager.doSafeShutdown();
					return;
				}
				else{
					clearPassword();
					logger.info("password wrong");
					GUIGameState.getInstance().getDisp().addWidget(FengUtils.createDismissableWindow("Kiosk Mode", "Password Incorrect", "OK", true));
				}
				
				
			}
		});
		
		buttonContainer.addWidget(submit);
		
		getContentContainer().addWidget(description, password, buttonContainer);
	}
	
	private void clearPassword(){
		password.setText("");
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("Betaville Kiosk");
		setSize(targetWidth, targetHeight);
		
		// Put me at the middle of the screen!
		StaticLayout.center(this, GUIGameState.getInstance().getDisp());
	}

}
