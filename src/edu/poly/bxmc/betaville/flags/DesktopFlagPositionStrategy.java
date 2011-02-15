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
package edu.poly.bxmc.betaville.flags;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.EmptyDesign;

/**
 * @author Skye Book
 *
 */
public class DesktopFlagPositionStrategy implements IFlagPositionStrategy {
	private static Logger logger = Logger.getLogger(DesktopFlagPositionStrategy.class);
	private float currentMaxHeight=0;
	
	/**
	 * 
	 */
	public DesktopFlagPositionStrategy() {}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.flags.IFlagPositionStrategy#placeFlag(edu.poly.bxmc.betaville.model.Design, java.util.ArrayList)
	 */
	public void placeFlag(UTMCoordinate location, int baseID, ArrayList<Design> proposals) {
		SceneGameState.getInstance().addToFlagNode(MapManager.locationToBetaville(location), baseID, proposals.size());
	}
	
	public int findHeight(Design base){
		
		// an EmptyDesign won't be found in the scene so we set its height manually
		if(base instanceof EmptyDesign){
			logger.debug("Handling an EmptyDesign, setting default height");
			return 50;
		}
		
		Spatial s = SceneGameState.getInstance().getDesignNode().getChild(base.getFullIdentifier());
		logger.info("Finding height of " + s.getName());
		long start = System.currentTimeMillis();
		currentMaxHeight=0;
		tester(s);
		logger.info("height is " + currentMaxHeight + " | Took " + (System.currentTimeMillis()-start) + " ms");
		return (int) currentMaxHeight;
	}
	
	/**
	 * Finds the height of a spatial
	 * TODO convert to self-standing recursive (pass in currentMaxHeight)
	 * @param s
	 */
	private void tester(Spatial s){
		if(s instanceof TriMesh){
			float maxHeightIncoming = GeometryUtilities.findHeightOfTriMesh((TriMesh)s);
			
			if(maxHeightIncoming>currentMaxHeight){
				currentMaxHeight=maxHeightIncoming;
			}
		}
		else if(s instanceof Node){
			Iterator<Spatial> it = ((Node)s).getChildren().iterator();
			while(it.hasNext()){
				tester(it.next());
			}
		}
	}
}
