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
package edu.poly.bxmc.betaville.jme.fenggui.tutorial;

import java.io.IOException;
import java.net.URL;

import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.binding.render.Binding;
import org.fenggui.binding.render.Pixmap;
import org.fenggui.layout.RowExLayout;

/**
 * @author Skye Book
 *
 */
public class ImageSlide extends Slide{
	
	private Label title;
	private Label picture;
	private Label caption;
	
	public ImageSlide(){}

	/**
	 * @throws IOException 
	 * 
	 */
	public ImageSlide(int slideWidth, int slideHeight, URL image, String title, String caption) throws IOException{
		super(slideWidth, slideHeight);
		setLayoutManager(new RowExLayout(false, 10));
		picture = FengGUI.createWidget(Label.class);
		picture.setPixmap(new Pixmap(Binding.getInstance().getTexture(image)));
		
		// make large black text here
		this.title = FengGUI.createWidget(Label.class);
		this.title.setText(title);
		
		// make medium white text here
		this.caption = FengGUI.createWidget(Label.class);
		this.caption.setText(caption);
		
		addWidget(this.title, this.caption, this.picture);
	}

}
