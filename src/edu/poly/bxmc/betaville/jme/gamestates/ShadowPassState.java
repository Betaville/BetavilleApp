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
package edu.poly.bxmc.betaville.jme.gamestates;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.RenderPass;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.LoadingGameState;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.pass.ConfigurableDirectionalShadowMapPass;

/**
 * <code>ShadowPassState</code> is responsible for rendering the different passes in the scene.
 * @author Skye Book
 *
 */
public class ShadowPassState extends GameState {
	private static final Logger logger = Logger.getLogger(ShadowPassState.class);
	
	private Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
	private BasicPassManager passManager = null;

	private ShadowedRenderPass shadowPass = null;
	private ConfigurableDirectionalShadowMapPass mapPass = null;
	private RenderPass groundSelectPass = null;
	private RenderPass flagPass = null;
	
	private SceneGameState sceneGameState;
	
	private boolean shadowMapCameraInitialized=false;

	public ShadowPassState(String name) {
		setName(name);
		passManager = new BasicPassManager();

		// The shadow pass renders the shadow volumes for the scene.
		((LoadingGameState)GameStateManager.getInstance().getChild("transitionGameState")).setProgress(0.82f, "Adding Occluders");

		Future<ShadowedRenderPass> future = GameTaskQueueManager.getManager().update(new Callable<ShadowedRenderPass>() {
			public ShadowedRenderPass call() throws Exception {
				shadowPass = new ShadowedRenderPass();
				mapPass = new ConfigurableDirectionalShadowMapPass(new Vector3f(.25f, -.85f, .75f), 4096);
				//mapPass = new DirectionalShadowMapPass(new Vector3f(-1, -2, -1));
				return shadowPass;
			}
		});
		try {
			if(shadowPass==null){
				shadowPass = future.get();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		/*
		shadowPass.addOccluder(SceneGameState.getInstance().getDesignNode());
		shadowPass.add(SceneGameState.getInstance().getTerrainNode());
		shadowPass.add(SceneGameState.getInstance().getDesignNode());
		shadowPass.setShadowColor(new ColorRGBA(.1f,.15f,.35f,1f));
		shadowPass.setLightingMethod(ShadowedRenderPass.LightingMethod.Modulative);
		//passManager.add(shadowPass);
		//shadowPass.setRenderShadows(SettingsPreferences.isShadowsOn());
		shadowPass.setRenderShadows(false);
		*/
		
		
		RenderPass scenePass = new RenderPass();
        scenePass.add(SceneGameState.getInstance().getTerrainNode());
        scenePass.add(SceneGameState.getInstance().getDesignNode());
        passManager.add(scenePass);
		
		//mapPass = new DirectionalShadowMapPass(new Vector3f(-1, -2, -1));
        //mapPass.setViewDistance(Scale.fromMeter(500));
        //mapPass.setViewDistance(Scale.fromMeter(50000));
        
        
        
        mapPass.setViewDistance(10);
        mapPass.addOccluder(SceneGameState.getInstance().getDesignNode());
        //mapPass.setShadowCameraFarPlane(mapPass.getViewDistance()*6f);
        mapPass.setShadowCameraNearPlane(SceneGameState.NEAR_FRUSTUM);
        mapPass.add(SceneGameState.getInstance().getTerrainNode());
        mapPass.add(SceneGameState.getInstance().getDesignNode());
        mapPass.setEnabled(SettingsPreferences.isShadowsOn());
        mapPass.setShadowMapScale(0.005f);
        passManager.add(mapPass);
        
        
        
		// The ground select render pass renders the little red square
		// that appears when you click on a ground location
		groundSelectPass = new RenderPass();
		groundSelectPass.add(SceneGameState.getInstance().getGroundBoxNode());
		passManager.add(groundSelectPass);
		
		// The flag pass renders the upside-down pyramid flags that indicateproposals
		flagPass = new RenderPass();
		flagPass.add(SceneGameState.getInstance().getFlagNode());
		flagPass.add(SceneGameState.getInstance().getSearchNode());
		passManager.add(flagPass);
		
		sceneGameState = SceneGameState.getInstance();
	}

	/* (non-Javadoc)
	 * @see com.jmex.game.state.GameState#cleanup()
	 */
	@Override
	public void cleanup() {}

	/* (non-Javadoc)
	 * @see com.jmex.game.state.GameState#render(float)
	 */
	@Override
	public void render(float tpf) {
		passManager.renderPasses(renderer);
	}

	/* (non-Javadoc)
	 * @see com.jmex.game.state.GameState#update(float)
	 */
	@Override
	public void update(float tpf) {
		passManager.updatePasses(tpf);
	}
	
	public void updateMapTarget(){
		mapPass.setViewTarget(sceneGameState.getCamera().getLocation());
	}

	public ShadowedRenderPass getShadowPass() {
		return shadowPass;
	}
	
	public void toggleMapPass(){
		mapPass.setEnabled(!mapPass.isEnabled());
	}
	
	public ConfigurableDirectionalShadowMapPass getMapPass(){
		return mapPass;
	}
	
	public static ShadowPassState getInstance(){
		return (ShadowPassState)GameStateManager.getInstance().getChild("shadowPassState");
	}
}
