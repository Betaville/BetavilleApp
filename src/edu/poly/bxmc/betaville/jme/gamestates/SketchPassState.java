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

import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

import edu.poly.bxmc.betaville.jme.pass.SketchOverRenderPass;

public class SketchPassState extends GameState
{
    private Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
 
    private SketchOverRenderPass sketchPass = null;
    private BasicPassManager pManager = null;

    private Camera cam; 
 
    public SketchPassState(String name, Camera camera)
    {
        setName(name);
       
        //Setup camera
        this.cam = camera;
        cam = renderer.getCamera();
        cam.setFrustumPerspective(55.0f, (float) DisplaySystem.getDisplaySystem().getWidth() / (float) DisplaySystem.getDisplaySystem().getHeight(), 1, 3000);
        
        //setup pass
        sketchPass = new SketchOverRenderPass(cam, 2);
        pManager = new BasicPassManager();
             
        Node rootNode = SceneGameState.getInstance().getRootNode();
        sketchPass.add(rootNode);
        sketchPass.setEnabled(true);
        
        RenderPass rPass = new RenderPass();
        rPass.add(SceneGameState.getInstance().getRootNode());
        
        pManager.add(rPass);
        pManager.add(sketchPass);
       
    }
 
    @Override
    public void render(float tpf)
    {
        renderer.clearBuffers();
        pManager.renderPasses(renderer);
        
    }
 
    @Override
    public void update(float tpf)
    {
        pManager.updatePasses(tpf);
    }
 
    @Override
    public void cleanup()
    {
    }

	public SketchOverRenderPass getSketchPass() {
		return sketchPass;
	}
	
	public static SketchPassState getInstance(){
		return (SketchPassState)GameStateManager.getInstance().getChild("sketchPassState");
	}
}