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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowLayout;

import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.bookmarks.Bookmark;
import edu.poly.bxmc.betaville.bookmarks.BookmarkManager;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.MapManager;

/**
 * 
 * @author Skye Book
 *
 */
public class BookmarkPopup extends Window implements IBetavilleWindow{
	
	private int targetWidth = 200;
	private int targetHeight = 125;
	private Label nameLabel;
	private TextEditor name;
	private Label descriptionLabel;
	private TextEditor description;
	private FixedButton ok;
	
	public BookmarkPopup(){
		super(true, true);
		getContentContainer().setSize(targetWidth, targetHeight);
		getContentContainer().setLayoutManager(new RowLayout(false));
		nameLabel = FengGUI.createWidget(Label.class);
		nameLabel.setText("name");
		
		name = FengGUI.createWidget(TextEditor.class);
		name.setSize(targetWidth-10, nameLabel.getHeight());
		
		descriptionLabel = FengGUI.createWidget(Label.class);
		descriptionLabel.setText("description");
		
		description = FengGUI.createWidget(TextEditor.class);
		description.setSize(targetWidth-10, descriptionLabel.getHeight());
		
		ok = FengGUI.createWidget(FixedButton.class);
		ok.setText("ok");
		ok.setWidth(ok.getWidth()+10);
		ok.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				// create the bookmark and add it to the bookmark manager
				Vector3f direction = SceneGameState.getInstance().getCamera().getDirection().clone();
				BookmarkManager.get().addBookmark(new Bookmark(FengUtils.getText(name), FengUtils.getText(description),
						JME2MapManager.instance.betavilleToUTM(SceneGameState.getInstance().getCamera().getLocation()),
						direction.getX(),
						direction.getY(),
						direction.getZ())
				);
				close();
			}
		});
		
		getContentContainer().addWidget(nameLabel, name, descriptionLabel, description, ok);
	}

	public void finishSetup(){
		setTitle("Create Bookmark");
		setSize(targetWidth, targetHeight);
	}
}