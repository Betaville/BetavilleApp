/** Copyright (c) 2008-2012, Brooklyn eXperimental Media Center
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
package edu.poly.bxmc.betaville.jme;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.fenggui.FengGUI;
import org.jdom.JDOMException;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;
import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.input.MouseInput;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.LoadingGameState;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.KioskMode;
import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.flags.DesktopFlagPositionStrategy;
import edu.poly.bxmc.betaville.flags.FlagProducer;
import edu.poly.bxmc.betaville.gui.AboutWindow;
import edu.poly.bxmc.betaville.gui.BetavilleSettingsPanel;
import edu.poly.bxmc.betaville.gui.CitySelector;
import edu.poly.bxmc.betaville.gui.CitySelector.CitySelectedCallback;
import edu.poly.bxmc.betaville.gui.SwingLoginWindow;
import edu.poly.bxmc.betaville.jme.fenggui.CreateKioskPasswordPrompt;
import edu.poly.bxmc.betaville.jme.gamestates.GUIGameState;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.gamestates.ShadowPassState;
import edu.poly.bxmc.betaville.jme.gamestates.SoundGameState;
import edu.poly.bxmc.betaville.jme.loaders.util.DriveFinder;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.logging.LogManager;
import edu.poly.bxmc.betaville.model.City;
import edu.poly.bxmc.betaville.model.Wormhole;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.net.NetModelLoader;
import edu.poly.bxmc.betaville.net.NetPool;
import edu.poly.bxmc.betaville.net.NetModelLoader.LookupRoutine;
import edu.poly.bxmc.betaville.obstructions.ObstructionManager;
import edu.poly.bxmc.betaville.proposals.LiveProposalManager;
import edu.poly.bxmc.betaville.terrain.TerrainLoader;
import edu.poly.bxmc.betaville.updater.BetavilleTask;
import edu.poly.bxmc.betaville.updater.BetavilleUpdater;
import edu.poly.bxmc.betaville.updater.KioskUpdater;
import edu.poly.bxmc.betaville.util.OS;
import edu.poly.bxmc.betaville.xml.BXBReader;
import edu.poly.bxmc.betaville.xml.PreferenceReader;
import edu.poly.bxmc.betaville.xml.UpdatedPreferenceWriter;

/**
 * Betaville's main class. Be sure to run this with the VM Argument -Xmx512M
 * 
 * @author Skye Book
 * 
 */
public class BetavilleNoCanvas {
	private static Logger logger = Logger.getLogger(BetavilleNoCanvas.class);

	private static GUIGameState guiGameState;
	private static SceneGameState sceneGameState;
	private static ShadowPassState shadowPassState;
	private static SoundGameState soundGameState;

	private static StandardGame game;

	private static BetavilleUpdater betavilleUpdater;

	private static ArrayList<IAppInitializationCompleteListener> listeners;

	private static File fileOpenArgument = null;

	private static ILocation cameraStartPosition=null;

