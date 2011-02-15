/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import org.fenggui.FengGUI;
import org.fenggui.binding.render.ImageFont;
import org.fenggui.binding.render.text.DirectTextRenderer;
import org.fenggui.util.Alphabet;
import org.fenggui.util.fonttoolkit.FontFactory;

import edu.poly.bxmc.betaville.jme.fenggui.FixedButton;

/**
 * Allows for the creation of buttons with style!
 * @author Skye Book
 *
 */
public class StyledButtonFactory {
	
	public static FixedButton greenCustomGreenButton(String fontName, int fontSize, int fontStyle){
		FixedButton button = FengGUI.createWidget(FixedButton.class);
		java.awt.Font font = new java.awt.Font(fontName, fontStyle, fontSize);
		ImageFont fengFont = FontFactory.renderStandardFont(font, true, Alphabet.ENGLISH);
		DirectTextRenderer tr = new DirectTextRenderer(fengFont);
		button.getAppearance().addRenderer(DirectTextRenderer.DEFAULTTEXTRENDERERKEY, tr);
		
		// changing color - doesn't work!
		//TextStyle def = button.getAppearance().getStyle(TextStyle.DEFAULTSTYLEKEY);
	    //def.getTextStyleEntry(TextStyleEntry.DEFAULTSTYLESTATEKEY).setColor(Color.CYAN);
	    
		return button;
	}
	
	public static FixedButton greenBoldButton(String fontName, int fontSize){
		return greenCustomGreenButton(fontName, fontSize, java.awt.Font.BOLD);
	}
	
	public static FixedButton greenItalicButton(String fontName, int fontSize){
		return greenCustomGreenButton(fontName, fontSize, java.awt.Font.ITALIC);
	}
	
	public static FixedButton greenPlainButton(String fontName, int fontSize){
		return greenCustomGreenButton(fontName, fontSize, java.awt.Font.PLAIN);
	}

}
