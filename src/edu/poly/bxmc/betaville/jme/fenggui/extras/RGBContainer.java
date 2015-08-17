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
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowLayout;

import com.jme.renderer.ColorRGBA;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.fenggui.listeners.IColorChangeApplicator;

/**
 * @author Skye Book
 *
 */
public class RGBContainer extends Container {
	
	private Container r;
	private Container g;
	private Container b;
	
	protected ColorRGBA color;
	
	private Label rL;
	private Label gL;
	private Label bL;
	
	private Slider rS;
	private Slider gS;
	private Slider bS;
	
	private IColorChangeApplicator applicator;
	
	protected int labelSize=75;
	
	/**
	 * 
	 */
	public RGBContainer(){
		super(new RowExLayout(false));
		
		color = new ColorRGBA(0,0,0,1);
		
		r = FengGUI.createWidget(Container.class);
		r.setLayoutManager(new RowLayout(true));
		
		rL = FengGUI.createWidget(Label.class);
		rL.setWidth(labelSize);
		
		rS = FengGUI.createSlider(true);
		rS.addSliderMovedListener(new ISliderMovedListener() {
			
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				rL.setText(Labels.get(RGBContainer.class.getSimpleName()+".red")+": " + (int)(rS.getValue()*255));
				color.r=(float) rS.getValue();
				apply();
			}
		});
		r.addWidget(rL, rS);
		
		g = FengGUI.createWidget(Container.class);
		g.setLayoutManager(new RowLayout(true));
		
		gL = FengGUI.createWidget(Label.class);
		gL.setWidth(labelSize);
		
		gS = FengGUI.createSlider(true);
		gS.addSliderMovedListener(new ISliderMovedListener() {
			
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				gL.setText(Labels.get(RGBContainer.class.getSimpleName()+".green")+": " + (int)(gS.getValue()*255));
				color.g=(float) gS.getValue();
				apply();
			}
		});
		g.addWidget(gL, gS);
		
		b = FengGUI.createWidget(Container.class);
		b.setLayoutManager(new RowLayout(true));
		
		bL = FengGUI.createWidget(Label.class);
		bL.setWidth(labelSize);
		
		bS = FengGUI.createSlider(true);
		bS.addSliderMovedListener(new ISliderMovedListener() {
			
			public void sliderMoved(SliderMovedEvent sliderMovedEvent) {
				bL.setText(Labels.get(RGBContainer.class.getSimpleName()+".blue")+": " + (int)(bS.getValue()*255));
				color.b=(float) bS.getValue();
				apply();
			}
		});
		b.addWidget(bL, bS);
		
		addWidget(r,g,b);
	}
	
	public void loadColor(ColorRGBA color){
		this.color=color;
		rS.setValue(color.r);
		gS.setValue(color.g);
		bS.setValue(color.b);
	}
	
	protected void apply(){
		if(applicator!=null){
			applicator.applyColor(color);
		}
	}
	
	public void setApplicator(IColorChangeApplicator applicator){
		this.applicator=applicator;
		
	}
}
