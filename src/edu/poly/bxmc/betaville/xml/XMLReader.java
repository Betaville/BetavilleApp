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
import java.io.InputStream;
import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author Skye Book
 *
 */
public abstract class XMLReader{
	private SAXBuilder builder;
	protected Document dom;
	protected Element rootElement;

	/**
	 * Reads an XML file.
	 * @throws JDOMException 
	 * @throws IOException 
	 */
	public XMLReader(){
		builder = new SAXBuilder();
	}
	
	public void loadFile(File xmlFile) throws JDOMException, IOException{
		dom = builder.build(xmlFile);
		rootElement = dom.getRootElement();
	}
	
	public void loadFile(URL xmlFile) throws JDOMException, IOException{
		dom = builder.build(xmlFile);
		rootElement = dom.getRootElement();
	}
	
	public void loadFile(InputStream xmlFile) throws JDOMException, IOException{
		dom = builder.build(xmlFile);
		rootElement = dom.getRootElement();
	}
	
	public void loadStream(InputStream is) throws JDOMException, IOException{
		dom = builder.build(is);
		rootElement = dom.getRootElement();
	}
	
	public abstract void parse() throws Exception;

}
