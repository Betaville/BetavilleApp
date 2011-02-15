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
package edu.poly.bxmc.betaville.jme.gamestates;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JFrame;

import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Spatial;
import com.jme.scene.state.CullState;
import com.jme.scene.state.CullState.Face;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jmex.editors.swing.pass.WaterPassEditor;
import com.jmex.effects.water.ProjectedGrid;
import com.jmex.effects.water.WaterHeightGenerator;
import com.jmex.effects.water.WaterRenderPass;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameStateManager;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * @author Skye Book
 *
 */
public class WaterGameState extends BasicGameState {
	private WaterRenderPass waterEffectRenderPass;
	private BasicPassManager passManager;
	private ProjectedGrid projectedGrid;
	private CullState cullState;
	private JFrame waterEditorWindow;
	private WaterPassEditor waterEditor;
	

	/**
	 * @param name
	 */
	public WaterGameState(String name) {
		super(name);
		
		passManager = new BasicPassManager();
		cullState = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
		cullState.setCullFace(Face.Back);
		
		// create water
		createWater();
		
	}

	public void createWater() {
		
		Future<WaterRenderPass> future = GameTaskQueueManager.getManager().update(new Callable<WaterRenderPass>() {
		    public WaterRenderPass call() throws Exception {
		    	/* Do not change the last two boolean parameters for the WaterRenderPass constructor,
		    	 * as they will cause the FengGUI icon set to be rendered on top of the water pass
		    	 * when using a FengGUI theme
		    	 */
				waterEffectRenderPass = new WaterRenderPass(DisplaySystem.getDisplaySystem().getRenderer().getCamera(), 4, false, false);
		        return waterEffectRenderPass;
		    }
		});
		try {
			if(waterEffectRenderPass==null)
			waterEffectRenderPass=future.get();
			
			// set equations to use y axis as up
	        waterEffectRenderPass.setWaterPlane(new Plane(new Vector3f(0.0f, 1.0f,
	                0.0f), 0.0f));

	        waterEffectRenderPass.setWaterColorEnd(new ColorRGBA(.25f, .75f, .5f, 1f));
	        waterEffectRenderPass.setWaterColorStart(new ColorRGBA(.25f, .5f, 1, 1f));
	        waterEffectRenderPass.setClipBias(0.5f);
	        waterEffectRenderPass.setWaterMaxAmplitude(Scale.fromMeter(.1f));
	        waterEffectRenderPass.setWaterHeight(Scale.fromMeter(-5));
	        
	        //waterEffectRenderPass.addReflectedScene(sceneGameState.getDesignNode());
	        waterEffectRenderPass.addReflectedScene(SceneGameState.getInstance().getSkybox());
	        
	        WaterHeightGenerator smallWater = new WaterHeightGenerator();
	        smallWater.setHeightbig(Scale.fromMeter(1));
	        smallWater.setHeightsmall(0);
	        projectedGrid = new ProjectedGrid("projectedGrid", DisplaySystem.getDisplaySystem().getRenderer().getCamera(), (int)Scale.fromMeter(200), (int)Scale.fromMeter(200), 1, smallWater);
	        //projectedGrid = new ProjectedGrid("projectedGrid", DisplaySystem.getDisplaySystem().getRenderer().getCamera(), 50, 50, 1, smallWater);
	        projectedGrid.setLocalTranslation(SceneGameState.getInstance().getCamera().getLocation());
	        waterEffectRenderPass.setWaterEffectOnSpatial(projectedGrid);
	        rootNode.attachChild(projectedGrid);
	        passManager.add(waterEffectRenderPass);

	        RenderPass rootPass = new RenderPass();
	        rootPass.add(rootNode);
	        passManager.add(rootPass);

	        if(SettingsPreferences.isWaterOn()){
	        	waterEditor = new WaterPassEditor(waterEffectRenderPass);
	        	waterEditorWindow = new JFrame("Water Pass Editor");
	        	waterEditorWindow.add(waterEditor);
	        	waterEditorWindow.setSize(350, 450);
	        	waterEditorWindow.setVisible(true);
	        }
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        rootNode.setCullHint(Spatial.CullHint.Never);
        rootNode.setRenderState(cullState);
        rootNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        rootNode.updateRenderState();
	}

	public void update(float tpf){
		super.update(tpf);
        passManager.renderPasses(DisplaySystem.getDisplaySystem().getRenderer());
		passManager.updatePasses(tpf);
	}

	public WaterRenderPass getWaterEffectRenderPass() {
		return waterEffectRenderPass;
	}
	
	public static WaterGameState getInstance(){
		return (WaterGameState)GameStateManager.getInstance().getChild("waterGameState");
	}
}
