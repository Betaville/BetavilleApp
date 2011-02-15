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
package edu.poly.bxmc.betaville.jme.exporters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import com.jme.image.Texture;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;

/**
 * @author Skye Book
 *
 */
public class MTLExporter {
	String materialData="";

	public String createMaterial(MaterialState ms, TextureState ts, URL folder, String meshName){
		// If there's no material state, there isn't much point to this.
		if(ms==null){
			return "";
		}
		
		materialData+=processMaterialState(ms, meshName);
		
		// Only export the textures if a texture state exists
		if(ts!=null){
			materialData+=processTextureState(ts, folder, meshName);
		}
		
		return materialData;
	}

	private String processTextureState(TextureState ts, URL folder, String meshName){
		int textureCount = ts.getNumberOfSetTextures();
		ArrayList<Texture> textures = new ArrayList<Texture>();

		// Gather the textures together
		for(int i=0; i<textureCount; i++){
			Texture t = ts.getTexture(i);
			
			// There is no guarantee that the texture units are filled
			// in order or without gaps
			if(t==null){
				// only look at a higher index if the local card supports
				// enough texture units
				if(textureCount<TextureState.getNumberOfFixedUnits()){
					textureCount++;
				}
			}
			else{
				textures.add(t);
			}
		}

		for(int i=0; i<textures.size(); i++){
			Texture t = textures.get(i);
			if(t.isStoreTexture()){
				// If the texture is stored, we need to write out the image
				try {
					ImageExporter.exportImage(t.getImage(), new URL(folder+meshName+"_"+i+".png"));
				} catch (MalformedURLException e) {
					// This should've happened further up the chain
					e.printStackTrace();
				}
			}
			else{
				// If the texture is not stored, we simply need to link the image
				// TODO: link to the mtl using: t.getImageLocation();
			}
		}
		
		return "";
	}

	private static String processMaterialState(MaterialState ms, String name){
		String material = "newmtl " + name + "\n";
		
		// Ambient color
		material+="Ka " + ms.getAmbient().r + " " + ms.getAmbient().g + " " + ms.getAmbient().b+"\n";
		
		// Diffuse color
		material+="Kd " + ms.getDiffuse().r + " " + ms.getDiffuse().g + " " + ms.getDiffuse().b+"\n";
		
		// Specular color & coefficient
		material+="Ks " + ms.getSpecular().r + " " + ms.getSpecular().g + " " + ms.getSpecular().b+"\n";
		//material+="Ns " + "\n";
		
		// Transparency
		material+="Tr " + ms.getDiffuse().a + "\n";
		
		return material;
	}
}
