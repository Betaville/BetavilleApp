/*
 * Author: Sami Salkosuo, sami.salkosuo@fi.ibm.com
 *
 * (c) Copyright IBM Corp. 2007
 */
package com.ibm.util;

import com.ibm.util.Digraphs;
import com.ibm.util.LatZones;

public class LatLon2MGRUTM extends LatLon2UTM
{
  public String convertLatLonToMGRUTM(double latitude, double longitude)
  {
    CoordinateConversion.validate(latitude, longitude);
    String mgrUTM = "";

    setVariables(latitude, longitude);

    String longZone = getLongZone(longitude);
    LatZones latZones = new LatZones();
    String latZone = latZones.getLatZone(latitude);

    double _easting = getEasting();
    double _northing = getNorthing(latitude);
    Digraphs digraphs = new Digraphs();
    String digraph1 = digraphs.getDigraph1(Integer.parseInt(longZone),
        _easting);
    String digraph2 = digraphs.getDigraph2(Integer.parseInt(longZone),
        _northing);

    String easting = String.valueOf((int) _easting);
    if (easting.length() < 5)
    {
      easting = "00000" + easting;
    }
    easting = easting.substring(easting.length() - 5);

    String northing;
    northing = String.valueOf((int) _northing);
    if (northing.length() < 5)
    {
      northing = "0000" + northing;
    }
    northing = northing.substring(northing.length() - 5);

    mgrUTM = longZone + latZone + digraph1 + digraph2 + easting + northing;
    return mgrUTM;
  }
}