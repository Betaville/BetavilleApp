/**
 * 
 */
package bvtest.map.tiles;

import com.jme.app.SimpleGame;
import com.jme.math.Vector2f;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jmex.terrain.TerrainBlock;

import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.terrain.FlatTerrainGenerator;
import edu.poly.bxmc.betaville.terrain.ITerrainCompletionListener;
import edu.poly.bxmc.betaville.terrain.MappedTerrainGenerator;
import edu.poly.bxmc.betaville.terrain.OSMTileRequestGenerator;
import edu.poly.bxmc.betaville.terrain.USGSTerrainGenerator;

/**
 * @author Skye Book
 *
 */
public class TerrainGeneratorTest extends SimpleGame {
	
	private MappedTerrainGenerator gen;
	private TerrainBlock tb;

	/**
	 * 
	 */
	public TerrainGeneratorTest() {
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		
		cam.setFrustumFar(1500);
		
		gen = new USGSTerrainGenerator(30, new GPSCoordinate(0, 40.773474, -73.890463), 1000);
		//gen = new FlatTerrainGenerator(new GPSCoordinate(0, 40, -74));
		gen.addTerrainCompletionListener(new ITerrainCompletionListener() {

			public void terrainGenerationComplete(Spatial terrainObject) {
				System.out.println("terrain generation complete");
				tb = (TerrainBlock)terrainObject;
				rootNode.attachChild(tb);
				System.out.println("Triangles: "+((TriMesh)terrainObject).getTriangleCount());
			}
		});
		gen.createTerrainBlock();
	}

	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		System.out.println(cam.getDirection());
		System.out.println("elevation: " + tb.getHeight(new Vector2f(cam.getLocation().x, cam.getLocation().z)));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TerrainGeneratorTest test = new TerrainGeneratorTest();
		test.start();
	}

}
