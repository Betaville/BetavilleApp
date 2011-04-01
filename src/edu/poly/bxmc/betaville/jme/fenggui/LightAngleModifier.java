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
package edu.poly.bxmc.betaville.jme.fenggui;

import org.apache.log4j.Logger;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Item;
import org.fenggui.Label;
import org.fenggui.Slider;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.layout.RowExLayout;

import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.math.Vector3f;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.fenggui.extras.SavableBetavilleWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * Tweaks the angle of the lights in the scene
 * @author Skye Book
 *
 */
public class LightAngleModifier extends SavableBetavilleWindow {
	private static final Logger logger = Logger.getLogger(LightAngleModifier.class);
	
	private int targetWidth=300;
	private int targetHeight=150;
	
	private Container sliderContainer;
	private Slider x;
	private Slider y;
	private Slider z;
	
	private Label xLabel;
	private String xDefaultLabel = "X: ";
	
	private Label yLabel;
	private String yDefaultLabel = "Y: ";
	
	private Label zLabel;
	private String zDefaultLabel = "Z: ";
	
	private ComboBox selector;

	/**
	 * 
	 */
	public LightAngleModifier() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
		createSelector();
		createSliders();
		getContentContainer().addWidget(selector, sliderContainer);
	}

	private void createSelector(){
		selector = FengGUI.createWidget(ComboBox.class);

		for(int i=0; i<SceneGameState.getInstance().getLightState().getLightList().size(); i++){
			selector.addItem(new LightItem(i, SceneGameState.getInstance().getLightState().getLightList().get(i)));
		}
		
		selector.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(Object arg0, SelectionChangedEvent arg1) {
				//TODO: update sliders
			}
		});
	}
	
	private void createSliders(){
		
		ISliderMovedListener listener = new ISliderMovedListener() {
			
			public void sliderMoved(SliderMovedEvent arg0) {
				updateLight();
			}
		};
		
		x = FengGUI.createSlider(true);
		x.addSliderMovedListener(listener);
		y = FengGUI.createSlider(true);
		y.addSliderMovedListener(listener);
		z = FengGUI.createSlider(true);
		z.addSliderMovedListener(listener);
		
		xLabel = FengGUI.createWidget(Label.class);
		xLabel.setText(xDefaultLabel);
		yLabel = FengGUI.createWidget(Label.class);
		yLabel.setText(yDefaultLabel);
		zLabel = FengGUI.createWidget(Label.class);
		zLabel.setText(zDefaultLabel);
		
		sliderContainer = FengGUI.createWidget(Container.class);
		sliderContainer.setLayoutManager(new RowExLayout(false));
		sliderContainer.addWidget(xLabel, x, yLabel, y, zLabel, z);
	}
	
	private void updateLight(){
		Light l = ((LightItem)selector.getSelectedItem()).itemLight;
		if(l!=null){
			if(l instanceof DirectionalLight){
				((DirectionalLight)l).setDirection(new Vector3f(valueFromSlider(x), valueFromSlider(y), valueFromSlider(z)));
			}
		}
	}
	
	private float valueFromSlider(Slider s){
		if(s.getValue()>.5d){
			return ((float)s.getValue()*2f)-1f;
		}
		else{
			return ((float)s.getValue()*-2f)-1f;
		}
	}
	
	private class LightItem extends Item{
		private Light itemLight;
		private int index;
		private LightItem(int index, Light l){
			logger.info("Adding Light " + index + " to selection list");
			itemLight=l;
			this.index=index;
		}
		
		public String toString(){
			return index+": "+itemLight.getClass().getSimpleName();
		}
		
		public String getText(){
			return toString();
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle("Light Angle Modifier");
		setSize(targetWidth, targetHeight);
	}

}
