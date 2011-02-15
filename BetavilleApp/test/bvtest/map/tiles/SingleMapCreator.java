/** Copyright (c) 2008-2011, Brooklyn eXperimental Media Center
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
package bvtest.map.tiles;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.math.Vector3f;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;

import edu.poly.bxmc.betaville.jme.map.BoundingBox;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;
import edu.poly.bxmc.betaville.jme.map.MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.net.UnexpectedServerResponse;
import edu.poly.bxmc.betaville.terrain.OSMTileRequestGenerator;
import edu.poly.bxmc.betaville.xml.USGSResponse;

/**
 * @author Skye Book
 *
 */
public class SingleMapCreator {

	private float[] heightMap;
	private float lowest;
	private float highest;
	private float scale;
	private TerrainBlock terrainBlock;

	private int completedElevationRequests=0;

	private ExecutorService threadPool = Executors.newFixedThreadPool(20);

	private ArrayList<UTMCoordinate> coordinateCache = new ArrayList<UTMCoordinate>();
	
	private BoundingBox boundingBox;

	/**
	 * 
	 */
	public SingleMapCreator(BoundingBox boundingBox, int numberPolls, URL imageURL){
		this.boundingBox=boundingBox;
		double distance = MapManager.greatCircleDistanced(boundingBox.getSw(), boundingBox.getSe());
		System.out.println("bounding box size: " + distance);
		heightMap = new float[numberPolls*numberPolls];



		// add tiles to cache
		UTMCoordinate transformable=boundingBox.getSw().getUTM();
		int distanceBetweenPolls = (int)(distance/numberPolls);
		/*
		for(int y=0; y<numberPolls; y++){
			for(int x=0; x<numberPolls; x++){
				coordinateCache.add(transformable.clone().move(x*distanceBetweenPolls, y*distanceBetweenPolls, 0));
			}
		}
		*/
		
		// flip the coordinates
		for(int y=numberPolls; y>0; y--){
			for(int x=0; x<numberPolls; x++){
				coordinateCache.add(transformable.clone().move(x*distanceBetweenPolls, y*distanceBetweenPolls, 0));
			}
		}

		// create empty height map
		heightMap = new float[coordinateCache.size()];

		// run elevation requests
		for(int i=0; i<coordinateCache.size(); i++){
			elevationQuery(i);
		}

		// wait for the elevation retrievals to finish
		while(completedElevationRequests<coordinateCache.size()){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// Yeah, whatever.
				e.printStackTrace();
			}
		}
		
		threadPool.shutdown();

		// at this point we have a file height map and its time to create the TerrainBlock
		terrainBlock = new TerrainBlock("terrain", numberPolls, new Vector3f(distanceBetweenPolls, 1, distanceBetweenPolls), heightMap, new Vector3f(0,(heightMap[0]*-1),0));
		//terrainBlock.setLocalRotation(Rotator.PITCH180);
		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		Texture t = TextureManager.loadTexture(imageURL, false);
		//t.setWrap(WrapMode.BorderClamp);
		//t.setRotation(Rotator.angleZ(90));
		//t.setTranslation(new Vector3f(1, 0, 0));
		//t.setScale(new Vector3f(-1,0,1));
		//t.setTranslation(new Vector3f(1,0,0));
		if(t!=null){
			ts.setTexture(t);
			terrainBlock.setRenderState(ts);
			terrainBlock.updateRenderState();
		}
	}
	
	public ILocation getSouthWestCorner(){
		return boundingBox.getSw();
	}

	/**
	 * Submits a coordinate to the threaded request pool.
	 * @param coordinate The index of the coordinate to retrieve from the coordinate cache.
	 */
	private void elevationQuery(final int coordinate){

		threadPool.submit(new Runnable(){
			public void run() {
				GPSCoordinate gps = coordinateCache.get(coordinate).getGPS();
				//System.out.println("Querying " + gps.toString());
				USGSResponse elevationResponse = new USGSResponse(gps.getLatitude(), gps.getLongitude());
				try {
					elevationResponse.parse();
				} catch (UnexpectedServerResponse e) {
					System.out.println(e.getMessage());
					return;
				}
				heightMap[coordinate] = (float)elevationResponse.getElevation();

				if(coordinate==0){
					lowest=heightMap[coordinate];
					highest=heightMap[coordinate];
				}
				else{
					if(heightMap[coordinate]<lowest) lowest=heightMap[coordinate];
					else if(heightMap[coordinate]>highest) highest=heightMap[coordinate];
				}

				incrementTotal();
			}
		});
	}

	private synchronized void incrementTotal(){
		completedElevationRequests++;
		if(completedElevationRequests%100==0){
			System.out.println(completedElevationRequests+"/"+(coordinateCache.size())+" complete");
		}
		if(completedElevationRequests==coordinateCache.size()-1){
			System.out.println("\nElevation retrieval complete!");
		}
	}

	public TerrainBlock getTerrainBlock(){
		return terrainBlock;
	}



	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		SimpleGame game = new SimpleGame(){

			@Override
			protected void simpleInitGame() {
				((FirstPersonHandler)input).getKeyboardLookHandler().setMoveSpeed(((FirstPersonHandler)input).getKeyboardLookHandler().getMoveSpeed());
				display.getRenderer().getCamera().setFrustumFar(2000);
				OSMTileRequestGenerator generator = new OSMTileRequestGenerator(18);
				String url = generator.generateTileRequest(40.644, -74.025);
				url = generator.generateTileRequest(36.1070, -113.216);
				System.out.println(url);
				SingleMapCreator creator;
				try {
					creator = new SingleMapCreator(generator.getCurrentBoundingBox(), 100, new URL(url));
					rootNode.attachChild(creator.getTerrainBlock());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		};
		game.start();
	}

}
