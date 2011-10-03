/**
 * 
 */
package edu.poly.bxmc.betaville.plugin;

/**
 * @author Skye Book
 *
 */
public class PluginAlreadyLoadedException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public PluginAlreadyLoadedException() {}

	/**
	 * @param arg0
	 */
	public PluginAlreadyLoadedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public PluginAlreadyLoadedException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PluginAlreadyLoadedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
