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

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

/**
 * ToggleAnimator combines two animations, one for each direction. 
 * @author Peter Schulz
 */
public class ToggleAnimator {
	
	public enum ToggleState { HIDDEN, SHOWN }
	
	private Animator showAni;
	private Animator hideAni;
	private ToggleState targetState;
	
	/**
	 * Updates {@link targetState} depending on the current animation phase.
	 */
	private TimingTarget stateTarget = new TimingTarget() {

		/* (non-Javadoc)
		 * @see org.jdesktop.animation.timing.TimingTarget#begin()
		 */
		public void begin() {}

		/* (non-Javadoc)
		 * @see org.jdesktop.animation.timing.TimingTarget#end()
		 */
		public void end() {
			targetState = targetState == ToggleState.SHOWN 
			? ToggleState.HIDDEN : ToggleState.SHOWN;
		}

		/* (non-Javadoc)
		 * @see org.jdesktop.animation.timing.TimingTarget#repeat()
		 */
		public void repeat() {}

		/* (non-Javadoc)
		 * @see org.jdesktop.animation.timing.TimingTarget#timingEvent(float)
		 */
		public void timingEvent(float fraction) {}
		
	};
	
	/**
	 * Creates a ToggleAnimator.
	 * @param show the animation used for the transition hidden-shown
	 * @param hide the animation used for the transition shown-hidden
	 * @param targetState the first target state
	 */
	public ToggleAnimator(Animator show, Animator hide, ToggleState targetState) {
		show.addTarget(stateTarget);
		hide.addTarget(stateTarget);
		this.showAni = show;
		this.hideAni = hide;
		this.targetState = targetState;
	}

	/**
	 * Returns the current target state.
	 * @return the target state
	 */
	public ToggleState getTargetState() {
		return targetState;
	}
	
	public void setTargetState(ToggleState state) {
	  targetState = state;
	}
	
	/**
	 * Checks if any transition is being processed.
	 * @return true, if either show or hide animation are currently running, false else
	 */
	public boolean isRunning() {
		return showAni.isRunning() || hideAni.isRunning();
	}

	/**
	 * Starts the appropriate animation according to the current target state.
	 */
	public void toggle() {		
		switch(targetState) {
		case SHOWN:
			if (showAni.isRunning()) {
				showAni.stop();
				targetState	= ToggleState.HIDDEN;
				hideAni.start();
				break;
			}
			showAni.start();
			break;
		case HIDDEN:
			if (hideAni.isRunning()) {
				hideAni.stop();
				targetState	= ToggleState.SHOWN;
				showAni.start();
				break;
			}
			hideAni.start();
			break;
		}
	}
}
