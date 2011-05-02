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
*/
package edu.poly.bxmc.betaville.jme.fenggui.experimental;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fenggui.binding.render.Graphics;
import org.fenggui.binding.render.IOpenGL;
import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.ImageFont;
import org.fenggui.binding.render.text.BaseTextRenderer;
import org.fenggui.theme.xml.IXMLStreamableException;
import org.fenggui.theme.xml.InputOnlyStream;
import org.fenggui.util.CharacterPixmap;
import org.fenggui.util.Color;

/**
 * Adapted from DirectTextRenderer
 * @author Skye Book
 *
 */
public class RichTextRenderer extends BaseTextRenderer {
	private Color red = Color.RED;
	private Color blue = Color.BLUE;
	
	private boolean notNormal=false;
	
	private Pattern urlPattern = Pattern.compile(".*\\[[uU][rR][lL]=.+\\].*");
	
	private ArrayList<Integer> urlIndices = new ArrayList<Integer>();

	public RichTextRenderer()
	{
		this(ImageFont.getDefaultFont());
	}

	public RichTextRenderer(ImageFont font)
	{
		super(font);
	}

	public RichTextRenderer(InputOnlyStream stream) throws IOException, IXMLStreamableException
	{
		process(stream);
	}

	public void render(int x, int y, String[] texts, Color color, Graphics g)
	{
		if (texts == null || texts.length == 0)
			return;

		IOpenGL gl = g.getOpenGL();

		int localX = x + g.getTranslation().getX();
		int localXbase = localX;
		int localY = y + g.getTranslation().getY() - getLineHeight();

		g.setColor(color);
		notNormal=false;
		
		gl.enableTexture2D(true);

		CharacterPixmap pixmap;

		boolean init = true;

		for (String text : texts)
		{
			String cutText=text;
			urlIndices.clear();

			// look for URL:
			Matcher urlMatcher = urlPattern.matcher(cutText);
			while(urlMatcher.find()){
				int start = urlMatcher.start();
				int end = cutText.substring(start).indexOf("]");
				urlIndices.add(start);
				urlIndices.add(end);
			}

			for (int i = 0; i < cutText.length(); i++)
			{
				// see if we are in the middle of a url
				for(int urlIndex=0; urlIndex<urlIndices.size(); urlIndex+=2){
					if(i>urlIndices.get(urlIndex) && i<urlIndices.get(urlIndex+1)+1){
						g.setColor(blue);
						notNormal=true;
					}
					else if(notNormal){
						g.setColor(color);
						notNormal=false;
					}
				}
				
				final char c = cutText.charAt(i);
				if (c == '\r' || c == '\f' || c == '\t')
					continue;
				else if (c == ' ')
				{
					localX += getFont().getWidth(' ');
					continue;
				}
				pixmap = font.getCharPixMap(c);

				if (init)
				{
					ITexture tex = pixmap.getTexture();

					if (tex.hasAlpha())
					{
						gl.setTexEnvModeModulate();
					}

					tex.bind();
					gl.startQuads();
					init = false;
				}

				final int imgWidth = pixmap.getWidth();
				final int imgHeight = pixmap.getHeight();

				final float endY = pixmap.getEndY();
				final float endX = pixmap.getEndX();

				final float startX = pixmap.getStartX();
				final float startY = pixmap.getStartY();

				gl.texCoord(startX, endY);
				gl.vertex(localX, localY);

				gl.texCoord(startX, startY);
				gl.vertex(localX, imgHeight + localY);

				gl.texCoord(endX, startY);
				gl.vertex(imgWidth + localX, imgHeight + localY);

				gl.texCoord(endX, endY);
				gl.vertex(imgWidth + localX, localY);

				localX += pixmap.getCharWidth();
			}
			//move to start of next line
			localY -= font.getHeight();
			localX = localXbase;
		}
		if (!init)
			gl.end();
		//	    gl.enableTexture2D(false);
	}

	public void render(int x, int y, String text, Color color, Graphics g)
	{
		if (text == null || text.length() == 0 || text.trim().length() == 0)
			return;

		//		Dimension size = this.calculateSize(text);

		IOpenGL gl = g.getOpenGL();
		int localX = x + g.getTranslation().getX();
		int localY = y + g.getTranslation().getY() - getLineHeight();

		g.setColor(color);
		gl.enableTexture2D(true);

		CharacterPixmap pixmap = null;

		boolean init = true;

		for (int i = 0; i < text.length(); i++)
		{
			final char c = text.charAt(i);
			if (c == '\r' || c == '\f' || c == '\t')
				continue;
			else if (c == ' ')
			{
				localX += getFont().getWidth(' ');
				continue;
			}
			pixmap = font.getCharPixMap(c);

			if (init)
			{
				ITexture tex = pixmap.getTexture();

				if (tex.hasAlpha())
				{
					gl.setTexEnvModeModulate();
				}

				tex.bind();
				gl.startQuads();
				init = false;
			}

			final int imgWidth = pixmap.getWidth();
			final int imgHeight = pixmap.getHeight();

			final float endY = pixmap.getEndY();
			final float endX = pixmap.getEndX();

			final float startX = pixmap.getStartX();
			final float startY = pixmap.getStartY();

			gl.texCoord(startX, endY);
			gl.vertex(localX, localY);

			gl.texCoord(startX, startY);
			gl.vertex(localX, imgHeight + localY);

			gl.texCoord(endX, startY);
			gl.vertex(imgWidth + localX, imgHeight + localY);

			gl.texCoord(endX, endY);
			gl.vertex(imgWidth + localX, localY);

			localX += pixmap.getCharWidth();
		}

		if (!init)
			gl.end();
		//	    gl.enableTexture2D(false);
	}

}
