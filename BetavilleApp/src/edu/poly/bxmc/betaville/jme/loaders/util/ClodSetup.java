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
package edu.poly.bxmc.betaville.jme.loaders.util;

import com.jme.bounding.BoundingBox;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.RenderState.StateType;

import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * Utility functions aimed at assisting in the
 * construction of CLOD meshes.
 * 
 * @author Skye Book
 *
 */
public class ClodSetup {
	
	public static Node setupClod(Node n){
		Node node = new Node(n.getName());
		setupClodImpl(node, n);
		return node;
	}
	
	public static void setupClodImpl(Node top, Spatial s){
		if(s instanceof TriMesh){
			AreaClodMesh acm = new AreaClodMesh(s.getName(), (TriMesh) s, null);
			acm.setModelBound(new BoundingBox());
			acm.updateModelBound();
			for(StateType type : StateType.values()){
				RenderState rs = s.getRenderState(type);
				if(rs!=null){
					acm.setRenderState(rs);
				}
			}
			acm.setTrisPerPixel(.5f);
			acm.setDistanceTolerance(Scale.fromMeter(500));
			acm.updateRenderState();
			top.attachChild(acm);
		}
		if(s instanceof Node){
			if(((Node)s).getChildren()!=null){
				for(Spatial child : ((Node)s).getChildren()){
					setupClodImpl(top, child);
				}
			}
		}
	}
}
