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

import java.text.DateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.Event;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseEvent;
import org.fenggui.event.mouse.MouseExitedEvent;
import org.fenggui.event.mouse.MouseReleasedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.bookmarks.Bookmark;
import edu.poly.bxmc.betaville.bookmarks.BookmarkManager;
import edu.poly.bxmc.betaville.bookmarks.IBookmarkChangeListener;
import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;

/**
 * @author Skye Book
 *
 */
public class BookmarkWindow extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(BookmarkWindow.class);
	
	private int targetWidth = 350;
	private int targetHeight = 300;
	private Container list;
	private Container bottom;
	private FixedButton addBookmark;
	private FixedButton removeBookmark;
	private FixedButton editBookmark;
	private FixedButton goTo;
	private BookmarkPopup addPopup;
	private String selectedBookmarkID;

	/**
	 * 
	 */
	public BookmarkWindow() {
		getContentContainer().setLayoutManager(new RowExLayout(false));
		list = FengGUI.createWidget(Container.class);
		list.setLayoutManager(new RowLayout(false));
		updateList();
		setupBottomButtons();
		setupGoTo();
		getContentContainer().addWidget(bottom, list);
		
		BookmarkManager.get().addListener(new IBookmarkChangeListener() {
			
			public void bookmarkRemoved(String bookmarkID) {
				generalUpdate();
			}
			
			public void bookmarkModified(String bookmarkID) {
				generalUpdate();
			}
			
			public void bookmarkAdded(Bookmark b) {
				generalUpdate();
			}
			
			private void generalUpdate(){
				getContentContainer().removeAllWidgets();
				updateList();
				getContentContainer().addWidget(list, bottom);
			}
		});
	}
	
	private void setupGoTo(){
		goTo = FengGUI.createWidget(FixedButton.class);
		goTo.setText(Labels.get("Generic.go"));
		goTo.setWidth(goTo.getWidth()+10);
		goTo.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				moveToSelectedBookmark();
			}
		});
	}
	
	private void moveToSelectedBookmark(){
		if(selectedBookmarkID==null) return;
		final Bookmark b = BookmarkManager.get().getBookmark(selectedBookmarkID);
		SceneGameState.getInstance().getCamera().setLocation(JME2MapManager.instance.locationToBetaville(b.getLocation()));
		//SceneGameState.getInstance().getCamera().getDirection().setX(b.getDirectionX());
		//SceneGameState.getInstance().getCamera().getDirection().setY(b.getDirectionY());
		//SceneGameState.getInstance().getCamera().getDirection().setZ(b.getDirectionZ());
		//SceneGameState.getInstance().getCamera().normalize();
		//SceneGameState.getInstance().getCamera().update();
		
		//Matrix3f rotMatrix = new Matrix3f();
	}
	
	private void setupBottomButtons(){
		bottom = FengGUI.createWidget(Container.class);
		bottom.setLayoutManager(new StaticLayout());
		
		addBookmark = FengGUI.createWidget(FixedButton.class);
		addBookmark.setText(Labels.get(this.getClass().getSimpleName()+".add_bookmark"));
		addBookmark.setWidth(addBookmark.getWidth()+10);
		addBookmark.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(addPopup==null){
					addPopup = FengGUI.createWidget(BookmarkPopup.class);
					addPopup.finishSetup();
					addPopup.setXY(FengUtils.midWidth(GUIGameState.getInstance().getDisp(), addPopup), FengUtils.midHeight(GUIGameState.getInstance().getDisp(), addPopup));
				}
				if(!addPopup.isInWidgetTree()) GUIGameState.getInstance().getDisp().addWidget(addPopup);
				else GUIGameState.getInstance().getDisp().removeWidget(addPopup);
			}
		});
		
		removeBookmark = FengGUI.createWidget(FixedButton.class);
		removeBookmark.setText(Labels.get(this.getClass().getSimpleName()+".remove_bookmark"));
		removeBookmark.setWidth(removeBookmark.getWidth()+10);
		removeBookmark.setEnabled(false);
		removeBookmark.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				BookmarkManager.get().removeBookmark(selectedBookmarkID);
				selectedBookmarkID=null;
				removeBookmark.setEnabled(false);
				editBookmark.setEnabled(false);
			}
		});
		
		editBookmark = FengGUI.createWidget(FixedButton.class);
		editBookmark.setText(Labels.get(this.getClass().getSimpleName()+".edit_bookmark"));
		editBookmark.setWidth(editBookmark.getWidth()+10);
		editBookmark.setEnabled(false);
		editBookmark.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				// TODO Edit the bookmark!  Actually... do we even need this?
			}
		});
		
		bottom.setSize(targetWidth, editBookmark.getHeight());
		removeBookmark.setXY(FengUtils.midWidth(bottom, removeBookmark), 0);
		editBookmark.setXY(bottom.getWidth()-editBookmark.getWidth()-5, 0);
		addBookmark.setXY(5, 0);
		bottom.addWidget(addBookmark, removeBookmark, editBookmark);
	}
	
	private void updateList(){
		list.removeAllWidgets();
		
		for(Bookmark b : BookmarkManager.get().getBookmarks()){
			BookmarkWidget w = new BookmarkWidget();
			w.setupInfo(b.getName(), b.getDescription(), b.getCreatedOn());
			w.bookmarkID=b.getBookmarkID();
			list.addWidget(w);
		}
		list.setSizeToMinSize();
		this.layout();
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);
	}
	
	private class BookmarkWidget extends Container{
		private String bookmarkID;
		private Label info;
		
		private BookmarkWidget(){
			super(new RowExLayout(true));
			setWidth(targetWidth-10);
			info=FengGUI.createWidget(Label.class);
			this.addWidget(info);
			this.addEventListener(EVENT_MOUSE, new IGenericEventListener() {
				public void processEvent(Object source, Event event) {
					if(!(event instanceof MouseEvent)) return;
					handleMouseEvent(event);
				}
			});
		}
		
		private void handleMouseEvent(Event event){
			if(event instanceof MouseEnteredEvent){
				this.getAppearance().add(new PlainBackground(Color.BLACK_HALF_TRANSPARENT));
			}
			else if(event instanceof MouseExitedEvent){
				if(selectedBookmarkID!=null) if(!selectedBookmarkID.equals(bookmarkID)) this.getAppearance().removeAll();
			}
			if(event instanceof MouseReleasedEvent){
				selectedBookmarkID=bookmarkID;
				editBookmark.setEnabled(true);
				removeBookmark.setEnabled(true);
				if(goTo.isInWidgetTree()) ((Container)goTo.getParent()).removeWidget(goTo);
				this.addWidget(goTo);
				for(IWidget w : list.getWidgets()){
					if((BookmarkWidget)w==this) ((BookmarkWidget)w).getAppearance().add(new PlainBackground(Color.BLACK));
					else ((BookmarkWidget)w).getAppearance().removeAll();
				}
			}
		}
		
		private void setupInfo(String title, String description, long date){
			String displayableDescription=description;
			if(displayableDescription.length()>20)displayableDescription=new String(displayableDescription.substring(0, 15))+"...";
			info.setText(title+" - "+DateFormat.getDateTimeInstance().format(new Date(date))+" - "+displayableDescription);
		}
	}
}
