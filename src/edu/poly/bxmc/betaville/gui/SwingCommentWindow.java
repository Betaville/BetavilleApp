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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.centerkey.utils.BareBonesBrowserLaunch;
import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.ShutdownManager;
import edu.poly.bxmc.betaville.ShutdownManager.IShutdownProcedure;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Comment;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class SwingCommentWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private int currentDesignCommentThread = -1;

	private JScrollPane scrollPane;
	private JEditorPane commentPane;

	private JSplitPane bottomSplit;
	private JButton submit;
	private JScrollPane editorScroller;
	private JPopupTextArea commentEditor;



	/**
	 * @throws HeadlessException
	 */
	public SwingCommentWindow() throws HeadlessException {
		setTitle("Discussion");
		setSize(640, 480);
		getContentPane().setLayout(new BorderLayout());

		commentPane = new JEditorPane();
		commentPane.setEditable(false);
		commentPane.setContentType("text/html");
		commentPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)){
					BareBonesBrowserLaunch.openURL(e.getURL().toString());
				}
			}
		});

		scrollPane = new JScrollPane(commentPane);

		bottomSplit = new JSplitPane();
		bottomSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		bottomSplit.setResizeWeight(1.0);

		submit = new JButton("Submit");
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				// do nothing if there is no content
				if(commentEditor.getText().length()==0) return;
				
				commentEditor.setEditable(false);
				submit.setText("...");
				submit.setEnabled(false);

				boolean result = NetPool.getPool().getSecureConnection().addComment(
						new Comment(0,
								currentDesignCommentThread,
								SettingsPreferences.getUser(),
								commentEditor.getText(),
								0),
								SettingsPreferences.getPass()
						);
				
				// if the comment submission succeeded, we can clear the text editor
				if(result) commentEditor.setText("");
				else{
					// if it didn't, we should flash the error
				}
				
				updateCommentDisplay(currentDesignCommentThread);
				
				commentEditor.setEditable(true);
				submit.setText("Submit");
				submit.setEnabled(true);
			}
		});

		bottomSplit.setRightComponent(submit);

		commentEditor = new JPopupTextArea();
		commentEditor.setLineWrap(true);
		//commentEditor.setColumns(20);
		commentEditor.setRows(3);

		editorScroller = new JScrollPane();
		editorScroller.setViewportView(commentEditor);

		bottomSplit.setLeftComponent(editorScroller);

		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(bottomSplit, BorderLayout.SOUTH);


		ShutdownManager.shutdownProcedures.add(new IShutdownProcedure() {

			@Override
			public boolean runProcedure() {
				setVisible(false);
				return true;
			}
		});

		// set up the comment maintenance
		SceneScape.addSelectionListener(new ISpatialSelectionListener() {

			@Override
			public void selectionCleared(Design previousDesign) {
				commentPane.removeAll();
				currentDesignCommentThread = -1;
			}

			@Override
			public void designSelected(Spatial spatial, Design design) {

				updateCommentDisplay(design.getID());
			}
		});
	}
	
	private synchronized void updateCommentDisplay(int designID){
		List<Comment> comments = NetPool.getPool().getConnection().getComments(designID);

		StringBuilder sb = new StringBuilder();
		sb.append("<style> type=\"text/css\" font-family: verdana, sans-serif;</style>");

		for(int i=0; i<comments.size(); i++){
			Comment comment = comments.get(i);
			sb.append("<b><a href=http://betaville.net/profile.php?uName="+comment.getUser()+"\">"+comment.getUser()+"</a> ("+comment.getDate()+")</b> - ");
			sb.append(comment.getComment());
			sb.append("<br>");
			
			// add a horizontal rule if this is not the last comment
			if(i<comments.size()-1) sb.append("<hr>");
		}

		commentPane.setText(sb.toString());
		validate();
		
		currentDesignCommentThread = designID;
	}
}
