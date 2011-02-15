/**
 * 
 */
package bvtest.models;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;

import edu.poly.bxmc.betaville.jme.loaders.util.GeometryUtilities;

/**
 * Tests the mechanism that finds the extents of an object
 * @author Skye Book
 *
 */
public class TranslationAdjustment extends SimpleGame {

	/**
	 * 
	 */
	public TranslationAdjustment() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		Box b = new Box("box", new Vector3f(12,3,7), 60, 80, 97);
		Box b2 = new Box("box", new Vector3f(5,-10,16), 60, 80, 97);
		rootNode.attachChild(b);
		rootNode.attachChild(b2);
		Vector3f[] minMax = GeometryUtilities.findObjectExtents(rootNode);
		for(Vector3f location : minMax){
			Box marker = new Box("marker", location.clone(), 5, 5, 5);
			marker.setSolidColor(ColorRGBA.green);
			rootNode.attachChild(marker);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TranslationAdjustment ta = new TranslationAdjustment();
		ta.start();
	}

}
