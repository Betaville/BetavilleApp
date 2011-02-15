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
package bvtest.models;

import java.io.File;
import java.io.IOException;

import com.jme.app.SimpleGame;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;

import edu.poly.bxmc.betaville.jme.exporters.ColladaExporter;
import edu.poly.bxmc.betaville.jme.map.Rotator;

/**
 * @author Skye Book
 *
 */
public class ColladaExportTest extends SimpleGame {
	
	private Box box;
	private MaterialState ms;

	/**
	 * 
	 */
	public ColladaExportTest(){
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		box = new Box("testbox", new Vector3f(0,0,0), 5, 5, 5);
		box.setLocalRotation(Rotator.PITCH045);
		
		System.out.println("vertex buffer size: " + box.getVertexBuffer().capacity());
		System.out.println("vertex count: " + box.getVertexCount());
		System.out.println("normal buffer count: " + box.getNormalBuffer().capacity());
		
		
		ms = display.getRenderer().createMaterialState();
		ms.setAmbient(ColorRGBA.blue);
		ms.setDiffuse(ColorRGBA.green);
		box.setRenderState(ms);
		box.updateRenderState();
		System.out.println("box created");
		try {
			ColladaExporter exporter = new ColladaExporter(new File("box.dae"), box, true);
			exporter.writeData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		rootNode.attachChild(box);
	}
	
	public static void main(String[] args){
		ColladaExportTest cet = new ColladaExportTest();
		cet.start();
	}

}
