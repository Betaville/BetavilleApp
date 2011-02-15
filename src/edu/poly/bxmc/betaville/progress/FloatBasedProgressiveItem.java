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
package edu.poly.bxmc.betaville.progress;

/**
 * @author Skye Book
 *
 */
public class FloatBasedProgressiveItem extends ProgressiveItem {
	
	private float current;
	private float maximum;

	/**
	 * @param name
	 */
	public FloatBasedProgressiveItem(String name, float current, float maximum) {
		super(name);
		this.current=current;
		this.maximum=maximum;
	}
	
	public void update(int current){
		this.current=current;
		for(IProgressUpdateListener listener : listeners){
			listener.progressUpdated(getPercentage(), getCurrentProgress());
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.progress.ProgressiveItem#getPercentage()
	 */
	@Override
	public float getPercentage() {
		return current/maximum;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.progress.ProgressiveItem#getCurrentProgress()
	 */
	@Override
	public String getCurrentProgress() {
		return current+"/"+maximum;
	}
}
