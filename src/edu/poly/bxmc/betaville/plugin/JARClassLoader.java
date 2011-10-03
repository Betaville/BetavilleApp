/**
 * 
 */
package edu.poly.bxmc.betaville.plugin;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Skye Book
 *
 */
public class JARClassLoader extends URLClassLoader {

	/**
	 * @param urls
	 */
	public JARClassLoader(URL jarURL, ClassLoader classLoader) {
		super(new URL[]{jarURL}, classLoader);
		addURL(jarURL);
	}
}
