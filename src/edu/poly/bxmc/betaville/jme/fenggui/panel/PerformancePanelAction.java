/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.panel;

import org.apache.log4j.Logger;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.Labels;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.fenggui.PerformanceWindow;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.FrameSyncModule;
import edu.poly.bxmc.betaville.module.Module;
import edu.poly.bxmc.betaville.module.ModuleNameException;

/**
 * @author Skye Book
 *
 */
public class PerformancePanelAction extends OnOffPanelAction {
	private static final Logger logger = Logger.getLogger(PerformancePanelAction.class);

	public PerformancePanelAction() {
		super(Labels.get(PerformancePanelAction.class, "title"), "Performance Data", AvailabilityRule.ALWAYS, UserType.MEMBER, false, PerformanceWindow.class, false);

		BetavilleNoCanvas.addCompletionListener(new IAppInitializationCompleteListener() {

			public void applicationInitializationComplete() {
				try {
					SceneGameState.getInstance().addModuleToUpdateList(new PerfUpdater());
				} catch (ModuleNameException e) {
					logger.error("Bad Module Name!", e);
				}
			}
		});
	}
	
	private class PerfUpdater extends Module implements FrameSyncModule{

		public PerfUpdater() {
			super("PerformanceUpdater", "Updates the performance stats");
		}

		public void frameUpdate(float timePerFrame) {
			// only update the performance window if its in the display list
			if(associatedWindow.isInWidgetTree()){
				((PerformanceWindow)associatedWindow).updateCounts(timePerFrame);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
		 */
		public void deconstruct() {}
	}

}
