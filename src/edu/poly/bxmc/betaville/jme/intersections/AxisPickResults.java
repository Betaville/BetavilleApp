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
package edu.poly.bxmc.betaville.jme.intersections;

import com.jme.intersection.TrianglePickResults;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
/**
 * Class <AxisPickResults> - Manage the collision
 *
 * @author Caroline Bouchat
 * @version 0.1 - Spring 2009
 */
public abstract class AxisPickResults extends TrianglePickResults {
	/**
	 * Constqnte <DIST_WAL> - Minimal distance of the camera to the wall
	 */
	protected static final float DIST_WALL = 5.0f;

	/**
	 * Attribute <gameState> - Game state of the world
	 */
	private SceneGameState gameState;

	/**
	 * Constructor - TODO
	 *
	 * @param worldGameState The game state
	 */
	public AxisPickResults(SceneGameState sceneGameState) {
		gameState = sceneGameState;
	}

	/**
	 * Method <getGameState> - Returns the game state
	 *
	 * @return The game state
	 */
	protected SceneGameState getGameState() {
		return gameState;
	}
	
	/* (non-Javadoc)
	 * @see com.jme.intersection.TrianglePickResults#processPick()
	 */
	public abstract void processPick();
}
