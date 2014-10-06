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
import org.fenggui.Button;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.Slider;
import org.fenggui.composite.Window;
import org.fenggui.decorator.background.PlainBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISliderMovedListener;
import org.fenggui.event.SliderMovedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.RowExLayoutData;
import org.fenggui.util.Color;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.system.DisplaySystem;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * @author Skye Book
 *
 */
public class CreatePrimitiveWindow extends Window implements IBetavilleWindow {
	private static final Logger logger = Logger.getLogger(CreatePrimitiveWindow.class);
	
	private int targetWidth = 450;
	private int targetHeight = 200;
	
	private ComboBox shapeSelector;
	
	private Label sl;
	private int sizeMultiplier = 5;
	private Slider sizeSlider;
	private Label al;
	private int altitudeMultiplier = 100;
	private Slider altitudeSlider;
	
	private Label rl;
	private Label gl;
	private Label bl;
	private Slider r;
	private Slider g;
	private Slider b;
	
	private Container colorDemo;
	private Button done;
	
	private boolean isReady=false;
	
	private ISliderMovedListener updateColorListener;

	/**
	 * 
	 */
	public CreatePrimitiveWindow() {
		super(true, true);
		getContentContainer().setLayoutManager(new RowExLayout(false));
	}
	
	private void setup(){
		shapeSelector = FengGUI.createWidget(ComboBox.class);
		shapeSelector.addItem("Box");
		shapeSelector.addItem("Sphere");
		shapeSelector.addItem("Pyramid");
		shapeSelector.setLayoutData(new RowExLayoutData(true, true));
		
		Container sizeContainer = FengGUI.createWidget(Container.class);
		sizeContainer.setLayoutManager(new BorderLayout());
		sizeContainer.setLayoutData(new RowExLayoutData(true, true));
		
		Container altitudeContainer = FengGUI.createWidget(Container.class);
		altitudeContainer.setLayoutManager(new BorderLayout());
		altitudeContainer.setLayoutData(new RowExLayoutData(true, true));
		
		sl = FengGUI.createWidget(Label.class);
		sl.setText(Labels.get(this.getClass().getSimpleName()+".size"));
		sl.setLayoutData(BorderLayoutData.NORTH);
		
		al = FengGUI.createWidget(Label.class);
		al.setText(Labels.get(this.getClass().getSimpleName()+".altitude"));
		al.setLayoutData(BorderLayoutData.NORTH);
		
		sizeSlider = FengGUI.createSlider(true);
		sizeSlider.setLayoutData(BorderLayoutData.SOUTH);
		sizeSlider.setValue(.5d);
		altitudeSlider = FengGUI.createSlider(true);
		altitudeSlider.setLayoutData(BorderLayoutData.SOUTH);
		altitudeSlider.setValue(.5d);
		
		sizeSlider.addSliderMovedListener(new ISliderMovedListener() {
			
			public void sliderMoved(SliderMovedEvent arg0) {
				sl.setText(Labels.get(this.getClass().getSimpleName()+".size")+": " + ((float)sizeSlider.getValue()*sizeMultiplier));
			}
		});
		
		altitudeSlider.addSliderMovedListener(new ISliderMovedListener() {
			
			public void sliderMoved(SliderMovedEvent arg0) {
				al.setText(Labels.get(this.getClass().getSimpleName()+".altitude")+": " + ((float)altitudeSlider.getValue()*altitudeMultiplier));
			}
		});
		
		altitudeContainer.addWidget(al, altitudeSlider);
		sizeContainer.addWidget(sl, sizeSlider);
		
		Container rc = FengGUI.createWidget(Container.class);
		rc.setLayoutManager(new BorderLayout());
		rc.setLayoutData(new RowExLayoutData(true, true));
		Container bc = FengGUI.createWidget(Container.class);
		bc.setLayoutManager(new BorderLayout());
		bc.setLayoutData(new RowExLayoutData(true, true));
		Container gc = FengGUI.createWidget(Container.class);
		gc.setLayoutManager(new BorderLayout());
		gc.setLayoutData(new RowExLayoutData(true, true));
		
		rl = FengGUI.createWidget(Label.class);
		rl.setText(Labels.get("Generic.red"));
		rl.setLayoutData(BorderLayoutData.NORTH);
		gl = FengGUI.createWidget(Label.class);
		gl.setText(Labels.get("Generic.green"));
		gl.setLayoutData(BorderLayoutData.NORTH);
		bl = FengGUI.createWidget(Label.class);
		bl.setText(Labels.get("Generic.blue"));
		bl.setLayoutData(BorderLayoutData.NORTH);
		
		rl.setWidth(gl.getWidth());
		bl.setWidth(gl.getWidth());
		
		r = FengGUI.createSlider(true);
		r.setLayoutData(BorderLayoutData.SOUTH);
		r.setValue(.5d);
		g = FengGUI.createSlider(true);
		g.setLayoutData(BorderLayoutData.SOUTH);
		g.setValue(.5d);
		b = FengGUI.createSlider(true);
		b.setLayoutData(BorderLayoutData.SOUTH);
		b.setValue(.5d);
		
		rc.addWidget(rl, r);
		gc.addWidget(gl, g);
		bc.addWidget(bl, b);
		
		updateColorListener = new ISliderMovedListener() {
			public void sliderMoved(SliderMovedEvent arg0) {
				rl.setText(Labels.get("Generic.red")+": " + (float)r.getValue());
				gl.setText(Labels.get("Generic.green")+": " + (float)g.getValue());
				bl.setText(Labels.get("Generic.blue")+": " + (float)b.getValue());
				updateColor();
			}
		};
		
		r.addSliderMovedListener(updateColorListener);
		g.addSliderMovedListener(updateColorListener);
		b.addSliderMovedListener(updateColorListener);
		
		colorDemo = FengGUI.createWidget(Container.class);
		Label filler = FengGUI.createWidget(Label.class);
		filler.setText(" ");
		colorDemo.addWidget(filler);
		colorDemo.setLayoutData(new RowExLayoutData(true, true));
		
		updateColor();
		
		done = FengGUI.createWidget(Button.class);
		done.setText(Labels.get("Generic.ok"));
		done.setLayoutData(new RowExLayoutData(true, true));
		done.addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				isReady=true;
			}
		});
		
		Container bottom = FengGUI.createWidget(Container.class);
		bottom.setLayoutManager(new RowExLayout(true));
		bottom.addWidget(colorDemo, done);
		
		getContentContainer().addWidget(shapeSelector);
		getContentContainer().addWidget(sizeContainer, altitudeContainer);
		getContentContainer().addWidget(rc, gc, bc);
		getContentContainer().addWidget(bottom);
	}
	
	private void updateColor(){
		colorDemo.getAppearance().removeAll();
		colorDemo.getAppearance().add(new PlainBackground(new Color((float)r.getValue(), (float)g.getValue(), (float)b.getValue(), 1f)));
	}
	
	public TriMesh generateShape(){
		TriMesh geometry;
		float size = Scale.fromMeter((float)sizeSlider.getValue()*sizeMultiplier);
		if(shapeSelector.getSelectedValue().equals("Box")){
			geometry = new Box("Generated Box", new Vector3f(), size, size, size);
		}
		else if(shapeSelector.getSelectedValue().equals("Sphere")){
			geometry = new Sphere("Generated Sphere", new Vector3f(), 25, 25, size);
		}
		else{
			geometry = new Pyramid("Generated Pyramid", size, size);
		}
		
		MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		ms.setAmbient(generateJMEColor());
		ms.setDiffuse(generateJMEColor());
		ms.setMaterialFace(MaterialFace.FrontAndBack);
		
		geometry.setRenderState(ms);
		geometry.updateRenderState();
		return geometry;
	}
	
	private ColorRGBA generateJMEColor(){
		return new ColorRGBA((float)r.getValue(), (float)g.getValue(), (float)b.getValue(), 1f);
	}
	
	public float getAltitude(){
		return (float)altitudeSlider.getValue()*altitudeMultiplier;
	}
	
	public boolean isReady(){
		return isReady;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup() {
		setTitle(Labels.get(this.getClass().getSimpleName()+".title"));
		setSize(targetWidth, targetHeight);
		setup();
	}

	public void reset() {
		isReady=false;
	}
	
}
