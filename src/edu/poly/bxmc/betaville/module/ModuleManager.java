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
package edu.poly.bxmc.betaville.module;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class ModuleManager {
	private static Logger logger = Logger.getLogger(ModuleManager.class);
	
	private static ModuleManager manager = new ModuleManager();
	
	/**
	 * 
	 */
	private ModuleManager(){}
	
	public void initializeModules(){
		
	}
	
	public static ModuleManager getInstance(){
		return manager;
	}
	
	public synchronized void registerModule(Module module) throws ModuleNameException{
		boolean moduleRecognized=false;
		
		if(module instanceof GUIModule){
			GUIGameState.getInstance().addModuleToUpdateList(module);
			moduleRecognized=true;
		}
		if(module instanceof SceneModule){
			SceneGameState.getInstance().addModuleToUpdateList(module);
			moduleRecognized=true;
		}
		if(module instanceof NetworkingModule){
			NetPool.getPool().addModuleToUpdateList(module);
			moduleRecognized=true;
		}
		
		if(!moduleRecognized){
			logger.error("This module '"+module.getName()+"' was not recognized by the client!");
		}
		else logger.info("Module: '" + module.getName() + "' added");
	}
	
	public synchronized  void deregisterModule(String name){
	}
}
