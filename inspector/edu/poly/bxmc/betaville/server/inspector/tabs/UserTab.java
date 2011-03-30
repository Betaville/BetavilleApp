package edu.poly.bxmc.betaville.server.inspector.tabs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.poly.bxmc.betaville.net.NetPool;

public class UserTab extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTextField search;
	
	public UserTab(){
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		
		searchPanel.add(new JLabel("Find User"));
		
		search = new JTextField();
		searchPanel.add(search);
		
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(NetPool.getPool().getConnection().checkNameAvailability(search.getText())){
					System.out.println("user exists");
				}
				else System.out.println("user does not exist");
			}
		});
		searchPanel.add(searchButton);
	}
}
