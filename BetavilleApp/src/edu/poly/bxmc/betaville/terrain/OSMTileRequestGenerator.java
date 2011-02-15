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
package edu.poly.bxmc.betaville.terrain;

import edu.poly.bxmc.betaville.jme.map.BoundingBox;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.ILocation;

/**
 * @author Skye Book
 *
 */
public class OSMTileRequestGenerator implements ITileRequestGenerator {
	
	public static final String TilesAtHomeServer = "http://tah.openstreetmap.org/Tiles/tile/";
	public static final String OSMSlippyServer = "http://tile.openstreetmap.org/";

	int zoomLevel;
	int[]xySet=new int[]{0,0};
	String baseTileServerURL;

	public OSMTileRequestGenerator(int zoomLevel) {
		this(zoomLevel,OSMSlippyServer);
	}
	public OSMTileRequestGenerator(int zoomLevel, String baseTileServerURL) {
		this.zoomLevel=zoomLevel;
		this.baseTileServerURL=baseTileServerURL;
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.terrain.ITileRequestGenerator#generateTileRequest(double, double, int)
	 */
	public String generateTileRequest(ILocation coordinate) {
		return generateTileRequest(coordinate.getGPS().getLatitude(), coordinate.getGPS().getLongitude());
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.terrain.ITileRequestGenerator#generateTileRequest(double, double)
	 */
	public String generateTileRequest(double lat, double lon){
		xySet = getTileXY(lat, lon);
		return getCurrentRequest();
	}
	
	
	public String getCurrentRequest(){
		return baseTileServerURL +zoomLevel+"/"+xySet[0]+"/"+xySet[1]+".png";
	}
	
	public String shiftAndGetRequest(int numLonTiles, int numLatTiles){
		BoundingBox bb = getCurrentBoundingBox();
		double lonDelta = bb.getNe().getLongitude()-bb.getNw().getLongitude();
		double latDelta = bb.getNe().getLatitude()-bb.getSe().getLatitude();
		
		//System.out.println("lon delta: " + lonDelta);
		//System.out.println("lat delta: " + latDelta);
		//System.out.println(bb.getNw());
		//System.out.println(bb.getNe());
		//System.out.println(bb.getSw());
		//System.out.println(bb.getSe());
		
		// normalize longitudinal stride
		if(lonDelta<0){
			lonDelta*=-1;
		}
		
		double startLat0 = bb.getSw().getLatitude()+(latDelta/2);
		double startLon0 = bb.getNw().getLongitude()+(lonDelta/2);

		return generateTileRequest(startLat0+(numLatTiles*latDelta), startLon0+(numLonTiles*lonDelta));
	}

	// This is based on a method found on the OpenStreetMaps wiki:
	// http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Java
	int[] getTileXY(double lat, double lon){
		int xTile = (int)Math.floor((lon + 180) / 360 * (1<<zoomLevel)) ;
		int yTile = (int)Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoomLevel)) ;
		return new int[]{xTile,yTile};
	}

	/*
	 * (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.terrain.ITileRequestGenerator#getCurrentBoundingBox()
	 */
	public BoundingBox getCurrentBoundingBox(){
		return getBoundingBox(xySet, zoomLevel);
	}

	public static BoundingBox getBoundingBox(int[] tileSet, int zoom){
		return new BoundingBox(new GPSCoordinate(0, tile2lat(tileSet[1], zoom), tile2lon(tileSet[0], zoom)),
				new GPSCoordinate(0, tile2lat(tileSet[1], zoom), tile2lon(tileSet[0]+1, zoom)),
				new GPSCoordinate(0, tile2lat(tileSet[1]+1, zoom), tile2lon(tileSet[0], zoom)),
				new GPSCoordinate(0, tile2lat(tileSet[1]+1, zoom), tile2lon(tileSet[0]+1, zoom)));
	}

	private static double tile2lon(int x, int z) {
		return x / Math.pow(2.0, z) * 360.0 - 180;
	}

	private static double tile2lat(int y, int z) {
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
		return Math.toDegrees(Math.atan(Math.sinh(n)));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ITileRequestGenerator gen = new OSMTileRequestGenerator(10);
		System.out.println(gen.generateTileRequest(41, -74));
		String path = gen.generateTileRequest(41, -74).substring(0, gen.generateTileRequest(41, -74).lastIndexOf("."));
		path = path.substring(path.lastIndexOf(".")+1);
		System.out.println(path);
		path = path.substring(path.indexOf("/"));
		System.out.println(path);
		BoundingBox bb = gen.getCurrentBoundingBox();
		//System.out.println(bb.getNw());
		//System.out.println(bb.getNe());
		//System.out.println(bb.getSw());
		//System.out.println(bb.getSe());
	}

}
