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
package edu.poly.bxmc.betaville.proposals;

import java.io.IOException;
import java.net.URISyntaxException;

import com.jme.scene.Node;

import edu.poly.bxmc.betaville.CacheManager;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.ModelLoader;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.ModeledDesign;

/**
 * @author Skye Book
 *
 */
public class JMEDesktopProposalController implements SceneProposalController {

	/**
	 * 
	 */
	public JMEDesktopProposalController() {}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.proposals.SceneProposalController#addDesignToScene(edu.poly.bxmc.betaville.model.Design)
	 */
	public boolean addDesignToScene(Design versionToAdd) {
		
		if(versionToAdd instanceof ModeledDesign){
			boolean fileResponse = CacheManager.getCacheManager().requestFile(versionToAdd.getID(), versionToAdd.getFilepath());

			if(fileResponse){
				ModelLoader loader = null;
				try {
					loader = new ModelLoader((ModeledDesign)versionToAdd, true, null);
					final Node dNode = loader.getModel();
					dNode.setName(versionToAdd.getFullIdentifier());
					dNode.setLocalTranslation(JME2MapManager.instance.locationToBetaville(versionToAdd.getCoordinate()));
					dNode.setLocalRotation(Rotator.fromThreeAngles(((ModeledDesign)versionToAdd).getRotationX(),
							((ModeledDesign)versionToAdd).getRotationY(), ((ModeledDesign)versionToAdd).getRotationZ()));

					SceneGameState.getInstance().getDesignNode().attachChild(dNode);
					SceneGameState.getInstance().getDesignNode().updateRenderState();
					return true;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.proposals.SceneProposalController#removeDesignFromScene(Design)
	 */
	public void removeDesignFromScene(Design versionToRemove) {
		SceneGameState.getInstance().getDesignNode().detachChildNamed(versionToRemove.getFullIdentifier());
	}

}
