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

import edu.poly.bxmc.betaville.osm.tag.*;

/**
 * @author Skye Book
 *
 */
public class KeyMatcher {
	public static HashMap<String, Class<? extends AbstractTag>> keys;
	static{
		keys = new HashMap<String, Class<? extends AbstractTag>>();
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
		addKeyMatch(Capacity.class);
		addKeyMatch(CastleType.class);
		addKeyMatch(CastleTypeDE.class);
		addKeyMatch(CentralKey.class);
		addKeyMatch(Cep.class);
		addKeyMatch(Clothes.class);
		addKeyMatch(CollectionTimes.class);
		addKeyMatch(Colour.class);
		addKeyMatch(Comment.class);
		addKeyMatch(Construction.class);
		addKeyMatch(Contact.class);
		addKeyMatch(Country.class);
		addKeyMatch(Covered.class);
		addKeyMatch(Craft.class);
		addKeyMatch(CreatedBy.class);
		addKeyMatch(Crossing.class);
		addKeyMatch(Cuisine.class);
		addKeyMatch(Cutting.class);
		addKeyMatch(CycleStreetsID.class);
		addKeyMatch(Cycleway.class);
		addKeyMatch(Demolished.class);
		addKeyMatch(Denomination.class);
		addKeyMatch(Depth.class);
		addKeyMatch(Descent.class);
		addKeyMatch(Description.class);
		addKeyMatch(Designation.class);
		addKeyMatch(Destination.class);
		addKeyMatch(Diet.class);
		addKeyMatch(Direction.class);
		addKeyMatch(Disabled.class);
		addKeyMatch(Dispensing.class);
		addKeyMatch(Distance.class);
		addKeyMatch(Disused.class);
		addKeyMatch(Drink.class);
		addKeyMatch(DriveIn.class);
		addKeyMatch(DriveThrough.class);
		addKeyMatch(Duration.class);
		addKeyMatch(EasyOvertaking.class);
		addKeyMatch(Ele.class);
		addKeyMatch(Electrified.class);
		addKeyMatch(Embankment.class);
		addKeyMatch(Emergency.class);
		addKeyMatch(EndDate.class);
		addKeyMatch(Enforcement.class);
		addKeyMatch(Fee.class);
		addKeyMatch(Fenced.class);
		addKeyMatch(FenceType.class);
		addKeyMatch(FixMe.class);
		addKeyMatch(FloodProne.class);
		addKeyMatch(FogSignal.class);
		addKeyMatch(Foot.class);
		addKeyMatch(Footway.class);
		addKeyMatch(Ford.class);
		addKeyMatch(Frequency.class);
		addKeyMatch(Fuel.class);
		addKeyMatch(Furniture.class);
		addKeyMatch(Gauge.class);
		addKeyMatch(Generator.class);
		addKeyMatch(Geological.class);
		addKeyMatch(Hazmat.class);
		addKeyMatch(Healthcare.class);
		addKeyMatch(Height.class);
		addKeyMatch(Highway.class);
		addKeyMatch(Historic.class);
		addKeyMatch(History.class);
		addKeyMatch(Horse.class);
		addKeyMatch(IceRoad.class);
		addKeyMatch(Image.class);
		addKeyMatch(Importance.class);
		addKeyMatch(Incline.class);
		addKeyMatch(Information.class);
		addKeyMatch(Intermittent.class);
		addKeyMatch(InternetAccess.class);
		addKeyMatch(IsIn.class);
		addKeyMatch(Junction.class);
		addKeyMatch(Kerb.class);
		addKeyMatch(Label.class);
		addKeyMatch(Landmark.class);
		addKeyMatch(Landuse.class);
		addKeyMatch(Lanes.class);
		addKeyMatch(Layer.class);
		addKeyMatch(LcnRef.class);
		addKeyMatch(Leisure.class);
		addKeyMatch(Length.class);
		addKeyMatch(Light.class);
		addKeyMatch(Lit.class);
		addKeyMatch(Lock.class);
		addKeyMatch(Manhole.class);
		addKeyMatch(ManMade.class);
		addKeyMatch(Maxage.class);
		addKeyMatch(MaxAirDraft.class);
		addKeyMatch(MaxAxleLoad.class);
		addKeyMatch(MaxDraught.class);
		addKeyMatch(MaxHeight.class);
		addKeyMatch(MaxLength.class);
		addKeyMatch(MaxSpeed.class);
		addKeyMatch(MaxStay.class);
		addKeyMatch(MaxWeight.class);
		addKeyMatch(MaxWidth.class);
		addKeyMatch(Military.class);
		addKeyMatch(Mineage.class);
		addKeyMatch(MinSpeed.class);
		addKeyMatch(Mofa.class);
		addKeyMatch(Monitoring.class);
		addKeyMatch(Mooring.class);
		addKeyMatch(MotorCar.class);
		addKeyMatch(Motorcycle.class);
		addKeyMatch(MotorRoad.class);
		addKeyMatch(MountainPass.class);
		addKeyMatch(Mtb.class);
		addKeyMatch(Name.class);
		addKeyMatch(Narrow.class);
		addKeyMatch(Natural.class);
		addKeyMatch(Ncat.class);
		addKeyMatch(NCNMilepost.class);
		addKeyMatch(NcnRef.class);
		addKeyMatch(Network.class);
		addKeyMatch(NoExit.class);
		addKeyMatch(NoName.class);
		addKeyMatch(Note.class);
		addKeyMatch(Office.class);
		addKeyMatch(OneWay.class);
		addKeyMatch(OpeningHours.class);
		addKeyMatch(Operator.class);
		addKeyMatch(OsmARender.class);
		addKeyMatch(Osmc.class);
		addKeyMatch(Overtaking.class);
		addKeyMatch(Parking.class);
		addKeyMatch(PassingPlaces.class);
		addKeyMatch(Paved.class);
		addKeyMatch(Phone.class);
		addKeyMatch(Pilotage.class);
		addKeyMatch(Place.class);
		addKeyMatch(Playground.class);
		addKeyMatch(Population.class);
		addKeyMatch(PostalCode.class);
		addKeyMatch(Power.class);
		addKeyMatch(PriorityRoad.class);
		addKeyMatch(Proposed.class);
		addKeyMatch(Psv.class);
		addKeyMatch(PublicTransport.class);
		addKeyMatch(RadarReflector.class);
		addKeyMatch(Railway.class);
		addKeyMatch(Ramp.class);
		addKeyMatch(Ramsar.class);
		addKeyMatch(RcnRef.class);
		addKeyMatch(RealAle.class);
		addKeyMatch(Ref.class);
		addKeyMatch(Religion.class);
		addKeyMatch(Resource.class);
		addKeyMatch(Room.class);
		addKeyMatch(Route.class);
		addKeyMatch(RoyalCypher.class);
		addKeyMatch(RtcRate.class);
		addKeyMatch(SacScale.class);
		addKeyMatch(SagnsId.class);
		addKeyMatch(Scenic.class);
		addKeyMatch(School.class);
		addKeyMatch(SeabedSurface.class);
		addKeyMatch(Seamark.class);
		addKeyMatch(Seasonal.class);
		addKeyMatch(Service.class);
		addKeyMatch(Shelter.class);
		addKeyMatch(Shop.class);
		addKeyMatch(Sidewalk.class);
		addKeyMatch(SignalStation.class);
		addKeyMatch(Ski.class);
		addKeyMatch(Smoothness.class);
		addKeyMatch(Snowplowing.class);
		addKeyMatch(SocialFacility.class);
		addKeyMatch(Source.class);
		addKeyMatch(Sport.class);
		addKeyMatch(Stars.class);
		addKeyMatch(StartDate.class);
		addKeyMatch(Status.class);
		addKeyMatch(StepCount.class);
		addKeyMatch(Stop.class);
		addKeyMatch(SubSea.class);
		addKeyMatch(Sulky.class);
		addKeyMatch(Surface.class);
		addKeyMatch(Surveillance.class);
		addKeyMatch(SustransRef.class);
		addKeyMatch(Symbol.class);
		addKeyMatch(TactilePaving.class);
		addKeyMatch(Tidal.class);
		addKeyMatch(Tiger.class);
		addKeyMatch(Timezone.class);
		addKeyMatch(Toll.class);
		addKeyMatch(Topmark.class);
		addKeyMatch(Tourism.class);
		addKeyMatch(TouristBus.class);
		addKeyMatch(Tracktype.class);
		addKeyMatch(Traffic.class);
		addKeyMatch(TrafficCalming.class);
		addKeyMatch(TrafficSign.class);
		addKeyMatch(TrafficSignals.class);
		addKeyMatch(TrailVisibility.class);
		addKeyMatch(TrolleyWire.class);
		addKeyMatch(Tunnel.class);
		addKeyMatch(Type.class);
		addKeyMatch(Unisex.class);
		addKeyMatch(Url.class);
		addKeyMatch(Usage.class);
		addKeyMatch(Voltage.class);
		addKeyMatch(Water.class);
		addKeyMatch(Waterway.class);
		addKeyMatch(Website.class);
		addKeyMatch(Wheelchair.class);
		addKeyMatch(Width.class);
		addKeyMatch(Wikipedia.class);
		addKeyMatch(WinterRoad.class);
		addKeyMatch(Wires.class);
		addKeyMatch(Woeid.class);
		addKeyMatch(Wood.class);
		addKeyMatch(Zoo.class);
	}
	
	private static void addKeyMatch(Class<? extends AbstractTag> key){
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
	
	public static Class<? extends AbstractTag> getKey(String keyName){
		return keys.get(keyName);
	}
}
