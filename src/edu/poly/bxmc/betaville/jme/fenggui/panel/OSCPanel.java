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
		//initialize one of the OnScreenControllers - could be extracted into a factory
		//osc = new OnScreenController(); //Old Layout
		
		osc = new OnScreenControllerPanel(); //Panel Layout
		addWidget(osc);
	}
	
	@Override
	public void finishSetup() {
		setTitle("Controls");
		setSize(osc.getWidth() + 2, osc.getHeight() + 20);
	}

}
