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
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import org.apache.log4j.Logger;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;

import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState.StateType;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.intersections.ISpatialSelectionListener;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;

/**
 * @author Skye Book
 *
 */
public class WireframePanelAction extends PanelAction {
	private static Logger logger = Logger.getLogger(WireframePanelAction.class);

	private IButtonPressedListener listener;

	private boolean wireOn;



	/**
	 * 
	 */
	public WireframePanelAction() {
		super("Wireframe", "Makes things wireframe!", "Apply Wireframe", AvailabilityRule.OBJECT_SELECTED, UserType.MEMBER, null);

		SceneScape.addSelectionListener(new ISpatialSelectionListener() {

			public void selectionCleared(Design previousDesign) {}

			public void designSelected(Spatial spatial, Design design) {
				if(GeometryUtilities.checkForRenderState(spatial, StateType.Wireframe)){
					button.setText("Turn Off Wireframe");
					wireOn=true;
				}
				else{
					button.setText("Apply Wireframe");
					wireOn=false;
				}

			}
		});

		listener = new IButtonPressedListener(){
			public void buttonPressed(Object source, ButtonPressedEvent e) {
				if(wireOn){
					GeometryUtilities.stripWireframe(SceneScape.getTargetSpatial());
					wireOn=false;
					button.setText("Apply Wireframe");
				}
				else{
					GeometryUtilities.applyWireframe(SceneScape.getTargetSpatial());
					wireOn=true;
					button.setText("Turn Off Wireframe");
				}
			}};

			button.addButtonPressedListener(listener);
	}

}
