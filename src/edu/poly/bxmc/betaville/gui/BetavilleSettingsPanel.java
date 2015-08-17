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
package edu.poly.bxmc.betaville.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import com.jme.system.GameSettings;
import com.jme.system.lwjgl.LWJGLSystemProvider;

import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.xml.PreferenceWriter;

/**
 * This is an alternative implementation to the GameSettingsPanel
 * provided by jME.  It will not only remove options that aren't
 * required but also offers the potential to include Betaville-
 * specific settings.
 * @author Skye Book
 * @experimental This is not yet a functional component and will cause errors if used.
 *
 */
public class BetavilleSettingsPanel extends JFrame{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(BetavilleSettingsPanel.class);
	private ArrayList<Dimension> resolutions;

	private JComboBox fullScreen;
	private JComboBox resolutionSelector;
	private JComboBox texturedSelector;
	private JCheckBox alwaysShow;

	private GridBagConstraints c;

	private JButton okButton;
	private JButton cancelButton;

	private KeyListener enterButtonListener;

	/*
	 * KEEP:
	 * resolution
	 * fullscreen
	 * 
	 * add:
	 * textures on/off
	 */

	public BetavilleSettingsPanel(final GameSettings settings){
		enterButtonListener = new KeyListener() {

			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					okAction(settings);
				}
			}
		};
		settings.setRenderer(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		settings.setDepth(32);
		settings.setVerticalSync(true);
		settings.setFrequency(60);
		settings.setDepthBits(8);
		settings.setAlphaBits(0);
		settings.setStencilBits(0);
		settings.setSamples(0);
		settings.setMusic(false);
		settings.setSFX(false);

		createFullScreen();
		createResolutions();
		updateResolutions();
		createTextured();

		alwaysShow = new JCheckBox();
		alwaysShow.setSelected(SettingsPreferences.alwaysShowSettings());
		alwaysShow.setToolTipText("If selected, this settings panel will be shown on startup");

		c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.gridx=1;
		c.gridy=0;

		c.fill=GridBagConstraints.HORIZONTAL;

		setLayout(new GridBagLayout());
		add(fullScreen, c);
		c.gridx=0;
		add(new JLabel(Labels.get(this.getClass(), "fullscreen")), c);
		c.gridx=1;
		c.gridy+=1;
		add(resolutionSelector, c);
		c.gridx=0;
		add(new JLabel(Labels.get(this.getClass(), "resolution")), c);
		c.gridx=1;
		c.gridy+=1;
		add(texturedSelector, c);
		c.gridx=0;
		add(new JLabel(Labels.get(this.getClass(), "load_textures")), c);
		c.gridx=1;
		c.gridy+=1;
		// 2 December 2014, changed to not show the checkbox or label for alwaysShow since we always show the dialog now
		//add(alwaysShow, c);
		c.gridx=0;
		//add(new JLabel(Labels.get(this.getClass(), "always_show")), c);
		c.gridx=1;

		okButton = new JButton(Labels.get("Generic.ok"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okAction(settings);
			}
		});
		okButton.addKeyListener(enterButtonListener);

		cancelButton = new JButton(Labels.get("Generic.cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});

		c.gridx=0;
		c.gridy+=2;
		add(okButton, c);
		c.gridx+=1;
		add(cancelButton, c);
	}

	protected void okAction(GameSettings settings){
		settings.setWidth((int) ((ResComboItem)resolutionSelector.getSelectedItem()).getDimension().getWidth());
		settings.setHeight((int) ((ResComboItem)resolutionSelector.getSelectedItem()).getDimension().getHeight());
		System.setProperty("betaville.display.resolution", (int) ((ResComboItem)resolutionSelector.getSelectedItem()).getDimension().getWidth()+"x"+
				(int) ((ResComboItem)resolutionSelector.getSelectedItem()).getDimension().getHeight());
		boolean fs = ((String)fullScreen.getSelectedItem()).equals(Labels.generic("on"));

		// only allow fullscreen if we are using the native resolution
		if(fs){
			Dimension selected = ((ResComboItem)resolutionSelector.getSelectedItem()).getDimension();
			if(selected.getWidth() != Toolkit.getDefaultToolkit().getScreenSize().getWidth() ||
					selected.getHeight() != Toolkit.getDefaultToolkit().getScreenSize().getHeight()){
				JDialog error = new JDialog();
				error.setTitle("Betaville");
				error.setLayout(new GridBagLayout());
				GridBagConstraints errorC = new GridBagConstraints();
				errorC.insets = new Insets(3,3,3,3);
				errorC.gridx=0;
				errorC.gridy=0;
				error.add(new JLabel("Fullscreen Only Allowed"), errorC);
				errorC.gridy=1;
				error.add(new JLabel("for Native Resolution"), errorC);
				error.setSize(175, 100);
				error.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2)-(error.getWidth()/2),
						(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2)-(error.getHeight()/2));
				error.setVisible(true);
				error.setAlwaysOnTop(true);
				return;
			}
		}
		settings.setFullscreen(fs);
		System.setProperty("betaville.display.fullscreen", Boolean.toString(((String)fullScreen.getSelectedItem()).equals(Labels.generic("on"))));
		System.setProperty("betaville.display.textured", Boolean.toString(((String)texturedSelector.getSelectedItem()).equals(Labels.generic("on"))));
		System.setProperty("betaville.startup.showsettings", Boolean.toString(alwaysShow.isSelected()));
		PreferenceWriter pr;
		try {
			pr = new PreferenceWriter();
			pr.writeData();
		} catch (IOException e) {
			logger.error("A preferences file could not be created in the Betaville directory.  " +
					"Please ensure that you're home directory has write-permissions " +
			"enabled.  Betaville will run but your preferences will not be saved.");
		}

		setVisible(false);
	}

	protected void createFullScreen(){
		fullScreen = new JComboBox();
		fullScreen.setName("Full Screen");
		fullScreen.addItem(Labels.generic("on"));
		fullScreen.addItem(Labels.generic("off"));
		fullScreen.setSelectedIndex(SettingsPreferences.isFullscreen() ? 0 : 1);
		fullScreen.addKeyListener(enterButtonListener);
	}

	protected void createResolutions(){
		resolutionSelector = new JComboBox();
		resolutionSelector.setName(Labels.get(this.getClass(), "resolution"));
		resolutionSelector.addKeyListener(enterButtonListener);
		resolutionSelector.addActionListener(new resolutionChangedListener());
		resolutions = new ArrayList<Dimension>();
	}

	protected void createTextured(){
		texturedSelector = new JComboBox();
		texturedSelector.setName("Textures");
		texturedSelector.addItem(Labels.generic("on"));
		texturedSelector.addItem(Labels.generic("off"));
		texturedSelector.setSelectedIndex(SettingsPreferences.isTextured() ? 0 : 1);
		texturedSelector.addKeyListener(enterButtonListener);
	}

	protected void updateResolutions(){
		resolutions.clear();

		if((fullScreen.getSelectedItem().toString().equals(Labels.generic("on")))){
			if((Toolkit.getDefaultToolkit().getScreenSize().getWidth()/Toolkit.getDefaultToolkit().getScreenSize().getHeight())==1.6){
				// 16:10 Aspect Ratio
				resolutions.add(new Dimension(768,590));
				resolutions.add(new Dimension(1024,640));			
				resolutions.add(new Dimension(1280,800));			
				resolutions.add(new Dimension(1440,900));
				resolutions.add(new Dimension(1680,1050));
				resolutions.add(new Dimension(1920,1200));
				resolutions.add(new Dimension(2560,1600));
			}
			else if(((Toolkit.getDefaultToolkit().getScreenSize().getWidth()/Toolkit.getDefaultToolkit().getScreenSize().getHeight())>1.6) &&
					((Toolkit.getDefaultToolkit().getScreenSize().getWidth()/Toolkit.getDefaultToolkit().getScreenSize().getHeight())<1.95)){
				// 16:9 Aspect Ratio
				resolutions.add(new Dimension(1024,576));
				resolutions.add(new Dimension(1280,720));
				resolutions.add(new Dimension(1366,768));
				resolutions.add(new Dimension(1600,900));
				resolutions.add(new Dimension(1920,1080));
				resolutions.add(new Dimension(2048,1152));
				resolutions.add(new Dimension(2560,1440));
			}
			else if(((Toolkit.getDefaultToolkit().getScreenSize().getWidth()/Toolkit.getDefaultToolkit().getScreenSize().getHeight())>1.3) &&
					((Toolkit.getDefaultToolkit().getScreenSize().getWidth()/Toolkit.getDefaultToolkit().getScreenSize().getHeight())<1.4) ||
					((Toolkit.getDefaultToolkit().getScreenSize().getWidth()/Toolkit.getDefaultToolkit().getScreenSize().getHeight())==1.25)){
				// 4:3 Aspect Ratio
				resolutions.add(new Dimension(640,480));
				resolutions.add(new Dimension(800,600));
				resolutions.add(new Dimension(1024,768));
				// Alternative SXGA resolution (1280x960)
				resolutions.add(new Dimension(1280,960));
				resolutions.add(new Dimension(1280,1024));
				resolutions.add(new Dimension(1600,1200));
			}
		}
		else{
			// full screen can support any resolution as long as it isn't larger than the screen
			resolutions.add(new Dimension(640,480));
			resolutions.add(new Dimension(768,590));
			resolutions.add(new Dimension(800,600));
			resolutions.add(new Dimension(1024,576));
			resolutions.add(new Dimension(1024,640));
			resolutions.add(new Dimension(1024,768));
			resolutions.add(new Dimension(1280,720));
			resolutions.add(new Dimension(1280,800));
			resolutions.add(new Dimension(1280,960));
			resolutions.add(new Dimension(1280,1024));
			resolutions.add(new Dimension(1366,768));
			resolutions.add(new Dimension(1440,900));
			resolutions.add(new Dimension(1600,900));
			resolutions.add(new Dimension(1600,1200));
			resolutions.add(new Dimension(1680,1050));
			resolutions.add(new Dimension(1920,1080));
			resolutions.add(new Dimension(1920,1200));
			resolutions.add(new Dimension(2048,1152));
			resolutions.add(new Dimension(2560,1440));
			resolutions.add(new Dimension(2560,1600));
		}

		// take out resolutions larger than the screen
		Iterator<Dimension> it = resolutions.iterator();
		while(it.hasNext()){
			Dimension d = it.next();
			if(d.getWidth() > Toolkit.getDefaultToolkit().getScreenSize().getWidth() ||
					d.getHeight() > Toolkit.getDefaultToolkit().getScreenSize().getHeight()){
				it.remove();
			}
		}

		// add to combobox
		for(Dimension resolution : resolutions){
			resolutionSelector.addItem(new ResComboItem(resolution));
		}

		// try to set the currently selected resolution if there is one
		String resString = SettingsPreferences.getResolution();
		if(resString!=null){
			// search for the resolution supplied in the file
			String[] resSplit = resString.split("x");
			try{
			Dimension setRes = new Dimension(Integer.parseInt(resSplit[0]), Integer.parseInt(resSplit[1]));
			for(int i=0; i<resolutionSelector.getItemCount(); i++){
				ResComboItem item = (ResComboItem) resolutionSelector.getItemAt(i);
				if(item.dimension.width==setRes.width && item.dimension.height==setRes.height){
					resolutionSelector.setSelectedIndex(i);
					break;
				}
			}
			}catch(NumberFormatException e){
				resolutionSelector.setSelectedIndex(0);
			}
		}
	}
	
	private class resolutionChangedListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			int resolutionIndex = resolutionSelector.getSelectedIndex();
			if(resolutions.get(resolutionIndex).getHeight() != Toolkit.getDefaultToolkit().getScreenSize().getHeight() || resolutions.get(resolutionIndex).getWidth() != (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()){
				fullScreen.setSelectedIndex(1);
			}
		}
	}

	private class ResComboItem{
		private Dimension dimension;
		public ResComboItem(Dimension dimension){
			this.dimension=dimension;
		}

		public Dimension getDimension(){
			return dimension;
		}

		public String toString(){
			return (int)dimension.getWidth()+"x"+(int)dimension.getHeight();
		}
	}

	public static final boolean prompt(GameSettings settings, String title) throws InterruptedException {
		BetavilleSettingsPanel bsp = new BetavilleSettingsPanel(settings);
		bsp.setTitle(Labels.get(BetavilleSettingsPanel.class, "title"));
		bsp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bsp.setAlwaysOnTop(true);
		bsp.setSize(300, 250);
		bsp.setResizable(true);
		bsp.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2)-(bsp.getWidth()/2),
				(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2)-(bsp.getHeight()/2));

		bsp.setVisible(true);
		
		// Wait for finish before returning
		while (bsp.isVisible()) {
			Thread.sleep(50);
		}
		return true;
	}
}
