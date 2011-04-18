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
package edu.poly.bxmc.betaville.net;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;
import edu.poly.bxmc.betaville.updater.AbstractUpdater;

/** Maintains the network connections throughout the application so that
 * unnecessary connections aren't created.
 * @author Skye Book
 *
 */
public class NetPool extends AbstractUpdater{
	private static Logger logger = Logger.getLogger(NetPool.class);
	private static NetPool netPool = new NetPool(90000);
	
	private Vector<ClientManager> managers;
	
	private boolean autoCleanupEnabled=true;
	
	private List<Module> modules;
	
	private boolean isInUpdate = false;
	
	/**
	 * 
	 */
	private NetPool(long interval){
		super(interval);
		managers = new Vector<ClientManager>();
		
		modules = new ArrayList<Module>();
	}

	public ClientManager getConnection(){

		// If there are no managers, create one and return it
		if(managers.isEmpty()){
			logger.debug("No " + InsecureClientManager.class.getName() + " was found.. creating one");
			InsecureClientManager icm = new InsecureClientManager(modules);
			managers.add(icm);
			return icm;
		}
		
		// Where there is a list of managers, find an idle manager and return it
		for(ClientManager m : managers){
			if(!m.isBusy() && m instanceof InsecureClientManager){
				return m;
			}
		}
		
		// If no idle managers were found, then we add a new manager to the pool and return that
		logger.debug("No idle " + InsecureClientManager.class.getName() + " was found.. creating one");
		InsecureClientManager icm = new InsecureClientManager(modules);
		managers.add(icm);
		return icm;
	}
	
	public SecureClientManager getSecureConnection(){
		// If there are no managers, create one and return it
		if(managers.isEmpty()){
			logger.debug("No " + SecureClientManager.class.getName() + " was found.. creating one");
			SecureClientManager scm;
			if(SettingsPreferences.useSSL()) scm = new SSLClientManager(modules);
			else scm = new SecureClientManager(modules, true);
			managers.add(scm);
			return scm;
		}
		
		// Where there is a list of managers, find an idle manager and return it
		for(ClientManager m : managers){
			if(!m.isBusy() && m instanceof SecureClientManager){
				return (SecureClientManager)m;
			}
		}
		
		// If no idle managers were found, then we add a new manager to the pool and return that
		logger.debug("No idle " + SecureClientManager.class.getName() + " was found.. creating one");
		SecureClientManager scm;
		if(SettingsPreferences.useSSL()) scm = new SSLClientManager(modules);
		else scm = new SecureClientManager(modules, true);
		managers.add(scm);
		return scm;
	}
	
	public synchronized void cleanAll(){
		try{
		for(ClientManager manager : managers){
			while(manager.isBusy()){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			manager.close();
			managers.remove(manager);
		}
		if(managers.size()==0) logger.info("All Network Connections Closed");
		}catch(ConcurrentModificationException e){
			logger.info("ConcurrentModificationException");
		}
	}
	
	public void addModuleToUpdateList(Module module) throws ModuleNameException{
		for(Module m : modules){
			if(module.getName().toLowerCase().equals(m.getName().toLowerCase())){
				throw new ModuleNameException("Module '" + module.getName() + "' requires a unique name");
			}
		}
		modules.add(module);
	}
	
	public void removeModuleFromUpdateList(int moduleIndex){
		modules.remove(modules.indexOf(moduleIndex));
	}
	
	public static NetPool getPool(){
		return netPool;
	}

	public void doUpdate(){
		isInUpdate=true;
		logger.debug("Performing network connection cleanup ("+managers.size()+" currently)");
		int startingTotal = managers.size();
		// This is reserved for the first idle connection we come across, it will be saved
		int firstIdle=-1;

		for(int i=0; i<managers.size(); i++){
			if(!managers.get(i).isBusy()){
				// If this is the first idle connection we encounter, save it
				if(firstIdle==-1){
					firstIdle=i;
				}
				else{
					// Remove extra idle managers
					logger.debug("Closing " + managers.get(i).getClass().getName());
					managers.get(i).close();
					managers.remove(i);
					if(i==managers.size()-1){
						break;
					}
					else{
						i--;
					}
				}
			}
		}

		logger.info("Network cleanup complete: " + (startingTotal-managers.size()) + " connections dropped");
		isInUpdate=false;
	}

	public boolean isUpdateRequired() {
		return autoCleanupEnabled;
	}

	@Override
	public boolean isInUpdate() {
		return isInUpdate;
	}
}
