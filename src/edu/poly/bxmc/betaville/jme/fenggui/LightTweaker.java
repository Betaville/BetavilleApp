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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.composite.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowExLayout;
import org.fenggui.layout.StaticLayout;

import com.jme.light.Light;
import com.jme.scene.state.LightState;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.fenggui.extras.BlockingScrollContainer;
import edu.poly.bxmc.betaville.jme.fenggui.extras.IBetavilleWindow;
import edu.poly.bxmc.betaville.jme.fenggui.extras.RGBAContainer;
import edu.poly.bxmc.betaville.jme.fenggui.panel.IPanelOnScreenAwareWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * @author Skye Book
 *
 */
public class LightTweaker extends Window implements IBetavilleWindow, IPanelOnScreenAwareWindow{
	private static Logger logger = Logger.getLogger(LightTweaker.class);
	
	private int targetWidth = 350;
	private int targetHeight = 200;
	
	private List<RGBAContainer> ambientList;
	private List<RGBAContainer> diffuseList;
	private List<RGBAContainer> specularList;
	
	private Label ambientLabel;
	private Label diffuseLabel;
	private Label specularLabel;
	
	private ComboBox lightSelector;
	
	private BlockingScrollContainer sc;
	private Container inner;
	
	/**
	 * 
	 */
	public LightTweaker(){
		super(true, true);
		logger.info("Creating Light Tweaker");
		getContentContainer().setLayoutManager(new StaticLayout());
		
		sc = FengGUI.createWidget(BlockingScrollContainer.class);
		
		inner = FengGUI.createWidget(Container.class);
		inner.setLayoutManager(new RowExLayout(false));
		
		sc.setInnerWidget(inner);
		sc.layout();
		
		ambientList = new ArrayList<RGBAContainer>();
		diffuseList = new ArrayList<RGBAContainer>();
		specularList = new ArrayList<RGBAContainer>();
		
		ambientLabel = FengGUI.createWidget(Label.class);
		ambientLabel.setMultiline(true);
		ambientLabel.setText("\n"+Labels.get(LightTweaker.class, "ambient"));
		diffuseLabel = FengGUI.createWidget(Label.class);
		diffuseLabel.setMultiline(true);
		diffuseLabel.setText("\n"+Labels.get(LightTweaker.class, "diffuse"));
		specularLabel = FengGUI.createWidget(Label.class);
		specularLabel.setMultiline(true);
		specularLabel.setText("\n"+Labels.get(LightTweaker.class, "specular"));
		
		createBlankSlate();
	}
	
	private void createBlankSlate(){
		lightSelector = FengGUI.createWidget(ComboBox.class);
		lightSelector.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(Object sender,
					SelectionChangedEvent selectionChangedEvent) {
				try{
					logger.info("Selected: " + lightSelector.getSelectedValue());
				int lightToShow = Integer.parseInt(new String(lightSelector.getSelectedValue().substring(lightSelector.getSelectedValue().length()-1)));
				logger.info("Light " + lightToShow + " selected");
				displayLight(lightToShow);
				}catch(NumberFormatException e){
					logger.info("NumberFormatException expected on creation");
					return;
				}
			}
		});
		
		lightSelector.setSize(targetWidth-15, lightSelector.getHeight()+5);
		sc.setSize(targetWidth-10, targetHeight-getTitleBar().getHeight()-lightSelector.getHeight()-10);
		inner.setSize(sc.getSize());
		
		lightSelector.setXY(5, 0);
		sc.setXY(5, lightSelector.getHeight()+10);
		
		getContentContainer().addWidget(lightSelector, sc);
	}
	
	private void displayLight(int index){
		inner.removeAllWidgets();
		sc.layout();
		
		inner.addWidget(ambientLabel, ambientList.get(index),
				diffuseLabel, diffuseList.get(index),
				specularLabel, specularList.get(index));
		
		sc.layout();
	}
	
	private void fillLightList(LightState ls){
		getContentContainer().removeAllWidgets();
		createBlankSlate();
		
		for(int i=0; i<ls.getLightList().size(); i++){
			Light light = ls.getLightList().get(i);
			RGBAContainer ambient = new RGBAContainer();
			RGBAContainer diffuse = new RGBAContainer();
			RGBAContainer specular = new RGBAContainer();
			
			ambient.loadColor(light.getAmbient());
			diffuse.loadColor(light.getDiffuse());
			specular.loadColor(light.getSpecular());
			
			ambientList.add(ambient);
			diffuseList.add(diffuse);
			specularList.add(specular);
			
			// add to list of lights to play with
			lightSelector.addItem(light.getType().toString()+ " " + i);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.jme.fenggui.IBetavilleWindow#finishSetup()
	 */
	public void finishSetup(){
		setTitle(Labels.get(this.getClass(), "title"));
		setSize(targetWidth, targetHeight);
	}

	public void panelTurnedOn() {
		fillLightList(SceneGameState.getInstance().getLightState());
	}

}
