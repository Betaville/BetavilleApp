/**
 * 
 */
package edu.poly.bxmc.betaville.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

/**
 * 
 * @author Nick Ebbutt (from ObjectDefinitions.com)
 *
 */
public class JPopupTextArea extends JTextArea{
	private static final long serialVersionUID = 1L;

	private HashMap<Object, Object> actions;

	public JPopupTextArea()
	{
		addPopupMenu();
	}

	private void addPopupMenu()
	{
		createActionTable();

		JPopupMenu menu = new JPopupMenu();
		menu.add(getActionByName(DefaultEditorKit.copyAction, "Copy"));
		menu.add(getActionByName(DefaultEditorKit.cutAction, "Cut"));
		menu.add(getActionByName(DefaultEditorKit.pasteAction, "Paste"));
		menu.add(new JSeparator());
		menu.add(getActionByName(DefaultEditorKit.selectAllAction, "Select All"));
		add(menu);

		addMouseListener(
				new PopupTriggerMouseListener(
						menu,
						this
						)
				);

		//no need to hold the references in the map,
		// we have used the ones we need.
		actions.clear();
	}

	private Action getActionByName(String name, String description) {
		Action a = (Action)(actions.get(name));
		a.putValue(Action.NAME, description);
		return a;
	}


	private void createActionTable() {
		actions = new HashMap<Object, Object>();
		Action[] actionsArray = getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			actions.put(a.getValue(Action.NAME), a);
		}
	}

	public static class PopupTriggerMouseListener extends MouseAdapter
	{
		private JPopupMenu popup;
		private JComponent component;

		public PopupTriggerMouseListener(JPopupMenu popup, JComponent component)
		{
			this.popup = popup;
			this.component = component;
		}

		//some systems trigger popup on mouse press, others on mouse release, we want to cater for both
		private void showMenuIfPopupTrigger(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				popup.show(component, e.getX() + 3, e.getY() + 3);
			}
		}

		//according to the javadocs on isPopupTrigger, checking for popup trigger on mousePressed and mouseReleased 
		//should be all  that is required
		//public void mouseClicked(MouseEvent e)  
		//{
		//    showMenuIfPopupTrigger(e);
		//}

		public void mousePressed(MouseEvent e)
		{
			showMenuIfPopupTrigger(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			showMenuIfPopupTrigger(e);
		}

	}

}