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
package edu.poly.bxmc.betaville.osm.builder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.jdom.JDOMException;

import com.jme.scene.Spatial;
import com.jme.util.export.xml.XMLExporter;

import edu.poly.bxmc.betaville.SettingsPreferences;
import edu.poly.bxmc.betaville.jme.exporters.ColladaExporter;
import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.loaders.util.DriveFinder;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.jme.map.MapManager.SquareCorner;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.osm.OSMRegistry;
import edu.poly.bxmc.betaville.osm.Way;
import edu.poly.bxmc.betaville.osm.tag.Highway;
import edu.poly.bxmc.betaville.osm.tag.Name;
import edu.poly.bxmc.betaville.osm.tag.Natural;
import edu.poly.bxmc.betaville.xml.OSMReader;

/**
 * Incorporates the OSM functionality into a city 
 * panel operation
 * @author Skye Book
 *
 */
public class QuickBuilder extends PanelAction {
	private static final Logger logger = Logger.getLogger(QuickBuilder.class);

	/**
	 * 
	 */
	public QuickBuilder() {
		super("Build OSM", "Builds an OSM road", "Build OSM", AvailabilityRule.ALWAYS, UserType.MODERATOR,
				null);
		getButton().addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				SettingsPreferences.getThreadPool().submit(new Runnable() {
					
					public void run() {
						try {
							
							UTMCoordinate[] box = JME2MapManager.createBox(5000, 5000, SquareCorner.CENTER,
									JME2MapManager.instance.betavilleToUTM(SceneGameState.getInstance().getCamera().getLocation()));
							
							GPSCoordinate bottomLeft = box[2].getGPS();
							double left = bottomLeft.getLongitude();
							double bottom = bottomLeft.getLatitude();
							GPSCoordinate topRight = box[1].getGPS();
							double right = topRight.getLongitude();
							double top = topRight.getLatitude();
							String request = "/api/0.6/map?bbox="+left+","+bottom+","+right+","+top;
							System.out.println("REQUEST:\t" + request);
							OSMReader reader = new OSMReader();
							//eader.loadFile(new File(System.getProperty("user.home")+"/Downloads/map.osm"));
							URL url = new URL("http://api.openstreetmap.org"+request);
							reader.loadFile(url);
							reader.parse();
							
							// check that an OSM cache folder exists:
							File osmCache = new File(DriveFinder.getBetavilleFolder().toString()+"/cache/osm/");
							if(!osmCache.exists()){
								logger.info("OSM geometry cache folder does not exist; Creating it.");
								osmCache.mkdirs();
							}
							
							boolean skipRoads = true;
							
							for(Way way : OSMRegistry.get().getWays()){
								Spatial object = null;
								ObjectBuilder rb;
								if(way.findTag(Highway.class)!=null){
									if(skipRoads) continue;
									//RoadNodeBuilder rb = new RoadNodeBuilder(way);
									
									//rb = new RoadGLTrianglesBuilder(way);
									rb = new RoadGLTriangleStripBuilder(way);
									object = rb.generateObject();
									
									// render the object as a wireframe
									//object.setRenderState(DisplaySystem.getDisplaySystem().getRenderer().createWireframeState());
									//object.updateRenderState();
									
									// if the object is already there, remove it
									SceneGameState.getInstance().getGISNode().detachChildNamed(""+way.getId());
									if(object!=null){
										SceneGameState.getInstance().getGISNode().attachChild(object);
										// export the object for later (this gets done on a separate thread so we can move on)
										submitFileExport(object, osmCache);
									}
								}
								else if(way.findTag(Natural.class)!=null){
									if(way.findTag(Natural.class).equals(Natural.BuiltIn.coastline.name())){
										//RoadNodeBuilder rb = new RoadNodeBuilder(way);
										//rb = new RoadGLTrianglesBuilder(way);
										rb = new CoastlineGLFanBuilder(way);
										object = rb.generateObject();
										
										// render the object as a wireframe
										//object.setRenderState(DisplaySystem.getDisplaySystem().getRenderer().createWireframeState());
										//object.updateRenderState();
										
										// if the object is already there, remove it
										SceneGameState.getInstance().getGISNode().detachChildNamed(""+way.getId());
										if(object!=null){
											SceneGameState.getInstance().getGISNode().attachChild(object);
											// export the object for later (this gets done on a separate thread so we can move on)
											submitFileExport(object, osmCache);
										}
									}
								}
							}
						} catch (JDOMException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					private void submitFileExport(final Spatial object, final File osmCache){
						SettingsPreferences.getThreadPool().submit(new Runnable() {
							
							public void run() {
								try {
									//XMLExporter.getInstance().save(object, new File(osmCache.toString()+"/"+object.getName()+".jme.xml"));
									ColladaExporter exporter = new ColladaExporter(new File(osmCache.toString()+"/"+object.getName()+".dae"), object, true);
									exporter.writeData();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						
					}
				});
			}
		});
		
		
	}
}
