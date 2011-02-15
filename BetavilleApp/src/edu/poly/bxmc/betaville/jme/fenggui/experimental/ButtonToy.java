/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.experimental;

import org.fenggui.Button;
import org.fenggui.FengGUI;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.GradientBackground;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.decorator.border.BevelBorder;
import org.fenggui.layout.RowExLayout;
import org.fenggui.util.Color;

import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;

/**
 * Provides a quick canvas for debugging buttons
 * @author Skye Book
 *
 */
public class ButtonToy extends Window implements IBetavilleWindow{
	
	private Color pressed = new Color(.25f, .75f, .25f, 1);
	private Color normal = new Color(.25f, .85f, .25f, 1);
	private Color over = new Color(.25f, .95f, .25f, 1);

	/**
	 * 
	 */
	public ButtonToy() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		
		FixedButton fixedButton = FengGUI.createWidget(FixedButton.class);
		fixedButton.setText(FixedButton.class.getName());
		
		Button b1 = FengGUI.createWidget(Button.class);
		
		GradientBackground g1 = new GradientBackground(Color.BLACK, Color.BLUE);
		GradientBackground g2 = new GradientBackground(Color.BLACK, Color.BLUE, Color.GREEN, Color.RED);
		PlainBackground pb1 = new PlainBackground(Color.CYAN);
		
		
		
		b1.getAppearance().add(Button.STATE_DEFAULT.toString(), new PlainBackground(normal));
		b1.getAppearance().add(Button.STATE_HOVERED.toString(), new PlainBackground(over));
		b1.getAppearance().add(Button.STATE_PRESSED.toString(), new PlainBackground(pressed));
		b1.setText(Button.class.getName());
		
		b1.getAppearance().add(Button.STATE_DEFAULT.toString(), new BevelBorder(Color.BLUE, Color.RED));
		b1.getAppearance().add(Button.STATE_PRESSED.toString(), new BevelBorder(Color.RED, Color.BLUE));
		
		getContentContainer().addWidget(fixedButton, b1);
	}
	
	public void finishSetup(){
		setTitle("Button Debugger");
		setSize(25, 50);
	}
}
