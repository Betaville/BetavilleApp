/** Copyright (c) 2008-2010, Brooklyn eXperimental Media Center
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
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Cursor;
import org.fenggui.binding.render.lwjgl.LWJGLCursorFactory;
import org.fenggui.composite.tree.Tree;
import org.fenggui.decorator.switches.SetFontStyleSwitch;
import org.fenggui.renderer.text.BufferedTextRenderer;
import org.fenggui.text.content.IContentFactory;
import org.fenggui.text.content.factory.simple.ContentFactory;
import org.fenggui.theme.XMLTheme;
import org.fenggui.theme.xml.IXMLStreamableException;
import org.fenggui.util.Color;
import org.fenggui.util.Util;

import edu.poly.bxmc.betaville.jme.fenggui.experimental.SimpleShadowTextRenderer;
import edu.poly.bxmc.betaville.jme.fenggui.tab.BottomTabContainer;
import edu.poly.bxmc.betaville.jme.fenggui.tab.BottomTabItem;

public class BetavilleXMLTheme extends XMLTheme {
	
	static {
	      TYPE_REGISTRY.register("Tabs", org.fenggui.composite.tab.TabContainer.class);
	      TYPE_REGISTRY.register("BottomTabs", BottomTabContainer.class);
	      TYPE_REGISTRY.register(org.fenggui.composite.tab.TabItem.class);
	      TYPE_REGISTRY.register(BottomTabItem.class);
	      TYPE_REGISTRY.register(edu.poly.bxmc.betaville.jme.fenggui.tab.TabContent.class);
//	      TYPE_REGISTRY.register(FixedButton.class);
	      TYPE_REGISTRY.register(Tree.class);
//	      TYPE_REGISTRY.register(ScrollContainer.class);
//	      TYPE_REGISTRY.register(ScrollBar.class);
//	      TYPE_REGISTRY.register(Slider.class);
//	      XMLTheme.TYPE_REGISTRY.register(TabItemLabel.class);
//	      XMLTheme.TYPE_REGISTRY.register(SnappingSlider.class);
//	      XMLTheme.TYPE_REGISTRY.register(Table.class);
	      
	      TYPE_REGISTRY.register("FontStyleSwitch", SetFontStyleSwitch.class);
	      
	      TYPE_REGISTRY.register(BufferedTextRenderer.class);
	      TEXTRENDERER_REGISTRY.register(BufferedTextRenderer.class);

	      TYPE_REGISTRY.register(SimpleShadowTextRenderer.class);
	      TEXTRENDERER_REGISTRY.register(SimpleShadowTextRenderer.class);
	}

	public BetavilleXMLTheme(String xmlThemeFile) throws IOException,
			IXMLStreamableException {
		super(xmlThemeFile);
		
		initialzieCursors();
	}

	private void initialzieCursors() {
	  Color textCursorColor = Color.WHITE;

	  IContentFactory cf = ContentFactory.getDefaultFactory();
	  if (cf instanceof ContentFactory) {
	    ((ContentFactory) cf)
	    .setTextCursorRenderer(new BetavilleTextCursorRenderer(textCursorColor));
	  }

	  int[][] textCursorMask = LWJGLCursorFactory.TEXT_CURSOR;
	  int size = textCursorMask.length;
	  BufferedImage textCursorImage = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
	  int textCursorRGB = Util.convert(textCursorColor).getRGB();

	  for (int i = 0; i < textCursorMask.length; ++i) {
	    for (int j = 0; j < textCursorMask[i].length; ++j) {
	      if (textCursorMask[i][j] == 0xff000000)
	        textCursorMask[i][j] |= textCursorRGB;
	      textCursorImage.setRGB(j, i, textCursorMask[i][j]);
	    }
	  }

	  Cursor textCursor = Binding.getInstance().getCursorFactory().createCursor(3, size / 2, textCursorImage);
	  Binding.getInstance().getCursorFactory().setTextCursor(textCursor);
	}
}
