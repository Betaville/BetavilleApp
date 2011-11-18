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
package edu.poly.bxmc.betaville.weather;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.geotools.nature.SunRelativePosition;

import com.jme.light.DirectionalLight;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState.StateType;

import edu.poly.bxmc.betaville.jme.gamestates.ShadowPassState;
import edu.poly.bxmc.betaville.jme.map.CardinalDirections;
import edu.poly.bxmc.betaville.jme.map.GPSCoordinate;
import edu.poly.bxmc.betaville.jme.map.JME2MapManager;
import edu.poly.bxmc.betaville.jme.map.Rotator;
import edu.poly.bxmc.betaville.jme.map.Scale;
import edu.poly.bxmc.betaville.module.GlobalSceneModule;
import edu.poly.bxmc.betaville.module.Module;

/**
 * Synchronizes light sources to the sun's position
 * @author Skye Book
 *
 */
public class SunSyncModule extends Module implements GlobalSceneModule {
	private static final Logger logger = Logger.getLogger(SunSyncModule.class);
	
	protected GPSCoordinate coordinate;
	
	private SunRelativePosition sunRelativePosition;
	
	protected Date dateTime;
	
	private float[] vector = new float[3];
	private Vector3f sunAngle = new Vector3f(0, 0, 0);
	
	private boolean realTime = true;
	
	private GregorianCalendar calendar;
	
	/**
	 * @param name
	 */
	public SunSyncModule(String name) {
		super(name);
		sunRelativePosition = new SunRelativePosition(Double.NaN);
		dateTime = new Date(System.currentTimeMillis());
		calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(System.currentTimeMillis());
		dateTime = calendar.getTime();
		
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#initialize(com.jme.scene.Node)
	 */
	@Override
	public void initialize(Node scene) {}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.SceneModule#onUpdate(com.jme.scene.Node, com.jme.math.Vector3f, com.jme.math.Vector3f)
	 */
	@Override
	public void onUpdate(Node scene, Vector3f cameraLocation,
			Vector3f cameraDirection) {
		
		if(realTime) dateTime.setTime(System.currentTimeMillis());
		else{
			dateTime = calendar.getTime();
		}
		
		// presuming the scene's two default lights
		LightState lightState = (LightState) scene.getRenderState(StateType.Light);
		
		coordinate = JME2MapManager.instance.betavilleToUTM(cameraLocation).getGPS();
		
		sunRelativePosition.setDate(dateTime);
		sunRelativePosition.setCoordinate(coordinate.getLongitude(), coordinate.getLatitude());
		double azimuth = sunRelativePosition.getAzimuth();
		double elevation = sunRelativePosition.getElevation();
		
		//Quaternion a = Rotator.angleY(((360f)-(float)azimuth));
		Quaternion a = Rotator.angleY((float)azimuth);
		Quaternion e = Rotator.angleZ(-1*(float)elevation);
		Quaternion fin = a.mult(e);
		
		fin.mult(CardinalDirections.NORTH, sunAngle);
		
		logger.info("Sun Azimuth: " + azimuth);
		logger.info("Sun Elevation: " + elevation);
		logger.info("Sun Angle: " + sunAngle.toString());
		
		((DirectionalLight)lightState.get(0)).setDirection(sunAngle);
		((DirectionalLight)lightState.get(1)).setDirection(sunAngle.negate());
		ShadowPassState.getInstance().getMapPass().setDirection(sunAngle);
	}

	/* (non-Javadoc)
	 * @see edu.poly.bxmc.betaville.module.IModule#deconstruct()
	 */
	@Override
	public void deconstruct() {
		// TODO Auto-generated method stub

	}

	public GPSCoordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(GPSCoordinate coordinate) {
		this.coordinate = coordinate;
	}

	public boolean isRealTime() {
		return realTime;
	}

	public void setRealTime(boolean realTime) {
		this.realTime = realTime;
	}

	public GregorianCalendar getCalendar() {
		return calendar;
	}

}
