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

import java.io.IOException;

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

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.util.Crypto;
import edu.poly.bxmc.betaville.xml.UpdatedPreferenceWriter;

/**
 * @author Skye Book
 *
 */
public class CreateKioskPasswordPrompt extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(CreateKioskPasswordPrompt.class);

	private int targetHeight = 200;
	private int targetWidth = 300;

	private Label description;
	private TextEditor password;
	private TextEditor confirm;
	private Button submit;
	private Button disablePassword;

	/**
	 * 
	 */
	public CreateKioskPasswordPrompt() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowLayout(false));
		getContentContainer().setSize(targetWidth, targetHeight);

		description = FengGUI.createWidget(Label.class);
		description.setText(Labels.get(this.getClass().getSimpleName()+".prompt"));
		description.setHeight(description.getHeight()*2);
		description.setMultiline(true);
		description.setWordWarping(true);

		password = FengGUI.createWidget(TextEditor.class);
		password.setEmptyText(Labels.get("Generic.password"));
		password.setWidth((int)(targetWidth*.75));
		password.setPasswordField(true);

		confirm = FengGUI.createWidget(TextEditor.class);
		confirm.setEmptyText(Labels.get("Generic.confirm"));
		confirm.setWidth((int)(targetWidth*.75));
		confirm.setPasswordField(true);
		
		Container buttonContainer = FengGUI.createWidget(Container.class);
		buttonContainer.setLayoutManager(new RowLayout(true));

		// limit the size of the button by putting it in a container

		submit = FengGUI.createWidget(Button.class);
		submit.setText(Labels.get("Generic.submit"));
		submit.addButtonPressedListener(new IButtonPressedListener() {

			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				// verify input
				if(!FengUtils.getText(password).equals(FengUtils.getText(confirm))){
					GUIGameState.getInstance().getDisp().addWidget(FengUtils.createDismissableWindow("Kiosk Mode", "Passwords do not match!", Labels.get("Generic.ok"), true));
				}
				else{
					// set the preferences and save
					System.setProperty("betaville.kiosk.password", Crypto.doSHA1(FengUtils.getText(password)));
					writePreferences();
					
					// close the window now
					close();
				}

				clearPasswordEditors();
			}
		});
		
		disablePassword = FengGUI.createWidget(Button.class);
		disablePassword.setText(Labels.get(this.getClass().getSimpleName()+".disable"));
		disablePassword.addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				GUIGameState.getInstance().getDisp().addWidget(FengUtils.createTwoOptionWindow("Kiosk Mode", "Are you sure that you would like to disable Kiosk Mode?", Labels.get("Generic.yes"), Labels.get("Generic.no"), new IButtonPressedListener() {
					
					public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
						clearPasswordEditors();
						System.setProperty("betaville.kiosk.requirepass", "false");
						writePreferences();
						
						// close the window now
						close();
					}
				}, null, true, true));
			}
		});

		buttonContainer.addWidget(submit, disablePassword);
		getContentContainer().addWidget(description, password, confirm, buttonContainer);
	}
	
	private void clearPasswordEditors(){
		password.setText("");
		confirm.setText("");
	}
	
	private void writePreferences(){
		try {
			UpdatedPreferenceWriter.writeDefaultPreferences();
		} catch (IOException e) {
			GUIGameState.getInstance().getDisp().addWidget(
					FengUtils.createDismissableWindow(
							"Kiosk Mode",
							"Default preferences could not be written, please be sure that your .betaville folder is writable",
							Labels.get("Generic.ok"), true)
			);
			logger.error("Default preferences could not be written", e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);

		// Put me at the middle of the screen!
		StaticLayout.center(this, GUIGameState.getInstance().getDisp());
	}

}
