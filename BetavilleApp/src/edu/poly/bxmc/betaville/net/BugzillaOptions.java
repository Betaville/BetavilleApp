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
package edu.poly.bxmc.betaville.net;

/**
 * Tools for building a Bugzilla bug entry URL
 * @author Skye Book
 *
 */
public class BugzillaOptions {
	
	public static final String baseURL = "http://128.238.56.115/bugzilla/";
	public static final String enterBug = "enter_bug.cgi";
	
	public static final String component = "component";
	
	public static final String component_3dContent = "Client:%203D%20Content";
	public static final String component_CoordinateSystem = "Client:%20Coordinate%20System";
	public static final String component_Graphics = "Client:%20Graphics";
	public static final String component_GUI = "Client:%20GUI";
	public static final String component_Interaction = "Client:%20Interaction";
	public static final String component_ReadFromServer = "Client:%20Read%20From%20Server";
	public static final String component_WriteToServer = "Client:%20Write%20To%20Server";
	public static final String component_DataModeling = "Data%20Modeling";
	public static final String component_Deployment = "Deployment";
	public static final String component_DatabaseTransactions = "Server:%20Database%20Transactions";
	public static final String component_ServerReadFromClient = "Server:%20Read%20From%20Client";
	public static final String component_ServerWriteToClient = "Server:%20Write%20To%20Client";
	
	public static String constructURL(String componentName){
		return baseURL+enterBug+"?"+"product=Betaville&"+component+"="+componentName;
	}
}
