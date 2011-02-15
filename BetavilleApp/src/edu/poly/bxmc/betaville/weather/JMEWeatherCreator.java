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
package edu.poly.bxmc.betaville.weather;

import com.jme.renderer.ColorRGBA;

import edu.poly.bxmc.betaville.jme.map.Scale;

/**
 * @author Skye Book
 *
 */
public class JMEWeatherCreator implements JMEWeatherCondition{
	private WeatherCondition condition;

	/**
	 * 
	 */
	public JMEWeatherCreator(WeatherCondition condition) {
		this.condition=condition;
	}

	public ColorRGBA getFogColor(int[] color) {
		return convertToCRGBA(condition.getFogColorRGB());
	}

	public int getFogStartFromMeters(int meters) {
		return (int)Scale.fromMeter(condition.getFogStartMeters());
	}

	public ColorRGBA getSkyColor(int[] color) {
		return convertToCRGBA(condition.getSkyColorRGB());
	}

	public void setCondition(WeatherCondition condition) {
		this.condition=condition;
	}
	private ColorRGBA convertToCRGBA(int[] color){
		ColorRGBA rgba = new ColorRGBA();
		if(color.length==3){
			return new ColorRGBA((color[0]/255f), (color[1]/255f), (color[2]/255f), 1f);
		}
		else if(color.length==4){
			return new ColorRGBA((color[0]/255f), (color[1]/255f), (color[2]/255f), (color[3]/255f));
		}
		else return ColorRGBA.randomColor();
	}
}
