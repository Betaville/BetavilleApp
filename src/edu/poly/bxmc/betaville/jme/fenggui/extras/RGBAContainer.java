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

import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.Slider;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.layout.RowLayout;

import com.jme.renderer.ColorRGBA;

import edu.poly.bxmc.betaville.Labels;

/**
 * @author Skye Book
 *
 */
public class RGBAContainer extends RGBContainer {
	
	private Container a;
	private Label aL;
	private Slider aS;

	/**
	 * 
	 */
	public RGBAContainer() {
		super();
		
		a = FengGUI.createWidget(Container.class);
		a.setLayoutManager(new RowLayout(true));
		
		aL = FengGUI.createWidget(Label.class);
		aL.setWidth(labelSize);
		
		aS = FengGUI.createSlider(true);
		aS.addSliderMovedListener(new ISliderMovedListener() {
			
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				aL.setText(Labels.get(RGBAContainer.class.getSimpleName()+".alpha")+": " + (int)(aS.getValue()*255));
				color.r=(float) aS.getValue();
				apply();
			}
		});
		a.addWidget(aL, aS);
		super.addWidget(a);
	}
	
	public void loadColor(ColorRGBA color){
		super.loadColor(color);
		aS.setValue(color.a);
	}

}
