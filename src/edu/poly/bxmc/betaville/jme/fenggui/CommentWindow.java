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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.TextEditor;
import org.fenggui.composite.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISizeChangedListener;
import org.fenggui.event.SizeChangedEvent;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Alignment;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BlockingScrollContainer;
import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Comment;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.updater.AbstractUpdater;
import edu.poly.bxmc.betaville.updater.BetavilleTask;
import edu.poly.bxmc.betaville.util.ITextFilter;

/**
 * @author Skye Book
 *
 */
public class CommentWindow extends Window implements IBetavilleWindow{
	private static Logger logger = Logger.getLogger(CommentWindow.class);
	private int targetWidth = 400;
	private int targetHeight = 500;
	
	private int currentDesign;
	
	List<ITextFilter> contentFilters;
	
	private AbstractUpdater commentUpdater;
	
	private Container entryContainer;
	private TextEditor newCommentEditor;
	private FixedButton postComment;
	
	private BlockingScrollContainer sc;
	private double rememberedPosition=0;
	private TextEditor commentText;
	private Label intermediary;
	
	private boolean latestOnBottom=true;
	private int commentCount=0;

	/**
	 * 
	 */
	public CommentWindow(){
		super(true, true);
		contentFilters = new ArrayList<ITextFilter>();
		//getContentContainer().setSize(targetWidth, targetHeight);
		getContentContainer().setLayoutManager(new StaticLayout());
		
		intermediary = FengGUI.createWidget(Label.class);
		intermediary.setText("Getting Comments");
		
		createEntryContainer();
		createCommentContainer();
		getContentContainer().layout();
		
		SceneScape.addSelectionListener(new ISpatialSelectionListener(){
			/*
			 * (non-Javadoc)
			 * @see edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener#selectionCleared(edu.poly.bxmc.betaville.model.Design)
			 */
			public void selectionCleared(Design previousDesign){
				postComment.setEnabled(false);
			}
			/*
			 * (non-Javadoc)
			 * @see edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener#designSelected(com.jme.scene.Spatial, edu.poly.bxmc.betaville.model.Design, edu.poly.bxmc.betaville.model.Design)
			 */
			public void designSelected(Spatial spatial, Design design){
				postComment.setEnabled(true);
				if(isInWidgetTree()) setCurrentDesign(design.getID());
			}
		});
		
		createCommentUpdater();
	}
	
	private void createCommentContainer(){
		sc = FengGUI.createWidget(BlockingScrollContainer.class);
		sc.setSize(targetWidth, targetHeight-titleBar.getHeight()-newCommentEditor.getHeight()-postComment.getHeight()-20);
		sc.setXY(0, newCommentEditor.getHeight()+postComment.getHeight()+5);
		sc.setShowScrollbars(true);
		commentText = FengGUI.createWidget(TextEditor.class);
		commentText.setWordWarping(true);
		commentText.setMultiline(true);
		commentText.getAppearance().setAlignment(Alignment.TOP_LEFT);
		commentText.setReadonly(true);
		commentText.setEnabled(false);
		sc.setInnerWidget(commentText);
		//sc.setXY(0, entryContainer.getHeight());
		getContentContainer().addWidget(sc);
		sc.layout();
	}
	
