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
package edu.poly.bxmc.betaville.osm;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import edu.poly.bxmc.betaville.jme.fenggui.AddLayersWindow;
import edu.poly.bxmc.betaville.osm.keys.*;

/**
 * @author Skye Book
 *
 */
public class KeyMatcher {
	public static HashMap<String, Class<? extends AbstractKey>> keys;
	static{
		keys = new HashMap<String, Class<? extends AbstractKey>>();
		keys.put(Abutters.keyName(), Abutters.class);
		keys.put(Access.keyName(), Access.class);
		keys.put(Address.keyName(), Address.class);
		addKeyMatch(Abutters.class);
		addKeyMatch(Access.class);
		addKeyMatch(Address.class);
		addKeyMatch(AdminLevel.class);
		addKeyMatch(AerialWay.class);
		addKeyMatch(AeroWay.class);
		addKeyMatch(Amenity.class);
		addKeyMatch(Architect.class);
		addKeyMatch(Area.class);
		addKeyMatch(Ascent.class);
		addKeyMatch(Attribution.class);
		addKeyMatch(Atv.class);
		addKeyMatch(Barrier.class);
		addKeyMatch(Basin.class);
		addKeyMatch(Beacon.class);
		addKeyMatch(Bench.class);
		addKeyMatch(Bicycle.class);
		addKeyMatch(BicycleParking.class);
		addKeyMatch(Boat.class);
		addKeyMatch(Books.class);
		addKeyMatch(BorderType.class);
		addKeyMatch(Boundary.class);
		addKeyMatch(Brand.class);
		addKeyMatch(Brewery.class);
		addKeyMatch(Bridge.class);
		addKeyMatch(Building.class);
		addKeyMatch(BunkerType.class);
		addKeyMatch(Buoy.class);
		addKeyMatch(Busway.class);
		addKeyMatch(Cables.class);
	}
	
	private static void addKeyMatch(Class<? extends AbstractKey> key){
		try {
			String keyName = (String)key.getMethod("keyName", (Class[])null).invoke(null, (Object[])null);
			keys.put(keyName, key);
			return;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Could not add "+key.getClass().getName() + " to the list of acceptable keys");
	}
}
