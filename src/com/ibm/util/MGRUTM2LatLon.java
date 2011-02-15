/*
 * Author: Sami Salkosuo, sami.salkosuo@fi.ibm.com
 *
 * (c) Copyright IBM Corp. 2007
 */
package com.ibm.util;

import com.ibm.util.Digraphs;
import com.ibm.util.LatZones;
import com.ibm.util.UTM2LatLon;

public class MGRUTM2LatLon extends UTM2LatLon
{
  public double[] convertMGRUTMToLatLong(String mgrutm)
  {
    double[] latlon = { 0.0, 0.0 };
    // 02CNR0634657742
    int zone = Integer.parseInt(mgrutm.substring(0, 2));
    String latZone = mgrutm.substring(2, 3);

    String digraph1 = mgrutm.substring(3, 4);
    String digraph2 = mgrutm.substring(4, 5);
    easting = Double.parseDouble(mgrutm.substring(5, 10));
    northing = Double.parseDouble(mgrutm.substring(10, 15));

    LatZones lz = new LatZones();
    double latZoneDegree = lz.getLatZoneDegree(latZone);

    double a1 = latZoneDegree * 40000000 / 360.0;
    double a2 = 2000000 * Math.floor(a1 / 2000000.0);

    Digraphs digraphs = new Digraphs();

    double digraph2Index = digraphs.getDigraph2Index(digraph2);

    double startindexEquator = 1;
    if ((1 + zone % 2) == 1)
    {
      startindexEquator = 6;
    }

    double a3 = a2 + (digraph2Index - startindexEquator) * 100000;
    if (a3 <= 0)
    {
      a3 = 10000000 + a3;
    }
    northing = a3 + northing;

    zoneCM = -183 + 6 * zone;
    double digraph1Index = digraphs.getDigraph1Index(digraph1);
    int a5 = 1 + zone % 3;
    double[] a6 = { 16, 0, 8 };
    double a7 = 100000 * (digraph1Index - a6[a5 - 1]);
    easting = easting + a7;

    setVariables();

    double latitude = 0;
    latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

    if (latZoneDegree < 0)
    {
      latitude = 90 - latitude;
    }

    double d = _a2 * 180 / Math.PI;
    double longitude = zoneCM - d;

    if (getHemisphere(latZone).equals("S"))
    {
      latitude = -latitude;
    }

    latlon[0] = latitude;
    latlon[1] = longitude;
    return latlon;
  }
}