	private void createEntryContainer(){
		entryContainer = FengGUI.createWidget(Container.class);
		entryContainer.setLayoutManager(new StaticLayout());
		entryContainer.setSize(targetWidth-10, 50);
		
		postComment = FengGUI.createWidget(FixedButton.class);
		postComment.setText("post");
		//postComment.setWidth(postComment.getWidth()+10);
		//postComment.setXY(FengUtils.midWidth(entryContainer, postComment), 0);
		postComment.setXY(0, 0);
		postComment.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				postComment();
			}
		});
		
		newCommentEditor = FengGUI.createWidget(TextEditor.class);
		newCommentEditor.setMultiline(true);
		newCommentEditor.setWordWarping(true);
		newCommentEditor.setSize(targetWidth-5, newCommentEditor.getHeight());
		//newCommentEditor.setXY(FengUtils.midWidth(entryContainer, newCommentEditor), postComment.getHeight());
		newCommentEditor.setXY(0, postComment.getHeight()+5);
		newCommentEditor.setText(" ");
		newCommentEditor.layout();
		newCommentEditor.addSizeChangedListener(new ISizeChangedListener() {
			
			public void sizeChanged(Object sender, SizeChangedEvent event) {
				int deltaY = event.getNewSize().getHeight()-event.getOldSize().getHeight();
				
				// shuffle and shift the scroll container
				sc.setHeight(sc.getHeight()-deltaY);
				sc.layout();
				sc.setY(sc.getY()+deltaY);
			}
		});
		
		getContentContainer().addWidget(newCommentEditor, postComment);
		//entryContainer.addWidget(postComment, newCommentEditor);
		//getContentContainer().addWidget(entryContainer);
	}
	
	private void createCommentUpdater(){
		
		commentUpdater = new AbstractUpdater(15000) {
			private boolean isInUpdate=false;
			
			public void doUpdate(){
				isInUpdate=true;
				refresh(false);
				isInUpdate=false;
			}
			
			public boolean isUpdateRequired() {
				return isInWidgetTree();
			}

			@Override
			public boolean isInUpdate() {
				return isInUpdate;
			}
		};
		
		BetavilleNoCanvas.getUpdater().addTask(new BetavilleTask(commentUpdater));
	}
	
	public void finishSetup(){
		setTitle("Comments");
		setSize(targetWidth, targetHeight);
	}
	
	
	public void setCurrentDesign(final int designID){
		rememberedPosition=sc.getVerticalScrollBar().getSlider().getValue();
		//sc.setInnerWidget(intermediary);
		sc.layout();
		
		String content = "";
		
		// create new content
		logger.info("getting comments for " + currentDesign);
		List<Comment> comments = NetPool.getPool().getConnection().getComments(designID);
		if(designID==currentDesign) if(comments.size()==commentCount){
			logger.info("Comments don't need updating");
			return;
		}
		
		if(!latestOnBottom)Collections.reverse(comments);
		for(int i=0; i<comments.size(); i++){
			logger.info("doing comment " + i);
			Comment c = comments.get(i);
			//commentText.addContentAtEnd("\n"+c.getUser()+" ("+c.getDate()+")\n"+c.getComment()+"\n");
			content+=("\n"+c.getUser()+" ("+c.getDate()+")\n"+c.getComment()+"\n");
		}
		commentText.setText(content);
		//sc.setInnerWidget(commentText);
		sc.layout();
		if(designID==currentDesign) sc.getVerticalScrollBar().getSlider().setValue(rememberedPosition);
		currentDesign = designID;
		commentCount=comments.size();
	}

	private void postComment(){
		String newComment = FengUtils.getText(newCommentEditor);
		
		// check for invalid comment
		if(newComment.isEmpty() || newComment.equals(" ")){
			logger.info("Cannot add empty comment");
			return;
		}
		
		// Scan for dirty content
		for(ITextFilter filter : contentFilters){
			if(!filter.isClean(newComment)){
				// flash problem
			}
		}
		
		Comment comment = new Comment(0,
				currentDesign,
				SettingsPreferences.getUser(),
				newComment,
				0);
		boolean response = NetPool.getPool().getSecureConnection().addComment(comment, SettingsPreferences.getPass());
		if(response) refresh(true);
		else{
			logger.error("Comment submission failed");
			// darn!
		}
	}
	
	private void refresh(boolean clearNewText){
		setCurrentDesign(currentDesign);
		if(clearNewText){
			newCommentEditor.setText("");
			newCommentEditor.setHeight(entryContainer.getHeight()-postComment.getHeight());
		}
	}
	
	public void close(){
		super.close();
	}	
}