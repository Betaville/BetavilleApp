package edu.poly.bxmc.betaville.server.inspector.tabs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;

public class UserTab extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextField search;

	public UserTab(){
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		Box searchPanel = new Box(BoxLayout.X_AXIS);
		//searchPanel.setLayout(new BoxLayout(searchPanel,));

		searchPanel.add(new JLabel("Find User"));

		search = new JTextField();
		searchPanel.add(search);

		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(NetPool.getPool().getConnection().checkNameAvailability(search.getText())){
					System.out.println("user does not exist");
					add(createNoUserPanel());
				}
				else{
					System.out.println("user exists");
					add(createUserInfo());
				}
			}
		});
		searchPanel.add(searchButton);
		//searchPanel.setSize(searchPanel.getMinimumSize());
		searchPanel.validate();
		validate();

		add(searchPanel);
	}

	private JPanel createUserInfo(){
		JPanel u = new JPanel();

		u.add(new JLabel("USER: "+search.getText()));

		List<Design> userDesigns = NetPool.getPool().getConnection().findDesignsByUser(search.getText());

		u.add(new JLabel(userDesigns.size()+" designs by " + search.getText()));

		return u;
	}

	private JPanel createNoUserPanel(){
		JPanel u = new JPanel();

		u.add(new JLabel("User does not exist"));

		return u;
	}
}
