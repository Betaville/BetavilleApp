/** Copyright (c) 2008-2011, Brooklyn eXperimental Media Center
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
 * 
 * $Id$
 */
package edu.poly.bxmc.betaville.jme.fenggui.experimental;

import java.io.IOException;

import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.renderer.text.BufferedTextRenderer;
import org.fenggui.theme.xml.IXMLStreamableException;
import org.fenggui.theme.xml.InputOnlyStream;
import org.fenggui.theme.xml.InputOutputStream;
import org.fenggui.util.Color;

/**
 * SimpleShadowTextRenderer creates shadow by rendering the buffered text {@link Pixmap} twice.</p>
 * A SimpleShadowTextRenderer may specify the attributes {@code x} and {@code y} for x- and y-offset. 
 * 
 * @author Peter Schulz
 */
public class SimpleShadowTextRenderer extends BufferedTextRenderer {
  
  private int offsetX = 1;
  private int offsetY = 1;
  private Color shadowColor = Color.BLACK;
  
  /**
   * Creates a SimpleShadowTextRenderer.
   */
  public SimpleShadowTextRenderer() {
    super();
  }
  
  /**
   * Creates a SimpleShadowTextRenderer.
   * @param stream
   * @throws IOException
   * @throws IXMLStreamableException
   */
  public SimpleShadowTextRenderer(InputOnlyStream stream) throws IOException, IXMLStreamableException {
    process(stream);
  }
  
  @Override
  public void process(InputOutputStream stream) throws IOException,
      IXMLStreamableException
  {
    super.process(stream);
    
    offsetX = stream.processAttribute("x", offsetX, 0);
    offsetY = stream.processAttribute("y", offsetY, 0);
    shadowColor = stream.processChild("Color", shadowColor, Color.BLACK, Color.class);
  }
  
  @Override
  public void render(int x, int y, String text, Color color, Graphics g)
  {
    Pixmap pixmap = getPixmap(text);
    if (pixmap != null) {
      g.setColor(shadowColor);
      g.drawImage(pixmap, x + offsetX, y + offsetY - getLineHeight());
      g.forceColor(true);
    }
    super.render(x, y, text, color, g);
  }
}
