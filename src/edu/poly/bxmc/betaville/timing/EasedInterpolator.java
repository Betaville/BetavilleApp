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
 * 
 * $Id$
 */
package edu.poly.bxmc.betaville.timing;

import org.jdesktop.animation.timing.interpolation.Interpolator;

/**
 * EasedInterpolator utilises an one or two ease functions.
 * @author Peter Schulz
 */
public class EasedInterpolator implements Interpolator {
	
	private Ease in;
	private Ease out;
	private float middle;
	private float scaleIn;
	private float scaleOut;
	
	/**
	 * Creates an EasedInterpolator without easing.
	 */
	public EasedInterpolator() {
	}
	
	/**
	 * Creates an EasedInterpolator with an ease out function.
	 * @param out ease out function
	 */
	public EasedInterpolator(Ease out) {
		this(null, out, 0);
	}
	
	/**
	 * Creates an EasedInterpolator with an ease in + out function.
	 * @param in ease in function
	 * @param out ease out function
	 */
	public EasedInterpolator(Ease in, Ease out) {
		this(in, out, 0.5f);
	}

	/**
	 * Creates an EasedInterpolator with an ease in + out function.
	 * Additionally allows to set the ratio between the both of them.
	 * @param in ease in function
	 * @param out ease out function
	 * @param middle a value between 0 and 1 (inclusive)
	 */
	public EasedInterpolator(Ease in, Ease out, float middle) {
		if (middle < 0 || middle > 1)
			throw new IllegalArgumentException("middle must lie between 0 and 1!");
		
		this.in			= in;
		this.out		= out;
		this.middle		= middle;
		this.scaleIn	= middle > 0 ? 1/middle : 0;
		this.scaleOut	= middle > 0 ? 1/middle - 1 : 1; // (1-middle) / middle
	}

	/* (non-Javadoc)
	 * @see org.jdesktop.animation.timing.interpolation.Interpolator#interpolate(float)
	 */
	public float interpolate(float fraction) {
		if (fraction < middle) {
			if (in != null) return in.easeIn(fraction * scaleIn); 
		} else {
			if (out != null) return out.easeOut(fraction * scaleOut);
		}
		return fraction;
	}
}
