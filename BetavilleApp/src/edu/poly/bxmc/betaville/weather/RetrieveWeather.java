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
package edu.poly.bxmc.betaville.weather;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import com.sun.cnpi.rss.elements.Category;
import com.sun.cnpi.rss.elements.Item;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserFactory;

/**
 * @author Skye Book
 *
 */
public class RetrieveWeather {
	
	public RetrieveWeather(int zipCode,String locationID, String unit) throws Exception
	{
		// create the parser
		RssParser parser = RssParserFactory.createDefault();
		locationID = "USNY0107";
		unit = "f";
		Rss rss = parser.parse(buildURL(zipCode, locationID, unit));
		
		// get the xml elements
		Collection<?> items = rss.getChannel().getItems();
		if(items != null && !items.isEmpty())
		{
			// iterate through main elements
			for (Iterator<?> i = items.iterator(); i.hasNext(); System.out.println())
			{
				for (int feedCount = 0; feedCount<1; feedCount++)
				{
					Item item = (Item)i.next();
					System.out.println("title: " + item.getTitle());
					System.out.println("something else: " + item.getDescription());
				}
			}
			
			 // iterate through categories if we are provided with any
	        Collection<?> categories = rss.getChannel().getCategories();
	        if(categories != null && !categories.isEmpty())
	        {
	            Category cat;
	            for(Iterator<?> i = categories.iterator();
	                i.hasNext();
	                System.out.println("Category Domain: " + cat.getDomain()))
	            {
	                cat = (Category)i.next();
	                System.out.println("Category: " + cat);
	            }

	        }
			
		}
	}
	
	private URL buildURL(int zipCode, String locationID, String unit) throws MalformedURLException
	{
		//String stringUrl = "http://weather.yahooapis.com/forecastrss?p="+ locationID + "&u="+ unit;
		String stringUrl = "http://rss.accuweather.com/rss/liveweather_rss.asp?metric=0&locCode=11201";
		
		// Use this for Europe and use zip codes for US
		//EUR|DE|GM005|BREMEN|
		URL rssUrl = new URL(stringUrl);
		//USNY0107 <- bellmore
		return rssUrl;
	}
	
	public static void main(String[] args){
		try {
			new RetrieveWeather(11201, "", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


