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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class SwingProposalWindow extends JFrame{
	private static final long serialVersionUID = 1L;

	private JPanel topBar;
	private JButton updateListButton;

	private JScrollPane proposalScroller;
	private JTree proposalTree;

	private JScrollPane infoScroller;
	private JEditorPane infoPane;

	private DefaultMutableTreeNode topTreeNode;



	/**
	 * @throws HeadlessException
	 */
	public SwingProposalWindow() throws HeadlessException {
		setTitle("Proposals");
		setSize(640, 480);
		getContentPane().setLayout(new BorderLayout());

		topBar = new JPanel();
		updateListButton = new JButton("Update List");
		updateListButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsPreferences.getGUIThreadPool().execute(new Runnable() {
					
					@Override
					public void run() {
						updateListButton.setEnabled(false);
						updateListButton.setText("Updating");
						updateTree();
						updateListButton.setEnabled(true);
						updateListButton.setText("Update List");
					}
				});
				
			}
		});

		topBar.add(updateListButton);
		add(topBar, BorderLayout.NORTH);


		topTreeNode = new DefaultMutableTreeNode("Proposals");
		proposalTree = new JTree(topTreeNode);
		proposalTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		proposalTree.addTreeSelectionListener(new ProposalTreeSelectionListener());

		proposalScroller = new JScrollPane(proposalTree);

		add(proposalScroller, BorderLayout.CENTER);

		infoPane = new JEditorPane();
		infoPane.setEditable(false);
		infoPane.setContentType("text/html");
		populateInfoPane(null);

		infoPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {

			}
		});

		infoScroller = new JScrollPane(infoPane);

		add(infoScroller, BorderLayout.SOUTH);

	}
	
	private synchronized void updateTree(){
		// clear any pre-existing children from the tree
		topTreeNode.removeAllChildren();
		
		NetPool.getPool().setAutoCleanupEnabled(false);
		
		ILocation location = JME2MapManager.instance.betavilleToUTM(SceneGameState.getInstance().getCamera().getLocation());
		List<Design> proposalDesigns = NetPool.getPool().getConnection().findAllProposalsNearLocation(location.getUTM(), 30000);
		for(Design proposalDesign : proposalDesigns){
			DesignNode proposalRootNode = new DesignNode(proposalDesign);

			int[] versionsOfProposal = NetPool.getPool().getConnection().findVersionsOfProposal(proposalDesign.getID());
			if(versionsOfProposal!=null){
				for(int thisIsConvoluted : versionsOfProposal){
					Design version = NetPool.getPool().getConnection().findDesignByID(thisIsConvoluted);
					if(version!=null){
						proposalRootNode.add(new DesignNode(version));
					}
				}
			}
			
			NetPool.getPool().setAutoCleanupEnabled(true);
			
			topTreeNode.add(proposalRootNode);
		}
	}

	private class DesignNode extends DefaultMutableTreeNode{
		private static final long serialVersionUID = 1L;

		private Design design;

		public DesignNode(Design design){
			super(design.getName());
			this.design=design;
		}

		public String toString(){
			return design.getName();
		}
	}

	private class ProposalTreeSelectionListener implements TreeSelectionListener{

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if(proposalTree.getLastSelectedPathComponent() instanceof DesignNode){
				Design design = ((DesignNode)proposalTree.getLastSelectedPathComponent()).design;
				populateInfoPane(design);
			}
			else{
				populateInfoPane(null);
			}
			
			validate();
		}
	}
	
	private void populateInfoPane(Design design){
		StringBuilder sb = new StringBuilder();
		sb.append("<style> type=\"text/css\" font-family: verdana, sans-serif;</style>");
		sb.append("<strong>Name:</strong> ");
		sb.append(design==null?"":design.getName());
		sb.append("<br><strong>User:</strong> ");
		sb.append(design==null?"":design.getUser());
		sb.append("<br><strong>Description: </strong>");
		sb.append(design==null?"":design.getDescription());
		infoPane.setText(sb.toString());
	}

}
