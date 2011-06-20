/**
 * 
 */
package edu.poly.bxmc.betaville.module;

import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.intersections.TranslatorMousePick;

/**
 * @author skyebook
 *
 */
public class TranslateModule extends Module implements LocalSceneModule {
	private TranslatorMousePick pick;
	
	private boolean wasClickedPreviously=false;
	
	private int mouseXLastClick = -1;
	private int mouseYLastClick = -1;
	
	private int mouseNewX = 0;
	private int mouseNewY = 0;

	private int checkPick = -1;
	private final int xAxisPicked = 1;
	private final int yAxisPicked = 2;
	private final int zAxisPicked = 3;

	/**
	 * @param name
	 */
	public TranslateModule(String name) {
		super("Translate Picker");
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#initialize(com.jme.scene.Node)
	 */
	public void initialize(Node scene) {
		pick = new TranslatorMousePick(scene);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#onUpdate(com.jme.scene.Node, com.jme.math.Vector3f, com.jme.math.Vector3f)
	 */
	public void onUpdate(Node scene, Vector3f cameraLocation,
			Vector3f cameraDirection) {
		
		
		
		if(pick.checkPick() != -1){
			checkPick = pick.checkPick();
			if(MouseInput.get().isButtonDown(0)){
				// do the drag
				
				if(mouseXLastClick==-1 || mouseYLastClick==-1){
					mouseXLastClick = MouseInput.get().getXAbsolute();
					mouseYLastClick = MouseInput.get().getYAbsolute();
					
					wasClickedPreviously = true;
					
				}
				else{
					
					if(wasClickedPreviously = true) {
						if(checkPick == xAxisPicked) {
							
							//make it so that the object can only move one way -- x-direction
							
							mouseNewX = mouseXLastClick - MouseInput.get().getXAbsolute();
							mouseNewY = mouseYLastClick - MouseInput.get().getYAbsolute();
							
							Vector3f target = SceneScape.getTargetSpatial().getLocalTranslation();
							
							float targetNewPos = (float)(Math.sqrt(Math.pow(mouseNewX, 2) + Math.pow(mouseNewY, 2)));
							
							SceneScape.getTargetSpatial().setLocalTranslation(target.x + targetNewPos, target.y, target.z);
							
						}
						
						else if(checkPick == yAxisPicked) {
							mouseNewX = mouseXLastClick - MouseInput.get().getXAbsolute();
							mouseNewY = mouseYLastClick - MouseInput.get().getYAbsolute();
							
							Vector3f target = SceneScape.getTargetSpatial().getLocalTranslation();
							
							float targetNewPos = (float)(Math.sqrt(Math.pow(mouseNewX, 2) + Math.pow(mouseNewY, 2)));
							
							SceneScape.getTargetSpatial().setLocalTranslation(target.x, target.y + targetNewPos, target.z);
							
						}
						
						else if(checkPick == zAxisPicked) {
							mouseNewX = mouseXLastClick - MouseInput.get().getXAbsolute();
							mouseNewY = mouseYLastClick - MouseInput.get().getYAbsolute();
							
							Vector3f target = SceneScape.getTargetSpatial().getLocalTranslation();
							
							float targetNewPos = (float)(Math.sqrt(Math.pow(mouseNewX, 2) + Math.pow(mouseNewY, 2)));
							
							SceneScape.getTargetSpatial().setLocalTranslation(target.x, target.y, target.z + targetNewPos);
							
						}
						
						wasClickedPreviously = false;
					}
					else {
						mouseXLastClick = MouseInput.get().getXAbsolute();
						mouseYLastClick = MouseInput.get().getYAbsolute();
						
						wasClickedPreviously = true;
					}
				}
				
				//wasClickedPreviously = true;
			}
			}
		else{
			wasClickedPreviously = false;
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
	 */
	public void deconstruct() {
		// TODO Auto-generated method stub

	}

}
