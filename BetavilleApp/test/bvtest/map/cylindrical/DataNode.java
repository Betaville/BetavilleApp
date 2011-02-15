/**
 * 
 */
package bvtest.map.cylindrical;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.system.DisplaySystem;

/**
 * @author Skye Book
 *
 */
public class DataNode extends Node {
	private static final long serialVersionUID = 1L;

	private MaterialState materialState;

	private Box startingPoint;
	private Box endPoint;

	/**
	 * @param name
	 */
	public DataNode(String name, Vector3f spawnLocation, Vector3f landingLocation) {
		super(name);

		materialState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		materialState.setAmbient(ColorRGBA.brown);
		materialState.setDiffuse(ColorRGBA.brown);
		materialState.setEmissive(ColorRGBA.brown);
		materialState.setSpecular(ColorRGBA.brown);
		materialState.setMaterialFace(MaterialFace.FrontAndBack);


		// this should be a sort of rectangular shape
		startingPoint = new Box(name+"_startingPoint", new Vector3f(), 10, 5, 5);
		//startingPoint.setRenderState(materialState);
		attachChild(startingPoint);
		startingPoint.setLocalTranslation(spawnLocation);

		endPoint = new Box(name+"_endPoint", new Vector3f(), 5, 5, 5);
		//endPoint.setRenderState(materialState);
		attachChild(endPoint);
		endPoint.setLocalTranslation(landingLocation);

		updateRenderState();
		createLineLinks();
	}

	private void createLineLinks(){
		for(int i=0; i<4; i++){
			int num = endPoint.getVertexCount();
			Line line = new Line("Line "+i,
					new Vector3f[]{
					new Vector3f(startingPoint.getVertexBuffer().get(i*3),startingPoint.getVertexBuffer().get((i*3)+1),startingPoint.getVertexBuffer().get((i*3)+2)),
					new Vector3f(endPoint.getVertexBuffer().get((i*3)),endPoint.getVertexBuffer().get(((i*3)+1)),endPoint.getVertexBuffer().get(((i*3)+2)))
					//new Vector3f(endPoint.getVertexBuffer().get(num-(i*3)),endPoint.getVertexBuffer().get(num-((i*3)+1)),endPoint.getVertexBuffer().get(num-((i*3)+2)))
			},
			new Vector3f[]{Vector3f.UNIT_Y, Vector3f.UNIT_Y}, new ColorRGBA[]{ColorRGBA.gray, ColorRGBA.gray}, new Vector2f[]{new Vector2f(), new Vector2f()});
			attachChild(line);
			line.setLineWidth(3);
			line.setRenderState(materialState);
			line.updateRenderState();
		}
	}

}
