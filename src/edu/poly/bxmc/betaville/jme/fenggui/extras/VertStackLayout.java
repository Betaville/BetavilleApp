/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import java.io.IOException;
import java.util.List;

import org.fenggui.Container;
import org.fenggui.IWidget;
import org.fenggui.layout.LayoutManager;
import org.fenggui.theme.xml.IXMLStreamableException;
import org.fenggui.theme.xml.InputOutputStream;
import org.fenggui.util.Dimension;

/**
 * @author Skye Book
 *
 */
public class VertStackLayout extends LayoutManager {

	private int spacing;

	/**
	 * 
	 */
	public VertStackLayout() {
		this(0);
	}

	/**
	 * 
	 */
	public VertStackLayout(int spacing) {
		this.spacing=spacing;
	}

	/* (non-Javadoc)
	 * @see org.fenggui.theme.xml.IXMLStreamable#process(org.fenggui.theme.xml.InputOutputStream)
	 */
	public void process(InputOutputStream stream) throws IOException,
	IXMLStreamableException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fenggui.layout.LayoutManager#doLayout(org.fenggui.Container, java.util.List)
	 */
	@Override
	public void doLayout(Container container, List<IWidget> content) {
		int currentY=0;

		for(IWidget w : content){
			w.setXY(0, currentY);
			currentY+=w.getSize().getHeight();
			currentY+=spacing;
			if(container.getHeight()<(currentY+w.getSize().getHeight())){
				container.setHeight(currentY+w.getSize().getHeight());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.fenggui.layout.LayoutManager#computeMinSize(java.util.List)
	 */
	@Override
	public Dimension computeMinSize(List<IWidget> content) {
		int width = 0;
		int height = 0;
		for (IWidget widget : content)
		{
			width = Math.max(width, widget.getSize().getWidth());
			height = Math.max(height, widget.getSize().getHeight());
			height+=spacing;
		}
		return new Dimension(width, height);
	}

}
