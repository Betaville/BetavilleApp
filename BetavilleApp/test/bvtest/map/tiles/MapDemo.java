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
package bvtest.map.tiles;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;

import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.terrain.OSMTileRequestGenerator;

/**
 * @author Skye Book
 *
 */
public class MapDemo extends SimpleGame {
	
	ExecutorService threadPool = Executors.newCachedThreadPool();

	/**
	 * 
	 */
	public MapDemo() {
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		threadPool.submit(new Runnable(){

			public void run(){
				int scale = 10;
				int size=2;
				float[] height = new float[size*size];
				for(int y=0; y<size; y++){
					for(int x=0; x<size; x++){
						height[x]=0;
					}
				}
				
				cam.setLocation(new Vector3f(size/2,50,size/2));
				cam.setFrustumFar(cam.getFrustumFar()*2);
				
				OSMTileRequestGenerator generator = new OSMTileRequestGenerator(16);
				String url = generator.generateTileRequest(40.7, -74.03);
				
				Node terrainNode = new Node("terrain");
				terrainNode.setLocalRotation(Rotator.PITCH180);
				rootNode.attachChild(terrainNode);
				
				int sideSize = 4;
				// create individual blocks
				for(int x=0; x<sideSize; x++){
					for(int y=0; y<sideSize; y++){
						TerrainBlock tb = new TerrainBlock(x+"/"+y, size, new Vector3f(scale, scale, scale), height, new Vector3f(0,0,0));
						tb.setLocalTranslation(new Vector3f(x*((size*scale)-scale),0,y*((size*scale)-scale)));
						try {
							URL imageURL = new URL(url);
							url = generator.shiftAndGetRequest(0, 1);
							TextureState ts = display.getRenderer().createTextureState();
							Texture t = TextureManager.loadTexture(imageURL, true);
							System.out.println("Texture created for " + x+","+y);
							ts.setTexture(t);
							tb.setRenderState(ts);
							tb.updateRenderState();
							terrainNode.attachChild(tb);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					url = generator.shiftAndGetRequest(1, -1*sideSize);
				}
			}
		}
		);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MapDemo md = new MapDemo();
		md.start();
	}

}
