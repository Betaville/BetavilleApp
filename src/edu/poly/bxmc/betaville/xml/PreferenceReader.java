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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

import edu.poly.bxmc.betaville.jme.loaders.util.DriveFinder;

/**
 * @author Skye Book
 *
 */
public class PreferenceReader extends XMLReader {
	private static Logger logger = Logger.getLogger(PreferenceReader.class);
	private boolean xmlLoaded=false;

	/**
	 * 
	 * @param xmlFile
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public PreferenceReader(File xmlFile) throws JDOMException, IOException{
		super();
		loadFile(xmlFile);
		xmlLoaded=true;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.xml.XMLReader#parse()
	 */
	@Override
	public void parse(){
		parseImpl(rootElement);
	}
	
	@SuppressWarnings("rawtypes")
	private void parseImpl(Element top){
		String currentFullName="";
		List children = top.getChildren();
		Iterator it = children.iterator();
		while(it.hasNext()){
			Element current = (Element)it.next();
			currentFullName = current.getName();
			
			if(current.getChildren().size()==0){
				Element parent = current.getParentElement();
				currentFullName = parent.getName()+"."+currentFullName;
				while((parent = parent.getParentElement())!=null){
					currentFullName = parent.getName()+"."+currentFullName;
				}
				
				System.setProperty(currentFullName, current.getValue());
			}
			else parseImpl(current);
		}
	}
	
	public boolean isXMLLoaded(){
		return xmlLoaded;
	}

	public static void main(String[] args) throws JDOMException, IOException{
		PreferenceReader pr = new PreferenceReader(new File(DriveFinder.getHomeDir().toString()+"/.betaville/preferences.xml"));
		pr.parse();
		PreferenceWriter pw = new PreferenceWriter();
		pw.writeData();
	}
}
