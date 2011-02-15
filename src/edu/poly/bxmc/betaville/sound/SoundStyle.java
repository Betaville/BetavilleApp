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
package edu.poly.bxmc.betaville.sound;

import java.io.Serializable;

/**
 * The style of a sound
 * TODO: rolloff handling
 * @author Skye Book
 * @author Joe Fattorini
 *
 */
public class SoundStyle implements Serializable{
	private static final long serialVersionUID = 3017L;
	
	protected int proposalDucking;
	protected int backgroundDucking;
	protected ExitAction exitAction;

	/**
	 * 
	 */
	public SoundStyle(int proposalDucking, int backgroundDucking, ExitAction exitAction){
		this.proposalDucking=proposalDucking;
		this.backgroundDucking=backgroundDucking;
		this.exitAction=exitAction;
	}
	
	
	/**
	 * Creates a SoundStyle without any ducking
	 * @param exitAction
	 */
	public SoundStyle(ExitAction exitAction){
		this(0, 0, exitAction);
	}


	public int getProposalDucking() {
		return proposalDucking;
	}


	public int getBackgroundDucking() {
		return backgroundDucking;
	}


	public ExitAction getExitAction() {
		return exitAction;
	}
}
