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
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.model.Comment;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.InsecureClientManager;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class SwingCommentWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel commentPanel;
	
	private JTextArea commentTextEntry;
	private JButton submitComment;

	/**
	 * @throws HeadlessException
	 */
	public SwingCommentWindow() throws HeadlessException {
		getContentPane().setLayout(new BorderLayout());
		
		commentPanel = new JPanel();
		commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(commentPanel);
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JPanel commentEntryPanel = new JPanel();
		
		commentTextEntry = new JTextArea(3, 30);
		commentTextEntry.setLayout(new BorderLayout());
		commentTextEntry.setEditable(true);
		commentTextEntry.setText("Enter Comment Here");
		commentEntryPanel.add(new JScrollPane(commentTextEntry), BorderLayout.CENTER);
		
		submitComment = new JButton("Submit");
		commentEntryPanel.add(submitComment, BorderLayout.LINE_END);
		
		getContentPane().add(commentEntryPanel, BorderLayout.PAGE_END);
		
		setSize(640, 480);
		
		// set up the comment maintenance
		SceneScape.addSelectionListener(new ISpatialSelectionListener() {
			
			@Override
			public void selectionCleared(Design previousDesign) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void designSelected(Spatial spatial, Design design) {
				commentPanel.removeAll();
				
				List<Comment> comments = NetPool.getPool().getConnection().getComments(design.getID());
				
				for(Comment comment : comments){
					JPanel postedComment = new JPanel();
					postedComment.setLayout(new BorderLayout());
					
					JLabel label = new JLabel(comment.getUser()+"\n"+comment.getDate());
					postedComment.add(label, BorderLayout.LINE_START);
					
					JTextArea commentText = new JTextArea();
					commentText.setEditable(false);
					commentText.setText(comment.getComment());
					postedComment.add(commentText, BorderLayout.CENTER);
					
					//commentPanel.add(postedComment);
					//commentPanel.add(new JSeparator(JSeparator.HORIZONTAL));
					System.out.println("bang");
					commentPanel.add(new JLabel("hello"));
				}
			}
		});
	}
}
