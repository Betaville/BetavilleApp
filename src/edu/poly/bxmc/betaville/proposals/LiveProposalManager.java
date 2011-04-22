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
package edu.poly.bxmc.betaville.proposals;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.model.Design;

/**
 * Functions as a central location to add and remove versions
 * of proposals from the user-viewable scene
 * 
 * TODO: Synchronize with SceneScape's list of designs
 * @author Skye Book
 *
 */
public class LiveProposalManager {
	private static Logger logger = Logger.getLogger(LiveProposalManager.class);
	private static LiveProposalManager lpm;
	private SceneProposalController proposalController;
	
	/**
	 * Listeners that get triggered when a proposal is changed.
	 */
	private List<ILiveProposalChangedListener> proposalChangedListeners;
	
	private List<IVersionTurnedOnListener> versionTurnedOnListeners;
	private List<IVersionTurnedOffListener> versionTurnedOffListeners;

	/**
	 * key = proposalID
	 * value = versionID being displayed
	 */
	private HashMap<Integer, Integer> proposals;

	/**
	 * 
	 */
	private LiveProposalManager() {
		proposals = new HashMap<Integer, Integer>();
		proposalChangedListeners = new Vector<ILiveProposalChangedListener>();
		versionTurnedOnListeners = new Vector<IVersionTurnedOnListener>();
		versionTurnedOffListeners = new Vector<IVersionTurnedOffListener>();
	}
	
	public synchronized void turnVersionOn(Design design){
		//SceneGameState.getInstance().removeDesignFromDisplay(320);
		
		
		// if this version is already on, leave the function
		if(proposals.containsValue(design.getID())){
			return;
		}
		
		logger.debug("Turning on a " + design.getClassification() + " named " + design.getName());
		
		int whatToRemove=0;
		
		printProposals();
		
		// If there is a version from this proposal already on, we will remove it
		if(design.isVersion() && proposals.containsKey(design.getSourceID())){
			logger.info("A "+design.getClassification()+" is already open");
			whatToRemove = proposals.get(design.getSourceID());
			logger.info("A version is already open: " + whatToRemove);
		}
		// We will also remove the first version if it is on
		else if(design.isProposal() && proposals.containsKey(design.getID())){
			whatToRemove=proposals.get(design.getID());
			logger.info("A proposal is already open: " + whatToRemove);
		}
		
		// last attempt here
		
		// If we need to remove anything, do it here
		if(whatToRemove!=0){
			logger.debug("Turning off " + whatToRemove);
			turnVersionOff(whatToRemove);
		}
		
		
		// Now that the scene is cleaned up a bit, turn on the version
		if(proposalController.addDesignToScene(design)){
			
			for(ILiveProposalChangedListener listener : proposalChangedListeners){
				listener.isChanged(design.getSourceID());
			}
			
			
			for(IVersionTurnedOnListener onListener : versionTurnedOnListeners){
				onListener.versionTurnedOn(design);
			}
			
			
			// Add the design to SceneScape and then set the target spatial
			SceneScape.getCity().addDesign(design);
			Spatial s = SceneGameState.getInstance().getSpecificDesign(design.getID());
			if(s!=null){
				SceneScape.setTargetSpatial(s);
				if(design.isVersion()){
					proposals.put(design.getSourceID(), design.getID());
				}
				else if(design.isProposal()){
					proposals.put(design.getID(), design.getID());
				}
			}
			else{
				logger.error("Could not find spatial in scene");
			}
			
		}
	}
	
	public synchronized void turnAllVersionsOff(){
		for(Entry<Integer, Integer> proposal : proposals.entrySet()){
			turnProposalOff(proposal.getValue());
		}
	}
	
	public void printProposals(){
		if(proposals.entrySet().isEmpty()){
			logger.debug("No Proposals Being Viewed");
			return;
		}
		for(Entry<Integer,Integer> e : proposals.entrySet()){
			logger.debug("Proposal: " + e.getKey() + " | Version: " + e.getValue());
		}
	}
	
	public synchronized void turnVersionOff(int versionID){
		if(proposals.containsValue(versionID)){
			Design remove = SceneScape.getCity().findDesignByID(versionID);
			proposalController.removeDesignFromScene(remove);
			proposals.remove(versionID);
			SceneScape.clearTargetSpatial();
			
			for(IVersionTurnedOffListener offListener : versionTurnedOffListeners){
				offListener.versionTurnedOff(remove);
			}
		}
	}

	public synchronized void turnProposalOff(int proposalID){
		if(proposals.containsKey(proposalID)){
			Design remove = SceneScape.getCity().findDesignByID(proposals.get(proposalID));
			proposalController.removeDesignFromScene(remove);
			proposals.remove(proposalID);
			SceneScape.clearTargetSpatial();
			
			for(IVersionTurnedOffListener offListener : versionTurnedOffListeners){
				offListener.versionTurnedOff(remove);
			}
		}
	}
	
	public boolean isVersionOn(int versionID){
		return proposals.containsValue(versionID);
	}

	public void registerProposalController(SceneProposalController controller){
		proposalController = controller;
	}
	
	public SceneProposalController getProposalController(){
		return proposalController;
	}
	
	public void addProposalChangedListener(ILiveProposalChangedListener listener){
		proposalChangedListeners.add(listener);
	}
	
	public void removeProposalChangedListener(ILiveProposalChangedListener listener){
		proposalChangedListeners.remove(listener);
	}
	
	public void removeAllProposalChangedListeners(){
		proposalChangedListeners.clear();
	}
	
	public void addVersionTurnedOnListener(IVersionTurnedOnListener listener){
		versionTurnedOnListeners.add(listener);
	}
	
	public void removeVersionTurnedOnListener(IVersionTurnedOnListener listener){
		versionTurnedOnListeners.remove(listener);
	}
	
	public void addVersionTurnedOffListener(IVersionTurnedOffListener listener){
		versionTurnedOffListeners.add(listener);
	}
	
	public void removeVersionTurnedOffListener(IVersionTurnedOffListener listener){
		versionTurnedOffListeners.remove(listener);
	}
	
	public HashMap<Integer, Integer> getProposals(){
		return proposals;
	}
	
	public static LiveProposalManager getInstance(){
		if(lpm==null) lpm = new LiveProposalManager();
		return lpm;
	}
}
