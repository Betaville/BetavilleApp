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
package edu.poly.bxmc.betaville.gui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.model.Wormhole;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.net.UnprotectedManager;

/**
 * @author Skye Book
 *
 */
public class CitySelector extends JFrame {
	private static final Logger logger = Logger.getLogger(CitySelector.class);
	private static final long serialVersionUID = 1L;

	private JList cityList;
	
	private CitySelectedCallback callback = null;
	
	/**
	 * @throws HeadlessException
	 */
	public CitySelector() throws HeadlessException {
		setLayout(new BorderLayout());
		final List<Wormhole> locations = NetPool.getPool().getConnection().getAllWormholes();

		UnprotectedManager net = NetPool.getPool().getConnection();
		DefaultListModel dlm = new DefaultListModel();
		for(Wormhole location : locations){
			String[] cityInfo = net.findCityByID(location.getCityID());
			dlm.addElement(((cityInfo!=null)?cityInfo[0]+" - ":"")+location.getName());
		}

		cityList = new JList(dlm);
		
		JButton selectButton = new JButton("Select");
		selectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// do nothing if an item has not been selected
				if(cityList.getSelectedIndex()==-1) return;
				
				// load this city
				Wormhole selectedLocation = locations.get(cityList.getSelectedIndex());
				logger.info("User selected wormhole: " + selectedLocation.getName());
				if(callback!=null) callback.onSelection(selectedLocation);
				setVisible(false);
			}
		});
		
		setSize(375, 210);
		setResizable(false);
		setLocation(((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2)-getWidth()/2,
				((int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2)-getHeight()/2);
		getContentPane().add(cityList, BorderLayout.NORTH);
		getContentPane().add(selectButton, BorderLayout.SOUTH);
	}
	
	public void setCitySelectedCallback(CitySelectedCallback callback){
		this.callback=callback;
	}
	
	public interface CitySelectedCallback{
		
		/**
		 * Called when a city, or location, is selected
		 * in {@link CitySelector}
		 * @param wormhole The selected wormhole
		 */
		public void onSelection(Wormhole wormhole);
	}
}
