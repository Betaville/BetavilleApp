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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.model.Design.Classification;
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

		Iterator<Design> proposalIt = proposalDesigns.iterator();
		while(proposalIt.hasNext()){
			Design design = proposalIt.next();
			if(design.getClassification().equals(Classification.PROPOSAL)){
				SceneScape.getCity().addDesign(design);
				bases.add(design.getSourceID());
			}
		}
		logger.debug(proposalDesigns.size() + " proposal(s) based on " +bases.size() + " designs found within the specified area");
	}

	public void placeFlags(){

		double groupingProximityThreshold = 50d;

		HashMap<ArrayList<Design>, UTMCoordinate> groupings = new HashMap<ArrayList<Design>, UTMCoordinate>();

		for(int i=0; i<proposalDesigns.size(); i++){
			Design base = proposalDesigns.get(i);
			// skip this design if it isn't a proposal root
			if(!base.getClassification().equals(Classification.PROPOSAL)) continue;

			boolean hasBeenPutInGroup = false;

			// go through each grouping and look for a match based on proximity
			for(Entry<ArrayList<Design>, UTMCoordinate> entry : groupings.entrySet()){
				// calculate the distance between this proposal and the average of the proposals in the group
				/*
				 * Note: This is a slightly skewed calculation as it means that proposals put in a fresh group
				 * at the beginning of the list could potentially be eligible for inclusion in a group in a later
				 * part of the list as the coordinate changes due to averaging.  I think this is something we can
				 * live with for the time being (and the odds are that this will probably never be an issue, presuming
				 * that the proximity threshold for inclusion in a group is kept fairly small)
				 */
				double distance = MapManager.greatCircleDistanced(entry.getValue().getGPS(), base.getCoordinate().getGPS());

				if(distance<groupingProximityThreshold){
					logger.debug("Found a proposal to be grouped!");
					ArrayList<Design> group = entry.getKey();
					group.add(base);
					hasBeenPutInGroup=true;

					// average the coordinates
					ArrayList<ILocation> coordinates = new ArrayList<ILocation>();
					for(Design design : group){
						coordinates.add(design.getCoordinate());
					}

					// assign the new average coordinate
					entry.setValue(MapManager.averageLocations(coordinates).getUTM());
				}
			}

			// if, after scanning the groups, we haven't found a match it is time to make a new group of proposals
			if(!hasBeenPutInGroup){
				ArrayList<Design> group = new ArrayList<Design>();
				group.add(base);
				groupings.put(group, base.getCoordinate().clone());
			}
		}

		logger.info("There will now be " + groupings.size() + " groupings rather than " + bases.size()+" indvidual flags");


		// now let's place those flags!
		for(Entry<ArrayList<Design>, UTMCoordinate> entry : groupings.entrySet()){
			ArrayList<Integer> baseIDs = new ArrayList<Integer>();
			for(Design proposalBase : entry.getKey()){
				baseIDs.add(proposalBase.getID());
			}

			entry.getValue().setAltitude(50);

			flagPositionStrategy.placeFlag(entry.getValue(), baseIDs);
		}

		/*
		Iterator<Integer> it = bases.iterator();
		while(it.hasNext()){
			int baseID = it.next();
			Design base = SceneScape.getCity().findDesignByID(baseID);
			if(base==null){
				logger.warn("Design " + baseID + " metainfo could not be found locally");
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
				ArrayList<Integer> baseIDs = new ArrayList<Integer>();
				baseIDs.add(base.getID());
				flagPositionStrategy.placeFlag(toUse, baseIDs);

			}
		}
		
		*/
		
	}
}