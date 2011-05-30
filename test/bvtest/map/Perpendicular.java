/**
 * 
 */
package bvtest.map;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;

/**
 * @author Skye Book
 *
 */
public class Perpendicular extends SimpleGame {
	
	private Box center;
	private Box left;
	private Box right;

	/**
	 * 
	 */
	public Perpendicular() {
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		center = new Box("center", new Vector3f(), 1, 1, 1);
		left = new Box("left", new Vector3f(), 1, 1, 1);
		right = new Box("right", new Vector3f(), 1, 1, 1);
		center.setLocalTranslation(0, 0, 5);
		//rootNode.attachChild(center);
		left.setLocalTranslation(center.getLocalTranslation().cross(Vector3f.UNIT_Y));
		right.setLocalTranslation(center.getLocalTranslation().cross(Vector3f.UNIT_X));
		System.out.println(left.getLocalTranslation().toString());
		System.out.println(right.getLocalTranslation().toString());
		//rootNode.attachChild(left);
		//rootNode.attachChild(right);
		
		Vector3f start = new Vector3f();
		Vector3f end = new Vector3f(50, 0, 20);
		
		MaterialState ms = display.getRenderer().createMaterialState();
		ms.setAmbient(ColorRGBA.red);
		ms.setDiffuse(ColorRGBA.red);
		
		Box startingBox = new Box("start", new Vector3f(), 1, 1, 1);
		startingBox.setLocalTranslation(start);
		startingBox.setRenderState(ms);
		Box endingBox = new Box("end", new Vector3f(), 1, 1, 1);
		endingBox.setLocalTranslation(end);
		endingBox.setRenderState(ms);
		
		rootNode.attachChild(startingBox);
		rootNode.attachChild(endingBox);
		rootNode.updateRenderState();
		
		for(int i=1; i<10; i++){
			Vector3f thisLocation = start.clone();
			thisLocation.interpolate(end, .1f*i);
			Vector3f sideLoc = thisLocation.clone().normalizeLocal();
			sideLoc.add(-1f, 0, +1f);
			sideLoc.normalizeLocal();
			sideLoc.addLocal(thisLocation);
			//Vector3f sideLoc = thisLocation.clone().cross(Vector3f.UNIT_XYZ);
			Box a = new Box(i+"a", new Vector3f(), .5f, .5f, .5f);
			a.setLocalTranslation(sideLoc);
			rootNode.attachChild(a);
			System.out.println(thisLocation.toString());
			Box b = new Box(i+"", new Vector3f(), 1, 1, 1);
			b.setLocalTranslation(thisLocation);
			rootNode.attachChild(b);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Perpendicular p = new Perpendicular();
		p.start();
	}

}
