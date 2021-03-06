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
package edu.poly.bxmc.betaville.xml;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * @author Skye Book
 *
 */
public class TutorialListLoader extends XMLReader{
	
	private static final Logger logger = Logger.getLogger(TutorialListLoader.class);

	private URL urlRoot;
	private List<URL> tutorialList;

	public TutorialListLoader(URL urlRoot) throws Exception{
		this.urlRoot = new URL(urlRoot.toString()+Locale.getDefault().getLanguage().toLowerCase()+"/");

		try{
			load();
		}catch(Exception e){
			logger.warn("Tutorials not found for default locale: " + Locale.getDefault().getLanguage().toLowerCase());
			this.urlRoot = new URL(urlRoot.toString()+"en/");
			load();
		}
	}

	private void load() throws Exception{
		URL url;
		if(this.urlRoot.toString().endsWith("/")) url = new URL(this.urlRoot.toString()+"tutorials.xml");
		else url = new URL(this.urlRoot.toString()+"/tutorials.xml");
		tutorialList = new ArrayList<URL>();
		loadFile(url);
		parse();
	}

	public List<URL> getTutoralList(){
		return tutorialList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parse() throws Exception {
		List<Element> tutorials = rootElement.getChildren();
		for(Element tutorial : tutorials){
			if(urlRoot.toString().endsWith("/")) tutorialList.add(new URL(urlRoot.toString()+tutorial.getText()));
			else tutorialList.add(new URL(urlRoot.toString()+"/"+tutorial.getText()));
		}
	}
}
