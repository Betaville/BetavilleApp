/* Copyright (c) 2008-2011, Brooklyn eXperimental Media Center
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
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import org.fenggui.binding.render.Graphics;
import org.fenggui.text.ITextCursorRenderer;
import org.fenggui.util.Color;

/**
 * BetavilleTextCursorRenderer renders a customisable, blinking cursor. 
 * @author Peter Schulz
 */
public class BetavilleTextCursorRenderer implements ITextCursorRenderer
{
  private static final int   DEFAULT_WIDTH = 1;
  private static final Color DEFAULT_COLOR = Color.BLACK;
  
  private long               lastTime      = 0;
  private long               delta         = 500;
  private boolean            show          = false;
  
  private int                width         = DEFAULT_WIDTH;
  private Color              color         = DEFAULT_COLOR;
  
  
  /**
   * Creates a new BetavilleTextCursorRenderer.
   * The Color defaults to black.
   * @param width the cursor width
   */
  public BetavilleTextCursorRenderer(int width)
  {
    this(width, DEFAULT_COLOR);
  }
  
  /**
   * Creates a new BetavilleTextCursorRenderer.
   * The width defaults to {@value #DEFAULT_WIDTH}.
   * @param color the cursor color
   */
  public BetavilleTextCursorRenderer(Color color)
  {
    this(DEFAULT_WIDTH, color);
  }
  
  /**
   * Creates a new BetavilleTextCursorRenderer.
   * @param width the cursor width
   * @param color the cursor color
   */
  public BetavilleTextCursorRenderer(int width, Color color)
  {
    this.width = width;
    this.color = color;
  }
  
  /* (non-Javadoc)
   * @see org.fenggui.text.ITextCursorRenderer#getWidth()
   */
  public int getWidth()
  {
    return width;
  }

  /* (non-Javadoc)
   * @see org.fenggui.text.ITextCursorRenderer#getHeight()
   */
  public int getHeight()
  {
    return DYNAMICSIZE;
  }

  /* (non-Javadoc)
   * @see org.fenggui.text.ITextCursorRenderer#render(int, int, int, int, org.fenggui.binding.render.Graphics)
   */
  public void render(int x, int y, int w, int h, Graphics g)
  {
    if (System.currentTimeMillis() - lastTime > delta) 
    {
      lastTime = System.currentTimeMillis();
      show = !show;
    }
      
    if (!show) return;
    
    g.setColor(color);
    g.drawFilledRectangle(x, y, w, h);
  }

}
