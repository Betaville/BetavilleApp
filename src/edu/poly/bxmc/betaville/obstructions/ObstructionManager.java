/**
 * 
 */
package edu.poly.bxmc.betaville.obstructions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.model.Design;
import edu.poly.bxmc.betaville.proposals.IVersionTurnedOffListener;
import edu.poly.bxmc.betaville.proposals.IVersionTurnedOnListener;
import edu.poly.bxmc.betaville.proposals.LiveProposalManager;

/**
 * @author Skye Book
 *
 */
public class ObstructionManager {
	private static Logger logger = Logger.getLogger(ObstructionManager.class);
	
	private static ObstructionManager manager = null;
	
	private List<IObstructionChangedListener> changedListeners;
	private List<IObstructionRemovedListener> removedListeners;
	private List<IObstructionReplacedListener> replacedListeners;
	
	private HashMap<Integer,List<Integer>> obstructions;

	/**
	 * 
	 */
	private ObstructionManager(){
		changedListeners = new ArrayList<IObstructionChangedListener>();
		removedListeners = new ArrayList<IObstructionRemovedListener>();
		replacedListeners = new ArrayList<IObstructionReplacedListener>();
		
		obstructions = new HashMap<Integer, List<Integer>>();
		
		LiveProposalManager.getInstance().addVersionTurnedOnListener(new IVersionTurnedOnListener() {
			public void versionTurnedOn(Design version) {
				performOnAction(version);
			}
		});
		
		LiveProposalManager.getInstance().addVersionTurnedOffListener(new IVersionTurnedOffListener() {
			public void versionTurnedOff(Design version) {
				logger.info(version.getName() + " turned off");
				for(int removable : version.getDesignsToRemove()){
					List<Integer> list = obstructions.get(removable);
					if(list==null) return;
					
					logger.info(removable + " has " + list.size() + " connection(s)");
					
					if(list.contains(version.getID())){
						if(list.size()==1){
							// remove it completely
							try {
								SceneGameState.getInstance().addDesignToDisplay(removable);
								obstructions.remove(removable);
								if(obstructions.get(removable)!=null){
									logger.error("key not removed");
								}
								else logger.info("key removal success");
							} catch (IOException e) {
								e.printStackTrace();
							} catch (URISyntaxException e) {
								e.printStackTrace();
							}
						}
						else{
							// remove it from here
							obstructions.get(removable).remove(version.getID());
						}
					}
				}
			}
		});
		
	}
	
	public void performOnAction(Design on){
		for(int removable : on.getDesignsToRemove()){
			if(!obstructions.containsKey(removable)){
				logger.info("new removable entry: " + removable);
				SceneGameState.getInstance().removeDesignFromDisplay(removable);
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(on.getID());
				obstructions.put(removable, list);
			}
			else{
				logger.info("adding connection to " + removable);
				obstructions.get(removable).add(on.getID());
			}
		}
	}
	
	public void performOffAction(){
		
	}
	
	// event listeners: add, remove, remove all

	public void addObstructionChangedListener(IObstructionChangedListener listener){
		changedListeners.add(listener);
	}
	
	public void removeObstructionChangedListener(IObstructionChangedListener listener){
		changedListeners.remove(listener);
	}
	
	public void removeAllChangedListeners(){
		changedListeners.clear();
	}
	
	public void addObstructionRemovedListener(IObstructionRemovedListener listener){
		removedListeners.add(listener);
	}
	
	public void removeObstructionRemovedListener(IObstructionRemovedListener listener){
		removedListeners.remove(listener);
	}
	
	public void removeAllObstructionRemovedListeners(){
		removedListeners.clear();
	}
	
	public void addObstructionReplacedListener(IObstructionReplacedListener listener){
		replacedListeners.add(listener);
	}
	
	public void removeObstructionReplacedListener(IObstructionReplacedListener listener){
		replacedListeners.remove(listener);
	}
	
	public void removeAllObstructionReplacedListeners(){
		replacedListeners.clear();
	}
	
	public static ObstructionManager getInstance(){
		if(manager==null) manager = new ObstructionManager();
		return manager;
	}
}
