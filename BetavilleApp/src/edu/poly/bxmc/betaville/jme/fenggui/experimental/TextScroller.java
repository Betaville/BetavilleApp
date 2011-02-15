/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.experimental;

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;

/**
 * @author Skye Book
 *
 */
public class TextScroller extends Container {
	private ScrollContainer sc;
	private TextEditor te;

	/**
	 * 
	 */
	public TextScroller(int width, int height) {
		super(new RowExLayout(false));
		setSize(width, height);
		
		sc = FengGUI.createWidget(ScrollContainer.class);
		addWidget(sc);
		sc.setLayoutData(new RowExLayoutData(true, true));
		
		System.out.println("sc created");
		
		te = FengGUI.createWidget(TextEditor.class);
		te.setMultiline(true);
		te.setWordWarping(true);
		
		System.out.println("te created");
		
		sc.addWidget(te);
	}
	
	public TextEditor getTextEditor(){
		return te;
	}
}
