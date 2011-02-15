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
package bvtest.map;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jmex.terrain.TerrainBlock;

/**
 * @author Skye Book
 *
 */
public class TerrainSize extends SimpleGame {
	
	private TerrainBlock tb;

	/**
	 * 
	 */
	public TerrainSize() {
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		int size=10; // size-1 is the number of lengths of the side
		float[] height = new float[size*size];
		for(int y=0; y<size; y++){
			for(int x=0; x<size; x++){
				height[(y*size)+x]=0;
			}
		}
		tb = new TerrainBlock("terrain", size, new Vector3f(1, 1, 1), height, new Vector3f(0,0,0));
		tb.setLocalTranslation(3, 3, 3);
		tb.setLocalScale(.01f);
		tb.updateGeometricState(0, true);
		tb.updateWorldVectors();
		printAt(-1,-1);
		printAt(0,0);
		printAt(1,1);
		printAt(2,2);
		printAt(11,11);
		rootNode.attachChild(tb);
		//tb.setRenderState(display.getRenderer().createWireframeState());
		tb.updateRenderState();
	}
	
	private void printAt(float x, float z){
		System.out.println("Height At: "+x+","+z+": "+tb.getHeight(x, z));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TerrainSize game = new TerrainSize();
		game.start();
	}

}
