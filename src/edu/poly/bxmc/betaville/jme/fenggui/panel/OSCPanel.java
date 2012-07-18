/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import org.fenggui.Container;
import org.fenggui.composite.Window;

import edu.poly.bxmc.betaville.jme.fenggui.OnScreenControllerPanel;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;

/**
 * @author Johannes Lier
 *
 */
public class OSCPanel extends Window implements IBetavilleWindow {

	Container osc;
	
	public OSCPanel() {
		osc = new OnScreenControllerPanel();
		addWidget(osc);
	}
	
	@Override
	public void finishSetup() {
		setTitle("Controls");
		setSize(osc.getWidth() + 2, osc.getHeight() + 20);
	}

}
