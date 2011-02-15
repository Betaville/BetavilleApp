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
package edu.poly.bxmc.betaville.jme.fenggui;

import java.io.IOException;

import org.fenggui.Button;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.ITexture;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.decorator.background.PixmapBackground;

/**
 * @author Skye Book
 *
 */
public class FixedButton extends Button {
	private static final long serialVersionUID = 1L;
	
	private static boolean texturesLoaded=false;
	private static ITexture normal=null;
	private static ITexture hover=null;
	private static ITexture down=null;
	private static ITexture inactive=null;
	
	PixmapBackground off;
	PixmapBackground over;
	PixmapBackground pressed;
	PixmapBackground unselectable;


	
	public FixedButton(){
		if(!texturesLoaded){
			try {
				initTextures();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Pixmap normalTopLeft = new Pixmap(normal, 0,0,5,5);
		Pixmap normalTopCenter = new Pixmap(normal, 5,0,48,5);
		Pixmap normalTopRight = new Pixmap(normal, 53,0,5,5);
		Pixmap normalMidLeft = new Pixmap(normal, 0,5,5,14);
		Pixmap normalMidCenter = new Pixmap(normal, 5,5,48,16);
		Pixmap normalMidRight = new Pixmap(normal, 53,5,5,14);
		Pixmap normalBottomLeft = new Pixmap(normal, 0,19,5,5);
		Pixmap normalBottomCenter = new Pixmap(normal, 5,19,48,5);
		Pixmap normalBottomRight = new Pixmap(normal, 53,19,5,5);
		
		off =  new PixmapBackground(
				normalMidCenter, normalTopLeft,
				normalTopCenter, normalTopRight, 
				normalMidRight, normalBottomRight, 
				normalBottomCenter, normalBottomLeft, 
				normalMidLeft, true);
		
		Pixmap hoverTopLeft = new Pixmap(hover, 0,0,5,5);
		Pixmap hoverTopCenter = new Pixmap(hover, 5,0,48,5);
		Pixmap hoverTopRight = new Pixmap(hover, 53,0,5,5);
		Pixmap hoverMidLeft = new Pixmap(hover, 0,5,5,14);
		Pixmap hoverMidCenter = new Pixmap(hover, 5,5,48,16);
		Pixmap hoverMidRight = new Pixmap(hover, 53,5,5,14);
		Pixmap hoverBottomLeft = new Pixmap(hover, 0,19,5,5);
		Pixmap hoverBottomCenter = new Pixmap(hover, 5,19,48,5);
		Pixmap hoverBottomRight = new Pixmap(hover, 53,19,5,5);
		
		over =  new PixmapBackground(
				hoverMidCenter, hoverTopLeft,
				hoverTopCenter, hoverTopRight, 
				hoverMidRight, hoverBottomRight, 
				hoverBottomCenter, hoverBottomLeft, 
				hoverMidLeft, true);
		
		Pixmap downTopLeft = new Pixmap(down, 0,0,5,5);
		Pixmap downTopCenter = new Pixmap(down, 5,0,48,5);
		Pixmap downTopRight = new Pixmap(down, 53,0,5,5);
		Pixmap downMidLeft = new Pixmap(down, 0,5,5,14);
		Pixmap downMidCenter = new Pixmap(down, 5,5,48,16);
		Pixmap downMidRight = new Pixmap(down, 53,5,5,14);
		Pixmap downBottomLeft = new Pixmap(down, 0,19,5,5);
		Pixmap downBottomCenter = new Pixmap(down, 5,19,48,5);
		Pixmap downBottomRight = new Pixmap(down, 53,19,5,5);
		
		pressed =  new PixmapBackground(
				downMidCenter, downTopLeft,
				downTopCenter, downTopRight, 
				downMidRight, downBottomRight, 
				downBottomCenter, downBottomLeft, 
				downMidLeft, true);
		
		Pixmap inactiveTopLeft = new Pixmap(inactive, 0,0,5,5);
		Pixmap inactiveTopCenter = new Pixmap(inactive, 5,0,48,5);
		Pixmap inactiveTopRight = new Pixmap(inactive, 53,0,5,5);
		Pixmap inactiveMidLeft = new Pixmap(inactive, 0,5,5,14);
		Pixmap inactiveMidCenter = new Pixmap(inactive, 5,5,48,16);
		Pixmap inactiveMidRight = new Pixmap(inactive, 53,5,5,14);
		Pixmap inactiveBottomLeft = new Pixmap(inactive, 0,19,5,5);
		Pixmap inactiveBottomCenter = new Pixmap(inactive, 5,19,48,5);
		Pixmap inactiveBottomRight = new Pixmap(inactive, 53,19,5,5);
		
		unselectable =  new PixmapBackground(
				inactiveMidCenter, inactiveTopLeft,
				inactiveTopCenter, inactiveTopRight, 
				inactiveMidRight, inactiveBottomRight, 
				inactiveBottomCenter, inactiveBottomLeft, 
				inactiveMidLeft, true);

		
		off.setScaled(true);
		over.setScaled(true);
		pressed.setScaled(true);
		unselectable.setScaled(true);
		
		// finish setup
		applyGreenMaps();
		getStateManager().activate(STATE_DEFAULT);
	}
	
	public void applyGreenMaps(){
		getAppearance().add(Button.STATE_DEFAULT.toString(), off);
		getAppearance().add(Button.STATE_HOVERED.toString(), over);
		getAppearance().add(Button.STATE_PRESSED.toString(), pressed);
		getAppearance().add(Button.STATE_DISABLED.toString(), unselectable);
	}
	
	public void removeGreen(){
		getAppearance().setEnabled(Button.STATE_DEFAULT.toString(), false);
		getAppearance().setEnabled(Button.STATE_HOVERED.toString(), false);
		getAppearance().setEnabled(Button.STATE_PRESSED.toString(), false);
		getAppearance().setEnabled(Button.STATE_DISABLED.toString(), false);
	}
	
	public void replaceGreen(){
		getAppearance().setEnabled(Button.STATE_DEFAULT.toString(), true);
		getAppearance().setEnabled(Button.STATE_HOVERED.toString(), true);
		getAppearance().setEnabled(Button.STATE_PRESSED.toString(), true);
		getAppearance().setEnabled(Button.STATE_DISABLED.toString(), true);
	}
	
	private static synchronized void initTextures() throws IOException{
		normal = Binding.getInstance().getTexture("data/themes/default/components/button.green.normal.png");
		hover = Binding.getInstance().getTexture("data/themes/default/components/button.green.hover.png");
		down = Binding.getInstance().getTexture("data/themes/default/components/button.green.pressed.png");
		inactive = Binding.getInstance().getTexture("data/themes/default/components/button.normal.png");
		texturesLoaded=true;
	}
}
