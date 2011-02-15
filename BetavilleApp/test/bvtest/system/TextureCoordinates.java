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
package bvtest.system;

import java.io.File;
import java.net.MalformedURLException;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.image.Texture.WrapMode;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

import edu.poly.bxmc.betaville.jme.map.Rotator;

/**
 * @author Skye Book
 *
 */
public class TextureCoordinates extends SimpleGame {

	/**
	 * 
	 */
	public TextureCoordinates() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		Quad box = new Quad("quad", 5, 5);
		box.setLocalRotation(Rotator.angleX(-90));
		TextureState ts = display.getRenderer().createTextureState();
		try {
			Texture t1 = TextureManager.loadTexture(new File("test/map.png").toURI().toURL());
			ts.setTexture(t1);
			t1.setWrap(WrapMode.BorderClamp);
			t1.setRotation(Rotator.angleZ(270));
			t1.setTranslation(new Vector3f(0, 1, 0));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		box.setRenderState(ts);
		box.updateRenderState();
		rootNode.attachChild(box);
	}
	
	public static void main(String[] args){
		TextureCoordinates game = new TextureCoordinates();
		game.start();
	}
}