	private static boolean validateResolutionString(String resString) {
		return resString.matches("[0-9]+x[0-9]+");
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException {
		if (OS.isMac()) {
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name",
					"Betaville");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		try {
			if(OS.isMac()){
				//UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel());
				//System.setProperty("Quaqua.tabLayoutPolicy","wrap");
				//System.out.println("Quaqua Look and Feel for OS X assigned");
			}else{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		listeners = new ArrayList<IAppInitializationCompleteListener>();

		java.util.logging.Logger.getLogger("com.jme").setLevel(
				java.util.logging.Level.WARNING);

		betavilleUpdater = new BetavilleUpdater();

		game = new StandardGame("Betaville");
		game.setConfigShowMode(ConfigShowMode.AlwaysShow);

		game.getSettings().setMusic(false);
		game.getSettings().setSFX(false);
		game.getSettings().setFramerate(30);
		DisplaySystem.getDisplaySystem().setMinStencilBits(4);

		LogManager.setupLoggers();

		if (args != null) {
			logger.info("Main started with " + args.length + " arguments"+((args.length>0)?":":""));
			for (int i = 0; i < args.length; i++) {
				logger.info("Argument " + i + ": " + args[i]);
				if(args[i].equals("-open")){
					fileOpenArgument = new File(args[i+1]);
				}
			}
		}
		// get input files for os x
		if (System.getProperty("os.name").startsWith("Mac")) {
			Application appleApp = new Application();

			/*
			appleApp.setOpenFileHandler(new OpenFilesHandler() {

				public void openFiles(OpenFilesEvent arg0) {
					logger.info("open files");
					List<File> files = arg0.getFiles();
					for(File file : files){
						logger.info("Apple File Open Requested for: " + file.toString());
						fileOpenArgument = file;
					}
				}
			});
			 */

			appleApp.addApplicationListener(new ApplicationListener() {

				public void handleReOpenApplication(ApplicationEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void handleQuit(ApplicationEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void handlePrintFile(ApplicationEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void handlePreferences(ApplicationEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void handleOpenFile(ApplicationEvent arg0) {
					logger.info("Apple File Open Requested for: " + arg0.getFilename());
					fileOpenArgument=new File(arg0.getFilename());
				}

				public void handleOpenApplication(ApplicationEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void handleAbout(ApplicationEvent arg0){
					AboutWindow aw = new AboutWindow();
					aw.setVisible(true);
				}
			});
		}


		if(fileOpenArgument!=null){
			logger.info("Application opened with file: " + fileOpenArgument.toString());
			try {
				if(fileOpenArgument.toString().toLowerCase().endsWith("bxb")){
					BXBReader bxb = new BXBReader(fileOpenArgument);
					cameraStartPosition = bxb.getCoordinate();
				}
			} catch (JDOMException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}

		// warmup modules
		LiveProposalManager.getInstance();
		ObstructionManager.getInstance();

		// Try to load the preferences
		try {
			PreferenceReader preferenceReader = new PreferenceReader(new File(
					DriveFinder.getHomeDir().toString()
					+ "/.betaville/preferences.xml"));
			if (preferenceReader.isXMLLoaded()) {
				preferenceReader.parse();
				UpdatedPreferenceWriter.writeDefaultPreferences();
			} else
				logger.info("Preferences file not found");
		} catch (JDOMException e) {
			logger.error("JDOM error", e);
		} catch (IOException e) {
			try {
				logger.info("Preferences file could not be found, writing one.");
				UpdatedPreferenceWriter.writeDefaultPreferences();
			} catch (IOException e1) {
				logger.error("Preferences file could not be written", e1);
			}
		}

		// Check for Kiosk Mode
		addCompletionListener(new IAppInitializationCompleteListener() {

			public void applicationInitializationComplete() {

				if(!KioskMode.isInKioskMode()) return;

				// setup kiosk password
				if(KioskMode.isExitPasswordRequired() &&
						(KioskMode.getKioskPasswordHash()==null || KioskMode.getKioskPasswordHash().length()!=40)){
					logger.info("Application is in Kiosk Mode and requires a password, but none was set at startup");
					CreateKioskPasswordPrompt prompt = FengGUI.createWidget(CreateKioskPasswordPrompt.class);
					prompt.finishSetup();
					GUIGameState.getInstance().getDisp().addWidget(prompt);
				}
				else{
					logger.info("Application is in Kiosk Mode and requires a password");
				}


				// setup kiosk refresher
				if(KioskMode.getRefreshRate()>0){
					betavilleUpdater.addTask(new BetavilleTask(new KioskUpdater(1000, KioskMode.getRefreshRate())));
				}

			}
		});

		// If there's a bad resolution value, or its set to always show, show
		// the options prompt
		if (!validateResolutionString(System
				.getProperty("betaville.display.resolution"))
				|| SettingsPreferences.alwaysShowSettings()) {
			if (BetavilleSettingsPanel.prompt(game.getSettings(),
					"Betaville Settings")) {
				logger.warn("Display settings set");
			}
		} else {
			// load the resolution already there
			String[] widthHeight = SettingsPreferences.getResolution().split(
					"x");
			game.getSettings().setWidth(Integer.parseInt(widthHeight[0]));
			game.getSettings().setHeight(Integer.parseInt(widthHeight[1]));
		}

		if(!SettingsPreferences.guestMode()) SwingLoginWindow.prompt();
		else SettingsPreferences.setUserType(UserType.GUEST);


		try {
			CitySelector citySelector = new CitySelector();
			citySelector.setCitySelectedCallback(new CitySelectedCallback() {

				@Override
				public void onSelection(Wormhole wormhole) {
					MapManager.setUTMZone(wormhole.getLocation().getLonZone(), wormhole.getLocation().getLatZone());
					SettingsPreferences.setStartupCity(wormhole.getCityID());
					cameraStartPosition = wormhole.getLocation();
				}
			});

			citySelector.setVisible(true);

			while(citySelector.isVisible()){
				Thread.sleep(25);
			}
		} catch (HeadlessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		if(game.getSettings().isFullscreen()){
			// setup for undecorated window
			//game.getSettings().setFullscreen(false);
			//System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		}
		game.start();

		MouseInput.get().setCursorVisible(true);

		LoadingGameState transitionGameState = new LoadingGameState();
		transitionGameState.setName("transitionGameState");
		GameStateManager.getInstance().attachChild(transitionGameState);
		transitionGameState.setProgress(0, "Getting Design Data");
		transitionGameState.setActive(true);

		transitionGameState.setProgress(0.03f, "Loading Preferences");

		try {
			List<City> cities = NetPool.getPool().getConnection().findAllCities();
			int startupCity = SettingsPreferences.getStartupCity();
			for (City c : cities) {
				if (c.getCityID() == startupCity)
					SettingsPreferences.addCityAndSetToCurrent(c);
				else
					SettingsPreferences.addCity(c);
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		transitionGameState.setProgress(0.05f, "Initializing Scene");

		sceneGameState = new SceneGameState("sceneGameState", cameraStartPosition);
		GameStateManager.getInstance().attachChild(sceneGameState);
		sceneGameState.setActive(false);
		if (!SettingsPreferences.useGeneratedTerrainEnabled())
			try {
				NetModelLoader.loadCurrentCityTerrain();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else {
			SettingsPreferences.getThreadPool().submit(new Runnable() {
				public void run() {
					TerrainLoader t = new TerrainLoader(JME2MapManager.instance
							.betavilleToUTM(sceneGameState.getCamera()
									.getLocation()), 20, 10, 17, false);
					t.loadTerrain();
				}
			});
		}

		/*
		 * SketchPassState sketchPassState = null; try { sketchPassState =
		 * GameTaskQueueManager.getManager().update(new
		 * Callable<SketchPassState>() { public SketchPassState call() throws
		 * Exception { return new
		 * SketchPassState("sketchPassState",DisplaySystem
		 * .getDisplaySystem().getRenderer().getCamera()); } }).get(); } catch
		 * (ExecutionException e) { e.printStackTrace(); }
		 * GameStateManager.getInstance().attachChild(sketchPassState);
		 * sketchPassState.setActive(true);
		 */

		/*
		 * waterGameState = null; if(SettingsPreferences.isWaterOn()){
		 * waterGameState = new WaterGameState("waterGameState");
		 * GameStateManager.getInstance().attachChild(waterGameState);
		 * waterGameState.setActive(false); }
		 */

		transitionGameState.setProgress(0.81f, "Drawing Shadows");

		shadowPassState = new ShadowPassState("shadowPassState");
		GameStateManager.getInstance().attachChild(shadowPassState);
		shadowPassState.setActive(false);

		transitionGameState.setProgress(0.90f, "Setting Up Sounds");

		soundGameState = new SoundGameState("soundGameState");
		GameStateManager.getInstance().attachChild(soundGameState);
		soundGameState.setActive(false);

		// transitionGameState.setProgress(0.94f, "Making Pretty Water");

		transitionGameState.setProgress(0.97f, "Making Pretty Interface");

		guiGameState = new GUIGameState("guiGameState");
		GameStateManager.getInstance().attachChild(guiGameState);
		guiGameState.setActive(false);

		transitionGameState.setProgress(1, "All Done!");
		transitionGameState.setActive(false);

		// turn on desired gamestates
		sceneGameState.setActive(true);
		shadowPassState.setActive(true);
		soundGameState.setActive(true);
		// if(SettingsPreferences.isWaterOn()) waterGameState.setActive(true);
		guiGameState.setActive(true);

		// now that the application is loaded, we can run the completion
		// listeners
		for (IAppInitializationCompleteListener listener : listeners) {
			listener.applicationInitializationComplete();
		}

		if (SettingsPreferences.loadModelsOnStart()) {
			SettingsPreferences.getThreadPool().submit(new Runnable() {
				public void run() {
					long startTime = System.currentTimeMillis();
					try {
						// load the base model
						NetModelLoader.loadCurrentCity(LookupRoutine.ALL_IN_CITY);

						// load proposals
						FlagProducer testFlagger = new FlagProducer(cameraStartPosition.getUTM(), new DesktopFlagPositionStrategy());

						testFlagger.getProposals(30000);
						testFlagger.placeFlags();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					

					// enable framerate optimization
					//sceneGameState.setFramerateOptimizationEnabled(true);

					//betavilleUpdater.addTask(new BetavilleTask(new BaseUpdater(30000)));
					logger.info("Done loading models, took: "
							+ (System.currentTimeMillis() - startTime));
					betavilleUpdater.addTask(new BetavilleTask(NetPool
							.getPool()));
				}
			});
		}
	}

	public static void addCompletionListener(
			IAppInitializationCompleteListener listener) {
		listeners.add(listener);
	}

	public static BetavilleUpdater getUpdater() {
		return betavilleUpdater;
	}

	public static StandardGame getGame() {
		return game;
	}
}