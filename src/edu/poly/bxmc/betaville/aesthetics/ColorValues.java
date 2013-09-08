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
package edu.poly.bxmc.betaville.aesthetics;

/**
 * @author Skye Book
 *
 */
public class ColorValues {
	private static int[] fogColor = new int[]{255,255,255,255};
	private static int[] diffuseLightColor = new int[]{255,255,255,255};
	private static int[] ambientLightColor = new int[]{0,0,0,0};
	
	private static float[] waterColorF = new float[]{10f/255f,15f/255f,20f/255f,1f};
	private static float[] fogColorF = new float[]{.7f,.85f,1,1f};
	private static float[] light1DiffuseColor = new float[]{1,.94f,.8f,1};
	private static float[] light1AmbientColor = new float[]{0f,0f,0f,0f};
	private static float[] light2DiffuseColor = new float[]{.3f,.4f,.45f,.3f};
	private static float[] light2AmbientColor = new float[]{0f,0f,0f,0f};

	private static float[] pyramidAmbientColor = new float[]{255/255f, 102/255f, 0/255f, 1};
	private static float[] pyramidDiffuseColor = new float[]{255/255f, 102/255f, 0/255f, 1};
	
	private static float[] selectionAmbientColor = new float[]{255/255f, 112/255f, 0/255f, 1};
	private static float[] selectionDiffuseColor = new float[]{255/255f, 112/255f, 0/255f, 1};
	
	

	public static float[] getFogColorAsUnit(){
		return new float[]{
				ColorUtil.convertEightBitToUnit(fogColor[0]),
				ColorUtil.convertEightBitToUnit(fogColor[1]),
				ColorUtil.convertEightBitToUnit(fogColor[2]),
				ColorUtil.convertEightBitToUnit(fogColor[3])
		};
	}

	public static int[] getFogColorAsEightBit(){
		return fogColor;
	}

	public static float[] getDiffuseLightColorAsUnit(){
		return new float[]{
				ColorUtil.convertEightBitToUnit(diffuseLightColor[0]),
				ColorUtil.convertEightBitToUnit(diffuseLightColor[1]),
				ColorUtil.convertEightBitToUnit(diffuseLightColor[2]),
				ColorUtil.convertEightBitToUnit(diffuseLightColor[3])
		};
	}

	public static int[] getDiffuseLightColorAsEightBit(){
		return diffuseLightColor;
	}

	public static float[] getAmbientLightColorAsUnit(){
		return new float[]{
				ColorUtil.convertEightBitToUnit(ambientLightColor[0]),
				ColorUtil.convertEightBitToUnit(ambientLightColor[1]),
				ColorUtil.convertEightBitToUnit(ambientLightColor[2]),
				ColorUtil.convertEightBitToUnit(ambientLightColor[3])
		};
	}

	public static int[] getAmbientLightColorAsEightBit(){
		return ambientLightColor;
	}
	
	public static float[] getPyramidAmbientColorAsUnit(){
		return pyramidAmbientColor;
	}

	public static float[] getPyramidDiffuseColorAsUnit() {
		return pyramidDiffuseColor;
	}

	public static float[] getSelectionAmbientColorAsUnit() {
		return selectionAmbientColor;
	}

	public static float[] getSelectionDiffuseColorAsUnit() {
		return selectionDiffuseColor;
	}
}
