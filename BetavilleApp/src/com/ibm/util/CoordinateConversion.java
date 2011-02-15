/*
 * Author: Sami Salkosuo, sami.salkosuo@fi.ibm.com
 *
 * (c) Copyright IBM Corp. 2007
 */
package com.ibm.util;

public class CoordinateConversion
{

  public CoordinateConversion()
  {

  }

  public static double[] utm2LatLon(String UTM)
  {
    UTM2LatLon c = new UTM2LatLon();
    return c.convertUTMToLatLong(UTM);
  }

  public static String latLon2UTM(double latitude, double longitude)
  {
    LatLon2UTM c = new LatLon2UTM();
    return c.convertLatLonToUTM(latitude, longitude);

  }

  public static void validate(double latitude, double longitude)
  {
    if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0
        || longitude >= 180.0)
    {
      throw new IllegalArgumentException(
          "Legal ranges: latitude [-90,90], longitude [-180,180).");
    }

  }

  public String latLon2MGRUTM(double latitude, double longitude)
  {
    LatLon2MGRUTM c = new LatLon2MGRUTM();
    return c.convertLatLonToMGRUTM(latitude, longitude);

  }

  public double[] mgrutm2LatLon(String MGRUTM)
  {
    MGRUTM2LatLon c = new MGRUTM2LatLon();
    return c.convertMGRUTMToLatLong(MGRUTM);
  }


  

  
}