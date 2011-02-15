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
package edu.poly.bxmc.betaville.jme.loaders.util;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.util.geom.BufferUtils;

/**
 * A collection of methods to check a graphics card for any compatibility
 * issues.  With the diverse set of hardware that Betaville attempts to target
 * it is inevitable that some users will have trouble running all of the assets
 * with all of the textures at their display's highest possible resolution.  By
 * catching any potential problems before they become real ones will significantly
 * help user experience.
 * 
 * @author Skye Book
 *
 */
public class GPUQuery {
	
	/**
	 * Gets the maximum supported texture size on the current platform.
	 * @return The maximum allowed size, in pixels, of a texture.
	 */
	public static int getMaxTextureSize(){
		//check maximum texture size
		IntBuffer maxSize = BufferUtils.createIntBuffer(16);
		GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, maxSize);
		maxSize.rewind();
		return maxSize.get();
	}
	
	/**
	 * Checks to see if Non-Power of Two textures are supported
	 * on the current platform.
	 * @return True if NPOT is supported, false if its not.
	 */
	public static boolean isNpotSupported(){
		String extensions = GL11.glGetString(GL11.GL_EXTENSIONS);
		return extensions.contains("GL_ARB_texture_non_power_of_two");
	}

}
