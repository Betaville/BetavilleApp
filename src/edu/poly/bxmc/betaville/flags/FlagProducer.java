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
package edu.poly.bxmc.betaville.flags;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.net.NetPool;

/**
 * @author Skye Book
 *
 */
public class FlagProducer {
	private static Logger logger = Logger.getLogger(FlagProducer.class);
	private UTMCoordinate currentLocation;
	private AbstractFlagPositionStrategy flagPositionStrategy;
	
	// all of the proposals found in an area
	private List<Design>proposalDesigns = null;
	
	
	ArrayList<Integer> bases = new ArrayList<Integer>();
	

	/**
	 * 
	 */
	public FlagProducer(UTMCoordinate currentLocation, AbstractFlagPositionStrategy flagPositionStrategy){
		this.currentLocation=currentLocation;
		this.flagPositionStrategy=flagPositionStrategy;
	}
	
	public void getProposals(int meterRadius){

		proposalDesigns = NetPool.getPool().getConnection().findAllProposalsNearLocation(currentLocation, meterRadius);
		logger.info(proposalDesigns.size() + " proposals retrieved");
		
		Iterator<Integer> it;
		Iterator<Design> designIt = proposalDesigns.iterator();
		while(designIt.hasNext()){
			boolean hasMatch=false;
			Design design = designIt.next();
			int source = design.getSourceID();
			
//			if(design.getName().equals("PROPOSAL TITLE")){
//				logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//				logger.info(design.getCityID());
//				logger.info(design.getCoordinate());
//				logger.info(design.getID());
//				logger.info(design.getName());
//				logger.info(design.getUser());
//			}
			
			it = bases.iterator();
			
			// if this sourceID has already been observed then
			// don't add it to the list
			while(it.hasNext()){
				if(source==it.next()){
					hasMatch=true;
					break;
				}
			}
			if(!hasMatch){
				bases.add(source);
			}
		}
		logger.debug(proposalDesigns.size() + " proposal(s) based on " +bases.size() + " designs found within the specified area");
	}
	
	public void placeFlags(){
		Iterator<Integer> it = bases.iterator();
		while(it.hasNext()){
			int baseID = it.next();
			Design base = SceneScape.getCity().findDesignByID(baseID);
			if(base==null){
				logger.debug("Design " + baseID + " metainfo could not be found locally");
				base = NetPool.getPool().getConnection().findDesignByID(baseID);
			}
			
			ArrayList<Design> relevantProposals = new ArrayList<Design>();
			if(base!=null){
				relevantProposals.clear();
				// go through the proposals and look for children of this base design
				Iterator<Design> proposalIt = proposalDesigns.iterator();
				while(proposalIt.hasNext()){
					Design proposal = proposalIt.next();
					if(proposal.getSourceID()==base.getID()){
						relevantProposals.add(proposal);
					}
				}
				
				logger.debug(relevantProposals.size() + " relevant proposal(s) found for " + base.getID() + " | " + base.getName());
				
				// with the relevant proposals found, create the flag
				int height = flagPositionStrategy.findHeight(base);
				UTMCoordinate toUse = base.getCoordinate().clone();
				toUse.setAltitude(height+30);
				logger.debug("Placing flag for " + base.getName());
				flagPositionStrategy.placeFlag(toUse, base.getID(), relevantProposals);
				
			}
		}
	}
}