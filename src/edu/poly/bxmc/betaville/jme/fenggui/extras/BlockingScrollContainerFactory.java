/**
 * 
 */
package edu.poly.bxmc.betaville.jme.fenggui.extras;

import org.fenggui.FengGUI;
import org.fenggui.IWidget;
import org.fenggui.ScrollContainer;
import org.fenggui.event.Event;
import org.fenggui.event.IGenericEventListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseEvent;
import org.fenggui.event.mouse.MouseExitedEvent;

import edu.poly.bxmc.betaville.IAppInitializationCompleteListener;
import edu.poly.bxmc.betaville.jme.BetavilleNoCanvas;
import edu.poly.bxmc.betaville.jme.controllers.MouseZoomAction.ZoomLock;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;

/**
 * Creates a scroll container that blocks mouse wheel scroll
 * from the jME scene.
 * @author Skye Book
 *
 */
public class BlockingScrollContainerFactory {


	public static ScrollContainer createBlockingScrollCotnainer(){
		ScrollContainer container = FengGUI.createWidget(ScrollContainer.class);

		final ScrollContainerZoomLock zl = new ScrollContainerZoomLock();

		System.out.println("adding zoom lock");

		container.addEventListener(IWidget.EVENT_MOUSE, new IGenericEventListener() {
			public void processEvent(Object arg0, Event arg1) {
				if(arg1 instanceof MouseEvent){
					if(arg1 instanceof MouseEnteredEvent){
						//System.out.println("zoom off");
						zl.isSafeToZoom=false;
					}
					else if(arg1 instanceof MouseExitedEvent){
						//System.out.println("zoom on");
						zl.isSafeToZoom=true;
					}
				}
			}
		});

		if(SceneGameState.getInstance()!=null){
			//System.out.println("SceneGameState is not null, adding here");
			SceneGameState.getInstance().getSceneController().getMouseZoom().addZoomLock(zl);
		}
		else{
			//System.out.println("SceneGameState is null, wait");
			// Wait until SceneGameState is accessible before getting the scene controller
			BetavilleNoCanvas.addCompletionListener(new IAppInitializationCompleteListener() {

				/*
				 * (non-Javadoc)
				 * @see edu.poly.bxmc.betaville.IAppInitializationCompleteListener#applicationInitializationComplete()
				 */
				public void applicationInitializationComplete() {
					SceneGameState.getInstance().getSceneController().getMouseZoom().addZoomLock(zl);
				}
			});
		}

		return container;
	}

	public static class ScrollContainerZoomLock implements ZoomLock{

		boolean isSafeToZoom=true;

		/*
		 * (non-Javadoc)
		 * @see edu.poly.bxmc.betaville.jme.controllers.MouseZoomAction.ZoomLock#zoomIsAllowed()
		 */
		public boolean zoomIsAllowed() {
			return isSafeToZoom;
		}

	}

}
