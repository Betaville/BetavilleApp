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

import java.io.IOException;
import java.net.URL;

import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.jdom.JDOMException;

import com.jme.scene.Spatial;

import edu.poly.bxmc.betaville.jme.gamestates.SceneGameState;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.UTMCoordinate;
import edu.poly.bxmc.betaville.jme.map.MapManager.SquareCorner;
import edu.poly.bxmc.betaville.model.IUser.UserType;
import edu.poly.bxmc.betaville.module.PanelAction;
import edu.poly.bxmc.betaville.osm.OSMRegistry;
import edu.poly.bxmc.betaville.osm.Way;
import edu.poly.bxmc.betaville.xml.OSMReader;

/**
 * @author Skye Book
 *
 */
public class QuickBuilder extends PanelAction {

	/**
	 * @param name
	 * @param description
	 * @param listener
	 */
	public QuickBuilder(String name, String description,
			IButtonPressedListener listener) {
		super(name, description, listener);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public QuickBuilder() {
		super("Build OSM", "Builds an OSM road", "Build OSM", AvailabilityRule.ALWAYS, UserType.MODERATOR,
				null);
		getButton().addButtonPressedListener(new IButtonPressedListener() {
			
			public void buttonPressed(Object arg0, ButtonPressedEvent arg1) {
				
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
					
					
					
					for(Way way : OSMRegistry.get().getWays()){
						Spatial object = null;
						RoadBuilder rb = new RoadBuilder(way);
						object = rb.generateObject();
						if(object!=null)SceneGameState.getInstance().getGISNode().attachChild(object);
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
		});
	}

}
