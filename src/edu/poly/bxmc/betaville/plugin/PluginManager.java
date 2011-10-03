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
package edu.poly.bxmc.betaville.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;

/**
 * @author Skye Book
 *
 */
public class PluginManager {
	
	private static Vector<Plugin> pluginList;
	
	static{
		pluginList = new Vector<Plugin>();
	}
	
	/**
	 * 
	 * @param pluginURL
	 * @param pluginClass
	 * @throws PluginAlreadyLoadedException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IncorrectPluginTypeException
	 */
	public synchronized static void loadPlugin(URL pluginURL, String pluginClass) throws PluginAlreadyLoadedException, ClassNotFoundException,
	InstantiationException, IllegalAccessException, IncorrectPluginTypeException{
		// check if the plugin is already loaded
		if(isPluginLoaded(pluginClass)) throw new PluginAlreadyLoadedException(pluginClass + " has already been loaded");
		
		loadAndRegister(pluginURL, pluginClass);
	}
	
	/**
	 * 
	 * @param pluginClass
	 * @return
	 */
	public synchronized static boolean unloadPlugin(String pluginClass){
		for(int i=0; i<pluginList.size(); i++){
			Plugin plugin = pluginList.get(i);
			// if this is the class we want, unload it
			if(plugin.getClass().getName().equals(pluginClass)){
				// first, destroy it
				plugin.destroy();
				
				// now remove it from the list
				pluginList.remove(plugin);
				
				// finally, get out of this function
				return true;
			}
		}
		
		// if we never found the function, return false
		return false;
	}
	
	private static boolean isPluginLoaded(String pluginClass){
		for(Plugin plugin : pluginList){
			if(plugin.getClass().getName().equals(pluginClass)) return true;
		}
		
		return false;
	}
	
	private static Plugin loadAndRegister(URL pluginURL, String pluginClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IncorrectPluginTypeException {
		//URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginURL}, PluginManager.class.getClass().getClassLoader());
		//Class<?> plugin = classLoader.loadClass(pluginClass);
		//JARClassLoader classLoader = new JARClassLoader(pluginURL, PluginManager.class.getClass().getClassLoader());
		//JARClassLoader classLoader = new JARClassLoader(pluginURL, ClassLoader.getSystemClassLoader());
		JARClassLoader classLoader = new JARClassLoader(pluginURL, BetavilleNoCanvas.class.getClassLoader());
		Class<?> plugin = Class.forName(pluginClass, false, classLoader);
		
		Object pluginInstance = plugin.newInstance();
		if(!(pluginInstance instanceof Plugin)) throw new IncorrectPluginTypeException(pluginInstance.getClass().getName() + " is not an allowed plugin type");
		else{
			((Plugin)pluginInstance).initialize();
			pluginList.add((Plugin)pluginInstance);
			return (Plugin)pluginInstance;
		}
	}
}
