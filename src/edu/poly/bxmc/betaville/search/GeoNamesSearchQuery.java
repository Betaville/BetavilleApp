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
package edu.poly.bxmc.betaville.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.geonames.FeatureClass;
import org.geonames.InsufficientStyleException;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import edu.poly.bxmc.betaville.jme.fenggui.extras.FengUtils;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;

/**
 * @author Skye Book
 *
 */
public class GeoNamesSearchQuery extends SearchQuery implements IConfigurableSearchQuery{
	private static final Logger logger = Logger.getLogger(GeoNamesSearchQuery.class);
	public static final String SEARCH_IDENTIFIER = "GeoNames";
	private ToponymSearchCriteria searchCriteria;
	/**
	 * 
	 */
	public GeoNamesSearchQuery() {
		this("");
	}

	/**
	 * @param searchTerm
	 */
	public GeoNamesSearchQuery(String searchTerm) {
		super(searchTerm);
		searchCriteria = new ToponymSearchCriteria();
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.search.ISearchQuery#fullFieldSearch(java.lang.String)
	 */
	public List<SearchResult> fullFieldSearch(String searchString) throws Exception {
		List<SearchResult> results = new ArrayList<SearchResult>();
		searchCriteria.setQ(searchString);
		try{
			ToponymSearchResult searchResult = WebService.search(searchCriteria);

			for (Toponym toponym : searchResult.getToponyms()) {
				GeoNamesSearchResult result;
				result  = new GeoNamesSearchResult(toponym);
				results.add(result);
			}
		}catch(Exception e){
			if(e.getMessage().contains("overloaded")){
				logger.warn(e.getMessage());
				GUIGameState.getInstance().getDisp().addWidget(FengUtils.createDismissableWindow("Geonames Search",
						"The Geonames search servers are currently overloaded!", "ok", true));
			}
		}
		return results;
	}

	/**
	 * Search functionality for finding just a city from GeoNames
	 * @param searchString
	 * @return
	 * @throws Exception
	 */
	public List<SearchResult> citySearch(String searchString) throws Exception {
		List<SearchResult> results = new ArrayList<SearchResult>();
		searchCriteria.setQ(searchString);
		searchCriteria.setFeatureClass(FeatureClass.P);
		searchCriteria.setFeatureCode("PPL");
		try{
			ToponymSearchResult searchResult = WebService.search(searchCriteria);

			for (Toponym toponym : searchResult.getToponyms()) {
				if(toponym.getFeatureClass()!=null){
					if(toponym.getFeatureClass().equals(FeatureClass.P)&&toponym.getFeatureCode().equals("PPL")){
						GeoNamesSearchResult result;
						result  = new GeoNamesSearchResult(toponym);
						results.add(result);
					}
				}
			}
		}catch(Exception e){
			if(e.getMessage().contains("overloaded")){
				logger.warn(e.getMessage());
				GUIGameState.getInstance().getDisp().addWidget(FengUtils.createDismissableWindow("Geonames Search",
						"The Geonames search servers are currently overloaded!", "ok", true));
			}
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.search.ISearchQuery#setFlags(boolean, java.lang.String[])
	 */
	public void setFlags(boolean clearPrevious, String... flags) {
		// TODO Auto-generated method stub

	}

	public String getQueryIdentifier() {
		return "GeoNames";
	}

	public List<SearchResult> searchNameField(String searchString, boolean exactMatch) throws Exception {
		List<SearchResult> results = new ArrayList<SearchResult>();

		if(exactMatch)searchCriteria.setNameEquals(searchString);
		else searchCriteria.setName(searchString);

		ToponymSearchResult searchResult = WebService.search(searchCriteria);
		for (Toponym toponym : searchResult.getToponyms()) {
			GeoNamesSearchResult result;
			result  = new GeoNamesSearchResult(toponym);
			results.add(result);
		}
		return results;
	}

}
