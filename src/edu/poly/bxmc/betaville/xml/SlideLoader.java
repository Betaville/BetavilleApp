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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;

import edu.poly.bxmc.betaville.ResourceLoader;
import edu.poly.bxmc.betaville.jme.fenggui.tutorial.ImageSlide;
import edu.poly.bxmc.betaville.jme.fenggui.tutorial.Slide;
import edu.poly.bxmc.betaville.jme.fenggui.tutorial.SlideDeck;
import edu.poly.bxmc.betaville.jme.fenggui.tutorial.TextSlide;

/**
 * @author Skye Book
 *
 */
public class SlideLoader extends XMLReader{
	private SlideDeck slideDeck;
	private int defaultSlideWidth=300;
	private int defaultSlideHeight=200;

	/**
	 * @throws IOException 
	 * @throws JDOMException 
	 * 
	 */
	public SlideLoader(URL slideXML) throws JDOMException, IOException{
		loadFile(slideXML);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.xml.XMLReader#parse()
	 */
	@Override
	public void parse() throws Exception {
		slideDeck = new SlideDeck();
		parseProperties();
		parseSlides();
	}
	
	private void parseProperties(){
		Element properties = rootElement.getChild("properties");
		
		// load authors
		Element authors = properties.getChild("authors");
		if(authors!=null){
			List<?> authorList = authors.getChildren();
			ArrayList<String> textAuthors = new ArrayList<String>();
			for(Object author : authorList){
				if(((Element)author).getName().equals("author")){
					textAuthors.add(((Element)author).getText());
				}
			}
			slideDeck.setAuthors(textAuthors.toArray(new String[textAuthors.size()]));
		}
		
		Element title = properties.getChild("title");
		if(title!=null)  slideDeck.setTitle(title.getText());
		
		Element date = properties.getChild("date");
		if(date!=null) slideDeck.setDate(date.getText());
		
		Element defaults = properties.getChild("default");
		if(defaults!=null){
			Element font = defaults.getChild("font");
			if(font!=null) slideDeck.setDefaultFont(font.getText());
			Element size = defaults.getChild("size");
			if(size!=null) slideDeck.setDefaultSize(Integer.parseInt(size.getText()));
			
			// create RGBA color array
			Element color = defaults.getChild("color");
			if(color!=null){
				String r = color.getAttributeValue("r");
				String g = color.getAttributeValue("g");
				String b = color.getAttributeValue("b");
				String a = color.getAttributeValue("a");
				// only assign a default color if valid RGBA values were provided
				if(r!=null && g!=null && b!=null && a !=null){
					slideDeck.setDefaultColor(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b), Integer.parseInt(a));
				}
			}
		}
	}
	
	private void parseSlides() throws ClassNotFoundException, IOException{
		Element slideList = rootElement.getChild("slides");
		if(slideList!=null){
			if(slideList.getChildren()==null) return;
			for(Object xmlSlide : slideList.getChildren()){
				Slide slide=null;
				if(Class.forName(((Element)xmlSlide).getAttributeValue("type")).equals(ImageSlide.class)){
					slide = new ImageSlide(defaultSlideWidth, defaultSlideHeight, ResourceLoader.loadResource("/data/tutorial/"+((Element)xmlSlide).getChild("image").getText()), ((Element)xmlSlide).getChild("title").getText(), ((Element)xmlSlide).getChild("caption").getText());
				}
				else if(Class.forName(((Element)xmlSlide).getAttributeValue("type")).equals(TextSlide.class)){
					String[] bullets = null;
					Element bulletsElement = ((Element)xmlSlide).getChild("bullets");
					if(bulletsElement.getChildren()!=null){
						bullets = new String[bulletsElement.getChildren().size()];
						for(int i=0; i<bullets.length; i++){
							bullets[i] = ((Element)bulletsElement.getChildren().get(i)).getText();
						}
					}
					if(bullets == null) bullets = new String[0];
					slide = new TextSlide(defaultSlideWidth, defaultSlideHeight, ((Element)xmlSlide).getChild("title").getText(), bullets);
				}
				if(slide!=null) slideDeck.addSlide(slide);
			}
		}
	}
	
	public SlideDeck getSlideDeck(){
		return slideDeck;
	}
}
