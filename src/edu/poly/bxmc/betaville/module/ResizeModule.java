package edu.poly.bxmc.betaville.module;

import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

import edu.poly.bxmc.betaville.SceneScape;
import edu.poly.bxmc.betaville.jme.intersections.ResizeMousePick;
import edu.poly.bxmc.betaville.jme.intersections.TranslatorMousePick;

/**
 * 
 * @author Vivian(Hyun) Park
 *
 */
public class ResizeModule extends Module implements LocalSceneModule {
	private ResizeMousePick pick;
	
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
	public ResizeModule(String name) {
		super("Resize Picker");
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#initialize(com.jme.scene.Node)
	 */
	public void initialize(Node scene) {
		pick = new ResizeMousePick(scene);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#onUpdate(com.jme.scene.Node, com.jme.math.Vector3f, com.jme.math.Vector3f)
	 */
	public void onUpdate(Node scene, Vector3f cameraLocation,
			Vector3f cameraDirection) {
		
		
		
		if(pick.checkPick() != -1){
			checkPick = pick.checkPick();
			if(MouseInput.get().isButtonDown(0)){
				// get the new size of the builidng by subtracting two coordinates x and y
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
	 */
	public void deconstruct() {
		// TODO Auto-generated method stub

	}

}
