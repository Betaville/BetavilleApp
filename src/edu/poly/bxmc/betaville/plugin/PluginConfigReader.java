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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

import edu.poly.bxmc.betaville.xml.XMLReader;

/**
 * @author Skye Book
 *
 */
public class PluginConfigReader extends XMLReader {
	private static Logger logger = Logger.getLogger(PluginConfigReader.class);
	
	private URL configURL;
	
	private List<PluginParsedCallback> callbacks;

	/**
	 * 
	 * @param xmlFile
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public PluginConfigReader(File xmlFile) throws JDOMException, IOException{
		super();
		configURL = xmlFile.toURI().toURL();
		callbacks = new ArrayList<PluginConfigReader.PluginParsedCallback>();
		loadFile(xmlFile);
	}
	
	public void addCallback(PluginParsedCallback callback){
		callbacks.add(callback);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.xml.XMLReader#parse()
	 */
	@Override
	public void parse(){
		String name = rootElement.getChild("name").getText();
		String description = rootElement.getChild("description").getText();
		String author = rootElement.getChild("author").getText();
		String classname = rootElement.getChild("classname").getText();
		Element dependencies = rootElement.getChild("dependencies");
		String version = rootElement.getChild("version").getText();
		List<?> dpChildren = dependencies.getChildren();
		List<String> jars = new ArrayList<String>();
		for(int i=0; i<dpChildren.size(); i++){
			jars.add(((Element)dpChildren.get(i)).getText());
		}
		
		logger.info("XML traversal complete");
		
		// convert to URLs
		List<URL> urlList = new ArrayList<URL>();
		for(String jar : jars){
			try {
				urlList.add(new URL(jar));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		logger.info("URL conversion complete");
		
		// start the plugin
		try {
			
			URL[] urlArray = new URL[urlList.size()];
			for(int i=0; i<urlList.size(); i++){
				urlArray[i] = urlList.get(i);
			}
			
			Plugin plugin = PluginManager.loadPlugin(urlArray, configURL, classname, version);
			logger.info("Plugin loaded");
			plugin.setName(name);
			plugin.setDescription(description);
			plugin.setAuthor(author);
			
			// trigger callbacks
			logger.info("Firing callbacks after plugin parsing");
			for(PluginParsedCallback callback : callbacks){
				callback.onPluginParsed(name, description, author, classname);
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncorrectPluginTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PluginAlreadyLoadedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public interface PluginParsedCallback{
		
		/**
		 * Called when a plugin is parsed
		 * @param name
		 * @param description
		 * @param author
		 * @param classname
		 */
		public void onPluginParsed(String name, String description, String author, String classname);
	}
}