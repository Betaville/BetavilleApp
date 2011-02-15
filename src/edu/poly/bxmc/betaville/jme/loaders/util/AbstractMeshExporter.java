/**
 * 
 */
package edu.poly.bxmc.betaville.jme.loaders.util;

import java.io.PrintWriter;

/**
 * @author Skye Book
 *
 */
public abstract class AbstractMeshExporter {
	protected PrintWriter writer;

	/**
	 * 
	 */
	public AbstractMeshExporter(PrintWriter writer) {
		this.writer=writer;
	}

}
