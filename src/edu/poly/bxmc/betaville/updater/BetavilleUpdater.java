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
package edu.poly.bxmc.betaville.updater;

import java.util.List;
import java.util.Timer;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * @author Skye Book
 *
 */
public class BetavilleUpdater extends Timer{
	private static Logger logger = Logger.getLogger(BetavilleUpdater.class);
	
	private List<BetavilleTask> tasks;
	
	/**
	 * Creates a new update timer.
	 */
	public BetavilleUpdater(){
		super();
		tasks = new Vector<BetavilleTask>();
	}
	
	public void addTask(BetavilleTask task){
		scheduleAtFixedRate(task, task.getUpdater().getUpdateInterval(), task.getUpdater().getUpdateInterval());
		tasks.add(task);
	}
	
	public void removeTask(BetavilleTask task){
		logger.info("Removing task from update schedule");
		task.cancel();
		tasks.remove(task);
	}
	
	public List<BetavilleTask> getTasks(){
		return tasks;
	}
	
	/**
	 * Dequeues all update tasks from the update timer.
	 * @see Timer#cancel()
	 */
	public void shutdown(){
		logger.info("Shutting down update timer");
		tasks.clear();
		cancel();
	}
}
