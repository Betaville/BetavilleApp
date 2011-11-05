/*
 * Author: Sami Salkosuo, sami.salkosuo@fi.ibm.com
 *
 * (c) Copyright IBM Corp. 2007
 */
package com.ibm.util;

import java.util.Hashtable;
import java.util.Map;

public class Digraphs
{
  @SuppressWarnings("unchecked")
	private Map digraph1 = new Hashtable();

  @SuppressWarnings("unchecked")
	private Map digraph2 = new Hashtable();

  private String[] digraph1Array = { "A", "B", "C", "D", "E", "F", "G", "H",
      "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
      "Y", "Z" };

  private String[] digraph2Array = { "V", "A", "B", "C", "D", "E", "F", "G",
      "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V" };

  @SuppressWarnings("unchecked")
	public Digraphs()
  {
    digraph1.put(Integer.valueOf(1), "A");
    digraph1.put(Integer.valueOf(2), "B");
    digraph1.put(Integer.valueOf(3), "C");
    digraph1.put(Integer.valueOf(4), "D");
    digraph1.put(Integer.valueOf(5), "E");
    digraph1.put(Integer.valueOf(6), "F");
    digraph1.put(Integer.valueOf(7), "G");
    digraph1.put(Integer.valueOf(8), "H");
    digraph1.put(Integer.valueOf(9), "J");
    digraph1.put(Integer.valueOf(10), "K");
    digraph1.put(Integer.valueOf(11), "L");
    digraph1.put(Integer.valueOf(12), "M");
    digraph1.put(Integer.valueOf(13), "N");
    digraph1.put(Integer.valueOf(14), "P");
    digraph1.put(Integer.valueOf(15), "Q");
    digraph1.put(Integer.valueOf(16), "R");
    digraph1.put(Integer.valueOf(17), "S");
    digraph1.put(Integer.valueOf(18), "T");
    digraph1.put(Integer.valueOf(19), "U");
    digraph1.put(Integer.valueOf(20), "V");
    digraph1.put(Integer.valueOf(21), "W");
    digraph1.put(Integer.valueOf(22), "X");
    digraph1.put(Integer.valueOf(23), "Y");
    digraph1.put(Integer.valueOf(24), "Z");

    digraph2.put(Integer.valueOf(0), "V");
    digraph2.put(Integer.valueOf(1), "A");
    digraph2.put(Integer.valueOf(2), "B");
    digraph2.put(Integer.valueOf(3), "C");
    digraph2.put(Integer.valueOf(4), "D");
    digraph2.put(Integer.valueOf(5), "E");
    digraph2.put(Integer.valueOf(6), "F");
    digraph2.put(Integer.valueOf(7), "G");
    digraph2.put(Integer.valueOf(8), "H");
    digraph2.put(Integer.valueOf(9), "J");
    digraph2.put(Integer.valueOf(10), "K");
    digraph2.put(Integer.valueOf(11), "L");
    digraph2.put(Integer.valueOf(12), "M");
    digraph2.put(Integer.valueOf(13), "N");
    digraph2.put(Integer.valueOf(14), "P");
    digraph2.put(Integer.valueOf(15), "Q");
    digraph2.put(Integer.valueOf(16), "R");
    digraph2.put(Integer.valueOf(17), "S");
    digraph2.put(Integer.valueOf(18), "T");
    digraph2.put(Integer.valueOf(19), "U");
    digraph2.put(Integer.valueOf(20), "V");

  }

  public int getDigraph1Index(String letter)
  {
    for (int i = 0; i < digraph1Array.length; i++)
    {
      if (digraph1Array[i].equals(letter))
      {
        return i + 1;
      }
    }

    return -1;
  }

  public int getDigraph2Index(String letter)
  {
    for (int i = 0; i < digraph2Array.length; i++)
    {
      if (digraph2Array[i].equals(letter))
      {
        return i;
      }
    }

    return -1;
  }

  public String getDigraph1(int longZone, double easting)
  {
    int a1 = longZone;
    double a2 = 8 * ((a1 - 1) % 3) + 1;

    double a3 = easting;
    double a4 = a2 + ((int) (a3 / 100000)) - 1;
    return (String) digraph1.get(Integer.valueOf(((int) Math.floor(a4))));
  }

  public String getDigraph2(int longZone, double northing)
  {
    int a1 = longZone;
    double a2 = 1 + 5 * ((a1 - 1) % 2);
    double a3 = northing;
    double a4 = (a2 + ((int) (a3 / 100000)));
    a4 = (a2 + ((int) (a3 / 100000.0))) % 20;
    a4 = Math.floor(a4);
    if (a4 < 0)
    {
      a4 = a4 + 19;
    }
    return (String) digraph2.get(Integer.valueOf(((int) Math.floor(a4))));

  }

}