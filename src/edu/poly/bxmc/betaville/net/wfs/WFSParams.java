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
package edu.poly.bxmc.betaville.net.wfs;

/**
 * Connection parameters for a Web Feature Service.
 * 
 * Created based on information found in the GeoTools
 * <a href="http://docs.geotools.org/latest/userguide/library/data/wfs.html">user guide</a>
 * @author Skye Book
 *
 */
public class WFSParams {
	
	/**
	 * Link to capabilities document. The implementation supports both
	 * WFS 1.0 (read/write) and WFS 1.1 (read-only).
	 */
	public static final String GET_CAPABILITIES_URL = "WFSDataStoreFactory:GET_CAPABILITIES_URL";
	
	/**
	 * Optional: True for Post, False for GET, null for auto
	 */
	public static final String PROTOCOL = "WFSDataStoreFactory:PROTOCOL";
	
	/**
	 * Optional
	 */
	public static final String USERNAME = "WFSDataStoreFactory:USERNAME";
	
	/**
	 * Optional
	 */
	public static final String PASSWORD = "WFSDataStoreFactory:PASSWORD";
	
	/**
	 * Optional with a default of UTF-8
	 */
	public static final String ENCODING = "WFSDataStoreFactory:ENCODING";
	
	/**
	 * Optional with a 3000ms default
	 */
	public static final String TIMEOUT = "WFSDataStoreFactory:TIMEOUT";
	
	/**
	 * Optional number of features to read in one gulp, defaults of 10
	 */
	public static final String BUFFER_SIZE = "WFSDataStoreFactory:BUFFER_SIZE";
	
	/**
	 * Optional with a default of true, try compression if available
	 */
	public static final String TRY_GZIP = "WFSDataStoreFactory:TRY_GZIP";
	
	/**
	 * Optional default of true. WFS implementations are terrible for actually
	 * obeying their DescribeFeatureType schema, setting this to true will try
	 * a few tricks to support implementations that are mostly correct:
	 * 
	 * <ul>
	 * <li>Accepting the data in any order</li>
	 * <li>Not getting too upset if the case of the attributes is wrong</li>
	 * </ul>
	 */
	public static final String LENIENT = "WFSDataStoreFactory:LENIENT";
	
	/**
	 * Limit on the number of features
	 */
	public static final String MAXFEATURES = "WFSDataStoreFactory:MAXFEATURES";
	
	/**
	 * Optional used to engage specific work arounds for known servers.
	 * 
	 * <ul>
	 * <li>"arcgis"</li>
	 * <li>"cuberx"</li>
	 * <li>"geoserver"</li>
	 * <li>"ionic"</li>
	 * <li>"mapserver"</li>
	 * <li>"nonstrict"</li>
	 * <li>"strict"</li>
	 * <li>null - automatic based GET_CAPABILITIES url</li>
	 * </ul>
	 * 
	 * You may need use this override if you are using mapserver with a custom URL
	 * not recognised by auto detection. WFS1.1 supports autodetection based on
	 * full capabilities doc for greater accuracy.
	 */
	public static final String WFS_STRATEGY = "WFSDataStoreFactory:WFS_STRATEGY";
	
	/**
	 * Optional used override how GetFeature operations encodes filters
	 * 
	 * <ul>
	 * <li>0 (low compliance) full range of geotools filters represented</li>
	 * <li>1 (medium compliance) Id filters mixed with bbox only</li>
	 * <li>2 (strict compliance) Id filters cannot be combined at all</li>
	 * </ul>
	 * 
	 * In general compliance levels stress the handling of Id filters which are not
	 * allowed with other filters (AND / OR / NOT). You may relax this constraint when
	 * working with some WFS implementations such as GeoServer.
	 */
	public static final String FILTER_COMPLIANCE = "WFSDataStoreFactory:FILTER_COMPLIANCE";
}
